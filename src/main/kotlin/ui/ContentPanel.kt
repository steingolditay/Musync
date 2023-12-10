package ui

import androidx.compose.desktop.ui.tooling.preview.Preview

import androidx.compose.runtime.*
import enums.NavigationTab

@Composable
@Preview
fun ContentPanel(selectedTab: NavigationTab) {
    when (selectedTab){
        NavigationTab.Music -> MusicTab()
        else -> {

        }
    }
}



