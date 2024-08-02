package com.sophoun.testcompose.utils

import android.util.Size
import androidx.compose.ui.geometry.Rect

object AspectRatioHelper {
    /**
     * Get aspect ratio
     */
    fun getAspectRatio(width: Int, height: Int): Float {
        if (width > height) {
            return width.toFloat() / height
        }
        return height.toFloat() / width
    }

    /**
     * Scale image
     */
    fun scale(width: Int, height: Int, aspectRatio: Float): Size {
        val newWidth = (width * aspectRatio).toInt()
        val newHeight = (height * aspectRatio).toInt()
        return Size(newWidth, newHeight)
    }

    fun scaleRect(rect: Rect, aspectRatio: Float): Rect {
        return Rect(
            left = rect.left * aspectRatio,
            top = rect.top * aspectRatio,
            right = rect.right * aspectRatio,
            bottom = rect.bottom * aspectRatio
        )
    }
}