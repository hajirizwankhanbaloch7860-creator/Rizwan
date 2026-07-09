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

    private val app = application

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

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentCount = repository.getCachedAyahsCount()
                if (currentCount < 6236) {
                    android.util.Log.d("IslamicViewModel", "Database is empty or incomplete ($currentCount/6236). Pre-populating Quran offline database...")
                    repository.clearCachedAyahs()
                    val jsonString = application.assets.open("quran_offline.json").bufferedReader().use { it.readText() }
                    val jsonObject = org.json.JSONObject(jsonString)
                    val surahsArray = jsonObject.getJSONArray("surahs")
                    val entitiesToInsert = mutableListOf<CachedAyah>()
                    
                    for (i in 0 until surahsArray.length()) {
                        val surahObj = surahsArray.getJSONObject(i)
                        val surahNum = surahObj.getInt("number")
                        val ayahsArray = surahObj.getJSONArray("ayahs")
                        
                        for (j in 0 until ayahsArray.length()) {
                            val ayahObj = ayahsArray.getJSONObject(j)
                            entitiesToInsert.add(
                                CachedAyah(
                                    surahNumber = surahNum,
                                    numberInSurah = ayahObj.getInt("numberInSurah"),
                                    textArabic = ayahObj.getString("textArabic"),
                                    textEnglish = ayahObj.getString("textEnglish"),
                                    textUrdu = ayahObj.getString("textUrdu")
                                )
                            )
                        }
                    }
                    
                    // Insert in batches of 1000 to keep it lightweight and transaction-friendly
                    entitiesToInsert.chunked(1000).forEach { batch ->
                        repository.insertCachedAyahs(batch)
                    }
                    android.util.Log.d("IslamicViewModel", "Successfully pre-populated ${entitiesToInsert.size} Quran offline verses.")
                } else {
                    android.util.Log.d("IslamicViewModel", "Quran offline database already fully populated with $currentCount verses.")
                }
            } catch (e: Exception) {
                android.util.Log.e("IslamicViewModel", "Error pre-populating Quran offline database", e)
            }
        }
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
    val juzList: List<Juz> = QuranDataProvider.getJuzList()

    private val _selectedSurah = MutableStateFlow<Surah?>(null)
    val selectedSurah: StateFlow<Surah?> = _selectedSurah.asStateFlow()

    private val _selectedJuz = MutableStateFlow<Juz?>(null)
    val selectedJuz: StateFlow<Juz?> = _selectedJuz.asStateFlow()

    private val _ayahList = MutableStateFlow<List<Ayah>>(emptyList())
    val ayahList: StateFlow<List<Ayah>> = _ayahList.asStateFlow()

    private val _isQuranLoading = MutableStateFlow(false)
    val isQuranLoading: StateFlow<Boolean> = _isQuranLoading.asStateFlow()

    private val _currentPlayingAyah = MutableStateFlow<Int?>(null)
    val currentPlayingAyah: StateFlow<Int?> = _currentPlayingAyah.asStateFlow()

    private val _activeReciter = MutableStateFlow("Sheikh Mishary Alafasy")
    val activeReciter: StateFlow<String> = _activeReciter.asStateFlow()

    private val _translationRecitationOption = MutableStateFlow("None") // "None", "English Translation", "Urdu Translation"
    val translationRecitationOption: StateFlow<String> = _translationRecitationOption.asStateFlow()

    fun setTranslationRecitationOption(option: String) {
        _translationRecitationOption.value = option
    }

    private val _currentPlayingHadithNumber = MutableStateFlow<String?>(null)
    val currentPlayingHadithNumber: StateFlow<String?> = _currentPlayingHadithNumber.asStateFlow()

    private val _hadithTtsOption = MutableStateFlow("Arabic + Urdu") // "Arabic Only", "Arabic + English", "Arabic + Urdu", "English Only", "Urdu Only"
    val hadithTtsOption: StateFlow<String> = _hadithTtsOption.asStateFlow()

    fun setHadithTtsOption(option: String) {
        _hadithTtsOption.value = option
    }

    private var textToSpeech: android.speech.tts.TextToSpeech? = null
    private var ttsOnDoneCallback: (() -> Unit)? = null

    private val _isAudioPlaying = MutableStateFlow(false)
    val isAudioPlaying: StateFlow<Boolean> = _isAudioPlaying.asStateFlow()

    val reciters = listOf("Sheikh Mishary Alafasy", "Sheikh Abdul Rahman Al-Sudais", "Sheikh Saad Al-Ghamdi")

    private var mediaPlayer: android.media.MediaPlayer? = null
    private var lastPlayedSurah: Int? = null
    private var lastPlayedAyah: Int? = null
    private var isPlayingBismillahPrepend = false
    private var bismillahPlayedForSurah: Int? = null

    private val httpClient = okhttp3.OkHttpClient.Builder()
        .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    fun selectSurah(surah: Surah) {
        stopAudio()
        isPlayingBismillahPrepend = false
        bismillahPlayedForSurah = null
        _selectedSurah.value = surah
        _currentPlayingAyah.value = null
        _isAudioPlaying.value = false

        val localAyahs = QuranDataProvider.getAyahsForSurah(surah.number)
        _ayahList.value = localAyahs
        loadSurahDynamically(surah.number)
    }

    private fun loadSurahDynamically(surahNumber: Int) {
        _isQuranLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            // Step 1: Try to load from local Room Database Cache first!
            try {
                val cached = repository.getCachedAyahsForSurah(surahNumber)
                if (cached.isNotEmpty()) {
                    val mapped = cached.map { c ->
                        val wordsList = mutableListOf<QuranWord>()
                        val arabicWords = c.textArabic.split(" ")
                        for (w in arabicWords) {
                            if (w.trim().isNotEmpty()) {
                                wordsList.add(QuranWord(w, "Word", "لفظ", "Word"))
                            }
                        }
                        Ayah(
                            numberInSurah = c.numberInSurah,
                            textArabic = c.textArabic,
                            textEnglish = c.textEnglish,
                            textUrdu = c.textUrdu,
                            words = wordsList
                        )
                    }
                    _ayahList.value = mapped
                    _isQuranLoading.value = false
                    return@launch
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Step 2: If not in cache, fetch from the Al-Quran Cloud API!
            val url = "https://api.alquran.cloud/v1/surah/$surahNumber/editions/quran-uthmani,en.sahih,ur.jalandhry"
            val request = okhttp3.Request.Builder().url(url).build()
            try {
                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body?.string()
                        if (body != null) {
                            val parsedAyahs = parseAlQuranApiResponse(body)
                            if (parsedAyahs.isNotEmpty()) {
                                // Save to local Room cache!
                                val cachedEntities = parsedAyahs.map { a ->
                                    CachedAyah(
                                        surahNumber = surahNumber,
                                        numberInSurah = a.numberInSurah,
                                        textArabic = a.textArabic,
                                        textEnglish = a.textEnglish,
                                        textUrdu = a.textUrdu
                                    )
                                }
                                repository.insertCachedAyahs(cachedEntities)
                                
                                _ayahList.value = parsedAyahs
                                _isQuranLoading.value = false
                                return@launch
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Fallback 1: Try to fetch using Gemini API!
            try {
                val geminiAyahs = fetchAyahsFromGemini(surahNumber)
                if (geminiAyahs.isNotEmpty()) {
                    // Save to local Room cache!
                    val cachedEntities = geminiAyahs.map { a ->
                        CachedAyah(
                            surahNumber = surahNumber,
                            numberInSurah = a.numberInSurah,
                            textArabic = a.textArabic,
                            textEnglish = a.textEnglish,
                            textUrdu = a.textUrdu
                        )
                    }
                    repository.insertCachedAyahs(cachedEntities)

                    _ayahList.value = geminiAyahs
                    _isQuranLoading.value = false
                    return@launch
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Fallback 2: Keep the local placeholder but dynamically adjust its Ayah count and numbers to match the Surah!
            val totalAyahsCount = surahList.firstOrNull { it.number == surahNumber }?.ayahsCount ?: 7
            val fallbackList = mutableListOf<Ayah>()
            for (i in 1..totalAyahsCount) {
                fallbackList.add(
                    Ayah(
                        i,
                        "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ (Ayah $i placeholder)",
                        "Praise be to Allah, Lord of the worlds. (Ayah $i Translation)",
                        "سب تعریفیں اللہ کے لیے ہیں جو سب جہانوں کا پالنے والا ہے۔ (آیت $i کا ترجمہ)",
                        listOf(
                            QuranWord("الْحَمْدُ", "All praise", "سب تعریفیں", "Al-Hamdu"),
                            QuranWord("لِلَّهِ", "to Allah", "اللہ کے لیے", "Lillahi")
                        )
                    )
                )
            }
            _ayahList.value = fallbackList
            _isQuranLoading.value = false
        }
    }

    private fun getAyahsFromAsset(surahNumber: Int): List<Ayah> {
        try {
            val jsonString = app.assets.open("quran_offline.json").bufferedReader().use { it.readText() }
            val jsonObject = org.json.JSONObject(jsonString)
            val surahsArray = jsonObject.getJSONArray("surahs")
            for (i in 0 until surahsArray.length()) {
                val surahObj = surahsArray.getJSONObject(i)
                val surahNum = surahObj.getInt("number")
                if (surahNum == surahNumber) {
                    val ayahsArray = surahObj.getJSONArray("ayahs")
                    val result = mutableListOf<Ayah>()
                    for (j in 0 until ayahsArray.length()) {
                        val ayahObj = ayahsArray.getJSONObject(j)
                        val textArabic = ayahObj.getString("textArabic")
                        val wordsList = mutableListOf<QuranWord>()
                        val arabicWords = textArabic.split(" ")
                        for (w in arabicWords) {
                            if (w.trim().isNotEmpty()) {
                                wordsList.add(QuranWord(w, "Word", "لفظ", "Word"))
                            }
                        }
                        result.add(
                            Ayah(
                                numberInSurah = ayahObj.getInt("numberInSurah"),
                                textArabic = textArabic,
                                textEnglish = ayahObj.getString("textEnglish"),
                                textUrdu = ayahObj.getString("textUrdu"),
                                words = wordsList
                            )
                        )
                    }
                    return result
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("IslamicViewModel", "Error reading fallback from asset", e)
        }
        return emptyList()
    }

    suspend fun getAyahsForSurahWithCache(surahNumber: Int): List<Ayah> {
        // 1. Try local Room cache
        try {
            val cached = repository.getCachedAyahsForSurah(surahNumber)
            if (cached.isNotEmpty()) {
                return cached.map { c ->
                    val wordsList = mutableListOf<QuranWord>()
                    val arabicWords = c.textArabic.split(" ")
                    for (w in arabicWords) {
                        if (w.trim().isNotEmpty()) {
                            wordsList.add(QuranWord(w, "Word", "لفظ", "Word"))
                        }
                    }
                    Ayah(
                        numberInSurah = c.numberInSurah,
                        textArabic = c.textArabic,
                        textEnglish = c.textEnglish,
                        textUrdu = c.textUrdu,
                        words = wordsList
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // 2. Try the embedded offline JSON asset directly as a bulletproof instant fallback!
        val assetAyahs = getAyahsFromAsset(surahNumber)
        if (assetAyahs.isNotEmpty()) {
            return assetAyahs
        }

        // 3. Try hardcoded provider (like for 1, 93, 94, 103, 108, 112, 113, 114)
        val providerAyahs = QuranDataProvider.getAyahsForSurah(surahNumber)
        if (surahNumber in listOf(1, 93, 94, 103, 108, 112, 113, 114)) {
            return providerAyahs
        }
        
        // 3. Otherwise try fetching online to cache it!
        val url = "https://api.alquran.cloud/v1/surah/$surahNumber/editions/quran-uthmani,en.sahih,ur.jalandhry"
        val request = okhttp3.Request.Builder().url(url).build()
        try {
            httpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (body != null) {
                        val parsedAyahs = parseAlQuranApiResponse(body)
                        if (parsedAyahs.isNotEmpty()) {
                            // Save to local Room cache!
                            val cachedEntities = parsedAyahs.map { a ->
                                CachedAyah(
                                    surahNumber = surahNumber,
                                    numberInSurah = a.numberInSurah,
                                    textArabic = a.textArabic,
                                    textEnglish = a.textEnglish,
                                    textUrdu = a.textUrdu
                                )
                            }
                            repository.insertCachedAyahs(cachedEntities)
                            return parsedAyahs
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Fallback: Gemini
        try {
            val geminiAyahs = fetchAyahsFromGemini(surahNumber)
            if (geminiAyahs.isNotEmpty()) {
                val cachedEntities = geminiAyahs.map { a ->
                    CachedAyah(
                        surahNumber = surahNumber,
                        numberInSurah = a.numberInSurah,
                        textArabic = a.textArabic,
                        textEnglish = a.textEnglish,
                        textUrdu = a.textUrdu
                    )
                }
                repository.insertCachedAyahs(cachedEntities)
                return geminiAyahs
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return providerAyahs
    }

    private fun parseAlQuranApiResponse(jsonStr: String): List<Ayah> {
        val list = mutableListOf<Ayah>()
        try {
            val root = org.json.JSONObject(jsonStr)
            val dataArray = root.getJSONArray("data")
            if (dataArray.length() >= 3) {
                val uthmaniEdition = dataArray.getJSONObject(0)
                val englishEdition = dataArray.getJSONObject(1)
                val urduEdition = dataArray.getJSONObject(2)

                val uthmaniAyahs = uthmaniEdition.getJSONArray("ayahs")
                val englishAyahs = englishEdition.getJSONArray("ayahs")
                val urduAyahs = urduEdition.getJSONArray("ayahs")

                val length = uthmaniAyahs.length()
                for (i in 0 until length) {
                    val uthmaniObj = uthmaniAyahs.getJSONObject(i)
                    val englishObj = englishAyahs.getJSONObject(i)
                    val urduObj = urduAyahs.getJSONObject(i)

                    val num = uthmaniObj.getInt("numberInSurah")
                    val arabicText = uthmaniObj.getString("text")
                    val englishText = englishObj.getString("text")
                    val urduText = urduObj.getString("text")

                    val wordsList = mutableListOf<QuranWord>()
                    val arabicWords = arabicText.split(" ")
                    for (w in arabicWords) {
                        if (w.trim().isNotEmpty()) {
                            wordsList.add(QuranWord(w, "Word", "لفظ", "Word"))
                        }
                    }

                    list.add(
                        Ayah(
                            numberInSurah = num,
                            textArabic = arabicText,
                            textEnglish = englishText,
                            textUrdu = urduText,
                            words = wordsList
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private suspend fun fetchAyahsFromGemini(surahNumber: Int): List<Ayah> {
        val surah = surahList.firstOrNull { it.number == surahNumber } ?: return emptyList()
        val prompt = """
            Provide the first 5 verses of Surah ${surah.nameEnglish} (Surah number $surahNumber) in JSON format.
            Format response as a JSON array of objects. Each object must have:
            - "numberInSurah": integer
            - "textArabic": string (clean Arabic text)
            - "textEnglish": string (English translation)
            - "textUrdu": string (Urdu translation)
            
            Do not include any explanation or markdown formatting, just return raw valid JSON array.
        """.trimIndent()
        
        val response = GeminiClient.askAssistant(prompt)
        val cleanJson = response.replace("```json", "").replace("```", "").trim()
        val list = mutableListOf<Ayah>()
        try {
            val jsonArray = org.json.JSONArray(cleanJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val num = obj.getInt("numberInSurah")
                val arabic = obj.getString("textArabic")
                val english = obj.getString("textEnglish")
                val urdu = obj.getString("textUrdu")
                
                val wordsList = mutableListOf<QuranWord>()
                val arabicWords = arabic.split(" ")
                for (w in arabicWords) {
                    if (w.trim().isNotEmpty()) {
                        wordsList.add(QuranWord(w, "Word", "لفظ", "Word"))
                    }
                }
                
                list.add(Ayah(num, arabic, english, urdu, wordsList))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun clearSelectedSurah() {
        stopAudio()
        _selectedSurah.value = null
        _currentPlayingAyah.value = null
        _isAudioPlaying.value = false
    }

    fun selectJuz(juz: Juz) {
        _selectedJuz.value = juz
        val surah = surahList.firstOrNull { it.number == juz.startSurahNumber }
        if (surah != null) {
            selectSurah(surah)
            _currentPlayingAyah.value = juz.startAyahNumber
        }
    }

    fun clearSelectedJuz() {
        _selectedJuz.value = null
        clearSelectedSurah()
    }

    fun selectReciter(reciter: String) {
        _activeReciter.value = reciter
        if (_isAudioPlaying.value) {
            val surah = _selectedSurah.value
            val current = _currentPlayingAyah.value ?: 1
            if (surah != null) {
                playAyahAudio(surah.number, current)
            }
        }
    }

    fun toggleAudioPlay() {
        if (_ayahList.value.isNotEmpty()) {
            val newState = !_isAudioPlaying.value
            _isAudioPlaying.value = newState
            if (newState) {
                val surah = _selectedSurah.value
                val current = _currentPlayingAyah.value ?: 1
                if (_currentPlayingAyah.value == null) {
                    _currentPlayingAyah.value = 1
                }
                if (surah != null) {
                    playAyahAudio(surah.number, current)
                }
            } else {
                pauseAudio()
            }
        }
    }

    fun playNextAyah() {
        val current = _currentPlayingAyah.value ?: return
        val next = if (current < _ayahList.value.size) current + 1 else 1
        _currentPlayingAyah.value = next
        val surah = _selectedSurah.value
        if (surah != null && _isAudioPlaying.value) {
            playAyahAudio(surah.number, next)
        }
    }

    fun playPreviousAyah() {
        val current = _currentPlayingAyah.value ?: return
        val prev = if (current > 1) current - 1 else _ayahList.value.size
        _currentPlayingAyah.value = prev
        val surah = _selectedSurah.value
        if (surah != null && _isAudioPlaying.value) {
            playAyahAudio(surah.number, prev)
        }
    }

    private fun playAyahAudio(surahNumber: Int, ayahNumber: Int) {
        if (lastPlayedSurah == surahNumber && lastPlayedAyah == ayahNumber && mediaPlayer != null) {
            try {
                mediaPlayer?.let {
                    if (!it.isPlaying) {
                        it.start()
                    }
                    return
                }
            } catch (e: Exception) {
                e.printStackTrace()
                stopAudio()
            }
        }

        stopAudio()
        
        val folder = when (_activeReciter.value) {
            "Sheikh Mishary Alafasy" -> "Alafasy_128kbps"
            "Sheikh Abdul Rahman Al-Sudais" -> "Abdurrahmaan_As-Sudais_192kbps"
            "Sheikh Saad Al-Ghamdi" -> "Ghamadi_40kbps"
            else -> "Alafasy_128kbps"
        }

        val isPrepend = ayahNumber == 1 && surahNumber != 1 && surahNumber != 9 && bismillahPlayedForSurah != surahNumber && !isPlayingBismillahPrepend

        val surahStr = if (isPrepend) "001" else String.format("%03d", surahNumber)
        val ayahStr = if (isPrepend) "001" else String.format("%03d", ayahNumber)
        val audioUrl = "https://everyayah.com/data/$folder/$surahStr$ayahStr.mp3"

        if (isPrepend) {
            isPlayingBismillahPrepend = true
        }

        try {
            val player = android.media.MediaPlayer().apply {
                setAudioAttributes(
                    android.media.AudioAttributes.Builder()
                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(audioUrl)
                setOnPreparedListener { mp ->
                    if (_isAudioPlaying.value) {
                        mp.start()
                        if (!isPrepend) {
                            lastPlayedSurah = surahNumber
                            lastPlayedAyah = ayahNumber
                        }
                    }
                }
                setOnCompletionListener {
                    viewModelScope.launch {
                        if (isPlayingBismillahPrepend) {
                            kotlinx.coroutines.delay(600)
                            isPlayingBismillahPrepend = false
                            bismillahPlayedForSurah = surahNumber
                            playAyahAudio(surahNumber, 1)
                        } else {
                            kotlinx.coroutines.delay(3000)
                            val currentOption = _translationRecitationOption.value
                            if (currentOption != "None") {
                                val currentAyah = _ayahList.value.firstOrNull { it.numberInSurah == ayahNumber }
                                if (currentAyah != null) {
                                    val textToSpeak = if (currentOption == "Urdu Translation") {
                                        currentAyah.textUrdu
                                    } else {
                                        currentAyah.textEnglish
                                    }
                                    speakTranslation(textToSpeak) {
                                        playNextAyahAuto()
                                    }
                                } else {
                                    playNextAyahAuto()
                                }
                            } else {
                                playNextAyahAuto()
                            }
                        }
                    }
                }
                setOnErrorListener { _, _, _ ->
                    if (folder != "Alafasy_128kbps") {
                        _activeReciter.value = "Sheikh Mishary Alafasy"
                        playAyahAudio(surahNumber, ayahNumber)
                    }
                    true
                }
                prepareAsync()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun speakTranslation(text: String, onDone: () -> Unit) {
        val tts = textToSpeech
        if (tts == null) {
            onDone()
            return
        }

        ttsOnDoneCallback = onDone

        val currentOption = _translationRecitationOption.value
        val locale = if (currentOption == "Urdu Translation") {
            java.util.Locale("ur", "PK")
        } else {
            java.util.Locale.US
        }

        try {
            tts.language = locale
            val params = android.os.Bundle().apply {
                putString(android.speech.tts.TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "translation_tts")
            }
            tts.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}

                override fun onDone(utteranceId: String?) {
                    if (utteranceId == "translation_tts") {
                        ttsOnDoneCallback?.let { callback ->
                            viewModelScope.launch {
                                callback.invoke()
                            }
                        }
                        ttsOnDoneCallback = null
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    ttsOnDoneCallback?.let { callback ->
                        viewModelScope.launch {
                            callback.invoke()
                        }
                    }
                    ttsOnDoneCallback = null
                }
            })
            tts.speak(text, android.speech.tts.TextToSpeech.QUEUE_FLUSH, params, "translation_tts")
        } catch (e: Exception) {
            e.printStackTrace()
            onDone()
        }
    }

    private fun playNextAyahAuto() {
        val current = _currentPlayingAyah.value ?: return
        if (current < _ayahList.value.size) {
            val next = current + 1
            _currentPlayingAyah.value = next
            val surah = _selectedSurah.value
            if (surah != null && _isAudioPlaying.value) {
                playAyahAudio(surah.number, next)
            }
        } else {
            _isAudioPlaying.value = false
            _currentPlayingAyah.value = null
            stopAudio()
            isPlayingBismillahPrepend = false
            bismillahPlayedForSurah = null
        }
    }

    private fun pauseAudio() {
        try {
            textToSpeech?.stop()
            ttsOnDoneCallback = null
            mediaPlayer?.let {
                it.setOnCompletionListener(null)
                it.setOnErrorListener(null)
                if (it.isPlaying) {
                    it.pause()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopAudio() {
        try {
            textToSpeech?.stop()
            ttsOnDoneCallback = null
            _currentPlayingHadithNumber.value = null
            mediaPlayer?.let {
                it.setOnCompletionListener(null)
                it.setOnErrorListener(null)
                it.release()
            }
            mediaPlayer = null
            lastPlayedSurah = null
            lastPlayedAyah = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playHadith(hadith: Hadith) {
        if (_currentPlayingHadithNumber.value == hadith.number) {
            stopHadithAudio()
            return
        }

        stopAudio() // Stop Quran audio if playing
        stopHadithAudio() // Reset TTS

        _currentPlayingHadithNumber.value = hadith.number

        val tts = textToSpeech
        if (tts == null) {
            _currentPlayingHadithNumber.value = null
            return
        }

        val option = _hadithTtsOption.value

        viewModelScope.launch {
            try {
                if (option == "Arabic Only" || option == "Arabic + English" || option == "Arabic + Urdu") {
                    // Speak Arabic first
                    speakHadithText(hadith.textArabic, java.util.Locale("ar")) {
                        // After Arabic is done, speak translation if needed
                        if (option == "Arabic + English") {
                            speakHadithText(hadith.textEnglish, java.util.Locale.US) {
                                _currentPlayingHadithNumber.value = null
                            }
                        } else if (option == "Arabic + Urdu") {
                            speakHadithText(hadith.textUrdu, java.util.Locale("ur", "PK")) {
                                _currentPlayingHadithNumber.value = null
                            }
                        } else {
                            _currentPlayingHadithNumber.value = null
                        }
                    }
                } else if (option == "English Only") {
                    speakHadithText(hadith.textEnglish, java.util.Locale.US) {
                        _currentPlayingHadithNumber.value = null
                    }
                } else if (option == "Urdu Only") {
                    speakHadithText(hadith.textUrdu, java.util.Locale("ur", "PK")) {
                        _currentPlayingHadithNumber.value = null
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _currentPlayingHadithNumber.value = null
            }
        }
    }

    private fun speakHadithText(text: String, locale: java.util.Locale, onDone: () -> Unit) {
        val tts = textToSpeech ?: return
        ttsOnDoneCallback = onDone
        try {
            tts.language = locale
            val params = android.os.Bundle().apply {
                putString(android.speech.tts.TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "hadith_tts")
            }
            tts.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    if (utteranceId == "hadith_tts") {
                        ttsOnDoneCallback?.let { callback ->
                            viewModelScope.launch {
                                callback.invoke()
                            }
                        }
                        ttsOnDoneCallback = null
                    }
                }
                override fun onError(utteranceId: String?) {
                    ttsOnDoneCallback?.let { callback ->
                        viewModelScope.launch {
                            callback.invoke()
                        }
                    }
                    ttsOnDoneCallback = null
                }
            })
            tts.speak(text, android.speech.tts.TextToSpeech.QUEUE_FLUSH, params, "hadith_tts")
        } catch (e: Exception) {
            e.printStackTrace()
            onDone()
        }
    }

    fun stopHadithAudio() {
        try {
            textToSpeech?.stop()
            ttsOnDoneCallback = null
            _currentPlayingHadithNumber.value = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // General purpose Text-To-Speech for educational screens
    private val _isTtsSpeaking = MutableStateFlow(false)
    val isTtsSpeaking = _isTtsSpeaking.asStateFlow()

    fun speakText(text: String, languageCode: String = "en", pitch: Float = 1.0f, speechRate: Float = 1.0f) {
        val tts = textToSpeech ?: return
        try {
            _isTtsSpeaking.value = true
            tts.setPitch(pitch)
            tts.setSpeechRate(speechRate)
            tts.language = java.util.Locale(languageCode)
            val params = android.os.Bundle().apply {
                putString(android.speech.tts.TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "general_tts")
            }
            tts.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isTtsSpeaking.value = true
                }
                override fun onDone(utteranceId: String?) {
                    _isTtsSpeaking.value = false
                    // Reset pitch and speech rate to default
                    tts.setPitch(1.0f)
                    tts.setSpeechRate(1.0f)
                }
                override fun onError(utteranceId: String?) {
                    _isTtsSpeaking.value = false
                    // Reset pitch and speech rate to default
                    tts.setPitch(1.0f)
                    tts.setSpeechRate(1.0f)
                }
            })
            tts.speak(text, android.speech.tts.TextToSpeech.QUEUE_FLUSH, params, "general_tts")
        } catch (e: Exception) {
            e.printStackTrace()
            _isTtsSpeaking.value = false
        }
    }

    fun stopSpeaking() {
        try {
            textToSpeech?.stop()
            _isTtsSpeaking.value = false
        } catch (e: Exception) {
            e.printStackTrace()
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
        try {
            textToSpeech = android.speech.tts.TextToSpeech(application) { status ->
                if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                    // TTS initialized
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

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

    // --- Donation & Sadaqah SharedPreferences Persistent States ---
    private val prefs = application.getSharedPreferences("islamic_donation_prefs", android.content.Context.MODE_PRIVATE)

    private val _donationAccountName = MutableStateFlow(prefs.getString("acc_name", "Haji Rizwan Khan Baloch") ?: "Haji Rizwan Khan Baloch")
    val donationAccountName: StateFlow<String> = _donationAccountName.asStateFlow()

    private val _donationPhoneNumber = MutableStateFlow(prefs.getString("phone_num", "+923260148196") ?: "+923260148196")
    val donationPhoneNumber: StateFlow<String> = _donationPhoneNumber.asStateFlow()

    private val _donationBankName = MutableStateFlow(prefs.getString("bank_name", "JazzCash") ?: "JazzCash")
    val donationBankName: StateFlow<String> = _donationBankName.asStateFlow()

    private val _donationAccountNumber = MutableStateFlow(prefs.getString("acc_num", "+923260148196") ?: "+923260148196")
    val donationAccountNumber: StateFlow<String> = _donationAccountNumber.asStateFlow()

    private val _donationLink = MutableStateFlow(prefs.getString("donate_link", "") ?: "")
    val donationLink: StateFlow<String> = _donationLink.asStateFlow()

    fun updateDonationDetails(name: String, phone: String, bank: String, accNum: String, link: String) {
        prefs.edit().apply {
            putString("acc_name", name)
            putString("phone_num", phone)
            putString("bank_name", bank)
            putString("acc_num", accNum)
            putString("donate_link", link)
            apply()
        }
        _donationAccountName.value = name
        _donationPhoneNumber.value = phone
        _donationBankName.value = bank
        _donationAccountNumber.value = accNum
        _donationLink.value = link
    }

    // --- Daily Quran & Hadith Reminder Preference States ---
    private val notifPrefs = application.getSharedPreferences("islamic_notification_settings", android.content.Context.MODE_PRIVATE)

    private val _isReminderEnabled = MutableStateFlow(notifPrefs.getBoolean("enabled", true))
    val isReminderEnabled: StateFlow<Boolean> = _isReminderEnabled.asStateFlow()

    private val _reminderHour = MutableStateFlow(notifPrefs.getInt("hour", 8))
    val reminderHour: StateFlow<Int> = _reminderHour.asStateFlow()

    private val _reminderMinute = MutableStateFlow(notifPrefs.getInt("minute", 0))
    val reminderMinute: StateFlow<Int> = _reminderMinute.asStateFlow()

    fun setReminderEnabled(enabled: Boolean) {
        _isReminderEnabled.value = enabled
        if (enabled) {
            com.example.utils.IslamicNotificationHelper.scheduleDailyReminder(
                getApplication(),
                _reminderHour.value,
                _reminderMinute.value
            )
        } else {
            com.example.utils.IslamicNotificationHelper.cancelDailyReminder(getApplication())
        }
    }

    fun updateReminderTime(hour: Int, minute: Int) {
        _reminderHour.value = hour
        _reminderMinute.value = minute
        if (_isReminderEnabled.value) {
            com.example.utils.IslamicNotificationHelper.scheduleDailyReminder(
                getApplication(),
                hour,
                minute
            )
        }
    }

    fun triggerTestNotification() {
        val randomIndex = kotlin.random.Random.nextInt(com.example.utils.IslamicNotificationHelper.reminders.size)
        val reminder = com.example.utils.IslamicNotificationHelper.reminders[randomIndex]
        com.example.utils.IslamicNotificationHelper.showNotification(getApplication(), reminder)
    }

    override fun onCleared() {
        super.onCleared()
        try {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            textToSpeech = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        stopAudio()
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
