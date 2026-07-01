package com.example.data.model

data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val nameTranslation: String,
    val type: String, // "Meccan" or "Medinan"
    val ayahsCount: Int
)

data class Ayah(
    val numberInSurah: Int,
    val textArabic: String,
    val textEnglish: String,
    val textUrdu: String,
    val words: List<QuranWord> = emptyList()
)

data class QuranWord(
    val arabic: String,
    val english: String,
    val urdu: String,
    val pronunciation: String // Transliteration phonetics
)

data class HadithBook(
    val key: String,
    val nameEnglish: String,
    val nameArabic: String,
    val nameUrdu: String,
    val author: String,
    val totalHadith: Int,
    val rating: String // "Authentic (Sahih)" or "Sunan"
)

data class Hadith(
    val number: String,
    val textArabic: String,
    val textEnglish: String,
    val textUrdu: String,
    val narrator: String,
    val chapter: String
)

data class Dua(
    val id: Int,
    val title: String,
    val category: String, // "Morning/Evening", "Prayer", "Travel", "Protection", "Sickness"
    val textArabic: String,
    val translationEnglish: String,
    val translationUrdu: String,
    val reference: String
)

data class NameOfAllah(
    val number: Int,
    val arabic: String,
    val transliteration: String,
    val translationEnglish: String,
    val translationUrdu: String,
    val benefits: String
)

data class IslamicEvent(
    val nameEnglish: String,
    val nameUrdu: String,
    val nameArabic: String,
    val dateHijri: String, // e.g., "1st Shawwal"
    val description: String
)

data class Mosque(
    val name: String,
    val distance: String,
    val address: String,
    val rating: Float,
    val latitude: Double,
    val longitude: Double
)

data class Juz(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val startSurahNumber: Int,
    val startSurahName: String,
    val startAyahNumber: Int,
    val description: String
)

