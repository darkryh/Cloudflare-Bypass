package com.ead.lib.cloudflare_bypass.model

import android.graphics.Bitmap
import android.webkit.WebView
import com.ead.lib.cloudflare_bypass.BaseClient

@Suppress("DEPRECATION")
class FakeBaseClient: BaseClient() {

    var isInitializeByPassCalled = false
    var isOnPageStartedPassedCalled = false


    @Deprecated(
        "Deprecated used onPageStartedPassed instead",
        replaceWith = ReplaceWith("onPageStartedPassed")
    )
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        isInitializeByPassCalled = true
        isOnPageStartedPassedCalled = true
        super.onPageStarted(view, url, favicon)
    }
}