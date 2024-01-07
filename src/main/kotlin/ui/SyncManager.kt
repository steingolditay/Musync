package ui

import enums.SyncProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import models.FileRecord
import network.ClientApi

object SyncManager {

    private val _syncProgressState = MutableStateFlow(SyncProgress.INDEXING)
    val syncProgressState = _syncProgressState.asStateFlow()

    private val _numberOfFilesToProcess = MutableStateFlow(0)
    val numberOfFilesToProcess = _numberOfFilesToProcess.asStateFlow()

    private val _numberOfFilesProcessed = MutableStateFlow(0)
    val numberOfFilesProcessed = _numberOfFilesProcessed.asStateFlow()

    private val _uploadProgress = MutableStateFlow(0f)
    val uploadProgress = _uploadProgress.asStateFlow()

    private val _filesToUpload = MutableStateFlow(listOf<FileRecord>())
    val filesToUpload = _filesToUpload.asStateFlow()

    private val _numberOfFilesToUpload = MutableStateFlow(0)
    val numberOfFilesToUpload = _numberOfFilesToUpload.asStateFlow()

    private val _numberOfFilesUploaded = MutableStateFlow(0)
    val numberOfFilesUploaded = _numberOfFilesUploaded.asStateFlow()


    fun setProgressState(state: SyncProgress){
        _syncProgressState.value = state
    }

    fun setUploadProgress(progress: Float){
        _uploadProgress.value = progress
    }

    fun setNumberOfFilesToUpload(count: Int){
        _numberOfFilesToUpload.value = count
    }

    fun setNumberOfFilesToProcess(count: Int){
        _numberOfFilesToProcess.value = count
    }

    fun setNumberOfFilesProcessed(count: Int){
        _numberOfFilesProcessed.value = count
    }

    fun setFilesToUpload(list: List<FileRecord>){
        _filesToUpload.value = list
    }

    fun setNumberOfFilesUploaded(count: Int){
        _numberOfFilesUploaded.value = count
    }

    suspend fun uploadFiles(){
        ClientApi.uploadFiles(
            filesToUpload.value,
            onFileUploaded = {
                setNumberOfFilesUploaded(numberOfFilesUploaded.value + 1)
                if (numberOfFilesToUpload.value == numberOfFilesUploaded.value){
                    setProgressState(SyncProgress.COMPLETE)
                }
            },
            onFileUploadProgress = {
                setUploadProgress(it)
            }
        )
    }

    fun reset(){
        setProgressState(SyncProgress.INDEXING)
        setUploadProgress(0f)
        setNumberOfFilesToUpload(0)
        setNumberOfFilesToProcess(0)
        setNumberOfFilesProcessed(0)
        setNumberOfFilesUploaded(0)
        setFilesToUpload(listOf())
    }
}