package ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import network.ClientApi
import Constants.AppColors
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.*
import database.DatabaseService
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import database.DatabaseSyncHelper
import database.FileRecord
import kotlinx.coroutines.Dispatchers

@Composable
@Preview
fun SyncDialog(onDismiss: () -> Unit) {

    val scope = rememberCoroutineScope()
    val surfaceWidth = 350.dp
    val surfaceHeight = 200.dp
    val shadowSize = 16.dp

    var fileToProcess by remember { mutableStateOf(1) }
    var filesProcessed by remember { mutableStateOf(0) }

    var titleText by remember { mutableStateOf("") }
    var bodyText by remember { mutableStateOf("") }

    val filesToUpload by remember { mutableStateOf(mutableListOf<FileRecord>()) }
    var showUploadButton by remember { mutableStateOf(false) }
    var fileUploadProgress by remember { mutableStateOf(0f) }
    var isUploading by remember { mutableStateOf(false) }
    var isFinishedUploading by remember { mutableStateOf(false) }

    var numberOfFilesToUpload = 0
    var numberOfFilesUploaded = 0


    DisposableEffect(scope){
        var currentJob: Job? = null

        val serverSyncJob = scope.launch(start = CoroutineStart.LAZY) {
            val databaseRecords = DatabaseService.getAll().filter { it.sync }
            val response = ClientApi.syncFiles(databaseRecords)
            response?.let {
                if (it.isNotEmpty()){
                    it.forEach { hashToUpload ->
                        databaseRecords.firstOrNull { record -> record.hash == hashToUpload }?.let { record -> filesToUpload.add(record) }
                    }
                    numberOfFilesToUpload = it.size
                    titleText = "Ready to upload"
                    bodyText = "You got $numberOfFilesToUpload new files."
                    showUploadButton = true
                }
                else {
                    titleText = "You are all set"
                    bodyText = "No files to upload"
                }
            }
        }

        val databaseSyncJob = scope.launch(start = CoroutineStart.LAZY){
            DatabaseSyncHelper.sync(
                onSyncStarted = {
                    fileToProcess = it
                    titleText = "Indexing library files"
                    bodyText = ""
                    println("Started Syncing $it Files")
                },
                onFileProcessed = {
                    filesProcessed = it
                    titleText = "Indexing library files"
                    bodyText = "$filesProcessed / $fileToProcess"
                    println("Processed $it Files")
                },
                onFinalizing = {
                    titleText = "Updating Database"
                    bodyText = "this might take a moment..."
                },
                onCompletion = {
                    titleText = "Fetching data from server"
                    bodyText = ""
                    currentJob = serverSyncJob
                    currentJob?.start()
                }
            )
        }
        currentJob = databaseSyncJob
        currentJob?.start()

        onDispose {
            currentJob?.cancel()
        }
    }


    Dialog(onDismiss,
        transparent = true,
        undecorated = true,
        resizable = false,
        state = DialogState(
            position = WindowPosition(Alignment.Center),

        ),
    ){

        Surface(
            modifier = Modifier.clip(RoundedCornerShape(8.dp)),
            shape = RoundedCornerShape(8.dp),
            elevation = 8.dp,

        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .background(AppColors.dialogBackground)
                    .padding(8.dp),

                ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit",
                        tint = AppColors.white,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clickable {
                                onDismiss.invoke()
                            }
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        when {
                            isUploading -> "Uploading"
                            isFinishedUploading -> "You are all set!"
                            else -> titleText
                        },
                        color = AppColors.white,
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(top = 8.dp),
                        textAlign = TextAlign.Center,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        fontSize = 18.sp
                    )
                    Text(
                        when {
                            isUploading -> "$numberOfFilesUploaded / $numberOfFilesToUpload"
                            isFinishedUploading -> "All files were uploaded successfully"
                            else -> bodyText
                        },
                        color = AppColors.white,
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(top = 24.dp),
                        textAlign = TextAlign.Center
                    )
                    if (showUploadButton){
                        LinearProgressIndicator(
                            progress = fileUploadProgress,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 32.dp)
                        )
                        Button(
                            modifier = Modifier
                                .wrapContentHeight()
                                .wrapContentWidth(),
                            onClick = {
                                isUploading = true
                                scope.launch(Dispatchers.IO) {
                                    ClientApi.uploadFiles(filesToUpload,
                                        onFileUploadProgress = {
                                           fileUploadProgress = it
                                        },
                                        onFileUploaded = {
                                            numberOfFilesUploaded++
                                            if (numberOfFilesUploaded == filesToUpload.size){
                                                isUploading = false
                                                isFinishedUploading = true
                                                showUploadButton = false
                                            }
                                    })
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = AppColors.accent,
                            ),
                        ) {
                            Text(
                                text = "Upload",
                                style = TextStyle(
                                    color = AppColors.white,
                                ),
                            )
                        }
                    }

                }
            }

        }
    }
}