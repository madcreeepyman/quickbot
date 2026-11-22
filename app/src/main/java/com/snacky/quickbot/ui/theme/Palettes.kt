package com.snacky.quickbot.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Описание одной палитры.
 *
 * @param id       — строковый ID для SharedPreferences (не меняй после релиза!)
 * @param label    — название, которое показывается в UI
 * @param light    — ColorScheme для светлой темы (null, если палитра только тёмная)
 * @param dark     — ColorScheme для тёмной темы (null, если палитра только светлая)
 * @param isSystem — если true, тема берётся из системы (Monet, только Android 12+)
 */
data class Palette(
    val id: String,
    val label: String,
    val light: ColorScheme?,
    val dark: ColorScheme?,
    val isSystem: Boolean = false
)

// =========================================================================
// 1. СИСТЕМНАЯ (Monet) — динамическая, только Android 12+
// =========================================================================
// Для неё ColorScheme определяется в Theme.kt через dynamicLightColorScheme/
// dynamicDarkColorScheme. Здесь — только заглушка.
val SystemPalette = Palette(
    id = "system",
    label = "Системная",
    light = null,
    dark = null,
    isSystem = true
)

// =========================================================================
// 2. МОНОХРОМ — оттенки серого, без насыщенных цветов
// =========================================================================
val MonochromeLight = lightColorScheme(
    primary = Color(0xFF424242),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE0E0E0),
    onPrimaryContainer = Color(0xFF1C1B1F),
    secondary = Color(0xFF757575),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEEEEEE),
    onSecondaryContainer = Color(0xFF1C1B1F),
    tertiary = Color(0xFF616161),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFEEEEEE),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B)
)

val MonochromeDark = darkColorScheme(
    primary = Color(0xFFBDBDBD),
    onPrimary = Color(0xFF1C1B1F),
    primaryContainer = Color(0xFF424242),
    onPrimaryContainer = Color(0xFFE0E0E0),
    secondary = Color(0xFF9E9E9E),
    onSecondary = Color(0xFF1C1B1F),
    secondaryContainer = Color(0xFF424242),
    onSecondaryContainer = Color(0xFFEEEEEE),
    tertiary = Color(0xFFB0B0B0),
    onTertiary = Color(0xFF1C1B1F),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC)
)

val MonochromePalette = Palette(
    id = "mono",
    label = "Монохром",
    light = MonochromeLight,
    dark = MonochromeDark
)

// =========================================================================
// 3. МЯТА — мягкий зелёный, свежий
// =========================================================================
val MintLight = lightColorScheme(
    primary = Color(0xFF2E7D5B),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFA8F0CB),
    onPrimaryContainer = Color(0xFF002115),
    secondary = Color(0xFF4D6357),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCFE9D9),
    onSecondaryContainer = Color(0xFF0A1F16),
    tertiary = Color(0xFF3D6473),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFF6FBF6),
    onBackground = Color(0xFF171D19),
    surface = Color(0xFFF6FBF6),
    onSurface = Color(0xFF171D19),
    surfaceVariant = Color(0xFFDCE5DD),
    onSurfaceVariant = Color(0xFF404943),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

val MintDark = darkColorScheme(
    primary = Color(0xFF8CD4B0),
    onPrimary = Color(0xFF003825),
    primaryContainer = Color(0xFF15503B),
    onPrimaryContainer = Color(0xFFA8F0CB),
    secondary = Color(0xFFB3CCBE),
    onSecondary = Color(0xFF1F352A),
    secondaryContainer = Color(0xFF354B40),
    onSecondaryContainer = Color(0xFFCFE9D9),
    tertiary = Color(0xFFA5CDDE),
    onTertiary = Color(0xFF073543),
    background = Color(0xFF0F1511),
    onBackground = Color(0xFFDEE4DE),
    surface = Color(0xFF0F1511),
    onSurface = Color(0xFFDEE4DE),
    surfaceVariant = Color(0xFF404943),
    onSurfaceVariant = Color(0xFFC0C9C1),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

val MintPalette = Palette(
    id = "mint",
    label = "Мята",
    light = MintLight,
    dark = MintDark
)

// =========================================================================
// 4. ИНДИГО — глубокий сине-фиолетовый
// =========================================================================
val IndigoLight = lightColorScheme(
    primary = Color(0xFF4A5BA8),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDDE1FF),
    onPrimaryContainer = Color(0xFF00105C),
    secondary = Color(0xFF5B5D72),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE0E1F9),
    onSecondaryContainer = Color(0xFF181A2C),
    tertiary = Color(0xFF77536D),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFFBF8FF),
    onBackground = Color(0xFF1B1B21),
    surface = Color(0xFFFBF8FF),
    onSurface = Color(0xFF1B1B21),
    surfaceVariant = Color(0xFFE2E1EC),
    onSurfaceVariant = Color(0xFF45464F),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

