package com.sophoun.testcompose

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sophoun.testcompose.activities.SwipePageActivity
import com.sophoun.testcompose.components.CommonButton
import com.sophoun.testcompose.components.CommonScaffoldWrapper
import com.sophoun.testcompose.features.FaceDetectionView
import com.sophoun.testcompose.features.NfcDetector
import com.sophoun.testcompose.features.ObjectDetectionView
import com.sophoun.testcompose.features.OpenCvView

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    Home(nav = navController)
                }
                composable("nfc") {
                    NfcDetector(onBack = {
                        navController.popBackStack()
                    })
                }
                composable("face_detection") {
                    FaceDetectionView(onBack = {
                        navController.popBackStack()
                    })
                }
                composable("object_detection") {
                    ObjectDetectionView(onBack = {
                        navController.popBackStack()
                    })
                }
                composable("open_cv") {
                    OpenCvView(onBack = {
                        navController.popBackStack()
                    })
                }
            }
        }
    }
}

@Composable
fun Home(
    nav: NavController
) {
    val activity = LocalContext.current as ComponentActivity
    CommonScaffoldWrapper(
        title = "Home",
        modifier = Modifier,
        isBack = false
    ) {
        Column {
            CommonButton(title = "NFC Detector") {
                nav.navigate("nfc")
            }
            CommonButton(title = "Swipe Pager") {
                activity.startActivity(Intent(activity, SwipePageActivity::class.java))
            }
            CommonButton(title = "Face Detection") {
                if (activity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    nav.navigate("face_detection")
                } else {
                    activity.requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
                }
                nav.navigate("face_detection")
            }
            CommonButton(title = "Object Detection") {
                nav.navigate("object_detection")
            }
            CommonButton(title = "OpenCV") {
                nav.navigate("open_cv")
//                activity.startActivity(Intent(activity, OpenCVActivity::class.java))
            }
        }
    }
}