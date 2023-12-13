import androidx.compose.ui.graphics.Color

object Constants {

    val supportedAudioExtensions = arrayOf("mp3", "wav", "flac", "wma", "aiff", "ogg")


    object AppColors {
        val background = Color(0xFF191919)
        val accent = Color(0xFF7EA3CC)
        val black = Color.Black
        val white = Color.White
        val disabled = Color.DarkGray

    }

    object StringResources {
        val appName = "FileSync"
        val noDirectorySelected = "No directory selected"
        val selectDirectory = "Select Directory"
    }

    object ImageResources {
        val logo = "logo-white.png"
        val folder = "folder.png"
        val audio = "audio.png"
        val back = "back.png"
    }

}