[Перейти на Русский](README.md)

# Quickbot

**A fast, minimalist, and open-source AI assistant for Android.**

Quickbot is designed for those who need quick access to AI without unnecessary clutter, Google services dependency, or complex setup. It started as a personal project and grew into a fully-fledged tool.

## ✨ Features

- 🚀 **Fast Launch**: Opens via the assistant gesture as a compact overlay on top of other apps.
- 🎨 **Material You / Material 3**: Support for dynamic colors on Android 12+, plus 7 built-in palettes (Monochrome, Mint, Indigo, etc.) for older versions.
- 🔒 **Privacy**: Your API key is stored only on your device in an encrypted form (`EncryptedSharedPreferences`).
- 📝 **Markdown**: Full rendering of AI responses (headings, lists, code blocks, tables).
- 📋 **Copying**: Long-press any message (including your own and error messages) to copy it to the clipboard.
- 📱 **Adaptive**: Optimized for phones and tablets.
- 🚫 **No GMS Required**: Works on devices without Google services (Huawei, LineageOS, crDroid).

## 🛠 Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose (Material 3)
- **Networking**: OkHttp, OpenRouter API
- **Architecture**: MVVM (ViewModel + StateFlow, Room for persistence)
- **Build System**: Gradle (Kotlin DSL)

## 📲 Installation

1. Download the latest APK from the [Releases](https://github.com/madcreeepyman/quickbot/releases) section.
2. Install it on your device (Android 7.0+).
3. On first launch, enter your [OpenRouter](https://openrouter.ai/keys) API key.
4. (Optional) Set Quickbot as your default assistant app in system settings to invoke it via gesture.

## ⚙️ Setup

- **Model**: Defaults to `openai/gpt-4o-mini`. You can change it in the app settings (e.g., to the free `openai/gpt-oss-120b:free`).
- **Overlay**: The "Display over other apps" permission is required for the assistant gesture to work.
- **VPN**: Users in Russia may need a VPN (OpenRouter is blocked by local ISPs).

## 🤖 Honest Note on AI

I have nothing to hide, so I'll be honest: this project was written with the help of an AI assistant. Under my supervision, with my architectural decisions, testing on real devices, and attention to UX. Coding is not my primary profession; this is a hobby project I built in my spare time, learning as I go.

Quickbot started as a personal app for myself. I made it public only because I was looking for something similar and couldn't find it — maybe it will be useful to someone else.

If you don't like AI coding — that's fine, no one is forcing you to install it. If you find a bug or have an idea — I'm open to healthy criticism and discussion.

## 📄 License

This project is distributed under the **MIT License**. See the [LICENSE](LICENSE) file for details.
