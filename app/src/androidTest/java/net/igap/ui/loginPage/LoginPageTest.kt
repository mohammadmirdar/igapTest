package net.igap.ui.loginPage

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import net.igap.MainActivity
import net.igap.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginPageTest {


    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun onLaunchButtonIsDisplayed() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.login_btn)).check(matches(isDisplayed()))
    }

    @Test
    fun onLaunchButtonTextIsLogin() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.login_btn)).check(matches(withText("Login")))
    }
}