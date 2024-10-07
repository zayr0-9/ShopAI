package com.example.shoppinglistv1

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.room.util.newStringBuilder
import coil.compose.rememberAsyncImagePainter
import com.example.shoppinglistv1.data.ChatData
import com.example.shoppinglistv1.data.Notes
import com.example.shoppinglistv1.data.NotesEvent
import com.example.shoppinglistv1.data.NotesState
import com.example.shoppinglistv1.data.NotesViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    state: NotesState,
    viewModel : NotesViewModel,
    noteId : Long,
    navController: NavController,
    onEvent: (NotesEvent) -> Unit,

) {
    val listState = rememberLazyListState()
    val pickedMediaUri by viewModel.pickedMediaUri.collectAsState(null)
    var expanded by remember { mutableStateOf(false) }
    viewModel.setItemIndex(noteId)
    val context = LocalContext.current

    LaunchedEffect(key1 = state.items.size) {
        if (state.items.isNotEmpty()) {
            listState.animateScrollToItem(index = state.items.size - 1)
        }
    }
    //contains the whole screen
    Scaffold(
        topBar = {
            TopAppBar(
                title = { NoteTitle(state,onEvent, noteId) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                          IconButton(onClick = { ShareNote(context = context, viewModel, noteId) }) {
                              Icon(
                                  Icons.Filled.Share,
                                  contentDescription = "Share Note", // "Search",
                                  modifier = Modifier.size(30.dp)
                              )
                          }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }) {
        Column(modifier = Modifier.padding(it)) {
            //top half (list)
            Box(
                modifier = Modifier
//                    .height(600.dp),
                    .weight(0.7f),
                contentAlignment = Alignment.Center,
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(2.dp),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    state = listState
                )
                {
                    items(state.items.size) { index ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
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
                                    Item(state = state, index = index, onEvent = onEvent)
                                }
                            }
                    }
                }
            }
            Divider(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp), color = MaterialTheme.colorScheme.primary)
            //bottom half (entry)
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .fillMaxWidth()
                    .weight(0.1f),
                contentAlignment = Alignment.Center
            ) {
                val anchor = DpOffset(0.dp, -190.dp)
                if (state.iName.value.isNotBlank() || state.iName.value != "") {
                    val animationSpec = tween<Float>(durationMillis = 300, easing = LinearOutSlowInEasing)
                    androidx.compose.animation.AnimatedVisibility(
                        visible = state.iName.value.isNotBlank(),
                        enter = fadeIn(animationSpec) + scaleIn(animationSpec = tween(durationMillis = 1000), transformOrigin = TransformOrigin.Center), // Animation for entering the title
                        exit = fadeOut(animationSpec)   // Animation for exiting the title
                    )
                    {
                        Popup(
                            alignment = Alignment.TopStart,
                            offset = IntOffset(anchor.x.value.toInt(), anchor.y.value.toInt()),
                            properties = PopupProperties(dismissOnClickOutside = true)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background)
                            ) {
                                SuggestedItemRow(viewModel, state.iName.value, noteId, onEvent)
                            }
                        }
                    }
                }
                Row {
                TextField(
                    value = state.iName.value,
                    onValueChange = {
                        state.iName.value = it
                        expanded = true
                    },
                    modifier = Modifier
                        .width(320.dp)
                        .padding(4.dp),
                    singleLine = true,
                    placeholder = { Text(text = "Enter Item") },
                    shape = RoundedCornerShape(percent = 50),
                    colors = TextFieldDefaults.textFieldColors(
                        // Assuming you want a transparent background
                        unfocusedIndicatorColor = Color.Transparent, // Underline color when the TextField is not focused
                        focusedIndicatorColor = Color.Transparent, // Underline color when the TextField is focused
                        cursorColor = Color.Black
                    ),
                    keyboardOptions = (
                            KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done,
                                capitalization = KeyboardCapitalization.Sentences)
                            ),
                    keyboardActions = KeyboardActions(onDone = {
                        if (state.iName.value.isNotBlank()) {
                            onEvent(
                                NotesEvent.SaveItem(
                                    parent = noteId, // current item index
                                    name = state.iName.value.trim(), //state.value.items[(itemIndex.value).toInt()].name, //convert long to int
                                    author = "", //adjust later if online accounts exist
                                    purchased = true, //mutableStateOf, toggelable
                                    quantity = 1
                                )
                            )
                            state.iName.value = ""
                        }
                    })
                )
                IconButton(
                    modifier = Modifier
                        .size(70.dp)
                        .align(Alignment.Top)
                        .padding(start = 1.dp, end = 1.dp, top = 0.dp, bottom = 5.dp)
                        .offset(0.dp, -4.dp),
                    onClick = {
                        if(state.iName.value.isNotBlank()) {
                            onEvent(
                                NotesEvent.SaveItem(
                                    parent = noteId, // current item index
                                    name = state.iName.value.trim(), //state.value.items[(itemIndex.value).toInt()].name, //convert long to int
                                    author = "", //adjust later if online accounts exist
                                    purchased = true, //mutableStateOf, toggelable
                                    quantity = 1
                                )
                            )
                            state.iName.value = ""
                        } },
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddCircle,
                        contentDescription = "Add",
                        modifier = Modifier.fillMaxSize(),
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.secondary
                    )
                }
            }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 3.dp, bottom = 0.dp, start = 6.dp, end = 6.dp), contentAlignment = Alignment.Center
            ) {
                Row {
                    //mediapickerfunction
                    MediaPicker(viewModel, context)
                    //CameraAppPicker
                    CameraAppPicker(viewModel, context, noteId, onEvent)
                }
            }
            DisplayImageFromUri(state, viewModel)
            ImagePreviewAndSave(viewModel, noteId, onEvent)
            viewImageFullScreen(viewModel, onEvent, context, noteId)
            trimmer(onEvent, noteId, viewModel)

        }
    }
}
@Composable
fun trimmer(onEvent: (NotesEvent) -> Unit, noteId: Long, viewModel: NotesViewModel){
//    val inputText = """
//    WALKERS WOTSITS FLAMIN HOTLU - 1.50
//    MIKADO MILK HOC 75GAIR - 0.99
//    FRYER LINERS 50PK - 1.99
//    TOTAL TO PAY - 4.48
//    CARD TENDER - 4. 03
//    CHANGE DUE - 0.00
//""".trimIndent()
    var aiResponse: String = ""
    LaunchedEffect(viewModel ) {
        viewModel.aiResponse.collectLatest { value->
            aiResponse = value



        val inputText = aiResponse.trimIndent()

        val lines = inputText.split("\n")

        val itemsAndPrices = lines.mapNotNull { line ->
            val parts = line.split(" - ")
            if (parts.size == 2) {
                val description = parts[0].trim()
                val priceString = parts[1].trim().replace(" ", "")
                val price = priceString.toDoubleOrNull()
                if (price != null) Pair(description, price) else null
            } else {
                null
            }
        }
        var name: String = ""
        var price: Double = 0.0
// Separate the item descriptions and prices into two lists
        val itemDescriptions = itemsAndPrices.map { it.first }
        val prices = itemsAndPrices.map { it.second }

            itemsAndPrices.forEach { (name, price) ->
                onEvent(NotesEvent.SaveMlItem(name, 1, true, "", noteId, price))
            }
        viewModel.resetAiResponse()
//    }

        }
    }

}


