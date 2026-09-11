package com.skyverse.app.core.vision

import android.content.Context

data class PersonCharacterTag(
    val personName: String,
    val samplePhotoPath: String,
    val dateTaggedMs: Long = System.currentTimeMillis()
)

data class GalleryPhotoItem(
    val photoName: String,
    val absolutePath: String,
    val matchedPersonName: String,
    val confidence: Float
)

class LocalFaceGalleryEngine(private val context: Context) {
    private val taggedCharacters = mutableListOf<PersonCharacterTag>()

    init {
        // Enrol default character tags for KriShna
        taggedCharacters.add(
            PersonCharacterTag(
                personName = "KriShna",
                samplePhotoPath = "/storage/emulated/0/DCIM/Camera/KriShna_Profile.jpg"
            )
        )
    }

    fun tagPersonCharacter(personName: String, photoPath: String) {
        taggedCharacters.removeAll { it.personName.equals(personName, ignoreCase = true) }
        taggedCharacters.add(PersonCharacterTag(personName, photoPath))
    }

    fun getTaggedCharacters(): List<PersonCharacterTag> = taggedCharacters

    fun searchPhotosByPerson(personName: String): List<GalleryPhotoItem> {
        val cleanName = personName.trim()
        val mockGallery = listOf(
            GalleryPhotoItem("KriShna_Lab_Working.jpg", "/storage/emulated/0/DCIM/Camera/KriShna_Lab_Working.jpg", "KriShna", 0.96f),
            GalleryPhotoItem("KriShna_Robot_Testing.jpg", "/storage/emulated/0/DCIM/Camera/KriShna_Robot_Testing.jpg", "KriShna", 0.94f),
            GalleryPhotoItem("Mom_Birthday.jpg", "/storage/emulated/0/DCIM/Camera/Mom_Birthday.jpg", "Mom", 0.92f),
            GalleryPhotoItem("Friend_Team.jpg", "/storage/emulated/0/DCIM/Camera/Friend_Team.jpg", "Alex", 0.89f)
        )

        return mockGallery.filter {
            it.matchedPersonName.contains(cleanName, ignoreCase = true) || cleanName.isBlank()
        }
    }
}
