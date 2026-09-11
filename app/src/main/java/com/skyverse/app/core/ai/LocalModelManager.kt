package com.skyverse.app.core.ai

enum class ModelStatus {
    READY,
    DOWNLOADING,
    UNLOADED,
    NOT_INSTALLED
}

data class ModelSpec(
    val id: String,
    val name: String,
    val sizeMb: Int,
    val quantization: String,
    val status: ModelStatus,
    val description: String
)

class LocalModelManager {
    private val models = mutableMapOf<String, ModelSpec>(
        "fast_llm" to ModelSpec(
            id = "fast_llm",
            name = "Sky-Qwen3-0.5B (Fast)",
            sizeMb = 350,
            quantization = "INT4-GGML",
            status = ModelStatus.READY,
            description = "Ultra-fast response model optimized for low RAM and battery saving."
        ),
        "reasoning_llm" to ModelSpec(
            id = "reasoning_llm",
            name = "Sky-Qwen3-1.5B (Reasoning)",
            sizeMb = 980,
            quantization = "INT4-GGUF",
            status = ModelStatus.READY,
            description = "Detailed reasoning and synthesis model for complex natural language queries."
        ),
        "vision_model" to ModelSpec(
            id = "vision_model",
            name = "Sky-VLM-Lite (Vision)",
            sizeMb = 620,
            quantization = "INT8-ONNX",
            status = ModelStatus.READY,
            description = "Local vision model for OCR and camera item description."
        )
    )

    fun getInstalledModels(): List<ModelSpec> = models.values.toList()

    fun getActiveModel(): ModelSpec = models["fast_llm"]!!

    fun benchmarkModelLoadTime(): Long {
        val start = System.currentTimeMillis()
        // Simulate local memory mapping / tensor initialization check
        Thread.sleep(35)
        return System.currentTimeMillis() - start
    }
}
