package ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import network.ClientApi
import Constants.AppColors
import androidx.compose.foundation.border
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.*
import database.DatabaseService
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import database.DatabaseSyncHelper

@Composable
fun SyncDialog(onDismiss: () -> Unit) {

    val scope = rememberCoroutineScope()
    val surfaceWidth = 350.dp
    val surfaceHeight = 140.dp
    val shadowSize = 16.dp

    var numberOfFiles by remember { mutableStateOf(1) }
    var fileProcessed by remember { mutableStateOf(0) }

    var titleText by remember { mutableStateOf("") }
    var bodyText by remember { mutableStateOf("") }
    var syncWithServer by remember { mutableStateOf(false) }



    DisposableEffect(scope){
        var currentJob: Job? = null

        val serverSyncJob = scope.launch(start = CoroutineStart.LAZY) {
            val databaseRecords = DatabaseService.getAll()
            val response = ClientApi.syncFiles(databaseRecords)
        }

        val databaseSyncJob = scope.launch(start = CoroutineStart.LAZY){
            DatabaseSyncHelper.sync(
                onSyncStarted = {
                    numberOfFiles = it
                    titleText = "Updating the database"
                    println("Started Syncing $it Files")
                },
                onFileProcessed = {
                    fileProcessed = it
                    bodyText = "$fileProcessed / $numberOfFiles"
                    println("Processed $it Files")
                },
                onFinalizing = {
                    titleText = "Applying changes to database"
                    bodyText = "this might take a moment...\nPlease do not turn off."
                },
                onCompletion = {
                    titleText = "Connecting to server"
                    bodyText = "Syncing files..."
                    syncWithServer = true
                    currentJob = serverSyncJob
                    currentJob?.start()
                }
            )
        }
        currentJob = databaseSyncJob
        currentJob?.start()

        onDispose {
            currentJob?.cancel()
        }
    }


    Dialog(onDismiss,
        transparent = true,
        undecorated = true,
        resizable = false,
        state = DialogState(
            size = DpSize(surfaceWidth + shadowSize, surfaceHeight + shadowSize),
            position = WindowPosition(Alignment.Center)
        ),
    ){

        Surface(
            modifier = Modifier
                .size(surfaceWidth, surfaceHeight)
                .border(1.dp, Color(0x33000000), RoundedCornerShape(8.dp)),
            shape = RoundedCornerShape(8.dp),
            elevation = 8.dp,
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .background(AppColors.dialogBackground)
                    .padding(8.dp),

                ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit",
                        tint = AppColors.white,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clickable {
                                onDismiss.invoke()
                            }
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.fillMaxSize()
                )
                {
                    Text(titleText,
                        color = AppColors.white,
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(top = 8.dp),
                        textAlign = TextAlign.Center,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        fontSize = 18.sp
                    )
                    Text(
                        bodyText,
                        color = AppColors.white,
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(top = 24.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

        }
    }
}