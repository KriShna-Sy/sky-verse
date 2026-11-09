# Sky Verse Models

Sky runs a real GGUF model on-device through llama.cpp (built into the APK).

## Where to put the model
Any `.gguf` in one of these folders is picked up (Settings → Local Model Registry shows them, tap to switch):

1. `/sdcard/Android/data/com.skyverse.app/files/models/`  ← easiest, no permission needed
   ```
   adb shell mkdir -p /sdcard/Android/data/com.skyverse.app/files/models
   adb push qwen2.5-0.5b-instruct-q4_k_m.gguf /sdcard/Android/data/com.skyverse.app/files/models/
   ```
2. In-app: **More → Import model (.gguf)** (copies into app storage)

If several are present Sky picks the largest one that fits the phone's RAM tier (or the one you tapped).

## Which model (Q4_K_M)
| Phone RAM | Model | Size |
|---|---|---|
| 4 GB | SmolLM2-360M-Instruct | ~270 MB |
| 6–8 GB | Qwen2.5-0.5B-Instruct | ~400 MB |
| 8–12 GB | Qwen2.5-1.5B-Instruct | ~1.0 GB |
| 12 GB+ | Qwen2.5-3B-Instruct | ~1.9 GB |

Download (on your PC):
```
pip install -U huggingface_hub
huggingface-cli download Qwen/Qwen2.5-0.5B-Instruct-GGUF qwen2.5-0.5b-instruct-q4_k_m.gguf --local-dir .
```

## Voice
Speech-to-text uses Android's recognizer (fully on-device on Android 13+ once the offline
speech pack for your language is installed); replies are spoken with the system TTS engine.
