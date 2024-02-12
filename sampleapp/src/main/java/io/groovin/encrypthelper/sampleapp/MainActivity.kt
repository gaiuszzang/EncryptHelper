package io.groovin.encrypthelper.sampleapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import io.groovin.encrypthelper.EncryptHelper
import io.groovin.encrypthelper.KeyType
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
    val context = LocalContext.current
    val encryptHelper = remember { EncryptHelper("sample_app_key_alias_4096", KeyType.RSA_ECB_PKCS1_4096) }
    var originalText by remember { mutableStateOf("") }
    var encryptText by remember { mutableStateOf("") }
    var decryptText by remember { mutableStateOf("") }

    fun encryptText() {
        try {
            encryptText = encryptHelper.toEncrypt(originalText)
        } catch (e: Exception) {
            encryptText = ""
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = originalText,
                placeholder = {
                    Text("Please input the plain text here.")
                },
                onValueChange = { originalText = it }
            )
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    encryptText()
                }
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
