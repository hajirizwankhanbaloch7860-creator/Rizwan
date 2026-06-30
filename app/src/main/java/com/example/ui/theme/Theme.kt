package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimaryDark,
    onPrimary = NightBackgroundDark,
    primaryContainer = SoftCardDark,
    onPrimaryContainer = TextPrimaryDark,
    secondary = EmeraldSecondaryDark,
    onSecondary = NightBackgroundDark,
    tertiary = GoldTertiaryDark,
    onTertiary = NightBackgroundDark,
    background = NightBackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SoftCardDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = NightBackgroundDark,
    onSurfaceVariant = TextSecondaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimaryLight,
    onPrimary = CreamBackgroundLight,
    primaryContainer = SoftCardLight,
    onPrimaryContainer = TextPrimaryLight,
    secondary = EmeraldSecondaryLight,
    onSecondary = CreamBackgroundLight,
    tertiary = GoldTertiaryLight,
    onTertiary = CreamBackgroundLight,
    background = CreamBackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SoftCardLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = CreamBackgroundLight,
    onSurfaceVariant = TextSecondaryLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Allow turning off dynamic color to preserve our custom designed premium Islamic aesthetic by default
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
