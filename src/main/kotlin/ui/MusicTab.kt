package ui

import androidx.compose.foundation.background
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
import utils.FolderPicker
import Constants.StringResources

@Composable
fun MusicTab() {
    var hasMusicDirectory by remember { mutableStateOf(PreferencesManager.hasMusicDir()) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(AppColors.background)
            .padding(vertical = 16.dp)
    ) {

        if (hasMusicDirectory){
            Text("Some Text", color = AppColors.white)

        } else {
            EmptyState {
                hasMusicDirectory = true
            }
        }
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
            val musicFolderPath = FolderPicker.pickFolder()
            musicFolderPath?.let {
                PreferencesManager.setMusicDir(it)
                onDirectorySelected.invoke()
            }
        }
    ){
        Text(StringResources.selectDirectory, color = AppColors.white)
    }
}