@Composable
fun MediaPicker(viewModel: NotesViewModel, context:Context) {
    val mediaPicker = rememberLauncherForActivityResult(
        contract = (ActivityResultContracts.PickVisualMedia()),
        onResult = { uri ->
            if (uri != null) {
                val contentResolver = context.contentResolver
                val takeFlags =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION //or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                contentResolver.takePersistableUriPermission(uri, takeFlags)
                viewModel.setPickedMediaUri(uri)
            }
        })
    FilledTonalButton(onClick = {
        mediaPicker.launch(
            PickVisualMediaRequest((ActivityResultContracts.PickVisualMedia.ImageOnly))
        )
    }, modifier = Modifier.padding(horizontal=4.dp)
    ) {
        Text("Pick image")
    }
}

fun createImageFile(context: Context): File {
    // Create an image file name
    var currentPhotoPath: String
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File = context.filesDir  // Get app's private directory
    return File.createTempFile(
        "JPEG_${timeStamp}_", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    ).apply {
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = absolutePath
    }
}
fun getFileUri(context: Context, file: File): Uri {
    return FileProvider.getUriForFile(context, "${context.applicationContext.packageName}.provider", file)
}

@Composable
fun CameraAppPicker(viewModel: NotesViewModel, context:Context, parentId: Long, onEvent: (NotesEvent) -> Unit) {
    val imageFile = remember { createImageFile(context) }  // Permanent file creation
    val imageUri = remember { getFileUri(context, imageFile) }
    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { uri ->
        if (uri) {
            onEvent(NotesEvent.SaveImage(uri = imageUri, parent = parentId))// Note: Some camera apps might not set `data`
        }
    }
    var flag = false
    FilledTonalButton(modifier = Modifier.padding(horizontal=4.dp), onClick = { takePicture.launch(imageUri)
    flag = true}) {
        Text("Take Picture")
    }
}


