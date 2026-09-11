package com.skyverse.app.tools

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore

class AppLauncherTool(private val context: Context) {

    fun playMediaOnYouTube(songOrVideoQuery: String): String {
        val cleanQuery = songOrVideoQuery
            .replace("can u play", "", ignoreCase = true)
            .replace("play", "", ignoreCase = true)
            .replace("song", "", ignoreCase = true)
            .replace("in utube", "", ignoreCase = true)
            .replace("on youtube", "", ignoreCase = true)
            .replace("in youtube", "", ignoreCase = true)
            .replace("open youtube and play it naaa", "", ignoreCase = true)
            .replace("and play it naaa", "", ignoreCase = true)
            .trim()

        val searchQuery = cleanQuery.ifEmpty { "mein tera boyfriend" }

        return try {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/results?search_query=" + Uri.encode(searchQuery))
            ).apply {
                setPackage("com.google.android.youtube")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            context.startActivity(intent)
            "Opening YouTube and playing '$searchQuery' for KriShna... 🎵"
        } catch (e: Exception) {
            // Fallback to browser or generic media intent if YouTube app package not set
            try {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/results?search_query=" + Uri.encode(searchQuery))
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(browserIntent)
                "Playing '$searchQuery' on YouTube..."
            } catch (e2: Exception) {
                "Could not launch YouTube: ${e2.localizedMessage}"
            }
        }
    }

    fun launchApp(rawAppName: String): String {
        var query = rawAppName.lowercase().trim()

        // Clean common conversational suffixes & fillers
        query = query
            .replace("and play it naaa", "")
            .replace("and play it", "")
            .replace("naaa", "")
            .replace("please", "")
            .trim()

        if (query.contains("utube") || query.contains("youtube") || query.contains("yt") || query.contains("play")) {
            return playMediaOnYouTube(rawAppName)
        }

        if (query.contains("camera")) {
            return try {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                "Opening CAMERA..."
            } catch (e: Exception) {
                "Could not launch camera: ${e.localizedMessage}"
            }
        }

        val pkg = when {
            query.contains("setting") -> "com.android.settings"
            query.contains("clock") || query.contains("alarm") -> "com.android.deskclock"
            query.contains("calculator") -> "com.android.calculator2"
            query.contains("calendar") -> "com.android.calendar"
            query.contains("contacts") -> "com.android.contacts"
            query.contains("chrome") || query.contains("browser") -> "com.android.chrome"
            query.contains("whatsapp") -> "com.whatsapp"
            else -> null
        }

        return try {
            if (pkg != null) {
                val intent = context.packageManager.getLaunchIntentForPackage(pkg)
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    "Opening ${query.uppercase()}..."
                } else {
                    "Application '$query' is not installed."
                }
            } else {
                "Searching local packages for '$query'..."
            }
        } catch (e: Exception) {
            "Could not launch $query: ${e.localizedMessage}"
        }
    }
}
