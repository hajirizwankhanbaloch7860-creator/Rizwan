package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface IslamicDao {

    // --- Quran Bookmarks ---
    @Query("SELECT * FROM quran_bookmarks ORDER BY timestamp DESC")
    fun getAllQuranBookmarks(): Flow<List<QuranBookmark>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuranBookmark(bookmark: QuranBookmark)

    @Query("DELETE FROM quran_bookmarks WHERE surahNumber = :surah AND ayahNumber = :ayah")
    suspend fun deleteQuranBookmarkByVerse(surah: Int, ayah: Int)

    @Query("DELETE FROM quran_bookmarks WHERE id = :id")
    suspend fun deleteQuranBookmarkById(id: Int)

    @Query("SELECT EXISTS(SELECT * FROM quran_bookmarks WHERE surahNumber = :surah AND ayahNumber = :ayah)")
    fun isQuranBookmarkedFlow(surah: Int, ayah: Int): Flow<Boolean>

    // --- Quran Notes ---
    @Query("SELECT * FROM quran_notes ORDER BY timestamp DESC")
    fun getAllQuranNotes(): Flow<List<QuranNote>>

    @Query("SELECT * FROM quran_notes WHERE surahNumber = :surah AND ayahNumber = :ayah LIMIT 1")
    suspend fun getNoteForVerse(surah: Int, ayah: Int): QuranNote?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuranNote(note: QuranNote)

    @Query("DELETE FROM quran_notes WHERE surahNumber = :surah AND ayahNumber = :ayah")
    suspend fun deleteQuranNoteByVerse(surah: Int, ayah: Int)

    // --- Quran Highlights ---
    @Query("SELECT * FROM quran_highlights")
    fun getAllQuranHighlights(): Flow<List<QuranHighlight>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuranHighlight(highlight: QuranHighlight)

    @Query("DELETE FROM quran_highlights WHERE surahNumber = :surah AND ayahNumber = :ayah")
    suspend fun deleteQuranHighlightByVerse(surah: Int, ayah: Int)

    // --- Tasbeeh Items ---
    @Query("SELECT * FROM tasbeeh_history ORDER BY timestamp DESC")
    fun getAllTasbeehHistory(): Flow<List<TasbeehItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasbeehItem(item: TasbeehItem)

    @Query("DELETE FROM tasbeeh_history WHERE id = :id")
    suspend fun deleteTasbeehItem(id: Int)

    @Query("DELETE FROM tasbeeh_history")
    suspend fun clearAllTasbeehHistory()

    // --- Hadith Bookmarks ---
    @Query("SELECT * FROM hadith_bookmarks ORDER BY timestamp DESC")
    fun getAllHadithBookmarks(): Flow<List<HadithBookmark>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHadithBookmark(bookmark: HadithBookmark)

    @Query("DELETE FROM hadith_bookmarks WHERE bookName = :bookName AND hadithNumber = :hadithNumber")
    suspend fun deleteHadithBookmark(bookName: String, hadithNumber: String)

    @Query("SELECT EXISTS(SELECT * FROM hadith_bookmarks WHERE bookName = :bookName AND hadithNumber = :hadithNumber)")
    fun isHadithBookmarkedFlow(bookName: String, hadithNumber: String): Flow<Boolean>

    // --- Islamic Alarms ---
    @Query("SELECT * FROM islamic_alarms ORDER BY timeString ASC")
    fun getAllIslamicAlarms(): Flow<List<IslamicAlarm>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIslamicAlarm(alarm: IslamicAlarm)

    @Query("DELETE FROM islamic_alarms WHERE id = :id")
    suspend fun deleteIslamicAlarm(id: Int)
}