fun NoteToString(note:Notes, viewModel: NotesViewModel): String {
    val builder = StringBuilder()
    builder.append("Note: ${note.title }\n")
    for (item in viewModel.items.value) {
        builder.append("- ${item.name}, Qty: ${item.quantity}")
        builder.append("\n")
    }
    return builder.toString()
}

fun shareNoteText(context: Context, noteText: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, noteText)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}

fun ShareNote(context: Context, viewModel: NotesViewModel, noteId: Long) {
    val note = viewModel.getNoteById(noteId)
    val noteText = NoteToString(note, viewModel)
    shareNoteText(context, noteText)
}


@Composable
fun SuggestedItemRow(viewModel: NotesViewModel, value: String, noteId: Long, onEvent: (NotesEvent) -> Unit) {
//    val allItems by viewModel.uniqueItems.collectAsState(initial = emptyList())
    var searchText by remember { mutableStateOf("") }
    val searchResult by viewModel.searchItems(value).collectAsState(initial = emptyList())
    val uItemList = searchResult.distinctBy{it.name}
    val scrollState = rememberScrollState()
    if(uItemList.isNotEmpty()) {
            Row(modifier = Modifier.horizontalScroll(scrollState)) {
                uItemList.forEach() { item ->
                    test(
                        itemName = item.name,
                        itemPadding = 2,
                        viewModel = viewModel,
                        modifier = Modifier,
                        noteId = noteId,
                        onEvent = onEvent
                        // No need for Modifier.horizontalScroll, LazyRow handles scrolling
                    )
                    // Adjust SuggestionItemRow as needed
                }
            }
        }
}
@Composable
fun test( itemName: String, itemPadding:Int, modifier: Modifier, viewModel : NotesViewModel,noteId: Long ,onEvent: (NotesEvent) -> Unit) {
    Box (modifier = modifier
        .width(100.dp)
        .height(40.dp)
        ){
        FilledTonalButton(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 1.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer) ,
            onClick = {
                onEvent(NotesEvent.SaveItem(
                    parent = noteId, // current item index
                    name = itemName, //state.value.items[(itemIndex.value).toInt()].name, //convert long to int
                    author = "", //adjust later if online accounts exist
                    purchased = true, //mutableStateOf, toggelable
                    quantity = 1
                ))
                viewModel.state.value.iName.value = ""
            }
        ) {
            Text(text = (itemName), color = MaterialTheme.colorScheme.onSecondaryContainer )}
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun NoteTitle(state: NotesState, onEvent: (NotesEvent) -> Unit, noteId: Long){
    val note = state.notes.find { it.id == noteId }//?.title
    val index = state.notes.indexOfFirst { it.id == noteId }
    val keyboardController = LocalSoftwareKeyboardController.current
    val localFocus = LocalFocusManager.current
    if (note != null) {
        val titleState = remember { mutableStateOf(note?.title ?: "") }
        TextField(
            value = titleState.value,
            onValueChange = { newTitle ->
                titleState.value = newTitle
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
                capitalization = KeyboardCapitalization.Sentences
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    // Trigger the save note event when the OK (Done) button on the keyboard is pressed
                    onEvent(
                        NotesEvent.SaveNote(
                            title = titleState.value,
                            content = "",  //
                            id = noteId
                        )
                    )
                    keyboardController?.hide()
                    localFocus.clearFocus()
                }
            ),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                cursorColor = Color.Black,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(4.dp))
//        Text(text = note.content, fontSize = 20.sp)
    }
}

