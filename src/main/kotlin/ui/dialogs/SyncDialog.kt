package ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import network.ClientApi
import Constants.AppColors
import androidx.compose.desktop.ui.tooling.preview.Preview
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
import enums.SyncProgress
import ui.sync.SyncItem
import ui.SyncManager

@Composable
@Preview
fun SyncDialog(onDismiss: () -> Unit) {

    val scope = rememberCoroutineScope()

    DisposableEffect(scope) {
        var currentJob: Job?

        val serverSyncJob = scope.launch(start = CoroutineStart.LAZY) {
            val databaseRecords = DatabaseService.getAll().filter { it.sync }
            val response = ClientApi.syncFiles(databaseRecords)
            response?.let {
                if (it.isNotEmpty()) {
                    SyncManager.setNumberOfFilesToUpload(it.size)
                    SyncManager.setFilesToUpload(it.map { hash -> databaseRecords.first { record -> record.hash == hash} })
                    SyncManager.setProgressState(SyncProgress.READY)
                } else {
                    SyncManager.setProgressState(SyncProgress.COMPLETE)
                }
            }
        }

        val databaseSyncJob = scope.launch(start = CoroutineStart.LAZY) {
            DatabaseSyncHelper.sync(
                onSyncStarted = {
                    SyncManager.setProgressState(SyncProgress.INDEXING)
                    SyncManager.setNumberOfFilesToProcess(it)
                    println("Started Syncing $it Files")
                },
                onFileProcessed = {
                    SyncManager.setProgressState(SyncProgress.PROCESSING)
                    SyncManager.setNumberOfFilesProcessed(it)
                    println("Processed $it Files")
                },
                onFinalizing = {
                    SyncManager.setProgressState(SyncProgress.FINALIZING)

                },
                onCompletion = {
                    SyncManager.setProgressState(SyncProgress.FETCHING)
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


    Dialog(
        onDismiss,
        transparent = true,
        undecorated = true,
        resizable = false,
        state = DialogState(position = WindowPosition(Alignment.Center), size = DpSize(450.dp, 450.dp))
    ) {

        Surface(
            modifier = Modifier.clip(RoundedCornerShape(8.dp)).wrapContentWidth().wrapContentHeight().padding(),
            shape = RoundedCornerShape(8.dp),
            elevation = 8.dp,

            ) {
            Column {
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
                            .padding(4.dp)
                            .size(20.dp)
                            .clickable {
                                onDismiss.invoke()
                            }
                    )
                }
                SyncProgress.values().forEach {
                    SyncItem(it)
                }

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
                        tint = Color.Transparent,
                        modifier = Modifier
                            .padding(4.dp)
                            .size(20.dp)
                    )
                }
            }
        }
    }
}