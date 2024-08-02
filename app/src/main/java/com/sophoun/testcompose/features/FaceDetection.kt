package com.sophoun.testcompose.features

import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.sophoun.testcompose.components.CameraPreview
import com.sophoun.testcompose.utils.AspectRatioHelper
import com.sophoun.testcompose.utils.pxToDp
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
        val faces = remember { mutableStateOf<List<Face>>(emptyList()) }
        val aspectRatio = remember { mutableFloatStateOf(1f) }

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            CameraPreview(
                modifier = Modifier
                    .wrapContentSize()
                    .onSizeChanged {
                        aspectRatio.floatValue =
                            AspectRatioHelper.getAspectRatio(it.width, it.height)
                    },
                imageAnalyzer = FaceAnalyzer { faceList, imgSize ->
                    faces.value = faceList
                }, lensFacing = CameraSelector.LENS_FACING_BACK
            ) {
                Canvas(
                    Modifier.fillMaxSize()
                ) {
                    faces.value.forEach {
                        val rect = it.boundingBox.toComposeRect()
                        val scaleRect = AspectRatioHelper.scaleRect(rect, aspectRatio.floatValue)
                        // draw round rect
                        drawRoundRect(
                            color = Color.Red,
                            topLeft = scaleRect.topLeft,
                            size = scaleRect.size,
                            cornerRadius = CornerRadius.Zero,
                            style = Stroke(
                                width = 2f
                            )
                        )
                    }
                }

                Column(
                    Modifier
                        .fillMaxSize()
                        .align(Alignment.TopStart)
                ) {
                    faces.value.forEach { f ->
                        val result = StringBuilder()
                        result.append("Track ID: ${f.trackingId}\n")
                        result.append("Left eye: ${f.leftEyeOpenProbability}\n")
                        result.append("Right eye: ${f.rightEyeOpenProbability}\n")
                        result.append("Smile: ${f.smilingProbability}\n")
                        result.append("headEulerAngleX ${f.headEulerAngleX}\n")
                        result.append("headEulerAngleY ${f.headEulerAngleY}\n")
                        result.append("headEulerAngleZ ${f.headEulerAngleZ}\n")
                        Text(
                            modifier = Modifier
                                .padding(16.dp),
                            color = Color.White,
                            text = result.toString()
                        )
                    }
                }
            }
        }
    }
}

// High-accuracy landmark detection and face classification
val highAccuracyOpts = FaceDetectorOptions.Builder()
//    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
    .setExecutor(Executors.newSingleThreadExecutor())
    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
    .build()

val detector = FaceDetection.getClient(highAccuracyOpts)

private class FaceAnalyzer(val resultCallBack: (List<Face>, Size) -> Unit) :
    ImageAnalysis.Analyzer {

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            detector.process(image)
                .addOnSuccessListener { faces ->
                    resultCallBack(faces, Size(image.width, image.height))
                    imageProxy.close()
                }
                .addOnFailureListener { _ ->
                    imageProxy.close()
                }
        }

    }
}