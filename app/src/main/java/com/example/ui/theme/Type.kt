package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.R

// Google Font Provider
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// Arabic Quranic Font Families (Classic Naskh Styles)
val AmiriFontFamily = FontFamily(
    Font(googleFont = GoogleFont("Amiri"), fontProvider = provider)
)

val QuranScheherazadeFontFamily = FontFamily(
    Font(googleFont = GoogleFont("Scheherazade New"), fontProvider = provider)
)

// Classic Persian/Indo-Pak Quranic Style (very elegant flowing curves)
val LateefFontFamily = FontFamily(
    Font(googleFont = GoogleFont("Lateef"), fontProvider = provider)
)

// Legible Modern Naskh Style
val NotoNaskhArabicFontFamily = FontFamily(
    Font(googleFont = GoogleFont("Noto Naskh Arabic"), fontProvider = provider)
)

// Urdu Font Family (Authentic Elegant Nastaliq Style)
val NotoNastaliqUrduFontFamily = FontFamily(
    Font(googleFont = GoogleFont("Noto Nastaliq Urdu"), fontProvider = provider)
)

// Set of Material typography styles to start with
val Typography =
  Typography(
    bodyLarge =
      TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
      )
  )
