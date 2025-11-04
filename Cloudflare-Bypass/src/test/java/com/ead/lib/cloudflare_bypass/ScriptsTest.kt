package com.ead.lib.cloudflare_bypass

import com.ead.lib.cloudflare_bypass.core.Scripts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ScriptsTest {

    @Test
    fun `test getCloudflareBypassScript returns non-null script`() {
        // when
        val script = Scripts.getCloudflareBypassScript()

        // then
        assertNotNull(script)
        assertTrue(script.isNotEmpty())
    }

    @Test
    fun `test getCloudflareBypassScript contains CloudFlareByPassInterface call`() {
        // when
        val script = Scripts.getCloudflareBypassScript()

        // then
        assertTrue(script.contains("CloudFlareByPassInterface.onByPass()"))
    }

    @Test
    fun `test getCloudflareBypassScript checks for challenge form`() {
        // when
        val script = Scripts.getCloudflareBypassScript()

        // then
        assertTrue(script.contains("#challenge-form"))
    }

    @Test
    fun `test getCloudflareBypassScript looks for simple challenge button`() {
        // when
        val script = Scripts.getCloudflareBypassScript()

        // then
        assertTrue(script.contains("#challenge-stage > div > input[type='button']"))
    }

    @Test
    fun `test getCloudflareBypassScript looks for turnstile captcha`() {
        // when
        val script = Scripts.getCloudflareBypassScript()

        // then
        assertTrue(script.contains("div.hcaptcha-box > iframe"))
    }

    @Test
    fun `test getCloudflareBypassScript uses default polling interval`() {
        // when
        val script = Scripts.getCloudflareBypassScript()

        // then
        assertTrue(script.contains("2500"))
    }

    @Test
    fun `test getCloudflareBypassScript with custom polling interval`() {
        // given
        val customInterval = 1000L

        // when
        val script = Scripts.getCloudflareBypassScript(customInterval)

        // then
        assertTrue(script.contains("1000"))
        assertFalse(script.contains("2500"))
    }

    @Test
    fun `test getCloudflareBypassScript with very short polling interval`() {
        // given
        val shortInterval = 500L

        // when
        val script = Scripts.getCloudflareBypassScript(shortInterval)

        // then
        assertTrue(script.contains("500"))
    }

    @Test
    fun `test getCloudflareBypassScript with very long polling interval`() {
        // given
        val longInterval = 5000L

        // when
        val script = Scripts.getCloudflareBypassScript(longInterval)

        // then
        assertTrue(script.contains("5000"))
    }

    @Test
    fun `test getCloudflareBypassScript includes clearInterval for cleanup`() {
        // when
        val script = Scripts.getCloudflareBypassScript()

        // then
        assertTrue(script.contains("clearInterval"))
    }

    @Test
    fun `test getCloudflareBypassScript has max attempts limit`() {
        // when
        val script = Scripts.getCloudflareBypassScript()

        // then
        assertTrue(script.contains("maxAttempts"))
        assertTrue(script.contains("attemptCount"))
    }

    @Test
    fun `test getCloudflareBypassScript wraps code in IIFE`() {
        // when
        val script = Scripts.getCloudflareBypassScript()

        // then
        assertTrue(script.startsWith("(function()"))
        assertTrue(script.contains("})()"))
    }

    @Test
    fun `test getCloudflareBypassScript has error handling for cross-origin iframe`() {
        // when
        val script = Scripts.getCloudflareBypassScript()

        // then
        assertTrue(script.contains("try"))
        assertTrue(script.contains("catch"))
    }

    @Test
    fun `test CLOUDFLARE_BYPASS constant is not empty`() {
        // when
        @Suppress("DEPRECATION")
        val script = Scripts.CLOUDFLARE_BYPASS

        // then
        assertNotNull(script)
        assertTrue(script.isNotEmpty())
    }

    @Test
    fun `test CLOUDFLARE_BYPASS uses default interval for backward compatibility`() {
        // when
        @Suppress("DEPRECATION")
        val legacyScript = Scripts.CLOUDFLARE_BYPASS
        val newScript = Scripts.getCloudflareBypassScript()

        // then - both should be equivalent
        assertEquals(legacyScript, newScript)
    }

    @Test
    fun `test DEFAULT_POLLING_INTERVAL constant value`() {
        // when/then
        assertEquals(2500L, Scripts.DEFAULT_POLLING_INTERVAL)
    }

    @Test
    fun `test getCloudflareBypassScript checks for contentWindow before access`() {
        // when
        val script = Scripts.getCloudflareBypassScript()

        // then
        assertTrue(script.contains("contentWindow"))
    }

    @Test
    fun `test getCloudflareBypassScript includes attemptCount tracking`() {
        // when
        val script = Scripts.getCloudflareBypassScript()

        // then
        assertTrue(script.contains("attemptCount++"))
    }

    @Test
    fun `test getCloudflareBypassScript stops after max attempts`() {
        // when
        val script = Scripts.getCloudflareBypassScript()

        // then
        assertTrue(script.contains("if (attemptCount > maxAttempts)"))
    }
}
