package net.igap.ui.loginPage

import android.graphics.Color
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.igap.R
import net.igap.activities.MainActivity
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.core.content.ContextCompat

import android.widget.Button

import android.view.View

import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.Description
import org.hamcrest.Matcher
import android.graphics.drawable.GradientDrawable

import android.graphics.drawable.ColorDrawable

import androidx.core.content.res.ResourcesCompat

import android.content.res.Resources





@RunWith(AndroidJUnit4::class)
class LoginPageTest {

    val context = InstrumentationRegistry.getInstrumentation().context
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

    @Test
    fun onDigitEnteredLoginButtonColorChangesToGreen() {
        onView(withId(R.id.inputPhoneNumberEditTex)).perform(clearText(), typeText("0"))
        onView(withId(R.id.loginButton)).check(matches(matchesBackgroundColor(R.color.button_green)))
    }

    fun matchesBackgroundColor(expectedResourceId: Int): Matcher<View?>? {
        return object : BoundedMatcher<View?, View>(View::class.java) {
            var actualColor = 0
            var expectedColor = 0
            var message: String? = null
            override fun matchesSafely(item: View): Boolean {
                if (item.background == null) {
                    message = item.id.toString() + " does not have a background"
                    return false
                }
                val resources = item.context.resources
                expectedColor = ResourcesCompat.getColor(resources, expectedResourceId, null)
                try {
                    actualColor = (item.background as ColorDrawable).color
                } catch (e: Exception) {
                    actualColor = (item.background as GradientDrawable).color!!.defaultColor
                } finally {
                    if (actualColor == expectedColor) {
                    }
                }
                return actualColor == expectedColor
            }

            override fun describeTo(description: Description) {
                if (actualColor != 0) {
                    message = ("Background color did not match: Expected "
                            + String.format(
                        "#%06X",
                        (0xFFFFFF and expectedColor)
                    ) + " was " + String.format("#%06X", (0xFFFFFF and actualColor)))
                }
                description.appendText(message)
            }
        }
    }
}