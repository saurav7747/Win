package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import com.example.ui.viewmodel.EsportsViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreenContainer(navController: NavController, viewModel: EsportsViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Listen to operation messages
    LaunchedEffect(key1 = true) {
        viewModel.operationMessage.collect { msg ->
            scope.launch {
                snackbarHostState.showSnackbar(msg)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = EsportsBackground,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.SportsEsports, contentDescription = "Tournaments") },
                    label = { Text("Arena") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricBlue,
                        selectedTextColor = ElectricBlue,
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray,
                        indicatorColor = EsportsSurfaceVariant
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Leaderboard, contentDescription = "Leaderboard") },
                    label = { Text("Rankings") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricBlue,
                        selectedTextColor = ElectricBlue,
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray,
                        indicatorColor = EsportsSurfaceVariant
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.MilitaryTech, contentDescription = "Rewards") },
                    label = { Text("Rewards") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricBlue,
                        selectedTextColor = ElectricBlue,
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray,
                        indicatorColor = EsportsSurfaceVariant
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Groups, contentDescription = "Clan") },
                    label = { Text("Clan") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricBlue,
                        selectedTextColor = ElectricBlue,
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray,
                        indicatorColor = EsportsSurfaceVariant
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Wallet") },
                    label = { Text("Profile") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricBlue,
                        selectedTextColor = ElectricBlue,
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray,
                        indicatorColor = EsportsSurfaceVariant
                    )
                )
            }
        },
        containerColor = EsportsBackground
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> TournamentListTab(navController, viewModel)
                1 -> LeaderboardTab(viewModel)
                2 -> RewardsTab(viewModel)
                3 -> ClanTab(navController, viewModel)
                4 -> ProfileAndWalletTab(navController, viewModel)
            }
        }
    }
}

