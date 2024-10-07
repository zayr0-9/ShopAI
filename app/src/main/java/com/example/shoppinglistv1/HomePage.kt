package com.example.shoppinglistv1

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.ModalDrawer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberDrawerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoppinglistv1.data.Item
import com.example.shoppinglistv1.data.NotesEvent
import com.example.shoppinglistv1.data.NotesState
import com.example.shoppinglistv1.data.NotesViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


//@Preview
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun homeScreen(
    state: NotesState,
    navController: NavController,
    onEvent : (NotesEvent) -> Unit,
    viewModel : NotesViewModel
) {

    val scaffoldState = rememberBottomSheetScaffoldState()
    val today = LocalDate.now()
    // Format the date as you like
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm")
    val dateToSave = System.currentTimeMillis()
    val datePlaceholder = convertLongToDateString(dateToSave)
    val date = Date(dateToSave)
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val day = sdf.format(date)
    val coroutineScope = rememberCoroutineScope()
    var showSearch = remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalDrawer(
        drawerState = drawerState,
         drawerShape = RoundedCornerShape(50f) ,
        drawerBackgroundColor = MaterialTheme.colorScheme.surface ,
        drawerContent = { drawerContent(navController)}) {
    //Lazy column here
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        backgroundColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Title(showSearch, viewModel, navController) },
                navigationIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                    }},
                actions = {
                    IconButton(onClick = { showSearch.value = !showSearch.value }) {
                        Crossfade(targetState = showSearch.value) { showSearch ->
                            if (showSearch) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = "Close Search", // "Search",
                                    modifier = Modifier.size(30.dp)
                                )
                            } else {
                                Icon(
                                    Icons.Filled.Search,
                                    contentDescription = "Search",
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }
                }
            )
        },
        sheetPeekHeight = 75.dp,
        floatingActionButton = {
            FloatingActionButton(
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                onClick = {
                    if (scaffoldState.bottomSheetState.isCollapsed) {
                        coroutineScope.launch { scaffoldState.bottomSheetState.expand() }
                    } else {
                        coroutineScope.launch { scaffoldState.bottomSheetState.collapse() }
                    }
                }
            ) {
                Crossfade(targetState = scaffoldState.bottomSheetState.isCollapsed) { isCollapsed ->
                    if (isCollapsed) {
                        Icon(Icons.Filled.Add, contentDescription = "Expand")
                    } else {
                        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Collapse")
                    }
                }
            }
        },
        sheetBackgroundColor = MaterialTheme.colorScheme.background,
        sheetContent = {
            val boxPadding = 4.dp
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(boxPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "New Note", fontSize = 36.sp, modifier = Modifier.padding(8.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(boxPadding)
//                .fillMaxHeight(),
                    .height(175.dp), //remove to let content decide height
                contentAlignment = Alignment.Center
            ) {
//            Text(text = "Bottom Sheet", fontSize = 60.sp)
                SuggestionRow(state, viewModel)
            }
            Divider(
                color = Color.LightGray,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp)
            )
            Box(
                modifier = Modifier
                    .height(300.dp)
                    .padding(boxPadding)
            ) {
                val itemsToAdd by viewModel.itemsToAdd.collectAsState()
                LazyColumn(modifier = Modifier) {
                    items(itemsToAdd.size) {
                        Text(
                            text = itemsToAdd[it].name, fontSize = 20.sp, modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .padding(horizontal = 6.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    TextField(
                        value = state.iName.value,
                        onValueChange = { state.iName.value = it },
                        keyboardOptions = (
                                KeyboardOptions.Default.copy(
                                    capitalization = KeyboardCapitalization.Sentences)
                                ),
                        modifier = Modifier
                            .weight(3f)
                            .padding(2.dp),
                        shape = RoundedCornerShape(percent = 50),
                        colors = TextFieldDefaults.textFieldColors(
                            // Assuming you want a transparent background
                            unfocusedIndicatorColor = Color.Transparent, // Underline color when the TextField is not focused
                            focusedIndicatorColor = Color.Transparent, // Underline color when the TextField is focused
                            cursorColor = Color.Black,
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        ),
                        placeholder = { Text(text = "Enter Item") },
                        singleLine = true,
                        keyboardActions = KeyboardActions(onDone = {if (state.iName.value.isNotBlank()) {
                            viewModel.addItem(
                                Item(
                                    id = viewModel.lastNoteId.value,
                                    name = state.iName.value.trim(),
                                    author = "",
                                    quantity = 0,
                                    purchased = false,
                                    parent = viewModel.lastNoteId.value,
                                    price= 0.0
                                )
                            )
                            state.iName.value = ""
                        }})
                    )
                    IconButton(
                        modifier = Modifier
                            .size(60.dp)
                            .align(Alignment.CenterVertically),
                        onClick = {
                            if (state.iName.value.isNotBlank()) {
                                viewModel.addItem(
                                    Item(
                                        id = viewModel.lastNoteId.value,
                                        name = state.iName.value.trim(),
                                        author = "",
                                        quantity = 0,
                                        purchased = false,
                                        parent = viewModel.lastNoteId.value,
                                        price = 0.0
                                    )
                                )
                            }
                            //reset the field
                            state.iName.value = ""
                        }) {

                        Icon(
                            imageVector = Icons.Filled.AddCircle,
                            contentDescription = "Add",
                            modifier = Modifier.fillMaxSize(),
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }) {
        Column(modifier = Modifier) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "History",
                        fontSize = 32.sp,
                        modifier = Modifier
                            .weight(0.8f)
                            .padding(start = 20.dp, top = 12.dp, bottom = 12.dp)
                    )
                    IconButton(onClick = { onEvent(NotesEvent.SortNotes) }) {
                        Icon(
                            imageVector = Icons.Filled.List,
                            contentDescription = "Sort Notes",
                            modifier = Modifier
                                .size(35.dp)
                                .weight(0.2f),
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
//                    .height(300.dp)
                    .fillMaxHeight()
                    .padding(bottom = 75.dp),
                contentAlignment = Alignment.Center,
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
//                        .padding(2.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                )
                {
                    items(state.notes.size) { index ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // Handle click event, navigate to a detail screen or perform another action
                                    navController.navigate("NotesScreen/${state.notes[index].id}")
                                }
                                .padding(2.dp)
                        )
                        {
                            Card(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp, vertical = 6.dp)
                                    .fillMaxSize(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                                        alpha = 0.5f
                                    )
                                )
                            ) {
                                noteItem(state = state, index = index, onEvent = onEvent)
                            }
                        }
                    }
                }
            }
        }

        //when bottom sheet = not collapsed, create new note, when collapsed, add new note
        //to database if itemsToAdd != null
        //when bottomSheet is collapsed, either it was never opened, in that case list empty
        //do nothing, case 2, user opened the bottomSheet then collapsed it, if they added
        //items to the list, add them to a new note
        LaunchedEffect(key1 = scaffoldState.bottomSheetState.currentValue) {
            if (scaffoldState.bottomSheetState.isCollapsed) {
                onEvent(NotesEvent.QuickSaveNote("Untitled", "", 0L, dateToSave))
            }
        }
    }
}

}

