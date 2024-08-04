package com.sophoun.testcompose.components

import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.rotationMatrix
import com.sophoun.testcompose.utils.AspectRatioHelper
import java.util.concurrent.Executors


@Composable
fun CameraPreview(
    imageAnalyzer: ImageAnalysis.Analyzer,
    modifier: Modifier = Modifier,
    lensFacing: Int = CameraSelector.LENS_FACING_FRONT,
    content: @Composable (previewSize: IntSize, aspectRatio: Float) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewSize = remember { mutableStateOf(IntSize(0, 0)) }
    val aspectRatio = remember { mutableFloatStateOf(0f) }
    val aspectRatioStrategy = AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY

    val cameraController = remember {
        LifecycleCameraController(context).apply {
            cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()
            previewResolutionSelector = ResolutionSelector.Builder()
                .setResolutionStrategy(ResolutionStrategy.HIGHEST_AVAILABLE_STRATEGY)
                .setAspectRatioStrategy(aspectRatioStrategy)
                .build()
            imageAnalysisBackpressureStrategy = ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
            setImageAnalysisAnalyzer(Executors.newSingleThreadExecutor(), imageAnalyzer)
            // Bind the LifecycleCameraController to the lifecycleOwner
            bindToLifecycle(lifecycleOwner)
        }
    }

    Box(modifier) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged {
                    val height = when (aspectRatioStrategy) {
                        AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY -> it.width * 4 / 3
                        AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY -> it.width * 16 / 9
                        else -> it.height
                    }
                    previewSize.value = IntSize(it.width, height)
                    aspectRatio.floatValue = AspectRatioHelper.getAspectRatio(it.width, height)
                },
            factory = { ctx ->
                // Initialize the PreviewView and configure it
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FIT_START
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    // Set the controller to manage the camera lifecycle
                    controller = cameraController
                }
            },
            onRelease = {
                // Release the camera controller when the composable is removed from the screen
                cameraController.unbind()
            }
        )
    }

    Log.d("CameraPreview", "CameraPreview: ${previewSize.value}, ${aspectRatio.floatValue}")
    content(previewSize.value, aspectRatio.floatValue)
}

/**
 * Base Image analysis class
 * Provide basic functionality to change analyzer resolution
 */
abstract class BaseImageAnalyzer : ImageAnalysis.Analyzer {

    private var targetResolution: Size? = null

    /**
     * Set target resolution to override default resolution
     * 640x480 by default
     */
    fun setTargetResolution(targetResolution: Size) {
        this.targetResolution = targetResolution
    }

    /**
     * Override to provide custom resolution
     * default resolution is 640x480
     */
    override fun getDefaultTargetResolution(): Size? {
        return targetResolution
    }
}