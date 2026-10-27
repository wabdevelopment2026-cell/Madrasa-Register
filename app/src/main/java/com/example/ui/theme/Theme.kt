package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = MadrasaEmeraldPrimaryDark,
    onPrimary = MadrasaOnEmeraldPrimaryDark,
    primaryContainer = MadrasaEmeraldContainerDark,
    onPrimaryContainer = MadrasaOnEmeraldContainerDark,
    secondary = MadrasaGoldSecondaryDark,
    onSecondary = Color.Black,
    secondaryContainer = MadrasaGoldContainerDark,
    onSecondaryContainer = MadrasaOnGoldContainerDark,
    background = MadrasaBackgroundDark,
    surface = MadrasaSurfaceDark,
    surfaceVariant = MadrasaSurfaceVariantDark,
    onBackground = MadrasaOnSurfaceDark,
    onSurface = MadrasaOnSurfaceDark,
    onSurfaceVariant = MadrasaOnSurfaceVariantDark,
    outline = MadrasaOutlineDark,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = MadrasaEmeraldPrimary,
    onPrimary = MadrasaOnEmeraldPrimary,
    primaryContainer = MadrasaEmeraldContainer,
    onPrimaryContainer = MadrasaOnEmeraldContainer,
    secondary = MadrasaGoldSecondary,
    onSecondary = Color.White,
    secondaryContainer = MadrasaGoldContainer,
    onSecondaryContainer = MadrasaOnGoldContainer,
    tertiary = MadrasaTealTertiary,
    tertiaryContainer = MadrasaTealContainer,
    onTertiaryContainer = MadrasaOnTealContainer,
    background = MadrasaBackgroundLight,
    surface = MadrasaSurfaceLight,
    surfaceVariant = MadrasaSurfaceVariantLight,
    onBackground = MadrasaOnSurfaceLight,
    onSurface = MadrasaOnSurfaceLight,
    onSurfaceVariant = MadrasaOnSurfaceVariantLight,
    outline = MadrasaOutlineLight,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // We prefer our curated Islamic Emerald Green theme over dynamic device palette
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
