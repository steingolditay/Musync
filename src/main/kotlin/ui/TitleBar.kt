package ui

import Constants
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import Constants.AppColors
import Constants.StringResources
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import database.DatabaseService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import network.ClientApi
import network.ServerStatus

@Composable
@Preview
fun TitleBar(onApplicationExit: () -> Unit,
             onShowServerStatusDialog: () -> Unit,
             state: WindowState) {
    var serverStatus by remember { mutableStateOf(ServerStatus.UNKNOWN) }

    ClientApi.scope?.launch {
        ClientApi.serverStatus.collectLatest {
            serverStatus = it
        }
    }
    ClientApi.isServerAlive()

    Surface {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AppColors.background)

        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.5f)

            ) {
                Text(StringResources.appName,
                    modifier = Modifier.padding(8.dp),
                    color = Color.White)

            }
            Row (
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
            ) {

                if (serverStatus == ServerStatus.OFFLINE || serverStatus == ServerStatus.ERROR_CODE){
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "connection",
                        tint = AppColors.red,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clickable {
                                onShowServerStatusDialog.invoke()
                            }
                    )
                }

                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = AppColors.white,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = AppColors.white,
                    modifier = Modifier.padding(start = 8.dp, end = 16.dp)
                        .clickable {
                            ClientApi.scope?.launch {
                                DatabaseService.getAll().forEach { println(it) }
                            }
                        }


                )

                Spacer(modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(AppColors.white)
                )

                Icon(
                    painter = painterResource(Constants.ImageResources.minimize),
                    contentDescription = "Minimize",
                    tint = AppColors.white,
                    modifier = Modifier.padding(horizontal = 8.dp).size(24.dp)
                        .clickable {
                            state.isMinimized = true
                        }
                )

                Icon(
                    painter = painterResource(if (state.placement == WindowPlacement.Floating) Constants.ImageResources.maximize else Constants.ImageResources.floating),
                    contentDescription = "Maximize",
                    tint = AppColors.white,
                    modifier = Modifier.padding(horizontal = 8.dp).size(20.dp)
                        .clickable {
                          state.placement =  if (state.placement == WindowPlacement.Maximized) WindowPlacement.Floating else WindowPlacement.Maximized
                        }
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Exit",
                    tint = AppColors.white,
                    modifier = Modifier.padding(horizontal = 8.dp)
                        .clickable {
                            onApplicationExit()
                        }
                )

            }
        }
    }

}

