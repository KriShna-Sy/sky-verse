package com.skyverse.app.core.ai

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

enum class ModelStatus {
    READY,        // loaded in RAM
    UNLOADED,     // on disk, not loaded
    NOT_INSTALLED
}

data class ModelSpec(
    val id: String,
    val name: String,
    val sizeMb: Int,
    val quantization: String,
    val status: ModelStatus,
    val description: String,
    val path: String = "",
    val isActive: Boolean = false
)

/**
 * Finds real GGUF models on the device. Searched folders (first wins on name clash):
 *  1. /sdcard/Android/data/com.skyverse.app/files/models   <- `adb push x.gguf` here, no permission needed
 *  2. <internal>/files/models                              <- where "Import model" copies to
 *  3. /sdcard/SkyVerse/models                              <- only if the OS lets us read it
 */
class LocalModelManager(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences("sky_models", Context.MODE_PRIVATE)

    fun modelDirs(): List<File> = listOfNotNull(
        appContext.getExternalFilesDir("models"),
        File(appContext.filesDir, "models"),
        File("/sdcard/SkyVerse/models")
    ).onEach { runCatching { it.mkdirs() } }

    fun findModelFiles(): List<File> = modelDirs()
        .filter { it.isDirectory && it.canRead() }
        .flatMap { dir -> dir.listFiles { f -> f.isFile && f.name.endsWith(".gguf", true) }?.toList().orEmpty() }
        .distinctBy { it.name }
        .sortedBy { it.length() }

    /** User choice if still present, else the biggest model that fits this phone's RAM tier. */
    fun activeModelFile(): File? {
        val files = findModelFiles()
        if (files.isEmpty()) return null
        prefs.getString(KEY_ACTIVE, null)?.let { saved -> files.firstOrNull { it.absolutePath == saved }?.let { return it } }
        val budgetBytes = when (getDeviceRamTier(appContext)) {
            RamTier.LITE -> 500L
            RamTier.STANDARD -> 1_100L
            RamTier.POWER -> 2_200L
            RamTier.ULTRA -> 4_000L
        } * 1024 * 1024
        return files.lastOrNull { it.length() <= budgetBytes } ?: files.first()
    }

    fun setActiveModel(path: String) {
        prefs.edit().putString(KEY_ACTIVE, path).apply()
    }

    fun getInstalledModels(): List<ModelSpec> {
        val active = activeModelFile()
        return findModelFiles().map { f ->
            val isActive = f.absolutePath == active?.absolutePath
            ModelSpec(
                id = f.absolutePath,
                name = f.nameWithoutExtension,
                sizeMb = (f.length() / (1024 * 1024)).toInt(),
                quantization = QUANT_REGEX.find(f.name.uppercase())?.value ?: "GGUF",
                status = if (isActive && LlamaEngine.loadedModelPath == f.absolutePath) ModelStatus.READY else ModelStatus.UNLOADED,
                description = f.parent ?: "",
                path = f.absolutePath,
                isActive = isActive
            )
        }
    }

    fun getActiveModel(): ModelSpec? = getInstalledModels().firstOrNull { it.isActive }

    /** Copies a .gguf picked with the system file picker into internal storage. */
    suspend fun importModel(uri: Uri): Result<File> = withContext(Dispatchers.IO) {
        runCatching {
            val resolver = appContext.contentResolver
            val name = resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
                if (c.moveToFirst()) c.getString(0) else null
            } ?: "model_${System.currentTimeMillis()}.gguf"
            require(name.endsWith(".gguf", true)) { "Not a .gguf file: $name" }
            val dir = File(appContext.filesDir, "models").apply { mkdirs() }
            val out = File(dir, name)
            val tmp = File(dir, "$name.part")
            resolver.openInputStream(uri)!!.use { input -> tmp.outputStream().use { input.copyTo(it, 1 shl 20) } }
            check(tmp.renameTo(out)) { "Could not save $name" }
            setActiveModel(out.absolutePath)
            out
        }
    }

    fun benchmarkModelLoadTime(): Long = LlamaEngine.lastLoadTimeMs

    companion object {
        private const val KEY_ACTIVE = "active_model_path"
        private val QUANT_REGEX = Regex("(IQ\\d_[A-Z0-9_]+|Q\\d(_[A-Z0-9]+)*|F16|BF16|F32)")
    }
}
