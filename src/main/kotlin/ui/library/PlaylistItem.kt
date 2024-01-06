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
import java.io.File
import Constants.AppColors
import Constants.ImageResources
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import ui.PlayerManager

@Composable
fun PlaylistItem(filePath: String,
                 index: Int,
                 onPlayPressed: () -> Unit,
                 onRemovePressed: () -> Unit
) {
    val file = File(filePath)
    val scope = rememberCoroutineScope()

    var isCurrentlyPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(scope){
        PlayerManager.currentPlayingIndex.collectLatest {
            isCurrentlyPlaying = index == it
        }
    }

    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .background(if (index % 2 == 0) Color.Transparent else AppColors.accentDark)
    ) {

        Box(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp).size(8.dp)){
            if (isCurrentlyPlaying){
                Icon(
                    painter = painterResource(ImageResources.audio),
                    contentDescription = null,
                    tint = Color.Transparent,
                    modifier = Modifier.fillMaxSize()
                        .clip(CircleShape)
                        .background(AppColors.accent)
                )
            }
        }


        Text(
            file.name,
            color = AppColors.white,
            maxLines = 1,
            style = TextStyle(fontSize = 14.sp),
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .wrapContentHeight()
                .padding(8.dp)
                .weight(1f)
        )

//        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.wrapContentWidth()) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = if (file.isDirectory) AppColors.accent else  AppColors.white,
                modifier = Modifier
                    .padding(8.dp)
                    .size(20.dp)
                    .clickable {
                        onPlayPressed()
                    }
            )

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = if (file.isDirectory) AppColors.accent else  AppColors.white,
                modifier = Modifier
                    .padding(8.dp)
                    .size(20.dp)
                    .clickable {
                        onRemovePressed()
                    }
            )
        }


    }
}