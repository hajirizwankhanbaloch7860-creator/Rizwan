package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// User Session Model
data class UserSession(
    val name: String,
    val email: String,
    val photoUrl: String,
    val authProvider: String,
    val role: String,
    val isSyncEnabled: Boolean
)

// Surah Model (represents a chapter of the Quran)
data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val ayahsCount: Int,
    val revelationType: String
)

// Juz Model (represents a part of the Quran)
data class Juz(
    val number: Int,
    val startSurahNumber: Int,
    val startAyahNumber: Int,
    val endSurahNumber: Int,
    val endAyahNumber: Int
)

// Ayah Model (represents a verse of the Quran)
data class Ayah(
    val numberInSurah: Int,
    val textArabic: String,
    val textEnglish: String,
    val textUrdu: String,
    val words: List<QuranWord> = emptyList()
)

// QuranWord Model
data class QuranWord(
    val word: String,
    val englishMeaning: String,
    val urduMeaning: String,
    val transliteration: String
)

// Hadith Model
data class Hadith(
    val number: String,
    val textArabic: String,
    val textEnglish: String,
    val textUrdu: String,
    val source: String,
    val narrator: String
)

// Mosque Model for Prayer Times
data class Mosque(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val city: String
)

// Room Entities

@Entity(tableName = "cached_ayahs")
data class CachedAyah(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val surahNumber: Int,
    val numberInSurah: Int,
    val textArabic: String,
    val textEnglish: String,
    val textUrdu: String
)

@Entity(tableName = "quran_bookmarks")
data class QuranBookmark(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val surahNumber: Int,
    val surahName: String,
    val ayahNumber: Int,
    val arabicText: String,
    val englishText: String,
    val urduText: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "quran_notes")
data class QuranNote(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val surahNumber: Int,
    val surahName: String,
    val ayahNumber: Int,
    val noteText: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "quran_highlights")
data class QuranHighlight(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val surahNumber: Int,
    val ayahNumber: Int,
    val color: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "hadith_bookmarks")
data class HadithBookmark(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val hadithNumber: String,
    val hadithText: String,
    val source: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "islamic_alarms")
data class IslamicAlarm(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val prayerName: String,
    val time: String,
    val enabled: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tasbeeh_history")
data class TasbeehHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val count: Int,
    val tasbeeh: String,
    val createdAt: Long = System.currentTimeMillis()
)
