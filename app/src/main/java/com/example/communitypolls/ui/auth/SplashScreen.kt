package com.example.communitypolls.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.example.communitypolls.R
import kotlinx.coroutines.delay

/**
 * WhatsApp-style splash:
 * - White background
 * - Centered app logo
 * - Company text at bottom center
 * - Session gate: navigates to appropriate screen if user is already signed in
 */
@Composable
fun SplashScreen(
    navController: NavController,
    // roleResolver: returns "admin" | "user" | "guest"
    resolveRole: suspend () -> String? = { "user" },
    companyName: String = "ByteForge",
    backgroundColor: Color = Color.White, // ← White background
    logoResId: Int = R.drawable.ic_app_logo,
    splashDurationMs: Long = 1000L
) {
    var navigating by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(splashDurationMs)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val role = try { resolveRole() } catch (_: Exception) { null }
            when (role?.lowercase()) {
                "admin" -> {
                    navController.navigate("home_admin") {
                        popUpTo("splash") { inclusive = true }
                        launchSingleTop = true
                    }
                }
                "guest" -> {
                    navController.navigate("home_guest") {
                        popUpTo("splash") { inclusive = true }
                        launchSingleTop = true
                    }
                }
                else -> {
                    navController.navigate("home_user") {
                        popUpTo("splash") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        } else {
            navController.navigate("welcome") {
                popUpTo("splash") { inclusive = true }
                launchSingleTop = true
            }
        }
        navigating = true
    }

    // Layout (white background)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        // Centered logo
        Image(
            painter = painterResource(id = logoResId),
            contentDescription = "App Logo",
            modifier = Modifier.size(120.dp)
        )

        // Bottom company name
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "from",
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = companyName,
                color = Color.Black, // black text on white bg
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Optional subtle loader (top-center)
        if (!navigating) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp)
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    strokeWidth = 2.dp
                )
            }
        }
    }
}
