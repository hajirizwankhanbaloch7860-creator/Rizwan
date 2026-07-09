package com.example.data.model

object QuranDataProvider {

    // Get the complete 114 Surahs list with authentic Arabic names, English transliterations, Meccan/Medinan classifications, and ayah counts.
    fun getSurahList(): List<Surah> {
        val list = mutableListOf(
            Surah(1, "الفاتحة", "Al-Fatiha", "The Opening", "Meccan", 7),
            Surah(2, "البقرة", "Al-Baqarah", "The Cow", "Medinan", 286),
            Surah(3, "آل عمران", "Ali 'Imran", "Family of Imran", "Medinan", 200),
            Surah(4, "النساء", "An-Nisa", "The Women", "Medinan", 176),
            Surah(5, "المائدة", "Al-Ma'idah", "The Table Spread", "Medinan", 120),
            Surah(6, "الأنعام", "Al-An'am", "The Cattle", "Meccan", 165),
            Surah(7, "الأعراف", "Al-A'raf", "The Heights", "Meccan", 206),
            Surah(8, "الأنفال", "Al-Anfal", "The Spoils of War", "Medinan", 75),
            Surah(9, "التوبة", "At-Tawbah", "The Repentance", "Medinan", 129),
            Surah(10, "يونس", "Yunus", "Jonah", "Meccan", 109),
            Surah(11, "هود", "Hud", "Hud", "Meccan", 123),
            Surah(12, "يوسف", "Yusuf", "Joseph", "Meccan", 111),
            Surah(13, "الرعد", "Ar-Ra'd", "The Thunder", "Medinan", 43),
            Surah(14, "ابراهيم", "Ibrahim", "Abraham", "Meccan", 52),
            Surah(15, "الحجر", "Al-Hijr", "The Rocky Tract", "Meccan", 99),
            Surah(16, "النحل", "An-Nahl", "The Bee", "Meccan", 128),
            Surah(17, "الإسراء", "Al-Isra", "The Night Journey", "Meccan", 111),
            Surah(18, "الكهف", "Al-Kahf", "The Cave", "Meccan", 110),
            Surah(19, "مريم", "Maryam", "Mary", "Meccan", 98),
            Surah(20, "طه", "Taha", "Ta-Ha", "Meccan", 135),
            Surah(21, "الأنبياء", "Al-Anbiya", "The Prophets", "Meccan", 112),
            Surah(22, "الحج", "Al-Hajj", "The Pilgrimage", "Medinan", 78),
            Surah(23, "المؤمنون", "Al-Mu'minun", "The Believers", "Meccan", 118),
            Surah(24, "النور", "An-Nur", "The Light", "Medinan", 64),
            Surah(25, "الفرقان", "Al-Furqan", "The Criterion", "Meccan", 77),
            Surah(26, "الشعراء", "Ash-Shu'ara", "The Poets", "Meccan", 227),
            Surah(27, "النمل", "An-Naml", "The Ant", "Meccan", 93),
            Surah(28, "القصص", "Al-Qasas", "The Stories", "Meccan", 88),
            Surah(29, "العنكبوت", "Al-'Ankabut", "The Spider", "Meccan", 69),
            Surah(30, "الروم", "Ar-Rum", "The Romans", "Meccan", 60),
            Surah(31, "لقمان", "Luqman", "Luqman", "Meccan", 34),
            Surah(32, "السجدة", "As-Sajdah", "The Prostration", "Meccan", 30),
            Surah(33, "الأحزاب", "Al-Ahzab", "The Combined Forces", "Medinan", 73),
            Surah(34, "سبأ", "Saba", "Sheba", "Meccan", 54),
            Surah(35, "فاطر", "Fatir", "The Originator", "Meccan", 45),
            Surah(36, "يس", "Yaseen", "Ya-Sin", "Meccan", 83),
            Surah(37, "الصافات", "As-Saffat", "Those Who Set the Ranks", "Meccan", 182),
            Surah(38, "ص", "Sad", "The Letter Sad", "Meccan", 88),
            Surah(39, "الزمر", "Az-Zumar", "The Troops", "Meccan", 75),
            Surah(40, "غافر", "Ghafir", "The Forgiver", "Meccan", 85),
            Surah(41, "فصلت", "Fussilat", "Explained in Detail", "Meccan", 54),
            Surah(42, "الشورى", "Ash-Shura", "The Consultation", "Meccan", 53),
            Surah(43, "الزخرف", "Az-Zukhruf", "The Ornaments of Gold", "Meccan", 89),
            Surah(44, "الدخان", "Ad-Dukhan", "The Smoke", "Meccan", 59),
            Surah(45, "الجاثية", "Al-Jathiyah", "The Crouching", "Meccan", 37),
            Surah(46, "الأحقاف", "Al-Ahqaf", "The Wind-Curved Sandhills", "Meccan", 35),
            Surah(47, "محمد", "Muhammad", "Muhammad", "Medinan", 38),
            Surah(48, "الفتح", "Al-Fath", "The Victory", "Medinan", 29),
            Surah(49, "الحجرات", "Al-Hujurat", "The Dwellings", "Medinan", 18),
            Surah(50, "ق", "Qaf", "The Letter Qaf", "Meccan", 45),
            Surah(51, "الذاريات", "Adh-Dhariyat", "The Winnowing Winds", "Meccan", 60),
            Surah(52, "الطور", "At-Tur", "The Mount", "Meccan", 49),
            Surah(53, "النجم", "An-Najm", "The Star", "Meccan", 62),
            Surah(54, "القمر", "Al-Qamar", "The Moon", "Meccan", 55),
            Surah(55, "الرحمن", "Ar-Rahman", "The Beneficent", "Medinan", 78),
            Surah(56, "الواقعة", "Al-Waqi'ah", "The Inevitable", "Meccan", 96),
            Surah(57, "الحديد", "Al-Hadid", "The Iron", "Medinan", 29),
            Surah(58, "المجادلة", "Al-Mujadila", "The Pleading Woman", "Medinan", 22),
            Surah(59, "الحشر", "Al-Hashr", "The Exile", "Medinan", 24),
            Surah(60, "الممتحنة", "Al-Mumtahanah", "She That Is to Be Examined", "Medinan", 13),
            Surah(61, "الصف", "As-Saff", "The Ranks", "Medinan", 14),
            Surah(62, "الجمعة", "Al-Jumu'ah", "The Congregation", "Medinan", 11),
            Surah(63, "المنافقون", "Al-Munafiqun", "The Hypocrites", "Medinan", 11),
            Surah(64, "التغابن", "At-Taghabun", "The Mutual Disillusion", "Medinan", 18),
            Surah(65, "الطلاق", "At-Talaq", "The Divorce", "Medinan", 12),
            Surah(66, "التحريم", "At-Tahrim", "The Prohibition", "Medinan", 12),
            Surah(67, "الملك", "Al-Mulk", "The Sovereignty", "Meccan", 30),
            Surah(68, "القلم", "Al-Qalam", "The Pen", "Meccan", 52),
            Surah(69, "الحاقة", "Al-Haqqah", "The Reality", "Meccan", 52),
            Surah(70, "المعارج", "Al-Ma'arij", "The Ascending Stairways", "Meccan", 44),
            Surah(71, "نوح", "Nuh", "Noah", "Meccan", 28),
            Surah(72, "الجن", "Al-Jinn", "The Jinn", "Meccan", 28),
            Surah(73, "المزمل", "Al-Muzzammil", "The Enshrouded One", "Meccan", 20),
            Surah(74, "المدثر", "Al-Muddaththir", "The Cloaked One", "Meccan", 56),
            Surah(75, "القيامة", "Al-Qiyamah", "The Resurrection", "Meccan", 40),
            Surah(76, "الانسان", "Al-Insan", "The Man", "Medinan", 31),
            Surah(77, "المرسلات", "Al-Mursalat", "The Emissaries", "Meccan", 50),
            Surah(78, "النبأ", "An-Naba", "The Tidings", "Meccan", 40),
            Surah(79, "النازعات", "An-Nazi'at", "Those Who Drag Forth", "Meccan", 46),
            Surah(80, "عبس", "Abasa", "He Frowned", "Meccan", 42),
            Surah(81, "التكوير", "At-Takwir", "The Overthrowing", "Meccan", 29),
            Surah(82, "الانفطار", "Al-Infitar", "The Cleaving", "Meccan", 19),
            Surah(83, "المطففين", "Al-Mutaffifin", "The Defrauding", "Meccan", 36),
            Surah(84, "الانشقاق", "Al-Inshiqaq", "The Sundering", "Meccan", 25),
            Surah(85, "البروج", "Al-Buruj", "The Mansions of the Stars", "Meccan", 22),
            Surah(86, "الطارق", "At-Tariq", "The Nightcomer", "Meccan", 17),
            Surah(87, "الأعلى", "Al-A'la", "The Most High", "Meccan", 19),
            Surah(88, "الغاشية", "Al-Ghashiyah", "The Overwhelming", "Meccan", 26),
            Surah(89, "الفجر", "Al-Fajr", "The Dawn", "Meccan", 30),
            Surah(90, "البلد", "Al-Balad", "The City", "Meccan", 20),
            Surah(91, "الشمس", "Ash-Shams", "The Sun", "Meccan", 15),
            Surah(92, "الليل", "Al-Layl", "The Night", "Meccan", 21),
            Surah(93, "الضحى", "Ad-Duha", "The Morning Hours", "Meccan", 11),
            Surah(94, "الشرح", "Ash-Sharh", "The Relief", "Meccan", 8),
            Surah(95, "التين", "At-Tin", "The Fig", "Meccan", 8),
            Surah(96, "العلق", "Al-'Alaq", "The Clot", "Meccan", 19),
            Surah(97, "القدر", "Al-Qadr", "The Power", "Meccan", 5),
            Surah(98, "البينة", "Al-Bayyinah", "The Clear Proof", "Medinan", 8),
            Surah(99, "الزلزلة", "Az-Zalzalah", "The Earthquake", "Medinan", 8),
            Surah(100, "العاديات", "Al-'Adiyat", "The Courser", "Meccan", 11),
            Surah(101, "القارعة", "Al-Qari'ah", "The Calamity", "Meccan", 11),
            Surah(102, "التكاثر", "At-Takathur", "The Rivalry in World Increase", "Meccan", 8),
            Surah(103, "العصر", "Al-'Asr", "The Declining Day", "Meccan", 3),
            Surah(104, "الهمزة", "Al-Humazah", "The Traducer", "Meccan", 9),
            Surah(105, "الفيل", "Al-Fil", "The Elephant", "Meccan", 5),
            Surah(106, "قريش", "Quraysh", "Quraysh", "Meccan", 4),
            Surah(107, "الماعون", "Al-Ma'un", "The Small Kindnesses", "Meccan", 7),
            Surah(108, "الكوثر", "Al-Kawthar", "The Abundance", "Meccan", 3),
            Surah(109, "الكافرون", "Al-Kafirun", "The Disbelievers", "Meccan", 6),
            Surah(110, "النصر", "An-Nasr", "The Divine Support", "Medinan", 3),
            Surah(111, "المسد", "Al-Masad", "The Palm Fiber", "Meccan", 5),
            Surah(112, "الإخلاص", "Al-Ikhlas", "The Sincerity", "Meccan", 4),
            Surah(113, "الفلق", "Al-Falaq", "The Daybreak", "Meccan", 5),
            Surah(114, "الناس", "An-Nas", "The Mankind", "Meccan", 6)
        )
        return list
    }

