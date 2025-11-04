# Troubleshooting Guide

## Common Issues and Solutions

### 1. Bypass Not Working

#### Symptoms
- WebView stays on Cloudflare challenge page
- `onPageFinishedByPassed` never called
- Challenge page doesn't automatically click through

#### Possible Causes and Solutions

**A. Cloudflare challenge type not supported**

Some challenges require human interaction (like image selection). The library handles:
- ✅ Simple "click to continue" buttons
- ✅ Turnstile/hCaptcha checkboxes (when accessible)
- ❌ Image-based CAPTCHA
- ❌ Audio challenges
- ❌ Advanced bot detection requiring mouse movement

**Solution**: Implement timeout handler and prompt user:
```kotlin
val client = object : BypassClient() {
    override fun onBypassTimeout(view: WebView?, url: String?) {
        // Show message to user
        runOnUiThread {
            Toast.makeText(context, 
                "Please complete the challenge manually", 
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
```

**B. Timeout too short**

Default 15-second timeout might not be enough for slow connections or complex challenges.

**Solution**: Increase timeout:
```kotlin
val client = BypassClient(bypassTimeoutSeconds = 30L)
```

**C. JavaScript disabled**

WebView must have JavaScript enabled for bypass to work.

**Solution**: Enable JavaScript:
```kotlin
webView.settings.javaScriptEnabled = true
```

**D. WebView not properly initialized**

The bypass interface must be added before challenge page loads.

**Solution**: Ensure client is set before loading URL:
```kotlin
webView.webViewClient = BypassClient()
webView.loadUrl(url) // Load after setting client
```

### 2. Timeout Issues

#### Symptoms
- `onBypassTimeout` called frequently
- Bypass takes longer than expected

#### Solutions

**A. Network latency**

Slow connections need more time.

**Solution**: Increase both timeout and polling interval:
```kotlin
val client = BypassClient(
    bypassTimeoutSeconds = 30L,
    pollingIntervalMs = 3000L
)
```

**B. Cloudflare changed their implementation**

Cloudflare regularly updates their challenge page structure.

**Solution**: Check if selectors in the bypass script match current Cloudflare pages. Update if needed or report an issue on GitHub.

**C. Multiple redirects**

Page might go through multiple Cloudflare challenges.

**Solution**: Monitor page navigation:
```kotlin
val client = object : BypassClient() {
    override fun onPageStartedPassed(view: WebView?, url: String?, favicon: Bitmap?) {
        Log.d("Bypass", "Page started: $url")
    }
    
    override fun onPageFinishedByPassed(view: WebView?, url: String?) {
        Log.d("Bypass", "Page finished: $url")
    }
}
```

### 3. Memory Issues

#### Symptoms
- App crashes with OutOfMemoryError
- Memory leaks detected
- App becomes slow over time

#### Solutions

**A. Not destroying WebView**

WebViews consume significant memory and must be destroyed.

**Solution**: Properly destroy WebView:
```kotlin
override fun onDestroy() {
    webView.stopLoading()
    webView.webViewClient = null
    webView.destroy()
    super.onDestroy()
}
```

**B. Holding WebView references**

Avoid holding references to WebView longer than needed.

**Solution**: Use weak references or null them out:
```kotlin
private var webView: WebView? = null

override fun onDestroy() {
    webView?.destroy()
    webView = null // Release reference
    super.onDestroy()
}
```

**C. Too many concurrent WebViews**

Multiple WebViews with bypass clients increase memory usage.

**Solution**: Limit concurrent WebViews and reuse when possible.

### 4. Performance Issues

#### Symptoms
- High CPU usage
- Battery drain
- UI lag

#### Solutions

**A. Polling interval too short**

Fast polling (< 1000ms) increases CPU usage.

**Solution**: Use longer polling for non-critical operations:
```kotlin
val client = BypassClient(pollingIntervalMs = 3000L)
```

**B. Too many WebViews running simultaneously**

Each WebView with active bypass consumes resources.

**Solution**: Queue WebView operations or limit concurrency:
```kotlin
// Example: Process one at a time
val queue = mutableListOf<String>()
var isProcessing = false

fun loadNext() {
    if (!isProcessing && queue.isNotEmpty()) {
        isProcessing = true
        val url = queue.removeAt(0)
        webView.loadUrl(url)
    }
}

val client = object : BypassClient() {
    override fun onPageFinishedByPassed(view: WebView?, url: String?) {
        isProcessing = false
        loadNext()
    }
}
```

### 5. Detection Issues

#### Symptoms
- Regular pages trigger bypass unnecessarily
- Cloudflare pages not detected

#### Solutions

**A. False positive detection**

Pages with "..." in title incorrectly identified as Cloudflare.

**Solution**: The improved detection now checks multiple patterns. If still an issue, override detection:
```kotlin
class CustomBypassClient : BypassClient() {
    @Deprecated("Deprecated, use onPageFinishedByPassed instead")
    override fun onPageFinished(view: WebView?, url: String?) {
        // Custom detection logic
        val title = view?.title ?: ""
        if (isActualCloudflareChallenge(title)) {
            // Manually trigger bypass
            view?.evaluateJavascript(Scripts.getCloudflareBypassScript())
            // ... handle response
        } else {
            onPageFinishedByPassed(view, url)
        }
    }
    
    private fun isActualCloudflareChallenge(title: String): Boolean {
        // Your custom logic
        return title == "Just a moment..." || title == "Please wait..."
    }
}
```

