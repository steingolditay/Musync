package utils

import Constants
import com.mpatric.mp3agic.Mp3File

import java.io.File
import java.math.BigInteger
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption
import java.security.MessageDigest
import java.util.*
import javax.swing.JFileChooser

object FileUtils {
    private val md5 = MessageDigest.getInstance("MD5")

    fun pickFolder(): String? {

         val fileChooser = JFileChooser().apply {
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
         }
         val result = fileChooser.showOpenDialog(null)
         return if (result == JFileChooser.APPROVE_OPTION){
             fileChooser.selectedFile.absolutePath
         } else {
             null
         }
    }

    fun File.isAudioFile(): Boolean {
        return Arrays.stream(Constants.supportedAudioExtensions).anyMatch {
            this.name.endsWith(it)
        }
    }

    fun File.getContentType(): String {
        val name = this.name
        return when  {
            name.endsWith("mp3", true) -> "audio/mpeg"
            name.endsWith("wav", true) -> "audio/wav"
            name.endsWith("flac", true) -> "audio/flac"
            name.endsWith("wma", true) -> "audio/x-ms-wma"
            name.endsWith("aiff", true) -> "audio/aiff"
            name.endsWith("aac", true) -> "audio/aac"
            else -> {
                "audio/mpeg"
            }
        }
    }

    fun File.getPathOnly(): String {
        return this.path.removeSuffix(this.name)
    }

    fun File.getFullPath(): String {
        return "${this.getPathOnly()}${this.name}"
    }

    fun File.getLengthInSeconds(): Long {
        return Mp3File(this.getFullPath()).lengthInSeconds
    }

    fun File.getLengthInMilliseconds(): Long {
        return Mp3File(this.getFullPath()).lengthInMilliseconds
    }



    fun File.calculateMD5(): String {

        val channel = FileChannel.open(this.toPath(), StandardOpenOption.READ)
        val fileSize = channel.size()
        val checkSize = fileSize / 30

        val startByteBuffer: MappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, checkSize)
        val startResult = ByteArray(checkSize.toInt())
        startByteBuffer.get(startResult)

        channel.close()

        return BigInteger(1, md5.digest(startResult)).toString(16).padStart(32, '0')
    }

}