@Composable
fun ImagePreviewAndSave(viewModel: NotesViewModel, parentId: Long, onEvent: (NotesEvent) -> Unit) {
    val uri by viewModel.pickedMediaUri.collectAsState()
    val openDialog = remember { mutableStateOf(false)}
    uri?.let {safeUri->
        if (safeUri == null) {openDialog.value = false } else {openDialog.value=true }
        if (openDialog.value)
             {
                Dialog(
                    onDismissRequest = { openDialog.value = false},
                    properties = DialogProperties(usePlatformDefaultWidth = false),
                    content = {
                        Box(
                        modifier = Modifier
                            .fillMaxSize() // Fill the screen
                            .background(Color.Black.copy(alpha = 0.5f)), // Semi-transparent black
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            val painter = rememberAsyncImagePainter(model = safeUri)
                            Image(
                                painter = painter,
                                contentDescription = "Selected Image",
                                modifier = Modifier
                                    .height(600.dp)
                                    .width(600.dp)
                                    .padding(10.dp),
                                contentScale = ContentScale.Fit // Adjust the scaling if needed
                            )
                            Row (modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp),
                                horizontalArrangement = Arrangement.Center){
                                Button(onClick = {
                                    // Action to save the image
                                    onEvent(NotesEvent.SaveImage(uri = safeUri, parent = parentId))
                                //reset the URI after saving
                                }, modifier = Modifier.padding(2.dp)) {
                                    Text("Save Image", fontSize = 22.sp)
                                }
                                Button(
                                    onClick = {
                                        openDialog.value = false
                                        viewModel.setPickedMediaUri(null)
                                    },
                                    modifier = Modifier.padding(2.dp),
                                ) {
                                    Text("Dismiss", fontSize = 22.sp)
                                }
                            }

                        }
                    }}) //{

//                }
            }
        }
    }

@Composable
fun viewImageFullScreen(viewModel: NotesViewModel, onEvent: (NotesEvent) -> Unit, context: Context, noteId: Long){
    val fsUri by viewModel.fsMediaUri.collectAsState(null)
    val allImages by viewModel.uniqueImages.collectAsState(initial = null)
    val currentId by viewModel.currentImgId.collectAsState(null)
    val currentImage = allImages?.find { it.id == currentId }
    val openDialog = remember { mutableStateOf(false)}
    val aiResponse by viewModel.aiResponse.collectAsState()
    if (fsUri == null) {openDialog.value = false } else {openDialog.value=true }
    if(openDialog.value) {

            Dialog(
                onDismissRequest = { openDialog.value = false},
                properties = DialogProperties(usePlatformDefaultWidth = false),
                content = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize() // Fill the screen
                            .background(Color.Black.copy(alpha = 0.5f)), // Semi-transparent black
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            val painter = rememberAsyncImagePainter(model = fsUri)
                            Image(
                                painter = painter,
                                contentDescription = "Selected Image",
                                modifier = Modifier
                                    .padding(4.dp)
                                    .height(600.dp)
                                    .align(Alignment.CenterHorizontally),
                                contentScale = ContentScale.Fit // Adjust the scaling if needed
                            )
                            Spacer(modifier = Modifier.height(100.dp))
                            Row (modifier = Modifier
                                .fillMaxWidth()
//                                .weight(0.1f)
                                .padding(horizontal = 6.dp),
                                horizontalArrangement = Arrangement.Center){
                                Button(
                                    onClick = {
                                        openDialog.value = false
                                        viewModel.setFsMediaUri(null)
                                        if (currentImage != null) {
                                            onEvent(NotesEvent.DeleteImage(currentImage))
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(0.5f)
                                        .padding(horizontal = 4.dp),
                                ) {
                                    Text("Delete", fontSize = 22.sp)
                                }
                                Button(
                                    onClick = {
                                        openDialog.value = false
                                        viewModel.setFsMediaUri(null)
                                    },
                                    modifier = Modifier
                                        .weight(0.5f)
                                        .padding(horizontal = 4.dp),
                                ) {
                                    Text("Dismiss", fontSize = 22.sp)
                                }
                            }
                            Button(onClick = {
                                mlTest(fsUri!!, context, onEvent, viewModel)
                                },
                                modifier = Modifier
//                                    .weight(0.1f)
                                    .padding(top = 20.dp, bottom = 10.dp)
                                    .align(Alignment.CenterHorizontally)
                            ) {
                                Text(text = "Scan", fontSize = 22.sp)
                            }

                        }
                    }}) //{

                }
        }