**B. Cloudflare page not detected**

New Cloudflare title format not recognized.

**Solution**: Report the new title format on GitHub. Temporary workaround:
```kotlin
fun String.isCloudflareChallenge(): Boolean {
    return this.contains("just a moment", ignoreCase = true) ||
           this.contains("please wait", ignoreCase = true) ||
           this.contains("checking your browser", ignoreCase = true) ||
           this.contains("...") ||
           this.contains("your new title pattern here")
}
```

### 6. Android API Level Issues

#### Symptoms
- Bypass fails on older Android versions
- "Outdated browser" warnings

#### Solutions

**A. Outdated User-Agent**

Android API ≤ 30 may have outdated WebView user agents.

**Solution**: Set custom user agent (as noted in README):
```kotlin
// Use a recent Chrome version user agent
// Update the version numbers periodically to match current Chrome releases
// Check https://www.whatismybrowser.com/guides/the-latest-user-agent/ for latest
webView.settings.userAgentString = "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"

// Or dynamically get current user agent and update version
val currentUA = webView.settings.userAgentString
val updatedUA = currentUA.replace(Regex("Chrome/\\d+\\."), "Chrome/120.")
webView.settings.userAgentString = updatedUA
```

**Note**: The Chrome version number (120.0.0.0 in the example) should be updated periodically. Check the [latest Chrome versions](https://www.whatismybrowser.com/guides/the-latest-user-agent/) and adjust accordingly.

**B. WebView version too old**

Old WebView versions may not support required JavaScript features.

**Solution**: Check WebView version and prompt update:
```kotlin
val webViewPackage = WebView.getCurrentWebViewPackage()
val version = webViewPackage?.versionName
Log.d("WebView", "Version: $version")

// Minimum recommended: Chrome 90+
```

### 7. Testing Issues

#### Symptoms
- Tests fail sporadically
- Mocked WebView doesn't work as expected
- Timeout in tests

#### Solutions

**A. Asynchronous operations**

Bypass uses coroutines which are asynchronous.

**Solution**: Use proper test synchronization:
```kotlin
@Test
fun testBypass() = runBlocking {
    // Test code with coroutines
    delay(100) // Allow async operations to complete
}
```

**B. Robolectric configuration**

Tests need proper Robolectric setup.

**Solution**: Ensure proper test runner:
```kotlin
@RunWith(RobolectricTestRunner::class)
class YourTest {
    // Tests
}
```

### 8. Integration Issues

#### Symptoms
- Bypass works in isolation but fails in app
- Conflicts with other WebViewClient functionality

#### Solutions

**A. Multiple WebViewClient**

You can only set one WebViewClient per WebView.

**Solution**: Extend BypassClient to add your functionality:
```kotlin
class MyCustomClient : BypassClient() {
    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        // Your custom logic
        return super.shouldInterceptRequest(view, request)
    }
    
    override fun onPageFinishedByPassed(view: WebView?, url: String?) {
        super.onPageFinishedByPassed(view, url)
        // Your custom logic after bypass
    }
}
```

**B. Compose integration issues**

AndroidView composition requires proper lifecycle handling.

**Solution**: Use proper Compose lifecycle:
```kotlin
@Composable
fun WebViewComposable() {
    val context = LocalContext.current
    var webView: WebView? by remember { mutableStateOf(null) }
    
    DisposableEffect(Unit) {
        onDispose {
            webView?.destroy()
        }
    }
    
    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                webView = this
                webViewClient = BypassClient()
            }
        }
    )
}
```

## Debugging Tips

### Enable Logging

Add logging to track bypass behavior:

```kotlin
class LoggingBypassClient : BypassClient() {
    override fun onPageStartedPassed(view: WebView?, url: String?, favicon: Bitmap?) {
        Log.d("Bypass", "Started: $url, Title: ${view?.title}")
    }
    
    override fun onPageFinishedByPassed(view: WebView?, url: String?) {
        Log.d("Bypass", "Finished: $url, Title: ${view?.title}")
    }
    
    override fun onBypassTimeout(view: WebView?, url: String?) {
        Log.w("Bypass", "Timeout: $url, Title: ${view?.title}")
    }
}
```

### Inspect WebView Console

Enable WebView debugging:

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
    WebView.setWebContentsDebuggingEnabled(true)
}
```

Then use Chrome DevTools (chrome://inspect) to see console messages and debug JavaScript.

### Test with Known Cloudflare Sites

Test with sites known to use Cloudflare:
- Various sites behind Cloudflare protection
- Create test cases with actual Cloudflare-protected URLs

### Check JavaScript Execution

Verify JavaScript is running:

```kotlin
webView.evaluateJavascript("'test'") { result ->
    Log.d("JS", "JavaScript enabled: $result")
}
```

## Getting Help

If you've tried these solutions and still have issues:

1. **Check existing issues**: https://github.com/darkryh/Cloudflare-Bypass/issues
2. **Create detailed bug report** including:
   - Android version and device
   - WebView version
   - Code snippet showing the issue
   - Logs and error messages
   - Steps to reproduce
3. **Contact maintainer**: @Darkryh on Twitter/X

## Related Documentation

- [README.md](README.md) - Basic usage and setup
- [PERFORMANCE.md](PERFORMANCE.md) - Performance optimization guide
- [Example app](app/) - Sample implementation
