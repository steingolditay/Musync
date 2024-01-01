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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

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
        Box(
            modifier = Modifier
                .size(36.dp)
                .wrapContentHeight()
                .padding(start = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .let clickable@{
                    if (currentPath != originPath) {
                        return@clickable it.clickable {
                            onBackNavigation.invoke()
                        }
                    }
                    it
                }


        ) {
            Icon(
                painter = painterResource(ImageResources.back),
                contentDescription = null,
                tint = if (currentPath != originPath) AppColors.white else AppColors.disabled,
                modifier = Modifier
                    .padding(8.dp)
            )
        }

        Text(
            currentPath,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = AppColors.white,
            style = TextStyle(fontSize = 14.sp),
            modifier = Modifier
                .wrapContentHeight()
                .wrapContentWidth()
                .padding(16.dp)

        )
    }
}