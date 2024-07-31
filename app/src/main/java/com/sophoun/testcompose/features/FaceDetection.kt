package com.sophoun.testcompose.features

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.sophoun.testcompose.components.CameraPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaceDetectionView() {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "Face Detection")
            })
        }
    ) { paddingValues ->
        val face = remember { mutableStateOf<Face?>(null) }

        Box(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()) {
            CameraPreview(imageAnalyzer = FaceAnalyzer {
                face.value = it
            })

            face.value?.let {
                val f = it.boundingBox.toComposeRect()
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // draw round rect
                    drawRoundRect(
                        color = Color.Red,
                        topLeft = f.topLeft,
                        size = f.size,
                        cornerRadius = CornerRadius.Zero,
                        style = Stroke(
                            width = 2f
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if (face.value != null) {
                    val f = face.value!!
                    val result = StringBuilder()
                    result.append("Left eye: ${f.leftEyeOpenProbability}\n")
                    result.append("Right eye: ${f.rightEyeOpenProbability}\n")
                    result.append("Smile: ${f.smilingProbability}\n")

                    Text(
                        modifier = Modifier
                            .padding(16.dp),
                        color = Color.White,
                        text = result.toString()
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (face.value != null) {
                    val f = face.value!!
                    val pass = f.smilingProbability!! > 0.9f && f.leftEyeOpenProbability!! > 0.9f && f.rightEyeOpenProbability!! > 0.9f
                    if(pass) {
                        Text(
                            modifier = Modifier
                                .padding(16.dp),
                            color = Color.Green,
                            text = "Pass"
                        )
                    } else {
                        Text(
                            modifier = Modifier
                                .padding(16.dp),
                            color = Color.White,
                            text = "Open your eyes and smile :)"
                        )
                    }
                }
            }
        }
    }
}

// High-accuracy landmark detection and face classification
val highAccuracyOpts = FaceDetectorOptions.Builder()
    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
    .build()

val detector = FaceDetection.getClient(highAccuracyOpts)

private class FaceAnalyzer(val resultCallBack: (Face) -> Unit) : ImageAnalysis.Analyzer {

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            detector.process(image)
                .addOnSuccessListener {
                    it.forEach {
                        resultCallBack(it)
                    }
                }
        }
        imageProxy.close()
    }
}