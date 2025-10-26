package com.uniandes.medisupply.common

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ResourceProviderTest {
    private lateinit var resourceProvider: ResourcesProvider
    private val context: Context = mockk(relaxed = true)

    @Before
    fun setUp() {
        resourceProvider = ResourcesProviderImpl(context)
    }

    @Test
    fun `get string SHOULD return correct string from resources`() {
        // Given
        val stringResId = 1000
        val expectedString = "Test String"
        // match getString(resId) and getString(resId, *args)
        every { context.getString(stringResId, *anyVararg()) } returns expectedString

        // When
        val result = resourceProvider.getString(stringResId)

        // Then
        // assertEquals expects (expected, actual)
        assertEquals(expectedString, result)
    }
}
