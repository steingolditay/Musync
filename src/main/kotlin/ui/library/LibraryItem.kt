package ui.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import Constants.AppColors
import Constants.ImageResources
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import enums.LibraryTitle
import models.LibraryItemModel
import ui.PlayerManager
import utils.FileUtils.getFullPath
import utils.FileUtils.isAudioFile

@Composable
fun LibraryItem(model: LibraryItemModel,
                onDirectorySelected: (path: String) -> Unit,
) {
    val title = model.title
    val file = model.file
    val isTitleItem = title != null


    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(bottom = 2.dp)
            .clickable {
                if (!isTitleItem && file.isDirectory){
                    onDirectorySelected(file.absolutePath)
                }
            }
    ) {
        if (!isTitleItem){
            Icon(
                painter = painterResource(if (file.isDirectory) ImageResources.folder else ImageResources.audio),
                contentDescription = null,
                tint = AppColors.white,
                modifier = Modifier
                    .size(48.dp)
                    .padding(16.dp)
            )
        }

        Text(
            if (isTitleItem) title.toString() else file.name,
            color = if (isTitleItem)  AppColors.accent else  AppColors.white,
            maxLines = 1,
            style = TextStyle(
                fontSize = if (isTitleItem) 18.sp else 14.sp,
                fontWeight = if (isTitleItem) FontWeight.Bold else FontWeight.Normal,
            ),
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .wrapContentHeight()
                .padding(8.dp)
                .weight(1f)
        )


        if ((title == LibraryTitle.Tracks ) ||
            file.isAudioFile() ||
            (!isTitleItem && file.isDirectory && !file.listFiles()?.filter { it.isAudioFile() }.isNullOrEmpty())) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = if (isTitleItem) AppColors.accent else  AppColors.white,
                modifier = Modifier
                    .padding(8.dp)
                    .size(20.dp)
                    .clickable {
                        if (!file.isDirectory){
                            PlayerManager.playSingleFile(file)
                        } else{
                            PlayerManager.playDirectory(file)
                        }
                    }
            )

            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "Add",
                tint = if (isTitleItem) AppColors.accent else  AppColors.white,
                modifier = Modifier
                    .padding(8.dp)
                    .size(20.dp)
                    .clickable {
                        if (!file.isDirectory){
                            PlayerManager.addToPlaylist(file.getFullPath())
                        } else {
                            file.listFiles()?.filter { it.isAudioFile() }?.forEach {
                                PlayerManager.addToPlaylist(it.getFullPath())
                            }
                        }
                    }
            )
        }
    }
}