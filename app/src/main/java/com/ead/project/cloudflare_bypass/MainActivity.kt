package com.ead.project.cloudflare_bypass

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.ead.lib.cloudflare_bypass.BypassClient
import com.ead.project.cloudflare_bypass.ui.theme.CloudFlareByPassTheme

@SuppressLint("SetJavaScriptEnabled")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CloudFlareByPassTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AndroidView(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        factory = { context ->
                            WebView(context).apply {
                                id = R.id.test_id_web_view
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                settings.userAgentString = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.6668.69 Mobile Safari/537.36"
                                webViewClient = object : BypassClient() {

                                    override fun onPageStartedPassed(
                                        view: WebView?,
                                        url: String?,
                                        favicon: Bitmap?
                                    ) {
                                        println("onPageStartedPassed")
                                        super.onPageStartedPassed(view, url, favicon)
                                    }

                                    override fun onPageFinishedByPassed(view: WebView?, url: String?) {
                                        super.onPageFinishedByPassed(view, url)
                                        Toast.makeText(context, "Bypass", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                loadUrl("https://ww3.animeonline.ninja/")
                            }
                        }
                    )

                }
            }
        }
    }
}