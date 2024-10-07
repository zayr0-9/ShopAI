package com.example.shoppinglistv1

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.shoppinglistv1.data.NotesViewModel

@Composable
fun SettingScreen(viewModel: NotesViewModel, navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        TextCard(text = "Hello world")
    }
}