package utils

import java.util.prefs.Preferences

object PreferencesManager {
    private const val SETTINGS = "SETTINGS"
    // dirs
    private const val MUSIC_DIR = "MUSIC_DIR"
    private const val PHOTOS_DIR = "PHOTOS_DIR"
    private const val VIDEO_DIR = "VIDEO_DIR"

    private val preferences = Preferences.userRoot().node(SETTINGS)

    fun hasMusicDir(): Boolean {
        return preferences.get(MUSIC_DIR, "").isNotBlank()
    }

    fun setMusicDir(path: String){
        preferences.put(MUSIC_DIR, path)
    }

    fun getMusicDir(): String? {
        val dir = preferences.get(MUSIC_DIR, "")
        return dir.ifBlank { null }
    }
}