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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.mpatric.mp3agic.Mp3File
import kotlinx.coroutines.flow.collectLatest
import ui.PlayerManager
import utils.FileUtils.getFullPath
import utils.PlayerUtils


@Composable
fun PlaylistItem(filePath: String,
                 index: Int,
                 onPlayPressed: () -> Unit,
                 onRemovePressed: () -> Unit
) {

    val scope = rememberCoroutineScope()
    var isCurrentlyPlaying by remember { mutableStateOf(false) }

    val file = File(filePath)
    val mp3File = Mp3File(file.getFullPath())
    val title = mp3File.id3v1Tag?.title ?: mp3File.id3v2Tag?.title ?: file.name
    val album = mp3File.id3v1Tag?.album ?: mp3File.id3v2Tag?.album ?: ""
    val artist = mp3File.id3v1Tag?.artist ?: mp3File.id3v2Tag?.artist ?: ""
    val duration = mp3File.lengthInSeconds


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
            .background(if (index % 2 == 0) Color.Transparent else AppColors.accentDarker)
            .padding(vertical = 8.dp)
    ) {

        Box(modifier = Modifier.width(32.dp).size(8.dp), contentAlignment = Alignment.Center){
            if (isCurrentlyPlaying){
                Icon(
                    painter = painterResource(ImageResources.audio),
                    contentDescription = null,
                    tint = Color.Transparent,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(AppColors.accent)
                )
            }
        }


        Text(
            title,
            color = AppColors.white,
            maxLines = 1,
            style = TextStyle(fontSize = 12.sp),
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .wrapContentHeight()
                .weight(1f)
        )

        Text(
            album,
            color = AppColors.white,
            maxLines = 1,
            style = TextStyle(fontSize = 12.sp),
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .wrapContentHeight()
                .weight(1f)
        )

        Text(
            artist,
            color = AppColors.white,
            maxLines = 1,
            style = TextStyle(fontSize = 12.sp),
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .wrapContentHeight()
                .weight(1f)
        )

        Text(
            PlayerUtils.getTimeString(duration),
            color = AppColors.white,
            maxLines = 1,
            style = TextStyle(fontSize = 12.sp),
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .wrapContentHeight()
                .weight(0.5f)
        )


        Row(
            modifier = Modifier.width(48.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = AppColors.white,
                modifier = Modifier
                    .size(16.dp)
//                    .padding(8.dp)
                    .clickable {
                        onPlayPressed()
                    }
            )

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = AppColors.white,
                modifier = Modifier
                    .size(16.dp)
//                    .padding(4.dp)
                    .clickable {
                        onRemovePressed()
                    }
            )
        }


    }
}