package utils

object PlayerUtils {
    fun getTimeString(seconds: Long): String {
        val minutes = if (seconds > 60) seconds / 60 else 0
        val actualSeconds = seconds - (minutes * 60)

        return "${minutes.twoDigitFormat()}:${actualSeconds.twoDigitFormat()}"

    }

    private fun Long.twoDigitFormat(): String{
        return if (this in 0..9) "0${this}" else this.toString()
    }
}