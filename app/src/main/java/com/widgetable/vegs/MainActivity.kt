package com.widgetable.vegs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.widgetable.vegs.ui.screens.*
import com.widgetable.vegs.ui.theme.WidgetableTheme
import com.widgetable.vegs.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WidgetableTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WidgetableAppRoot()
                }
            }
        }
    }
}

@Composable
fun WidgetableAppRoot() {
    val viewModel: MainViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val navController = rememberNavController()

    val startDestination = if (uiState.isLoggedIn) "home" else "auth"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("auth") {
            AuthScreen(
                onSuccess = {
                    viewModel.refreshUser()
                    navController.navigate("home") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToPet = { petId -> navController.navigate("pet/$petId") },
                onNavigateToFriends = { navController.navigate("friends") },
                onNavigateToMood = { navController.navigate("mood") },
                onNavigateToProfile = { navController.navigate("profile") },
                onLogout = {
                    viewModel.logout()
                    navController.navigate("auth") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable("pet/{petId}") { backStack ->
            val petId = backStack.arguments?.getString("petId") ?: return@composable
            PetHomeScreen(
                petId = petId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("friends") {
            FriendsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("mood") {
            MoodScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("profile") {
            ProfileScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("create_pet") {
            CreatePetScreen(
                viewModel = viewModel,
                onCreated = { petId ->
                    navController.navigate("pet/$petId") {
                        popUpTo("home")
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
