package ui.library

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import Constants.AppColors
import Constants.ImageResources
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.TextStyle
import com.goxr3plus.streamplayer.enums.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ui.PlayerManager
import utils.PlayerUtils


@Composable
@Preview
fun PlayerControls() {
    val scope = rememberCoroutineScope()
    var isPlaying by remember { mutableStateOf(false) }
    var playlist: List<String> by remember { mutableStateOf(listOf()) }
    var currentPlayingIndex: Int? by remember { mutableStateOf(null) }
    var currentPlayingTime by remember { mutableStateOf(0L) }
    var currentPlayingDuration by remember { mutableStateOf(0L) }
    var currentPlayingProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(scope){
        PlayerManager.playlist.collectLatest {
            playlist = it
        }
    }

    LaunchedEffect(scope){
        PlayerManager.currentPlayingIndex.collectLatest {
            currentPlayingIndex = it
        }
    }

    LaunchedEffect(scope){
        PlayerManager.currentPlayingTime.collectLatest {
            currentPlayingTime = it ?: 0
        }
    }

    LaunchedEffect(scope){
        PlayerManager.currentPlayingDuration.collectLatest {
            currentPlayingDuration = it ?: 0
        }
    }

    LaunchedEffect(scope){
        PlayerManager.currentPlayingProgress.collectLatest {
            currentPlayingProgress = it ?: 0f
        }
    }

    LaunchedEffect(scope){
        PlayerManager.playerState.collectLatest {
            isPlaying = it == Status.PLAYING || it == Status.RESUMED
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().wrapContentWidth().background(AppColors.background),
        horizontalAlignment = Alignment.CenterHorizontally) {

        Spacer(modifier = Modifier
            .height(8.dp)
            .fillMaxWidth()
            .background(AppColors.surfaceColor)
        )

        Image(
            painter = painterResource(ImageResources.logo),
            contentDescription = "Cover Image",
            modifier = Modifier
                .clip(RoundedCornerShape(32.dp))
                .size(192.dp)
                .background(AppColors.background)
                .shadow(elevation = 4.dp)
                .padding(12.dp)

        )

        Slider(
            value = currentPlayingProgress,
            onValueChange = {
                currentPlayingProgress = it
            },

            colors = SliderDefaults.colors(thumbColor = AppColors.accent, activeTrackColor = AppColors.accent),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 64.dp)
                .padding(top = 16.dp, bottom = 16.dp)
                .height(5.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 64.dp)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Start) {
            Text(
                text = PlayerUtils.getTimeString(currentPlayingTime),
                modifier = Modifier.weight(1f),
                style = TextStyle(color = AppColors.white)
            )

            Text(
                text = PlayerUtils.getTimeString(currentPlayingDuration),
                modifier = Modifier.wrapContentWidth(),
                style = TextStyle(color = AppColors.white)

            )
        }

        Row (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = 32.dp)
        ){
            val isBackPreviousDisabled = currentPlayingIndex == 0 || playlist.isEmpty()
            val isNextDisabled = currentPlayingIndex?.let { it + 1 > playlist.size } ?: true
            Icon(
                painter = painterResource(ImageResources.previous),
                contentDescription = "Previous",
                tint = if (isBackPreviousDisabled) AppColors.disabled else AppColors.white,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .size(32.dp)
                    .clickable {
                        if (!isBackPreviousDisabled){
                            scope.launch(Dispatchers.IO) {
                                PlayerManager.playPrevious()
                            }
                        }
                    }
            )

            Icon(
                painter = painterResource(
                    when (isPlaying){
                        true -> ImageResources.pause
                        false -> ImageResources.play
                    }
                ),
                contentDescription = "Play / Pause",
                tint = AppColors.background,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AppColors.white)
                    .padding(12.dp)
                    .clickable {
                        when (isPlaying && playlist.isNotEmpty()){
                            true -> PlayerManager.pause()
                            false -> PlayerManager.resume()
                        }
                    }

            )
            Icon(

                painter = painterResource(ImageResources.next),
                contentDescription = "Next",
                tint = if (isNextDisabled) AppColors.disabled else AppColors.white,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .size(32.dp)
                    .clickable {
                        if (!isNextDisabled){
                            scope.launch(Dispatchers.IO) {
                                PlayerManager.playNext()
                            }
                        }
                    }
            )
        }

    }

}