//@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun DisplayImageFromUri( state: NotesState, viewModel: NotesViewModel) {
    val uniqueImages by viewModel.uniqueImages.collectAsState(initial = null)
    Box(
        modifier = Modifier
            .padding(top = 2.dp, bottom = 10.dp, start = 3.dp, end = 3.dp)
    ) {
        LazyRow(modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
        ) {
            uniqueImages?.let {
                items(it.count()) { it ->
                    val currentImg = uniqueImages!![it]
                    val convUri = Uri.parse(currentImg.uri)
                    if (convUri != null) {
                        val painter = rememberAsyncImagePainter(model = convUri)
                        Image(
                            painter = painter,
                            contentDescription = "Selected Image",
                            modifier = Modifier
                                .requiredWidth(200.dp)
                                .requiredHeight(150.dp)
                                .padding(5.dp)
                                .clickable {
                                    viewModel.setFsMediaUri(convUri)
                                    viewModel.setCurrentImgId(currentImg.id)
                                }, //set currently selected image
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
    }
}

fun mlTest(uri: Uri, context: Context, onEvent: (NotesEvent) -> Unit, viewModel: NotesViewModel){
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    val image: InputImage
//    val response by viewModel.aiResponse.collectAsState("")

    val prompt = "the following is output from ml kit of a receipt. " +
            "I want you to list items then prices. " +
            "They should be separated by a new line. " +
            "Try to correct the item names if they don't make sense as " +
            "machine vision is not perfect. Most items are groceries. Mention nothing else."


    val itemList = newStringBuilder()
    itemList.append("Items - ")
    val priceList = newStringBuilder()
    priceList.append("Prices - ")
    try {
        image = InputImage.fromFilePath(context, uri)
        val height = image.height
        val width = image.width
        val result = recognizer.process(image)
            .addOnSuccessListener { result ->
                if(result != null){
                    val resultText = result.text
                    for (block in result.textBlocks) {
                        for (line in block.lines) {
                            val lineText = line.text
                            val lineCornerPoints = line.cornerPoints
                            val lineFrame = line.boundingBox
                            if (lineFrame!!.right < ((width / 1.5))) {
                                itemList.append(lineText)
                            }
                            if (lineFrame!!.right > (width - (width / 5))) {
                                priceList.append(lineText)
                            }
                            for (element in line.elements) {
                                val elementText = element.text
                                val elementCornerPoints = element.cornerPoints
                                val elementFrame = element.boundingBox
                            }
                        }
                    }
                    // Task completed successfully
                    // ...
                    onEvent(NotesEvent.promptAI(prompt + itemList.toString() + priceList.toString()))
                }
                }
            .addOnFailureListener { e ->
                // Task failed with an exception
                println(e.toString())
            }
    } catch (e: IOException) {
        e.printStackTrace()
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Item(
    state: NotesState,
    index : Int,
    onEvent: (NotesEvent) -> Unit
) {
    val priceField = remember { mutableStateOf("")}
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)) {
        Spacer(modifier = Modifier.height((16.dp)))
        val item = state.items[index]
        val name = index.toString() + ". " + item.name
        Text(
            text = name,
            fontSize = 24.sp,
            modifier = Modifier.weight(0.8f)
        )
        Spacer(modifier = Modifier.height((8.dp)))

        TextField(value = item.price.toString(),modifier = Modifier
            .width(110.dp)
            .height(60.dp),
            shape = RoundedCornerShape(30) ,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done),
            colors = TextFieldDefaults.textFieldColors(
            // Assuming you want a transparent background
            unfocusedIndicatorColor = Color.Transparent, // Underline color when the TextField is not focused
            focusedIndicatorColor = Color.Transparent, // Underline color when the TextField is focused
            cursorColor = Color.Black,
            containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            onValueChange ={
            priceField.value = it
            },
            keyboardActions = KeyboardActions(onDone = {
                val number = priceField.value.toDouble()
                onEvent(NotesEvent.SaveItemPrice(number, item))
            }),
            supportingText = {"Price"},
            leadingIcon = { Text("£") }

        )

        IconButton(onClick = {onEvent(NotesEvent.DeleteItem(state.items[index]))}) {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "Delete Notes",
                modifier = Modifier
                    .size(24.dp)
                    .weight(0.2f),
                tint = androidx.compose.material3.MaterialTheme.colorScheme.primary
            )
        }
    }
}