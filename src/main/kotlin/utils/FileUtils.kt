package utils

import Constants
import java.io.File
import java.util.*
import javax.swing.JFileChooser

object FileUtils {
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

    fun getSortedFilesFromDirectory(directory: File): Array<File> {
        if (!directory.isDirectory){
            return emptyArray()
        }
        val files = directory.listFiles()

        files?.filter { it.isDirectory || it.isAudioFile() }?.sortedBy {
            val s = it.name.split(" ")[0]
            it.name.split(" ")[0]

        }
        return emptyArray()

    }

    fun File.isAudioFile(): Boolean {
        return Arrays.stream(Constants.supportedAudioExtensions).anyMatch {
            this.name.endsWith(it)
        }
    }
}