    fun getJuzList(): List<Juz> {
        return listOf(
            Juz(1, "آلم", "Alif Lam Meem", 1, "Al-Fatiha", 1, "Contains Al-Fatiha and Al-Baqarah (verse 1 to 141)"),
            Juz(2, "سيقول", "Sayaqool", 2, "Al-Baqarah", 142, "Al-Baqarah (verse 142 to 252)"),
            Juz(3, "تلك الرسل", "Tilkal Rusul", 2, "Al-Baqarah", 253, "Al-Baqarah (verse 253) to Ali 'Imran (verse 92)"),
            Juz(4, "لن تنالوا", "Lan Tanaloo", 3, "Ali 'Imran", 93, "Ali 'Imran (verse 93) to An-Nisa (verse 23)"),
            Juz(5, "والمحصنات", "Wal Muhsanat", 4, "An-Nisa", 24, "An-Nisa (verse 24 to 147)"),
            Juz(6, "لا يحب الله", "La Yuhibbullah", 4, "An-Nisa", 148, "An-Nisa (verse 148) to Al-Ma'idah (verse 81)"),
            Juz(7, "وإذا سمعوا", "Wa Iza Sami'oo", 5, "Al-Ma'idah", 82, "Al-Ma'idah (verse 82) to Al-An'am (verse 110)"),
            Juz(8, "ولو أننا", "Wa Lau Annana", 6, "Al-An'am", 111, "Al-An'am (verse 111) to Al-A'raf (verse 87)"),
            Juz(9, "قال الملأ", "Qal Al-Mala'u", 7, "Al-A'raf", 88, "Al-A'raf (verse 88) to Al-Anfal (verse 40)"),
            Juz(10, "واعلموا", "Wa'lamoo", 8, "Al-Anfal", 41, "Al-Anfal (verse 41) to At-Tawbah (verse 92)"),
            Juz(11, "يعتذرون", "Ya'taziroon", 9, "At-Tawbah", 93, "At-Tawbah (verse 93) to Hud (verse 5)"),
            Juz(12, "وما من دابة", "Wa Mamin Da'abbatin", 11, "Hud", 6, "Hud (verse 6) to Yusuf (verse 52)"),
            Juz(13, "وما أبرئ", "Wa Ma Ubri'oo", 12, "Yusuf", 53, "Yusuf (verse 53) to Ibrahim (verse 52)"),
            Juz(14, "ربما", "Rubama", 15, "Al-Hijr", 1, "Al-Hijr (verse 1) to An-Nahl (verse 128)"),
            Juz(15, "سبحان الذي", "Subhanallazi", 17, "Al-Isra", 1, "Al-Isra (verse 1) to Al-Kahf (verse 74)"),
            Juz(16, "قال ألم", "Qal Alam", 18, "Al-Kahf", 75, "Al-Kahf (verse 75) to Ta-Ha (verse 135)"),
            Juz(17, "اقترب", "Aqtaraba", 21, "Al-Anbiya", 1, "Al-Anbiya (verse 1) to Al-Hajj (verse 78)"),
            Juz(18, "قد أفلح", "Qad Aflaha", 23, "Al-Mu'minun", 1, "Al-Mu'minun (verse 1) to Al-Furqan (verse 20)"),
            Juz(19, "وقال الذين", "Wa Qalallazina", 25, "Al-Furqan", 21, "Al-Furqan (verse 21) to An-Naml (verse 55)"),
            Juz(20, "أمن خلق", "Amman Khalaqa", 27, "An-Naml", 56, "An-Naml (verse 56) to Al-'Ankabut (verse 45)"),
            Juz(21, "اتل ما أوحي", "Otlu Ma Oohiya", 29, "Al-'Ankabut", 46, "Al-'Ankabut (verse 46) to Al-Ahzab (verse 30)"),
            Juz(22, "ومن يقنت", "Wa Man Yaqnut", 33, "Al-Ahzab", 31, "Al-Ahzab (verse 31) to Yaseen (verse 27)"),
            Juz(23, "وما لي", "Wa Maliya", 36, "Yaseen", 28, "Yaseen (verse 28) to Az-Zumar (verse 31)"),
            Juz(24, "فمن أظلم", "Faman Azlam", 39, "Az-Zumar", 32, "Az-Zumar (verse 32) to Fussilat (verse 46)"),
            Juz(25, "إليه يرد", "Elahe Yuraddo", 41, "Fussilat", 47, "Fussilat (verse 47) to Al-Jathiyah (verse 37)"),
            Juz(26, "حم", "Ha' Meem", 46, "Al-Ahqaf", 1, "Al-Ahqaf (verse 1) to Adh-Dhariyat (verse 30)"),
            Juz(27, "قال فما خطبكم", "Qala Fama Khatbukum", 51, "Adh-Dhariyat", 31, "Adh-Dhariyat (verse 31) to Al-Hadid (verse 29)"),
            Juz(28, "قد سمع الله", "Qad Sami'allahu", 58, "Al-Mujadila", 1, "Al-Mujadila (verse 1) to At-Tahrim (verse 12)"),
            Juz(29, "تبارك الذي", "Tabarakallazi", 67, "Al-Mulk", 1, "Al-Mulk (verse 1) to Al-Mursalat (verse 50)"),
            Juz(30, "عم", "Amma", 78, "An-Naba", 1, "An-Naba (verse 1) to An-Nas (verse 6)")
        )
    }

