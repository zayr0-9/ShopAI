package com.example.shoppinglistv1.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class NotesState(
    val notes: List<Notes> = emptyList(),
    val title: MutableState<String> = mutableStateOf(""),
    val content: MutableState<String> = mutableStateOf(""),
    val iName: MutableState<String> = mutableStateOf(""),
    val items: List<Item> = emptyList(),
    val images: List<Image> = emptyList()
)
