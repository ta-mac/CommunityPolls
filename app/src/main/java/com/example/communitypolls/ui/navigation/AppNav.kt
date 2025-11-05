package com.example.communitypolls.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.communitypolls.AuthVmFactory
import com.example.communitypolls.ui.auth.AuthViewModel
import com.example.communitypolls.ui.auth.SplashScreen
import com.example.communitypolls.ui.polls.*
import com.example.communitypolls.ui.screens.*
import com.example.communitypolls.ui.sugg.AdminSuggRoute
import com.example.communitypolls.ui.sugg.SuggestPollRoute

sealed class Route(val route: String) {
    object Splash : Route("splash")
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
    object Suggest : Route("suggest")
    object SuggestionsAdmin : Route("suggestions_admin")
    object Profile : Route("profile")
}

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val authVm: AuthViewModel = viewModel(factory = AuthVmFactory())
    val state by authVm.state.collectAsState()
    val user = state.user

    // ✅ Guard: If user is null, always force navigation to Welcome and clear backstack
    LaunchedEffect(user) {
        if (user == null) {
            nav.navigate(Route.Welcome.route) {
                popUpTo(0) { inclusive = true } // clear all previous destinations
                launchSingleTop = true
            }
        }
    }

    val resolveRole: suspend () -> String? = { user?.role }

    NavHost(navController = nav, startDestination = Route.Splash.route) {

        composable(Route.Splash.route) {
            SplashScreen(
                navController = nav,
                resolveRole = resolveRole,
                companyName = "ByteForge"
            )
        }

        composable(Route.Welcome.route) {
            WelcomeScreen(
                loading = state.loading,
                error = state.error,
                onSignInClick = { nav.navigate(Route.SignIn.route) },
                onSignUpClick = { nav.navigate(Route.SignUp.route) },
                onGuestClick = { authVm.signInGuest() },
                user = user,
                onEnter = {
                    val resolved = user?.role ?: "guest"
                    navigateToRoleHome(nav, resolved)
                }
            )
        }

        composable(Route.SignIn.route) {
            LaunchedEffect(user) { if (user != null) navigateToRoleHome(nav, user.role) }
            SignInScreen(
                loading = state.loading,
                error = state.error,
                onSubmit = authVm::signIn,
                onGoToSignUp = { nav.navigate(Route.SignUp.route) },
                onResetPassword = authVm::resetPassword
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
                    nav.navigate(Route.Welcome.route) {
                        popUpTo(0) { inclusive = true } // ✅ clear all previous routes
                        launchSingleTop = true
                    }
                },
                onPollClick = { id -> nav.navigate("poll_results/$id") }
            )
        }

        composable(Route.HomeUser.route) {
            HomeUserScreen(
                navController = nav,
                onSignOut = {
                    authVm.signOut()
                    nav.navigate(Route.Welcome.route) {
                        popUpTo(0) { inclusive = true } // ✅ back-press safe
                        launchSingleTop = true
                    }
                },
                onPollClick = { id -> nav.navigate("poll_vote/$id") },
                onSuggestClick = { nav.navigate(Route.Suggest.route) },
                displayName = user?.displayName.orEmpty(),
                email = user?.email.orEmpty(),
                onProfileClick = { nav.navigate(Route.Profile.route) }
            )
        }

        composable(Route.HomeAdmin.route) {
            HomeAdminScreen(
                navController = nav,
                onCreatePoll = {
                    nav.navigate(Route.PollCreate.route) {
                        launchSingleTop = true
                        restoreState = false
                    }
                },
                onSignOut = {
                    authVm.signOut()
                    nav.navigate(Route.Welcome.route) {
                        popUpTo(0) { inclusive = true } // ✅ prevent back navigation
                        launchSingleTop = true
                    }
                },
                onPollClick = { id -> nav.navigate("poll_vote/$id") },
                onEditPoll = { id -> nav.navigate("poll_edit/$id") },
                onSuggestClick = { nav.navigate(Route.SuggestionsAdmin.route) }
            )
        }

        // Poll creation/edit/vote/results
        composable(Route.PollCreate.route) {
            val u = user
            if (u == null) {
                Box(Modifier.fillMaxSize()) { CircularProgressIndicator() }
            } else {
                if (!u.role.equals("admin", ignoreCase = true)) {
                    LaunchedEffect(Unit) { nav.popBackStack() }
                } else {
                    PollEditorRoute(
                        createdByUid = u.uid,
                        onSaved = { nav.popBackStack() },
                        onCancel = { nav.popBackStack() }
                    )
                }
            }
        }

        composable(Route.PollVote.route) { backStack ->
            val pollId = backStack.arguments?.getString("pollId") ?: return@composable
            PollVoteRoute(
                pollId = pollId,
                onClose = { nav.popBackStack() },
                onViewResults = { nav.navigate("poll_results/$pollId") }
            )
        }

        composable(Route.PollResults.route) { backStack ->
            val pollId = backStack.arguments?.getString("pollId") ?: return@composable
            PollResultsRoute(
                pollId = pollId,
                onClose = { nav.popBackStack() }
            )
        }

        composable(Route.PollEdit.route) { backStack ->
            val pollId = backStack.arguments?.getString("pollId") ?: return@composable
            PollEditRoute(
                pollId = pollId,
                onSaved = { nav.popBackStack() },
                onCancel = { nav.popBackStack() }
            )
        }

        composable(Route.Suggest.route) {
            SuggestPollRoute(
                onSubmitted = { nav.popBackStack() },
                onCancel = { nav.popBackStack() }
            )
        }

        composable(Route.SuggestionsAdmin.route) {
            AdminSuggRoute(onClose = { nav.popBackStack() })
        }

        // Admin: View votes screen
        composable(
            route = "voteList/{pollId}/{pollTitle}",
            arguments = listOf(
                navArgument("pollId") { type = NavType.StringType },
                navArgument("pollTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val pollId = backStackEntry.arguments?.getString("pollId") ?: ""
            val pollTitle = backStackEntry.arguments?.getString("pollTitle") ?: ""
            VoteListScreen(
                pollId = pollId,
                pollTitle = pollTitle,
                onBack = { nav.popBackStack() }
            )
        }
    }
}

// ✅ Helper for role-based routing
private fun navigateToRoleHome(nav: NavHostController, role: String) {
    when (role.lowercase()) {
        "admin" -> nav.navigate(Route.HomeAdmin.route) {
            popUpTo(Route.Splash.route) { inclusive = true }
            launchSingleTop = true
        }
        "guest" -> nav.navigate(Route.HomeGuest.route) {
            popUpTo(Route.Splash.route) { inclusive = true }
            launchSingleTop = true
        }
        else -> nav.navigate(Route.HomeUser.route) {
            popUpTo(Route.Splash.route) { inclusive = true }
            launchSingleTop = true
        }
    }
}
