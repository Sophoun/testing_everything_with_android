package com.sophoun.testcompose

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sophoun.testcompose.features.FaceDetectionView
import com.sophoun.testcompose.features.NfcDetector
import com.sophoun.testcompose.features.ObjectDetectionView

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    Scaffold(
                        topBar = { TopAppBar(
                            modifier = Modifier
                                .background(colorResource(id = R.color.purple_200)),
                            title = {
                            Text(text = "Everything in Compose")
                        }) }
                    ) { paddingValues ->
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)) {
                            Column {
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 12.dp, end = 12.dp),
                                    onClick = {
                                    navController.navigate("nfc")
                                }) {
                                    Text(text = "Nfc")
                                }
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 12.dp, end = 12.dp),
                                    onClick = {
                                    context.startActivity(Intent(context, SwipePageActivity::class.java))
                                }) {
                                    Text(text = "Swipe")
                                }
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 12.dp, end = 12.dp),
                                    onClick = {
                                        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                            navController.navigate("face_detection")
                                        } else {
                                            requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
                                        }
                                    }) {
                                    Text(text = "Face Detection")
                                }
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 12.dp, end = 12.dp),
                                    onClick = {
                                        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                            navController.navigate("object_detection")
                                        } else {
                                            requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
                                        }
                                    }) {
                                    Text(text = "Object Detection")
                                }
                            }
                        }
                    }
                }
                composable("nfc") {
                    NfcDetector(navController = navController)
                }
                composable("face_detection") {
                    FaceDetectionView()
                }
                composable("object_detection") {
                    ObjectDetectionView()
                }
            }
        }
    }

}