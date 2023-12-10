package ui

import Constants
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import Constants.AppColors
import Constants.StringResources

@Composable
@Preview
fun TitleBar(onApplicationExit: () -> Unit) {
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

                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Exit",
                    tint = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp)
                        .clickable {
                            onApplicationExit()
                        }
                )

            }
        }
    }

}

