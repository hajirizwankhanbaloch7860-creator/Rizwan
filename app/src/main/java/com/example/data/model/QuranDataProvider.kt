package com.example.data.model

object QuranDataProvider {

    fun getSurahList(): List<Surah> {
        return listOf(
            Surah(1, "الفاتحة", "Al-Fatiha", 7, "Meccan"),
            Surah(2, "البقرة", "Al-Baqarah", 286, "Medinan"),
            Surah(3, "آل عمران", "Aal-e-Imran", 200, "Medinan"),
            Surah(4, "النساء", "An-Nisa", 176, "Medinan"),
            Surah(5, "المائدة", "Al-Ma'idah", 120, "Medinan"),
            Surah(6, "الأنعام", "Al-An'am", 165, "Meccan"),
            Surah(7, "الأعراف", "Al-A'raf", 206, "Meccan"),
            Surah(8, "الأنفال", "Al-Anfal", 75, "Medinan"),
            Surah(9, "التوبة", "At-Tawbah", 129, "Medinan"),
            Surah(10, "يونس", "Yunus", 109, "Meccan"),
            Surah(11, "هود", "Hud", 123, "Meccan"),
            Surah(12, "يوسف", "Yusuf", 111, "Meccan"),
            Surah(13, "الرعد", "Ar-Rad", 43, "Medinan"),
            Surah(14, "إبراهيم", "Ibrahim", 52, "Meccan"),
            Surah(15, "الحجر", "Al-Hijr", 99, "Meccan"),
            Surah(16, "النحل", "An-Nahl", 128, "Meccan"),
            Surah(17, "الإسراء", "Al-Isra", 111, "Meccan"),
            Surah(18, "الكهف", "Al-Kahf", 110, "Meccan"),
            Surah(19, "مريم", "Maryam", 98, "Meccan"),
            Surah(20, "طه", "Taha", 135, "Meccan")
        )
    }

    fun getJuzList(): List<Juz> {
        return listOf(
            Juz(1, 1, 1, 1, 141),
            Juz(2, 1, 142, 2, 141),
            Juz(3, 2, 142, 2, 251),
            Juz(4, 3, 1, 3, 91),
            Juz(5, 3, 92, 4, 23),
            Juz(6, 4, 24, 4, 147),
            Juz(7, 5, 1, 5, 81),
            Juz(8, 5, 82, 6, 110),
            Juz(9, 7, 1, 7, 87),
            Juz(10, 7, 88, 8, 40),
            Juz(11, 8, 41, 9, 34),
            Juz(12, 9, 35, 11, 5),
            Juz(13, 11, 6, 12, 52),
            Juz(14, 12, 53, 15, 99),
            Juz(15, 16, 1, 16, 128),
            Juz(16, 17, 1, 18, 74),
            Juz(17, 18, 75, 21, 45),
            Juz(18, 21, 46, 23, 118),
            Juz(19, 24, 1, 25, 20),
            Juz(20, 25, 21, 27, 55),
            Juz(21, 27, 56, 29, 45),
            Juz(22, 29, 46, 33, 30),
            Juz(23, 33, 31, 36, 21),
            Juz(24, 36, 22, 39, 31),
            Juz(25, 39, 32, 41, 46),
            Juz(26, 41, 47, 46, 10),
            Juz(27, 46, 11, 51, 30),
            Juz(28, 51, 31, 57, 29),
            Juz(29, 58, 1, 66, 12),
            Juz(30, 67, 1, 114, 6)
        )
    }

    fun getAyahsForSurah(surahNumber: Int): List<Ayah> {
        return when (surahNumber) {
            1 -> getSurah1Ayahs()
            93 -> getSurah93Ayahs()
            94 -> getSurah94Ayahs()
            103 -> getSurah103Ayahs()
            108 -> getSurah108Ayahs()
            112 -> getSurah112Ayahs()
            113 -> getSurah113Ayahs()
            114 -> getSurah114Ayahs()
            else -> emptyList()
        }
    }

    private fun getSurah1Ayahs(): List<Ayah> {
        return listOf(
            Ayah(1, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "All praise is due to Allah, Lord of the worlds", "تمام تعریفیں اللہ تعالیٰ کے لیے ہیں جو تمام جہانوں کا پالنے والا ہے", listOf()),
            Ayah(2, "الرَّحْمَٰنِ الرَّحِيمِ", "The Entirely Merciful, the Especially Merciful", "بہت مہربان اور نہایت رحم والے", listOf()),
            Ayah(3, "مَالِكِ يَوْمِ الدِّينِ", "Sovereign of the Day of Recompense", "روز جزا کے دن کے مالک", listOf()),
            Ayah(4, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "It is you we worship and you we ask for help", "ہم تمہاری ہی عبادت کریں گے اور تمہی سے مدد مانگیں گے", listOf()),
            Ayah(5, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "Guide us to the straight path", "ہمیں سیدھے راستے پر چلایں", listOf()),
            Ayah(6, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ ۝ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "The path of those upon whom You have bestowed favor, not those who have earned Your anger nor those who are astray", "ان لوگوں کے راستے پر جن کو تم نے نعمتیں دی ہیں، نہ ان کے جن پر غضب ہے اور نہ ان گمراہوں کے", listOf())
        )
    }

