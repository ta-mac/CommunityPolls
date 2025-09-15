package com.example.communitypolls.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import com.example.communitypolls.AuthVmFactory
import com.example.communitypolls.ui.auth.AuthViewModel
import com.example.communitypolls.ui.polls.PollEditRoute
import com.example.communitypolls.ui.polls.PollEditorRoute
import com.example.communitypolls.ui.polls.PollResultsRoute
import com.example.communitypolls.ui.polls.PollVoteRoute
import com.example.communitypolls.ui.screens.*

sealed class Route(val route: String) {
    object Welcome : Route("welcome")
    object SignIn : Route("signin")
    object SignUp : Route("signup")
    object HomeGuest : Route("home_guest")
    object HomeUser : Route("home_user")
    object HomeAdmin : Route("home_admin")
    object PollCreate : Route("poll_create")
    object PollVote : Route("poll_vote/{pollId}")
    object PollResults : Route("poll_results/{pollId}")
    object PollEdit : Route("poll_edit/{pollId}")
}

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val authVm: AuthViewModel = viewModel(factory = AuthVmFactory())
    val state by authVm.state.collectAsState()
    val user = state.user

    NavHost(navController = nav, startDestination = Route.Welcome.route) {

        composable(Route.Welcome.route) {
            WelcomeScreen(
                loading = state.loading,
                error = state.error,
                onSignInClick = { nav.navigate(Route.SignIn.route) },
                onSignUpClick = { nav.navigate(Route.SignUp.route) },
                onGuestClick = { authVm.signInGuest() },
                user = user,
                onEnter = { navigateToRoleHome(nav, user?.role ?: "guest") }
            )
        }

        composable(Route.SignIn.route) {
            LaunchedEffect(user) { if (user != null) navigateToRoleHome(nav, user.role) }
            SignInScreen(
                loading = state.loading,
                error = state.error,
                onSubmit = authVm::signIn,
                onGoToSignUp = { nav.navigate(Route.SignUp.route) }
            )
        }

        composable(Route.SignUp.route) {
            LaunchedEffect(user) { if (user != null) navigateToRoleHome(nav, user.role) }
            SignUpScreen(
                loading = state.loading,
                error = state.error,
                onSubmit = authVm::signUp,
                onGoToSignIn = { nav.navigate(Route.SignIn.route) }
            )
        }

        composable(Route.HomeGuest.route) {
            HomeGuestScreen(
                onSignOut = {
                    authVm.signOut()
                    nav.navigate(Route.Welcome.route) { popUpTo(Route.Welcome.route) { inclusive = true } }
                },
                onPollClick = { id -> nav.navigate("poll_vote/$id") }
            )
        }

        composable(Route.HomeUser.route) {
            HomeUserScreen(
                onSignOut = {
                    authVm.signOut()
                    nav.navigate(Route.Welcome.route) { popUpTo(Route.Welcome.route) { inclusive = true } }
                },
                onPollClick = { id -> nav.navigate("poll_vote/$id") }
            )
        }

        composable(Route.HomeAdmin.route) {
            HomeAdminScreen(
                onCreatePoll = {
                    nav.navigate(Route.PollCreate.route) {
                        launchSingleTop = true
                        restoreState = false
                    }
                },
                onSignOut = {
                    authVm.signOut()
                    nav.navigate(Route.Welcome.route) { popUpTo(Route.Welcome.route) { inclusive = true } }
                },
                onPollClick = { id -> nav.navigate("poll_vote/$id") },
                onEditPoll = { id -> nav.navigate("poll_edit/$id") }
            )
        }

        // Create Poll (admin only) — WAIT for user, don't pop when user==null
        composable(Route.PollCreate.route) {
            when (val u = user) {
                null -> Box(Modifier.fillMaxSize()) { CircularProgressIndicator() }
                else -> {
                    if (!u.role.equals("admin", ignoreCase = true)) {
                        LaunchedEffect(Unit) { nav.popBackStack() }
                    } else {
                        PollEditorRoute(
                            createdByUid = u.uid,
                            onSaved = { _ -> nav.popBackStack() },
                            onCancel = { nav.popBackStack() }
                        )
                    }
                }
            }
        }

        composable(Route.PollVote.route) { backStackEntry ->
            val pollId = backStackEntry.arguments?.getString("pollId") ?: return@composable
            PollVoteRoute(
                pollId = pollId,
                onClose = { nav.popBackStack() },
                onViewResults = { nav.navigate("poll_results/$pollId") }
            )
        }

        composable(Route.PollResults.route) { backStackEntry ->
            val pollId = backStackEntry.arguments?.getString("pollId") ?: return@composable
            PollResultsRoute(pollId = pollId, onClose = { nav.popBackStack() })
        }

        composable(Route.PollEdit.route) { backStackEntry ->
            val pollId = backStackEntry.arguments?.getString("pollId") ?: return@composable
            PollEditRoute(
                pollId = pollId,
                onSaved = { nav.popBackStack() },
                onCancel = { nav.popBackStack() }
            )
        }
    }
}

private fun navigateToRoleHome(nav: NavHostController, role: String) {
    when (role.lowercase()) {
        "admin" -> nav.navigate(Route.HomeAdmin.route) { popUpTo(Route.Welcome.route) { inclusive = true } }
        "guest" -> nav.navigate(Route.HomeGuest.route) { popUpTo(Route.Welcome.route) { inclusive = true } }
        else    -> nav.navigate(Route.HomeUser.route)  { popUpTo(Route.Welcome.route) { inclusive = true } }
    }
}
