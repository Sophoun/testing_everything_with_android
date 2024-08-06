package com.sophoun.testcompose.activities

import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.WindowManager
import android.widget.Toast
import com.sophoun.testcompose.databinding.LayoutOpencvBinding
import org.opencv.android.CameraActivity
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat


class OpenCVActivity : CameraActivity(), CvCameraViewListener2 {
    val TAG: String = "OCVSample::Activity"

    private lateinit var binding: LayoutOpencvBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "called onCreate")
        super.onCreate(savedInstanceState)
        if (OpenCVLoader.initLocal()) {
            Log.i(TAG, "OpenCV loaded successfully")
        } else {
            Log.e(TAG, "OpenCV initialization failed!")
            Toast.makeText(this, "OpenCV initialization failed!", Toast.LENGTH_LONG)
                .show()
            return
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        LayoutOpencvBinding.inflate(layoutInflater).apply {
            binding = this
        }
        setContentView(binding.root)


        binding.javaCameraView.visibility = SurfaceView.VISIBLE
        binding.javaCameraView.setCvCameraViewListener(this)
    }

    public override fun onPause() {
        super.onPause()
        binding.javaCameraView.disableView()
    }

    public override fun onResume() {
        super.onResume()
        binding.javaCameraView.enableView()
    }

    override fun getCameraViewList(): List<CameraBridgeViewBase?> {
        return listOf(binding.javaCameraView)
    }

    public override fun onDestroy() {
        super.onDestroy()
        binding.javaCameraView.disableView()
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
    }

    override fun onCameraViewStopped() {
    }

    override fun onCameraFrame(inputFrame: CvCameraViewFrame): Mat {
        return inputFrame.rgba()
    }
}