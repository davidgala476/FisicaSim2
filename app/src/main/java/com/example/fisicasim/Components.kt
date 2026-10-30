package com.example.fisicasim

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Formas teal decorativas (esquina superior derecha e inferior izquierda). */
@Composable
fun Decorations() {
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        drawCircle(Teal, radius = w * 0.30f, center = Offset(w * 0.98f, 0f))
        drawCircle(Teal, radius = w * 0.12f, center = Offset(w * 0.68f, h * 0.005f))
        drawCircle(Teal, radius = w * 0.22f, center = Offset(w * 0.02f, h))
        drawCircle(Teal, radius = w * 0.14f, center = Offset(w * 0.20f, h))
        drawCircle(Teal, radius = w * 0.09f, center = Offset(w * 0.33f, h * 1.01f))
    }
}

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) { Column(content = content) }
}

@Composable
fun TabChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(CircleShape)
            .background(if (selected) Ink else Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 11.dp)
    ) {
        Text(text, color = if (selected) Color.White else Ink, fontWeight = FontWeight.Medium, fontSize = 15.sp)
    }
}

@Composable
fun PillButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, filled: Boolean = true) {
    Box(
        modifier
            .clip(CircleShape)
            .then(if (filled) Modifier.background(Teal) else Modifier.border(1.5.dp, Teal, CircleShape))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (filled) Color.White else TealDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

@Composable
fun TagPill(text: String, color: Color) {
    Box(Modifier.clip(CircleShape).background(color.copy(alpha = 0.14f)).padding(horizontal = 12.dp, vertical = 5.dp)) {
        Text(text, color = color, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
    }
}

@Composable
fun IconBubble(emoji: String, color: Color, size: Int = 48) {
    Box(
        Modifier.size(size.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) { Text(emoji, fontSize = (size * 0.45f).sp) }
}

@Composable
fun ProgressBar(fraction: Float, color: Color = Teal) {
    val f by animateFloatAsState(fraction.coerceIn(0f, 1f), label = "progress")
    Box(Modifier.fillMaxWidth().height(8.dp).clip(CircleShape).background(TrackGrey)) {
        Box(Modifier.fillMaxWidth(f).fillMaxHeight().clip(CircleShape).background(color))
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, color = Ink)
}
