package ui

import Utils
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import enums.NavigationTab

@Composable
@Preview
fun NavigationPanel(onTabSelected:(tab: NavigationTab) -> Unit) {
    var selectedTab by remember { mutableStateOf(NavigationTab.Music) }
    val navigationTabs by remember { mutableStateOf(NavigationTab.values()) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
        .fillMaxWidth(0.2f)
        .background(Utils.AppColors.colorBackground)
        .border(border = ButtonDefaults.outlinedBorder, shape = RoundedCornerShape(8.dp))
        .padding(vertical = 16.dp, horizontal = 8.dp)

    ) {
        // logo
        Image(
            painterResource("logo-white.png"),
            contentDescription = "logo",
            modifier = Modifier
                .fillMaxHeight(0.20f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxHeight()) {
            items(navigationTabs.size){
                val tab = navigationTabs[it]
                val isTabSelected = tab == selectedTab
                SidePanelTab(tab, isTabSelected) {
                    selectedTab = tab
                }
            }
        }

    }
}


@Composable
fun SidePanelTab(tab: NavigationTab, isSelected: Boolean, onTabSelected: (tab: NavigationTab) -> Unit){
    val buttonColor = if (isSelected) Utils.AppColors.colorButtonPressed else Utils.AppColors.colorBackground
    val colorAnimation = animateColorAsState(buttonColor, animationSpec = tween(500))
    Button(
        onClick = {
            onTabSelected(tab)
        },
        modifier = Modifier
            .fillMaxWidth()
        ,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colorAnimation.value,
            contentColor = colorAnimation.value,
        ),
        elevation = ButtonDefaults.elevation(0.dp)
    ) {
        Text(
            text = tab.toString(),
            style = TextStyle(
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier
                .padding(vertical = 16.dp)
        )
    }
}