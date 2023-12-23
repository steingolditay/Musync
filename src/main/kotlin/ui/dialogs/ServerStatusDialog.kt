package ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import network.ClientApi
import network.ServerStatus
import Constants.AppColors
import androidx.compose.foundation.border
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ServerStatusDialog(serverStatus: ServerStatus, windowState: WindowState, onDismiss: () -> Unit) {

    val scope = rememberCoroutineScope()
    val surfaceWidth = 350.dp
    val surfaceHeight = 200.dp
    val shadowSize = 16.dp

    val x = windowState.position.x + windowState.size.width - surfaceWidth - 80.dp
    val y = windowState.position.y + 60.dp

    Dialog(onDismiss,
        transparent = true,
        undecorated = true,
        resizable = false,
        state = DialogState(
            size = DpSize(surfaceWidth + shadowSize, surfaceHeight + shadowSize),
            position = WindowPosition(x, y)
        ),


    ){
        Surface(
            modifier = Modifier
                .size(surfaceWidth, surfaceHeight)
                .border(1.dp, Color(0x33000000), RoundedCornerShape(8.dp)),
            shape = RoundedCornerShape(8.dp),
            elevation = 8.dp,
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .background(AppColors.dialogBackground)
                    .padding(8.dp),

                ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit",
                        tint = AppColors.white,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clickable {
                                onDismiss.invoke()
                            }
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.fillMaxSize()
                )
                {
                    Text("Connection Issue",
                        color = AppColors.white,
                        modifier = Modifier
                            .wrapContentWidth(),
                        textAlign = TextAlign.Center,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        fontSize = 18.sp
                    )
                    val bodyText = if (serverStatus == ServerStatus.OFFLINE)
                        Constants.StringResources.serverStatusOffline
                    else "${Constants.StringResources.serverStatusErrorCode} ${serverStatus.code}"
                    Text(
                        bodyText,
                        color = AppColors.white,
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(top = 24.dp),
                        textAlign = TextAlign.Center
                    )
                    Button(
                        modifier = Modifier
                            .wrapContentHeight()
                            .wrapContentWidth()
                            .padding(top = 32.dp),
                        onClick = {
                            scope.launch(Dispatchers.IO){
                                ClientApi.isServerAlive()
                            }

                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = AppColors.accent,
                        ),
                    ) {
                        Text(
                            text = "Retry",
                            style = TextStyle(
                                color = AppColors.white,
                            ),
                        )
                    }

                }
            }

        }
    }
}