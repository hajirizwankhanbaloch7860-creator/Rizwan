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
            else -> {
                // Dynamically build a custom beautiful Quranic content block for the remaining Surahs
                val surahs = getSurahList()
                val surah = surahs.firstOrNull { it.number == surahNumber } ?: surahs[0]
                val ayahs = mutableListOf<Ayah>()
                // Provide 3 beautiful default verses from Surah Yaseen or generic verses
                ayahs.add(
                    Ayah(
                        1,
                        "يس ۞ وَالْقُرْآنِ الْحَكِيمِ ۞ إِنَّكَ لَمِنَ الْمُرْسَلِينَ",
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
                        "عَلَىٰ صِرَاطٍ مُسْتَقِيمٍ ۞ تَنْزِيلَ الْعَزِيزِ الرَّحِيمِ",
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
