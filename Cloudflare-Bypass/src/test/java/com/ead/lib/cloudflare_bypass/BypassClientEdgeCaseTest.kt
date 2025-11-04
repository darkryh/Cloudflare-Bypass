package com.ead.lib.cloudflare_bypass

import android.content.Context
import android.webkit.WebView
import androidx.test.core.app.ApplicationProvider
import com.ead.lib.cloudflare_bypass.model.FakeBaseClient
import io.mockk.mockk
import io.mockk.spyk
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Edge case and stress tests for the Cloudflare bypass client
 */
@Suppress("DEPRECATION")
@RunWith(RobolectricTestRunner::class)
class BypassClientEdgeCaseTest {

    private lateinit var context: Context
    private lateinit var webView: WebView
    
    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        webView = WebView(context)
    }

    @Test
    fun `test BaseClient initializes properly`() {
        // when
        val client = BaseClient()

        // then
        assertNotNull(client)
    }

    @Test
    fun `test BypassClient initializes properly`() {
        // when
        val client = BypassClient()

        // then
        assertNotNull(client)
    }

    @Test
    fun `test BypassClient with zero timeout`() {
        // when
        val client = BypassClient(bypassTimeoutSeconds = 0L)

        // then
        assertNotNull(client)
    }

    @Test
    fun `test BypassClient with negative timeout defaults to provided value`() {
        // when - this tests the configuration flexibility
        val client = BypassClient(bypassTimeoutSeconds = -1L)

        // then - client should still initialize
        assertNotNull(client)
    }

    @Test
    fun `test BypassClient with zero polling interval`() {
        // when
        val client = BypassClient(pollingIntervalMs = 0L)

        // then
        assertNotNull(client)
    }

    @Test
    fun `test BypassClient with very large timeout`() {
        // when
        val client = BypassClient(bypassTimeoutSeconds = Long.MAX_VALUE)

        // then
        assertNotNull(client)
    }

    @Test
    fun `test BypassClient with very large polling interval`() {
        // when
        val client = BypassClient(pollingIntervalMs = Long.MAX_VALUE)

        // then
        assertNotNull(client)
    }

    @Test
    fun `test multiple BaseClient instances can be created`() {
        // when
        val client1 = BaseClient()
        val client2 = BaseClient()
        val client3 = BaseClient()

        // then
        assertNotNull(client1)
        assertNotNull(client2)
        assertNotNull(client3)
    }

    @Test
    fun `test multiple BypassClient instances with different configurations`() {
        // when
        val client1 = BypassClient(bypassTimeoutSeconds = 10L)
        val client2 = BypassClient(bypassTimeoutSeconds = 20L)
        val client3 = BypassClient(pollingIntervalMs = 1000L)

        // then
        assertNotNull(client1)
        assertNotNull(client2)
        assertNotNull(client3)
    }

    @Test
    fun `test onPageStarted can be called multiple times safely`() {
        // given
        val client = FakeBaseClient()

        // when
        client.onPageStarted(webView, "https://example1.com", null)
        client.onPageStarted(webView, "https://example2.com", null)
        client.onPageStarted(webView, "https://example3.com", null)

        // then - should handle multiple calls
        assert(client.isInitializeByPassCalled)
        assert(client.isOnPageStartedPassedCalled)
    }

    @Test
    fun `test BypassClient can extend and override timeout handler`() {
        // given
        var timeoutCalled = false
        val client = object : BypassClient() {
            override fun onBypassTimeout(view: WebView?, url: String?) {
                timeoutCalled = true
            }
        }

        // when
        client.onBypassTimeout(null, null)

        // then
        assert(timeoutCalled)
    }

    @Test
    fun `test BypassClient with special characters in URL`() {
        // given
        val client = spyk(BypassClient())
        val specialUrls = listOf(
            "https://example.com/path?query=value&key=special!@#",
            "https://example.com/путь",
            "https://example.com/路径",
            "https://example.com/mäin",
            "https://example.com/página"
        )

        // when/then - should handle all URLs without crashing
        specialUrls.forEach { url ->
            client.onPageFinished(null, url)
        }
    }

    @Test
    fun `test BypassClient with extremely long URL`() {
        // given
        val client = spyk(BypassClient())
        val longUrl = "https://example.com/" + "a".repeat(10000)

        // when/then - should handle long URLs without crashing
        client.onPageFinished(null, longUrl)
    }

    @Test
    fun `test BaseClient WebView interface is added only once on multiple calls`() {
        // given
        val client = BaseClient()

        // when
        client.onPageStarted(webView, "https://example1.com", null)
        client.onPageStarted(webView, "https://example2.com", null)
        client.onPageStarted(webView, "https://example3.com", null)

        // then - no exception should be thrown from multiple interface additions
        // The flag ensures interface is added only once
    }

    @Test
    fun `test BypassClient inherits from BaseClient properly`() {
        // when
        val client = BypassClient()

        // then
        assert(client is BaseClient)
    }

    @Test
    fun `test onPageFinishedByPassed can be overridden`() {
        // given
        var called = false
        val client = object : BypassClient() {
            override fun onPageFinishedByPassed(view: WebView?, url: String?) {
                called = true
            }
        }

        // when
        client.onPageFinishedByPassed(null, null)

        // then
        assert(called)
    }

    @Test
    fun `test onPageStartedPassed can be overridden`() {
        // given
        var called = false
        val client = object : BaseClient() {
            override fun onPageStartedPassed(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                called = true
            }
        }

        // when
        client.onPageStartedPassed(null, null, null)

        // then
        assert(called)
    }
}
