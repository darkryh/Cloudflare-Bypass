package com.ead.lib.cloudflare_bypass.core.system.extensions

import android.webkit.WebView

fun WebView.evaluateJavascript(script: String) {
    evaluateJavascript(script, null)
}