@Composable
fun drawerContent(navController: NavController) {
    Column (modifier = Modifier
        .fillMaxSize()
        .padding((6.dp))
        ){
        Card (
            modifier = Modifier
                .padding(vertical = 10.dp)
//                .fillMaxWidth()
                .clickable { navController.navigate("Settings") },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(0.5f))
        )
        {
            Text(text = "Settings", modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp), fontSize = 24.sp)
        }
        Card (
            modifier = Modifier
                .padding(vertical = 10.dp)
//                .fillMaxWidth()
                .clickable { navController.navigate("Items") },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(0.5f))
        )
        {
            Text(text = "Manage Items", modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp), fontSize = 24.sp)
        }
    }

    

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Title(showSearch: MutableState<Boolean>, viewModel: NotesViewModel, navController: NavController) {

    var searchText by remember { mutableStateOf("") }
    val animationSpec = tween<Float>(durationMillis = 300, easing = LinearOutSlowInEasing)
    val outAnimationSpec = tween<Float>(durationMillis = 500, easing = LinearOutSlowInEasing)
    var expanded by remember { mutableStateOf(false) }
    var searchList by remember { mutableStateOf(false) }
    val searchResult by viewModel.searchNotes(searchText).collectAsState(initial = emptyList())

    AnimatedVisibility(
        visible = showSearch.value,
        enter = fadeIn(animationSpec) + scaleIn(animationSpec = tween(durationMillis = 300),
            transformOrigin = TransformOrigin.Center),
        exit = fadeOut(animationSpec)   // Animation for exiting the title
    ) {
        if (showSearch.value) {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(80),
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(0.9f),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search, capitalization = KeyboardCapitalization.Sentences),
                    keyboardActions = KeyboardActions(onSearch = { // Hide the keyboard when search is performed
//                        showSearch.value = false // Optionally close search after search
//                    searchText = ""
                        searchList = true
                        expanded = true
                        viewModel.searchNotes(searchText)
                    }),
                    placeholder = { Text("Search...") },
                    singleLine = true
                )
//                if (searchList) {
            Box( modifier = Modifier
                .fillMaxWidth(0.5f)
                ){
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        offset =  DpOffset(0.dp,70.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
//                            .padding(top = 70.dp)
                            .height(200.dp)
                    ) {
                        searchResult.forEach {note->
                            DropdownMenuItem(
                                text = {
                                    Card (modifier = Modifier.padding(2.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)){
                                        Text(
                                            text = note.title,
                                            fontSize = 24.sp,
                                            modifier = Modifier.padding(8.dp))
                                    }
                                       },
                                onClick = { navController.navigate("NotesScreen/${note.id}") })
                        }
                    }
                }
        }
    }
    AnimatedVisibility(
        visible = !showSearch.value,
        enter = fadeIn(outAnimationSpec), // Animation for entering the title
        exit = fadeOut(outAnimationSpec)  // Animation for exiting the title
    ){
        if (!showSearch.value){
            Text(text = "My Grocery", fontSize = 38.sp, modifier = Modifier.padding(20.dp))
        }}
}

