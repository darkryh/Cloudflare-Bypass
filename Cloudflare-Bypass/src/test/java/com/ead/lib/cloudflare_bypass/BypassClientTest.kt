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
        verify(exactly = 1) { mockWebView.evaluateJavascript(any<String>()) }
        verify(exactly = 0) { bypassClient.onPageFinishedByPassed(mockWebView, "https://example.com") }
    }

    @Test
    fun `test onPageFinished without Cloudflare challenge`() {

        //given
        every { mockWebView.title } returns "Regular Page Title"

        //when
        bypassClient.onPageFinished(mockWebView, "https://example.com")

        //then
        verify(exactly = 0) { mockWebView.evaluateJavascript(any<String>()) }
        verify(exactly = 1) { bypassClient.onPageFinishedByPassed(mockWebView, "https://example.com") }
    }
    
    @Test
    fun `test onPageFinished with different Cloudflare challenge title`() {
        // given
        every { mockWebView.title } returns "Please wait..."

        // when
        bypassClient.onPageFinished(mockWebView, "https://example.com")

        // then
        verify(exactly = 1) { mockWebView.evaluateJavascript(any<String>()) }
    }
    
    @Test
    fun `test onPageFinished with checking your browser title`() {
        // given
        every { mockWebView.title } returns "Checking your browser..."

        // when
        bypassClient.onPageFinished(mockWebView, "https://example.com")

        // then
        verify(exactly = 1) { mockWebView.evaluateJavascript(any<String>()) }
    }
    
    @Test
    fun `test onPageFinished with null title does not trigger bypass`() {
        // given
        every { mockWebView.title } returns null

        // when
        bypassClient.onPageFinished(mockWebView, "https://example.com")

        // then
        verify(exactly = 0) { mockWebView.evaluateJavascript(any<String>()) }
        verify(exactly = 1) { bypassClient.onPageFinishedByPassed(mockWebView, "https://example.com") }
    }
    
    @Test
    fun `test onPageFinished with empty title does not trigger bypass`() {
        // given
        every { mockWebView.title } returns ""

        // when
        bypassClient.onPageFinished(mockWebView, "https://example.com")

        // then
        verify(exactly = 0) { mockWebView.evaluateJavascript(any<String>()) }
        verify(exactly = 1) { bypassClient.onPageFinishedByPassed(mockWebView, "https://example.com") }
    }
    
    @Test
    fun `test BypassClient can be created with custom timeout`() {
        // given
        val customTimeout = 30L
        
        // when
        val client = BypassClient(bypassTimeoutSeconds = customTimeout)

        // then
        // No exception thrown, client created successfully
        assert(client is BypassClient)
    }
    
    @Test
    fun `test BypassClient can be created with custom polling interval`() {
        // given
        val customPollingInterval = 1000L
        
        // when
        val client = BypassClient(pollingIntervalMs = customPollingInterval)

        // then
        // No exception thrown, client created successfully
        assert(client is BypassClient)
    }
    
    @Test
    fun `test BypassClient can be created with both custom parameters`() {
        // given
        val customTimeout = 20L
        val customPollingInterval = 1500L
        
        // when
        val client = BypassClient(
            bypassTimeoutSeconds = customTimeout,
            pollingIntervalMs = customPollingInterval
        )

        // then
        // No exception thrown, client created successfully
        assert(client is BypassClient)
    }
    
    @Test
    fun `test onPageFinished with null WebView does not crash`() {
        // when
        bypassClient.onPageFinished(null, "https://example.com")

        // then
        // No exception thrown
        verify(exactly = 1) { bypassClient.onPageFinishedByPassed(null, "https://example.com") }
    }
    
    @Test
    fun `test onPageFinished with null URL does not crash`() {
        // given
        every { mockWebView.title } returns "Regular Page Title"

        // when
        bypassClient.onPageFinished(mockWebView, null)

        // then
        // No exception thrown
        verify(exactly = 1) { bypassClient.onPageFinishedByPassed(mockWebView, null) }
    }
    
    @Test
    fun `test onPageFinished with uppercase Cloudflare title`() {
        // given
        every { mockWebView.title } returns "JUST A MOMENT..."

        // when
        bypassClient.onPageFinished(mockWebView, "https://example.com")

        // then
        verify(exactly = 1) { mockWebView.evaluateJavascript(any<String>()) }
    }
}
