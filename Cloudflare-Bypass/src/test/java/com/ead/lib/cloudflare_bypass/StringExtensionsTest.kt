package com.ead.lib.cloudflare_bypass

import com.ead.lib.cloudflare_bypass.core.system.extensions.isCloudFlareByPassTitle
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StringExtensionsTest {

    @Test
    fun `test isCloudFlareByPassTitle with ellipsis`() {
        // given
        val title = "Just a moment..."

        // when
        val result = title.isCloudFlareByPassTitle()

        // then
        assertTrue(result)
    }

    @Test
    fun `test isCloudFlareByPassTitle with lowercase ellipsis pattern`() {
        // given
        val title = "please wait..."

        // when
        val result = title.isCloudFlareByPassTitle()

        // then
        assertTrue(result)
    }

    @Test
    fun `test isCloudFlareByPassTitle with uppercase just a moment`() {
        // given
        val title = "JUST A MOMENT..."

        // when
        val result = title.isCloudFlareByPassTitle()

        // then
        assertTrue(result)
    }

    @Test
    fun `test isCloudFlareByPassTitle with checking your browser`() {
        // given
        val title = "Checking your browser before accessing example.com"

        // when
        val result = title.isCloudFlareByPassTitle()

        // then
        assertTrue(result)
    }

    @Test
    fun `test isCloudFlareByPassTitle with mixed case`() {
        // given
        val title = "Please Wait Before Accessing Site..."

        // when
        val result = title.isCloudFlareByPassTitle()

        // then
        assertTrue(result)
    }

    @Test
    fun `test isCloudFlareByPassTitle with only ellipsis in middle`() {
        // given
        val title = "Loading... please stand by"

        // when
        val result = title.isCloudFlareByPassTitle()

        // then
        assertTrue(result)
    }

    @Test
    fun `test isCloudFlareByPassTitle returns false for regular page title`() {
        // given
        val title = "Welcome to Example Website"

        // when
        val result = title.isCloudFlareByPassTitle()

        // then
        assertFalse(result)
    }

    @Test
    fun `test isCloudFlareByPassTitle returns false for empty string`() {
        // given
        val title = ""

        // when
        val result = title.isCloudFlareByPassTitle()

        // then
        assertFalse(result)
    }

    @Test
    fun `test isCloudFlareByPassTitle returns false for normal content title`() {
        // given
        val title = "Home | My Website"

        // when
        val result = title.isCloudFlareByPassTitle()

        // then
        assertFalse(result)
    }

    @Test
    fun `test isCloudFlareByPassTitle returns false when similar but different text`() {
        // given
        val title = "A momentous occasion"

        // when
        val result = title.isCloudFlareByPassTitle()

        // then
        assertFalse(result)
    }

    @Test
    fun `test isCloudFlareByPassTitle with double dots but not triple`() {
        // given
        val title = "Loading.."

        // when
        val result = title.isCloudFlareByPassTitle()

        // then
        assertFalse(result)
    }
}
