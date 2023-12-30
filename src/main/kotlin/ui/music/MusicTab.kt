package ui.music

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import utils.PreferencesManager
import Constants.AppColors
import java.io.File
import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.ui.draw.clip
import database.DatabaseService
import database.getFullPath
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ui.dialogs.SyncDialog
import utils.FileUtils
import utils.FileUtils.getFullPath
import utils.FileUtils.isAudioFile
import utils.SyncManager
import kotlin.io.path.Path

@Composable
fun MusicTab() {
    val scope = rememberCoroutineScope()
    var currentDirectory by remember { mutableStateOf("") }
    var showSyncDialog by remember { mutableStateOf(false) }
    var syncedFiles by remember { mutableStateOf(listOf<String>()) }


    val syncJob = scope.launch(start = CoroutineStart.LAZY, context = Dispatchers.IO) {
        syncedFiles = DatabaseService.getAll().filter { it.sync }.map { it.getFullPath() }.toList()
    }

    LaunchedEffect(scope) {
        syncJob.start()
    }

    PreferencesManager.getMusicDir()?.let {
        if (currentDirectory.isBlank() && it.isNotBlank()) {
            currentDirectory = it
        }
    }
    if (showSyncDialog) {
        SyncDialog(
            onDismiss = {
                SyncManager.reset()
                showSyncDialog = false
            },
        )
    }

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = if (currentDirectory.isNotBlank()) Arrangement.Top else Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(AppColors.background)

    ) {
        if (currentDirectory.isNotBlank()) {
            FolderNavigationBar(currentDirectory) {
                if (currentDirectory != PreferencesManager.getMusicDir()) {
                    val backPath =
                        currentDirectory.removeSuffix(Path(currentDirectory).last().toString()).removeSuffix("\\")
                    currentDirectory = backPath
                }
            }

            if (syncedFiles.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1.0f)
                        .fillMaxWidth()
                        .background(AppColors.background)
                ) {
                    val directory = File(currentDirectory)
                    directory.listFiles()
                        ?.filter { it.isDirectory || it.isAudioFile() }
                        ?.sortedByDescending { it.isDirectory }
                        ?.let { fileList ->
                            items(fileList.size) { it ->
                                val file = fileList[it]
                                val fullPath = file.getFullPath()
                                val isSynced = syncedFiles.contains(fullPath)
                                FileItem(file, isSynced,
                                    onSyncStateChanged = {
                                        if (it) {
                                            syncedFiles += fullPath
                                        } else {
                                            syncedFiles -= fullPath
                                        }
                                    },
                                    onInnerFilesSyncChanged = {
                                        syncJob.start()
                                    },
                                    onDirectorySelected = { dirPath ->
                                        currentDirectory = dirPath

                                    })
                            }
                        }
                }

                Row {
                    Button(
                        modifier = Modifier
                            .wrapContentWidth()
                            .wrapContentHeight()
                            .padding(8.dp),
                        colors = ButtonDefaults.buttonColors(AppColors.accent),
                        onClick = {
                            val musicFolderPath = FileUtils.pickFolder()
                            musicFolderPath?.let {
                                scope.launch(Dispatchers.IO) {
                                    DatabaseService.drop().also {
                                        showSyncDialog = true
                                    }
                                    PreferencesManager.setMusicDir(it)
                                    currentDirectory = it
                                }

                            }
                        }
                    ) {
                        Text("Change Folder", color = AppColors.white)
                    }
                    Button(
                        modifier = Modifier
                            .wrapContentWidth()
                            .wrapContentHeight()
                            .padding(8.dp),
                        colors = ButtonDefaults.buttonColors(AppColors.accent),
                        onClick = {
                            showSyncDialog = true
                        }
                    ) {
                        Text("Sync", color = AppColors.white)
                    }
                }
            }


        } else {
            EmptyState {
                currentDirectory = it
                scope.launch(Dispatchers.IO) {
                    DatabaseService.drop().also {
                        showSyncDialog = true
                    }
                }
            }
        }
    }
}