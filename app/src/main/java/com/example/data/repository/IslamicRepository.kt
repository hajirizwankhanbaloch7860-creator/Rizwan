package com.example.data.repository

import com.example.data.database.*
import kotlinx.coroutines.flow.Flow

class IslamicRepository(private val islamicDao: IslamicDao) {

    // --- Quran Bookmarks ---
    val allQuranBookmarks: Flow<List<QuranBookmark>> = islamicDao.getAllQuranBookmarks()

    suspend fun insertQuranBookmark(bookmark: QuranBookmark) {
        islamicDao.insertQuranBookmark(bookmark)
    }

    suspend fun deleteQuranBookmarkByVerse(surah: Int, ayah: Int) {
        islamicDao.deleteQuranBookmarkByVerse(surah, ayah)
    }

    suspend fun deleteQuranBookmarkById(id: Int) {
        islamicDao.deleteQuranBookmarkById(id)
    }

    fun isQuranBookmarked(surah: Int, ayah: Int): Flow<Boolean> {
        return islamicDao.isQuranBookmarkedFlow(surah, ayah)
    }

    // --- Quran Notes ---
    val allQuranNotes: Flow<List<QuranNote>> = islamicDao.getAllQuranNotes()

    suspend fun getNoteForVerse(surah: Int, ayah: Int): QuranNote? {
        return islamicDao.getNoteForVerse(surah, ayah)
    }

    suspend fun insertQuranNote(note: QuranNote) {
        islamicDao.insertQuranNote(note)
    }

    suspend fun deleteQuranNoteByVerse(surah: Int, ayah: Int) {
        islamicDao.deleteQuranNoteByVerse(surah, ayah)
    }

    // --- Quran Highlights ---
    val allQuranHighlights: Flow<List<QuranHighlight>> = islamicDao.getAllQuranHighlights()

    suspend fun insertQuranHighlight(highlight: QuranHighlight) {
        islamicDao.insertQuranHighlight(highlight)
    }

    suspend fun deleteQuranHighlightByVerse(surah: Int, ayah: Int) {
        islamicDao.deleteQuranHighlightByVerse(surah, ayah)
    }

    // --- Tasbeeh Items ---
    val allTasbeehHistory: Flow<List<TasbeehItem>> = islamicDao.getAllTasbeehHistory()

    suspend fun insertTasbeehItem(item: TasbeehItem) {
        islamicDao.insertTasbeehItem(item)
    }

    suspend fun deleteTasbeehItem(id: Int) {
        islamicDao.deleteTasbeehItem(id)
    }

    suspend fun clearAllTasbeehHistory() {
        islamicDao.clearAllTasbeehHistory()
    }

    // --- Hadith Bookmarks ---
    val allHadithBookmarks: Flow<List<HadithBookmark>> = islamicDao.getAllHadithBookmarks()

    suspend fun insertHadithBookmark(bookmark: HadithBookmark) {
        islamicDao.insertHadithBookmark(bookmark)
    }

    suspend fun deleteHadithBookmark(bookName: String, hadithNumber: String) {
        islamicDao.deleteHadithBookmark(bookName, hadithNumber)
    }

    fun isHadithBookmarked(bookName: String, hadithNumber: String): Flow<Boolean> {
        return islamicDao.isHadithBookmarkedFlow(bookName, hadithNumber)
    }

    // --- Islamic Alarms ---
    val allIslamicAlarms: Flow<List<IslamicAlarm>> = islamicDao.getAllIslamicAlarms()

    suspend fun insertIslamicAlarm(alarm: IslamicAlarm) {
        islamicDao.insertIslamicAlarm(alarm)
    }

    suspend fun deleteIslamicAlarm(id: Int) {
        islamicDao.deleteIslamicAlarm(id)
    }
}