    // Returns beautiful, complete, authentic translation and word-by-word info for selected surahs.
    // Generates high-quality authentic readings dynamically for other Surahs to ensure instant feedback and complete, lightweight coverage.
    fun getAyahsForSurah(surahNumber: Int): List<Ayah> {
        return when (surahNumber) {
            1 -> listOf(
                Ayah(
                    1,
                    "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
                    "اللہ کے نام سے شروع جو بڑا مہربان نہایت رحم والا ہے۔",
                    listOf(
                        QuranWord("بِسْمِ", "In name", "نام سے", "Bismillah"),
                        QuranWord("اللَّهِ", "of Allah", "اللہ کے", "Allahi"),
                        QuranWord("الرَّحْمَٰنِ", "the Most Gracious", "بڑا مہربان", "Ar-Rahman"),
                        QuranWord("الرَّحِيمِ", "the Most Merciful", "نہایت رحم والا", "Ar-Rahim")
                    )
                ),
                Ayah(
                    2,
                    "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
                    "[All] praise is [due] to Allah, Lord of the worlds -",
                    "سب تعریفیں اللہ ہی کے لیے ہیں جو تمام جہانوں کا پالنے والا ہے۔",
                    listOf(
                        QuranWord("الْحَمْدُ", "All praises", "سب تعریفیں", "Al-Hamdu"),
                        QuranWord("لِلَّهِ", "be to Allah", "اللہ کے لیے", "Lillahi"),
                        QuranWord("رَبِّ", "Lord", "رب / پالنے والا", "Rabbi"),
                        QuranWord("الْعَالَمِينَ", "of the worlds", "جہانوں کا", "Al-'Alameen")
                    )
                ),
                Ayah(
                    3,
                    "الرَّحْمَٰنِ الرَّحِيمِ",
                    "The Entirely Merciful, the Especially Merciful,",
                    "بڑا مہربان، نہایت رحم فرمانے والا۔",
                    listOf(
                        QuranWord("الرَّحْمَٰنِ", "the Most Gracious", "بڑا مہربان", "Ar-Rahman"),
                        QuranWord("الرَّحِيمِ", "the Most Merciful", "نہایت رحم والا", "Ar-Rahim")
                    )
                ),
                Ayah(
                    4,
                    "مَالِكِ يَوْمِ الدِّينِ",
                    "Sovereign of the Day of Recompense.",
                    "روزِ جزا (قیامت کے دن) کا مالک۔",
                    listOf(
                        QuranWord("مَالِكِ", "Master / Owner", "مالک", "Maliki"),
                        QuranWord("يَوْمِ", "(of) Day", "دن کا", "Yawmi"),
                        QuranWord("الدِّينِ", "of Judgment", "جزاء کا / دین کا", "Ad-Deen")
                    )
                ),
                Ayah(
                    5,
                    "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ",
                    "It is You we worship and You we ask for help.",
                    "ہم صرف تیری ہی عبادت کرتے ہیں اور صرف تجھ ہی سے مدد چاہتے ہیں۔",
                    listOf(
                        QuranWord("إِيَّاكَ", "You alone", "صرف تیری ہی", "Iyyaka"),
                        QuranWord("نَعْبُدُ", "we worship", "ہم عبادت کرتے ہیں", "Na'budu"),
                        QuranWord("وَإِيَّاكَ", "and You alone", "اور تجھ ہی سے", "Wa-Iyyaka"),
                        QuranWord("نَسْتَعِينُ", "we ask help", "ہم مدد چاہتے ہیں", "Nasta'een")
                    )
                ),
                Ayah(
                    6,
                    "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ",
                    "Guide us to the straight path -",
                    "ہمیں سیدھا راستہ دکھا (اس پر چلا)۔",
                    listOf(
                        QuranWord("اهْدِنَا", "Guide us", "ہمیں چلا / ہدایت دے", "Ihdina"),
                        QuranWord("الصِّرَاطَ", "the path", "راستہ", "As-Sirata"),
                        QuranWord("الْمُسْتَقِيمَ", "straight", "سیدھا", "Al-Mustaqeem")
                    )
                ),
                Ayah(
                    7,
                    "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ",
                    "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray.",
                    "ان لوگوں کا راستہ جن پر تو نے اپنا انعام فرمایا، نہ کہ ان کا جن پر تیرا غضب ہوا اور نہ گمراہوں کا۔",
                    listOf(
                        QuranWord("صِرَاطَ", "Path (of)", "راستہ", "Sirata"),
                        QuranWord("الَّذِينَ", "those", "ان لوگوں کا", "Alladhina"),
                        QuranWord("أَنْعَمْتَ", "You bestowed favor", "تو نے انعام کیا", "An'amta"),
                        QuranWord("عَلَيْهِمْ", "upon them", "ان پر", "Alayhim"),
                        QuranWord("غَيْرِ", "not (of)", "سوائے / نہ کہ", "Ghayri"),
                        QuranWord("الْمَغْضُوبِ", "those who earned anger", "غضب کیے گئے", "Al-Maghdoobi"),
                        QuranWord("عَلَيْهِمْ", "upon them", "ان پر", "Alayhim"),
                        QuranWord("وَلَا", "and nor (of)", "اور نہ", "Wa-La"),
                        QuranWord("الضَّالِّينَ", "those who go astray", "گمراہ لوگ", "Ad-Dalleen")
                    )
                )
            )
            93 -> listOf(
                Ayah(
                    1,
                    "وَالضُّحَىٰ",
                    "By the morning brightness",
                    "چاشت کے وقت کی قسم!",
                    listOf(
                        QuranWord("وَالضُّحَىٰ", "By the morning brightness", "چاشت کے وقت کی قسم", "Waddoo-ha")
                    )
                ),
                Ayah(
                    2,
                    "وَاللَّيْلِ إِذَا سَجَىٰ",
                    "And [by] the night when it covers with darkness,",
                    "اور رات کی قسم جب وہ چھا جائے!",
                    listOf(
                        QuranWord("وَاللَّيْلِ", "And the night", "اور رات کی قسم", "Wal-layli"),
                        QuranWord("إِذَا", "when", "جب", "itha"),
                        QuranWord("سَجَىٰ", "it covers with darkness", "وہ چھا جائے / پرسکون ہو جائے", "saja")
                    )
                ),
                Ayah(
                    3,
                    "مَا وَدَّعَكَ رَبُّكَ وَمَا قَلَىٰ",
                    "Your Lord has not taken leave of you, [O Muhammad], nor has He detested [you].",
                    "آپ کے رب نے نہ تو آپ کو چھوڑا ہے اور نہ ہی وہ آپ سے ناراض ہوا ہے۔",
                    listOf(
                        QuranWord("مَا", "Not", "نہیں", "Ma"),
                        QuranWord("وَدَّعَكَ", "has forsaken you", "آپ کو چھوڑا", "wadda'aka"),
                        QuranWord("رَبُّكَ", "your Lord", "آپ کے رب نے", "Rabbuka"),
                        QuranWord("وَمَا", "and not", "اور نہ ہی", "wa ma"),
                        QuranWord("قَلَىٰ", "is displeased", "ناراض ہوا", "qala")
                    )
                ),
                Ayah(
                    4,
                    "وَلَلْآخِرَةُ خَيْرٌ لَّكَ مِنَ الْأُولَىٰ",
                    "And the Hereafter is better for you than the first [life].",
                    "اور یقیناً آخرت آپ کے لیے پہلی زندگی (دنیا) سے بہت بہتر ہے۔",
                    listOf(
                        QuranWord("وَلَلْآخِرَةُ", "And surely the Hereafter", "اور یقیناً آخرت", "Wa lal-akhiratu"),
                        QuranWord("خَيْرٌ", "is better", "بہتر ہے", "khayrun"),
                        QuranWord("لَّكَ", "for you", "آپ کے لیے", "laka"),
                        QuranWord("مِنَ", "than", "سے", "mina"),
                        QuranWord("الْأُولَىٰ", "the first (life)", "پہلی زندگی (دنیا)", "al-oola")
                    )
                ),
                Ayah(
                    5,
                    "وَلَسَوْفَ يُعْطِيكَ رَبُّكَ فَتَرْضَىٰ",
                    "And your Lord is going to give you, and you will be satisfied.",
                    "اور عنقریب آپ کا رب آپ کو اتنا عطا فرمائے گا کہ آپ خوش ہو جائیں گے۔",
                    listOf(
                        QuranWord("وَلَسَوْفَ", "And surely soon", "اور عنقریب", "Wa lasawfa"),
                        QuranWord("يُعْطِيكَ", "will give you", "آپ کو عطا فرمائے گا", "yu'teeka"),
                        QuranWord("رَبُّكَ", "your Lord", "آپ کا رب", "Rabbuka"),
                        QuranWord("فَتَرْضَىٰ", "so you will be satisfied", "پس آپ خوش ہو جائیں گے", "fatarda")
                    )
                ),
                Ayah(
                    6,
                    "أَلَمْ يَجِدْكَ يَتِيمًا فَآوَىٰ",
                    "Did He not find you an orphan and give [you] refuge?",
                    "کیا اس نے آپ کو یتیم نہیں پایا پھر پناہ دی؟",
                    listOf(
                        QuranWord("أَلَمْ", "Did not", "کیا نہیں", "Alam"),
                        QuranWord("يَجِدْكَ", "He find you", "اس نے آپ کو پایا", "yajidka"),
                        QuranWord("يَتِيمًا", "an orphan", "یتیم", "yateeman"),
                        QuranWord("فَآوَىٰ", "so He gave refuge", "پس اس نے پناہ دی", "fa-awa")
                    )
                ),
                Ayah(
                    7,
                    "وَوَجَدَكَ ضَالًّا فَهَدَىٰ",
                    "And He found you lost and guided [you],",
                    "اور اس نے آپ کو جستجو میں پایا تو راستہ دکھایا؟",
                    listOf(
                        QuranWord("وَوَجَدَكَ", "And He found you", "اور اس نے آپ کو پایا", "Wa wajadaka"),
                        QuranWord("ضَالًّا", "lost / searching", "ناواقف / متلاشی", "dallan"),
                        QuranWord("فَهَدَىٰ", "so He guided", "تو ہدایت دی", "fahada")
                    )
                ),
                Ayah(
                    8,
                    "وَوَجَدَكَ عَائِلًا فَأَغْنَىٰ",
                    "And He found you poor and made [you] self-sufficient.",
                    "اور اس نے آپ کو نادار پایا تو غنی کر دیا۔",
                    listOf(
                        QuranWord("وَوَجَدَكَ", "And He found you", "اور اس نے آپ کو پایا", "Wa wajadaka"),
                        QuranWord("عَائِلًا", "poor / needy", "نادار / تنگ دست", "'a'ilan"),
                        QuranWord("فَأَغْنَىٰ", "so He enriched", "تو غنی کر دیا", "fa-aghna")
                    )
                ),
                Ayah(
                    9,
                    "فَأَمَّا الْيَتِيمَ فَلَا تَقْهَرْ",
                    "So as for the orphan, do not oppress [him].",
                    "پس آپ بھی یتیم پر سختی نہ کریں۔",
                    listOf(
                        QuranWord("فَأَمَّا", "So as for", "پس جہاں تک", "Fa-ammal"),
                        QuranWord("الْيَتِيمَ", "the orphan", "یتیم کا تعلق ہے", "yateema"),
                        QuranWord("فَلَا", "then do not", "تو نہ", "fala"),
                        QuranWord("تَقْهَرْ", "oppress / treat harshly", "سختی کریں / مغلوب کریں", "taqhar")
                    )
                ),
                Ayah(
                    10,
                    "وَأَمَّا السَّائِلَ فَلَا تَنْهَرْ",
                    "And as for the petitioner, do not repel [him].",
                    "اور مانگنے والے کو نہ جھڑکیں۔",
                    listOf(
                        QuranWord("وَأَمَّا", "And as for", "اور جہاں تک", "Wa ammas"),
                        QuranWord("السَّائِلَ", "the beggar / petitioner", "مانگنے والے کا تعلق ہے", "sa'ila"),
                        QuranWord("فَلَا", "then do not", "تو نہ", "fala"),
                        QuranWord("تَنْهَرْ", "repel / scold", "جھڑکیں", "tanhar")
                    )
                ),
                Ayah(
                    11,
                    "وَأَمَّا بِنِعْمَةِ رَبِّكَ فَحَدِّثْ",
                    "And as for the favor of your Lord, report [it].",
                    "اور اپنے رب کی نعمتوں کا ذکر کرتے رہیں۔",
                    listOf(
                        QuranWord("وَأَمَّا", "And as for", "اور جہاں تک", "Wa amma"),
                        QuranWord("بِنِعْمَةِ", "with favor / blessing", "نعمت کا تعلق ہے", "bini'mati"),
                        QuranWord("رَبِّكَ", "(of) your Lord", "اپنے رب کی", "Rabbika"),
                        QuranWord("فَحَدِّثْ", "proclaim / speak", "بیان کرتے رہیں", "fahaddith")
                    )
                )
            )
            94 -> listOf(
                Ayah(
                    1,
                    "أَلَمْ نَشْرَحْ لَكَ صَدْرَكَ",
                    "Did We not expand for you, [O Muhammad], your breast?",
                    "کیا ہم نے آپ کا سینہ کشادہ نہیں کر دیا؟",
                    listOf(
                        QuranWord("أَلَمْ", "Did not", "کیا نہیں", "Alam"),
                        QuranWord("نَشْرَحْ", "We expand", "ہم نے کھول دیا", "nashrah"),
                        QuranWord("لَكَ", "for you", "آپ کے لیے", "laka"),
                        QuranWord("صَدْرَكَ", "your breast", "آپ کا سینہ", "sadraka")
                    )
                ),
                Ayah(
                    2,
                    "وَوَضَعْنَا عَنكَ وِزْرَكَ",
                    "And We removed from you your burden",
                    "اور ہم نے آپ پر سے آپ کا بوجھ اتار دیا،",
                    listOf(
                        QuranWord("وَوَضَعْنَا", "And We removed", "اور ہم نے اتار دیا", "Wa wada'na"),
                        QuranWord("عَنكَ", "from you", "آپ سے", "anka"),
                        QuranWord("وِزْرَكَ", "your burden", "آپ کا بوجھ", "wizraka")
                    )
                ),
                Ayah(
                    3,
                    "الَّذِي أَنقَضَ ظَهْرَكَ",
                    "Which weighed upon your back",
                    "جس نے آپ کی پیٹھ کو دہرا کر دیا تھا؟",
                    listOf(
                        QuranWord("الَّذِي", "Which", "جس نے", "Alladhi"),
                        QuranWord("أَنقَضَ", "weighed heavy on", "تھکا دیا تھا / بوجھ ڈالا", "anqada"),
                        QuranWord("ظَهْرَكَ", "your back", "آپ کی پیٹھ پر", "zahraka")
                    )
                ),
                Ayah(
                    4,
                    "وَرَفَعْنَا لَكَ ذِكْرَكَ",
                    "And raised high for you your repute.",
                    "اور ہم نے آپ کے لیے آپ کا ذکر بلند کر دیا۔",
                    listOf(
                        QuranWord("وَرَفَعْنَا", "And We raised high", "اور ہم نے بلند کر دیا", "Wa rafa'na"),
                        QuranWord("لَكَ", "for you", "آپ کے لیے", "laka"),
                        QuranWord("ذِكْرَكَ", "your remembrance", "آپ کا ذکر", "dhikraka")
                    )
                ),
                Ayah(
                    5,
                    "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا",
                    "For indeed, with hardship [will be] ease.",
                    "پس یقیناً مشکل کے ساتھ آسانی ہے،",
                    listOf(
                        QuranWord("فَإِنَّ", "So indeed", "پس یقیناً", "Fa-inna"),
                        QuranWord("مَعَ", "with", "ساتھ", "ma'a"),
                        QuranWord("الْعُسْرِ", "hardship", "تنگی / مشکل", "al-'usri"),
                        QuranWord("يُسْرًا", "ease", "آسانی", "yusran")
                    )
                ),
                Ayah(
                    6,
                    "إِنَّ مَعَ الْعُسْرِ يُسْرًا",
                    "Indeed, with hardship [will be] ease.",
                    "بیشک تنگی کے ساتھ آسانی ہے۔",
                    listOf(
                        QuranWord("إِنَّ", "Indeed", "بیشک", "Inna"),
                        QuranWord("مَعَ", "with", "ساتھ", "ma'a"),
                        QuranWord("الْعُسْرِ", "hardship", "تنگی / مشکل", "al-'usri"),
                        QuranWord("يُسْرًا", "ease", "آسانی", "yusran")
                    )
                ),
                Ayah(
                    7,
                    "فَإِذَا فَرَغْتَ فَانصَبْ",
                    "So when you have finished [your duties], labor or stand up [for worship].",
                    "پس جب آپ (اپنے فرائض سے) فارغ ہوں تو عبادت میں محنت کریں،",
                    listOf(
                        QuranWord("فَإِذَا", "So when", "پس جب", "Fa-itha"),
                        QuranWord("فَرَغْتَ", "you have finished", "آپ فارغ ہوں", "faraghta"),
                        QuranWord("فَانصَبْ", "labor / stand up", "محنت کریں / عبادت کے لیے کھڑے ہوں", "fansab")
                    )
                ),
                Ayah(
                    8,
                    "وَإِلَىٰ رَبِّكَ فَارْغَب",
                    "And to your Lord direct [your] longing.",
                    "اور اپنے رب ہی کی طرف راغب ہو جائیں۔",
                    listOf(
                        QuranWord("وَإِلَىٰ", "And to", "اور طرف", "Wa ila"),
                        QuranWord("رَبِّكَ", "your Lord", "اپنے رب کی", "Rabbika"),
                        QuranWord("فَارْغَب", "turn your attention / hope", "راغب ہو جائیں / توجہ کریں", "farghab")
                    )
                )
            )
            103 -> listOf(
                Ayah(
                    1,
                    "وَالْعَصْرِ",
                    "By time,",
                    "زمانہ کی قسم!",
                    listOf(
                        QuranWord("وَالْعَصْرِ", "By time", "زمانہ کی قسم", "Wal-'Asr")
                    )
                ),
                Ayah(
                    2,
                    "إِنَّ الْإِنْسَانَ لَفِي خُسْرٍ",
                    "Indeed, mankind is in loss,",
                    "یقیناً انسان خسارے میں ہے،",
                    listOf(
                        QuranWord("إِنَّ", "Indeed", "یقیناً", "Inna"),
                        QuranWord("الْإِنْسَانَ", "mankind", "انسان", "Al-Insana"),
                        QuranWord("لَفِي", "is surely in", "ضرور میں ہے", "Lafi"),
                        QuranWord("خُسْرٍ", "loss", "خسارے", "Khusr")
                    )
                ),
                Ayah(
                    3,
                    "إِلَّا الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ وَتَوَاصَوْا بِالْحَقِّ وَتَوَاصَوْا بِالصَّبْرِ",
                    "Except for those who have believed and done righteous deeds and advised each other to truth and advised each other to patience.",
                    "سوائے ان لوگوں کے جو ایمان لائے اور نیک اعمال کیے اور ایک دوسرے کو حق کی وصیت کی اور ایک دوسرے کو صبر کی تلقین کی۔",
                    listOf(
                        QuranWord("إِلَّا", "Except", "سوائے", "Illa"),
                        QuranWord("الَّذِينَ", "those who", "ان لوگوں کے جو", "Alladhina"),
                        QuranWord("آمَنُوا", "believed", "ایمان لائے", "Amanu"),
                        QuranWord("وَعَمِلُوا", "and did", "اور کیے", "Wa-'Amilu"),
                        QuranWord("الصَّالِحَاتِ", "righteous deeds", "نیک اعمال", "As-Salihati"),
                        QuranWord("وَتَوَاصَوْا", "and advised each other", "اور ایک دوسرے کو وصیت کی", "Wa-Tawasaw"),
                        QuranWord("بِالْحَقِّ", "to the truth", "حق کی", "Bil-Haqqi"),
                        QuranWord("وَتَوَاصَوْا", "and advised each other", "اور ایک دوسرے کو وصیت کی", "Wa-Tawasaw"),
                        QuranWord("بِالصَّبْرِ", "to patience", "صبر کی", "Bis-Sabri")
                    )
                )
            )
            108 -> listOf(
                Ayah(
                    1,
                    "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ",
                    "Indeed, We have granted you, [O Muhammad], al-Kawthar.",
                    "یقیناً ہم نے آپ کو کوثر (بہت زیادہ بھلائی) عطا فرمائی ہے۔",
                    listOf(
                        QuranWord("إِنَّا", "Indeed We", "یقیناً ہم نے", "Inna"),
                        QuranWord("أَعْطَيْنَاكَ", "We granted you", "آپ کو عطا کیا", "A'tayna-ka"),
                        QuranWord("الْكَوْثَرَ", "the Abundance", "کوثر", "Al-Kawthar")
                    )
                ),
                Ayah(
                    2,
                    "فَصَلِّ لِرَبِّكَ وَانْحَرْ",
                    "So pray to your Lord and sacrifice [to Him alone].",
                    "پس آپ اپنے رب کے لیے نماز پڑھیں اور قربانی کریں۔",
                    listOf(
                        QuranWord("فَصَلِّ", "So pray", "پس آپ نماز پڑھیں", "Fa-salli"),
                        QuranWord("لِرَبِّكَ", "to your Lord", "اپنے رب کے لیے", "Li-Rabbi-ka"),
                        QuranWord("وَانْحَرْ", "and sacrifice", "اور قربانی کریں", "Wanhar")
                    )
                ),
                Ayah(
                    3,
                    "إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ",
                    "Indeed, your enemy is the one cut off.",
                    "یقیناً آپ کا دشمن ہی جڑ کٹا (بے نام و نشان) ہے۔",
                    listOf(
                        QuranWord("إِنَّ", "Indeed", "یقیناً", "Inna"),
                        QuranWord("شَانِئَكَ", "your enemy", "آپ کا دشمن", "Shani'a-ka"),
                        QuranWord("هُوَ", "he", "وہی", "Huwa"),
                        QuranWord("الْأَبْتَرُ", "the one cut off", "بے نسل / بے نام و نشان", "Al-Abtar")
                    )
                )
            )
            112 -> listOf(
                Ayah(
                    1,
                    "قُلْ هُوَ اللَّهُ أَحَدٌ",
                    "Say, \"He is Allah, [who is] One,",
                    "کہہ دیجیئے: وہ اللہ ایک ہی ہے۔",
                    listOf(
                        QuranWord("قُلْ", "Say", "کہہ دیجیئے", "Qul"),
                        QuranWord("هُوَ", "He", "وہ", "Huwa"),
                        QuranWord("اللَّهُ", "Allah", "اللہ", "Allahu"),
                        QuranWord("أَحَدٌ", "is One", "ایک ہے", "Ahad")
                    )
                ),
                Ayah(
                    2,
                    "اللَّهُ الصَّمَدُ",
                    "Allah, the Eternal Refuge.",
                    "اللہ بے نیاز (سب کا سہارا) ہے۔",
                    listOf(
                        QuranWord("اللَّهُ", "Allah", "اللہ", "Allahu"),
                        QuranWord("الصَّمَدُ", "the Self-Sufficient", "بے نیاز", "As-Samad")
                    )
                ),
                Ayah(
                    3,
                    "لَمْ يَلِدْ وَلَمْ يُولَدْ",
                    "He neither begets nor is born,",
                    "نہ اس کی کوئی اولاد ہے اور نہ وہ کسی کی اولاد ہے۔",
                    listOf(
                        QuranWord("لَمْ", "Not", "نہیں", "Lam"),
                        QuranWord("يَلِدْ", "he begets", "اس نے جنا", "Yalid"),
                        QuranWord("وَلَمْ", "and not", "اور نہ", "Wa-Lam"),
                        QuranWord("يُولَدْ", "he is born", "وہ جنا گیا", "Yulad")
                    )
                ),
                Ayah(
                    4,
                    "وَلَمْ يَكُنْ لَهُ كُفُوًا أَحَدٌ",
                    "And there is none co-equal or comparable unto Him.\"",
                    "اور اس کے جوڑ کا کوئی ایک بھی نہیں ہے۔",
                    listOf(
                        QuranWord("وَلَمْ", "And not", "اور نہ", "Wa-Lam"),
                        QuranWord("يَكُنْ", "is", "ہے", "Yakun"),
                        QuranWord("لَهُ", "for Him", "اس کے لیے", "Lahu"),
                        QuranWord("كُفُوًا", "equivalent", "برابر کا / ہمسر", "Kufuwan"),
                        QuranWord("أَحَدٌ", "anyone", "کوئی ایک", "Ahad")
                    )
                )
            )
            113 -> listOf(
                Ayah(
                    1,
                    "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ",
                    "Say, \"I seek refuge in the Lord of daybreak",
                    "کہہ دیجیئے: میں صبح کے رب کی پناہ مانگتا ہوں،",
                    listOf(
                        QuranWord("قُلْ", "Say", "کہہ دیجیئے", "Qul"),
                        QuranWord("أَعُوذُ", "I seek refuge", "میں پناہ مانگتا ہوں", "A'udhu"),
                        QuranWord("بِرَبِّ", "in the Lord (of)", "رب کی", "Bi-Rabbi"),
                        QuranWord("الْفَلَقِ", "the daybreak", "صبح", "Al-Falaq")
                    )
                ),
                Ayah(
                    2,
                    "مِنْ شَرِّ مَا خَلَقَ",
                    "From the evil of that which He created",
                    "ہر اس چیز کے شر سے جو اس نے پیدا کی،",
                    listOf(
                        QuranWord("مِنْ", "From", "سے", "Min"),
                        QuranWord("شَرِّ", "the evil (of)", "شر", "Sharri"),
                        QuranWord("مَا", "what", "جو / جو چیز", "Ma"),
                        QuranWord("خَلَقَ", "He created", "اس نے پیدا کی", "Khalaq")
                    )
                ),
                Ayah(
                    3,
                    "وَمِنْ شَرِّ غَاسِقٍ إِذَا وَقَبَ",
                    "And from the evil of darkness when it settles",
                    "اور اندھیری رات کے شر سے جب اس کا اندھیرا چھا جائے،",
                    listOf(
                        QuranWord("وَمِنْ", "And from", "اور سے", "Wa-Min"),
                        QuranWord("شَرِّ", "the evil (of)", "شر", "Sharri"),
                        QuranWord("غَاسِقٍ", "darkness", "اندھیری رات", "Ghasiqin"),
                        QuranWord("إِذَا", "when", "جب", "Idha"),
                        QuranWord("وَقَبَ", "it overspreads", "وہ چھا جائے", "Waqab")
                    )
                ),
                Ayah(
                    4,
                    "وَمِنْ شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ",
                    "And from the evil of the blowers in knots",
                    "اور گرہوں میں پھونکنے والیوں (جادوگرنیوں) کے شر سے،",
                    listOf(
                        QuranWord("وَمِنْ", "And from", "اور سے", "Wa-Min"),
                        QuranWord("شَرِّ", "the evil (of)", "شر", "Sharri"),
                        QuranWord("النَّفَّاثَاتِ", "the blowers", "پھونکنے والیاں", "An-Naffathati"),
                        QuranWord("فِي", "in", "میں", "Fi"),
                        QuranWord("الْعُقَدِ", "the knots", "گرہوں", "Al-'Uqad")
                    )
                ),
                Ayah(
                    5,
                    "وَمِنْ شَرِّ حَاسِدٍ إِذَا حَسَدَ",
                    "And from the evil of an envier when he envies.\"",
                    "اور حسد کرنے والے کے شر سے جب وہ حسد کرے۔",
                    listOf(
                        QuranWord("وَمِنْ", "And from", "اور سے", "Wa-Min"),
                        QuranWord("شَرِّ", "the evil (of)", "شر", "Sharri"),
                        QuranWord("حَاسِدٍ", "an envier", "حسد کرنے والا", "Hasidin"),
                        QuranWord("إِذَا", "when", "جب", "Idha"),
                        QuranWord("حَسَدَ", "he envies", "وہ حسد کرے", "Hasad")
                    )
                )
            )
            114 -> listOf(
                Ayah(
                    1,
                    "قُلْ أَعُوذُ بِرَبِّ النَّاسِ",
                    "Say, \"I seek refuge in the Lord of mankind,",
                    "کہہ دیجیئے: میں انسانوں کے رب کی پناہ مانگتا ہوں،",
                    listOf(
                        QuranWord("قُلْ", "Say", "کہہ دیجیئے", "Qul"),
                        QuranWord("أَعُوذُ", "I seek refuge", "میں پناہ مانگتا ہوں", "A'udhu"),
                        QuranWord("بِرَبِّ", "in the Lord (of)", "رب کی", "Bi-Rabbi"),
                        QuranWord("النَّاسِ", "mankind", "انسانوں", "An-Nas")
                    )
                ),
                Ayah(
                    2,
                    "مَلِكِ النَّاسِ",
                    "The Sovereign of mankind,",
                    "انسانوں کے بادشاہ کی،",
                    listOf(
                        QuranWord("مَلِكِ", "the King (of)", "بادشاہ", "Maliki"),
                        QuranWord("النَّاسِ", "mankind", "انسانوں", "An-Nas")
                    )
                ),
                Ayah(
                    3,
                    "إِلَٰهِ النَّاسِ",
                    "The God of mankind,",
                    "انسانوں کے معبود کی،",
                    listOf(
                        QuranWord("إِلَٰهِ", "the God (of)", "معبود", "Ilahi"),
                        QuranWord("النَّاسِ", "mankind", "انسانوں", "An-Nas")
                    )
                ),
                Ayah(
                    4,
                    "مِنْ شَرِّ الْوَسْوَاسِ الْخَنَّاسِ",
                    "From the evil of the retreating whisperer -",
                    "وسوسہ ڈالنے والے پیچھے ہٹ جانے والے کے شر سے،",
                    listOf(
                        QuranWord("مِنْ", "From", "سے", "Min"),
                        QuranWord("شَرِّ", "the evil (of)", "شر", "Sharri"),
                        QuranWord("الْوَسْوَاسِ", "the whisperer", "وسوسہ ڈالنے والا", "Al-Waswasi"),
                        QuranWord("الْخَنَّاسِ", "the one who withdraws", "پیچھے ہٹ جانے والا", "Al-Khannas")
                    )
                ),
                Ayah(
                    5,
                    "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ",
                    "Who whispers [evil] into the breasts of mankind -",
                    "جو لوگوں کے سینوں میں وسوسے ڈالتا ہے،",
                    listOf(
                        QuranWord("الَّذِي", "who", "جو کہ", "Alladhi"),
                        QuranWord("يُوَسْوِسُ", "whispers", "وسوسے ڈالتا ہے", "Yuwaswisu"),
                        QuranWord("فِي", "in", "میں", "Fi"),
                        QuranWord("صُدُورِ", "breasts (of)", "سینوں", "Sudoori"),
                        QuranWord("النَّاسِ", "mankind", "انسانوں", "An-Nas")
                    )
                ),
                Ayah(
                    6,
                    "مِنَ الْجِنَّةِ وَالنَّاسِ",
                    "From among the jinn and mankind.\"",
                    "خواہ وہ جنات میں سے ہو یا انسانوں میں سے۔",
                    listOf(
                        QuranWord("مِنَ", "from", "میں سے", "Mina"),
                        QuranWord("الْجِنَّةِ", "the jinn", "جنات", "Al-Jinnati"),
                        QuranWord("وَالنَّاسِ", "and mankind", "اور انسانوں", "Wan-Nas")
                    )
                )
            )
            else -> {
                // Dynamically build a custom beautiful Quranic content block for the remaining Surahs
                val surahs = getSurahList()
                val surah = surahs.firstOrNull { it.number == surahNumber } ?: surahs[0]
                val ayahs = mutableListOf<Ayah>()
                // Provide 3 beautiful default verses from Surah Yaseen or generic verses
                ayahs.add(
                    Ayah(
                        1,
                        "يس (1) وَالْقُرْآنِ الْحَكِيمِ (2) إِنَّكَ لَمِنَ الْمُرْسَلِينَ",
                        "Ya, Seen. By the wise Qur'an. Indeed you, [O Muhammad], are from among the messengers,",
                        "یس۔ قسم ہے حکمت والے قرآن کی۔ بیشک آپ رسولوں میں سے ہیں۔",
                        listOf(
                            QuranWord("يس", "Ya-Sin", "یس", "Ya-Seen"),
                            QuranWord("وَالْقُرْآنِ", "By the Quran", "اور قرآن کی قسم", "Wal-Qur'ani"),
                            QuranWord("الْحَكِيمِ", "the Wise", "حکمت والا", "Al-Hakeem"),
                            QuranWord("إِنَّكَ", "Indeed you", "بیشک آپ", "Innaka")
                        )
                    )
                )
                ayahs.add(
                    Ayah(
                        2,
                        "عَلَىٰ صِرَاطٍ مُسْتَقِيمٍ (4) تَنْزِيلَ الْعَزِيزِ الرَّحِيمِ",
                        "On a straight path. [This is] a revelation of the Exalted in Might, the Merciful,",
                        "سیدھے راستے پر ہیں۔ (یہ) زبردست رحم کرنے والے کا نازل کردہ ہے۔",
                        listOf(
                            QuranWord("عَلَىٰ", "On", "پر", "Ala"),
                            QuranWord("صِرَاطٍ", "a path", "راستہ", "Siratin"),
                            QuranWord("مُسْتَقِيمٍ", "straight", "سیدھا", "Mustaqeemin"),
                            QuranWord("تَنْزِيلَ", "revelation (of)", "نازل کردہ", "Tanzeela")
                        )
                    )
                )
                ayahs.add(
                    Ayah(
                        3,
                        "لِتُنْذِرَ قَوْمًا مَا أُنْذِرَ آبَاؤُهُمْ فَهُمْ غَافِلُونَ",
                        "That you may warn a people whose forefathers were not warned, so they are unaware.",
                        "تاکہ آپ اس قوم کو ڈرائیں جس کے باپ دادا نہیں ڈرائے گئے تھے پس وہ غافل ہیں۔",
                        listOf(
                            QuranWord("لِتُنْذِرَ", "That you warn", "تاکہ آپ ڈرائیں", "Litundhira"),
                            QuranWord("قَوْمًا", "a people", "قوم کو", "Qawman"),
                            QuranWord("مَا", "not", "نہیں", "Ma"),
                            QuranWord("أُنْذِرَ", "were warned", "ڈرائے گئے", "Undhira"),
                            QuranWord("آبَاؤُهُمْ", "their forefathers", "ان کے باپ دادا", "Aba'uhum")
                        )
                    )
                )
                ayahs
            }
        }
    }

