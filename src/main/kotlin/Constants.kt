import androidx.compose.ui.graphics.Color

object Constants {

    val supportedAudioExtensions = arrayOf("mp3", "wav", "flac", "wma", "aiff", "ogg", "aac")


    object AppColors {
        val background = Color(0xFF151515)
        val dialogBackground = Color(0xFF222222)
        val accent = Color(0xFF7EA3CC)
        val green = Color(0xFF41D6A2)
        val red = Color(0xFFD64040)

        val disabled = Color.DarkGray
        val black = Color.Black
        val white = Color.White

    }

    object StringResources {
        const val appName = "FileSync"
        const val noDirectorySelected = "No directory selected"
        const val selectDirectory = "Select Directory"
        const val serverStatusOffline = "Server seems to be offline."
        const val serverStatusErrorCode = "Failed to communicate with the server.\n Error Code: "

    }

    object ImageResources {
        const val logo = "logo.png"
        const val folder = "folder.png"
        const val audio = "audio.png"
        const val back = "back.png"
        const val minimize = "minimize.png"
        const val maximize = "maximize.png"
        const val floating = "floating.png"
        const val sync = "sync.svg"
        const val dontSync = "dont_sync.svg"

    }

}