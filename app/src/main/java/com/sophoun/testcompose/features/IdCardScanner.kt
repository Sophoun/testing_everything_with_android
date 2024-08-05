package com.sophoun.testcompose.features

import androidx.compose.runtime.Composable
import com.sophoun.testcompose.components.CommonScaffoldWrapper

@Composable
fun IdCardScanner(onBack: () -> Unit) {
    CommonScaffoldWrapper(
        title = "Id Card Scanner",
        onBack = onBack
    ) {

    }
}