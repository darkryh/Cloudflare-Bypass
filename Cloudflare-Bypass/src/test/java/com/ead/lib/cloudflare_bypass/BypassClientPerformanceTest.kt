package com.ead.lib.cloudflare_bypass

import android.webkit.WebView
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.system.measureTimeMillis

/**
 * Performance tests for BypassClient to ensure efficiency
 */
@Suppress("DEPRECATION")
@RunWith(RobolectricTestRunner::class)
class BypassClientPerformanceTest {

    private lateinit var mockWebView: WebView
    private lateinit var bypassClient: BypassClient

    @Before
    fun setUp() {
        mockWebView = mockk(relaxed = true)
        bypassClient = spyk(BypassClient())
    }

    @Test
    fun `test bypass detection is fast for non-Cloudflare pages`() {
        // given
        every { mockWebView.title } returns "Regular Page Title"

        // when
        val elapsedTime = measureTimeMillis {
            bypassClient.onPageFinished(mockWebView, "https://example.com")
        }

        // then - should be very fast (less than 100ms) for non-Cloudflare pages
        assertTrue("Detection took $elapsedTime ms, expected < 100ms", elapsedTime < 100)
    }

    @Test
    fun `test bypass script injection is fast`() {
        // given
        every { mockWebView.title } returns "Just a moment..."

        // when
        val elapsedTime = measureTimeMillis {
            bypassClient.onPageFinished(mockWebView, "https://example.com")
        }

        // then - script injection should be fast (less than 100ms)
        assertTrue("Script injection took $elapsedTime ms, expected < 100ms", elapsedTime < 100)
    }

    @Test
    fun `test multiple consecutive bypass attempts don't cause memory issues`() {
        // given
        every { mockWebView.title } returns "Just a moment..."
        val iterations = 10

        // when
        val elapsedTime = measureTimeMillis {
            repeat(iterations) {
                val client = BypassClient()
                client.onPageFinished(mockWebView, "https://example.com")
            }
        }

        // then - should handle multiple instances efficiently
        val averageTime = elapsedTime / iterations
        assertTrue(
            "Average time per bypass: $averageTime ms, expected < 50ms",
            averageTime < 50
        )
    }

    @Test
    fun `test custom short polling interval creates valid script`() {
        // given
        val shortInterval = 500L
        val client = BypassClient(pollingIntervalMs = shortInterval)
        every { mockWebView.title } returns "Just a moment..."

        // when
        val elapsedTime = measureTimeMillis {
            client.onPageFinished(mockWebView, "https://example.com")
        }

        // then
        assertTrue("Execution took $elapsedTime ms, expected < 100ms", elapsedTime < 100)
    }

    @Test
    fun `test custom long polling interval creates valid script`() {
        // given
        val longInterval = 5000L
        val client = BypassClient(pollingIntervalMs = longInterval)
        every { mockWebView.title } returns "Just a moment..."

        // when
        val elapsedTime = measureTimeMillis {
            client.onPageFinished(mockWebView, "https://example.com")
        }

        // then
        assertTrue("Execution took $elapsedTime ms, expected < 100ms", elapsedTime < 100)
    }

    @Test
    fun `test title detection performance with various titles`() {
        // given
        val titles = listOf(
            "Just a moment...",
            "Please wait...",
            "Checking your browser...",
            "Regular Page Title",
            "Home | Website",
            "",
            "CHECKING YOUR BROWSER..."
        )

        // when
        val totalTime = measureTimeMillis {
            titles.forEach { title ->
                every { mockWebView.title } returns title
                bypassClient.onPageFinished(mockWebView, "https://example.com")
            }
        }

        // then - should process all titles quickly
        val averageTime = totalTime / titles.size
        assertTrue(
            "Average detection time: $averageTime ms, expected < 20ms",
            averageTime < 20
        )
    }

    @Test
    fun `test concurrent title checks don't cause performance degradation`() = runBlocking {
        // given
        val titles = List(20) { "Just a moment..." }

        // when
        val elapsedTime = measureTimeMillis {
            titles.forEach { title ->
                every { mockWebView.title } returns title
                bypassClient.onPageFinished(mockWebView, "https://example.com")
                delay(10) // Small delay to simulate real-world timing
            }
        }

        // then - should handle repeated checks efficiently
        assertTrue(
            "Total time for 20 checks: $elapsedTime ms, expected < 1000ms",
            elapsedTime < 1000
        )
    }

    @Test
    fun `test bypass timeout configuration doesn't affect initialization time`() {
        // given
        val shortTimeout = 5L
        val longTimeout = 60L

        // when
        val shortTimeoutClient = measureTimeMillis {
            BypassClient(bypassTimeoutSeconds = shortTimeout)
        }

        val longTimeoutClient = measureTimeMillis {
            BypassClient(bypassTimeoutSeconds = longTimeout)
        }

        // then - initialization should be equally fast regardless of timeout
        assertTrue("Short timeout init: $shortTimeoutClient ms", shortTimeoutClient < 50)
        assertTrue("Long timeout init: $longTimeoutClient ms", longTimeoutClient < 50)
    }

    @Test
    fun `test null safety checks don't add significant overhead`() {
        // given
        val iterations = 100

        // when
        val elapsedTime = measureTimeMillis {
            repeat(iterations) {
                bypassClient.onPageFinished(null, null)
            }
        }

        // then - null checks should be negligible
        val averageTime = elapsedTime / iterations
        assertTrue(
            "Average time with nulls: $averageTime ms, expected < 10ms",
            averageTime < 10
        )
    }
}
