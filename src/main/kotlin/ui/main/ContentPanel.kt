package ui.main

import androidx.compose.desktop.ui.tooling.preview.Preview

import androidx.compose.runtime.*
import enums.NavigationTab
import ui.library.LibraryTab
import ui.sync.SyncTab

@Composable
@Preview
fun ContentPanel(selectedTab: NavigationTab) {
    when (selectedTab){
        NavigationTab.Library -> LibraryTab()
        NavigationTab.Sync -> SyncTab()
    }
}



