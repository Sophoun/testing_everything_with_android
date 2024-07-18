package com.sophoun.testcompose

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.MifareClassic
import android.nfc.tech.Ndef
import android.nfc.tech.NfcA
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import com.sophoun.testcompose.ui.theme.TestcomposeTheme
import java.nio.charset.Charset

class MainActivity : ComponentActivity(), NfcAdapter.ReaderCallback {
    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestcomposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        /**
         * NFC PART
         */
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcAdapter?.enableReaderMode(
            this, this, NfcAdapter.FLAG_READER_NFC_A
                    or NfcAdapter.FLAG_READER_NFC_B
                    or NfcAdapter.FLAG_READER_NFC_F
                    or NfcAdapter.FLAG_READER_NFC_V
                    or NfcAdapter.FLAG_READER_NFC_BARCODE
                    or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
                    or NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS, null
        )
    }

    override fun onResume() {
        super.onResume()
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onTagDiscovered(tag: Tag?) {
        Log.d(javaClass.name, "onTagDiscovered: ${tag?.toString()}")
//        IsoDep.get(tag).let {
//            it.connect()
//            // read IsoDep tag from nfc and get value in it
//            Log.d(javaClass.name, "onTagDiscovered: $it")
//            it.close()
//        }
//        MifareClassic.get(tag).let {
//            it.connect()
//            for (i in 0 until it.sectorCount) {
//                val auth = it.authenticateSectorWithKeyA(i, MifareClassic.KEY_DEFAULT)
////                val auth2 = it.authenticateSectorWithKeyB(i, MifareClassic.KEY_NFC_FORUM)
////                val auth3 = it.authenticateSectorWithKeyB(i, MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY)
//                if(auth) {
//                    val bCount = it.getBlockCountInSector(i)
//                    for(j in 0 until bCount) {
//                        val bIndex = it.sectorToBlock(j)
//                        val data = it.readBlock(bIndex)
//                        Log.d(javaClass.name, "onTagDiscovered: ${String(data)}")
//                    }
//                }
//            }
//            it.close()
//        }
//        Ndef.get(tag).let {
//            it.connect()
//            Log.d(javaClass.name, "onTagDiscovered: ${it}")
//            it.close()
//        }
        NfcA.get(tag).let {
            it.connect()
            it.transceive(byteArrayOf(0x30.toByte(), 0x00.toByte())).let {
                Log.d(javaClass.name, "onTagDiscovered: ${it}")
            }
            Log.d(javaClass.name, "onTagDiscovered: ${String(it.atqa, Charset.forName("US-ASCII"))}")
            it.close()
        }
    }

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        Log.d(javaClass.name, "onNewIntent: ${intent}")
//    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val counter = remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
        Text(text = "Count ${counter.value}")
        Button(onClick = {
            counter.value = counter.value.inc()
        }) {
            Text(text = "Click me to increase")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestcomposeTheme {
        Greeting("Android")
    }
}