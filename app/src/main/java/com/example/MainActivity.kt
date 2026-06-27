package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.navigation.Screen
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.EsportsViewModel
import com.example.ui.viewmodel.EsportsViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup full Edge-to-Edge display
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = com.example.ui.theme.EsportsBackground
                ) {
                    val viewModel: EsportsViewModel by viewModels {
                        EsportsViewModelFactory(application)
                    }
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Splash.route
                    ) {
                        composable(Screen.Splash.route) {
                            SplashScreen(navController = navController, viewModel = viewModel)
                        }
                        
                        composable(Screen.Onboarding.route) {
                            OnboardingScreen(navController = navController)
                        }
                        
                        composable(Screen.Login.route) {
                            LoginScreen(navController = navController, viewModel = viewModel)
                        }
                        
                        composable(Screen.Signup.route) {
                            SignupScreen(navController = navController, viewModel = viewModel)
                        }
                        
                        composable(Screen.ForgotPassword.route) {
                            ForgotPasswordScreen(navController = navController)
                        }
                        
                        composable(Screen.Main.route) {
                            MainScreenContainer(navController = navController, viewModel = viewModel)
                        }
                        
                        composable(
                            route = Screen.TournamentDetails.route,
                            arguments = listOf(navArgument("tournamentId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getLong("tournamentId") ?: 0L
                            TournamentDetailsScreen(
                                navController = navController,
                                viewModel = viewModel,
                                tournamentId = id
                            )
                        }
                        
                        composable(
                            route = Screen.RoomDetails.route,
                            arguments = listOf(navArgument("tournamentId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getLong("tournamentId") ?: 0L
                            RoomDetailsScreen(
                                navController = navController,
                                viewModel = viewModel,
                                tournamentId = id
                            )
                        }
                        
                        composable(Screen.AdminDashboard.route) {
                            AdminDashboardScreen(navController = navController, viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
