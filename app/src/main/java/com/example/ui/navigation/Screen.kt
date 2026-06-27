package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object ForgotPassword : Screen("forgot_password")
    
    // Main Container screen with Bottom Nav
    object Main : Screen("main")
    
    // Nested screens inside Main or pushed onto backstack
    object TournamentDetails : Screen("tournament_details/{tournamentId}") {
        fun createRoute(tournamentId: Long) = "tournament_details/$tournamentId"
    }
    object RoomDetails : Screen("room_details/{tournamentId}") {
        fun createRoute(tournamentId: Long) = "room_details/$tournamentId"
    }
    object Wallet : Screen("wallet")
    object Social : Screen("social")
    object AdminDashboard : Screen("admin_dashboard")
    object ClanCreate : Screen("clan_create")
}
