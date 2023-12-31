import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import enums.NavigationTab
import network.ClientApi
import ui.main.ContentPanel
import ui.main.NavigationPanel
import ui.main.TitleBar
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import network.isError
import ui.dialogs.ServerStatusDialog
import java.awt.Dimension

@Composable
@Preview
fun App() {
    //.\gradlew :createDistributable
    var selectedTab by remember { mutableStateOf(NavigationTab.Play) }

    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start

    ) {
        Row {
            NavigationPanel(onTabSelected = {
                selectedTab = it
            })
            ContentPanel(selectedTab)
        }
    }
}


fun main() = application {
    val windowState = rememberWindowState(position = WindowPosition(Alignment.Center), size = DpSize(1280.dp, 768.dp))
    var showServerStatusDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    scope.launch {
        ClientApi.serverStatus.collectLatest { serverStatus ->
            if (!serverStatus.isError() && showServerStatusDialog){
                showServerStatusDialog = false
            }
        }
    }

    Window(

        onCloseRequest = ::exitApplication,
        transparent = true,
        undecorated = true,
        title = Constants.StringResources.appName,
        icon = painterResource(Constants.ImageResources.logo),
        state = windowState,
        ) {
    window.minimumSize = Dimension(1280, 768)
        MaterialTheme(darkColors(surface = Constants.AppColors.surfaceColor)) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(8.dp)

            ){
                Column {
                    WindowDraggableArea {
                        TitleBar(
                            onApplicationExit = {
                                this@application.exitApplication()
                            },
                            onShowServerStatusDialog = {
                                showServerStatusDialog = true
                            },
                            state = windowState
                        )
                    }
                    App()
                }
                if (showServerStatusDialog){
                    ServerStatusDialog(
                        ClientApi.serverStatus.value,
                        windowState,
                        onDismiss = {
                            showServerStatusDialog = false
                        })
                }
            }
        }
    }
}


