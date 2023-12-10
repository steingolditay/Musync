package utils

import javax.swing.JFileChooser

object FolderPicker {
     fun pickFolder(): String? {

         val fileChooser = JFileChooser().apply {
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
         }
         val result = fileChooser.showOpenDialog(null)
         return if (result == JFileChooser.APPROVE_OPTION){
             fileChooser.selectedFile.path
         } else {
             null
         }
    }
}