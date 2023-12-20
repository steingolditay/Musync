package utils

import Constants
import database.DatabaseRecord
import database.DatabaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import network.ClientApi
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.math.BigInteger
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption
import java.security.DigestInputStream
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

    fun File.getPathOnly(): String {
        return this.path.removeSuffix(this.name)
    }

    fun syncMusicFilesToDb(){
        PreferencesManager.getMusicDir()?.let {
            ClientApi.scope?.launch(Dispatchers.IO) {

                val musicFiles = File(it).walk()
                    .filter { file -> !file.isDirectory && file.isAudioFile()}
                    .map { file -> DatabaseRecord(null, file.name, file.getPathOnly(), file.calculateMD5(), true) }
                    .toList()

                val databaseRecords = DatabaseService.getAll().map { it.apply { sync = true } }
                val duplicatedHashDealt = mutableListOf<String>()

                musicFiles.forEach { musicFile ->
                    if (duplicatedHashDealt.contains(musicFile.hash)){
                        return@forEach
                    }
                    if (databaseRecords.none { it.hash == musicFile.hash}){
                        DatabaseService.insert(musicFile)
                    } else {
                        val existingRecords = databaseRecords.filter { it.hash == musicFile.hash }
                        if (existingRecords.size == 1){
                            val record = existingRecords[0]
                            val isPathChanged = record.path != musicFile.path
                            val isNameChanged = record.name != musicFile.name
                            if (isPathChanged || isNameChanged){
                                record.apply {
                                    if (isNameChanged) name = musicFile.name
                                    if (isPathChanged) path = musicFile.path
                                }
                                DatabaseService.update(record)
                            }
                            return@forEach
                        }
                        duplicatedHashDealt.add(musicFile.hash)
                        val hashInstances = musicFiles.filter { it.hash == musicFile.hash }.toMutableList()
                        existingRecords.forEach duplicates@ { record ->
                            val recordCopy = record.copy(id = null)
                            if (hashInstances.contains(recordCopy)){
                                // do nothing
                                hashInstances.remove(recordCopy)
                                return@duplicates
                            }

                            if (hashInstances.none { instance -> instance.path == record.path}){
                                // delete from db
                                DatabaseService.delete(record.id)
                            }

                            hashInstances.firstOrNull {
                                // rename in db
                                it.path == record.path
                            }?.let { renamedInstance ->
                                DatabaseService.update(record.apply { name = renamedInstance.name })
                                hashInstances.remove(record.copy(id = null))
                            }
                        }
                        hashInstances.forEach {
                            DatabaseService.insert(it)
                        }
                    }
                }
                DatabaseService.getAll().forEach {
                    println(it)
                }
            }
        }

    }

}