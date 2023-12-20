import androidx.compose.ui.graphics.Color

object Constants {

    val supportedAudioExtensions = arrayOf("mp3", "wav", "flac", "wma", "aiff", "ogg")


    object AppColors {
        val background = Color(0xFF191919)
        val dialogBackground = Color(0xFF252729)
        val accent = Color(0xFF7EA3CC)
        val green = Color(0xFF41D6A2)
        val red = Color(0xFFD64040)

        val disabled = Color.DarkGray
        val black = Color.Black
        val white = Color.White

    }

    object StringResources {
        val appName = "FileSync"
        val noDirectorySelected = "No directory selected"
        val selectDirectory = "Select Directory"
        val serverStatusOffline = "Server seems to be offline."
        val serverStatusErrorCode = "Failed to communicate with the server.\n Error Code: "

    }

    object ImageResources {
        val logo = "logo-white.png"
        val folder = "folder.png"
        val audio = "audio.png"
        val back = "back.png"
    }

}