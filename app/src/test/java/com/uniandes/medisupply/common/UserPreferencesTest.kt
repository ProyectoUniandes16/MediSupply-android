import android.content.Context
import android.content.SharedPreferences
import com.uniandes.medisupply.common.UserPreferences
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class UserPreferencesTest {

    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var userPreferences: UserPreferences

    @Before
    fun setup() {
        UserPreferences.clearInstanceForTests()

        prefs = mockk()
        editor = mockk()
        val context = mockk<Context>()

        every { prefs.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.putBoolean(any(), any()) } returns editor
        every { editor.clear() } returns editor
        every { editor.apply() } just Runs
        every { context.getSharedPreferences(any(), any()) } returns prefs
        every { context.applicationContext } returns context

        userPreferences = UserPreferences.getInstance(context)
    }

    @Test
    fun `save and get access token`() {
        every { prefs.getString("access_token", null) } returns "token_123"

        userPreferences.setAccessToken("token_123")
        val token = userPreferences.getAccessToken()

        assertEquals("token_123", token)
        verify { editor.putString("access_token", "token_123") }
    }

    @Test
    fun `set user logged in`() {
        every { prefs.getBoolean("is_logged_in", false) } returns true

        userPreferences.setLoggedIn(true)
        val loggedIn = userPreferences.isLoggedIn()

        assertTrue(loggedIn)
        verify { editor.putBoolean("is_logged_in", true) }
    }

    @Test
    fun `clean preferences`() {
        userPreferences.clearAll()
        verify { editor.clear(); editor.apply() }
    }

    @Test
    fun `returns null if no access token is set`() {
        every { prefs.getString("access_token", null) } returns null
        assertNull(userPreferences.getAccessToken())
    }

    @Test
    fun `returns false if user is not logged in`() {
        every { prefs.getBoolean("is_logged_in", false) } returns false
        assertFalse(userPreferences.isLoggedIn())
    }
}
