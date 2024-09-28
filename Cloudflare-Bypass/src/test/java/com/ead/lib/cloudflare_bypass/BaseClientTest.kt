package com.ead.lib.cloudflare_bypass

import android.content.Context
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.test.core.app.ApplicationProvider
import com.ead.lib.cloudflare_bypass.model.FakeBaseClient
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.lang.reflect.Field

@RunWith(RobolectricTestRunner::class)
class BaseClientTest {

    private lateinit var webView: WebView
    private lateinit var request: WebResourceRequest

    private lateinit var context : Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        webView = WebView(context)
        request = mockk(relaxed = true)
    }

    @Test
    fun `test to verify when onPageStarted is called the initializeByPass function is called and the onPageStartedPassed function is called`() {

        // given
        val baseClient = FakeBaseClient()

        // when
        baseClient.onPageStarted(webView, "https://example.com", null)

        // then
        assert(baseClient.isInitializeByPassCalled)
        assert(baseClient.isOnPageStartedPassedCalled)
    }

    @Test
    fun `test to verify when onPageStarted is called the assert that onPageStartedPassed is true`() {

        // given
        val client = FakeBaseClient()
        val fieldOnPageStartedPassed = getOnPageStartedField()

        // when
        client.onPageStarted(webView, "https://example.com",null)
        val value = fieldOnPageStartedPassed.get(client) as Boolean

        // then
        assert(value)
    }

    private fun getOnPageStartedField(classToAccess: Class<*> = BaseClient::class.java, nameOfField: String = "onPageStartedPassed"): Field {
        val field = classToAccess.getDeclaredField(nameOfField)
        field.isAccessible = true
        return field
    }

}