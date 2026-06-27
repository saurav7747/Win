package com.example.data.repository

import com.example.data.dao.EsportsDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class EsportsRepository(private val esportsDao: EsportsDao) {

    // Expose flows to ViewModels
    val userProfile: Flow<UserProfile?> = esportsDao.getUserProfile()
    val tournaments: Flow<List<Tournament>> = esportsDao.getTournaments()
    val transactions: Flow<List<WalletTransaction>> = esportsDao.getTransactions()
    val clans: Flow<List<Clan>> = esportsDao.getClans()
    val friends: Flow<List<Friend>> = esportsDao.getFriends()
    val rewardTasks: Flow<List<RewardTask>> = esportsDao.getRewardTasks()
    val announcements: Flow<List<Announcement>> = esportsDao.getAnnouncements()

    fun getTournamentById(id: Long): Flow<Tournament?> = esportsDao.getTournamentById(id)
    fun getClanById(id: Long): Flow<Clan?> = esportsDao.getClanById(id)
    fun getClanMessages(clanId: Long): Flow<List<ClanMessage>> = esportsDao.getClanMessages(clanId)
    fun getRegistrationsForTournament(tournamentId: Long): Flow<List<TournamentRegistration>> =
        esportsDao.getRegistrationsForTournament(tournamentId)
    fun getRegistrationsForUser(username: String): Flow<List<TournamentRegistration>> =
        esportsDao.getRegistrationsForUser(username)
    fun isRegistered(tournamentId: Long, username: String): Flow<Boolean> =
        esportsDao.isRegistered(tournamentId, username)

    // Suspend functions for operations
    suspend fun insertUserProfile(user: UserProfile) = esportsDao.insertUserProfile(user)
    suspend fun updateUserProfile(user: UserProfile) = esportsDao.updateUserProfile(user)

    suspend fun insertTournament(tournament: Tournament) = esportsDao.insertTournament(tournament)
    suspend fun deleteTournament(id: Long) = esportsDao.deleteTournament(id)

    suspend fun joinTournament(tournamentId: Long, username: String, gameUid: String): Boolean {
        val user = esportsDao.getUserProfileOnce() ?: return false
        val tournament = esportsDao.getTournamentByIdOnce(tournamentId) ?: return false

        // Check slots
        if (tournament.registeredSlots >= tournament.maxSlots) return false

        // Check if already registered
        if (esportsDao.isRegisteredOnce(tournamentId, username)) return false

        // Charge fee
        val updatedUser = when (tournament.entryType) {
            "PAID" -> {
                if (user.balanceCash < tournament.entryFee) return false
                user.copy(balanceCash = user.balanceCash - tournament.entryFee)
            }
            "COINS" -> {
                if (user.balanceCoins < tournament.entryCoins) return false
                user.copy(balanceCoins = user.balanceCoins - tournament.entryCoins)
            }
            else -> user // Free
        }

        // Add registration
        val registration = TournamentRegistration(
            tournamentId = tournamentId,
            username = username,
            gameUid = gameUid,
            status = "APPROVED" // auto join approve
        )
        esportsDao.insertRegistration(registration)

        // Increment tournament slot count
        esportsDao.incrementRegisteredSlots(tournamentId)

        // Record wallet transaction if paid
        if (tournament.entryType != "FREE") {
            val amount = if (tournament.entryType == "PAID") tournament.entryFee else tournament.entryCoins.toDouble()
            val unit = if (tournament.entryType == "PAID") "$" else " Coins"
            val transaction = WalletTransaction(
                amount = amount,
                type = "ENTRY_FEE",
                status = "COMPLETED",
                timestamp = System.currentTimeMillis(),
                description = "Joined Tournament: ${tournament.title} ($amount$unit fee)"
            )
            esportsDao.insertTransaction(transaction)
        }

        // Save updated user profile
        esportsDao.updateUserProfile(updatedUser)
        return true
    }

    suspend fun insertTransaction(transaction: WalletTransaction) = esportsDao.insertTransaction(transaction)

    suspend fun depositFunds(amount: Double): Boolean {
        val user = esportsDao.getUserProfileOnce() ?: return false
        val updatedUser = user.copy(balanceCash = user.balanceCash + amount)
        esportsDao.updateUserProfile(updatedUser)

        val transaction = WalletTransaction(
            amount = amount,
            type = "DEPOSIT",
            status = "COMPLETED",
            timestamp = System.currentTimeMillis(),
            description = "Deposited Funds via Payment Gateway"
        )
        esportsDao.insertTransaction(transaction)
        return true
    }

    suspend fun withdrawFunds(amount: Double): Boolean {
        val user = esportsDao.getUserProfileOnce() ?: return false
        if (user.balanceCash < amount) return false

        val updatedUser = user.copy(balanceCash = user.balanceCash - amount)
        esportsDao.updateUserProfile(updatedUser)

        val transaction = WalletTransaction(
            amount = amount,
            type = "WITHDRAWAL",
            status = "PENDING", // Withdrawals are pending admin approval
            timestamp = System.currentTimeMillis(),
            description = "Withdrawal Request Submited"
        )
        esportsDao.insertTransaction(transaction)
        return true
    }

    suspend fun createClan(name: String, tag: String): Boolean {
        val user = esportsDao.getUserProfileOnce() ?: return false
        if (user.clanId != null) return false

        // Create clan
        val newClan = Clan(
            name = name,
            tag = tag.uppercase(),
            points = 100,
            logoSeed = (1..10).random(),
            leaderUsername = user.username,
            memberCount = 1
        )
        val clanId = esportsDao.insertClan(newClan)

        // Update user
        esportsDao.updateUserProfile(user.copy(clanId = clanId))

        // System message
        esportsDao.insertClanMessage(
            ClanMessage(
                clanId = clanId,
                sender = "System",
                text = "Clan $name [$tag] was created by ${user.username}!",
                timestamp = System.currentTimeMillis()
            )
        )
        return true
    }

    suspend fun joinClan(clanId: Long): Boolean {
        val user = esportsDao.getUserProfileOnce() ?: return false
        if (user.clanId != null) return false

        val clan = esportsDao.getClanByIdOnce(clanId) ?: return false
        val updatedClan = clan.copy(memberCount = clan.memberCount + 1, points = clan.points + 50)
        esportsDao.updateClan(updatedClan)

        esportsDao.updateUserProfile(user.copy(clanId = clanId))

        esportsDao.insertClanMessage(
            ClanMessage(
                clanId = clanId,
                sender = "System",
                text = "${user.username} has joined the clan!",
                timestamp = System.currentTimeMillis()
            )
        )
        return true
    }

    suspend fun sendClanMessage(clanId: Long, sender: String, text: String) {
        esportsDao.insertClanMessage(
            ClanMessage(
                clanId = clanId,
                sender = sender,
                text = text,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun claimTaskReward(taskId: String): Boolean {
        val task = esportsDao.getRewardTasks().firstOrNull()?.find { it.id == taskId } ?: return false
        if (task.isCompleted) return false

        val user = esportsDao.getUserProfileOnce() ?: return false
        
        // Mark task completed
        esportsDao.updateRewardTask(task.copy(isCompleted = true))

        // Reward XP and Coins
        val finalXp = user.xp + task.xpReward
        var level = user.level
        var xpMax = user.xpMax
        var currXp = finalXp
        
        while (currXp >= xpMax) {
            currXp -= xpMax
            level += 1
            xpMax = (xpMax * 1.25).toInt()
        }

        val updatedUser = user.copy(
            level = level,
            xp = currXp,
            xpMax = xpMax,
            balanceCoins = user.balanceCoins + task.coinReward
        )
        esportsDao.updateUserProfile(updatedUser)

        // Record coin reward transaction
        if (task.coinReward > 0) {
            esportsDao.insertTransaction(
                WalletTransaction(
                    amount = task.coinReward.toDouble(),
                    type = "REFERRAL_BONUS", // Reward system credits
                    status = "COMPLETED",
                    timestamp = System.currentTimeMillis(),
                    description = "Claimed reward for task: ${task.title}"
                )
            )
        }
        return true
    }

    suspend fun applyReferralCode(code: String): Boolean {
        val user = esportsDao.getUserProfileOnce() ?: return false
        if (code.lowercase() == user.referralCode.lowercase()) return false // cannot refer self

        // Reward coins for applying code
        val updatedUser = user.copy(balanceCoins = user.balanceCoins + 200)
        esportsDao.updateUserProfile(updatedUser)

        esportsDao.insertTransaction(
            WalletTransaction(
                amount = 200.0,
                type = "REFERRAL_BONUS",
                status = "COMPLETED",
                timestamp = System.currentTimeMillis(),
                description = "Referral Bonus Code Applied ($code)"
            )
        )
        return true
    }

    suspend fun searchFriends(query: String): List<Friend> = esportsDao.searchFriends(query)

    suspend fun addFriend(username: String, gameUid: String): Boolean {
        val friend = Friend(
            username = username,
            gameUid = gameUid,
            rank = "Heroic",
            status = "ONLINE"
        )
        esportsDao.insertFriend(friend)
        return true
    }

    suspend fun deleteFriend(id: Long) = esportsDao.deleteFriend(id)

    // Admin commands
    suspend fun updateTournamentRoomDetails(tournamentId: Long, roomId: String, pass: String): Boolean {
        val tournament = esportsDao.getTournamentByIdOnce(tournamentId) ?: return false
        val updated = tournament.copy(roomId = roomId, roomPassword = pass)
        esportsDao.insertTournament(updated)
        return true
    }

    suspend fun completeTournament(tournamentId: Long, winner: String, kills: Int, mvp: String): Boolean {
        val tournament = esportsDao.getTournamentByIdOnce(tournamentId) ?: return false
        val updated = tournament.copy(
            status = "COMPLETED",
            winnerUsername = winner,
            winnerKills = kills,
            mvpUsername = mvp
        )
        esportsDao.insertTournament(updated)

        // If the winner is the current user, reward them!
        val user = esportsDao.getUserProfileOnce() ?: return true
        if (user.username.lowercase() == winner.lowercase()) {
            val prize = tournament.prizePool
            val finalUser = user.copy(
                balanceCash = user.balanceCash + prize,
                wins = user.wins + 1,
                totalMatches = user.totalMatches + 1,
                totalKills = user.totalKills + kills
            )
            esportsDao.updateUserProfile(finalUser)

            esportsDao.insertTransaction(
                WalletTransaction(
                    amount = prize,
                    type = "PRIZE_WINNING",
                    status = "COMPLETED",
                    timestamp = System.currentTimeMillis(),
                    description = "Won 1st Place in ${tournament.title}!"
                )
            )
        } else {
            // Increments matches anyway
            val finalUser = user.copy(
                totalMatches = user.totalMatches + 1,
                totalKills = user.totalKills + (0..4).random() // generic match kill increment
            )
            esportsDao.updateUserProfile(finalUser)
        }
        return true
    }

    // Database pre-population
    suspend fun prepopulateDatabase() {
        val existingUser = esportsDao.getUserProfileOnce()
        if (existingUser != null) return // already populated

        // 1. Create default profile for Saurav
        val defaultUser = UserProfile(
            username = "Saurav",
            gameUid = "592837482",
            bio = "Competitive gamer. Creator of Win or Learn platform! 🏆",
            rank = "Grandmaster",
            level = 42,
            xp = 3500,
            xpMax = 10000,
            balanceCash = 180.0,
            balanceCoins = 1500,
            referralCode = "SAURAV99",
            totalMatches = 156,
            totalKills = 642,
            wins = 54
        )
        esportsDao.insertUserProfile(defaultUser)

        // 2. Create sample tournaments
        val currentTime = System.currentTimeMillis()
        val oneHour = 3600 * 1000L

        val tournamentsList = listOf(
            Tournament(
                title = "Free Fire Pro Solo Clash",
                game = "Free Fire",
                status = "UPCOMING",
                entryType = "FREE",
                entryFee = 0.0,
                entryCoins = 0,
                prizePool = 120.00,
                maxSlots = 50,
                registeredSlots = 37,
                matchTime = currentTime + (1.5 * oneHour).toLong(),
                gameType = "Solo",
                rules = "1. Emotes are not allowed in-game.\n2. All hacks or scripts will lead to permanent bans.\n3. Teaming up in Solo matches is strictly prohibited.\n4. Room credentials will release 10 minutes before the match start.",
                mapName = "Bermuda Remastered"
            ),
            Tournament(
                title = "Free Fire Diamond Duo Showdown",
                game = "Free Fire",
                status = "UPCOMING",
                entryType = "PAID",
                entryFee = 5.0,
                entryCoins = 0,
                prizePool = 350.00,
                maxSlots = 25,
                registeredSlots = 12,
                matchTime = currentTime + (4 * oneHour).toLong(),
                gameType = "Duo",
                rules = "1. Only Duo registrations are allowed.\n2. Both members must have verified UIDs.\n3. Standard Bermuda map configuration.",
                mapName = "Kalahari"
            ),
            Tournament(
                title = "BGMI Royal Esports Cup",
                game = "BGMI",
                status = "UPCOMING",
                entryType = "COINS",
                entryFee = 0.0,
                entryCoins = 100,
                prizePool = 1000.0, // prize in coins
                maxSlots = 100,
                registeredSlots = 82,
                matchTime = currentTime + (8 * oneHour).toLong(),
                gameType = "Squad",
                rules = "1. Classic Erangel Map.\n2. Team coordinators must report in Discord.\n3. Room ID will be shared automatically.",
                mapName = "Erangel"
            ),
            Tournament(
                title = "Free Fire Ultimate Squad Brawl",
                game = "Free Fire",
                status = "LIVE",
                entryType = "PAID",
                entryFee = 10.0,
                entryCoins = 0,
                prizePool = 600.00,
                maxSlots = 12,
                registeredSlots = 12,
                matchTime = currentTime - (15 * 60 * 1000), // started 15 mins ago
                gameType = "Squad",
                rules = "1. Squad play only.\n2. Standard Esports guidelines.\n3. Screenshotted results must be uploaded after matches.",
                mapName = "Purgatory",
                roomId = "5829104",
                roomPassword = "wl_esports_9"
            ),
            Tournament(
                title = "Free Fire Creator Invitational",
                game = "Free Fire",
                status = "COMPLETED",
                entryType = "FREE",
                entryFee = 0.0,
                entryCoins = 0,
                prizePool = 500.00,
                maxSlots = 50,
                registeredSlots = 50,
                matchTime = currentTime - (24 * oneHour), // yesterday
                gameType = "Solo",
                rules = "Special guest invitational curated by Saurav. Clean play, top prizes.",
                mapName = "Bermuda",
                roomId = "4910248",
                roomPassword = "win_or_learn_inv",
                winnerUsername = "Saurav",
                winnerKills = 14,
                mvpUsername = "Saurav"
            ),
            Tournament(
                title = "Valorant Spike Rush Speedrun",
                game = "Valorant",
                status = "UPCOMING",
                entryType = "FREE",
                entryFee = 0.0,
                entryCoins = 0,
                prizePool = 150.0,
                maxSlots = 16,
                registeredSlots = 3,
                matchTime = currentTime + (24 * oneHour),
                gameType = "Squad",
                rules = "Future-ready simulation! 5v5 Spike Rush standard rules apply.",
                mapName = "Bind"
            ),
            Tournament(
                title = "Chess Master Blitz Tournament",
                game = "Chess",
                status = "UPCOMING",
                entryType = "COINS",
                entryFee = 0.0,
                entryCoins = 50,
                prizePool = 300.0, // prize in coins
                maxSlots = 32,
                registeredSlots = 11,
                matchTime = currentTime + (48 * oneHour),
                gameType = "Solo",
                rules = "Blitz mode: 5 minutes per player. Direct link will be active.",
                mapName = "Classic Board"
            )
        )

        for (t in tournamentsList) {
            esportsDao.insertTournament(t)
        }

        // 3. Pre-register Saurav for the completed tournament and the Live tournament
        // Query to get inserted IDs is not needed as we can use hardcoded or pre-known ids.
        // Let's register Saurav for tournament id 4 and 5 (the live and completed ones)
        esportsDao.insertRegistration(
            TournamentRegistration(4, "Saurav", "592837482", "APPROVED")
        )
        esportsDao.insertRegistration(
            TournamentRegistration(5, "Saurav", "592837482", "APPROVED")
        )

        // 4. Create sample transactions
        val transactionList = listOf(
            WalletTransaction(
                amount = 200.0,
                type = "DEPOSIT",
                status = "COMPLETED",
                timestamp = currentTime - (12 * oneHour),
                description = "Deposit via UPI payment"
            ),
            WalletTransaction(
                amount = 500.0,
                type = "PRIZE_WINNING",
                status = "COMPLETED",
                timestamp = currentTime - (23 * oneHour),
                description = "Won 1st Place in Free Fire Creator Invitational"
            ),
            WalletTransaction(
                amount = 50.0,
                type = "WITHDRAWAL",
                status = "COMPLETED",
                timestamp = currentTime - (6 * oneHour),
                description = "Withdrew cash to bank account"
            )
        )
        for (tx in transactionList) {
            esportsDao.insertTransaction(tx)
        }

        // 5. Create default clans
        val clanElite = Clan(
            name = "Elite Force",
            tag = "ELF",
            points = 12500,
            logoSeed = 3,
            leaderUsername = "Saurav",
            memberCount = 12
        )
        val clanId = esportsDao.insertClan(clanElite)
        
        // Update Saurav to be part of Elite Force
        esportsDao.updateUserProfile(defaultUser.copy(clanId = clanId))

        val clanShadow = Clan(
            name = "Shadow Assassins",
            tag = "SHA",
            points = 9800,
            logoSeed = 7,
            leaderUsername = "AlphaGamer",
            memberCount = 8
        )
        esportsDao.insertClan(clanShadow)

        // Clan messages
        val messages = listOf(
            ClanMessage(clanId = clanId, sender = "System", text = "Clan created by Saurav!", timestamp = currentTime - (5 * oneHour)),
            ClanMessage(clanId = clanId, sender = "AlphaGamer", text = "Welcome guys! Let's conquer the leaderboard.", timestamp = currentTime - (4 * oneHour)),
            ClanMessage(clanId = clanId, sender = "ViperPlayz", text = "Free Fire Duo tournament is coming up in 2 hours, who is ready?", timestamp = currentTime - (2 * oneHour)),
            ClanMessage(clanId = clanId, sender = "Saurav", text = "I am ready. Let's practice now!", timestamp = currentTime - 30 * 60 * 1000)
        )
        for (msg in messages) {
            esportsDao.insertClanMessage(msg)
        }

        // 6. Create friends
        val friendsList = listOf(
            Friend(username = "AlphaGamer", gameUid = "849203928", rank = "Grandmaster", status = "ONLINE"),
            Friend(username = "ViperPlayz", gameUid = "293049203", rank = "Heroic", status = "ONLINE"),
            Friend(username = "SniperQueen", gameUid = "104920394", rank = "Diamond I", status = "OFFLINE")
        )
        for (fr in friendsList) {
            esportsDao.insertFriend(fr)
        }

        // 7. Create reward tasks
        val tasksList = listOf(
            RewardTask("daily_login", "Daily Check-in Reward", "Open Win or Learn every day to claim bonus Coins!", 20, 50, false),
            RewardTask("first_match", "First Blood Match", "Register and complete 1 tournament match.", 150, 100, false),
            DestroyerTask("clan_join", "Join or Create Clan", "Join hands with an elite team or craft your own clan.", 100, 50, true), // mark default finished
            RewardTask("refer_friend", "Spread the Creed", "Refer 1 gamer friend to the Win or Learn arena.", 250, 200, false)
        )
        for (task in tasksList) {
            esportsDao.insertRewardTask(task)
        }

        // 8. Create announcements
        val newsList = listOf(
            Announcement(
                title = "🎉 Win or Learn Launch Celebration!",
                content = "Welcome to Win or Learn Esports Platform, designed with passion by Saurav! Play your favorite games like Free Fire, improve your skillset with every match, and earn rewards doing what you love.",
                timestamp = currentTime - 2 * oneHour
            ),
            Announcement(
                title = "🚀 Future Ready Tournament Arena",
                content = "BGMI, Valorant, COD Mobile, Chess, eFootball, and Clash Royale brackets are rolling out soon. Set up your clan and train your squad!",
                timestamp = currentTime - oneHour
            )
        )
        for (news in newsList) {
            esportsDao.insertAnnouncement(news)
        }
    }
}

// Utility class extension to bypass private fields or custom entities if needed
private fun DestroyerTask(id: String, title: String, description: String, xp: Int, coins: Int, isDone: Boolean): RewardTask {
    return RewardTask(id, title, description, xp, coins, isDone)
}
