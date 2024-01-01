package ui.music

import Constants
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import utils.PreferencesManager
import Constants.AppColors
import java.io.File
import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import database.DatabaseService
import database.getFullPath
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ui.dialogs.SyncDialog
import ui.sync.EmptyState
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

    Row(
        modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()) {

        Column(
            verticalArrangement = if (currentDirectory.isNotBlank()) Arrangement.Top else Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AppColors.background)
                .weight(1f)

        ) {
            if (currentDirectory.isNotBlank()) {
                Column(modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppColors.background)){

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
        Column(modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .weight(1f)
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(AppColors.background)){

        }
    }


}