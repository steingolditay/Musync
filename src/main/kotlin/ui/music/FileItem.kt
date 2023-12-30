package ui.music

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import database.DatabaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import utils.FileUtils.isAudioFile
import java.io.File
import Constants.AppColors
import Constants.ImageResources

@Composable
fun FileItem(file: File,
             isSynced: Boolean,
             onDirectorySelected: (path: String) -> Unit,
             onSyncStateChanged: (state: Boolean) -> Unit,
             onInnerFilesSyncChanged: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var syncState by remember { mutableStateOf(false) }
    syncState = isSynced
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(bottom = 2.dp)
            .clickable {
                if (file.isDirectory) {
                    onDirectorySelected(file.absolutePath)
                }
            }
    ) {
        Icon(
            painter = painterResource(if (file.isDirectory) ImageResources.folder else ImageResources.audio),
            contentDescription = null,
            tint = AppColors.white,
            modifier = Modifier
                .size(48.dp)
                .padding(16.dp)
        )
        Text(
            file.name,
            color = AppColors.white,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .wrapContentHeight()
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        if (!file.isDirectory) {
            Icon(
                painter = painterResource(if (syncState) ImageResources.sync else ImageResources.dontSync),
                contentDescription = null,
                tint = if (syncState) AppColors.accent else AppColors.disabled,
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        syncState = !syncState
                        onSyncStateChanged(syncState)
                        scope.launch(Dispatchers.IO) {
                            DatabaseService.get(file)?.let { fileRecord ->
                                val updatedRecord = fileRecord.copy(sync = syncState)
                                DatabaseService.updateSync(updatedRecord)
                            }
                        }
                    }

            )
        } else {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = null,
                tint = AppColors.disabled,
                modifier = Modifier
                    .size(28.dp)
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        val nestedFiles = file.walk().filter { it.isAudioFile() }.toList()
                        if (nestedFiles.isEmpty()) {
                            return@clickable
                        }
                        scope.launch(Dispatchers.IO) {
                            nestedFiles.forEach {
                                DatabaseService.get(it)?.apply {
                                    sync = false
                                    DatabaseService.updateSync(this)
                                }
                            }
                            onInnerFilesSyncChanged()
                        }
                    }
            )

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = AppColors.disabled,
                modifier = Modifier
                    .size(28.dp)
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        val nestedFiles = file.walk().filter { it.isAudioFile() }.toList()
                        if (nestedFiles.isEmpty()) {
                            return@clickable
                        }
                        scope.launch(Dispatchers.IO) {
                            nestedFiles.forEach {
                                DatabaseService.get(it)?.apply {
                                    sync = true
                                    DatabaseService.updateSync(this)
                                }
                            }
                            onInnerFilesSyncChanged()
                        }
                    }
            )
        }
    }
}