    private fun getSurah93Ayahs(): List<Ayah> {
        return listOf(
            Ayah(1, "وَالضُّحَىٰ", "By the morning brightness", "قسم ہے صبح کی روشنی کی", listOf()),
            Ayah(2, "وَاللَّيْلِ إِذَا سَجَىٰ", "And by the night when it covers with darkness", "اور رات کی قسم جب وہ سکون میں آجائے", listOf()),
            Ayah(3, "مَا وَدَّعَكَ رَبُّكَ وَمَا قَلَىٰ", "Your Lord has not taken leave of you, nor has He detested you", "تمہارے رب نے تمہیں نہیں چھوڑا اور نہ ہی ناپسند کیا", listOf())
        )
    }

    private fun getSurah94Ayahs(): List<Ayah> {
        return listOf(
            Ayah(1, "أَلَمْ نَشْرَحْ لَكَ صَدْرَكَ", "Have We not expanded for you your breast?", "کیا ہم نے تمہارے لیے تمہارا سینہ کشادہ نہیں کیا؟", listOf()),
            Ayah(2, "وَوَضَعْنَا عَنكَ وِزْرَكَ", "And We removed from you your burden", "اور ہم نے تمہارے سے تمہارا بوجھ ہٹا دیا", listOf()),
            Ayah(3, "الَّذِي أَنقَضَ ظَهْرَكَ", "Which had weighed upon your back", "جو تمہاری کمر کو توڑ رہا تھا", listOf())
        )
    }

    private fun getSurah103Ayahs(): List<Ayah> {
        return listOf(
            Ayah(1, "وَالْعَصْرِ", "By the time", "قسم ہے زمانے کی", listOf()),
            Ayah(2, "إِنَّ الْإِنسَانَ لَفِي خُسْرٍ", "Indeed, mankind is in loss", "بیشک انسان خسارے میں ہے", listOf()),
            Ayah(3, "إِلَّا الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ وَتَوَاصَوْا بِالْحَقِّ وَتَوَاصَوْا بِالصَّبْرِ", "Except for those who have believed and done righteous deeds and advised each other to truth and advised each other to patience", "مگر وہ لوگ جو ایمان لائے اور عمل صالح کیے اور ایک دوسرے کو حق کی وصیت کی اور صبر کی وصیت کی", listOf())
        )
    }

    private fun getSurah108Ayahs(): List<Ayah> {
        return listOf(
            Ayah(1, "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ", "Indeed, We have given you al-Kawthar", "بیشک ہم نے تمہیں الکوثر دیا ہے", listOf()),
            Ayah(2, "فَصَلِّ لِرَبِّكَ وَانْحَرْ", "So pray to your Lord and sacrifice", "پس اپنے رب کے لیے نماز پڑھو اور قربانی دو", listOf()),
            Ayah(3, "إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ", "Indeed, he who hates you - he will be cut off", "بیشک تمہارا دشمن بے اولاد ہو گا", listOf())
        )
    }

    private fun getSurah112Ayahs(): List<Ayah> {
        return listOf(
            Ayah(1, "قُلْ هُوَ اللَّهُ أَحَدٌ", "Say, He is Allah, One", "کہہ وہ اللہ ہے ایک", listOf()),
            Ayah(2, "اللَّهُ الصَّمَدُ", "Allah, the Self-Sufficient Sustainer", "اللہ بے نیاز ہے", listOf()),
            Ayah(3, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "He neither begets nor is born", "نہ کسی کا باپ ہے نہ کسی کا بیٹا", listOf()),
            Ayah(4, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "And there is not to Him any equivalent", "اور اس کے برابر کوئی نہیں", listOf())
        )
    }

    private fun getSurah113Ayahs(): List<Ayah> {
        return listOf(
            Ayah(1, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "Say, I seek refuge in Lord of daybreak", "کہہ میں صبح کے رب کی پناہ چاہتا ہوں", listOf()),
            Ayah(2, "مِن شَرِّ مَا خَلَقَ", "From the evil of that which He has created", "اس کی برائی سے جو اس نے پیدا کی", listOf()),
            Ayah(3, "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "And from the evil of darkness when it settles", "اور رات کی برائی سے جب وہ آئے", listOf()),
            Ayah(4, "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "And from the evil of the blowers in knots", "اور گرہ پھونکنے والیوں کی برائی سے", listOf()),
            Ayah(5, "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "And from the evil of an envier when he envies", "اور حسد کرنے والے کی برائی سے جب وہ حسد کرے", listOf())
        )
    }

    private fun getSurah114Ayahs(): List<Ayah> {
        return listOf(
            Ayah(1, "قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "Say, I seek refuge in the Lord of mankind", "کہہ میں لوگوں کے رب کی پناہ چاہتا ہوں", listOf()),
            Ayah(2, "مَلِكِ النَّاسِ", "Sovereign of mankind", "لوگوں کا بادشاہ", listOf()),
            Ayah(3, "إِلَٰهِ النَّاسِ", "God of mankind", "لوگوں کا معبود", listOf()),
            Ayah(4, "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "From the evil of the retreating whisperer", "پوشیدہ شیطان کی برائی سے", listOf()),
            Ayah(5, "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "Who whispers [discord] into the breasts of mankind", "جو لوگوں کے دلوں میں وسوسہ ڈالتا ہے", listOf()),
            Ayah(6, "مِنَ الْجِنَّةِ وَالنَّاسِ", "From among the jinn and mankind", "جنات اور انسانوں میں سے", listOf())
        )
    }
}
