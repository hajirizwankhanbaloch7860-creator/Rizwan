package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Ayah
import com.example.data.model.QuranDataProvider
import com.example.data.model.Surah
import com.example.ui.IslamicViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun convertToArabicNumerals(number: Int): String {
    val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    val numStr = number.toString()
    val builder = StringBuilder()
    for (ch in numStr) {
        if (ch in '0'..'9') {
            builder.append(arabicDigits[ch - '0'])
        } else {
            builder.append(ch)
        }
    }
    return builder.toString()
}

fun convertToUrduNumerals(number: Int): String {
    val urduDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    val numStr = number.toString()
    val builder = StringBuilder()
    for (ch in numStr) {
        if (ch in '0'..'9') {
            builder.append(urduDigits[ch - '0'])
        } else {
            builder.append(ch)
        }
    }
    return builder.toString()
}

fun cleanFirstAyahText(text: String, surahNumber: Int): String {
    if (surahNumber == 1 || surahNumber == 9) return text
    
    val trimmed = text.trim()
    val bismillahNormalized = "بسم الله الرحمن الرحيم"
    
    fun isDiacritic(c: Char): Boolean {
        return c in '\u064B'..'\u065F' || c == '\u0670'
    }
    
    fun normalizeChar(c: Char): Char {
        return when (c) {
            'ٱ', 'أ', 'إ', 'آ' -> 'ا'
            else -> c
        }
    }
    
    val originalIndices = mutableListOf<Int>()
    val normalizedBuilder = StringBuilder()
    
    for (i in trimmed.indices) {
        val c = trimmed[i]
        if (!isDiacritic(c)) {
            normalizedBuilder.append(normalizeChar(c))
            originalIndices.add(i)
        }
    }
    
    val normalizedText = normalizedBuilder.toString()
    if (normalizedText.startsWith(bismillahNormalized)) {
        val normSkipCount = bismillahNormalized.length
        if (normSkipCount < originalIndices.size) {
            val origSkipIndex = originalIndices[normSkipCount]
            return trimmed.substring(origSkipIndex).trim()
        } else {
            return ""
        }
    }
    
    return trimmed
}

fun getJuzNumberForSurah(surahNum: Int): Int {
    return when (surahNum) {
        1 -> 1
        2 -> 1
        3 -> 3
        4 -> 4
        5 -> 6
        6 -> 7
        7 -> 8
        8 -> 10
        9 -> 10
        10 -> 11
        11 -> 11
        12 -> 12
        13 -> 13
        14 -> 13
        15 -> 14
        16 -> 14
        17 -> 15
        18 -> 15
        19 -> 16
        20 -> 16
        21 -> 17
        22 -> 17
        23 -> 18
        24 -> 18
        25 -> 18
        26 -> 19
        27 -> 19
        28 -> 20
        29 -> 20
        30 -> 21
        31 -> 21
        32 -> 21
        33 -> 21
        34 -> 22
        35 -> 22
        36 -> 22
        37 -> 23
        38 -> 23
        39 -> 23
        40 -> 24
        41 -> 24
        42 -> 25
        43 -> 25
        44 -> 25
        45 -> 25
        46 -> 26
        47 -> 26
        48 -> 26
        49 -> 26
        50 -> 26
        51 -> 26
        52 -> 27
        53 -> 27
        54 -> 27
        55 -> 27
        56 -> 27
        57 -> 27
        58 -> 28
        59 -> 28
        60 -> 28
        61 -> 28
        62 -> 28
        63 -> 28
        64 -> 28
        65 -> 28
        66 -> 28
        67 -> 29
        68 -> 29
        69 -> 29
        70 -> 29
        71 -> 29
        72 -> 29
        73 -> 29
        74 -> 29
        75 -> 29
        76 -> 29
        77 -> 29
        in 78..114 -> 30
        else -> 30
    }
}

fun getJuzArabicName(juzNum: Int): String {
    return when (juzNum) {
        1 -> "الجُزْءُ الأَوَّلُ"
        2 -> "الجُزْءُ الثَّانِي"
        3 -> "الجُزْءُ الثَّالِثُ"
        4 -> "الجُزْءُ الرَّابِعُ"
        5 -> "الجُزْءُ الْخَامِسُ"
        6 -> "الجُزْءُ السَّادِسُ"
        7 -> "الجُزْءُ السَّابِعُ"
        8 -> "الجُزْءُ الثَّامِنُ"
        9 -> "الجُزْءُ التَّاسِعُ"
        10 -> "الجُزْءُ الْعَاشِرُ"
        11 -> "الجُزْءُ الْحَادِي عَشَرَ"
        12 -> "الجُزْءُ الثَّانِي عَشَرَ"
        13 -> "الجُزْءُ الثَّالِثَ عَشَرَ"
        14 -> "الجُزْءُ الرَّابِعَ عَشَرَ"
        15 -> "الجُزْءُ الْخَامِسَ عَشَرَ"
        16 -> "الجُزْءُ السَّادِسَ عَشَرَ"
        17 -> "الجُزْءُ السَّابِعَ عَشَرَ"
        18 -> "الجُزْءُ الثَّامِنَ عَشَرَ"
        19 -> "الجُزْءُ التَّاسِعَ عَشَرَ"
        20 -> "الجُزْءُ الْعِشْرُونَ"
        21 -> "الجُزْءُ الْحَادِي وَالْعِشْرُونَ"
        22 -> "الجُزْءُ الثَّانِي وَالْعِشْرُونَ"
        23 -> "الجُزْءُ الثَّالِثُ وَالْعِشْرُونَ"
        24 -> "الجُزْءُ الرَّابِعُ وَالْعِشْرُونَ"
        25 -> "الجُزْءُ الْخَامِسُ وَالْعِشْرُونَ"
        26 -> "الجُزْءُ السَّادِسُ وَالْعِشْرُونَ"
        27 -> "الجُزْءُ السَّابِعُ وَالْعِشْرُونَ"
        28 -> "الجُزْءُ الثَّامِنُ وَالْعِشْرُونَ"
        29 -> "الجُزْءُ التَّاسِعُ وَالْعِشْرُونَ"
        30 -> "الجُزْءُ الثَّلَاثُونَ"
        else -> "الجُزْءُ $juzNum"
    }
}

