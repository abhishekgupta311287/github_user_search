package com.abhishekgupta.githubsearch.util

import org.junit.Assert.*
import org.junit.Test

class UtilTest {

    @Test
    fun `is cache expired`() {
        val currentTimeMillis = getCurrentTimeMillis()

        assertFalse(isCacheExpired(currentTimeMillis))

        val timeExpired = currentTimeMillis - 2 * 60 * 61 * 1000

        assertTrue(isCacheExpired(timeExpired))

    }

    @Test
    fun `test current time`() {
        assertNotNull(getCurrentTimeMillis())
    }

}