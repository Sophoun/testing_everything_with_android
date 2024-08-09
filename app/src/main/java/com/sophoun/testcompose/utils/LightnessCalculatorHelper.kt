package com.sophoun.testcompose.utils

import androidx.camera.core.ImageProxy

/**
 * Calculate the lightness of the image
 */
object LightnessCalculatorHelper {

    /**
     * Get the luminance (Brightness) value of an image
     */
    fun getLuminance(image: ImageProxy): Int {
        val bytes = ByteArray(image.planes[0].buffer.remaining())
        image.planes[0].buffer.get(bytes)
        var total = 0
        for (value in bytes) {
            total += value.toInt() and 0xFF
        }
        var luminance = 0
        if (bytes.isNotEmpty()) {
            luminance = total / bytes.size
        }
        return luminance
    }

    /**
     * Check if the image is dark or not
     */
    fun isDark(image: ImageProxy): Boolean {
        // calculate luminance value percentage of maximum 255
        // Note: But the 255 is too height of brightness so, we will reduce it to 160 instead.
        val luminancePercentage = getLuminance(image) / 160f
        return luminancePercentage < 0.5
    }
}