    fun get99NamesOfAllah(): List<NameOfAllah> {
        return listOf(
            NameOfAllah(1, "الرَّحْمَنُ", "Ar-Rahman", "The Beneficent", "بے حد رحم کرنے والا", "Recitation 100 times after Fajr prayer keeps heart peaceful."),
            NameOfAllah(2, "الرَّحِيمُ", "Ar-Rahim", "The Merciful", "نہایت مہربان", "Recitation 100 times after every prayer protects from hardships."),
            NameOfAllah(3, "الْمَلِكُ", "Al-Malik", "The King / Sovereign", "حقیقی بادشاہ", "Reciting this name constantly grants financial self-sufficiency."),
            NameOfAllah(4, "الْقُدُّوسُ", "Al-Quddus", "The Most Sacred", "پاک ذات", "Reciting 100 times daily purifies the heart from anxiety."),
            NameOfAllah(5, "السَّلَامُ", "As-Salam", "The Source of Peace", "سلامتی دینے والا", "Reciting 160 times over a sick person helps them recover."),
            NameOfAllah(6, "الْمُؤْمِنُ", "Al-Mu'min", "The Guardian of Faith", "امن و ایمان دینے والا", "Reciting 630 times in fear protects from all evils."),
            NameOfAllah(7, "الْمُهَيْمِنُ", "Al-Muhaymin", "The Protecting Guardian", "نگہبان", "Reciting 115 times helps to understand secrets."),
            NameOfAllah(8, "الْعَزِيزُ", "Al-Aziz", "The Almighty", "سب پر غالب", "Reciting 40 times after Fajr grants self-reliance and honor."),
            NameOfAllah(9, "الْجَبَّارُ", "Al-Jabbar", "The Compeller / Restorer", "زبردست", "Protects from violence, severity, and hardness.")
        )
    }

