package ui.main

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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun NavigationPanel(onTabSelected: (tab: NavigationTab) -> Unit) {
    var selectedTab by remember { mutableStateOf(NavigationTab.Library) }
    val navigationTabs by remember { mutableStateOf(NavigationTab.values()) }
    var panelVisible by remember { mutableStateOf(true) }

    var offsetX by remember { mutableStateOf(0f) }
    val animatedPanelWeight by animateFloatAsState(
        targetValue = if (panelVisible) 0f + offsetX else -0.125f,
        label = "alpha"
    )

    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxWidth((0.025f).coerceAtLeast(0.15f + animatedPanelWeight))
            .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(AppColors.background),
    ) {
        AnimatedVisibility(panelVisible, modifier = Modifier.weight(1f)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Image(
                    painterResource(ImageResources.logo),
                    contentDescription = "logo",
                    modifier = Modifier
                        .size(128.dp)
                        .background(AppColors.background)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                ) {
                    items(navigationTabs.size) { it ->
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

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(32.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            AppColors.accentDark,
                            Color.Transparent
                        )
                    )
                )
                .width(4.dp)
                .pointerHoverIcon(PointerIcon.Hand)
                .onClick(
                    matcher = PointerMatcher.mouse(PointerButton.Secondary),
                    onClick = {
                        panelVisible = !panelVisible
                    })
                .draggable(

                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        if (panelVisible){
                            val actualValue = offsetX + delta / 1000
                            val capped = when {
                                actualValue > 0.05f -> 0.05f
                                actualValue < -0.05f -> -0.05f
                                else -> actualValue
                            }
                            offsetX = capped
                        }

                    }
                )

        )

    }

}


@Composable
fun SidePanelTab(tab: NavigationTab, isSelected: Boolean, onTabSelected: (tab: NavigationTab) -> Unit) {
    val buttonColor = if (isSelected) AppColors.accent else AppColors.background
    val colorAnimation = animateColorAsState(buttonColor, animationSpec = tween(500))
    Button(
        onClick = {
            onTabSelected(tab)
        },
        modifier = Modifier
            .fillMaxWidth(),
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