package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.EsportsDao
import com.example.data.model.*

@Database(
    entities = [
        UserProfile::class,
        Tournament::class,
        TournamentRegistration::class,
        WalletTransaction::class,
        Clan::class,
        ClanMessage::class,
        Friend::class,
        RewardTask::class,
        Announcement::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun esportsDao(): EsportsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "win_or_learn_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