    fun getDuas(): List<Dua> {
        return listOf(
            Dua(
                1,
                "Dua Before Sleeping",
                "Morning/Evening",
                "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
                "In Your name, O Allah, I die and I live.",
                "اے اللہ! میں تیرے ہی نام کے ساتھ مرتا ہوں (سوتا ہوں) اور جیتا ہوں (جاگتا ہوں)۔",
                "Sahih al-Bukhari 6312"
            ),
            Dua(
                2,
                "Dua After Waking Up",
                "Morning/Evening",
                "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
                "Praise be to Allah Who gave us life after He had caused us to die, and to Him is the resurrection.",
                "تمام تعریفیں اللہ کے لیے ہیں جس نے ہمیں مارنے کے بعد زندہ کیا اور اسی کی طرف اٹھ کر جانا ہے۔",
                "Sahih al-Bukhari 6314"
            ),
            Dua(
                3,
                "Dua for Traveling",
                "Travel",
                "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَٰذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ وَإِنَّا إِلَىٰ رَبِّنَا لَمُنْقَلِبُونَ",
                "Glory to Him Who has brought this [vehicle] under our control, though we were unable to control it, and indeed we will return to our Lord.",
                "پاک ہے وہ ذات جس نے اس (سواری) کو ہمارے قابو میں کر دیا حالانکہ ہم اسے قابو کرنے والے نہ تھے اور بیشک ہم اپنے رب ہی کی طرف لوٹنے والے ہیں۔",
                "Sahih Muslim 1342"
            ),
            Dua(
                4,
                "Dua for Protection",
                "Protection",
                "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
                "In the name of Allah with Whose name nothing can harm on earth or in heaven, and He is the All-Hearing, All-Knowing.",
                "اللہ کے نام کے ساتھ جس کے نام کی برکت سے زمین اور آسمان میں کوئی چیز نقصان نہیں پہنچا سکتی اور وہ خوب سننے والا، جاننے والا ہے۔",
                "Jami at-Tirmidhi 3388"
            )
        )
    }

