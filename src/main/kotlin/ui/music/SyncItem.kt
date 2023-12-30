package ui.music

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import Constants.AppColors
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import enums.SyncProgress
import enums.SyncRelativeState
import enums.relativeProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import utils.SyncManager
import utils.SyncUtils.getImageResourceForProgress


@Composable
@Preview
fun SyncItem(state: SyncProgress) {
    val scope = rememberCoroutineScope()

    var relativeProgress by remember { mutableStateOf(SyncRelativeState.UPCOMING) }
    var filesProcessed by remember { mutableStateOf(0) }
    var uploadProgress by remember { mutableStateOf(0f) }
    var filesUploaded by remember { mutableStateOf(0) }


    LaunchedEffect(scope){
        SyncManager.syncProgressState.collectLatest {
            relativeProgress = state.relativeProgress(it)
        }
    }

    LaunchedEffect(scope){
        SyncManager.uploadProgress.collectLatest {
            uploadProgress = it
        }
    }

    LaunchedEffect(scope){
        SyncManager.numberOfFilesUploaded.collectLatest {
            filesUploaded = it
        }
    }

    LaunchedEffect(scope){
        SyncManager.numberOfFilesProcessed.collectLatest {
            filesProcessed = it
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.background(AppColors.dialogBackground).padding(horizontal = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(bottom = if (state == SyncProgress.COMPLETE) 8.dp else 0.dp), verticalAlignment = Alignment.CenterVertically) {

            Column(modifier = Modifier.wrapContentWidth().wrapContentHeight(), verticalArrangement = Arrangement.Center) {
                Icon(
                    painter = painterResource(state.getImageResourceForProgress()),
                    contentDescription = "Icon",
                    tint = if (relativeProgress == SyncRelativeState.UPCOMING) AppColors.disabled else (if (state == SyncProgress.COMPLETE) AppColors.green else AppColors.white),
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.wrapContentHeight().fillMaxWidth()) {
                Text(
                    text = when (state){
                            SyncProgress.PROCESSING -> String.format(state.title, filesProcessed, SyncManager.numberOfFilesToProcess.value)
                            SyncProgress.UPLOADING -> String.format(state.title, filesUploaded, SyncManager.numberOfFilesToUpload.value)
                            else -> state.title
                    },
                    style = TextStyle(color = if (relativeProgress == SyncRelativeState.UPCOMING) AppColors.disabled else AppColors.white),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(16.dp)
                )
                if (state == SyncProgress.READY && relativeProgress == SyncRelativeState.ACTIVE){
                    Button(
                            modifier = Modifier
                                .wrapContentHeight()
                                .wrapContentWidth(),
                            onClick = {
                                SyncManager.setProgressState(SyncProgress.UPLOADING)
                                scope.launch(Dispatchers.IO) {
                                    SyncManager.uploadFiles()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent, contentColor = Color.Transparent),
                            elevation = ButtonDefaults.elevation(0.dp)
                        ) {
                            Text(
                                text = "Upload",
                                style = TextStyle(color = AppColors.accent)
                            )
                        }
                }
            }
        }
        if (state == SyncProgress.UPLOADING && relativeProgress == SyncRelativeState.ACTIVE){
            Row(modifier = Modifier.fillMaxWidth().wrapContentHeight(), verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = uploadProgress,
                    color = AppColors.accent,
                    backgroundColor = AppColors.background,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 44.dp, end = 44.dp, top = 4.dp, bottom = 8.dp)

                )
            }
        }

        if (state != SyncProgress.COMPLETE){
            Spacer(modifier = Modifier.height(0.5.dp).fillMaxWidth(0.8f).background(AppColors.disabled))
        }
    }



}