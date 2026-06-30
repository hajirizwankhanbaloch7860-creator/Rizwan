package com.example.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        QuranBookmark::class,
        QuranNote::class,
        QuranHighlight::class,
        TasbeehItem::class,
        HadithBookmark::class,
        IslamicAlarm::class
    ],
    version = 1,
    exportSchema = false
)
abstract class IslamicDatabase : RoomDatabase() {
    abstract fun islamicDao(): IslamicDao
}
