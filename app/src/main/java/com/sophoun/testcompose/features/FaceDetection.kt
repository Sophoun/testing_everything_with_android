package com.sophoun.testcompose.features

import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.sophoun.testcompose.components.BaseImageAnalyzer
import com.sophoun.testcompose.components.CameraPreview
import com.sophoun.testcompose.components.CommonScaffoldWrapper
import com.sophoun.testcompose.utils.pxToDp
import java.util.concurrent.Executors

@Composable
fun FaceDetectionView(onBack: () -> Unit) {
    CommonScaffoldWrapper(
        title = "Face Detection",
        onBack = onBack,
        keepScreenOn = true
    ) {
        val faces = remember { mutableStateOf<List<Face>>(emptyList()) }
        val faceAnalyzer = remember {
            FaceAnalyzer { faceList ->
                faces.value = faceList
            }
        }

        CameraPreview(
            modifier = Modifier
                .wrapContentSize(),
            imageAnalyzer = faceAnalyzer,
            lensFacing = CameraSelector.LENS_FACING_FRONT,
        ) { previewSize, _ ->
            faceAnalyzer.setTargetResolution(Size(previewSize.width, previewSize.height))
            Canvas(
                Modifier
                    .size(previewSize.width.pxToDp(), previewSize.height.pxToDp())
                    .graphicsLayer {
                        rotationY = 180f
                    }
            ) {
                faces.value.forEach {
                    val rect = it.boundingBox.toComposeRect()
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

            Column(
                Modifier
                    .fillMaxSize()
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

// High-accuracy landmark detection and face classification
val highAccuracyOpts = FaceDetectorOptions.Builder()
    .setExecutor(Executors.newSingleThreadExecutor())
    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
    .build()

val detector = FaceDetection.getClient(highAccuracyOpts)

private class FaceAnalyzer(val resultCallBack: (List<Face>) -> Unit) : BaseImageAnalyzer() {

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            detector.process(image)
                .addOnSuccessListener { faces ->
                    resultCallBack(faces)
                    imageProxy.close()
                }
                .addOnFailureListener { _ ->
                    imageProxy.close()
                }
        }
    }
}