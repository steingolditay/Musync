package ui.music

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import utils.FileUtils
import utils.PreferencesManager
import Constants.StringResources
import Constants.AppColors
import androidx.compose.desktop.ui.tooling.preview.Preview

@Composable
@Preview
fun EmptyState(onDirectorySelected: (directory: String) -> Unit){
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
                onDirectorySelected.invoke(it)
            }
        }
    ){
        Text(StringResources.selectDirectory, color = AppColors.white)
    }
}