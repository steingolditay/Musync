package ui.main

import androidx.compose.desktop.ui.tooling.preview.Preview

import androidx.compose.runtime.*
import enums.NavigationTab
import ui.music.MusicTab

@Composable
@Preview
fun ContentPanel(selectedTab: NavigationTab) {
    when (selectedTab){
        NavigationTab.Music -> MusicTab()
        else -> {

        }
    }
}



