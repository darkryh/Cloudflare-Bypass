package com.ead.lib.cloudflare_bypass

import android.webkit.JavascriptInterface
import java.util.concurrent.CountDownLatch

internal class CloudFlareByPassInterface(
    private val countDownLatch: CountDownLatch
) {
    @Suppress("unused")
    @JavascriptInterface
    fun onByPass() = countDownLatch.countDown()
}