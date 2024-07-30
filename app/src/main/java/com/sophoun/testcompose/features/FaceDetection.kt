package com.sophoun.testcompose.features

import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.Executors

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
        // Obtain the current context and lifecycle owner
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current

        val face = remember { mutableStateOf<Face?>(null) }

        // Remember a LifecycleCameraController for this composable
        val cameraController = remember {
            LifecycleCameraController(context).apply {
                cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                    .build()
                setImageAnalysisAnalyzer(Executors.newSingleThreadExecutor(), FaceAnalyzer {
                    face.value = it
                })
                // Bind the LifecycleCameraController to the lifecycleOwner
                bindToLifecycle(lifecycleOwner)
            }
        }

        Box(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    // Initialize the PreviewView and configure it
                    PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_START
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        controller = cameraController // Set the controller to manage the camera lifecycle
                    }
                },
                onRelease = {
                    // Release the camera controller when the composable is removed from the screen
                    cameraController.unbind()
                }
            )

//            if(face.value != null) {
//                val f = face.value!!
//                Canvas(
//                    modifier = Modifier.fillMaxSize()
//                ) {
//                    // draw round rect
//                    drawRoundRect(
//                        color = Color.Red,
//                        topLeft = Offset(0f, 0f),
//                        size = Size(
//                            f.boundingBox.width().toFloat(),
//                            f.boundingBox.height().toFloat()
//                        ),
//                        cornerRadius = CornerRadius.Zero,
//                        style = Stroke(
//                            width = 2f
//                        )
//                    )
//                }
//            }

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