package com.example.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        CachedAyah::class,
        QuranBookmark::class,
        QuranNote::class,
        QuranHighlight::class,
        HadithBookmark::class,
        IslamicAlarm::class,
        TasbeehHistory::class
    ],
    version = 1,
    exportSchema = false
)
abstract class IslamicDatabase : RoomDatabase() {
    abstract fun islamicDao(): IslamicDao
}