val IndigoDark = darkColorScheme(
    primary = Color(0xFFBBC3FF),
    onPrimary = Color(0xFF162478),
    primaryContainer = Color(0xFF323C90),
    onPrimaryContainer = Color(0xFFDDE1FF),
    secondary = Color(0xFFC4C5DD),
    onSecondary = Color(0xFF2D2F42),
    secondaryContainer = Color(0xFF434559),
    onSecondaryContainer = Color(0xFFE0E1F9),
    tertiary = Color(0xFFE6BAD7),
    onTertiary = Color(0xFF45263D),
    background = Color(0xFF121318),
    onBackground = Color(0xFFE3E1E9),
    surface = Color(0xFF121318),
    onSurface = Color(0xFFE3E1E9),
    surfaceVariant = Color(0xFF45464F),
    onSurfaceVariant = Color(0xFFC6C5D0),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

val IndigoPalette = Palette(
    id = "indigo",
    label = "Индиго",
    light = IndigoLight,
    dark = IndigoDark
)

// =========================================================================
// 5. ТЕРРАКОТА — тёплый оранжево-коричневый
// =========================================================================
val TerracottaLight = lightColorScheme(
    primary = Color(0xFF8B4B27),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDBCB),
    onPrimaryContainer = Color(0xFF321200),
    secondary = Color(0xFF75584B),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDBCB),
    onSecondaryContainer = Color(0xFF2B160C),
    tertiary = Color(0xFF665E2E),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFFFF8F5),
    onBackground = Color(0xFF221A16),
    surface = Color(0xFFFFF8F5),
    onSurface = Color(0xFF221A16),
    surfaceVariant = Color(0xFFF4DED3),
    onSurfaceVariant = Color(0xFF52443C),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

val TerracottaDark = darkColorScheme(
    primary = Color(0xFFFFB68F),
    onPrimary = Color(0xFF4E2506),
    primaryContainer = Color(0xFF6D3B17),
    onPrimaryContainer = Color(0xFFFFDBCB),
    secondary = Color(0xFFE5BFAE),
    onSecondary = Color(0xFF422B20),
    secondaryContainer = Color(0xFF5B4135),
    onSecondaryContainer = Color(0xFFFFDBCB),
    tertiary = Color(0xFFD0C58E),
    onTertiary = Color(0xFF363000),
    background = Color(0xFF1A120E),
    onBackground = Color(0xFFF0DFD8),
    surface = Color(0xFF1A120E),
    onSurface = Color(0xFFF0DFD8),
    surfaceVariant = Color(0xFF52443C),
    onSurfaceVariant = Color(0xFFD8C2B7),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

val TerracottaPalette = Palette(
    id = "terracotta",
    label = "Терракота",
    light = TerracottaLight,
    dark = TerracottaDark
)

// =========================================================================
// 6. РОЗА — мягкий розово-пурпурный
// =========================================================================
val RoseLight = lightColorScheme(
    primary = Color(0xFF9C4059),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFD9E1),
    onPrimaryContainer = Color(0xFF3F001C),
    secondary = Color(0xFF74565E),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFD9E1),
    onSecondaryContainer = Color(0xFF2B151C),
    tertiary = Color(0xFF7E5734),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFFFF8F8),
    onBackground = Color(0xFF22191B),
    surface = Color(0xFFFFF8F8),
    onSurface = Color(0xFF22191B),
    surfaceVariant = Color(0xFFF2DDE1),
    onSurfaceVariant = Color(0xFF514347),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

