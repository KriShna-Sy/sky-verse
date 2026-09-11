# SKY VERSE — SYSTEM ARCHITECTURE & TECHNICAL STRATEGY

> **Sky belongs to the user. Sky runs primarily on the user's phone. Personal information remains on the phone by default.**

---

## 1. Executive Architecture Overview

**Sky Verse** is built as an **offline-first, privacy-first, zero-cloud mobile AI companion**. The personal AI assistant inside the application is named **Sky** and addresses the user as **KriShna**.

```
                           +------------------------+
                           |       SKY VERSE        |
                           |   (Android App / UI)   |
                           +-----------+------------+
                                       |
                                       v
                           +------------------------+
                           |    SKY ORCHESTRATOR    |
                           +-----------+------------+
                                       |
                                       v
                           +------------------------+
                           |     INTENT ROUTER      |
                           +-----------+------------+
                                       |
           +---------------------------+---------------------------+
           |                                                       |
           v                                                       v
+-----------------------+                               +-----------------------+
|  DETERMINISTIC TOOLS  |                               |    LOCAL AI ENGINE    |
| (Battery, Flashlight, |                               | (Qwen3 / SmolLM INT4, |
|  App Launcher, RAM)   |                               | Selective Local DB)   |
+-----------+-----------+                               +-----------+-----------+
           |                                                       |
           v                                                       v
+-----------------------+                               +-----------------------+
|  ANDROID NATIVE APIs  |                               |    LOCAL MEMORY DB    |
|  (Zero LLM Overhead)  |                               |   (Room / SQLite DB)  |
+-----------------------+                               +-----------------------+
```

---

## 2. Core Technical Principles & Enforcement

| Principle | Technical Implementation & Enforcement | Status |
| :--- | :--- | :--- |
| **1. Local-First** | `AndroidManifest.xml` omits `android.permission.INTERNET`. Zero cloud LLM API calls, zero remote telemetry, zero analytics SDKs. | **STRICTLY ENFORCED** |
| **2. Deterministic Command Routing** | Queries matching battery, flashlight, app launcher, or device info bypass the LLM completely. Executed directly via Android APIs in < 15ms. | **ACTIVE** |
| **3. Selective Local Memory** | Room Database (`SkyMemoryDatabase`) stores explicit preferences, facts, routines, and user instructions. Fully viewable, searchable, and erasable. | **ACTIVE** |
| **4. KriShna Visual Theme** | Deep midnight navy (`#0B132B`), electric cyan (`#00B4D8`), royal blue (`#3A506B`), with animated voice orb and glowing energy rings. | **IMPLEMENTED** |
| **5. Extreme Efficiency** | Event-driven wake word detection, on-demand local model loading, quantized INT4/INT8 formats, NNAPI/Vulkan backend acceleration. | **OPTIMIZED** |

---

## 3. Component Architecture breakdown

### 3.1 `core/ai` (AI Orchestration Engine)
- **`IntentRouter.kt`**: Parses queries against regex/semantic intent tables. Maps deterministic commands (`BATTERY_QUERY`, `FLASHLIGHT_ON`, `APP_LAUNCH`, `DEVICE_INFO`, `MEMORY_QUERY`, `PRIVACY_QUERY`) directly to native tool handlers, routing ambiguous queries to the local LLM.
- **`SkyOrchestrator.kt`**: Main coordinator. Fetches memory context from `MemoryManager`, executes safety checks via `SecurityPolicyManager`, delegates to `ToolManager` or `LocalLlamaInferenceEngine`, and shapes spoken responses.
- **`LocalModelManager.kt`**: Manages installed model specs (Fast 0.5B model, Reasoning 1.5B model, Vision VLM model), track memory footprints, load latency, and runtime status.

### 3.2 `core/voice` (Offline Voice Pipeline)
- **`SkyVoicePipeline.kt`**: Controls Android `TextToSpeech` with natural pitch (1.05f) and rate (0.95f), offline `SpeechRecognizer` listener, and "Hey Sky" wake word detection state machine.

### 3.3 `core/memory` (Selective Memory Engine)
- **`MemoryEntity.kt`**, **`MemoryDao.kt`**, **`SkyMemoryDatabase.kt`**: Modern Room Database storing key-value memories categorized under `PREFERENCE`, `FACT`, `REMINDER`, `ROUTINE`. Supports real-time Kotlin Coroutines Flow emissions, instant search, deletion, and nuclear wipe.

### 3.4 `tools` (Android Hardware Integrations)
- **`BatteryTool.kt`**: Queries `BatteryManager` directly for percentage and charging state.
- **`FlashlightTool.kt`**: Toggles camera LED torch via `CameraManager`.
- **`DeviceInfoTool.kt`**: Queries `ActivityManager.MemoryInfo` for RAM usage, available cores, and OS build metadata.
- **`AppLauncherTool.kt`**: Launches local installed apps via package manager intents.

### 3.5 `core/permissions` & `core/security`
- **`PermissionManager.kt`**: Handles granular runtime permission checks for microphone, camera, contacts, calendar, notifications.
- **`SecurityPolicyManager.kt`**: Enforces action safety policies. Safe actions execute immediately; destructive actions (file deletion, database wipe) mandate explicit user confirmation dialogs.

---

## 4. UI/UX Navigation Structure

The modern Jetpack Compose application includes 7 dedicated tabs in the bottom navigation bar:

1. **Home Screen**: Greeting to KriShna, `🔒 LOCAL MODE` indicator pill, large glowing cyan energy orb voice activator, and quick action suggestion chips.
2. **Talk Screen**: Interactive energy ring visualizer, live streaming speech-to-text transcript, response bubble with metadata pills (`⚡ DETERMINISTIC` vs `🧠 LOCAL LLM`).
3. **Memory Screen**: SQLite/Room local memory manager with search bar, category badges, individual delete buttons, and modal dialog for nuclear memory wipe.
4. **Skills Screen**: Interactive testing dashboard for deterministic tools (Battery, Flashlight, Device Metrics, App Launcher).
5. **Privacy Dashboard**: Live isolation status showing Internet Access OFF, Cloud AI OFF, Telemetry NONE, installed model counts, and permission toggles.
6. **Settings Screen**: User name customization ("KriShna"), Proactive Assistance slider (OFF, LOW, BALANCED, HIGH), local model manager registry.
7. **Performance Dashboard**: Real-time developer profiler tracking RAM consumption, tokens/sec, STT/TTS latency, thermal state, and local benchmark runner.

---

## 5. Verification & Build Artifacts

- **Android Debug APK**: Compiled successfully at `app/build/outputs/apk/debug/app-debug.apk` using `./gradlew assembleDebug` (37 Gradle tasks executed, zero warnings/errors).
- **Python `ai_env` Model Benchmark**: Evaluated model loading, PyTorch CUDA GPU support, ONNX execution, and token synthesis via `model_benchmark.py` running in `/home/krishna/ai_env`.
- **Interactive Web Companion Simulator**: Built at `web_demo/index.html` and served locally via HTTP server on port 8080.
