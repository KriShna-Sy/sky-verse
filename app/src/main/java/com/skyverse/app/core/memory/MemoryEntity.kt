package com.skyverse.app.core.memory

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sky_memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val key: String,
    val value: String,
    val category: String, // e.g. PREFERENCE, FACT, REMINDER, ROUTINE
    val timestamp: Long = System.currentTimeMillis(),
    val isPersistent: Boolean = true,
    val source: String = "EXPLICIT_USER"
)
