package com.ead.lib.cloudflare_bypass

import android.webkit.WebView
import com.ead.lib.cloudflare_bypass.core.Scripts
import com.ead.lib.cloudflare_bypass.core.system.extensions.evaluateJavascript
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@Suppress("DEPRECATION")
@RunWith(RobolectricTestRunner::class)
class BypassClientTest {

    private lateinit var mockWebView: WebView
    private lateinit var bypassClient: BypassClient

    @Before
    fun setUp() {
        mockWebView = mockk(relaxed = true)
        bypassClient = spyk(BypassClient())
    }

    @Test
    fun `test onPageFinished with Cloudflare challenge`() {

        // given
        every { mockWebView.title } returns "Just a moment..."

        // when
        bypassClient.onPageFinished(mockWebView, "https://example.com")

        // then
        verify(exactly = 1) { mockWebView.evaluateJavascript(Scripts.CLOUDFLARE_BYPASS) }
        verify(exactly = 0) { bypassClient.onPageFinishedByPassed(mockWebView, "https://example.com") }
    }

    @Test
    fun `test onPageFinished without Cloudflare challenge`() {

        //given
        every { mockWebView.title } returns "Regular Page Title"

        //when
        bypassClient.onPageFinished(mockWebView, "https://example.com")

        //then
        verify(exactly = 0) { mockWebView.evaluateJavascript(Scripts.CLOUDFLARE_BYPASS) }
        verify(exactly = 1) { bypassClient.onPageFinishedByPassed(mockWebView, "https://example.com") }
    }
}
