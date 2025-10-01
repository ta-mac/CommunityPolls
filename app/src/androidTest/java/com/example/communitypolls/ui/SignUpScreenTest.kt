package com.example.communitypolls.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.communitypolls.ui.screens.SignUpScreen
import org.junit.Rule
import org.junit.Test

class SignUpScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private fun fieldLabeled(label: String): SemanticsMatcher =
        hasSetTextAction() and hasAnyDescendant(
            hasText(label, ignoreCase = true, substring = true)
        )

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
    fun createAccount_callsOnSubmit_withEnteredValues() {
        var gotEmail = ""
        var gotPassword = ""
        var gotDisplay = ""
        var submitted = false

        rule.setContent {
            SignUpScreen(
                loading = false,
                error = null,
                onSubmit = { e, p, d ->
                    gotEmail = e
                    gotPassword = p
                    gotDisplay = d
                    submitted = true
                },
                onGoToSignIn = {}
            )
        }

        rule.onNode(fieldLabeled("Email"), useUnmergedTree = true)
            .performTextInput("mac@example.com")

        // Label could be "Password (min 6 chars)" – substring match
        rule.onNode(fieldLabeled("Password"), useUnmergedTree = true)
            .performTextInput("SuperSecret123")

        // Try likely labels; if not found, fall back to the 3rd text field (Email, Password, Display)
        val displayMatcher = fieldLabeled("Display") or fieldLabeled("Name") or fieldLabeled("Username")
        val nodes = rule.onAllNodes(displayMatcher, useUnmergedTree = true).fetchSemanticsNodes()
        if (nodes.isNotEmpty()) {
            rule.onNode(displayMatcher, useUnmergedTree = true).performTextInput("Mac")
        } else {
            val allInputs = rule.onAllNodes(hasSetTextAction(), useUnmergedTree = true)
            allInputs[2].performTextInput("Mac")
        }

        val createBtn = clickableWithText("Create account", "Sign up", "Register")
        rule.onNode(createBtn, useUnmergedTree = true).assertIsEnabled().performClick()

        assert(submitted)
        assert(gotEmail == "mac@example.com")
        assert(gotPassword == "SuperSecret123")
        assert(gotDisplay == "Mac")
    }

    @Test
    fun loading_disablesCreate() {
        rule.setContent {
            SignUpScreen(
                loading = true,
                error = null,
                onSubmit = { _, _, _ -> },
                onGoToSignIn = {}
            )
        }

        val createBtn = clickableWithText("Create account", "Sign up", "Register")
        rule.onNode(createBtn, useUnmergedTree = true).assertIsNotEnabled()
    }

    @Test
    fun error_isRendered() {
        rule.setContent {
            SignUpScreen(
                loading = false,
                error = "Email already in use",
                onSubmit = { _, _, _ -> },
                onGoToSignIn = {}
            )
        }

        rule.onNodeWithText("Email already in use", substring = true, ignoreCase = true)
            .assertIsDisplayed()
    }
}
