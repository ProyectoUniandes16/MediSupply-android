// kotlin
package com.uniandes.medisupply.common

import android.app.Activity
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import io.mockk.Runs
import io.mockk.andThenJust
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class InternalNavigatorTest {

    private lateinit var internalNavigator: InternalNavigator
    private val navigationProvider: NavigationProvider = mockk()
    private val navController: NavController = mockk(relaxed = true)
    private val activity: Activity = mockk()

    @Before
    fun setUp() {
        internalNavigator = InternalNavigatorImpl(navigationProvider)
        internalNavigator.init(navController, activity)
    }

    @Test
    fun `stepBack SHOULD finish activity WHEN there is no previous destination`() {
        every { navController.previousBackStackEntry } returns null
        every { activity.finish() } just Runs

        internalNavigator.stepBack()

        verify { activity.finish() }
    }

    @Test
    fun `stepBack SHOULD pop back stack WHEN there is a previous destination`() {
        every { navController.previousBackStackEntry } returns mockk<NavBackStackEntry>()
        every { navController.popBackStack() } returns true

        internalNavigator.stepBack()

        verify { navController.popBackStack() }
    }

    @Test
    fun `navigateTo SHOULD navigate to destination`() {
        val destination = "destination"
        val params = mapOf("param1" to "value1", "param2" to "value2")

        var capturedDestination: String? = null
        var capturedNavOptions: NavOptions? = null
        var capturedExtras: Navigator.Extras? = null

        every {
            navController.navigate(any() as Any, any(), any())
        } answers {
            capturedDestination = args[0] as String
            capturedNavOptions = args[1] as NavOptions?
            capturedExtras = args[2] as Navigator.Extras?
        } andThenJust Runs

        internalNavigator.navigateTo(destination, params)

        assertEquals(destination, capturedDestination)
        assertNull(capturedNavOptions)
        assertNull(capturedExtras)
    }

    @Test
    fun `addParams SHOULD add parameters and then get params retrive`() {
        val params = mapOf("param1" to "value1", "param2" to 42)

        internalNavigator.addParams(params)

        val param1 = internalNavigator.getParam("param1")
        val param2 = internalNavigator.getParam("param2")

        assertEquals("value1", param1)
        assertEquals(42, param2)
    }

    @Test
    fun `requestDestination SHOULD call navigationProvider requestDestination`() {
        val appDestination = AppDestination.NewClient
        val requestResultCode = 100

        every {
            navigationProvider.requestDestination(appDestination, requestResultCode)
        } just Runs

        internalNavigator.requestDestination(appDestination, requestResultCode)

        verify {
            navigationProvider.requestDestination(appDestination, requestResultCode)
        }
    }

    @Test
    fun `finishCurrentDestination SHOULD call navigationProvider finishCurrentDestination`() {
        val extras = mapOf("result" to "success")
        val success = true
        every {
            navigationProvider.finishCurrentDestination(extras, success)
        } just Runs
        internalNavigator.finishCurrentDestination(extras, success)
        verify {
            navigationProvider.finishCurrentDestination(extras, success)
        }
    }

    @Test
    fun `clear SHOULD nullify navController and activity`() {
        internalNavigator.clear()

        // Using reflection to access private properties for testing
        val navControllerField = InternalNavigatorImpl::class.java.getDeclaredField("navController")
        navControllerField.isAccessible = true
        val activityField = InternalNavigatorImpl::class.java.getDeclaredField("activity")
        activityField.isAccessible = true

        val navControllerValue = navControllerField.get(internalNavigator)
        val activityValue = activityField.get(internalNavigator)

        assertNull(navControllerValue)
        assertNull(activityValue)
    }

    @Test
    fun `getParam SHOULD throw IllegalArgumentException WHEN parameter not found`() {
        val exception = kotlin.runCatching {
            internalNavigator.getParam("non_existent_param")
        }.exceptionOrNull()

        assert(exception is IllegalArgumentException)
        assertEquals("Parameter non_existent_param not found", exception?.message)
    }
}
