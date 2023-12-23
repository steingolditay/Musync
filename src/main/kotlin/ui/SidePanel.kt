package ui

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import enums.NavigationTab
import Constants.AppColors
import Constants.ImageResources
import androidx.compose.ui.draw.clip

@Composable
@Preview
fun NavigationPanel(onTabSelected:(tab: NavigationTab) -> Unit) {
    var selectedTab by remember { mutableStateOf(NavigationTab.Music) }
    val navigationTabs by remember { mutableStateOf(NavigationTab.values()) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
        .fillMaxWidth(0.2f)
        .padding(vertical = 8.dp, horizontal = 8.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(AppColors.background)


    ) {
        // logo
        Image(
            painterResource(ImageResources.logo),
            contentDescription = "logo",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.20f)
                .background(AppColors.background)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
        ) {
            items(navigationTabs.size){ it ->
                val tab = navigationTabs[it]
                val isTabSelected = tab == selectedTab
                SidePanelTab(tab, isTabSelected) {
                    selectedTab = tab
                    onTabSelected(it)
                }
            }
        }

    }
}


@Composable
fun SidePanelTab(tab: NavigationTab, isSelected: Boolean, onTabSelected: (tab: NavigationTab) -> Unit){
    val buttonColor = if (isSelected) AppColors.accent else AppColors.background
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
                color = AppColors.white,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier
                .padding(vertical = 16.dp)
        )
    }
}