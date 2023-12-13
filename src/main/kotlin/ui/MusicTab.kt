package ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import utils.PreferencesManager
import Constants.AppColors
import androidx.compose.ui.unit.sp
import utils.FileUtils
import Constants.StringResources
import androidx.compose.material.Icon
import androidx.compose.ui.res.painterResource
import java.io.File
import Constants.ImageResources
import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.text.style.TextOverflow
import utils.FileUtils.isAudioFile
import kotlin.io.path.Path

@Composable
fun MusicTab() {
    var hasMusicDirectory by remember { mutableStateOf(PreferencesManager.hasMusicDir()) }
    var currentDirectory by remember { mutableStateOf("") }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if (hasMusicDirectory) Arrangement.Top else Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(AppColors.background)
            .padding(bottom = 16.dp)
    ) {

        if (hasMusicDirectory){
            if (currentDirectory.isBlank()){
                currentDirectory = PreferencesManager.getMusicDir()!!
            }

            NavigationBar(PreferencesManager.getMusicDir()!!, currentDirectory) {
                if (currentDirectory != PreferencesManager.getMusicDir()){
                   val backPath = currentDirectory.removeSuffix(Path(currentDirectory).last().toString()).removeSuffix("\\")
                    currentDirectory = backPath
                }
            }

            LazyColumn (
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                val directory = File(currentDirectory)
                directory.listFiles()?.filter { it.isDirectory || it.isAudioFile() }?.let { fileList ->
                    items(fileList.size){ it ->
                        val file = fileList[it]
                        FileItem(file){ dirPath ->
                            currentDirectory = dirPath
                        }
                    }
                }
            }

        } else {
            EmptyState {
                hasMusicDirectory = true
            }
        }
    }
}


@Composable
private fun NavigationBar(originPath: String, currentPath: String, onBackNavigation: () -> Unit){
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 4.dp)
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
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 2.dp)
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
            modifier = Modifier
                .wrapContentHeight()
                .wrapContentWidth()
                .padding(16.dp)

        )
    }
}

@Composable
private fun EmptyState(onDirectorySelected: () -> Unit){
    Text(StringResources.noDirectorySelected,
        modifier = Modifier
            .padding(8.dp),
        color = AppColors.white,
        fontSize = 12.sp
    )
    Button(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        colors = ButtonDefaults.buttonColors(AppColors.accent),
        onClick = {
            val musicFolderPath = FileUtils.pickFolder()
            musicFolderPath?.let {
                PreferencesManager.setMusicDir(it)
                onDirectorySelected.invoke()
            }
        }
    ){
        Text(StringResources.selectDirectory, color = AppColors.white)
    }
}
