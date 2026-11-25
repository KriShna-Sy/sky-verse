# Sky Verse: Offline, Private AI Assistant for Android

[![Android](https://img.shields.io/badge/Android-8.0%2B%20(API%2026)-3DDC84?logo=android)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-Jetpack%20Compose-7F52FF?logo=kotlin)](https://kotlinlang.org/)
[![llama.cpp](https://img.shields.io/badge/LLM-llama.cpp%20(JNI)-black)](https://github.com/ggml-org/llama.cpp)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**Sky** is a voice assistant that runs **entirely on your phone**. It has no cloud
LLM calls, no telemetry, and the app requests no `INTERNET` permission. A quantized
GGUF model (Qwen2.5 / SmolLM2) runs on-device through llama.cpp over JNI.
Simple commands skip the LLM entirely and hit native Android APIs directly.

## Highlights

- **On-device LLM.** llama.cpp is compiled into the APK (static, CPU-only, arm64-v8a / x86_64). Sky picks the model by phone RAM tier.
- **Deterministic fast path.** `IntentRouter` sends battery, flashlight, app-launch and device-info queries straight to native tools, with no LLM round-trip.
- **Offline voice.** Uses Android's on-device speech recognizer and system TTS, with a "Hey Sky" wake-word state machine.
- **Selective local memory.** A Room/SQLite store of preferences, facts and routines. Every entry can be viewed, searched and deleted.
- **Safety policy.** `SecurityPolicyManager` requires explicit confirmation before destructive actions.
- **7-screen Compose UI.** Home, Talk, Memory, Skills, Privacy dashboard, Settings, Performance profiler.

## Architecture

```
SkyVerse UI (Jetpack Compose, MVVM, Hilt)
        │
  SkyOrchestrator ──► SecurityPolicyManager
        │
   IntentRouter
     ├── deterministic ──► ToolManager (Battery, Flashlight, AppLauncher, DeviceInfo, ...)
     └── open-ended ────► llama.cpp JNI (local GGUF) + Room memory context
```

Full design notes are in [ARCHITECTURE.md](ARCHITECTURE.md).

## Build

Requirements: Android Studio (or the SDK + NDK + CMake 3.22.1+), JDK 17.

```bash
git clone https://github.com/KriShna-Sy/sky-verse.git && cd sky-verse
# llama.cpp source is pinned and not vendored:
git clone --depth 1 --branch b12212 https://github.com/ggml-org/llama.cpp app/src/main/cpp/llama.cpp
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Add a model

```bash
pip install -U huggingface_hub
huggingface-cli download Qwen/Qwen2.5-0.5B-Instruct-GGUF qwen2.5-0.5b-instruct-q4_k_m.gguf --local-dir .
adb shell mkdir -p /sdcard/Android/data/com.skyverse.app/files/models
adb push qwen2.5-0.5b-instruct-q4_k_m.gguf /sdcard/Android/data/com.skyverse.app/files/models/
```

[models/README.md](models/README.md) has the RAM-tier → model table and the in-app import option.

## Web demo

`web_demo/index.html` is a browser-only simulator of the UI:

```bash
cd web_demo && python3 -m http.server 8080   # open http://localhost:8080
```

## License

MIT, see [LICENSE](LICENSE). Model weights keep their own licenses.
