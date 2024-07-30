package com.ead.project.cloudflare_bypass

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.ead.lib.cloudflare_bypass.ByPassClient
import com.ead.project.cloudflare_bypass.ui.theme.CloudFlareByPassTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CloudFlareByPassTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WebViewByPass(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewByPass(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
            }
        },
        update = {
            it.webViewClient = object : ByPassClient() {

                override fun onPageFinishedByPassed(view: WebView?, url: String?) {
                    super.onPageFinishedByPassed(view, url)

                    Toast.makeText(view?.context, "ByPassed", Toast.LENGTH_SHORT).show()
                }
            }
            it.loadUrl("https://monoschinos2.com/")
        }
    )
}