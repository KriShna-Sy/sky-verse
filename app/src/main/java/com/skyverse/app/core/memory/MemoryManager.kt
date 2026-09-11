package com.skyverse.app.core.memory

import android.content.Context
import kotlinx.coroutines.flow.Flow

class MemoryManager(context: Context) {
    private val dao = SkyMemoryDatabase.getInstance(context).memoryDao()

    val allMemoriesFlow: Flow<List<MemoryEntity>> = dao.getAllMemoriesFlow()

    suspend fun saveMemory(
        key: String,
        value: String,
        category: String = "PREFERENCE",
        isPersistent: Boolean = true
    ): Long {
        val entity = MemoryEntity(
            key = key,
            value = value,
            category = category,
            isPersistent = isPersistent
        )
        return dao.insertMemory(entity)
    }

    suspend fun getAllMemories(): List<MemoryEntity> {
        return dao.getAllMemories()
    }

    suspend fun searchMemories(query: String): List<MemoryEntity> {
        return dao.searchMemories(query)
    }

    suspend fun deleteMemory(id: Long) {
        dao.deleteMemoryById(id)
    }

    suspend fun clearAll() {
        dao.clearAllMemories()
    }

    suspend fun getMemoryCount(): Int {
        return dao.getMemoryCount()
    }

    suspend fun getFormattedMemoryContext(): String {
        val memories = dao.getAllMemories()
        if (memories.isEmpty()) return "No stored memory yet."
        val builder = StringBuilder("User Memories:\n")
        memories.take(10).forEach { mem ->
            builder.append("- ").append(mem.key).append(": ").append(mem.value).append("\n")
        }
        return builder.toString()
    }
}
