package com.example.fisicasim

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Paleta tomada del sistema de diseño de FinMojarra
val Teal = Color(0xFF00B9B3)      // Primary
val TealDark = Color(0xFF00817D)
val Coral = Color(0xFFFF6347)     // Secondary
val Mint = Color(0xFF00D2B4)      // Tertiary
val Ink = Color(0xFF0A0F14)       // Neutral
val AppBg = Color(0xFFF5FBFA)
val Muted = Color(0xFF7B8794)
val TrackGrey = Color(0xFFE9EDEF)

/**
 * Tipografía de FinMojarra: Plus Jakarta Sans.
 * Para usarla: descarga la fuente, copia los .ttf a res/font/ (plus_jakarta_sans_regular.ttf,
 * plus_jakarta_sans_semibold.ttf, plus_jakarta_sans_bold.ttf) y reemplaza la línea de abajo por:
 * FontFamily(Font(R.font.plus_jakarta_sans_regular), Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold), Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold))
 */
val AppFont: FontFamily = FontFamily.SansSerif

private val AppColors = lightColorScheme(
    primary = Teal,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6F4F2),
    onPrimaryContainer = Ink,
    secondary = Coral,
    tertiary = Mint,
    background = AppBg,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    outline = Color(0xFFD5DDE0)
)

private val AppTypography = Typography(
    headlineSmall = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Bold, fontSize = 24.sp),
    titleLarge = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Bold, fontSize = 20.sp),
    titleMedium = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Bold, fontSize = 16.sp),
    bodyLarge = TextStyle(fontFamily = AppFont, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = AppFont, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = AppFont, fontSize = 12.sp),
    labelLarge = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
)

@Composable
fun FisicaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = AppColors, typography = AppTypography, content = content)
}

/** Color de acento por tarjeta (alterna los colores de la paleta). */
fun accentFor(i: Int): Color = when (i % 3) {
    0 -> Teal
    1 -> Coral
    else -> TealDark
}
