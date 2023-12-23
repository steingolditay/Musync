package ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import utils.PreferencesManager
import Constants.AppColors
import androidx.compose.material.Icon
import androidx.compose.ui.res.painterResource
import java.io.File
import Constants.ImageResources
import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import database.DatabaseService
import database.FileRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import network.ClientApi
import utils.FileUtils
import utils.FileUtils.isAudioFile
import kotlin.io.path.Path

@Composable
fun MusicTab() {
    var currentDirectory by remember { mutableStateOf("") }
    var showSyncDialog by remember { mutableStateOf(false) }

    PreferencesManager.getMusicDir()?.let {
        if (currentDirectory.isBlank() && it.isNotBlank()) {
            currentDirectory = it
        }
    }
    if (showSyncDialog){
        SyncDialog(
           onDismiss = {
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


        if (currentDirectory.isNotBlank()){
            NavigationBar(currentDirectory) {
                if (currentDirectory != PreferencesManager.getMusicDir()){
                   val backPath = currentDirectory.removeSuffix(Path(currentDirectory).last().toString()).removeSuffix("\\")
                    currentDirectory = backPath
                }
            }

            LazyColumn (
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
                        items(fileList.size){ it ->
                        val file = fileList[it]
                        FileItem(file){ dirPath ->
                            currentDirectory = dirPath
                        }
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
                            ClientApi.scope?.launch(Dispatchers.IO){
                                DatabaseService.drop().also {
                                    showSyncDialog = true
                                }
                                PreferencesManager.setMusicDir(it)
                                currentDirectory = it
                            }

                        }
                    }
                ){
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
                ){
                    Text("Sync", color = AppColors.white)
                }
            }

        } else {
            EmptyState {
                currentDirectory = it
                ClientApi.scope?.launch(Dispatchers.IO) {
                    DatabaseService.drop().also {
                        showSyncDialog = true
                    }
                }
            }
        }
    }
}




@Composable
private fun NavigationBar(currentPath: String, onBackNavigation: () -> Unit){
    val originPath = PreferencesManager.getMusicDir()
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 4.dp)
    ) {
        Icon(
            painter = painterResource(ImageResources.back),
            contentDescription = null,
            tint = if (currentPath != originPath) AppColors.white else AppColors.disabled,
            modifier = Modifier
                .padding(16.dp)
                .size(12.dp)
                .let {
                    if (currentPath != originPath){
                        return@let it.clickable {
                            onBackNavigation.invoke()
                        }
                    }
                    it
                }
        )
        Text(
            currentPath,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = AppColors.white,
            modifier = Modifier
                .wrapContentHeight()
                .wrapContentWidth()
                .padding(16.dp)

        )
    }
}
@Composable
private fun FileItem(file: File, onDirectorySelected: (path: String) -> Unit){
    var fileRecord: FileRecord? by remember { mutableStateOf(null) }


    if (!file.isDirectory){
        ClientApi.scope?.launch(Dispatchers.IO) {
            fileRecord = DatabaseService.get(file)
        }
    }

    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(bottom = 2.dp)
            .clickable {
                if (file.isDirectory){
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

        if (!file.isDirectory){
            Icon(
                painter = painterResource(if (fileRecord?.sync == true) ImageResources.sync else ImageResources.dontSync),
                contentDescription = null,
                tint = if (fileRecord?.sync == true) AppColors.accent else AppColors.disabled,
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        fileRecord?.let {
                            val updatedRecord = it.copy(sync = !it.sync)
                            fileRecord = updatedRecord
                            ClientApi.scope?.launch(Dispatchers.IO) { DatabaseService.updateSync(updatedRecord) }
                        }
                    }

            )
        }
         else {
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
                        if (nestedFiles.isEmpty()){
                            return@clickable
                        }
                        ClientApi.scope?.launch(Dispatchers.IO) {
                            nestedFiles.forEach {
                                DatabaseService.get(it)?.apply {
                                    sync = false
                                    DatabaseService.updateSync(this)
                                }
                            }
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
                        file.walk().filter { it.isAudioFile() }.forEach {
                            ClientApi.scope?.launch(Dispatchers.IO) {
                                DatabaseService.get(it)?.apply {
                                    sync = true
                                    DatabaseService.updateSync(this)
                                }
                            }
                        }
                    }
            )
        }
    }


}


