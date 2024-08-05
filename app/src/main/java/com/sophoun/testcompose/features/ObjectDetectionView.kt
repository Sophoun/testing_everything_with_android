package com.sophoun.testcompose.features

import android.util.Size
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toComposeRect
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.sophoun.testcompose.components.BaseImageAnalyzer
import com.sophoun.testcompose.components.CameraPreview
import com.sophoun.testcompose.components.CommonScaffoldWrapper

@Composable
fun ObjectDetectionView(onBack: () -> Unit) {
    val detectedObject = remember {
        mutableStateOf<DetectedObject?>(null)
    }
    val objectDetector = remember {
        ObjectDetectionAnalyzer {
            detectedObject.value = it
            it?.labels?.forEach { label ->
                println(label)
            }
        }
    }

    CommonScaffoldWrapper(
        title = "Object Detection",
        onBack = onBack
    ) {
        CameraPreview(imageAnalyzer = objectDetector) { previewSize, _ ->
            objectDetector.setTargetResolution(Size(previewSize.width, previewSize.height))
            Canvas(modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = 180f
                }) {
                detectedObject.value?.boundingBox?.let {
                    val rect = it.toComposeRect()
                    drawRoundRect(
                        color = Color.Red,
                        topLeft = rect.topLeft,
                        size = rect.size,
                        cornerRadius = CornerRadius.Zero,
                        style = Stroke(
                            width = 2f
                        )
                    )
                }
            }

            if (detectedObject.value?.labels?.firstOrNull() != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
                    val v = detectedObject.value!!.labels.first()
                    val textDisplay = StringBuilder()
                    textDisplay.append("Index: ${v.index}\n")
                    textDisplay.append("Label: ${v.text}\n")
                    textDisplay.append("Confident: ${v.confidence}\n")
                    Text(text = textDisplay.toString())
                }
            }
        }
    }
}

// Live detection and tracking
val options = ObjectDetectorOptions.Builder()
    .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
//    .enableClassification()  // Optional
    .build()

val objectDetector = ObjectDetection.getClient(options)

class ObjectDetectionAnalyzer(val onResult: (DetectedObject?) -> Unit) : BaseImageAnalyzer() {

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            objectDetector.process(image)
                .addOnSuccessListener { detectedObjects ->
                    println("Detected ${detectedObjects.size} objects")
                    if (detectedObjects.isEmpty()) {
                        onResult(null)
                    }
                    detectedObjects.forEach {
                        onResult(it)
                    }
                    imageProxy.close()
                }
                .addOnFailureListener { _ ->
                    onResult(null)
                    println("Failed")
                    imageProxy.close()
                }
        }
    }
}