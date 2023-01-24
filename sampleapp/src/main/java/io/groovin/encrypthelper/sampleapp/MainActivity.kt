package io.groovin.encrypthelper.sampleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.groovin.encrypthelper.EncryptHelper
import io.groovin.encrypthelper.sampleapp.theme.SampleAppTheme

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleAppTheme {
                Screen()
            }
        }
    }
}

@Composable
fun Screen() {
    val encryptHelper = remember { EncryptHelper("sample_app_key_alias") }
    var originalText by remember { mutableStateOf("") }
    var encryptText by remember { mutableStateOf("") }
    var decryptText by remember { mutableStateOf("") }
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = originalText,
                onValueChange = { originalText = if (it.length < 255) it else it.substring(0, 255) }
            )
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { encryptText = encryptHelper.toEncrypt(originalText) }
            ) {
                Text(text = "to Encrypt")
            }
            Text(text = "Encrypt :\n$encryptText")
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                enabled = (encryptText.isNotEmpty()),
                onClick = { decryptText = encryptHelper.toDecrypt(encryptText) }
            ) {
                Text(text = "to Decrypt")
            }
            Text(text = "Decrypt :\n$decryptText")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SampleAppTheme {
        Screen()
    }
}
