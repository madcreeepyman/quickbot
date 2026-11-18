package com.snacky.quickbot

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.WindowInsets
import android.view.WindowManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.snacky.quickbot.ui.theme.QuickbotTheme

class OverlayService : Service(),
    LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val TAG = "QuickbotOverlay"

    private lateinit var windowManager: WindowManager
    private var overlayView: ComposeView? = null

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val viewModelStore: ViewModelStore = ViewModelStore()

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    // Ресивер: при выключении экрана — закрываем оверлей
    private val screenOffReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_SCREEN_OFF) {
                Log.d(TAG, "SCREEN_OFF → stopSelf")
                stopSelf()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        try {
            startForegroundNotification()
            Log.d(TAG, "onCreate: startForeground OK")
        } catch (t: Throwable) {
            Log.e(TAG, "onCreate: startForeground FAILED", t)
            stopSelf()
            return
        }

        super.onCreate()

        try {
            savedStateRegistryController.performRestore(null)
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
            windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

            // Регистрируем ресивер для SCREEN_OFF
            registerReceiver(
                screenOffReceiver,
                IntentFilter(Intent.ACTION_SCREEN_OFF)
            )
            Log.d(TAG, "onCreate: registries + receiver OK")
        } catch (t: Throwable) {
            Log.e(TAG, "onCreate: registry init FAILED", t)
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: overlayView=${overlayView != null}")

        if (overlayView == null) {
            Handler(Looper.getMainLooper()).post {
                try {
                    showOverlay()
                } catch (t: Throwable) {
                    Log.e(TAG, "showOverlay FAILED", t)
                    stopSelf()
                }
            }
        }
        try {
            lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        } catch (t: Throwable) {
            Log.e(TAG, "lifecycle RESUMED FAILED", t)
        }
        return START_NOT_STICKY
    }

    private fun showOverlay() {
        Log.d(TAG, "showOverlay: begin")

        val view = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@OverlayService)
            setViewTreeViewModelStoreOwner(this@OverlayService)
            setViewTreeSavedStateRegistryOwner(this@OverlayService)
            setContent {
                CompositionLocalProvider(
                    LocalViewModelStoreOwner provides this@OverlayService
                ) {
                    val vm: ChatViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                        factory = ChatViewModelFactory(application)
                    )
                    QuickbotTheme(
                        themeMode = ThemePrefs.getMode(this@OverlayService),
                        paletteId = PalettePrefs.getPalette(this@OverlayService)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            tonalElevation = 8.dp,
                            shadowElevation = 12.dp
                        ) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 200.dp, max = 400.dp)
                                    .padding(8.dp)
                            ) {
                                OverlayContent(vm = vm)
                            }
                        }
                    }
                }
            }
        }
        Log.d(TAG, "showOverlay: ComposeView created")

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE

        // Флаги: аппаратное ускорение + фокусируемое окно + тапы вне окна идут дальше
        val flags = WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

        val navBarHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val insets = windowManager.currentWindowMetrics.windowInsets
            insets.getInsets(WindowInsets.Type.navigationBars()).bottom
        } else {
            0
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            flags,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            y = navBarHeight
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        }

        windowManager.addView(view, params)
        overlayView = view
        Log.d(TAG, "showOverlay: addView OK")
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        runCatching { unregisterReceiver(screenOffReceiver) }
        overlayView?.let {
            runCatching { windowManager.removeView(it) }
                .onFailure { t -> Log.e(TAG, "removeView FAILED", t) }
        }
        overlayView = null
        runCatching { lifecycleRegistry.currentState = Lifecycle.State.DESTROYED }
        viewModelStore.clear()
        super.onDestroy()
    }

    private fun startForegroundNotification() {
        val channelId = "quickbot_overlay"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)
            if (nm != null && nm.getNotificationChannel(channelId) == null) {
                nm.createNotificationChannel(
                    NotificationChannel(
                        channelId,
                        "Quickbot overlay",
                        NotificationManager.IMPORTANCE_MIN
                    )
                )
            }
        }
        val openApp = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notif: Notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Quickbot")
            .setContentText("Ассистент активен")
            .setContentIntent(openApp)
            .setOngoing(true)
            .build()
        startForeground(NOTIF_ID, notif)
    }

    companion object {
        private const val NOTIF_ID = 4711
    }
}