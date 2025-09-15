package com.example.communitypolls.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.communitypolls.ui.screens.SignInScreen
import org.junit.Rule
import org.junit.Test

class SignInScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private fun fieldLabeled(label: String): SemanticsMatcher =
        hasSetTextAction() and hasAnyDescendant(
            hasText(label, ignoreCase = true, substring = true)
        )

    // Finds a clickable (Button) whose own text OR any descendant's text matches any label
    private fun clickableWithText(vararg labels: String): SemanticsMatcher {
        var m = hasClickAction() and (
                hasText(labels.first(), ignoreCase = true, substring = true) or
                        hasAnyDescendant(hasText(labels.first(), ignoreCase = true, substring = true))
                )
        for (i in 1 until labels.size) {
            val l = labels[i]
            val candidate = hasClickAction() and (
                    hasText(l, ignoreCase = true, substring = true) or
                            hasAnyDescendant(hasText(l, ignoreCase = true, substring = true))
                    )
            m = m or candidate
        }
        return m
    }

    @Test
    fun signin_callsOnSubmit_withEnteredCredentials() {
        var capturedEmail = ""
        var capturedPassword = ""
        var submitted = false

        rule.setContent {
            SignInScreen(
                loading = false,
                error = null,
                onSubmit = { e, p ->
                    capturedEmail = e
                    capturedPassword = p
                    submitted = true
                },
                onGoToSignUp = {}
            )
        }

        // Use unmerged tree for TextFields
        rule.onNode(fieldLabeled("Email"), useUnmergedTree = true)
            .performTextInput("mac@example.com")
        rule.onNode(fieldLabeled("Password"), useUnmergedTree = true)
            .performTextInput("SuperSecret123")

        // Your app shows "Continue" here; include fallbacks
        val signInBtn = clickableWithText("Continue", "Sign in", "Login")
        rule.onNode(signInBtn, useUnmergedTree = true).assertIsEnabled().performClick()

        assert(submitted)
        assert(capturedEmail == "mac@example.com")
        assert(capturedPassword == "SuperSecret123")
    }

    @Test
    fun loading_disablesContinue() {
        rule.setContent {
            SignInScreen(
                loading = true,
                error = null,
                onSubmit = { _, _ -> },
                onGoToSignUp = {}
            )
        }

        val signInBtn = clickableWithText("Continue", "Sign in", "Login")
        rule.onNode(signInBtn, useUnmergedTree = true).assertIsNotEnabled()
    }

    @Test
    fun error_isRendered() {
        rule.setContent {
            SignInScreen(
                loading = false,
                error = "Invalid credentials",
                onSubmit = { _, _ -> },
                onGoToSignUp = {}
            )
        }

        rule.onNodeWithText("Invalid credentials", substring = true, ignoreCase = true)
            .assertIsDisplayed()
    }
}
