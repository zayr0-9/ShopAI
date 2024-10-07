package com.example.shoppinglistv1.data

import android.net.Uri

sealed interface NotesEvent {
//    object SaveNote : NotesEvent
    object SortNotes : NotesEvent
    data class DeleteNote(val note: Notes) : NotesEvent
    data class SaveNote(
        val title : String,
        val content: String,
        val id: Long
    ): NotesEvent
    data class QuickSaveNote(
        val title : String,
        val content: String,
        val id: Long,
        val date: Long
    ): NotesEvent

    data class DeleteItem(val item:Item) : NotesEvent
    data class SaveItem(
        val name: String,
        val quantity: Int,
        val purchased: Boolean,
        val author: String,
        val parent: Long
    ) : NotesEvent
    data class SaveItemPrice(
        val price : Double,
        val item: Item
    ) : NotesEvent

    data class SaveMlItem(
        val name: String,
        val quantity: Int,
        val purchased: Boolean,
        val author: String,
        val parent: Long,
        val price: Double
    ) : NotesEvent

    data class SearchNote(val query: String) : NotesEvent
    data class SaveImage(val id: Long = 0,
                         val uri: Uri,
                         val parent: Long) : NotesEvent

    data class DeleteImage(val image: Image) : NotesEvent
    data class promptAI(val prompt: String) : NotesEvent
}

