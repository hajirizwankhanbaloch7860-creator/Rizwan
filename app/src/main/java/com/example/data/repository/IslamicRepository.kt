package com.example.data.repository

import com.example.data.database.IslamicDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class IslamicRepository(private val dao: IslamicDao) {

    // Cached Ayahs
    suspend fun insertCachedAyahs(ayahs: List<CachedAyah>) = dao.insertCachedAyahs(ayahs)

    suspend fun getCachedAyahsForSurah(surahNumber: Int): List<CachedAyah> =
        dao.getCachedAyahsForSurah(surahNumber)

    suspend fun getCachedAyahsCount(): Int = dao.getCachedAyahsCount()

    suspend fun clearCachedAyahs() = dao.clearCachedAyahs()

    // Quran Bookmarks
    suspend fun insertQuranBookmark(bookmark: QuranBookmark) = dao.insertQuranBookmark(bookmark)

    val allQuranBookmarks: Flow<List<QuranBookmark>> = dao.getAllQuranBookmarks()

    suspend fun deleteQuranBookmarkByVerse(surahNumber: Int, ayahNumber: Int) =
        dao.deleteQuranBookmarkByVerse(surahNumber, ayahNumber)

    suspend fun deleteQuranBookmark(id: Int) = dao.deleteQuranBookmark(id)

    // Quran Notes
    suspend fun insertQuranNote(note: QuranNote) = dao.insertQuranNote(note)

    val allQuranNotes: Flow<List<QuranNote>> = dao.getAllQuranNotes()

    suspend fun deleteQuranNote(id: Int) = dao.deleteQuranNote(id)

    // Quran Highlights
    suspend fun insertQuranHighlight(highlight: QuranHighlight) =
        dao.insertQuranHighlight(highlight)

    fun getQuranHighlightsForSurah(surahNumber: Int): Flow<List<QuranHighlight>> =
        dao.getQuranHighlightsForSurah(surahNumber)

    val allQuranHighlights: Flow<List<QuranHighlight>> = dao.getAllQuranHighlights()

    suspend fun deleteQuranHighlight(id: Int) = dao.deleteQuranHighlight(id)

    // Hadith Bookmarks
    suspend fun insertHadithBookmark(bookmark: HadithBookmark) =
        dao.insertHadithBookmark(bookmark)

    val allHadithBookmarks: Flow<List<HadithBookmark>> = dao.getAllHadithBookmarks()

    suspend fun deleteHadithBookmark(id: Int) = dao.deleteHadithBookmark(id)

    // Islamic Alarms
    suspend fun insertIslamicAlarm(alarm: IslamicAlarm) = dao.insertIslamicAlarm(alarm)

    suspend fun getAlarmForPrayer(prayerName: String): IslamicAlarm? =
        dao.getAlarmForPrayer(prayerName)

    val allIslamicAlarms: Flow<List<IslamicAlarm>> = dao.getAllIslamicAlarms()

    suspend fun deleteIslamicAlarm(id: Int) = dao.deleteIslamicAlarm(id)

    // Tasbeeh History
    suspend fun insertTasbeehHistory(history: TasbeehHistory) =
        dao.insertTasbeehHistory(history)

    val allTasbeehHistory: Flow<List<TasbeehHistory>> = dao.getAllTasbeehHistory()

    suspend fun deleteTasbeehHistory(id: Int) = dao.deleteTasbeehHistory(id)
}
