package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.data.repository.EsportsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EsportsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EsportsRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = EsportsRepository(database.esportsDao())
        
        // Populate sample data if needed on startup in background
        viewModelScope.launch {
            repository.prepopulateDatabase()
        }
    }

    // Reactively exposed flows
    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val tournaments: StateFlow<List<Tournament>> = repository.tournaments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<WalletTransaction>> = repository.transactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val clans: StateFlow<List<Clan>> = repository.clans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val friends: StateFlow<List<Friend>> = repository.friends
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rewardTasks: StateFlow<List<RewardTask>> = repository.rewardTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val announcements: StateFlow<List<Announcement>> = repository.announcements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI state states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _joinStatus = MutableSharedFlow<Boolean>()
    val joinStatus = _joinStatus.asSharedFlow()

    private val _operationMessage = MutableSharedFlow<String>()
    val operationMessage = _operationMessage.asSharedFlow()

    // Get current user's clan reactively
    val userClan: StateFlow<Clan?> = userProfile
        .flatMapLatest { profile ->
            if (profile?.clanId != null) {
                repository.getClanById(profile.clanId)
            } else {
                flowOf(null)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Get active clan chat
    val clanMessages: StateFlow<List<ClanMessage>> = userProfile
        .flatMapLatest { profile ->
            if (profile?.clanId != null) {
                repository.getClanMessages(profile.clanId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Get registered tournament IDs for current user
    val userRegisteredTournaments: StateFlow<List<Long>> = userProfile
        .flatMapLatest { profile ->
            if (profile != null) {
                repository.getRegistrationsForUser(profile.username).map { list ->
                    list.map { it.tournamentId }
                }
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Actions ---

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun joinTournament(tournamentId: Long) {
        viewModelScope.launch {
            val user = userProfile.value ?: return@launch
            val success = repository.joinTournament(tournamentId, user.username, user.gameUid)
            _joinStatus.emit(success)
            if (success) {
                _operationMessage.emit("Successfully joined tournament!")
            } else {
                _operationMessage.emit("Failed to join. Check funds or slot availability.")
            }
        }
    }

    fun depositFunds(amount: Double) {
        viewModelScope.launch {
            val success = repository.depositFunds(amount)
            if (success) {
                _operationMessage.emit("Deposited $${String.format("%.2f", amount)} successfully!")
            }
        }
    }

    fun withdrawFunds(amount: Double) {
        viewModelScope.launch {
            val success = repository.withdrawFunds(amount)
            if (success) {
                _operationMessage.emit("Withdrawal request for $${String.format("%.2f", amount)} submitted! Awaiting admin approval.")
            } else {
                _operationMessage.emit("Insufficient balance to withdraw $${String.format("%.2f", amount)}.")
            }
        }
    }

    fun createClan(name: String, tag: String) {
        viewModelScope.launch {
            val success = repository.createClan(name, tag)
            if (success) {
                _operationMessage.emit("Clan '$name' created successfully!")
            } else {
                _operationMessage.emit("Failed to create clan. Are you already in a clan?")
            }
        }
    }

    fun joinClan(clanId: Long) {
        viewModelScope.launch {
            val success = repository.joinClan(clanId)
            if (success) {
                _operationMessage.emit("Welcome to your new clan!")
            } else {
                _operationMessage.emit("Failed to join clan.")
            }
        }
    }

    fun sendClanMessage(text: String) {
        val user = userProfile.value ?: return
        val clanId = user.clanId ?: return
        viewModelScope.launch {
            repository.sendClanMessage(clanId, user.username, text)
        }
    }

    fun claimTaskReward(taskId: String) {
        viewModelScope.launch {
            val success = repository.claimTaskReward(taskId)
            if (success) {
                _operationMessage.emit("Reward claimed! Level/XP updated successfully.")
            }
        }
    }

    fun applyReferralCode(code: String) {
        viewModelScope.launch {
            val success = repository.applyReferralCode(code)
            if (success) {
                _operationMessage.emit("Referral applied! 200 Coins added to your wallet.")
            } else {
                _operationMessage.emit("Invalid referral code. You cannot refer yourself.")
            }
        }
    }

    fun addFriend(username: String, gameUid: String) {
        viewModelScope.launch {
            val success = repository.addFriend(username, gameUid)
            if (success) {
                _operationMessage.emit("$username added to friends list.")
            }
        }
    }

    fun deleteFriend(id: Long) {
        viewModelScope.launch {
            repository.deleteFriend(id)
            _operationMessage.emit("Friend removed.")
        }
    }

    fun registerNewUser(username: String, gameUid: String, bio: String): Boolean {
        if (username.isBlank() || gameUid.isBlank()) return false
        viewModelScope.launch {
            val newUser = UserProfile(
                username = username,
                gameUid = gameUid,
                bio = bio.ifBlank { "Elite Esports player!" },
                rank = "Diamond III",
                level = 1,
                xp = 0,
                xpMax = 1000,
                balanceCash = 50.0, // starter signup cash
                balanceCoins = 100, // starter signup coins
                referralCode = "${username.uppercase()}${ (10..99).random() }"
            )
            repository.insertUserProfile(newUser)
            _operationMessage.emit("Profile created! Welcome $username.")
        }
        return true
    }

    // --- Admin Dashboard Actions ---

    fun createTournamentAdmin(
        title: String,
        game: String,
        entryType: String,
        entryFee: Double,
        entryCoins: Int,
        prizePool: Double,
        maxSlots: Int,
        gameType: String,
        mapName: String,
        hoursFromNow: Int
    ) {
        viewModelScope.launch {
            val matchTime = System.currentTimeMillis() + (hoursFromNow * 3600 * 1000L)
            val newTournament = Tournament(
                title = title,
                game = game,
                status = "UPCOMING",
                entryType = entryType,
                entryFee = entryFee,
                entryCoins = entryCoins,
                prizePool = prizePool,
                maxSlots = maxSlots,
                registeredSlots = 0,
                matchTime = matchTime,
                gameType = gameType,
                rules = "1. Play honestly. No hacking.\n2. Room ID will be released 10 mins before match start.\n3. Screen grab or results must be submitted for manual verification.",
                mapName = mapName
            )
            repository.insertTournament(newTournament)
            _operationMessage.emit("Tournament created successfully!")
        }
    }

    fun updateRoomDetailsAdmin(tournamentId: Long, roomId: String, pass: String) {
        viewModelScope.launch {
            val success = repository.updateTournamentRoomDetails(tournamentId, roomId, pass)
            if (success) {
                _operationMessage.emit("Room credentials updated and sent!")
            }
        }
    }

    fun completeTournamentAdmin(tournamentId: Long, winner: String, kills: Int, mvp: String) {
        viewModelScope.launch {
            val success = repository.completeTournament(tournamentId, winner, kills, mvp)
            if (success) {
                _operationMessage.emit("Tournament completed! Rewards disbursed to $winner.")
            }
        }
    }

    fun deleteTournamentAdmin(id: Long) {
        viewModelScope.launch {
            repository.deleteTournament(id)
            _operationMessage.emit("Tournament removed.")
        }
    }
}

// Factory for standard viewmodel provider instantiation
class EsportsViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EsportsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EsportsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