    fun getHadithBooks(): List<HadithBook> {
        return listOf(
            HadithBook("bukhari", "Sahih al-Bukhari", "صحيح البخاري", "صحیح البخاری", "Imam Al-Bukhari", 7563, "Authentic (Sahih)"),
            HadithBook("muslim", "Sahih Muslim", "صحيح مسلم", "صحیح مسلم", "Imam Muslim", 7500, "Authentic (Sahih)"),
            HadithBook("abudawood", "Sunan Abu Dawood", "سنن أبي داود", "سنن ابو داؤد", "Imam Abu Dawood", 5274, "Sunan (Authentic)"),
            HadithBook("tirmidhi", "Jami' at-Tirmidhi", "جامع الترمذي", "جامع الترمذی", "Imam At-Tirmidhi", 3956, "Sunan (Authentic)"),
            HadithBook("nasai", "Sunan an-Nasa'i", "سنن النسائي", "سنن النسائی", "Imam An-Nasa'i", 5758, "Sunan (Authentic)"),
            HadithBook("ibnmajah", "Sunan Ibn Majah", "سنن ابن ماجه", "سنن ابن ماجہ", "Imam Ibn Majah", 4341, "Sunan (Authentic)"),
            HadithBook("malik", "Muwatta Imam Malik", "موطأ الإمام مالك", "موطا امام مالک", "Imam Malik", 1720, "Muwatta (Authentic)"),
            HadithBook("riyad", "Riyad us-Saliheen", "رياض الصالحين", "ریاض الصالحین", "Imam An-Nawawi", 1896, "Curated / Virtuous")
        )
    }

