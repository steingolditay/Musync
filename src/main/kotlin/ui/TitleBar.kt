package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import network.ClientApi
import network.ServerStatus

@Composable
@Preview
fun TitleBar(onApplicationExit: () -> Unit,
             onShowServerStatusDialog: () -> Unit) {
    var serverStatus by remember { mutableStateOf(ServerStatus.UNKNOWN) }

    ClientApi.scope?.launch {
        ClientApi.serverStatus.collectLatest {
            serverStatus = it
        }
    }
    ClientApi.isServerAlive()

    Surface(
        modifier = Modifier
            .border(border = ButtonDefaults.outlinedBorder, shape = RoundedCornerShape(8.dp))

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
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
                modifier = Modifier.fillMaxWidth()
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
                    modifier = Modifier.padding(horizontal = 8.dp)
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

