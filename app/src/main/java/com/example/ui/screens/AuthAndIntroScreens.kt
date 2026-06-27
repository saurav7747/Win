package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ui.components.EsportsButton
import com.example.ui.components.GlassCard
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import com.example.ui.viewmodel.EsportsViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, viewModel: EsportsViewModel) {
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2200)
        // Check if profile exists. If so, jump straight to main app. Otherwise onboarding.
        val hasUser = viewModel.userProfile.value != null
        if (hasUser) {
            navController.navigate(Screen.Main.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.Onboarding.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EsportsBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            AnimatedVisibility(
                visible = startAnimation,
                enter = fadeIn(tween(1000)) + scaleIn(tween(1000)),
                exit = fadeOut(tween(500))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Trophy representation
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background(Brush.radialGradient(listOf(ElectricBlue.copy(0.4f), Color.Transparent)))
                            .border(1.5.dp, EsportsGold, RoundedCornerShape(30.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Trophy Icon",
                            tint = EsportsGold,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Win or Learn",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"Every Match Makes You Better.\"",
                        fontSize = 15.sp,
                        color = ElectricBlue,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(120.dp))
            CircularProgressIndicator(color = CyberPurpleLight, strokeWidth = 3.dp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Made with ❤️ by Saurav",
                fontSize = 12.sp,
                color = TextMuted,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
fun OnboardingScreen(navController: NavController) {
    var currentPage by remember { mutableIntStateOf(0) }
    val pages = listOf(
        Triple(
            Icons.Default.SportsEsports,
            "Elite Esports Arena",
            "Participate in daily high-stakes Free Fire, BGMI, and Valorant tournaments. Rise through the ranks and claim the throne."
        ),
        Triple(
            Icons.Default.Groups,
            "Elite Clans & Squads",
            "Form clans with pro esports players, coordinate strategy via live tactical clan chat, and join tournaments together as a squad."
        ),
        Triple(
            Icons.Default.Payments,
            "Instant Cashout",
            "Win matches, earn reward coins, secure prize pools, and easily withdraw earnings directly to your bank account."
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EsportsBackground)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏆 Win or Learn",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Text(
                    text = "Skip",
                    color = ElectricBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            // Central Illustration Card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                borderColor = CyberPurple.copy(0.3f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(Brush.linearGradient(listOf(CyberPurple.copy(0.3f), ElectricBlue.copy(0.1f)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = pages[currentPage].first,
                            contentDescription = "Onboarding Icon",
                            tint = ElectricBlue,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(28.dp))
                    Text(
                        text = pages[currentPage].second,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = pages[currentPage].third,
                        fontSize = 14.sp,
                        color = TextGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }

            // Bottom Navigation and Page Indicators
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                // Dot Indicators
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    pages.forEachIndexed { idx, _ ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(width = if (idx == currentPage) 24.dp else 8.dp, height = 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (idx == currentPage) ElectricBlue else EsportsSurfaceVariant)
                        )
                    }
                }

                // CTA Button
                EsportsButton(
                    text = if (currentPage == pages.size - 1) "GET STARTED" else "NEXT MATCH",
                    onClick = {
                        if (currentPage < pages.size - 1) {
                            currentPage++
                        } else {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("onboarding_cta_button")
                )
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavController, viewModel: EsportsViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EsportsBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🏆 Win or Learn",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Login to join tournaments",
                color = TextGray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            GlassCard(
                borderColor = CyberPurple.copy(0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it; errorMessage = "" },
                    label = { Text("Gamers Tag / Username") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "User", tint = ElectricBlue) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = EsportsSurfaceVariant,
                        focusedLabelColor = ElectricBlue,
                        unfocusedLabelColor = TextGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("login_username_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = "" },
                    label = { Text("Secret Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Lock", tint = ElectricBlue) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = EsportsSurfaceVariant,
                        focusedLabelColor = ElectricBlue,
                        unfocusedLabelColor = TextGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("login_password_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Forgot Password?",
                        color = ElectricBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { navController.navigate(Screen.ForgotPassword.route) }
                    )
                }

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage,
                        color = EsportsRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                EsportsButton(
                    text = "ENTER ARENA",
                    onClick = {
                        if (username.isBlank() || password.isBlank()) {
                            errorMessage = "Please enter both credentials."
                        } else {
                            // If default Saurav profile is created, we bypass or insert new user
                            viewModel.registerNewUser(username, "UID_${ (100000..999999).random() }", "Hello, I am $username!")
                            navController.navigate(Screen.Main.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("login_submit_button")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Admin instant access bypass
                OutlinedButton(
                    onClick = {
                        // Create Saurav default user on bypass
                        viewModel.registerNewUser("Saurav", "592837482", "Elite gamer.")
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EsportsGold),
                    border = BorderStroke(1.dp, EsportsGold.copy(0.4f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("login_bypass_button")
                ) {
                    Text("Instant Demo Bypass (Saurav)", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "New to the platform? ", color = TextGray, fontSize = 14.sp)
                Text(
                    text = "Sign Up",
                    color = ElectricBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { navController.navigate(Screen.Signup.route) }
                )
            }
        }
    }
}

@Composable
fun SignupScreen(navController: NavController, viewModel: EsportsViewModel) {
    var username by remember { mutableStateOf("") }
    var gameUid by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EsportsBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Join Arena 🏆",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Register your elite Esports ID",
                color = TextGray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            GlassCard(
                borderColor = CyberPurple.copy(0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it; errorMessage = "" },
                    label = { Text("Gaming Name (Tag)") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "User", tint = ElectricBlue) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = EsportsSurfaceVariant,
                        focusedLabelColor = ElectricBlue,
                        unfocusedLabelColor = TextGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("signup_username_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = gameUid,
                    onValueChange = { gameUid = it; errorMessage = "" },
                    label = { Text("Game UID (Free Fire / BGMI)") },
                    leadingIcon = { Icon(Icons.Default.Pin, contentDescription = "UID", tint = ElectricBlue) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = EsportsSurfaceVariant,
                        focusedLabelColor = ElectricBlue,
                        unfocusedLabelColor = TextGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("signup_uid_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Player Bio (Achievements)") },
                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = "Bio", tint = ElectricBlue) },
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = EsportsSurfaceVariant,
                        focusedLabelColor = ElectricBlue,
                        unfocusedLabelColor = TextGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("signup_bio_input")
                )

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage,
                        color = EsportsRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                EsportsButton(
                    text = "CREATE PROFILE",
                    onClick = {
                        if (username.isBlank() || gameUid.isBlank()) {
                            errorMessage = "Gaming Name and Game UID are mandatory."
                        } else {
                            val success = viewModel.registerNewUser(username, gameUid, bio)
                            if (success) {
                                navController.navigate(Screen.Main.route) {
                                    popUpTo(Screen.Signup.route) { inclusive = true }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("signup_submit_button")
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Already have an ID? ", color = TextGray, fontSize = 14.sp)
                Text(
                    text = "Log In",
                    color = ElectricBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { navController.navigate(Screen.Login.route) }
                )
            }
        }
    }
}

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EsportsBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.LockReset,
                contentDescription = "Reset Key",
                tint = EsportsGold,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Reset Passkey",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Enter your registered email below",
                color = TextGray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            GlassCard(
                borderColor = EsportsGold.copy(0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!submitted) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Registered Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = ElectricBlue) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = EsportsSurfaceVariant,
                            focusedLabelColor = ElectricBlue,
                            unfocusedLabelColor = TextGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    EsportsButton(
                        text = "SEND DECRYPTION KEY",
                        onClick = {
                            if (email.isNotBlank()) {
                                submitted = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = "Decryption code has been transmitted to $email. Please complete verification link within 15 minutes.",
                        color = ElectricBlue,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(8.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    EsportsButton(
                        text = "BACK TO LOGIN",
                        onClick = { navController.navigate(Screen.Login.route) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
