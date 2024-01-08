package ui.library

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.PreferencesManager
import Constants.AppColors
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ui.sync.EmptyState
import ui.sync.FolderNavigationBar
import ui.PlayerManager
import utils.FileUtils.getLibraryItemsForDirectory
import java.io.File

import kotlin.io.path.Path

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayTab() {
    val scope = rememberCoroutineScope()
    var currentDirectory by remember { mutableStateOf("") }
    var playlist by remember { mutableStateOf(listOf<String>()) }
    var libraryVisible by remember { mutableStateOf(true) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }


    val animatedLibraryWeight by animateFloatAsState(
        targetValue = if (libraryVisible) 1.0f + offsetX else 0.05f,
        label = "alpha"
    )

    PreferencesManager.getMusicDir()?.let {
        if (currentDirectory.isBlank() && it.isNotBlank()) {
            currentDirectory = it
        }
    }
    PlayerManager.scope = scope

    LaunchedEffect(scope) {
        PlayerManager.playlist.collectLatest {
            playlist = it
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .weight(1.5f)
                .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AppColors.background)
        ) {
            Column(modifier = Modifier.fillMaxHeight(0.5f + offsetY)) {
                // Playlist header
                PlaylistHeader()
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    AppColors.accentDark,
                                    Color.Transparent
                                )
                            )
                        )
                        .height(4.dp)
                        .pointerHoverIcon(PointerIcon.Hand)
                        .draggable(
                            orientation = Orientation.Vertical,
                            state = rememberDraggableState { delta ->
                                val actualValue = offsetY + delta / 500
                                val capped = when {
                                    actualValue < -0.3f -> -0.3f
                                    actualValue > 0.017f -> 0.017f
                                    else -> actualValue
                                }
                                println(actualValue)
                                offsetY = capped
                            }
                        )
                )
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
                .weight(animatedLibraryWeight)

        ) {
            if (currentDirectory.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(32.dp))
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        AppColors.accentDark,
                                        Color.Transparent
                                    )
                                )
                            )
                            .width(4.dp)
                            .pointerHoverIcon(PointerIcon.Hand)
                            .onClick(
                                matcher = PointerMatcher.mouse(PointerButton.Secondary),
                                onClick = {
                                    libraryVisible = !libraryVisible
                                })
                            .draggable(
                                orientation = Orientation.Horizontal,
                                state = rememberDraggableState { delta ->
                                    if (libraryVisible) {
                                        val actualValue = offsetX - delta / 500
                                        val capped = when {
                                            actualValue < -0.3f -> -0.3f
                                            actualValue > 1.2f -> 1.2f
                                            else -> actualValue
                                        }
                                        offsetX = capped
                                    }
                                }
                            )
                    )

                    AnimatedVisibility(libraryVisible) {
                        Column {
                            FolderNavigationBar(currentDirectory) {
                                if (currentDirectory != PreferencesManager.getMusicDir()) {
                                    val backPath =
                                        currentDirectory.removeSuffix(Path(currentDirectory).last().toString())
                                            .removeSuffix("\\")
                                    currentDirectory = backPath
                                }
                            }
                            Row {
                                Column(

                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxHeight().width(6.dp).padding(end = 2.dp)
                                ) {


                                }
                                LazyColumn(
                                    modifier = Modifier
                                        .weight(1.0f)
                                        .fillMaxWidth()
                                        .background(AppColors.background)
                                ) {
                                    val directory = File(currentDirectory)
                                    val directoryItems = directory.getLibraryItemsForDirectory()
                                    items(directoryItems.size) {
                                        LibraryItem(
                                            directoryItems[it],
                                            onDirectorySelected = { dirPath ->
                                                currentDirectory = dirPath
                                            }
                                        )
                                    }
                                }


                            }
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