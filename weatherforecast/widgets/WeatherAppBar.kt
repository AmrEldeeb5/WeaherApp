package com.example.weatherforecast.widgets

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherAppBar(
    title: String,
    navController: NavController? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Filled.ArrowBack,
    isMainScreen: Boolean = true,
    onAddActionClicked: (() -> Unit)? = null,
    elevation: Dp = 0.dp,
    onBackPressed: (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(text = title, color = Color.White) },
        navigationIcon = {
            if (!isMainScreen) {
                IconButton(onClick = { onBackPressed?.invoke() ?: navController?.popBackStack() }) {
                    Icon(imageVector = icon, contentDescription = "Back", tint = Color.White)
                }
            }
        },
        actions = {
            if (isMainScreen && onAddActionClicked != null) {
                IconButton(onClick = onAddActionClicked) {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "Search", tint = Color.White)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}