val RoseDark = darkColorScheme(
    primary = Color(0xFFFFB1C4),
    onPrimary = Color(0xFF5E112B),
    primaryContainer = Color(0xFF7D2841),
    onPrimaryContainer = Color(0xFFFFD9E1),
    secondary = Color(0xFFE3BDC5),
    onSecondary = Color(0xFF422930),
    secondaryContainer = Color(0xFF5B3F46),
    onSecondaryContainer = Color(0xFFFFD9E1),
    tertiary = Color(0xFFF1BD92),
    onTertiary = Color(0xFF472A0C),
    background = Color(0xFF1A1113),
    onBackground = Color(0xFFF0DEE0),
    surface = Color(0xFF1A1113),
    onSurface = Color(0xFFF0DEE0),
    surfaceVariant = Color(0xFF514347),
    onSurfaceVariant = Color(0xFFD5C2C6),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

val RosePalette = Palette(
    id = "rose",
    label = "Роза",
    light = RoseLight,
    dark = RoseDark
)

// =========================================================================
// 7. АКВА — бирюзово-синий
// =========================================================================
val AquaLight = lightColorScheme(
    primary = Color(0xFF006874),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF97F0FF),
    onPrimaryContainer = Color(0xFF001F24),
    secondary = Color(0xFF4A6267),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCDE7EC),
    onSecondaryContainer = Color(0xFF051F23),
    tertiary = Color(0xFF54647C),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFF5FAFB),
    onBackground = Color(0xFF171D1E),
    surface = Color(0xFFF5FAFB),
    onSurface = Color(0xFF171D1E),
    surfaceVariant = Color(0xFFDBE4E6),
    onSurfaceVariant = Color(0xFF3F484A),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

val AquaDark = darkColorScheme(
    primary = Color(0xFF4FD8EB),
    onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF004F58),
    onPrimaryContainer = Color(0xFF97F0FF),
    secondary = Color(0xFFB1CBD0),
    onSecondary = Color(0xFF1C3438),
    secondaryContainer = Color(0xFF334B4F),
    onSecondaryContainer = Color(0xFFCDE7EC),
    tertiary = Color(0xFFBBC7E3),
    onTertiary = Color(0xFF253140),
    background = Color(0xFF0E1415),
    onBackground = Color(0xFFDEE3E4),
    surface = Color(0xFF0E1415),
    onSurface = Color(0xFFDEE3E4),
    surfaceVariant = Color(0xFF3F484A),
    onSurfaceVariant = Color(0xFFBFC8CA),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

val AquaPalette = Palette(
    id = "aqua",
    label = "Аква",
    light = AquaLight,
    dark = AquaDark
)

// =========================================================================
// 4. БАНАН — жёлтый, банановый
// =========================================================================
val BananaLight = lightColorScheme(
    primary = Color(0xFF745B00),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFE086),
    onPrimaryContainer = Color(0xFF231A00),
    secondary = Color(0xFF6D5E31),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF2E1B3),
    onSecondaryContainer = Color(0xFF241A04),
    tertiary = Color(0xFF4B6478),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFD3E4F1),
    onTertiaryContainer = Color(0xFF061E2A),
    background = Color(0xFFFFFBF3),
    onBackground = Color(0xFF1D1B12),
    surface = Color(0xFFFFFBF3),
    onSurface = Color(0xFF1D1B12),
    surfaceVariant = Color(0xFFECE2C8),
    onSurfaceVariant = Color(0xFF4C4630),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

val BananaDark = darkColorScheme(
    primary = Color(0xFFEFC235),
    onPrimary = Color(0xFF3B2F00),
    primaryContainer = Color(0xFF564500),
    onPrimaryContainer = Color(0xFFFFE086),
    secondary = Color(0xFFD6C5A0),
    onSecondary = Color(0xFF382E0F),
    secondaryContainer = Color(0xFF504723),
    onSecondaryContainer = Color(0xFFF2E1B3),
    tertiary = Color(0xFFB0CAE2),
    onTertiary = Color(0xFF153340),
    tertiaryContainer = Color(0xFF334A59),
    onTertiaryContainer = Color(0xFFD3E4F1),
    background = Color(0xFF1A1409),
    onBackground = Color(0xFFE9E1D0),
    surface = Color(0xFF1A1409),
    onSurface = Color(0xFFE9E1D0),
    surfaceVariant = Color(0xFF4C4630),
    onSurfaceVariant = Color(0xFFCCC4AE),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

val BananaPalette = Palette(
    id = "banana",
    label = "Банан",
    light = BananaLight,
    dark = BananaDark
)


//
// СПИСОК ВСЕХ ПАЛИТ: чтобы добавить новую палитру просто добавь её сюда. Больше ничего менять не нужно.
val allPalettes: List<Palette> = listOf(
    SystemPalette,
    MonochromePalette,
    MintPalette,
    IndigoPalette,
    TerracottaPalette,
    RosePalette,
    AquaPalette,
    BananaPalette
)

// Быстрый поиск по ID (для Theme.kt)
fun findPalette(id: String): Palette =
    allPalettes.find { it.id == id } ?: SystemPalette