package com.sophoun.testcompose.features

import android.app.PendingIntent
import android.nfc.NfcAdapter
import android.nfc.tech.NfcA
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sophoun.testcompose.components.CommonScaffoldWrapper
import java.nio.charset.Charset

@Composable
fun NfcDetector(onBack: () -> Unit) {
    val activity = LocalContext.current as ComponentActivity
    val nfcAdapter = remember {
        NfcAdapter.getDefaultAdapter(activity)
    }
    val nfcCallback = remember {
        NfcAdapter.ReaderCallback { tag ->
            NfcA.get(tag).let {
                it.connect()
                it.transceive(byteArrayOf(0x30.toByte(), 0x00.toByte())).let {
                    Log.d("NfcDetector", "onTagDiscovered: ${it}")
                }
                Log.d(
                    "NfcDetector",
                    "onTagDiscovered: ${String(it.atqa, Charset.forName("US-ASCII"))}"
                )
                it.close()
            }
        }
    }
    LaunchedEffect(nfcAdapter) {
        nfcAdapter?.enableReaderMode(
            activity, nfcCallback, NfcAdapter.FLAG_READER_NFC_A
                    or NfcAdapter.FLAG_READER_NFC_B
                    or NfcAdapter.FLAG_READER_NFC_F
                    or NfcAdapter.FLAG_READER_NFC_V
                    or NfcAdapter.FLAG_READER_NFC_BARCODE
                    or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
                    or NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS, null
        )
        val pendingIntent =
            PendingIntent.getActivity(activity, 0, activity.intent, PendingIntent.FLAG_IMMUTABLE)
        nfcAdapter?.enableForegroundDispatch(activity, pendingIntent, null, null)
    }

    // listen to back press
    LaunchedEffect(Unit) {
        activity.onBackPressedDispatcher.addCallback {
            nfcAdapter?.disableForegroundDispatch(activity)
            onBack()
        }
    }

    CommonScaffoldWrapper(
        title = "Nfc Detector",
        onBack = onBack
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(text = "NfcDetector")
        }
    }
}