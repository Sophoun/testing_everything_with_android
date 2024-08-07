package com.sophoun.testcompose.features

import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.sophoun.testcompose.components.BaseImageAnalyzer
import com.sophoun.testcompose.components.CameraPreview
import com.sophoun.testcompose.components.CommonScaffoldWrapper
import com.sophoun.testcompose.utils.ObjectDetectorHelper
import com.sophoun.testcompose.utils.pxToDp
import org.tensorflow.lite.task.gms.vision.detector.Detection

@Composable
fun TensorFlowLiteView(onBack: () -> Unit) {
    val context = LocalContext.current
    val detections = remember {
        mutableStateOf<List<Detection>?>(emptyList())
    }

    val objectDetector = remember {
        ObjectDetectorHelper(
            context = context,
            objectDetectorListener = object : ObjectDetectorHelper.DetectorListener {
                override fun onInitialized() {
                    Log.d("TensorFlow", "onInitialized: tensorflow")
                }

                override fun onError(error: String) {
                    Log.d("TensorFlow", "onError: Tensorflow $error")
                }

                override fun onResults(
                    results: MutableList<Detection>?,
                    inferenceTime: Long,
                    imageHeight: Int,
                    imageWidth: Int
                ) {
                    detections.value = results
                    Log.d("TensorFlow", "onResults: ${results?.size} $inferenceTime")
                    results?.forEach {
                        Log.d("TensorFlow", "onResults: ${it.boundingBox}")
                        it.categories.forEach {
                            Log.d("TensorFlow", "onResults: category ${it.label} ${it.score}")
                        }
                    }
                }

            }, currentModel = ObjectDetectorHelper.MODEL_MOBILENETV1
        )
    }

    val tensorAnalyzer = remember {
        TensorFlowImageAnalyzer(objectDetector)
    }
    val textMeasurer = rememberTextMeasurer()

    CommonScaffoldWrapper(title = "TensorFlow Lite", onBack = onBack) {
        CameraPreview(
            imageAnalyzer = tensorAnalyzer,
            lensFacing = CameraSelector.LENS_FACING_BACK
        ) { previewSize, _ ->
            tensorAnalyzer.setTargetResolution(Size(previewSize.width, previewSize.height))
            Canvas(
                modifier = Modifier
                    .width(previewSize.width.pxToDp())
                    .height(previewSize.height.pxToDp())
            ) {
                detections.value?.forEach {
                    val rect = Rect(
                        it.boundingBox.left,
                        it.boundingBox.top,
                        it.boundingBox.right,
                        it.boundingBox.bottom
                    )
                    drawRoundRect(
                        color = Color.Red,
                        topLeft = rect.topLeft,
                        size = rect.size,
                        style = Stroke(
                            width = 2f
                        )
                    )

                    drawText(
                        textMeasurer,
                        "${it.categories[0].label}, ${it.categories[0].score}",
                        topLeft = rect.topLeft,
                        style = TextStyle(
                            color = Color.Green,
                            fontSize = TextUnit(12f, TextUnitType.Sp)
                        )
                    )
                }

            }
        }
    }
}

class TensorFlowImageAnalyzer(private val objectDetectorHelper: ObjectDetectorHelper) :
    BaseImageAnalyzer() {
    override fun analyze(image: ImageProxy) {
        objectDetectorHelper.detect(image.toBitmap(), image.imageInfo.rotationDegrees)
        image.close()
    }
}