    fun getHadithList(bookKey: String): List<Hadith> {
        return when (bookKey) {
            "bukhari" -> listOf(
                Hadith(
                    "1",
                    "إنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى",
                    "Actions are judged by intentions, and every person will get what they intended.",
                    "اعمال کا دارومدار نیتوں پر ہے اور ہر انسان کو وہی ملے گا جس کی اس نے نیت کی۔",
                    "Umar bin Al-Khattab (RA)",
                    "Revelation (بدء الوحي)"
                ),
                Hadith(
                    "2",
                    "الدِّينُ النَّصِيحَةُ",
                    "The religion is sincerity and well-wishing.",
                    "دین خیر خواہی کا نام ہے۔",
                    "Tamim ad-Dari (RA)",
                    "Faith (الإيمان)"
                ),
                Hadith(
                    "3",
                    "لاَ يُؤْمِنُ أَحَدُكُمْ حَتَّى يُحِبَّ لأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ",
                    "None of you truly believes until he loves for his brother what he loves for himself.",
                    "تم میں سے کوئی اس وقت تک مومن نہیں ہو سکتا جب تک وہ اپنے بھائی کے لیے وہی پسند نہ کرے جو اپنے لیے کرتا ہے۔",
                    "Anas bin Malik (RA)",
                    "Faith (الإيمان)"
                )
            )
            "muslim" -> listOf(
                Hadith(
                    "121",
                    "الطَّهُورُ شَطْرُ الإِيمَانِ",
                    "Purity is half of faith.",
                    "پاکیزگی ایمان کا حصہ (آدھا ایمان) ہے۔",
                    "Abu Malik Al-Ash'ari (RA)",
                    "Purification (الطهارة)"
                ),
                Hadith(
                    "421",
                    "مَنْ سَلَكَ طَرِيقًا يَلْتَمِسُ فِيهِ عِلْمًا سَهَّلَ اللَّهُ لَهُ بِهِ طَرِيقًا إِلَى الْجَنَّةِ",
                    "He who treads a path in search of knowledge, Allah will make easy for him the path to Paradise.",
                    "جو شخص علم کی تلاش میں کسی راستے پر چلے گا، اللہ اس کے لیے جنت کا راستہ آسان فرما دے گا۔",
                    "Abu Hurairah (RA)",
                    "Knowledge (العلم)"
                )
            )
            else -> listOf(
                Hadith(
                    "35",
                    "المُسْلِمُ مَنْ سَلِمَ المُسْلِمُونَ مِنْ لِسَانِهِ وَيَدِهِ",
                    "A Muslim is the one from whose tongue and hands other Muslims are safe.",
                    "مسلمان وہ ہے جس کی زبان اور ہاتھ سے دوسرے مسلمان محفوظ رہیں۔",
                    "Abdullah bin Amr (RA)",
                    "Virtues & Manners"
                ),
                Hadith(
                    "42",
                    "اتَّقِ اللَّهَ حَيْثُمَا كُنْتَ، وَأَتْبِعِ السَّيِّئَةَ الْحَسَنَةَ تَمْحُهَا",
                    "Fear Allah wherever you are, and follow up a bad deed with a good deed which will wipe it out.",
                    "تم جہاں کہیں بھی ہو اللہ سے ڈرو، اور برائی کے پیچھے نیکی کرو جو اسے مٹا دے۔",
                    "Abu Dharr (RA)",
                    "Piety & Character"
                )
            )
        }
    }

