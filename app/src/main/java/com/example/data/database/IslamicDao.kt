package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface IslamicDao {
    // Cached Ayahs
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedAyahs(ayahs: List<CachedAyah>)

    @Query("SELECT * FROM cached_ayahs WHERE surahNumber = :surahNumber")
    suspend fun getCachedAyahsForSurah(surahNumber: Int): List<CachedAyah>

    @Query("SELECT COUNT(*) FROM cached_ayahs")
    suspend fun getCachedAyahsCount(): Int

    @Query("DELETE FROM cached_ayahs")
    suspend fun clearCachedAyahs()

    // Quran Bookmarks
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuranBookmark(bookmark: QuranBookmark)

    @Query("SELECT * FROM quran_bookmarks ORDER BY createdAt DESC")
    fun getAllQuranBookmarks(): Flow<List<QuranBookmark>>

    @Query("DELETE FROM quran_bookmarks WHERE surahNumber = :surahNumber AND ayahNumber = :ayahNumber")
    suspend fun deleteQuranBookmarkByVerse(surahNumber: Int, ayahNumber: Int)

    @Query("DELETE FROM quran_bookmarks WHERE id = :id")
    suspend fun deleteQuranBookmark(id: Int)

    // Quran Notes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuranNote(note: QuranNote)

    @Query("SELECT * FROM quran_notes ORDER BY createdAt DESC")
    fun getAllQuranNotes(): Flow<List<QuranNote>>

    @Query("DELETE FROM quran_notes WHERE id = :id")
    suspend fun deleteQuranNote(id: Int)

    // Quran Highlights
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuranHighlight(highlight: QuranHighlight)

    @Query("SELECT * FROM quran_highlights WHERE surahNumber = :surahNumber")
    fun getQuranHighlightsForSurah(surahNumber: Int): Flow<List<QuranHighlight>>

    @Query("SELECT * FROM quran_highlights ORDER BY createdAt DESC")
    fun getAllQuranHighlights(): Flow<List<QuranHighlight>>

    @Query("DELETE FROM quran_highlights WHERE id = :id")
    suspend fun deleteQuranHighlight(id: Int)

    // Hadith Bookmarks
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHadithBookmark(bookmark: HadithBookmark)

    @Query("SELECT * FROM hadith_bookmarks ORDER BY createdAt DESC")
    fun getAllHadithBookmarks(): Flow<List<HadithBookmark>>

    @Query("DELETE FROM hadith_bookmarks WHERE id = :id")
    suspend fun deleteHadithBookmark(id: Int)

    // Islamic Alarms
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIslamicAlarm(alarm: IslamicAlarm)

    @Query("SELECT * FROM islamic_alarms WHERE prayerName = :prayerName")
    suspend fun getAlarmForPrayer(prayerName: String): IslamicAlarm?

    @Query("SELECT * FROM islamic_alarms")
    fun getAllIslamicAlarms(): Flow<List<IslamicAlarm>>

    @Query("DELETE FROM islamic_alarms WHERE id = :id")
    suspend fun deleteIslamicAlarm(id: Int)

    // Tasbeeh History
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasbeehHistory(history: TasbeehHistory)

    @Query("SELECT * FROM tasbeeh_history ORDER BY createdAt DESC")
    fun getAllTasbeehHistory(): Flow<List<TasbeehHistory>>

    @Query("DELETE FROM tasbeeh_history WHERE id = :id")
    suspend fun deleteTasbeehHistory(id: Int)
}
