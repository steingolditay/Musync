package ui.library

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.PreferencesManager
import Constants.AppColors
import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import enums.LibraryTitle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ui.sync.EmptyState
import ui.sync.FolderNavigationBar
import utils.FileUtils.isAudioFile
import ui.PlayerManager
import java.io.File

import kotlin.io.path.Path

@Composable
fun LibraryTab() {
    val scope = rememberCoroutineScope()
    var currentDirectory by remember { mutableStateOf("") }
    var playlist by remember { mutableStateOf(listOf<String>()) }

    PreferencesManager.getMusicDir()?.let {
        if (currentDirectory.isBlank() && it.isNotBlank()) {
            currentDirectory = it
        }
    }
    PlayerManager.scope = scope

    LaunchedEffect(scope){
        PlayerManager.playlist.collectLatest {
            playlist = it
        }
    }



    Row(
        modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()) {

        // Player
        Column(modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .weight(1.5f)
            .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(AppColors.background)){
            // Playlist
            LazyColumn(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxWidth()
                    .background(AppColors.background)
            ) {
                items(playlist.size) { index ->
                    val filePath = playlist[index]
                    PlaylistItem(filePath, index,
                        onPlayPressed = {
                            scope.launch(Dispatchers.IO) {
                                PlayerManager.playForIndex(index)
                            }
                        },
                        onRemovePressed = {
                            PlayerManager.removeFromPlaylist(index)
                        }
                    )

                }
            }
            // Controls
            PlayerControls()

        }

        // Library
        Column(
            verticalArrangement = if (currentDirectory.isNotBlank()) Arrangement.Top else Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AppColors.background)
                .weight(1f)

        ) {
            if (currentDirectory.isNotBlank()) {
                FolderNavigationBar(currentDirectory) {
                    if (currentDirectory != PreferencesManager.getMusicDir()) {
                        val backPath = currentDirectory.removeSuffix(Path(currentDirectory).last().toString()).removeSuffix("\\")
                        currentDirectory = backPath
                    }
                }

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
                            val itemModels = fileList.map { LibraryItemModel(it, null) }.toMutableList()
                            if (itemModels.isEmpty()){
                                return@let
                            }
                            if (!itemModels.first().file.isAudioFile()){
                                itemModels.add(0, LibraryItemModel(directory, LibraryTitle.Folders))
                            }
                            (itemModels.indexOfFirst { it.file.isAudioFile() }).let {
                                if (it != -1){
                                    itemModels.add(it, LibraryItemModel(directory, LibraryTitle.Tracks))
                                }
                            }

                            items(itemModels.size) {
                                LibraryItem(
                                    itemModels[it],
                                    onDirectorySelected = { dirPath ->
                                        currentDirectory = dirPath
                                    }
                                )
                            }
                        }
                }
            } else {
                EmptyState {
                    currentDirectory = it
                }
            }
        }
    }


}