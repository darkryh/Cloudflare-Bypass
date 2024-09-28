package com.ead.project.cloudflare_bypass

import android.webkit.WebView
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.web.assertion.WebViewAssertions.webContent
import androidx.test.espresso.web.matcher.DomMatchers.hasElementWithXpath
import androidx.test.espresso.web.sugar.Web.onWebView
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class WebViewImplementationTest {

    private val titleVerifier = "Ver Anime | Online Sin Censura Gratis HD Sub Español Latino"

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        onWebView().forceJavascriptEnabled()
    }

    @Test
    fun checkWebViewInAdBlockTesterByCheckingFinalScoreAboveTheMinimum() {
        val webView = composeTestRule.activity.findViewById<WebView>(R.id.test_id_web_view)
        val idlingResource = WebViewIdlingResource(webView,titleVerifier)

        idlingResource.verifier.start()

        runBlocking {
            if (!idlingResource.latch.await(30, TimeUnit.SECONDS)) {
                throw TimeoutException("Timeout waiting for WebView to check final score class")
            }
        }

        idlingResource.verifier.cancel()

        onWebView(withId(R.id.test_id_web_view))
            .check(webContent(hasElementWithXpath("//title[not(contains(text(), '...'))]")))

    }
}
