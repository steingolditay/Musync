package database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import utils.FileUtils.calculateMD5
import utils.FileUtils.getPathOnly
import utils.FileUtils.isAudioFile
import ui.PreferencesManager
import java.io.File

object DatabaseSyncHelper {

    suspend fun sync(onSyncStarted: (numberOfFiles: Int) -> Unit,
                     onFileProcessed: (count: Int) -> Unit,
                     onFinalizing: () -> Unit,
                     onCompletion: () -> Unit) {
        val mainDirectory = PreferencesManager.getMusicDir() ?: return
        var count = 0;
        val musicFileRecords = getMusicFileRecords(mainDirectory,
            numberOfFiles = {
                onSyncStarted(it)
            },
            onFileProcessed = {
                count++
                onFileProcessed(count)
            }
        )
        onFinalizing()
        updateDatabase(musicFileRecords)
        onCompletion()
        return
    }

    private suspend fun updateDatabase(musicFileRecords: List<FileRecord>) {
        val databaseRecords = DatabaseService.getAll().toSet()

        val newFiles = musicFileRecords.filter { file -> databaseRecords.none { it.dataEqualsTo(file) } }
        val databaseRecordsToDelete = databaseRecords.filter { record -> musicFileRecords.none { it.dataEqualsTo(record) } }
        databaseRecordsToDelete.forEach {
            DatabaseService.delete(it.id)
        }

        newFiles.forEach {
            DatabaseService.insert(it)
        }


    }
    private suspend fun getMusicFileRecords(mainDirectory: String,
                                            numberOfFiles: (numberOfFiles:Int) -> Unit,
                                            onFileProcessed: () -> Unit): List<FileRecord> {
        return withContext(Dispatchers.IO) {
            val files = File(mainDirectory).walk()
                .filter { file -> !file.isDirectory && file.isAudioFile() }.also { numberOfFiles(it.toList().size) }
                .toList()

            val recordList = mutableListOf<FileRecord>()
            files.forEach {
                if (isActive){
                    recordList.add(FileRecord(null, it.name, it.getPathOnly(), it.calculateMD5(), true))
                    onFileProcessed()
                }
            }
            return@withContext recordList
        }
    }
}