package com.sophoun.testcompose.features

import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sophoun.testcompose.components.BaseImageAnalyzer
import com.sophoun.testcompose.components.CameraPreview
import com.sophoun.testcompose.components.CommonScaffoldWrapper
import com.sophoun.testcompose.utils.LightnessCalculatorHelper
import com.sophoun.testcompose.utils.pxToDp

@Composable
fun AmountOfLightDetectionView(onBack: () -> Unit) {
    val detection = remember {
        mutableIntStateOf(0)
    }

    val amountOfLightImageAnalyzer = remember {
        AmountOfLightImageAnalyzer {
            detection.intValue = it
        }
    }

    CommonScaffoldWrapper(title = "TensorFlow Lite", onBack = onBack) {
        CameraPreview(
            imageAnalyzer = amountOfLightImageAnalyzer,
            lensFacing = CameraSelector.LENS_FACING_BACK
        ) { previewSize, _ ->
            amountOfLightImageAnalyzer.setTargetResolution(
                Size(
                    previewSize.width,
                    previewSize.height
                )
            )
            Box(
                modifier = Modifier
                    .width(previewSize.width.pxToDp())
                    .height(previewSize.height.pxToDp())
                    .padding(12.dp),
                contentAlignment = Alignment.TopStart,
            ) {
                Text(
                    text = "Image lightness is : ${detection.intValue}",
                    color = Color.White
                )
            }
        }
    }
}

class AmountOfLightImageAnalyzer(private val callback: (Int) -> Unit) :
    BaseImageAnalyzer() {

    override fun analyze(image: ImageProxy) {
        println("Is dark: ${LightnessCalculatorHelper.isDark(image)}")
        callback(LightnessCalculatorHelper.getLuminance(image))
        image.close()
    }
}
