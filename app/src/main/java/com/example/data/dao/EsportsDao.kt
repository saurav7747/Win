package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EsportsDao {

    // --- User Profile ---
    @Query("SELECT * FROM users LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(user: UserProfile)

    @Update
    suspend fun updateUserProfile(user: UserProfile)


    // --- Tournaments ---
    @Query("SELECT * FROM tournaments ORDER BY matchTime ASC")
    fun getTournaments(): Flow<List<Tournament>>

    @Query("SELECT * FROM tournaments WHERE id = :id")
    fun getTournamentById(id: Long): Flow<Tournament?>

    @Query("SELECT * FROM tournaments WHERE id = :id")
    suspend fun getTournamentByIdOnce(id: Long): Tournament?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournament(tournament: Tournament)

    @Query("DELETE FROM tournaments WHERE id = :id")
    suspend fun deleteTournament(id: Long)

    @Query("UPDATE tournaments SET registeredSlots = registeredSlots + 1 WHERE id = :id AND registeredSlots < maxSlots")
    suspend fun incrementRegisteredSlots(id: Long): Int


    // --- Registrations ---
    @Query("SELECT * FROM registrations WHERE tournamentId = :tournamentId")
    fun getRegistrationsForTournament(tournamentId: Long): Flow<List<TournamentRegistration>>

    @Query("SELECT * FROM registrations WHERE username = :username")
    fun getRegistrationsForUser(username: String): Flow<List<TournamentRegistration>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistration(registration: TournamentRegistration)

    @Query("DELETE FROM registrations WHERE tournamentId = :tournamentId AND username = :username")
    suspend fun deleteRegistration(tournamentId: Long, username: String)

    @Query("SELECT EXISTS(SELECT 1 FROM registrations WHERE tournamentId = :tournamentId AND username = :username)")
    fun isRegistered(tournamentId: Long, username: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM registrations WHERE tournamentId = :tournamentId AND username = :username)")
    suspend fun isRegisteredOnce(tournamentId: Long, username: String): Boolean


    // --- Transactions ---
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getTransactions(): Flow<List<WalletTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: WalletTransaction)


    // --- Clans ---
    @Query("SELECT * FROM clans ORDER BY points DESC")
    fun getClans(): Flow<List<Clan>>

    @Query("SELECT * FROM clans WHERE id = :id")
    fun getClanById(id: Long): Flow<Clan?>

    @Query("SELECT * FROM clans WHERE id = :id")
    suspend fun getClanByIdOnce(id: Long): Clan?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClan(clan: Clan): Long

    @Update
    suspend fun updateClan(clan: Clan)


    // --- Clan Messages ---
    @Query("SELECT * FROM clan_messages WHERE clanId = :clanId ORDER BY timestamp ASC")
    fun getClanMessages(clanId: Long): Flow<List<ClanMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClanMessage(message: ClanMessage)


    // --- Friends ---
    @Query("SELECT * FROM friends ORDER BY status DESC, username ASC")
    fun getFriends(): Flow<List<Friend>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriend(friend: Friend)

    @Query("DELETE FROM friends WHERE id = :id")
    suspend fun deleteFriend(id: Long)

    @Query("SELECT * FROM friends WHERE username LIKE :query OR gameUid LIKE :query")
    suspend fun searchFriends(query: String): List<Friend>


    // --- Reward Tasks ---
    @Query("SELECT * FROM reward_tasks")
    fun getRewardTasks(): Flow<List<RewardTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRewardTask(task: RewardTask)

    @Update
    suspend fun updateRewardTask(task: RewardTask)


    // --- Announcements ---
    @Query("SELECT * FROM announcements ORDER BY timestamp DESC")
    fun getAnnouncements(): Flow<List<Announcement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: Announcement)
}