// 1. ARENA (TOURNAMENT LIST) TAB
@Composable
fun TournamentListTab(navController: NavController, viewModel: EsportsViewModel) {
    val tournaments by viewModel.tournaments.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val announcements by viewModel.announcements.collectAsState()
    val registeredIds by viewModel.userRegisteredTournaments.collectAsState()

    var activeGameFilter by remember { mutableStateOf("All") }
    var activeStatusFilter by remember { mutableStateOf("UPCOMING") } // UPCOMING, LIVE, COMPLETED

    val games = listOf("All", "Free Fire", "BGMI", "Valorant", "Chess")
    val statuses = listOf("UPCOMING", "LIVE", "COMPLETED")

    val filteredTournaments = tournaments.filter {
        (activeGameFilter == "All" || it.game == activeGameFilter) &&
                it.status == activeStatusFilter
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Platform Header with Saurav creator credit
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🏆 Win or Learn",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Made with ❤️ by Saurav",
                        fontSize = 11.sp,
                        color = EsportsGold,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Admin dashboard button
                IconButton(
                    onClick = { navController.navigate(Screen.AdminDashboard.route) },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(EsportsSurfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin Dashboard",
                        tint = EsportsGold
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // News alert banner
        if (announcements.isNotEmpty()) {
            item {
                GlassCard(
                    borderColor = EsportsGold.copy(0.3f),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    val alert = announcements.first()
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Announcement",
                            tint = EsportsGold,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = alert.title,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                            Text(
                                text = alert.content,
                                color = TextGray,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Game Filter list
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(games) { game ->
                    val isSelected = activeGameFilter == game
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) ElectricBlue else EsportsSurface)
                            .border(
                                1.dp,
                                if (isSelected) ElectricBlue else EsportsSurfaceVariant,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { activeGameFilter = game }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = game,
                            color = if (isSelected) Color.Black else TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Status filter tabs (Upcoming, Live, Completed)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(EsportsSurface)
                    .padding(4.dp)
            ) {
                statuses.forEach { status ->
                    val isSelected = activeStatusFilter == status
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) EsportsSurfaceVariant else Color.Transparent)
                            .clickable { activeStatusFilter = status }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = status,
                            color = if (isSelected) ElectricBlue else TextGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Tournament Cards
        if (filteredTournaments.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = "Empty",
                        tint = TextMuted,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Active Brackets Found",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Try switching filters. Future brackets are coming!",
                        color = TextGray,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        } else {
            items(filteredTournaments) { tournament ->
                val hasJoined = registeredIds.contains(tournament.id)

                GlassCard(
                    borderColor = if (hasJoined) EsportsGold.copy(0.4f) else ElectricBlue.copy(0.2f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clickable { navController.navigate(Screen.TournamentDetails.createRoute(tournament.id)) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = tournament.game,
                                    color = ElectricBlue,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${tournament.gameType}",
                                    color = TextGray,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = tournament.title,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.MilitaryTech,
                                    contentDescription = "Prize",
                                    tint = EsportsGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Prize Pool: " + if (tournament.entryType == "COINS") "${tournament.prizePool.toInt()} Coins" else "$${tournament.prizePool}",
                                    color = EsportsGold,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            TournamentStatusBadge(status = tournament.status)
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            // Slots bar
                            val ratio = tournament.registeredSlots.toFloat() / tournament.maxSlots.toFloat()
                            Column(horizontalAlignment = Alignment.End) {
                                LinearProgressIndicator(
                                    progress = { ratio },
                                    color = ElectricBlue,
                                    trackColor = EsportsSurfaceVariant,
                                    modifier = Modifier
                                        .width(70.dp)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${tournament.registeredSlots}/${tournament.maxSlots} Slots",
                                    color = TextGray,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = EsportsSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatTime(tournament.matchTime),
                            color = TextGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = if (tournament.entryType == "FREE") "FREE ENTRY"
                            else if (tournament.entryType == "COINS") "${tournament.entryCoins} COINS"
                            else "$${tournament.entryFee} ENTRY FEE",
                            color = if (tournament.entryType == "FREE") ElectricBlue else EsportsGold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

// 2. LEADERS (RANKINGS) TAB
@Composable
fun LeaderboardTab(viewModel: EsportsViewModel) {
    var leaderboardType by remember { mutableIntStateOf(0) } // 0: Global, 1: Weekly, 2: Clans

    val clans by viewModel.clans.collectAsState()

    // Static winners for illustration
    val topWinners = listOf(
        Triple("Saurav", "Grandmaster", 14500),
        Triple("AlphaGamer", "Grandmaster", 12400),
        Triple("ViperPlayz", "Heroic", 9800),
        Triple("SniperQueen", "Diamond I", 7600),
        Triple("ZenEsports", "Diamond II", 6400),
        Triple("TriggerHappy", "Heroic", 6100),
        Triple("Deadshot", "Platinum", 4500)
    )

    val topKillers = listOf(
        Triple("AlphaGamer", "642 Kills", 12.5f),
        Triple("Saurav", "638 Kills", 14.2f),
        Triple("ViperPlayz", "410 Kills", 8.9f),
        Triple("GhostRider", "352 Kills", 7.1f),
        Triple("PhantomGamer", "288 Kills", 6.8f)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Leaderboard Arena 🏆",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "Every match updates global standings",
                color = TextGray,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Tab row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(EsportsSurface)
                    .padding(4.dp)
            ) {
                listOf("Top Winners", "Top Killers", "Clans").forEachIndexed { idx, label ->
                    val isSelected = leaderboardType == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) EsportsSurfaceVariant else Color.Transparent)
                            .clickable { leaderboardType = idx }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) ElectricBlue else TextGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (leaderboardType == 0) {
            // Global winners
            items(topWinners.size) { idx ->
                val user = topWinners[idx]
                LeaderRow(
                    rank = idx + 1,
                    name = user.first,
                    rankTier = user.second,
                    stat = "${user.third} XP"
                )
            }
        } else if (leaderboardType == 1) {
            // Weekly killers
            items(topKillers.size) { idx ->
                val user = topKillers[idx]
                LeaderRow(
                    rank = idx + 1,
                    name = user.first,
                    rankTier = "K/D: ${user.third}",
                    stat = user.second
                )
            }
        } else {
            // Clan rankings
            items(clans.size) { idx ->
                val clan = clans[idx]
                LeaderRow(
                    rank = idx + 1,
                    name = "${clan.name} [${clan.tag}]",
                    rankTier = "Leader: ${clan.leaderUsername}",
                    stat = "${clan.points} pts"
                )
            }
        }
    }
}

@Composable
fun LeaderRow(rank: Int, name: String, rankTier: String, stat: String) {
    val rankColor = when (rank) {
        1 -> EsportsGold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> TextMuted
    }

    GlassCard(
        borderColor = if (rank <= 3) rankColor.copy(0.4f) else EsportsSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Rank number
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (rank <= 3) rankColor.copy(0.15f) else EsportsSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#$rank",
                        color = if (rank <= 3) rankColor else TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = name,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = rankTier,
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }
            }

            Text(
                text = stat,
                color = ElectricBlue,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp
            )
        }
    }
}

// 3. REWARDS TAB
@Composable
fun RewardsTab(viewModel: EsportsViewModel) {
    val tasks by viewModel.rewardTasks.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var refCodeInput by remember { mutableStateOf("") }
    var dailyClaimed by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Esports Rewards Center 🎁",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "Complete missions, earn Coins, level up!",
                color = TextGray,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Daily Check-in Card
        item {
            GlassCard(
                borderColor = EsportsGold.copy(0.3f),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Daily Combat Check-In",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Claim your free 50 coins everyday!",
                            color = TextGray,
                            fontSize = 12.sp
                        )
                    }

                    if (!dailyClaimed) {
                        Button(
                            onClick = {
                                dailyClaimed = true
                                // Simulated wallet coin increase
                                viewModel.depositFunds(0.0) // just triggers callback messages if needed, let's claim via dynamic coin task
                                viewModel.claimTaskReward("daily_login")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EsportsGold),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("CLAIM", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Done", tint = EsportsGold)
                        }
                    }
                }
            }
        }

        // Referral System
        item {
            GlassCard(
                borderColor = CyberPurple.copy(0.3f),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Text(
                    text = "Refer Your Gaming Squad",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    text = "Enter a friend's referral code to instantly credit 200 Coins to your wallet.",
                    color = TextGray,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = refCodeInput,
                        onValueChange = { refCodeInput = it },
                        placeholder = { Text("E.g. SAURAV99") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = EsportsSurfaceVariant,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    EsportsButton(
                        text = "APPLY",
                        onClick = {
                            if (refCodeInput.isNotBlank()) {
                                viewModel.applyReferralCode(refCodeInput)
                                refCodeInput = ""
                            }
                        },
                        modifier = Modifier.height(54.dp)
                    )
                }

                // Show personal referral code
                userProfile?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = EsportsSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Your Referral Code:", color = TextGray, fontSize = 13.sp)
                        Text(
                            text = it.referralCode,
                            color = EsportsGold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Battle Missions",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // List of Missions
        items(tasks) { task ->
            if (task.id != "daily_login") { // handled in separate card
                GlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Text(
                                text = task.description,
                                color = TextGray,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row {
                                Text(
                                    text = "+${task.xpReward} XP ",
                                    color = ElectricBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "• +${task.coinReward} Coins",
                                    color = EsportsGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (!task.isCompleted) {
                            Button(
                                onClick = { viewModel.claimTaskReward(task.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberPurple),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("CLAIM", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Finished", tint = ElectricBlue)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 4. CLAN TAB (CLAN MANAGEMENT & CHAT)
@Composable
fun ClanTab(navController: NavController, viewModel: EsportsViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    val clans by viewModel.clans.collectAsState()
    val userClan by viewModel.userClan.collectAsState()
    val clanMessages by viewModel.clanMessages.collectAsState()

    var clanNameInput by remember { mutableStateOf("") }
    var clanTagInput by remember { mutableStateOf("") }
    var textMessage by remember { mutableStateOf("") }

    if (userProfile?.clanId == null) {
        // Not in a clan: Show Create/Join forms
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "Esports Clans 🛡️",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Band together with professional teams",
                    color = TextGray,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Create Clan
            item {
                GlassCard(borderColor = CyberPurple.copy(0.4f), modifier = Modifier.padding(bottom = 24.dp)) {
                    Text(
                        text = "Forge an Elite Clan",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = clanNameInput,
                        onValueChange = { clanNameInput = it },
                        label = { Text("Clan Name (E.g. Team Soul)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = EsportsSurfaceVariant,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = clanTagInput,
                        onValueChange = { clanTagInput = it },
                        label = { Text("Clan Tag (Max 4 chars)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = EsportsSurfaceVariant,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    EsportsButton(
                        text = "FOUND CLAN",
                        onClick = {
                            if (clanNameInput.isNotBlank() && clanTagInput.isNotBlank()) {
                                viewModel.createClan(clanNameInput, clanTagInput)
                                clanNameInput = ""
                                clanTagInput = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                Text(
                    text = "Active Clans Recruiting",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // List of Recruiting Clans
            items(clans) { clan ->
                GlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Brush.radialGradient(listOf(ElectricBlue, CyberPurple))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = clan.tag,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = clan.name,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "Leader: ${clan.leaderUsername}",
                                        color = TextGray,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "${clan.points} pts", color = EsportsGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = { viewModel.joinClan(clan.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = EsportsSurfaceVariant),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("JOIN", color = ElectricBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    } else {
        // In a clan: Show stats and dynamic live clan chat!
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            userClan?.let { clan ->
                GlassCard(borderColor = CyberPurple.copy(0.4f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Brush.radialGradient(listOf(ElectricBlue, CyberPurple))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = clan.tag,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = clan.name,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = "Leader: ${clan.leaderUsername} | ${clan.memberCount} Members",
                                    color = TextGray,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Text(
                            text = "${clan.points} XP",
                            color = EsportsGold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Clan Tactical Chat Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Forum, contentDescription = "Chat", tint = ElectricBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tactical Comm Link (Live)",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Message Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(EsportsSurface)
                    .padding(8.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    reverseLayout = true
                ) {
                    val reversedMsg = clanMessages.reversed()
                    items(reversedMsg) { msg ->
                        val isMe = msg.sender == userProfile?.username
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                        ) {
                            Text(
                                text = msg.sender,
                                color = if (isMe) ElectricBlue else EsportsGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 8.dp,
                                            topEnd = 8.dp,
                                            bottomStart = if (isMe) 8.dp else 0.dp,
                                            bottomEnd = if (isMe) 0.dp else 8.dp
                                        )
                                    )
                                    .background(if (isMe) EsportsSurfaceVariant else Color(0xFF1E2135))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Send message box
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textMessage,
                    onValueChange = { textMessage = it },
                    placeholder = { Text("Enter tactical comm...") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = EsportsSurfaceVariant,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                    onClick = {
                        if (textMessage.isNotBlank()) {
                            viewModel.sendClanMessage(textMessage)
                            textMessage = ""
                        }
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(ElectricBlue)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Black)
                }
            }
        }
    }
}

// 5. PROFILE & WALLET TAB
@Composable
fun ProfileAndWalletTab(navController: NavController, viewModel: EsportsViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    var showDepositDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var amountValue by remember { mutableStateOf("") }

    if (userProfile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ElectricBlue)
        }
        return
    }

    val profile = userProfile!!

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Player Esports Profile Card
        item {
            GlassCard(borderColor = ElectricBlue.copy(0.3f), modifier = Modifier.padding(bottom = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar box
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(ElectricBlue, CyberPurple))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile.username.take(2).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = profile.username,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "UID: " + profile.gameUid,
                            color = EsportsGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = profile.rank,
                            color = ElectricBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Level Badge
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "LEVEL", color = TextGray, fontSize = 9.sp)
                        Text(
                            text = "${profile.level}",
                            color = EsportsGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = profile.bio,
                    color = TextGray,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )

                // Level XP Progress
                Spacer(modifier = Modifier.height(14.dp))
                val xpRatio = profile.xp.toFloat() / profile.xpMax.toFloat()
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "XP Progress", color = TextGray, fontSize = 11.sp)
                        Text(text = "${profile.xp}/${profile.xpMax}", color = ElectricBlue, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { xpRatio },
                        color = ElectricBlue,
                        trackColor = EsportsSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                }
            }
        }

        // Wallet Balance Card
        item {
            GlassCard(borderColor = EsportsGold.copy(0.3f), modifier = Modifier.padding(bottom = 16.dp)) {
                Text(
                    text = "Win or Learn Wallet",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Cash Balance", color = TextGray, fontSize = 12.sp)
                        Text(
                            text = "$${String.format("%.2f", profile.balanceCash)}",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Esports Coins", color = TextGray, fontSize = 12.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = "Coins", tint = EsportsGold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${profile.balanceCoins}",
                                color = EsportsGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { showDepositDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("DEPOSIT", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { showWithdrawDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPurple),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("WITHDRAW", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Stats Analytics Section
        item {
            GlassCard(modifier = Modifier.padding(bottom = 16.dp)) {
                Text(
                    text = "Combat Analytics (Kills Trend)",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 15.sp
                )
                Text(
                    text = "Track your recent tournament matches kills progression",
                    color = TextGray,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Our Custom Canvas Line Chart!
                PerformanceChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Matches", color = TextGray, fontSize = 11.sp)
                        Text(text = "${profile.totalMatches}", color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Total Kills", color = TextGray, fontSize = 11.sp)
                        Text(text = "${profile.totalKills}", color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Wins 🏆", color = TextGray, fontSize = 11.sp)
                        Text(text = "${profile.wins}", color = EsportsGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }

        // Wallet Transactions History
        item {
            Text(
                text = "Transaction Records",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (transactions.isEmpty()) {
            item {
                Text(
                    text = "No recorded transactions yet.",
                    color = TextGray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            items(transactions) { tx ->
                GlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = tx.description,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                            Text(
                                text = formatTime(tx.timestamp),
                                color = TextGray,
                                fontSize = 11.sp
                            )
                        }

                        val isPositive = tx.type == "DEPOSIT" || tx.type == "PRIZE_WINNING" || tx.type == "REFERRAL_BONUS"
                        val amountText = if (isPositive) "+$${tx.amount}" else "-$${tx.amount}"
                        val textColor = if (isPositive) ElectricBlue else EsportsRed

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = amountText,
                                color = textColor,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = tx.status,
                                color = if (tx.status == "COMPLETED") EsportsGold else TextGray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }

    // Deposit Dialog Form
    if (showDepositDialog) {
        AlertDialog(
            onDismissRequest = { showDepositDialog = false },
            title = { Text("Deposit Esports Funds", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Top-up cash balance to pay tournament registration entry fees.", color = TextGray, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = amountValue,
                        onValueChange = { amountValue = it },
                        label = { Text("Amount ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = ElectricBlue
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = amountValue.toDoubleOrNull()
                        if (amt != null && amt > 0) {
                            viewModel.depositFunds(amt)
                            showDepositDialog = false
                            amountValue = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                ) {
                    Text("DEPOSIT", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDepositDialog = false; amountValue = "" }) {
                    Text("CANCEL", color = TextGray)
                }
            },
            containerColor = EsportsSurface
        )
    }

    // Withdraw Dialog Form
    if (showWithdrawDialog) {
        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            title = { Text("Withdraw Winnings", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Transfer cash earnings to your bank account or wallet. Takes up to 12 hours.", color = TextGray, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = amountValue,
                        onValueChange = { amountValue = it },
                        label = { Text("Amount ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = ElectricBlue
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = amountValue.toDoubleOrNull()
                        if (amt != null && amt > 0) {
                            viewModel.withdrawFunds(amt)
                            showWithdrawDialog = false
                            amountValue = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPurple)
                ) {
                    Text("WITHDRAW", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false; amountValue = "" }) {
                    Text("CANCEL", color = TextGray)
                }
            },
            containerColor = EsportsSurface
        )
    }
}
