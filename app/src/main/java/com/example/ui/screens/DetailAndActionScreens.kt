package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.model.Tournament
import com.example.ui.components.*
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import com.example.ui.viewmodel.EsportsViewModel
import kotlinx.coroutines.delay

@Composable
fun TournamentDetailsScreen(
    navController: NavController,
    viewModel: EsportsViewModel,
    tournamentId: Long
) {
    val tournaments by viewModel.tournaments.collectAsState()
    val registeredTournaments by viewModel.userRegisteredTournaments.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val tournament = tournaments.find { it.id == tournamentId }
    if (tournament == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Tournament not found", color = Color.White)
        }
        return
    }

    val isRegistered = registeredTournaments.contains(tournamentId)

    Scaffold(
        topBar = {
            OptInTopAppBar(
                title = { Text("Bracket Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        },
        containerColor = EsportsBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Info Card
            GlassCard(borderColor = ElectricBlue.copy(0.3f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tournament.game.uppercase(),
                            color = ElectricBlue,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tournament.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    TournamentStatusBadge(status = tournament.status)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = EsportsSurfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("FORMAT", color = TextGray, fontSize = 10.sp)
                        Text(tournament.gameType, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column {
                        Text("MAP ARENA", color = TextGray, fontSize = 10.sp)
                        Text(tournament.mapName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("ENTRY COST", color = TextGray, fontSize = 10.sp)
                        val entryStr = if (tournament.entryType == "FREE") "FREE"
                        else if (tournament.entryType == "COINS") "${tournament.entryCoins} Coins"
                        else "$${tournament.entryFee}"
                        Text(entryStr, color = EsportsGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time & Countdown Timer
            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("MATCH COMMENCES", color = TextGray, fontSize = 11.sp)
                        Text(
                            text = formatTime(tournament.matchTime),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    if (tournament.status == "UPCOMING") {
                        EsportsCountdownTimer(matchTime = tournament.matchTime)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Results Card if Completed
            if (tournament.status == "COMPLETED") {
                GlassCard(borderColor = EsportsGold.copy(0.4f)) {
                    Text(
                        text = "🏆 Tournament Match Results",
                        fontWeight = FontWeight.Bold,
                        color = EsportsGold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("WINNER / SQUAD", color = TextGray, fontSize = 11.sp)
                            Text(
                                text = tournament.winnerUsername ?: "TBD",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        Column {
                            Text("ELIMINATIONS", color = TextGray, fontSize = 11.sp)
                            Text(
                                text = "${tournament.winnerKills ?: 0} Kills",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("MVP GAMER", color = TextGray, fontSize = 11.sp)
                            Text(
                                text = tournament.mvpUsername ?: "TBD",
                                color = EsportsGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Rules details
            GlassCard {
                Text(
                    text = "Official Ruleset",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = tournament.rules,
                    color = TextGray,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // CTA Logic
            when (tournament.status) {
                "COMPLETED" -> {
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricBlue),
                        border = BorderStroke(1.dp, ElectricBlue.copy(0.4f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("BACK TO BRACKETS", fontWeight = FontWeight.SemiBold)
                    }
                }
                "LIVE" -> {
                    if (isRegistered) {
                        EsportsButton(
                            text = "ACCESS LOBBY ROOM CREDENTIALS",
                            onClick = { navController.navigate(Screen.RoomDetails.createRoute(tournamentId)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Button(
                            onClick = {},
                            enabled = false,
                            colors = ButtonDefaults.buttonColors(disabledContainerColor = EsportsSurfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("REGISTRATIONS CLOSED (MATCH LIVE)", color = TextGray)
                        }
                    }
                }
                "UPCOMING" -> {
                    if (isRegistered) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Already joined, show lobby button
                            EsportsButton(
                                text = "VIEW ROOM INFO",
                                onClick = { navController.navigate(Screen.RoomDetails.createRoute(tournamentId)) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        // Join Tournament Flow
                        var showConfirmDialog by remember { mutableStateOf(false) }

                        EsportsButton(
                            text = "REGISTER FOR TOURNAMENT",
                            onClick = { showConfirmDialog = true },
                            modifier = Modifier.fillMaxWidth().testTag("join_tournament_button")
                        )

                        if (showConfirmDialog) {
                            AlertDialog(
                                onDismissRequest = { showConfirmDialog = false },
                                title = { Text("Confirm Registration", color = Color.White, fontWeight = FontWeight.Bold) },
                                text = {
                                    Column {
                                        Text("Are you ready to join this tournament? Entering will deduct:", color = TextGray, fontSize = 13.sp)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        val entryStr = if (tournament.entryType == "FREE") "FREE"
                                        else if (tournament.entryType == "COINS") "${tournament.entryCoins} Coins"
                                        else "$${tournament.entryFee}"
                                        Text(
                                            text = entryStr,
                                            color = EsportsGold,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 18.sp
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text("Your Username: ${userProfile?.username}", color = TextWhite, fontSize = 13.sp)
                                        Text("Your Game UID: ${userProfile?.gameUid}", color = TextWhite, fontSize = 13.sp)
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            viewModel.joinTournament(tournamentId)
                                            showConfirmDialog = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                                    ) {
                                        Text("CONFIRM JOIN", color = Color.Black)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showConfirmDialog = false }) {
                                        Text("CANCEL", color = TextGray)
                                    }
                                },
                                containerColor = EsportsSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

// ROOM ID & PASSWORD RELEASE SCREEN
@Composable
fun RoomDetailsScreen(
    navController: NavController,
    viewModel: EsportsViewModel,
    tournamentId: Long
) {
    val tournaments by viewModel.tournaments.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    var copyAlert by remember { mutableStateOf("") }

    val tournament = tournaments.find { it.id == tournamentId }
    if (tournament == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Tournament not found", color = Color.White)
        }
        return
    }

    Scaffold(
        topBar = {
            OptInTopAppBar(
                title = { Text("Lobby Access Link", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        },
        containerColor = EsportsBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Key,
                contentDescription = "Room Credentials",
                tint = EsportsGold,
                modifier = Modifier.size(72.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Lobby Credentials Released!",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Copy the credentials below to join the game server lobby now.",
                color = TextGray,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            GlassCard(borderColor = EsportsGold.copy(0.4f), modifier = Modifier.fillMaxWidth()) {
                if (tournament.roomId.isNotBlank() && tournament.roomPassword.isNotBlank()) {
                    // Show credentials
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("ROOM LOBBY ID", color = TextGray, fontSize = 11.sp)
                                Text(
                                    text = tournament.roomId,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(tournament.roomId))
                                    copyAlert = "Room ID copied!"
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = ElectricBlue)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = EsportsSurfaceVariant)
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("LOBBY PASSWORD", color = TextGray, fontSize = 11.sp)
                                Text(
                                    text = tournament.roomPassword,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(tournament.roomPassword))
                                    copyAlert = "Lobby Password copied!"
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = ElectricBlue)
                            }
                        }
                    }
                } else {
                    // Credentials not yet released
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = EsportsGold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Awaiting Room Release...",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Admin is setting up the custom lobby. Credentials will reveal here 10-15 minutes before Match Time.",
                            color = TextGray,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }

            if (copyAlert.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = copyAlert, color = EsportsGold, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                LaunchedEffect(copyAlert) {
                    delay(1500)
                    copyAlert = ""
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedButton(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                border = BorderStroke(1.dp, EsportsSurfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("CLOSE DETAILS")
            }
        }
    }
}

// ADMIN DASHBOARD SCREEN
@Composable
fun AdminDashboardScreen(navController: NavController, viewModel: EsportsViewModel) {
    val tournaments by viewModel.tournaments.collectAsState()
    var selectedAdminTab by remember { mutableStateOf("CREATE") } // CREATE, MANAGE

    // Create Tournament states
    var tTitle by remember { mutableStateOf("") }
    var tGame by remember { mutableStateOf("Free Fire") }
    var tEntryType by remember { mutableStateOf("FREE") } // FREE, PAID, COINS
    var tFee by remember { mutableStateOf("") }
    var tCoins by remember { mutableStateOf("") }
    var tPrize by remember { mutableStateOf("") }
    var tMaxSlots by remember { mutableStateOf("") }
    var tGameType by remember { mutableStateOf("Solo") }
    var tMapName by remember { mutableStateOf("") }

    // Update Room states
    var selectTournamentIdRoom by remember { mutableStateOf<Long?>(null) }
    var roomLobbyId by remember { mutableStateOf("") }
    var roomLobbyPass by remember { mutableStateOf("") }

    // Declare Result states
    var selectTournamentIdResult by remember { mutableStateOf<Long?>(null) }
    var winnerName by remember { mutableStateOf("") }
    var winnerKills by remember { mutableStateOf("") }
    var mvpName by remember { mutableStateOf("") }

    val gamesList = listOf("Free Fire", "BGMI", "Valorant", "Chess", "eFootball")

    Scaffold(
        topBar = {
            OptInTopAppBar(
                title = { Text("Platform HQ (Admin)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        },
        containerColor = EsportsBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Admin Sub Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(EsportsSurface)
                    .padding(4.dp)
            ) {
                listOf("CREATE", "MANAGE").forEach { tab ->
                    val isSel = selectedAdminTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) EsportsSurfaceVariant else Color.Transparent)
                            .clickable { selectedAdminTab = tab }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (tab == "CREATE") "CREATE TOURNAMENT" else "MANAGE BRACKETS",
                            color = if (isSel) EsportsGold else TextGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedAdminTab == "CREATE") {
                // FORM TO CREATE TOURNAMENT
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = tTitle,
                            onValueChange = { tTitle = it },
                            label = { Text("Tournament Title") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = ElectricBlue
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("admin_title_input")
                        )
                    }

                    item {
                        Text("Select Game Type", color = TextGray, fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            gamesList.forEach { g ->
                                val isSelected = tGame == g
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) ElectricBlue else EsportsSurfaceVariant)
                                        .clickable { tGame = g }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(text = g, color = if (isSelected) Color.Black else TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = tGameType,
                                onValueChange = { tGameType = it },
                                label = { Text("Mode (Solo/Duo/Squad)") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = ElectricBlue
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = tMapName,
                                onValueChange = { tMapName = it },
                                label = { Text("Map name") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = ElectricBlue
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = tPrize,
                                onValueChange = { tPrize = it },
                                label = { Text("Prize Pool ($ or Coins)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = ElectricBlue
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = tMaxSlots,
                                onValueChange = { tMaxSlots = it },
                                label = { Text("Max Slots") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = ElectricBlue
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Text("Entry Fee Type", color = TextGray, fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            listOf("FREE", "PAID", "COINS").forEach { type ->
                                val isSelected = tEntryType == type
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) EsportsGold else EsportsSurfaceVariant)
                                        .clickable { tEntryType = type }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(text = type, color = if (isSelected) Color.Black else TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    item {
                        if (tEntryType == "PAID") {
                            OutlinedTextField(
                                value = tFee,
                                onValueChange = { tFee = it },
                                label = { Text("Entry Fee Cash ($)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = ElectricBlue
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else if (tEntryType == "COINS") {
                            OutlinedTextField(
                                value = tCoins,
                                onValueChange = { tCoins = it },
                                label = { Text("Entry Fee Coins") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = ElectricBlue
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        EsportsButton(
                            text = "PUBLISH BRACKET",
                            onClick = {
                                if (tTitle.isNotBlank() && tPrize.isNotBlank() && tMaxSlots.isNotBlank()) {
                                    viewModel.createTournamentAdmin(
                                        title = tTitle,
                                        game = tGame,
                                        entryType = tEntryType,
                                        entryFee = tFee.toDoubleOrNull() ?: 0.0,
                                        entryCoins = tCoins.toIntOrNull() ?: 0,
                                        prizePool = tPrize.toDoubleOrNull() ?: 100.0,
                                        maxSlots = tMaxSlots.toIntOrNull() ?: 50,
                                        gameType = tGameType.ifBlank { "Solo" },
                                        mapName = tMapName.ifBlank { "Bermuda" },
                                        hoursFromNow = (1..6).random()
                                    )
                                    // Reset fields
                                    tTitle = ""
                                    tFee = ""
                                    tCoins = ""
                                    tPrize = ""
                                    tMaxSlots = ""
                                    tMapName = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("admin_submit_button")
                        )
                    }
                }
            } else {
                // MANAGE ACTIVE TOURNAMENTS
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(tournaments) { tournament ->
                        val isUpcoming = tournament.status == "UPCOMING"
                        val isLive = tournament.status == "LIVE"

                        GlassCard(borderColor = if (isLive) EsportsRed.copy(0.4f) else EsportsSurfaceVariant) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "${tournament.game} • ${tournament.status}", color = ElectricBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(text = tournament.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    if (tournament.roomId.isNotBlank()) {
                                        Text(text = "Room: ${tournament.roomId} / Pass: ${tournament.roomPassword}", color = EsportsGold, fontSize = 12.sp)
                                    }
                                }

                                IconButton(onClick = { viewModel.deleteTournamentAdmin(tournament.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = EsportsRed)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                if (isUpcoming) {
                                    Button(
                                        onClick = { selectTournamentIdRoom = tournament.id },
                                        colors = ButtonDefaults.buttonColors(containerColor = EsportsSurfaceVariant),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("SET LOBBY ID", color = ElectricBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (isUpcoming || isLive) {
                                    Button(
                                        onClick = { selectTournamentIdResult = tournament.id },
                                        colors = ButtonDefaults.buttonColors(containerColor = EsportsSurfaceVariant),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("DECLARE RESULT", color = EsportsGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Set Room Lobby credentials dialog
    if (selectTournamentIdRoom != null) {
        AlertDialog(
            onDismissRequest = { selectTournamentIdRoom = null },
            title = { Text("Publish Lobby ID & Pass", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("These credentials will instantly expose to all registered participants.", color = TextGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = roomLobbyId,
                        onValueChange = { roomLobbyId = it },
                        label = { Text("Room ID") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = ElectricBlue),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = roomLobbyPass,
                        onValueChange = { roomLobbyPass = it },
                        label = { Text("Lobby Password") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = ElectricBlue),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (roomLobbyId.isNotBlank() && roomLobbyPass.isNotBlank()) {
                            viewModel.updateRoomDetailsAdmin(selectTournamentIdRoom!!, roomLobbyId, roomLobbyPass)
                            selectTournamentIdRoom = null
                            roomLobbyId = ""
                            roomLobbyPass = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                ) {
                    Text("PUBLISH", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectTournamentIdRoom = null }) {
                    Text("CANCEL", color = TextGray)
                }
            },
            containerColor = EsportsSurface
        )
    }

    // Declare results and disburse prize dialog
    if (selectTournamentIdResult != null) {
        AlertDialog(
            onDismissRequest = { selectTournamentIdResult = null },
            title = { Text("Declare Match Results", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Specify the match winner to disburse prize pools and close the brackets.", color = TextGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = winnerName,
                        onValueChange = { winnerName = it },
                        label = { Text("Winner Username") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = ElectricBlue),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = winnerKills,
                        onValueChange = { winnerKills = it },
                        label = { Text("Winner Total Kills") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = ElectricBlue),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = mvpName,
                        onValueChange = { mvpName = it },
                        label = { Text("MVP Gamer") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = ElectricBlue),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (winnerName.isNotBlank() && winnerKills.isNotBlank() && mvpName.isNotBlank()) {
                            viewModel.completeTournamentAdmin(
                                selectTournamentIdResult!!,
                                winnerName,
                                winnerKills.toIntOrNull() ?: 0,
                                mvpName
                            )
                            selectTournamentIdResult = null
                            winnerName = ""
                            winnerKills = ""
                            mvpName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EsportsGold)
                ) {
                    Text("DISBURSE & FINISH", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectTournamentIdResult = null }) {
                    Text("CANCEL", color = TextGray)
                }
            },
            containerColor = EsportsSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptInTopAppBar(
    title: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = title,
        navigationIcon = navigationIcon,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = EsportsBackground,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
        )
    )
}
