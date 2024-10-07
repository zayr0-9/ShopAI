package com.example.shoppinglistv1.data

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppinglistv1.trimmer
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotesViewModel (
    private val dao: NotesDao,
    private val iDao: ItemDao,
    private val imgDao: ImageDao
): ViewModel() {


    private val isSortedByDate = MutableStateFlow(true)
    private var notes =
        isSortedByDate.flatMapLatest { sort ->
            if (sort) {
                dao.getNotesOrderedByDate()
            }
            else {
                dao.getNotesOrderedByQuestion()
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun searchNotes(searchText: String) = notes.map { list ->
        list.filter { note ->
            note.title.contains(searchText, ignoreCase = true)
        }
    }
    fun resetAiResponse(){
        aiResponse.value = ""
    }
    fun searchItems(searchText: String) = everyItem.map { list ->
        list.filter { item ->
            item.name.contains(searchText, ignoreCase = true)
        }
    }

    fun getNoteById(id: Long): Notes{
        var note = Notes(0,"",0,"")
        viewModelScope.launch { note = dao.getNoteById(id)!! }
        return note
    }

    private var itemIndex = MutableStateFlow(0L)

    val aiResponse = MutableStateFlow("")

    //get all items for current (if any) note
    var items =itemIndex.flatMapLatest {
        iDao.getListItemsForNote(it)}.
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    var everyItem =itemIndex.flatMapLatest {
        iDao.getAllItems()}.
    stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

//    val uniqueAllItems: Flow<List<Item>> = items.map { it.distinctBy { item -> item.id } }

    var images =itemIndex.flatMapLatest {
        imgDao.getImageItemsForNote(it)}.
    stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _searchNotes = MutableStateFlow<List<Notes>>(emptyList())
    val searchNotes : StateFlow<List<Notes>> = _searchNotes.asStateFlow()

    val _itemsWithDate = MutableStateFlow<List<ItemWithDate>>(emptyList())
    val itemsWithDate : StateFlow<List<ItemWithDate>> = _itemsWithDate.asStateFlow()

    val uniqueImages: Flow<List<Image>> = images.map { it.distinctBy { item -> item.id } }

    var _itemsToAdd = MutableStateFlow<List<Item>>(emptyList())
    val itemsToAdd: StateFlow<List<Item>> = _itemsToAdd.asStateFlow()
    //create funciton to empty this

    val _lastNoteId = MutableStateFlow<Long>(0L)
    val lastNoteId : StateFlow<Long> = _lastNoteId.asStateFlow()

    private val _state = MutableStateFlow(NotesState())
    //public, read-only StateFlow derived from _state
    val state =
        combine(_state, isSortedByDate, notes, items, images) { state, isSortedByDate, notes, items, images ->
            state.copy(
                notes = notes,
                items = items,
                images = images
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotesState() )

    private val _allItems = MutableStateFlow<List<Item>>(emptyList())
    val allItems: StateFlow<List<Item>> = _allItems.asStateFlow()

    val uniqueItems: Flow<List<Item>> = _allItems.map { it.distinctBy { item -> item.name } }

    init {
        viewModelScope.launch {
            iDao.getAllItems().collect {items ->
                _allItems.value = items
            }
        }
    }

    private val _noteToDelete = MutableStateFlow<Notes>(Notes(id=0L, title="Untitled", date=0L, content= ""))
    val noteToDelete: StateFlow<Notes> = _noteToDelete.asStateFlow()


    private val _pickedMediaUri = MutableStateFlow<Uri?>(null)
    val pickedMediaUri: StateFlow<Uri?> = _pickedMediaUri

    private val _fsMediaUri = MutableStateFlow<Uri?>(null)
    val fsMediaUri: StateFlow<Uri?> = _fsMediaUri

    private val _currentImgId = MutableStateFlow<Long?>(null)
    val currentImgId: StateFlow<Long?> = _currentImgId
    fun setFsMediaUri(uri: Uri?) {
        _fsMediaUri.value = uri
    }
    fun setPickedMediaUri(uri: Uri?) {
        _pickedMediaUri.value = uri
    }
    fun setCurrentImgId(id: Long?) {
        _currentImgId.value = id
    }

    fun getSearchResults(query: String) {
        viewModelScope.launch {
            dao.searchNotes(query).collect{results ->
                _searchNotes.value = results
        }
        }
    }

    fun getItemWithDate(query: String){
        viewModelScope.launch {
            iDao.getItemsAndDates(query).collect(){results->
                _itemsWithDate.value = results
            }
        }
    }

    fun updateNoteToDelete() {
        viewModelScope.launch {
            val updatedNote = dao.getNote(lastNoteId.value)
            _noteToDelete.value = updatedNote  }

    }

    fun clearRecentItemList() {
        _itemsToAdd.value = emptyList()
    }
    fun addItem(item: Item) {
        val currentList = _itemsToAdd.value
        val updatedList = currentList + item
        _itemsToAdd.value = updatedList
    }

    fun getAllItems() {
        viewModelScope.launch {
            iDao.getAllItems().collect {items ->
                _allItems.value = items
            }
        }
    }

    fun setItemIndex(index:Long){
        itemIndex.value = index
    }
    fun getItemIndex(): Long {
        return itemIndex.value
    }

    fun onEvent(event:NotesEvent) {
        when (event) {
            is NotesEvent.DeleteNote -> {
                viewModelScope.launch { dao.deleteNote(event.note)}
            }
            is NotesEvent.SaveNote -> {
                viewModelScope.launch {
                    val existingNote = if (event.id != 0L) dao.getNoteById(event.id) else null
                    var noteId = 0L
                    if (existingNote != null) {
                        // Note exists, update title and preserve old content
                        val updatedNote = existingNote.copy(title = event.title)
                        dao.upsertNote(updatedNote)
                    } else {
                        // Note does not exist, insert new note with both title and content
                        val newNote = Notes(
                            title = event.title,
                            content = event.content,
                            date = System.currentTimeMillis()
                        )
                        noteId = dao.upsertNote(newNote)
                        _lastNoteId.value = noteId
                    }
                }
                _state.update {
                    it.copy(
                        title = mutableStateOf(""),
                        content = mutableStateOf("")
                    )
                }
                state.value.notes.lastOrNull()?.id?.let { setItemIndex(it.toLong()) }
            }
            is NotesEvent.QuickSaveNote -> {
                viewModelScope.launch {
                    if(_itemsToAdd.value.isNotEmpty()) {
                        val note = Notes(
                            title = event.title,
                            content = event.content,
                            date = System.currentTimeMillis()
                        )
                        val newNoteId = dao.upsertNote(note)
                        _lastNoteId.value = newNoteId
                        _state.update {
                            it.copy(
                                title = mutableStateOf(""),
                                content = mutableStateOf("")
                            )
                        }
                        for (x in itemsToAdd.value) {
                            val item = Item(
                                parent = _lastNoteId.value, //itemIndex.value, // current item index
                                name = x.name, //state.value.items[(itemIndex.value).toInt()].name, //convert long to int
                                author = "", //adjust later if online accounts exist
                                purchased = true, //mutableStateOf, toggelable
                                quantity = 1,
                                price = 0.0//get from state, need field in NotesScreen
                            )
                            iDao.upsertItem(item)
                        }
                        //all items added to newly created note, clear the list
                        clearRecentItemList()
                    }
                    }
                }
            //this wont work, before i delete an item i need to display it
            is NotesEvent.DeleteItem -> {
                viewModelScope.launch {
                    viewModelScope.launch { iDao.deleteItem(event.item)}
                }
            }
            is NotesEvent.SaveItem -> {

                val item = Item(
                    parent = event.parent, //itemIndex.value, // current item index
                    name = event.name, //state.value.items[(itemIndex.value).toInt()].name, //convert long to int
                    author = "", //adjust later if online accounts exist
                    purchased = true, //mutableStateOf, toggelable
                    quantity = 1,  //get from state, need field in NotesScreen
                    price = 0.0
                )
                viewModelScope.launch {
                    iDao.upsertItem(item)
                }
            }

            NotesEvent.SortNotes -> {
                isSortedByDate.value = !isSortedByDate.value
                }

            is NotesEvent.SearchNote -> {
            }

            is NotesEvent.SaveImage -> {
                val image = Image(
                    id = event.id,
                    uri = event.uri.toString(),
                    parent = event.parent
                    )
                viewModelScope.launch {
                     imgDao.upsertImage(image)
                }
                setPickedMediaUri(null)
            }
            is NotesEvent.DeleteImage -> {
                viewModelScope.launch { imgDao.deleteImage(event.image)}
            }

            is NotesEvent.promptAI -> {
                viewModelScope.launch {
                    val chat = ChatData.getResponse(event.prompt)
                    aiResponse.value = chat.prompt
                }
            }

            is NotesEvent.SaveItemPrice -> {
                viewModelScope.launch{
                    val updatedItem = event.item.copy(price = event.price)
                    iDao.upsertItem(updatedItem)
                }
            }

            is NotesEvent.SaveMlItem -> {
                val item = Item(
                    parent = event.parent, //itemIndex.value, // current item index
                    name = event.name, //state.value.items[(itemIndex.value).toInt()].name, //convert long to int
                    author = "", //adjust later if online accounts exist
                    purchased = true, //mutableStateOf, toggelable
                    quantity = 1,  //get from state, need field in NotesScreen
                    price = event.price
                )
                viewModelScope.launch {
                    iDao.upsertItem(item)
                    println("ml item saved it works")
                }
            }
        }
    }
}

