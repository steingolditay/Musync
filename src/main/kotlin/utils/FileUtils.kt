package utils

import Constants

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



    fun File.getPathOnly(): String {
        return this.path.removeSuffix(this.name)
    }


    fun File.calculateMD5(): String {

        val channel = FileChannel.open(this.toPath(), StandardOpenOption.READ)
        val fileSize = channel.size()
        val checkSize = fileSize / 30

        val startByteBuffer: MappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, checkSize)
        val startResult = ByteArray(checkSize.toInt())
        startByteBuffer.get(startResult)

//        val midPosition = (fileSize / 2 ) - (checkSize / 2)
//        val midByteBuffer: MappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, midPosition, checkSize)
//        val midResult = ByteArray(checkSize.toInt())
//        midByteBuffer.get(midResult)
//
//        val endByteBuffer: MappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, fileSize - checkSize, checkSize)
//        val endResult = ByteArray(checkSize.toInt())
//        endByteBuffer.get(endResult)

        channel.close()
//        val result = startResult.plus(midResult).plus(endResult)

        return BigInteger(1, md5.digest(startResult)).toString(16).padStart(32, '0')
    }

}