fun convertLongToDateString(dateInMilliseconds: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
    return formatter.format(Date(dateInMilliseconds))
}

@Composable
fun noteItem(
    state: NotesState,
    index : Int,
    onEvent: (NotesEvent) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
            Text(
                text = index.toString() + ".",
                fontSize = 24.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Column(modifier = Modifier.weight(0.8f)) {
                var newTitle = ""
                val note = state.notes[index]
                val title = note.title
                val noteDate = note.date
                val date = Date(noteDate)
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                val hour = sdf.format(date)
                if (title == "Untitled") {
                    newTitle = title //+ " " + index
                } else {
                    newTitle = title
                }

                Row(){
                    Text(
                        text = newTitle,
                        fontSize = 24.sp,
                    )
                    Text(text = hour, modifier = Modifier.padding(4.dp))
                }
                Spacer(modifier = Modifier.height((8.dp)))
                Text(
                    text = convertLongToDateString(noteDate),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                )

            }
            IconButton(onClick = { onEvent(NotesEvent.DeleteNote(state.notes[index])) }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete Notes",
                    modifier = Modifier
                        .size(35.dp)
                        .weight(0.2f),
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.primary
                )
            }

        }

}



@Composable
fun SuggestionRow(state: NotesState, viewModel : NotesViewModel) {

    val allItems by viewModel.uniqueItems.collectAsState(initial = emptyList())
    if (allItems.isEmpty()) {
        Text("No items / loading items")
    } else {
        val chunkSize = (allItems.size + 2) / 3
        val chunks = allItems.chunked(chunkSize)
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            chunks.forEach { chunk ->
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp), // Space between items within a row
                    contentPadding = PaddingValues(horizontal = 6.dp) // Padding on the sides of the LazyRow
                ) {
                    items(chunk) { item ->
                        SuggestionItemRow(
                            text = item.name,
                            itemPadding = 2,
                            viewModel = viewModel,
                            modifier = Modifier
                            // No need for Modifier.horizontalScroll, LazyRow handles scrolling
                        )
                        // Adjust SuggestionItemRow as needed
                    }
                }
            }
        }
    }
}
//}


@Composable
fun SuggestionItemRow( text: String, itemPadding:Int, modifier: Modifier, viewModel : NotesViewModel) {
//    Row  {
//        text.forEach {
            Box (modifier = modifier
                .width(120.dp)
                .height(60.dp)
                .padding(itemPadding.dp)){
                FilledTonalButton(
                    modifier = modifier.fillMaxSize(),
                    onClick = {
                        viewModel.addItem(
                            Item(
                                name=text,
                                author = "",
                                parent = viewModel.lastNoteId.value,
                                id = 0L,
                                quantity = 0,
                                purchased = false,
                                price = 0.0)
                        )
                    }
                ) {
                    TextCard(text = text)
                }
            }
}



@Composable
fun TextCard(text:String, padding:Int = 0) {
        Text(
            text = text,
            modifier = Modifier.fillMaxSize(),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
}