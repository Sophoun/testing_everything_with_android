package com.sophoun.testcompose.components

import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonScaffoldWrapper(
    title: String,
    modifier: Modifier = Modifier,
    isBack: Boolean = true,
    onBack: () -> Unit = {},
    keepScreenOn: Boolean = false,
    content: @Composable () -> Unit
) {
    val activity = LocalContext.current as ComponentActivity
    LaunchedEffect(activity) {
        if (keepScreenOn) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = title)
                },
                navigationIcon = {
                    if (isBack) {
                        Icon(
                            modifier = Modifier.clickable {
                                onBack()
                            },
                            painter = painterResource(id = android.R.drawable.ic_menu_revert),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                })
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            content()
        }
    }
}