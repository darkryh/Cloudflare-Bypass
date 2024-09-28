
[![](https://jitpack.io/v/darkryh/Cloudflare-Bypass.svg)](https://jitpack.io/#darkryh/Cloudflare-Bypass)  
![CI](https://github.com/darkryh/Cloudflare-Bypass/actions/workflows/ci-develop.yml/badge.svg)  
![CI](https://github.com/darkryh/Cloudflare-Bypass/actions/workflows/ci-develop-instrumental.yml/badge.svg)  
![CI](https://github.com/darkryh/Cloudflare-Bypass/actions/workflows/ci-release-production.yml/badge.svg)

# Cloudflare-Bypass

> **Note:** For devices running Android API level 30 or lower, it is recommended to specify a custom `userAgent` in the `WebView` settings to prevent being blocked by outdated browser warnings.

Cloudflare-Bypass is an Android library designed to seamlessly bypass Cloudflare's anti-bot protection using a custom `WebViewClient`. This library allows developers to load websites protected by Cloudflare's challenge without needing manual user intervention.

## Features

- Custom `WebViewClient` for Cloudflare bypass.
- Automatic handling of Cloudflare's anti-bot checks.
- Easy integration into any Android project with minimal setup.
- Supports bypass for sites using Cloudflare protection.

## Requirements

- **Minimum SDK:** 21 (Android 5.0 Lollipop)
- **Compile SDK:** 34
- **Target SDK:** 34
- **Language:** Kotlin

## Getting Started

#### Add the JitPack repository to your root `build.gradle` at the end of the repositories section:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

## Installation - Gradle
```gradle
dependencies {  
    implementation("com.github.darkryh:Cloudflare-Bypass:$version")
}
```

# Example of Implementation

```kotlin
@Composable
fun ComposableWebView(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                webViewClient = BypassClient()
            }
        }
    )
}
```

# Replacement options for override clients
Options available to the client. The other settings remain unchanged.

```kotlin
@SuppressLint("SetJavaScriptEnabled")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CloudFlareByPassTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AndroidView(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        factory = { context ->
                            WebView(context).apply {
                                settings.javaScriptEnabled = true
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
                                loadUrl("Your target site")
                            }
                        }
                    )
                }
            }
        }
    }
}
```

# Want to collaborate

If you want to help or collaborate, feel free to contact me on X (Twitter) account @Darkryh or just make a pull request.
