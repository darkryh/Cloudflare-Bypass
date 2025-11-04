package com.ead.lib.cloudflare_bypass

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class CloudFlareByPassInterfaceTest {

    private lateinit var latch: CountDownLatch
    private lateinit var interface_: CloudFlareByPassInterface

    @Before
    fun setUp() {
        latch = CountDownLatch(1)
        interface_ = CloudFlareByPassInterface(latch)
    }

    @Test
    fun `test onByPass decrements latch count`() {
        // given
        val initialCount = latch.count

        // when
        interface_.onByPass()

        // then
        assertEquals(initialCount - 1, latch.count)
    }

    @Test
    fun `test onByPass allows latch to proceed`() {
        // given
        var completed = false

        // when
        Thread {
            interface_.onByPass()
        }.start()

        val result = latch.await(1, TimeUnit.SECONDS)
        completed = true

        // then
        assert(result)
        assert(completed)
    }

    @Test
    fun `test onByPass can be called only once effectively`() {
        // when
        interface_.onByPass()
        interface_.onByPass() // second call should have no effect

        // then
        assertEquals(0, latch.count)
    }

    @Test
    fun `test latch times out without onByPass call`() {
        // when
        val result = latch.await(100, TimeUnit.MILLISECONDS)

        // then
        assert(!result) // should timeout
        assertEquals(1, latch.count)
    }

    @Test
    fun `test interface can be created with different latch counts`() {
        // given
        val multiCountLatch = CountDownLatch(3)
        val multiInterface = CloudFlareByPassInterface(multiCountLatch)

        // when
        multiInterface.onByPass()
        multiInterface.onByPass()
        multiInterface.onByPass()

        // then
        assertEquals(0, multiCountLatch.count)
    }

    @Test
    fun `test multiple interfaces with separate latches`() {
        // given
        val latch1 = CountDownLatch(1)
        val latch2 = CountDownLatch(1)
        val interface1 = CloudFlareByPassInterface(latch1)
        val interface2 = CloudFlareByPassInterface(latch2)

        // when
        interface1.onByPass()

        // then
        assertEquals(0, latch1.count)
        assertEquals(1, latch2.count) // latch2 should not be affected
    }

    @Test
    fun `test onByPass is thread-safe`() {
        // given
        val iterations = 100
        val threads = mutableListOf<Thread>()

        // when
        repeat(iterations) {
            val thread = Thread {
                interface_.onByPass()
            }
            threads.add(thread)
            thread.start()
        }

        threads.forEach { it.join() }

        // then
        assertEquals(0, latch.count) // should be counted down to zero
    }

    @Test
    fun `test interface works with zero-count latch`() {
        // given
        val zeroLatch = CountDownLatch(0)
        val zeroInterface = CloudFlareByPassInterface(zeroLatch)

        // when
        zeroInterface.onByPass()

        // then
        assertEquals(0, zeroLatch.count)
    }
}
