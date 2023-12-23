package utils

import database.FileRecord
import database.DatabaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import utils.FileUtils.calculateMD5
import utils.FileUtils.getPathOnly
import utils.FileUtils.isAudioFile
import java.io.File

object DatabaseSyncHelper {

    suspend fun sync(onSyncStarted: (numberOfFiles: Int) -> Unit,
                     onFileProcessed: (count: Int) -> Unit,
                     onFinalizing: () -> Unit,
                     onCompletion: () -> Unit): Boolean {
        val mainDirectory = PreferencesManager.getMusicDir() ?: return false
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
        execute(musicFileRecords)
        execute(musicFileRecords)
        onCompletion()
        return true
    }

    private suspend fun execute(musicFileRecords: List<FileRecord>) {
        var databaseRecords = DatabaseService.getAll()
            .map {
                it.apply { sync = true }
            }

        val deletedRecords = getDeletedFileRecordIds(databaseRecords, musicFileRecords)

        deletedRecords.forEach {
            DatabaseService.delete(it)
        }
        databaseRecords = databaseRecords.filter { !deletedRecords.contains(it.id) }

        val uniqueFileRecords = musicFileRecords.filter { fileRecord -> musicFileRecords.filter { it.hash == fileRecord.hash }.size == 1}
        val duplicateFileRecords = musicFileRecords.filter { fileRecord -> musicFileRecords.filter { it.hash == fileRecord.hash }.size > 1}.groupBy { it.hash }

        uniqueFileRecords.forEach { fileRecord ->
            val dbRecords = databaseRecords.filter { it.hash == fileRecord.hash}.toMutableList()
            // no records in db with the same hash
            if (dbRecords.isEmpty()){
                DatabaseService.insert(fileRecord)
                return@forEach
            }

            // one record in db with the same hash
            if (dbRecords.size == 1){
                val dbRecord = dbRecords[0].copy()
                dbRecord.name = fileRecord.name
                dbRecord.path = fileRecord.path
                DatabaseService.update(dbRecord)
                return@forEach

            }
            // multiple records in db with the same hash
            val referenceRecord = dbRecords.firstOrNull { it.path == fileRecord.path } ?: dbRecords.first()
            if (referenceRecord.name != fileRecord.name){
                DatabaseService.update(referenceRecord.apply { name = fileRecord.name })
            }
            dbRecords.filter { it.id != referenceRecord.id }.forEach {
                DatabaseService.delete(it.id)
            }
        }

        duplicateFileRecords.keys.forEach {
            duplicateFileRecords[it]?.let { fileRecords ->
                val dbRecords = databaseRecords.filter { dbRecord -> dbRecord.hash == it }
                handleDuplicates(fileRecords, dbRecords)
            }
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

    private suspend fun getDeletedFileRecordIds(databaseRecords: List<FileRecord>, musicRecords: List<FileRecord>): List<Int?> {
        return databaseRecords
            .filter { record -> musicRecords.none { it.hash == record.hash } }
            .map { it.id }
    }

    private suspend fun handleDuplicates(fileRecords: List<FileRecord>, dbRecords: List<FileRecord>){
        val mutableFileRecords = fileRecords.toMutableList()
        val mutableDbRecords = dbRecords.toMutableList()

        // handle same path instances

        fileRecords.forEach { fileRecord ->
            mutableDbRecords.firstOrNull { it.path ==  fileRecord.path}?.let { dbRecord ->
                if (dbRecord.name != fileRecord.name){
                    DatabaseService.update(dbRecord.apply { name = fileRecord.name })
                }
                mutableDbRecords.remove(dbRecord)
                mutableFileRecords.remove(fileRecord)
            }
        }

        val filteredFileRecords = mutableFileRecords.toMutableList()

        mutableFileRecords.forEach { fileRecord ->
            mutableDbRecords.firstOrNull()?.let { dbRecord ->
                dbRecord.copy().apply {
                    path = fileRecord.path
                    name = fileRecord.name
                }
                DatabaseService.update(dbRecord)
                mutableFileRecords.remove(dbRecord)
                filteredFileRecords.remove(fileRecord)
            }
        }
        filteredFileRecords.forEach {
            DatabaseService.insert(it)
        }
        mutableDbRecords.forEach {
            DatabaseService.delete(it.id)
        }

        return

    }
}