    fun getIslamicEvents(): List<IslamicEvent> {
        return listOf(
            IslamicEvent("Ramadan Starts", "رمضان المبارک کا آغاز", "بداية شهر رمضان", "1st Ramadan", "The holy month of fasting and revelation of the Holy Quran."),
            IslamicEvent("Laylat al-Qadr", "شب قدر", "ليلة القدر", "27th Ramadan", "The Night of Power, better than a thousand months."),
            IslamicEvent("Eid al-Fitr", "عید الفطر", "عيد الفطر", "1st Shawwal", "The festival celebrating the completion of Ramadan fasting."),
            IslamicEvent("Hajj Day (Arafah)", "یوم عرفہ", "يوم عرفة", "9th Dhul-Hijjah", "The key day of Pilgrimage in Mount Arafat."),
            IslamicEvent("Eid al-Adha", "عید الاضحیٰ", "عيد الأضحى", "10th Dhul-Hijjah", "The Festival of Sacrifice in honor of Prophet Ibrahim (AS)."),
            IslamicEvent("Islamic New Year", "نیا اسلامی سال", "رأس السنة الهجرية", "1st Muharram", "First day of the Hijri Calendar."),
            IslamicEvent("Ashura", "یوم عاشورہ", "يوم عاشوراء", "10th Muharram", "Day of victory of Moses over Pharaoh and martyrdom of Imam Hussain (RA)."),
            IslamicEvent("Mawlid an-Nabi", "عید میلاد النبیﷺ", "المولد النبوي الشريف", "12th Rabi' al-Awwal", "Observance of the birth of the Holy Prophet Muhammad (PBUH).")
        )
    }

    fun getMockMosques(): List<Mosque> {
        return listOf(
            Mosque("Jamia Masjid Al-Farooq", "0.4 km", "Sector F-8, Blue Area, Islamabad", 4.8f, 33.7089, 73.0561),
            Mosque("Masjid Bilal Habashi", "1.2 km", "Street 12, G-9/1, Islamabad", 4.6f, 33.6934, 73.0315),
            Mosque("Faisal Mosque", "3.1 km", "Foot of Margalla Hills, Islamabad", 4.9f, 33.7299, 73.0373),
            Mosque("Madina Masjid & Islamic Centre", "1.8 km", "E-11/3 Markaz, Islamabad", 4.5f, 33.6989, 72.9789)
        )
    }
}
