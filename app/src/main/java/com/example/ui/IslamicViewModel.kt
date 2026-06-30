package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.database.*
import com.example.data.model.*
import com.example.data.repository.IslamicRepository
import com.example.network.GeminiClient
import com.example.utils.IslamicDateTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class IslamicViewModel(application: Application) : AndroidViewModel(application) {

    // --- Database & Repository Setup (Constructor Injection / Service Locator Style) ---
    private val database: IslamicDatabase by lazy {
        Room.databaseBuilder(
            application,
            IslamicDatabase::class.java,
            "islamic_companion_db"
        ).fallbackToDestructiveMigration().build()
    }

    private val repository: IslamicRepository by lazy {
        IslamicRepository(database.islamicDao())
    }

    // --- Authentication & User Identity State ---
    private val _currentUser = MutableStateFlow<UserSession?>(null)
    val currentUser: StateFlow<UserSession?> = _currentUser.asStateFlow()

    fun loginAs(method: String, username: String = "") {
        val role = if (username.lowercase() == "admin") "ADMIN" else "USER"
        _currentUser.value = UserSession(
            name = username.ifEmpty { "Guest Pilgrim" },
            email = if (username.isNotEmpty()) "$username@example.com" else "pilgrim@islamiccompanion.org",
            photoUrl = "",
            authProvider = method,
            role = role,
            isSyncEnabled = true
        )
    }

    fun logout() {
        _currentUser.value = null
    }

    // --- Location & Coordinates State (Defaults to Faisal Mosque, Islamabad) ---
    private val _userLatitude = MutableStateFlow(33.7299)
    val userLatitude: StateFlow<Double> = _userLatitude.asStateFlow()

    private val _userLongitude = MutableStateFlow(73.0373)
    val userLongitude: StateFlow<Double> = _userLongitude.asStateFlow()

    private val _currentAddress = MutableStateFlow("Faisal Mosque Area, Islamabad, Pakistan")
    val currentAddress: StateFlow<String> = _currentAddress.asStateFlow()

    fun updateLocation(lat: Double, lng: Double, address: String) {
        _userLatitude.value = lat
        _userLongitude.value = lng
        _currentAddress.value = address
    }

    // --- Prayer Times & Hijri Calendar State ---
    val prayerTimes: StateFlow<IslamicDateTimeUtils.PrayerTimes> = combine(userLatitude, userLongitude) { lat, lng ->
        IslamicDateTimeUtils.getPrayerTimes(lat, lng)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), IslamicDateTimeUtils.getPrayerTimes(33.7299, 73.0373))

    private val _hijriOffset = MutableStateFlow(0)
    val hijriOffset: StateFlow<Int> = _hijriOffset.asStateFlow()

    val hijriDate: StateFlow<IslamicDateTimeUtils.HijriDate> = _hijriOffset.map { offset ->
        IslamicDateTimeUtils.getHijriDate(Calendar.getInstance(), offset)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), IslamicDateTimeUtils.getHijriDate())

    fun adjustHijriDate(offsetChange: Int) {
        _hijriOffset.value += offsetChange
    }

    // --- Qibla Compass Angle State ---
    val qiblaAngle: StateFlow<Double> = combine(userLatitude, userLongitude) { lat, lng ->
        IslamicDateTimeUtils.calculateQiblaAngle(lat, lng)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 118.0)

    // --- Quran Reader & Audio States ---
    val surahList: List<Surah> = QuranDataProvider.getSurahList()

    private val _selectedSurah = MutableStateFlow<Surah?>(null)
    val selectedSurah: StateFlow<Surah?> = _selectedSurah.asStateFlow()

    private val _ayahList = MutableStateFlow<List<Ayah>>(emptyList())
    val ayahList: StateFlow<List<Ayah>> = _ayahList.asStateFlow()

    private val _currentPlayingAyah = MutableStateFlow<Int?>(null)
    val currentPlayingAyah: StateFlow<Int?> = _currentPlayingAyah.asStateFlow()

    private val _activeReciter = MutableStateFlow("Sheikh Mishary Alafasy")
    val activeReciter: StateFlow<String> = _activeReciter.asStateFlow()

    private val _isAudioPlaying = MutableStateFlow(false)
    val isAudioPlaying: StateFlow<Boolean> = _isAudioPlaying.asStateFlow()

    val reciters = listOf("Sheikh Mishary Alafasy", "Sheikh Abdul Rahman Al-Sudais", "Sheikh Saad Al-Ghamdi")

    fun selectSurah(surah: Surah) {
        _selectedSurah.value = surah
        _ayahList.value = QuranDataProvider.getAyahsForSurah(surah.number)
        _currentPlayingAyah.value = null
        _isAudioPlaying.value = false
    }

    fun selectReciter(reciter: String) {
        _activeReciter.value = reciter
    }

    fun toggleAudioPlay() {
        if (_ayahList.value.isNotEmpty()) {
            _isAudioPlaying.value = !_isAudioPlaying.value
            if (_isAudioPlaying.value && _currentPlayingAyah.value == null) {
                _currentPlayingAyah.value = 1
            }
        }
    }

    fun playNextAyah() {
        val current = _currentPlayingAyah.value ?: return
        if (current < _ayahList.value.size) {
            _currentPlayingAyah.value = current + 1
        } else {
            _currentPlayingAyah.value = 1 // loop back
        }
    }

    fun playPreviousAyah() {
        val current = _currentPlayingAyah.value ?: return
        if (current > 1) {
            _currentPlayingAyah.value = current - 1
        } else {
            _currentPlayingAyah.value = _ayahList.value.size
        }
    }

    // --- SQLite Flows via Room Repository ---
    val quranBookmarks = repository.allQuranBookmarks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val quranNotes = repository.allQuranNotes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val quranHighlights = repository.allQuranHighlights.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val hadithBookmarks = repository.allHadithBookmarks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val islamicAlarms = repository.allIslamicAlarms.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val tasbeehHistory = repository.allTasbeehHistory.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Bookmarking logic
    fun toggleQuranBookmark(surah: Surah, ayah: Ayah) {
        viewModelScope.launch(Dispatchers.IO) {
            val isBookmarked = quranBookmarks.value.any { it.surahNumber == surah.number && it.ayahNumber == ayah.numberInSurah }
            if (isBookmarked) {
                repository.deleteQuranBookmarkByVerse(surah.number, ayah.numberInSurah)
            } else {
                repository.insertQuranBookmark(
                    QuranBookmark(
                        surahNumber = surah.number,
                        surahName = surah.nameEnglish,
                        ayahNumber = ayah.numberInSurah,
                        arabicText = ayah.textArabic,
                        englishText = ayah.textEnglish,
                        urduText = ayah.textUrdu
                    )
                )
            }
        }
    }

    // Notes logic
    fun addQuranNote(surah: Surah, ayah: Ayah, noteText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertQuranNote(
                QuranNote(
                    surahNumber = surah.number,
                    surahName = surah.nameEnglish,
                    ayahNumber = ayah.numberInSurah,
                    noteText = noteText
                )
            )
        }
    }

    fun deleteQuranNote(surah: Surah, ayah: Ayah) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteQuranNoteByVerse(surah.number, ayah.numberInSurah)
        }
    }

    // Highlights logic
    fun highlightVerse(surahNumber: Int, ayahNumber: Int, colorHex: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertQuranHighlight(
                QuranHighlight(
                    surahNumber = surahNumber,
                    ayahNumber = ayahNumber,
                    colorHex = colorHex
                )
            )
        }
    }

    fun clearHighlight(surahNumber: Int, ayahNumber: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteQuranHighlightByVerse(surahNumber, ayahNumber)
        }
    }

    // --- Hadith Collection & Bookmarks States ---
    val hadithBooks = QuranDataProvider.getHadithBooks()

    private val _selectedHadithBook = MutableStateFlow<HadithBook?>(null)
    val selectedHadithBook: StateFlow<HadithBook?> = _selectedHadithBook.asStateFlow()

    private val _hadithList = MutableStateFlow<List<Hadith>>(emptyList())
    val hadithList: StateFlow<List<Hadith>> = _hadithList.asStateFlow()

    private val _hadithSearchQuery = MutableStateFlow("")
    val hadithSearchQuery: StateFlow<String> = _hadithSearchQuery.asStateFlow()

    val filteredHadithList: StateFlow<List<Hadith>> = combine(_hadithList, _hadithSearchQuery) { list, query ->
        if (query.isEmpty()) list
        else {
            list.filter {
                it.number.contains(query, ignoreCase = true) ||
                it.textEnglish.contains(query, ignoreCase = true) ||
                it.textUrdu.contains(query, ignoreCase = true) ||
                it.narrator.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectHadithBook(book: HadithBook) {
        _selectedHadithBook.value = book
        _hadithList.value = QuranDataProvider.getHadithList(book.key)
    }

    fun setHadithSearchQuery(query: String) {
        _hadithSearchQuery.value = query
    }

    fun toggleHadithBookmark(book: HadithBook, hadith: Hadith) {
        viewModelScope.launch(Dispatchers.IO) {
            val isBookmarked = hadithBookmarks.value.any { it.bookName == book.nameEnglish && it.hadithNumber == hadith.number }
            if (isBookmarked) {
                repository.deleteHadithBookmark(book.nameEnglish, hadith.number)
            } else {
                repository.insertHadithBookmark(
                    HadithBookmark(
                        bookName = book.nameEnglish,
                        chapterName = hadith.chapter,
                        hadithNumber = hadith.number,
                        arabicText = hadith.textArabic,
                        englishTranslation = hadith.textEnglish,
                        urduTranslation = hadith.textUrdu
                    )
                )
            }
        }
    }

    // --- Tasbeeh Screen States ---
    private val _tasbeehCount = MutableStateFlow(0)
    val tasbeehCount: StateFlow<Int> = _tasbeehCount.asStateFlow()

    private val _selectedTasbeehPhrases = listOf("Subhan'Allah (سبحان الله)", "Alhamdulillah (الحمد لله)", "Allahu Akbar (الله أكبر)", "Astaghfirullah (أستغفر الله)")
    val tasbeehPhrases: List<String> = _selectedTasbeehPhrases

    private val _currentTasbeehPhrase = MutableStateFlow("Subhan'Allah (سبحان الله)")
    val currentTasbeehPhrase: StateFlow<String> = _currentTasbeehPhrase.asStateFlow()

    private val _tasbeehTarget = MutableStateFlow(33)
    val tasbeehTarget: StateFlow<Int> = _tasbeehTarget.asStateFlow()

    fun selectTasbeehPhrase(phrase: String) {
        _currentTasbeehPhrase.value = phrase
        _tasbeehCount.value = 0
    }

    fun adjustTasbeehTarget(target: Int) {
        _tasbeehTarget.value = target
        _tasbeehCount.value = 0
    }

    fun incrementTasbeeh() {
        if (_tasbeehCount.value < _tasbeehTarget.value) {
            _tasbeehCount.value += 1
            if (_tasbeehCount.value == _tasbeehTarget.value) {
                // Target reached, save to Room database history
                viewModelScope.launch(Dispatchers.IO) {
                    repository.insertTasbeehItem(
                        TasbeehItem(
                            name = _currentTasbeehPhrase.value,
                            currentCount = _tasbeehCount.value,
                            targetCount = _tasbeehTarget.value
                        )
                    )
                }
            }
        }
    }

    fun resetCurrentTasbeeh() {
        _tasbeehCount.value = 0
    }

    fun clearTasbeehHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllTasbeehHistory()
        }
    }

    // --- Alarms Screen States ---
    fun addIslamicAlarm(label: String, type: String, time: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertIslamicAlarm(
                IslamicAlarm(
                    label = label,
                    alarmType = type,
                    timeString = time,
                    isEnabled = true
                )
            )
        }
    }

    fun deleteIslamicAlarm(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteIslamicAlarm(id)
        }
    }

    fun prefillDefaultAlarms() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertIslamicAlarm(IslamicAlarm(label = "Tahajjud Alert", alarmType = "TAHAJJUD", timeString = "03:30 AM"))
            repository.insertIslamicAlarm(IslamicAlarm(label = "Suhoor Alarm", alarmType = "SUHOOR", timeString = "04:00 AM"))
            repository.insertIslamicAlarm(IslamicAlarm(label = "Fajr Adhan Reminder", alarmType = "FAJR", timeString = "04:45 AM"))
            repository.insertIslamicAlarm(IslamicAlarm(label = "Dhuhr Prayer Reminder", alarmType = "PRAYER", timeString = "12:30 PM"))
            repository.insertIslamicAlarm(IslamicAlarm(label = "Asr Prayer Reminder", alarmType = "PRAYER", timeString = "04:15 PM"))
            repository.insertIslamicAlarm(IslamicAlarm(label = "Iftar Alert (Maghrib)", alarmType = "IFTAR", timeString = "07:12 PM"))
            repository.insertIslamicAlarm(IslamicAlarm(label = "Isha Adhan Reminder", alarmType = "PRAYER", timeString = "08:45 PM"))
            repository.insertIslamicAlarm(IslamicAlarm(label = "Quran Reading Session", alarmType = "QURAN", timeString = "09:30 PM"))
        }
    }

    // --- AI Islamic Assistant Chat States ---
    private val _aiChatMessages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage(
            "Assalamu Alaikum! I am your AI Islamic Assistant, equipped to answer your questions and guide you with verified references from the Holy Quran and authentic Hadiths. How can I help you today?",
            isUser = false
        )
    ))
    val aiChatMessages: StateFlow<List<ChatMessage>> = _aiChatMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    fun askAiAssistant(query: String) {
        if (query.trim().isEmpty()) return
        
        // Add User Message
        val messages = _aiChatMessages.value.toMutableList()
        messages.add(ChatMessage(text = query, isUser = true))
        _aiChatMessages.value = messages
        _isAiLoading.value = true

        viewModelScope.launch {
            try {
                val response = GeminiClient.askAssistant(query)
                val updatedMessages = _aiChatMessages.value.toMutableList()
                updatedMessages.add(ChatMessage(text = response, isUser = false))
                _aiChatMessages.value = updatedMessages
            } catch (e: Exception) {
                val updatedMessages = _aiChatMessages.value.toMutableList()
                updatedMessages.add(ChatMessage(text = "Apologies, I encountered an issue: ${e.localizedMessage}. Please verify your key.", isUser = false))
                _aiChatMessages.value = updatedMessages
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    fun clearAiChat() {
        _aiChatMessages.value = listOf(
            ChatMessage(
                "Assalamu Alaikum! Let us begin our session. Feel free to ask any question regarding Tafsir, Sunnah, Islamic rules, or general ethics.",
                isUser = false
            )
        )
    }

    // --- Makkah Live Section ---
    private val _makkahLiveTime = MutableStateFlow(IslamicDateTimeUtils.getCurrentMakkahTime())
    val makkahLiveTime: StateFlow<String> = _makkahLiveTime.asStateFlow()

    private val _liveViewerCount = MutableStateFlow(42100)
    val liveViewerCount: StateFlow<Int> = _liveViewerCount.asStateFlow()

    private val _liveActiveStream = MutableStateFlow("Kaaba Live 4K Feed")
    val liveActiveStream: StateFlow<String> = _liveActiveStream.asStateFlow()

    val liveStreams = listOf("Kaaba Live 4K Feed", "Masjid al-Haram Crowd Feed", "Maqam Ibrahim Close-Up", "Mina Pilgrim Camp")

    fun selectLiveStream(streamName: String) {
        _liveActiveStream.value = streamName
    }

    init {
        // Run a clock simulation for Makkah Live Time and Viewership Flux
        viewModelScope.launch {
            while (true) {
                delay(60000)
                _makkahLiveTime.value = IslamicDateTimeUtils.getCurrentMakkahTime()
                // Slightly randomize viewership counts to look ultra real and dynamic
                _liveViewerCount.value += (-150..150).random()
            }
        }
        // Prefill default alarms if list is empty
        viewModelScope.launch {
            delay(1000)
            islamicAlarms.first().let {
                if (it.isEmpty()) {
                    prefillDefaultAlarms()
                }
            }
        }
    }

    // --- Static Islamic Data loaders for UI rendering ---
    val Names99: List<NameOfAllah> = QuranDataProvider.get99NamesOfAllah()
    val duasList: List<Dua> = QuranDataProvider.getDuas()
    val eventsList: List<IslamicEvent> = QuranDataProvider.getIslamicEvents()
    val mosquesList: List<Mosque> = QuranDataProvider.getMockMosques()

    // --- Admin panel metrics & configurations ---
    private val _adminNotificationText = MutableStateFlow("")
    val adminNotificationText: StateFlow<String> = _adminNotificationText.asStateFlow()

    fun updateAdminNotification(text: String) {
        _adminNotificationText.value = text
    }

    private val _adminSystemLogs = MutableStateFlow(listOf("System Initialized", "SQLite DB bound", "Gemini AI listener active"))
    val adminSystemLogs: StateFlow<List<String>> = _adminSystemLogs.asStateFlow()

    fun triggerGlobalAdminNotification(title: String, body: String) {
        viewModelScope.launch {
            val logs = _adminSystemLogs.value.toMutableList()
            logs.add(0, "Broadcast sent: [$title] -> $body")
            _adminSystemLogs.value = logs
            _adminNotificationText.value = ""
        }
    }
}

// Session data class
data class UserSession(
    val name: String,
    val email: String,
    val photoUrl: String,
    val authProvider: String, // "GOOGLE", "FACEBOOK", "MOBILE", "GUEST"
    val role: String, // "USER", "ADMIN"
    val isSyncEnabled: Boolean
)

// Simple Chat Message class
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