fun getQuranPageNumber(surahNum: Int): Int {
    val landmarks = listOf(
        Pair(1, 1),
        Pair(2, 2),
        Pair(3, 50),
        Pair(4, 77),
        Pair(5, 106),
        Pair(6, 128),
        Pair(7, 151),
        Pair(8, 177),
        Pair(9, 187),
        Pair(10, 208),
        Pair(12, 235),
        Pair(15, 262),
        Pair(17, 282),
        Pair(18, 293),
        Pair(20, 312),
        Pair(25, 359),
        Pair(30, 404),
        Pair(36, 440),
        Pair(40, 467),
        Pair(45, 499),
        Pair(50, 518),
        Pair(55, 531),
        Pair(56, 534),
        Pair(60, 549),
        Pair(67, 562),
        Pair(70, 568),
        Pair(78, 582),
        Pair(85, 590),
        Pair(90, 594),
        Pair(93, 596),
        Pair(100, 599),
        Pair(105, 601),
        Pair(110, 603),
        Pair(112, 604),
        Pair(113, 604),
        Pair(114, 604)
    )
    for (i in 0 until landmarks.size - 1) {
        val (s1, p1) = landmarks[i]
        val (s2, p2) = landmarks[i+1]
        if (surahNum in s1..s2) {
            val rangeS = s2 - s1
            if (rangeS == 0) return p1
            val fraction = (surahNum - s1).toFloat() / rangeS
            return p1 + (fraction * (p2 - p1)).toInt()
        }
    }
    return 604
}

