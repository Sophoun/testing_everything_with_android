package com.sophoun.testcompose.features

import android.view.LayoutInflater
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.sophoun.testcompose.components.CommonScaffoldWrapper
import com.sophoun.testcompose.databinding.LayoutOpencvBinding
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat

@Composable
fun OpenCvView(onBack: () -> Unit) {
    val binding = remember {
        mutableStateOf<LayoutOpencvBinding?>(null)
    }
    CommonScaffoldWrapper(
        title = "OpenCV",
        modifier = Modifier
            .fillMaxSize(),
        onBack = onBack
    ) {
        val context = LocalContext.current
        LaunchedEffect(context) {
            if (OpenCVLoader.initLocal()) {
                Toast.makeText(context, "Open CV loaded", Toast.LENGTH_SHORT).show()
                binding.value?.javaCameraView?.enableView()
            } else {
                Toast.makeText(context, "Open CV not loaded", Toast.LENGTH_SHORT).show()
            }
        }
        AndroidView(
            modifier = Modifier
                .fillMaxSize(),
            factory = { ctx ->
                LayoutOpencvBinding.inflate(LayoutInflater.from(ctx)).apply {
                    javaCameraView.setCvCameraViewListener(object : CvCameraViewListener2 {
                        override fun onCameraViewStarted(width: Int, height: Int) {}

                        override fun onCameraViewStopped() {}

                        override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
                            return inputFrame.rgba()
                        }
                    })

                    javaCameraView.visibility = CameraBridgeViewBase.VISIBLE
                    javaCameraView.setCameraPermissionGranted()
                    binding.value = this
                }.root
            })
    }
}