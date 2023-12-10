import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import enums.NavigationTab
import ui.ContentPanel
import ui.NavigationPanel
import ui.TitleBar
import java.util.prefs.Preferences

@Composable
@Preview
fun App() {
    var selectedTab by remember { mutableStateOf(NavigationTab.Music) }
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(bottomEnd = 8.dp, bottomStart = 8.dp)
        ) {

            Column (
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Row {
                    NavigationPanel(onTabSelected = {
                        selectedTab = it
                    })
                ContentPanel()
                }
            }
        }

    }
}



fun main() = application {
    val preferences = Preferences.userRoot().node("settings")
    preferences.put("Some key", "Some value")
    val s = preferences.get("Some key", "")

    Window(
        onCloseRequest = ::exitApplication,
        transparent = true,
        undecorated = true,
    ) {

        Column {
            WindowDraggableArea {
                TitleBar {
                     this@application.exitApplication()
                }
            }
            App()
        }



    }
}