fun Modifier.mushafBorderFrame(
    borderColor: Color = Color(0xFF1B4D3E),
    goldColor: Color = Color(0xFFD4AF37),
    accentColor: Color = Color(0xFFE05275)
) = this.drawBehind {
    val width = size.width
    val height = size.height
    
    drawRect(color = Color(0xFFFDFBF7))
    
    val borderWidthPx = 18.dp.toPx()
    val outerGoldOffset = 4.dp.toPx()
    val innerGoldOffset = borderWidthPx + outerGoldOffset
    
    drawRect(
        color = goldColor,
        topLeft = Offset(outerGoldOffset, outerGoldOffset),
        size = androidx.compose.ui.geometry.Size(width - 2 * outerGoldOffset, height - 2 * outerGoldOffset),
        style = Stroke(width = 1.dp.toPx())
    )
    
    val greenBandOffset = outerGoldOffset + 1.dp.toPx()
    val greenBandWidth = borderWidthPx - 2.dp.toPx()
    drawRect(
        color = borderColor,
        topLeft = Offset(greenBandOffset, greenBandOffset),
        size = androidx.compose.ui.geometry.Size(width - 2 * greenBandOffset, height - 2 * greenBandOffset),
        style = Stroke(width = greenBandWidth)
    )
    
    val dotSpacing = 22.dp.toPx()
    val halfBand = greenBandOffset + greenBandWidth / 2f
    
    var x = borderWidthPx + 15.dp.toPx()
    while (x < width - borderWidthPx - 15.dp.toPx()) {
        drawCircle(
            color = goldColor,
            radius = 2.5f.dp.toPx(),
            center = Offset(x, halfBand)
        )
        drawCircle(
            color = accentColor,
            radius = 1.2f.dp.toPx(),
            center = Offset(x - 6.dp.toPx(), halfBand)
        )
        drawCircle(
            color = accentColor,
            radius = 1.2f.dp.toPx(),
            center = Offset(x + 6.dp.toPx(), halfBand)
        )
        
        drawCircle(
            color = goldColor,
            radius = 2.5f.dp.toPx(),
            center = Offset(x, height - halfBand)
        )
        drawCircle(
            color = accentColor,
            radius = 1.2f.dp.toPx(),
            center = Offset(x - 6.dp.toPx(), height - halfBand)
        )
        drawCircle(
            color = accentColor,
            radius = 1.2f.dp.toPx(),
            center = Offset(x + 6.dp.toPx(), height - halfBand)
        )
        
        x += dotSpacing
    }
    
    var y = borderWidthPx + 15.dp.toPx()
    while (y < height - borderWidthPx - 15.dp.toPx()) {
        drawCircle(
            color = goldColor,
            radius = 2.5f.dp.toPx(),
            center = Offset(halfBand, y)
        )
        drawCircle(
            color = accentColor,
            radius = 1.2f.dp.toPx(),
            center = Offset(halfBand, y - 6.dp.toPx())
        )
        drawCircle(
            color = accentColor,
            radius = 1.2f.dp.toPx(),
            center = Offset(halfBand, y + 6.dp.toPx())
        )
        
        drawCircle(
            color = goldColor,
            radius = 2.5f.dp.toPx(),
            center = Offset(width - halfBand, y)
        )
        drawCircle(
            color = accentColor,
            radius = 1.2f.dp.toPx(),
            center = Offset(width - halfBand, y - 6.dp.toPx())
        )
        drawCircle(
            color = accentColor,
            radius = 1.2f.dp.toPx(),
            center = Offset(width - halfBand, y + 6.dp.toPx())
        )
        
        y += dotSpacing
    }
    
    val cornerRadius = 14.dp.toPx()
    val corners = listOf(
        Offset(halfBand, halfBand),
        Offset(width - halfBand, halfBand),
        Offset(halfBand, height - halfBand),
        Offset(width - halfBand, height - halfBand)
    )
    
    corners.forEach { center ->
        drawCircle(
            color = goldColor,
            radius = cornerRadius,
            center = center,
            style = Stroke(width = 1.5f.dp.toPx())
        )
        drawCircle(
            color = borderColor,
            radius = cornerRadius - 2.dp.toPx(),
            center = center
        )
        drawCircle(
            color = accentColor,
            radius = cornerRadius - 6.dp.toPx(),
            center = center
        )
        drawCircle(
            color = goldColor,
            radius = cornerRadius - 10.dp.toPx(),
            center = center
        )
    }
    
    drawRect(
        color = goldColor,
        topLeft = Offset(innerGoldOffset, innerGoldOffset),
        size = androidx.compose.ui.geometry.Size(width - 2 * innerGoldOffset, height - 2 * innerGoldOffset),
        style = Stroke(width = 1.5f.dp.toPx())
    )
    
    val finalGreenOffset = innerGoldOffset + 1.5f.dp.toPx()
    drawRect(
        color = borderColor.copy(alpha = 0.4f),
        topLeft = Offset(finalGreenOffset, finalGreenOffset),
        size = androidx.compose.ui.geometry.Size(width - 2 * finalGreenOffset, height - 2 * finalGreenOffset),
        style = Stroke(width = 0.5f.dp.toPx())
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineQuranBookScreen(
    viewModel: IslamicViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val quranBookmarks by viewModel.quranBookmarks.collectAsStateWithLifecycle()
    val isTtsSpeaking by viewModel.isTtsSpeaking.collectAsStateWithLifecycle()

    // Screen display configurations
    var selectedSurahNum by remember { mutableStateOf(93) } // Defaults to 93 (Surah Ad-Duha) to match user image!
    var showSettingsPanel by remember { mutableStateOf(true) } // Overlaid controls shown by default
    var isSurahSelectorOpen by remember { mutableStateOf(false) }

    // Readability controls
    var arabicTextSize by remember { mutableStateOf(32f) }
    var lineSpacingMultiplier by remember { mutableStateOf(2.0f) }
    var showTranslation by remember { mutableStateOf(false) }
    var showUrduTranslation by remember { mutableStateOf(true) }
    var fontSelected by remember { mutableStateOf("Lateef") } // Default Persian / Indo-Pak clear Quran font

    // Localization and offline Qari configuration states
    var appLanguage by remember { mutableStateOf("en") } // "en", "ur", "ar"
    var offlineQariIndex by remember { mutableStateOf(1) } // 1, 2, 3 (offline voices/pitches)

    // Real offline reciter audio configuration and status states
    var selectedReciterFolder by remember { mutableStateOf("Alafasy_128kbps") }
    var selectedReciterName by remember { mutableStateOf("Sheikh Mishary Alafasy") }
    var activeMediaPlayer by remember { mutableStateOf<android.media.MediaPlayer?>(null) }
    var isAudioPlayingReal by remember { mutableStateOf(false) }
    var currentlyPlayingAyahIndex by remember { mutableStateOf<Int?>(null) }
    var currentlyPlayingWordIndex by remember { mutableStateOf<Int?>(null) }
    var downloadProgressMap by remember { mutableStateOf<Map<String, Float>>(emptyMap()) }
    var isDownloadingAll by remember { mutableStateOf(false) }

    // Clean up mediaPlayer on disposal to prevent audio leakage
    DisposableEffect(Unit) {
        onDispose {
            try {
                activeMediaPlayer?.stop()
                activeMediaPlayer?.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            activeMediaPlayer = null
        }
    }

    val localizedStrings = remember {
        mapOf(
            "en" to mapOf(
                "title" to "Offline Quran Reader (Mushaf)",
                "subtitle" to "Full Screen Book Page Layout",
                "recitation_started" to "Starting recitation...",
                "recitation_stopped" to "Recitation stopped",
                "first_page" to "This is the first page",
                "last_page" to "This is the last page",
                "page" to "Page",
                "juz" to "Juz 30",
                "next" to "Next Page",
                "prev" to "Prev Page",
                "bookmark_added" to "Bookmark saved",
                "bookmark_removed" to "Bookmark removed",
                "select_surah" to "Select Surah",
                "search_placeholder" to "Search Surah...",
                "close" to "Close",
                "hint_tap" to "Tap anywhere on screen to toggle toolbar",
                "font_size" to "Font Size",
                "spacing" to "Line Spacing",
                "font_style" to "Font Style",
                "translation" to "Translation",
                "listen_voice" to "Play Recitation",
                "stop_voice" to "Stop Recitation",
                "offline_qari" to "Offline Qari (Voice)",
                "announcement" to "Now reciting Surah",
                "interface_lang" to "UI Language"
            ),
            "ur" to mapOf(
                "title" to "آف لائن قرآن ریڈر (مصحف)",
                "subtitle" to "مکمل اسکرین صفحات کتاب",
                "recitation_started" to "تلاوت شروع ہو رہی ہے...",
                "recitation_stopped" to "تلاوت روک دی گئی ہے",
                "first_page" to "یہ پہلا صفحہ ہے",
                "last_page" to "یہ آخری صفحہ ہے",
                "page" to "صفحہ",
                "juz" to "پارہ 30",
                "next" to "اگلا صفحہ",
                "prev" to "پچھلا صفحہ",
                "bookmark_added" to "بک مارک محفوظ کر لیا گیا",
                "bookmark_removed" to "بک مارک ہٹا دیا گیا",
                "select_surah" to "سورت منتخب کریں",
                "search_placeholder" to "تلاش کریں...",
                "close" to "بند کریں",
                "hint_tap" to "ٹول بار دیکھنے کے لیے اسکرین پر کہیں بھی ٹیپ کریں",
                "font_size" to "فونٹ سائز",
                "spacing" to "سطروں کا فاصلہ",
                "font_style" to "رسم الخط (فونٹ)",
                "translation" to "ترجمہ",
                "listen_voice" to "تلاوت سنیں",
                "stop_voice" to "تلاوت روکیں",
                "offline_qari" to "آف لائن قاری (آواز)",
                "announcement" to "اب سنئے سورہ",
                "interface_lang" to "زبان (Language)"
            ),
            "ar" to mapOf(
                "title" to "القرآن الكريم (مصحف)",
                "subtitle" to "قراءة كاملة للصفحة",
                "recitation_started" to "بدء التلاوة...",
                "recitation_stopped" to "تم إيقاف التلاوة",
                "first_page" to "هذه هي الصفحة الأولى",
                "last_page" to "هذه هي الصفحة الأخيرة",
                "page" to "صفحة",
                "juz" to "الجزء ٣٠",
                "next" to "الصفحة التالية",
                "prev" to "الصفحة السابقة",
                "bookmark_added" to "تم حفظ الآية",
                "bookmark_removed" to "تم إزالة الآية",
                "select_surah" to "اختر السورة",
                "search_placeholder" to "بحث عن سورة...",
                "close" to "إغلاق",
                "hint_tap" to "انقر في أي مكان لعرض شريط الأدوات",
                "font_size" to "حجم الخط",
                "spacing" to "تباعد الأسطر",
                "font_style" to "رسم الخط",
                "translation" to "الترجمة",
                "listen_voice" to "تشغيل التلاوة",
                "stop_voice" to "إيقاف التلاوة",
                "offline_qari" to "الالقارئ (صوت)",
                "announcement" to "تلاوة سورة",
                "interface_lang" to "اللغة"
            )
        )
    }

    fun txt(key: String): String {
        return localizedStrings[appLanguage]?.get(key) ?: localizedStrings["en"]?.get(key) ?: ""
    }

    val surahList = viewModel.surahList

    // Get current Surah object
    val currentSurah = surahList.firstOrNull { it.number == selectedSurahNum } ?: surahList.firstOrNull { it.number == 93 } ?: surahList[0]

    // Load Ayahs. If Surah 93 is selected, we automatically group Surah 93 and 94 together on the same page
    // just like the printed Mushaf page reference uploaded by the user!
    var pageAyahGroups by remember(selectedSurahNum) {
        val groups = mutableListOf<Pair<Surah, List<Ayah>>>()
        if (selectedSurahNum in listOf(1, 93, 94, 103, 108, 112, 113, 114)) {
            if (selectedSurahNum == 93) {
                // Group 93 (Ad-Duha) and 94 (Al-Inshirah)
                val s93 = surahList.firstOrNull { it.number == 93 } ?: Surah(93, "الضحى", "Ad-Duha", "The Morning Hours", "Meccan", 11)
                val s94 = surahList.firstOrNull { it.number == 94 } ?: Surah(94, "الشرح", "Al-Inshirah", "The Expansion", "Meccan", 8)
                groups.add(Pair(s93, QuranDataProvider.getAyahsForSurah(93)))
                groups.add(Pair(s94, QuranDataProvider.getAyahsForSurah(94)))
            } else {
                groups.add(Pair(currentSurah, QuranDataProvider.getAyahsForSurah(selectedSurahNum)))
            }
        }
        // For other Surahs, we leave it as empty list so that the LaunchedEffect will load the authentic cached
        // verses from the database and prevent flashing the incorrect Surah (Surah Yaseen) text!
        mutableStateOf<List<Pair<Surah, List<Ayah>>>>(groups)
    }

    // Load authentic complete verses dynamically from cache or API and refresh the display state!
    LaunchedEffect(selectedSurahNum) {
        val groups = mutableListOf<Pair<Surah, List<Ayah>>>()
        if (selectedSurahNum == 93) {
            val s93 = surahList.firstOrNull { it.number == 93 } ?: Surah(93, "الضحى", "Ad-Duha", "The Morning Hours", "Meccan", 11)
            val s94 = surahList.firstOrNull { it.number == 94 } ?: Surah(94, "الشرح", "Al-Inshirah", "The Expansion", "Meccan", 8)
            val a93 = viewModel.getAyahsForSurahWithCache(93)
            val a94 = viewModel.getAyahsForSurahWithCache(94)
            groups.add(Pair(s93, a93))
            groups.add(Pair(s94, a94))
        } else {
            val aList = viewModel.getAyahsForSurahWithCache(selectedSurahNum)
            groups.add(Pair(currentSurah, aList))
        }
        pageAyahGroups = groups
    }

    // LaunchedEffect to track and update the currently playing word within the active ayah
    LaunchedEffect(isAudioPlayingReal, currentlyPlayingAyahIndex, pageAyahGroups) {
        if (isAudioPlayingReal && currentlyPlayingAyahIndex != null) {
            // Find the active ayah to get its word list and segment timings
            val activeAyah = pageAyahGroups.flatMap { it.second }
                .firstOrNull { it.numberInSurah == currentlyPlayingAyahIndex }
            if (activeAyah != null) {
                val words = if (currentlyPlayingAyahIndex == 1) {
                    cleanFirstAyahText(activeAyah.textArabic, selectedSurahNum).split(" ").filter { it.isNotEmpty() }
                } else {
                    activeAyah.textArabic.split(" ").filter { it.isNotEmpty() }
                }
                
                // Calculate word start and end percentages based on character length weighting
                val wordLengths = words.map { it.length }
                val totalLength = wordLengths.sum()
                val wordSegments = mutableListOf<Pair<Float, Float>>()
                if (totalLength > 0) {
                    var currentStart = 0f
                    for (length in wordLengths) {
                        val share = length.toFloat() / totalLength
                        val wordEnd = currentStart + share
                        wordSegments.add(Pair(currentStart, wordEnd))
                        currentStart = wordEnd
                    }
                }
                
                while (isAudioPlayingReal) {
                    val player = activeMediaPlayer
                    if (player != null) {
                        try {
                            if (player.isPlaying) {
                                val duration = player.duration
                                val position = player.currentPosition
                                if (duration > 0 && wordSegments.isNotEmpty()) {
                                    val p = position.toFloat() / duration.toFloat()
                                    val index = if (currentlyPlayingAyahIndex == 1 && selectedSurahNum != 1 && selectedSurahNum != 9) {
                                        // The audio of Ayah 1 of any Surah (except Surah 1 and 9) starts with Bismillah recitation.
                                        // We allocate the first ~35% of the audio duration for Bismillah, during which no words are highlighted in the Ayah text.
                                        val bismillahRatio = 0.35f
                                        if (p < bismillahRatio) {
                                            -1
                                        } else {
                                            val cleanedProgress = (p - bismillahRatio) / (1f - bismillahRatio)
                                            val coerced = cleanedProgress.coerceIn(0f, 1f)
                                            var foundIdx = wordSegments.size - 1
                                            for (i in wordSegments.indices) {
                                                if (coerced >= wordSegments[i].first && coerced <= wordSegments[i].second) {
                                                    foundIdx = i
                                                    break
                                                }
                                            }
                                            foundIdx
                                        }
                                    } else {
                                        val coerced = p.coerceIn(0f, 1f)
                                        var foundIdx = wordSegments.size - 1
                                        for (i in wordSegments.indices) {
                                            if (coerced >= wordSegments[i].first && coerced <= wordSegments[i].second) {
                                                foundIdx = i
                                                break
                                            }
                                        }
                                        foundIdx
                                    }
                                    
                                    currentlyPlayingWordIndex = if (index >= 0) index else null
                                }
                            }
                        } catch (e: Exception) {
                            // MediaPlayer state might be changing asynchronously
                        }
                    }
                    kotlinx.coroutines.delay(100) // Poll every 100ms
                }
            }
        } else {
            currentlyPlayingWordIndex = null
        }
    }

    val selectedFontFamily = when (fontSelected) {
        "Lateef" -> LateefFontFamily
        "Amiri" -> AmiriFontFamily
        "Noto Naskh" -> NotoNaskhArabicFontFamily
        else -> QuranScheherazadeFontFamily
    }

    // Real offline / cached quran recitation player helpers
    fun getLocalAudioFile(context: android.content.Context, reciterFolder: String, surahNum: Int, ayahNum: Int): java.io.File {
        val dir = java.io.File(context.filesDir, "offline_quran_audio/$reciterFolder")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return java.io.File(dir, String.format("%03d%03d.mp3", surahNum, ayahNum))
    }

    suspend fun downloadAudioFile(context: android.content.Context, reciterFolder: String, surahNum: Int, ayahNum: Int, onProgress: (Float) -> Unit): Boolean {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val surahStr = String.format("%03d", surahNum)
            val ayahStr = String.format("%03d", ayahNum)
            val urlString = "https://everyayah.com/data/$reciterFolder/$surahStr$ayahStr.mp3"
            val file = getLocalAudioFile(context, reciterFolder, surahNum, ayahNum)
            try {
                val url = java.net.URL(urlString)
                val connection = url.openConnection()
                connection.connect()
                val fileLength = connection.contentLength
                val input = java.io.BufferedInputStream(url.openStream(), 8192)
                val output = java.io.FileOutputStream(file)
                val data = ByteArray(1024)
                var total: Long = 0
                var count: Int
                while (input.read(data).also { count = it } != -1) {
                    total += count
                    if (fileLength > 0) {
                        onProgress(total.toFloat() / fileLength.toFloat())
                    }
                    output.write(data, 0, count)
                }
                output.flush()
                output.close()
                input.close()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                if (file.exists()) {
                    file.delete()
                }
                false
            }
        }
    }

    fun playRealAudio(surahNum: Int, ayahNum: Int, onComplete: () -> Unit) {
        val folder = selectedReciterFolder
        val localFile = getLocalAudioFile(context, folder, surahNum, ayahNum)
        val surahStr = String.format("%03d", surahNum)
        val ayahStr = String.format("%03d", ayahNum)
        
        // Use local file path if downloaded, else use online stream URL
        val audioSource = if (localFile.exists() && localFile.length() > 1024) {
            localFile.absolutePath
        } else {
            val streamUrl = "https://everyayah.com/data/$folder/$surahStr$ayahStr.mp3"
            // Auto cache in background so next play is 100% offline!
            coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                downloadAudioFile(context, folder, surahNum, ayahNum) {}
            }
            streamUrl
        }

        try {
            activeMediaPlayer?.apply {
                setOnCompletionListener(null)
                setOnErrorListener(null)
                try {
                    if (isPlaying) {
                        stop()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                release()
            }
            activeMediaPlayer = null
            
            val mp = android.media.MediaPlayer().apply {
                setAudioAttributes(
                    android.media.AudioAttributes.Builder()
                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(audioSource)
                setOnPreparedListener { player ->
                    player.start()
                    isAudioPlayingReal = true
                    currentlyPlayingAyahIndex = ayahNum
                }
                setOnCompletionListener { player ->
                    player.setOnCompletionListener(null)
                    player.setOnErrorListener(null)
                    currentlyPlayingWordIndex = null
                    coroutineScope.launch {
                        kotlinx.coroutines.delay(3000)
                        try {
                            player.release()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        if (activeMediaPlayer == player) {
                            activeMediaPlayer = null
                        }
                        onComplete()
                    }
                }
                setOnErrorListener { player, _, _ ->
                    try {
                        player.setOnCompletionListener(null)
                        player.setOnErrorListener(null)
                        player.release()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    if (activeMediaPlayer == player) {
                        activeMediaPlayer = null
                    }
                    isAudioPlayingReal = false
                    true
                }
                prepareAsync()
            }
            activeMediaPlayer = mp
        } catch (e: Exception) {
            e.printStackTrace()
            isAudioPlayingReal = false
            onComplete()
        }
    }

    fun stopRealRecitation() {
        try {
            activeMediaPlayer?.apply {
                setOnCompletionListener(null)
                setOnErrorListener(null)
                try {
                    if (isPlaying) {
                        stop()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        activeMediaPlayer = null
        isAudioPlayingReal = false
        // DO NOT set currentlyPlayingAyahIndex to null so it stays highlighted on that same ayah
    }

    // Audio Playback recitation trigger (Plays entire page of real high-quality recitation!)
    fun playRecitation() {
        if (isAudioPlayingReal) {
            stopRealRecitation()
            Toast.makeText(context, txt("recitation_stopped"), Toast.LENGTH_SHORT).show()
        } else {
            viewModel.stopSpeaking() // Stop any fallback TTS
            val allPageAyahs = pageAyahGroups.flatMap { group -> 
                group.second.map { Pair(group.first, it) }
            }
            if (allPageAyahs.isEmpty()) return
            Toast.makeText(context, txt("recitation_started"), Toast.LENGTH_SHORT).show()
            
            // Find if there is an already selected/playing ayah on the page to resume from
            val initialIndex = allPageAyahs.indexOfFirst { it.second.numberInSurah == currentlyPlayingAyahIndex }
            var currentIndex = if (initialIndex >= 0) initialIndex else 0
            
            fun playNext() {
                if (currentIndex >= allPageAyahs.size) {
                    stopRealRecitation()
                    currentlyPlayingAyahIndex = null // Page completed, clear highlight
                    return
                }
                val (surah, ayah) = allPageAyahs[currentIndex]
                
                playRealAudio(surah.number, ayah.numberInSurah) {
                    currentIndex++
                    playNext()
                }
            }
            
            playNext()
        }
    }

    fun downloadPageAudio() {
        if (isDownloadingAll) return
        isDownloadingAll = true
        Toast.makeText(context, "Downloading page audio for offline use...", Toast.LENGTH_SHORT).show()
        coroutineScope.launch {
            var successCount = 0
            val allPageAyahs = pageAyahGroups.flatMap { group -> 
                group.second.map { Pair(group.first, it) }
            }
            allPageAyahs.forEach { (surah, ayah) ->
                val progressKey = "${surah.number}_${ayah.numberInSurah}"
                val success = downloadAudioFile(context, selectedReciterFolder, surah.number, ayah.numberInSurah) { progress ->
                    downloadProgressMap = downloadProgressMap + (progressKey to progress)
                }
                if (success) successCount++
            }
            isDownloadingAll = false
            downloadProgressMap = emptyMap()
            Toast.makeText(context, "Downloaded $successCount verses for offline use!", Toast.LENGTH_LONG).show()
        }
    }

    // Quick book page navigation
    fun navigateToNextPage() {
        if (selectedSurahNum < 114) {
            // If current is 93, the page showed both 93 and 94, so next should be 95
            selectedSurahNum = if (selectedSurahNum == 93) 95 else selectedSurahNum + 1
            stopRealRecitation()
            viewModel.stopSpeaking()
        } else {
            Toast.makeText(context, txt("last_page"), Toast.LENGTH_SHORT).show()
        }
    }

    fun navigateToPrevPage() {
        if (selectedSurahNum > 1) {
            // If current is 95, previous page should go back to 93 (which displays both 93 & 94)
            selectedSurahNum = if (selectedSurahNum == 95) 93 else selectedSurahNum - 1
            stopRealRecitation()
            viewModel.stopSpeaking()
        } else {
            Toast.makeText(context, txt("first_page"), Toast.LENGTH_SHORT).show()
        }
    }

    // Immersive Page view scaffold with zero padding edge-to-edge
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF1B2C24) // Royal emerald night background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // --- CLASSIC BOOK DECORATIVE FRAME CANVAS ---
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFBF7)), // Beautiful thick ivory paper page
                shape = RoundedCornerShape(0.dp), // Complete full screen cover
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                // Toggle settings toolbar on tap to allow pure distraction-free reading
                                showSettingsPanel = !showSettingsPanel
                            }
                        )
                    }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // --- BOOK HEADER MARGIN (OUTSIDE THE FRAME) ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 4.dp, start = 20.dp, end = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Right side in Arabic Mushaf is Juz Name
                        val juzNum = getJuzNumberForSurah(selectedSurahNum)
                        Text(
                            text = getJuzArabicName(juzNum),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B4D3E),
                            fontFamily = selectedFontFamily
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // Left side in Arabic Mushaf is Surah Name
                        Text(
                            text = if (selectedSurahNum == 93) "سُورَةُ الضُّحَىٰ - سُورَةُ الشَّرْح" else "سُورَةُ ${currentSurah.nameArabic}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B4D3E),
                            fontFamily = selectedFontFamily
                        )
                    }

                    // --- THE ORNATE MUSHAF FRAME ---
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .mushafBorderFrame()
                    ) {
                        // Content container inside the borders
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                        ) {
                            // --- SCROLLABLE MULTI-SURAH READ PAGE ---
                            if (pageAyahGroups.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        CircularProgressIndicator(color = Color(0xFF1B4D3E))
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = if (appLanguage == "ur") "آف لائن سورت لوڈ ہو رہی ہے..." else "Loading Surah offline...",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1B4D3E),
                                            fontFamily = selectedFontFamily
                                        )
                                    }
                                }
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .testTag("quran_book_lazy_column"),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    pageAyahGroups.forEach { (surah, ayahs) ->
                                        item {
                                            // Surah Header calligraphic banner box (Matches exactly the printed visual boxes)
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(68.dp)
                                                    .background(Color(0xFFCDEBD7), RoundedCornerShape(4.dp))
                                                    .border(2.dp, Color(0xFFC5A030), RoundedCornerShape(4.dp))
                                                    .drawBehind {
                                                        drawRect(
                                                            color = Color(0xFF1B4D3E),
                                                            topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
                                                            size = androidx.compose.ui.geometry.Size(
                                                                size.width - 8.dp.toPx(),
                                                                size.height - 8.dp.toPx()
                                                            ),
                                                            style = Stroke(width = 1.dp.toPx())
                                                        )
                                                        val bracketSize = 8.dp.toPx()
                                                        val goldColor = Color(0xFFC5A030)
                                                        drawRect(
                                                            color = goldColor,
                                                            topLeft = Offset(6.dp.toPx(), 6.dp.toPx()),
                                                            size = androidx.compose.ui.geometry.Size(bracketSize, bracketSize)
                                                        )
                                                        drawRect(
                                                            color = goldColor,
                                                            topLeft = Offset(size.width - 6.dp.toPx() - bracketSize, 6.dp.toPx()),
                                                            size = androidx.compose.ui.geometry.Size(bracketSize, bracketSize)
                                                        )
                                                        drawRect(
                                                            color = goldColor,
                                                            topLeft = Offset(6.dp.toPx(), size.height - 6.dp.toPx() - bracketSize),
                                                            size = androidx.compose.ui.geometry.Size(bracketSize, bracketSize)
                                                        )
                                                        drawRect(
                                                            color = goldColor,
                                                            topLeft = Offset(size.width - 6.dp.toPx() - bracketSize, size.height - 6.dp.toPx() - bracketSize),
                                                            size = androidx.compose.ui.geometry.Size(bracketSize, bracketSize)
                                                        )
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "سُورَةُ ${surah.nameArabic}",
                                                    fontFamily = selectedFontFamily,
                                                    fontSize = 26.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = Color(0xFF1B4D3E),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }

                                        // Bismillah banner if not Al-Fatiha and not Al-Tawbah
                                        if (surah.number != 1 && surah.number != 9) {
                                            item {
                                                Box(
                                                    modifier = Modifier
                                                        .padding(vertical = 4.dp)
                                                        .fillMaxWidth(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                                        fontFamily = selectedFontFamily,
                                                        fontSize = (arabicTextSize + 4).sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF1B4D3E),
                                                        textAlign = TextAlign.Center,
                                                        lineHeight = ((arabicTextSize + 4) * lineSpacingMultiplier).sp
                                                    )
                                                }
                                            }
                                        }

                                        // Render the verses with flow and custom gold circle markers at the end of each verse
                                        item {
                                            var textLayoutResult by remember { mutableStateOf<androidx.compose.ui.text.TextLayoutResult?>(null) }

                                            val annotatedArabic = buildAnnotatedString {
                                                ayahs.forEach { ayah ->
                                                    val start = this.length
                                                    val textToAppend = if (ayah.numberInSurah == 1) {
                                                        cleanFirstAyahText(ayah.textArabic, surah.number)
                                                    } else {
                                                        ayah.textArabic
                                                    }
                                                    append(textToAppend)
                                                    
                                                    val arabicNumber = convertToArabicNumerals(ayah.numberInSurah)
                                                    val marker = "\u202F\uFD3F$arabicNumber\uFD3E"
                                                    append(marker)
                                                    append(" ")
                                                    val end = this.length
                                                    
                                                    val isPlaying = isAudioPlayingReal && currentlyPlayingAyahIndex == ayah.numberInSurah
                                                    if (isPlaying) {
                                                        addStyle(
                                                            SpanStyle(
                                                                background = Color(0xFFE2C974).copy(alpha = 0.25f),
                                                                color = Color(0xFFD32F2F), // Distinct beautiful red color for the active verse
                                                                fontWeight = FontWeight.Bold
                                                            ),
                                                            start = start,
                                                            end = end
                                                        )
                                                    } else {
                                                        addStyle(
                                                            SpanStyle(
                                                                color = Color(0xFF1B4D3E),
                                                                fontWeight = FontWeight.Bold
                                                            ),
                                                            start = start,
                                                            end = start + textToAppend.length
                                                        )
                                                        addStyle(
                                                            SpanStyle(
                                                                color = Color(0xFFC5A030),
                                                                fontWeight = FontWeight.Bold
                                                            ),
                                                            start = start + textToAppend.length,
                                                            end = end
                                                        )
                                                    }
                                                    
                                                    addStringAnnotation(
                                                        "AYAH_CLICK",
                                                        "${surah.number}_${ayah.numberInSurah}",
                                                        start,
                                                        end
                                                    )
                                                }
                                            }
                                            
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 12.dp, horizontal = 4.dp)
                                            ) {
                                                Text(
                                                    text = annotatedArabic,
                                                    onTextLayout = { textLayoutResult = it },
                                                    style = TextStyle(
                                                        fontFamily = selectedFontFamily,
                                                        fontSize = arabicTextSize.sp,
                                                        lineHeight = (arabicTextSize * lineSpacingMultiplier).sp,
                                                        textAlign = TextAlign.Justify,
                                                        textDirection = TextDirection.Rtl
                                                    ),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .pointerInput(Unit) {
                                                            detectTapGestures(
                                                                onTap = { offset ->
                                                                    val layout = textLayoutResult
                                                                    if (layout != null) {
                                                                        val characterOffset = layout.getOffsetForPosition(offset)
                                                                        annotatedArabic.getStringAnnotations(tag = "AYAH_CLICK", start = characterOffset, end = characterOffset)
                                                                            .firstOrNull()?.let { annotation ->
                                                                                val parts = annotation.item.split("_")
                                                                                val sNum = parts[0].toInt()
                                                                                val aNum = parts[1].toInt()
                                                                                
                                                                                isAudioPlayingReal = true
                                                                                currentlyPlayingAyahIndex = aNum
                                                                                playRealAudio(sNum, aNum) {
                                                                                    isAudioPlayingReal = false
                                                                                    currentlyPlayingAyahIndex = null
                                                                                }
                                                                                
                                                                                Toast.makeText(context, "Playing Verse $aNum...", Toast.LENGTH_SHORT).show()
                                                                            }
                                                                    }
                                                                },
                                                                onLongPress = { offset ->
                                                                    val layout = textLayoutResult
                                                                    if (layout != null) {
                                                                        val characterOffset = layout.getOffsetForPosition(offset)
                                                                        annotatedArabic.getStringAnnotations(tag = "AYAH_CLICK", start = characterOffset, end = characterOffset)
                                                                            .firstOrNull()?.let { annotation ->
                                                                                val parts = annotation.item.split("_")
                                                                                val sNum = parts[0].toInt()
                                                                                val aNum = parts[1].toInt()
                                                                                val tappedAyah = ayahs.firstOrNull { it.numberInSurah == aNum }
                                                                                if (tappedAyah != null) {
                                                                                    viewModel.toggleQuranBookmark(surah, tappedAyah)
                                                                                    Toast.makeText(context, "Toggled bookmark for Verse $aNum", Toast.LENGTH_SHORT).show()
                                                                                }
                                                                            }
                                                                    }
                                                                }
                                                            )
                                                        }
                                                )
                                                
                                                if (showTranslation) {
                                                    Spacer(modifier = Modifier.height(24.dp))
                                                    Divider(color = Color(0xFFE2C974).copy(alpha = 0.4f), thickness = 1.dp)
                                                    Spacer(modifier = Modifier.height(12.dp))
                                                    
                                                    Text(
                                                        text = if (showUrduTranslation) "ترجمہ (Urdu Translation)" else "English Translation",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFFC5A030),
                                                        modifier = Modifier.padding(bottom = 8.dp)
                                                    )
                                                    
                                                    ayahs.forEach { ayah ->
                                                        val isPlaying = isAudioPlayingReal && currentlyPlayingAyahIndex == ayah.numberInSurah
                                                        Column(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .background(
                                                                    if (isPlaying) Color(0xFFE2C974).copy(alpha = 0.12f) else Color.Transparent,
                                                                    RoundedCornerShape(6.dp)
                                                                )
                                                                .padding(6.dp)
                                                        ) {
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                                verticalAlignment = Alignment.Top
                                                            ) {
                                                                if (!showUrduTranslation) {
                                                                    Box(
                                                                        modifier = Modifier
                                                                            .size(22.dp)
                                                                            .background(Color(0xFFF7F1E1), CircleShape)
                                                                            .border(1.dp, Color(0xFFD4AF37), CircleShape),
                                                                        contentAlignment = Alignment.Center
                                                                    ) {
                                                                        Text(
                                                                            text = ayah.numberInSurah.toString(),
                                                                            fontSize = 9.sp,
                                                                            fontWeight = FontWeight.Bold,
                                                                            color = Color(0xFF1B4D3E)
                                                                        )
                                                                    }
                                                                }
                                                                
                                                                Column(modifier = Modifier.weight(1f)) {
                                                                    Text(
                                                                        text = if (showUrduTranslation) ayah.textUrdu else ayah.textEnglish,
                                                                        fontFamily = if (showUrduTranslation) NotoNastaliqUrduFontFamily else null,
                                                                        fontSize = if (showUrduTranslation) 13.sp else 11.sp,
                                                                        lineHeight = if (showUrduTranslation) 22.sp else 16.sp,
                                                                        color = Color(0xFF333333),
                                                                        textAlign = if (showUrduTranslation) TextAlign.Right else TextAlign.Left,
                                                                        modifier = Modifier.fillMaxWidth()
                                                                    )
                                                                }
                                                                
                                                                if (showUrduTranslation) {
                                                                    Box(
                                                                        modifier = Modifier
                                                                            .size(22.dp)
                                                                            .background(Color(0xFFF7F1E1), CircleShape)
                                                                            .border(1.dp, Color(0xFFD4AF37), CircleShape),
                                                                        contentAlignment = Alignment.Center
                                                                    ) {
                                                                        Text(
                                                                            text = convertToUrduNumerals(ayah.numberInSurah),
                                                                            fontFamily = NotoNastaliqUrduFontFamily,
                                                                            fontSize = 10.sp,
                                                                            fontWeight = FontWeight.Bold,
                                                                            color = Color(0xFF1B4D3E)
                                                                        )
                                                                    }
                                                                }
                                                                
                                                                val isBookmarked = quranBookmarks.any { it.surahNumber == surah.number && it.ayahNumber == ayah.numberInSurah }
                                                                IconButton(
                                                                    onClick = { 
                                                                        viewModel.toggleQuranBookmark(surah, ayah)
                                                                    },
                                                                    modifier = Modifier.size(22.dp)
                                                                ) {
                                                                    Icon(
                                                                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                                                        contentDescription = null,
                                                                        tint = if (isBookmarked) Color(0xFFD4AF37) else Color.LightGray,
                                                                        modifier = Modifier.size(13.dp)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // --- PAGE BOTTOM MARGIN (OUTSIDE THE FRAME) ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Prev page button
                        IconButton(
                            onClick = { navigateToPrevPage() },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF1B4D3E), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Prev",
                                tint = Color(0xFFE2C974),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Centered page number in Arabic numerals
                        val pageNum = getQuranPageNumber(selectedSurahNum)
                        Text(
                            text = convertToArabicNumerals(pageNum),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B4D3E),
                            fontFamily = selectedFontFamily
                        )

                        // Next page button
                        IconButton(
                            onClick = { navigateToNextPage() },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF1B4D3E), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next",
                                tint = Color(0xFFE2C974),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // --- FLOATING OVERLAID CONTROL PANEL ---
            AnimatedVisibility(
                visible = showSettingsPanel,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2C24).copy(alpha = 0.95f)),
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                    border = BorderStroke(1.5.dp, Color(0xFFD4AF37)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .statusBarsPadding()
                    ) {
                        // Header title of overlaid panel
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { 
                                        viewModel.stopSpeaking()
                                        onBack() 
                                    },
                                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White.copy(alpha = 0.1f))
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = txt("title"),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFE2C974)
                                    )
                                    Text(
                                        text = txt("subtitle"),
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            // Speech trigger button
                            Button(
                                onClick = { playRecitation() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isAudioPlayingReal) Color.Red else Color(0xFFC5A030)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = if (isAudioPlayingReal) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                    contentDescription = "Voice",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isAudioPlayingReal) txt("stop_voice") else txt("listen_voice"),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // UI Language selector
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(txt("interface_lang"), fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                    .padding(2.dp)
                            ) {
                                listOf("en" to "English", "ur" to "اردو", "ar" to "العربية").forEach { (code, label) ->
                                    val isSelected = appLanguage == code
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color(0xFF1B2C24) else Color.White,
                                        modifier = Modifier
                                            .background(if (isSelected) Color(0xFFE2C974) else Color.Transparent, RoundedCornerShape(6.dp))
                                            .clickable { 
                                                appLanguage = code 
                                                // Sync Urdu translation checked state with lang choice
                                                if (code == "ur") showUrduTranslation = true
                                                if (code == "en") showUrduTranslation = false
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // Real Reciter Selector & Offline Download controls
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Reciter / قاری:", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                    .padding(2.dp)
                            ) {
                                listOf(
                                    "Alafasy_128kbps" to "Alafasy",
                                    "Abdurrahmaan_As-Sudais_192kbps" to "Sudais",
                                    "Ghamadi_40kbps" to "Ghamdi"
                                ).forEach { (folder, label) ->
                                    val isSelected = selectedReciterFolder == folder
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color(0xFF1B2C24) else Color.White,
                                        modifier = Modifier
                                            .background(if (isSelected) Color(0xFFE2C974) else Color.Transparent, RoundedCornerShape(6.dp))
                                            .clickable { 
                                                selectedReciterFolder = folder
                                                selectedReciterName = when (folder) {
                                                    "Alafasy_128kbps" -> "Sheikh Mishary Alafasy"
                                                    "Abdurrahmaan_As-Sudais_192kbps" -> "Sheikh Abdul Rahman Al-Sudais"
                                                    else -> "Sheikh Saad Al-Ghamdi"
                                                }
                                                stopRealRecitation()
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // Offline download action row
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Offline Audio / آف لائن تلاوت:", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            Button(
                                onClick = { downloadPageAudio() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isDownloadingAll) Color.Gray else Color(0xFFE2C974)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp),
                                enabled = !isDownloadingAll
                             ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "Download",
                                    tint = Color(0xFF1B2C24),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isDownloadingAll) "Downloading..." else "Download Page",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B2C24)
                                )
                            }
                        }

                        // Selection & Reading settings
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Surah picker button
                            Button(
                                onClick = { isSurahSelectorOpen = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.12f)),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFE2C974).copy(alpha = 0.5f))
                            ) {
                                Icon(Icons.Default.ListAlt, contentDescription = "Surah Selector", modifier = Modifier.size(16.dp), tint = Color(0xFFE2C974))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("${txt("select_surah")} (${selectedSurahNum})", fontSize = 11.sp, color = Color.White)
                            }

                            // Translation toggler
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(txt("translation"), fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Switch(
                                    checked = showTranslation,
                                    onCheckedChange = { showTranslation = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color(0xFFE2C974),
                                        checkedTrackColor = Color(0xFF1B4D3E)
                                    )
                                )
                            }
                        }

                        if (showTranslation) {
                            Spacer(modifier = Modifier.height(8.dp))
                            // Language picker for translations
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Row(
                                    modifier = Modifier
                                        .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                        .padding(2.dp)
                                ) {
                                    Text(
                                        text = "اردو / Urdu",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (showUrduTranslation) Color(0xFF1B2C24) else Color.White,
                                        modifier = Modifier
                                            .background(if (showUrduTranslation) Color(0xFFE2C974) else Color.Transparent, RoundedCornerShape(6.dp))
                                            .clickable { showUrduTranslation = true }
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                    Text(
                                        text = "English",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (!showUrduTranslation) Color(0xFF1B2C24) else Color.White,
                                        modifier = Modifier
                                            .background(if (!showUrduTranslation) Color(0xFFE2C974) else Color.Transparent, RoundedCornerShape(6.dp))
                                            .clickable { showUrduTranslation = false }
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Text Size Slider (From small up to extra large)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(txt("font_size"), fontSize = 11.sp, color = Color.White, modifier = Modifier.width(100.dp))
                            Text("A-", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
                            Slider(
                                value = arabicTextSize,
                                onValueChange = { arabicTextSize = it },
                                valueRange = 22f..52f,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp),
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFFE2C974),
                                    activeTrackColor = Color(0xFFE2C974)
                                )
                            )
                            Text("A+", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        // Line spacing slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(txt("spacing"), fontSize = 11.sp, color = Color.White, modifier = Modifier.width(100.dp))
                            Text("1.5x", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
                            Slider(
                                value = lineSpacingMultiplier,
                                onValueChange = { lineSpacingMultiplier = it },
                                valueRange = 1.5f..2.5f,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp),
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFFE2C974),
                                    activeTrackColor = Color(0xFFE2C974)
                                )
                            )
                            Text("2.5x", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        // Font selector row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(txt("font_style"), fontSize = 11.sp, color = Color.White)
                            Row {
                                listOf("Lateef", "Amiri", "Noto Naskh").forEach { f ->
                                    val isSelected = fontSelected == f
                                    val name = when (f) {
                                        "Lateef" -> "Persian"
                                        "Amiri" -> "Uthmanic"
                                        else -> "Modern"
                                    }
                                    Text(
                                        text = name,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color(0xFF1B2C24) else Color.White,
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .background(if (isSelected) Color(0xFFE2C974) else Color.White.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                            .clickable { fontSelected = f }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- HINT OVERLAY IF TOOLBAR COLLAPSED ---
            if (!showSettingsPanel) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 60.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = txt("hint_tap"),
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // --- SELECTION SURAH DIALOG OVERLAY ---
    if (isSurahSelectorOpen) {
        AlertDialog(
            onDismissRequest = { isSurahSelectorOpen = false },
            title = { Text(txt("select_surah"), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
            text = {
                Column(modifier = Modifier.height(350.dp)) {
                    var filterQuery by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = filterQuery,
                        onValueChange = { filterQuery = it },
                        placeholder = { Text(txt("search_placeholder")) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    val filteredList = surahList.filter {
                        it.nameEnglish.contains(filterQuery, ignoreCase = true) ||
                        it.number.toString().contains(filterQuery)
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredList) { surah ->
                            Card(
                                onClick = {
                                    selectedSurahNum = surah.number
                                    isSurahSelectorOpen = false
                                    viewModel.stopSpeaking()
                                },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedSurahNum == surah.number) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                                ),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedSurahNum = surah.number
                                        isSurahSelectorOpen = false
                                        viewModel.stopSpeaking()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row {
                                        Text("${surah.number}. ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        Text(surah.nameEnglish, fontWeight = FontWeight.SemiBold)
                                    }
                                    Text(surah.nameArabic, color = Color(0xFF1B4D3E), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { isSurahSelectorOpen = false }) {
                    Text(txt("close"))
                }
            }
        )
    }
}
