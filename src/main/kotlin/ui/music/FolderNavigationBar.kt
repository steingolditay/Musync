package ui.music

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import utils.PreferencesManager
import Constants.ImageResources
import Constants.AppColors

@Composable
fun FolderNavigationBar(currentPath: String, onBackNavigation: () -> Unit) {
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
                    if (currentPath != originPath) {
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