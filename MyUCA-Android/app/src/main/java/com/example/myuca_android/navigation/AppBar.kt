package com.example.myuca_android.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun AppBar(
    title: String,
    navigationIcon: @Composable () -> Unit,
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = navigationIcon,
    )
}

@Composable
fun AppBar(
    title: String,
) {
    TopAppBar(
        title = { Text(title) },
    )
}

@Composable
fun AppBar(
    title: String,
    navController: NavController,
) {
    AppBar(title, navigationIcon = {
        IconButton(onClick = {
            navController.navigateUp()
        }) {
            Icon(Icons.Default.ArrowBack, "Atrás")
        }
    })
}

@Composable
fun AppBar(
    title: String,
    actions: @Composable RowScope.() -> Unit,
) {
    TopAppBar(
        title = { Text(title) },
        actions = actions,
    )
}
