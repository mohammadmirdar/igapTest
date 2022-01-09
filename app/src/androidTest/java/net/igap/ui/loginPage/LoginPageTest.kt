package net.igap.ui.loginPage

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.igap.R
import net.igap.activities.MainActivity
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginPageTest {

    @Before
    fun setUp() {
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun onLaunchPrefixPhoneIsDisplayed() {
        onView(withId(R.id.prefixNumberTextView)).check(matches(isDisplayed()))
    }

    @Test
    fun onLaunchPrefixPhoneIsClickable() {
        onView(withId(R.id.prefixNumberTextView)).check(matches(not(isClickable())))
    }

    @Test
    fun onLaunchPhoneEditTextIsDisplayed() {
        onView(withId(R.id.inputPhoneNumberEditTex)).check(matches(isDisplayed()))
    }

    @Test
    fun onLaunchPhoneEditTextIsClickable() {
        onView(withId(R.id.inputPhoneNumberEditTex)).check(matches(isClickable()))
    }

    @Test
    fun onLaunchLoginTextViewIsDisplayed() {
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
    }

    @Test
    fun onLaunchLoginTextViewIsClickable() {
        onView(withId(R.id.loginButton)).check(matches(isClickable()))
    }

    @Test
    fun onLaunchLoginWithTextViewIsDisplayed() {
        onView(withId(R.id.loginWighQrCodeButton)).check(matches(isDisplayed()))
    }

    @Test
    fun onLaunchLoginWithTextViewIsClickable() {
        onView(withId(R.id.loginWighQrCodeButton)).check(matches(isClickable()))
    }
}