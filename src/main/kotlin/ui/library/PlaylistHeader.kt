package ui.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.io.File
import Constants.AppColors
import Constants.ImageResources
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import ui.PlayerManager

@Composable
fun PlaylistHeader() {

    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .background(AppColors.accentDark)
            .padding(start = 32.dp, end = 48.dp, top = 2.dp, bottom = 2.dp)

    ) {

        Text(
            "Name",
            color = AppColors.white,
            maxLines = 1,
            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .wrapContentHeight()
                .weight(1f)
        )

        Text(
            "Album",
            color = AppColors.white,
            maxLines = 1,
            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .wrapContentHeight()
                .weight(1f)
        )

        Text(
            "Artist",
            color = AppColors.white,
            maxLines = 1,
            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .wrapContentHeight()
                .weight(1f)
        )

        Text(
            "Duration",
            color = AppColors.white,
            maxLines = 1,
            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .wrapContentHeight()
                .weight(0.5f)
        )




    }
}