package com.skyverse.app.core.voice

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sky_voice_profiles")
data class VoiceProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val speakerName: String = "KriShna",
    val isEnrolled: Boolean = true,
    val embeddingVectorJson: String, // Encrypted JSON array of local float embedding
    val minConfidenceThreshold: Float = 0.78f,
    val enrolledTimestamp: Long = System.currentTimeMillis()
)
