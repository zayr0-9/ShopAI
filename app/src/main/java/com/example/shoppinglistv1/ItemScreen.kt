package com.example.shoppinglistv1

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.shoppinglistv1.data.NotesEvent
import com.example.shoppinglistv1.data.NotesState
import com.example.shoppinglistv1.data.NotesViewModel
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreen(viewModel: NotesViewModel, navController: NavController, onEvent: (NotesEvent) -> Unit, state: NotesState) {
    val itemList by viewModel.everyItem.collectAsState(emptyList())
    val itemWithDate by viewModel.itemsWithDate.collectAsState(emptyList())
    val uItemList = itemList.distinctBy{it.name}
    val openDialog = remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Manage Items", fontSize = 32.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                })
        }
    ) {


        Column(modifier = Modifier
            .fillMaxSize()
            .padding(it)) {
        LazyColumn(modifier = Modifier
            .weight(0.9f)
            .padding(6.dp), content = {
            items(uItemList.count()) {index->
                val currentItem = uItemList[index]
                Row(modifier = Modifier.padding(6.dp)) {
                    Text(
                        text = currentItem.name,
                        modifier = Modifier
                            .weight(0.8f)
                            .clickable {
                                viewModel.getItemWithDate(currentItem.name)
                                openDialog.value = true
                            },
                        fontSize = 26.sp
                    )
                    IconButton(onClick = { onEvent(NotesEvent.DeleteItem(itemList[index])) }) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "Delete Notes",
                            modifier = Modifier
                                .size(26.dp)
                                .weight(0.2f),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                }
            })
        }

//        if(itemWithDate.isNotEmpty()){openDialog.value= true } else {openDialog.value= false}

        if(openDialog.value) {
            Dialog(onDismissRequest = { openDialog.value = false },
                properties = DialogProperties(usePlatformDefaultWidth = false),
                content = {
                    Column{
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.8f).padding(horizontal = 10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column {
                                if (itemWithDate.isNotEmpty()) {
                                    Text(
                                        text = itemWithDate[0].name,
                                        fontSize = 30.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp)
                                    )
                                }
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                                    colors = cardColors(containerColor= MaterialTheme.colorScheme.secondaryContainer.copy(0.5f))  ,
                                    content = {
                                        Row(modifier = Modifier.fillMaxWidth()) {

                                            Text(
                                                text = "Name",
                                                modifier = Modifier.padding(
                                                    top = 4.dp,
                                                    bottom = 4.dp,
                                                    start = 50.dp,
                                                    end = 10.dp
                                                ),
                                                fontSize = 24.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "Price",
                                                modifier = Modifier.padding(
                                                    top = 4.dp,
                                                    bottom = 4.dp,
                                                    start = 25.dp,
                                                    end = 10.dp
                                                ),
                                                fontSize = 24.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "Quantity",
                                                modifier = Modifier.padding(
                                                    top = 4.dp,
                                                    bottom = 4.dp,
                                                    start = 45.dp,
                                                    end = 10.dp
                                                ),
                                                fontSize = 24.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    })
                                Box(
                                    modifier = Modifier.weight(0.7f),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    LazyColumn(content = {
                                        items(itemWithDate.count()) { itemIndex ->
                                            Row(modifier = Modifier.fillMaxWidth()) {
                                                val currentTempItem = itemWithDate[itemIndex]
                                                Text(
                                                    text = "$itemIndex. ",
                                                    modifier = Modifier
                                                        .padding(4.dp)
                                                        .weight(0.3f),
                                                    fontSize = 20.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = currentTempItem.name,
                                                    modifier = Modifier
                                                        .padding(4.dp)
                                                        .width(100.dp)
                                                        .weight(1f),
                                                    fontSize = 20.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = currentTempItem.price.toString(),
                                                    modifier = Modifier
                                                        .padding(4.dp)
                                                        .weight(1f),
                                                    fontSize = 20.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
//                                            val date = Date(currentTempItem.date)
                                                val sdf =
                                                    convertLongToDateString(currentTempItem.date)
                                                Text(
                                                    text = sdf,
                                                    modifier = Modifier.padding(horizontal = 20.dp),
                                                    fontSize = 20.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }

                                        }
                                    })
                                }

                            }
                        }
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 15.dp),
                            contentAlignment = Alignment.Center) {
                            FilledTonalButton(onClick = {
                                openDialog.value = false
                            }) {
                                Text(text = "Dismiss")
                            }
                        }
                    }

                }
            )
        }
        }
    }

