package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quran_bookmarks")
data class QuranBookmark(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val surahNumber: Int,
    val surahName: String,
    val ayahNumber: Int,
    val arabicText: String,
    val englishText: String,
    val urduText: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "quran_notes")
data class QuranNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val surahNumber: Int,
    val surahName: String,
    val ayahNumber: Int,
    val noteText: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "quran_highlights")
data class QuranHighlight(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val surahNumber: Int,
    val ayahNumber: Int,
    val colorHex: String, // e.g., "#FFEB3B" (yellow)
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "tasbeeh_history")
data class TasbeehItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val currentCount: Int,
    val targetCount: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "hadith_bookmarks")
data class HadithBookmark(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookName: String,
    val chapterName: String,
    val hadithNumber: String,
    val arabicText: String,
    val englishTranslation: String,
    val urduTranslation: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "islamic_alarms")
data class IslamicAlarm(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val label: String, // e.g., "Fajr Alarm", "Quran Reading Reminder", "Suhoor Alarm"
    val alarmType: String, // "FAJR", "TAHAJJUD", "PRAYER", "QURAN", "HADITH", "SUHOOR", "IFTAR", "CUSTOM"
    val timeString: String, // "04:30"
    val isEnabled: Boolean = true,
    val isRecurring: Boolean = true,
    val soundUri: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
