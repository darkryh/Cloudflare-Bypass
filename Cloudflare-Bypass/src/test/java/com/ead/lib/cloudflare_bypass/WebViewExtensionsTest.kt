package com.ead.lib.cloudflare_bypass

import android.content.Context
import android.webkit.WebView
import androidx.test.core.app.ApplicationProvider
import com.ead.lib.cloudflare_bypass.core.system.extensions.evaluateJavascript
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WebViewExtensionsTest {

    private lateinit var mockWebView: WebView
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        mockWebView = mockk(relaxed = true)
    }

    @Test
    fun `test evaluateJavascript extension calls WebView method with null callback`() {
        // given
        val script = "console.log('test')"

        // when
        mockWebView.evaluateJavascript(script)

        // then
        verify { mockWebView.evaluateJavascript(script, null) }
    }

    @Test
    fun `test evaluateJavascript with simple script`() {
        // given
        val script = "1 + 1"

        // when
        mockWebView.evaluateJavascript(script)

        // then
        verify(exactly = 1) { mockWebView.evaluateJavascript(script, null) }
    }

    @Test
    fun `test evaluateJavascript with complex script`() {
        // given
        val script = """
            (function() {
                const result = document.querySelector('#test');
                return result ? result.textContent : null;
            })()
        """.trimIndent()

        // when
        mockWebView.evaluateJavascript(script)

        // then
        verify(exactly = 1) { mockWebView.evaluateJavascript(script, null) }
    }

    @Test
    fun `test evaluateJavascript with empty script`() {
        // given
        val script = ""

        // when
        mockWebView.evaluateJavascript(script)

        // then
        verify(exactly = 1) { mockWebView.evaluateJavascript(script, null) }
    }

    @Test
    fun `test evaluateJavascript with multi-line script`() {
        // given
        val script = """
            console.log('line 1');
            console.log('line 2');
            console.log('line 3');
        """.trimIndent()

        // when
        mockWebView.evaluateJavascript(script)

        // then
        verify(exactly = 1) { mockWebView.evaluateJavascript(script, null) }
    }

    @Test
    fun `test evaluateJavascript with script containing quotes`() {
        // given
        val script = """console.log("Hello 'World'")"""

        // when
        mockWebView.evaluateJavascript(script)

        // then
        verify(exactly = 1) { mockWebView.evaluateJavascript(script, null) }
    }

    @Test
    fun `test evaluateJavascript with script containing special characters`() {
        // given
        val script = "console.log('Special: !@#$%^&*()')"

        // when
        mockWebView.evaluateJavascript(script)

        // then
        verify(exactly = 1) { mockWebView.evaluateJavascript(script, null) }
    }

    @Test
    fun `test evaluateJavascript can be called multiple times`() {
        // given
        val scripts = listOf(
            "console.log('test 1')",
            "console.log('test 2')",
            "console.log('test 3')"
        )

        // when
        scripts.forEach { script ->
            mockWebView.evaluateJavascript(script)
        }

        // then
        scripts.forEach { script ->
            verify(exactly = 1) { mockWebView.evaluateJavascript(script, null) }
        }
    }

    @Test
    fun `test evaluateJavascript with real WebView does not crash`() {
        // given
        val realWebView = WebView(context)
        val script = "1 + 1"

        // when/then - should not crash
        realWebView.evaluateJavascript(script)
    }

    @Test
    fun `test evaluateJavascript with very long script`() {
        // given
        val longScript = "console.log('${"x".repeat(10000)}')"

        // when
        mockWebView.evaluateJavascript(longScript)

        // then
        verify(exactly = 1) { mockWebView.evaluateJavascript(longScript, null) }
    }
}
