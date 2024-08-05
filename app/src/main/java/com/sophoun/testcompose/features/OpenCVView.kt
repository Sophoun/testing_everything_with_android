package com.sophoun.testcompose.features

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sophoun.testcompose.components.CommonScaffoldWrapper
import org.opencv.android.OpenCVLoader

@Composable
fun OpenCvView(onBack: () -> Unit) {
    CommonScaffoldWrapper(
        title = "OpenCV",
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        onBack = onBack
    ) {
        val context = LocalContext.current
        LaunchedEffect(context) {
            if (OpenCVLoader.initLocal()) {
                Toast.makeText(context, "Open CV loaded", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Open CV not loaded", Toast.LENGTH_SHORT).show()
            }
        }
        Text(text = "OpenCV")
    }
}