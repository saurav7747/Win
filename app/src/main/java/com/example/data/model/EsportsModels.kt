package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "users")
data class UserProfile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val gameUid: String,
    val bio: String,
    val rank: String,
    val level: Int,
    val xp: Int,
    val xpMax: Int,
    val balanceCash: Double,
    val balanceCoins: Int,
    val clanId: Long? = null,
    val referralCode: String,
    val totalMatches: Int = 0,
    val totalKills: Int = 0,
    val wins: Int = 0,
    val xpMultiplier: Float = 1.0f
) : Serializable

@Entity(tableName = "tournaments")
data class Tournament(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val game: String, // Free Fire, BGMI, COD Mobile, Valorant, eFootball, Chess, etc.
    val status: String, // UPCOMING, LIVE, COMPLETED
    val entryType: String, // FREE, PAID, COINS
    val entryFee: Double,
    val entryCoins: Int,
    val prizePool: Double,
    val maxSlots: Int,
    val registeredSlots: Int,
    val matchTime: Long, // timestamp
    val gameType: String, // Solo, Duo, Squad
    val rules: String,
    val mapName: String,
    val roomId: String = "",
    val roomPassword: String = "",
    val winnerUsername: String? = null,
    val winnerKills: Int? = null,
    val mvpUsername: String? = null
) : Serializable

@Entity(tableName = "registrations", primaryKeys = ["tournamentId", "username"])
data class TournamentRegistration(
    val tournamentId: Long,
    val username: String,
    val gameUid: String,
    val status: String // PENDING, APPROVED
) : Serializable

@Entity(tableName = "transactions")
data class WalletTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val type: String, // DEPOSIT, WITHDRAWAL, ENTRY_FEE, PRIZE_WINNING, REFERRAL_BONUS
    val status: String, // PENDING, COMPLETED, FAILED
    val timestamp: Long,
    val description: String
) : Serializable

@Entity(tableName = "clans")
data class Clan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val tag: String,
    val points: Int,
    val logoSeed: Int,
    val leaderUsername: String,
    val memberCount: Int
) : Serializable

@Entity(tableName = "clan_messages")
data class ClanMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clanId: Long,
    val sender: String,
    val text: String,
    val timestamp: Long
) : Serializable

@Entity(tableName = "friends")
data class Friend(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val gameUid: String,
    val rank: String,
    val status: String // ONLINE, OFFLINE
) : Serializable

@Entity(tableName = "reward_tasks")
data class RewardTask(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val xpReward: Int,
    val coinReward: Int,
    val isCompleted: Boolean = false
) : Serializable

@Entity(tableName = "announcements")
data class Announcement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val timestamp: Long
) : Serializable
