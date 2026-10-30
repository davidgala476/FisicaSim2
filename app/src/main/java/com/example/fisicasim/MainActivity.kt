package com.example.fisicasim

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { FisicaTheme { App() } }
    }
}

private val topicIcons = listOf("⚖️", "✏️", "📐", "🪢", "🌀", "〰️", "🏃", "⚡", "🔄")

@Composable
fun App() {
    var tab by rememberSaveable { mutableStateOf(0) }
    var selected by rememberSaveable { mutableStateOf<Int?>(null) }
    var topic by rememberSaveable { mutableStateOf<Int?>(null) }
    var workshop by rememberSaveable { mutableStateOf<Int?>(null) }
    var pdfAsset by rememberSaveable { mutableStateOf<String?>(null) }
    var visited by rememberSaveable { mutableStateOf(listOf<Int>()) }

    val viewing = pdfAsset != null
    val inDetail = when (tab) {
        0 -> selected != null
        1 -> topic != null
        else -> workshop != null
    }
    fun goBack() {
        when {
            pdfAsset != null -> pdfAsset = null
            tab == 0 -> selected = null
            tab == 1 -> topic = null
            else -> workshop = null
        }
    }
    BackHandler(enabled = viewing || inDetail) { goBack() }

    Box(Modifier.fillMaxSize().background(AppBg)) {
        Decorations()
        Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().imePadding()) {
            AppHeader(showBack = viewing || inDetail, onBack = { goBack() })
            if (!viewing && !inDetail) {
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TabChip("Simulador", tab == 0) { tab = 0; selected = null; topic = null; workshop = null }
                    TabChip("Teoría", tab == 1) { tab = 1; selected = null; topic = null; workshop = null }
                    TabChip("Taller", tab == 2) { tab = 2; selected = null; topic = null; workshop = null }
                }
                Spacer(Modifier.height(6.dp))
            }
            val sel = selected
            val tp = topic
            val ws = workshop
            val pdf = pdfAsset?.let { a -> allPdfs().firstOrNull { it.asset == a } }
            when {
                pdf != null -> PdfViewerScreen(pdf)
                tab == 0 && sel == null -> ExerciseList(visited) { i ->
                    if (i !in visited) visited = visited + i
                    selected = i
                }
                tab == 0 && sel != null -> ExerciseScreen(sel)
                tab == 1 && tp == null -> TheoryList { topic = it }
                tab == 1 && tp != null -> TopicScreen(tp) { pdfAsset = it.asset }
                tab == 2 && ws == null -> WorkshopList { workshop = it }
                tab == 2 && ws != null -> WorkshopScreen(ws) { pdfAsset = it.asset }
            }
        }
    }
}

@Composable
fun AppHeader(showBack: Boolean, onBack: () -> Unit) {
    Box(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp).height(48.dp)) {
        if (showBack) {
            Box(
                Modifier.align(Alignment.CenterStart).size(40.dp).clip(CircleShape)
                    .background(Color.White).clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) { Text("←", fontSize = 20.sp, color = Ink) }
        }
        Text(
            "FisicaSim",
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.titleLarge,
            color = Ink
        )
        Box(
            Modifier.align(Alignment.CenterEnd).size(46.dp).clip(CircleShape)
                .background(Ink).border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) { Text("⚛️", fontSize = 22.sp) }
    }
}

@Composable
fun ExerciseList(visited: List<Int>, onPick: (Int) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(20.dp, 8.dp, 20.dp, 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            val total = exercises.size
            val done = visited.size.coerceAtMost(total)
            AppCard {
                Column(Modifier.padding(16.dp)) {
                    SectionTitle("Laboratorio de Física")
                    Spacer(Modifier.height(2.dp))
                    Text("Elige un ejercicio, cambia los valores y observa la simulación.", fontSize = 13.sp, color = Muted)
                    Spacer(Modifier.height(14.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("$done de $total explorados", fontSize = 13.sp, color = Muted)
                        Text("${if (total == 0) 0 else done * 100 / total}%", fontSize = 13.sp, color = Teal, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(6.dp))
                    ProgressBar(if (total == 0) 0f else done / total.toFloat())
                }
            }
        }
        itemsIndexed(exercises) { i, ex ->
            val m = metas[i]
            val accent = accentFor(i)
            AppCard(onClick = { onPick(i) }) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconBubble(m.icon, accent)
                    Spacer(Modifier.width(12.dp))
                    Text(ex.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Ink, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    if (i in visited) TagPill("Visto", Muted)
                    Spacer(Modifier.width(6.dp))
                    Text("›", fontSize = 26.sp, color = Teal)
                }
            }
        }
    }
}

@Composable
fun ExerciseScreen(idx: Int) {
    val ex = exercises[idx]
    val meta = metas[idx]
    val accent = accentFor(idx)
    val info = exerciseInfos.getOrNull(idx)
    val values = remember(idx) {
        mutableStateMapOf<String, String>().apply { ex.params.forEach { put(it.key, it.default.toString()) } }
    }
    var t by remember(idx) { mutableFloatStateOf(0f) }
    var playing by remember(idx) { mutableStateOf(true) }
    var slow by remember(idx) { mutableStateOf(false) }
    val slowNow by rememberUpdatedState(slow)

    LaunchedEffect(playing, idx) {
        if (playing) {
            var last = withFrameNanos { it }
            while (true) {
                val now = withFrameNanos { it }
                t += (now - last) / 1e9f * (if (slowNow) 0.3f else 1f)
                last = now
                if (t > 6f) t = 0f
            }
        }
    }

    val nums = ex.params.associate { it.key to values[it.key]?.replace(',', '.')?.toDoubleOrNull() }
    val map: Map<String, Double>? = if (nums.values.all { it != null }) nums.mapValues { it.value!! } else null
    val results = map?.let { runCatching { ex.solve(it) }.getOrNull() }

    Column(
        Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(top = 4.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Encabezado del ejercicio
        AppCard {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconBubble(meta.icon, accent)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(ex.title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Ink)
                        Text(meta.tag, color = accent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                if (info != null) {
                    Spacer(Modifier.height(12.dp))
                    Text("¿Qué es?", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TealDark)
                    Spacer(Modifier.height(2.dp))
                    Text(info.description, fontSize = 14.sp, color = Ink, lineHeight = 20.sp)
                }
            }
        }

        // Simulación
        AppCard {
            Column(Modifier.padding(12.dp)) {
                Canvas(Modifier.fillMaxWidth().height(230.dp).clip(RoundedCornerShape(16.dp)).background(AppBg)) {
                    if (map != null) runCatching { meta.sim(this, map, t) }
                }
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PillButton(if (playing) "⏸ Pausa" else "▶ Reproducir", { playing = !playing })
                    PillButton("↺ Reiniciar", { t = 0f }, filled = false)
                    Spacer(Modifier.weight(1f))
                    Text("Lenta", fontSize = 12.sp, color = Muted)
                    Switch(
                        checked = slow,
                        onCheckedChange = { slow = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = Teal, checkedThumbColor = Color.White)
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(meta.legend, fontSize = 12.sp, color = Muted)
            }
        }

        // Valores
        AppCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionTitle("Valores")
                ex.params.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { p ->
                            OutlinedTextField(
                                value = values[p.key] ?: "",
                                onValueChange = { values[p.key] = it },
                                label = { Text(if (p.unit.isEmpty()) p.label else "${p.label} (${p.unit})", fontSize = 11.sp, maxLines = 1) },
                                textStyle = TextStyle(fontSize = 14.sp, color = Ink),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal,
                                    unfocusedBorderColor = Color(0xFFD5DDE0),
                                    focusedLabelColor = TealDark,
                                    cursorColor = Teal,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
                PillButton(
                    "Restablecer valores",
                    { ex.params.forEach { values[it.key] = it.default.toString() }; t = 0f },
                    filled = false
                )
            }
        }

        // Resultados
        AppCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionTitle("Resultados")
                if (results == null) {
                    Text("Revisa los valores ingresados.", color = Coral, fontSize = 14.sp)
                } else {
                    results.forEachIndexed { i, r ->
                        if (i > 0) Box(Modifier.fillMaxWidth().height(1.dp).background(TrackGrey))
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(r.label, color = Muted, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            Text(
                                r.value, color = Teal, fontWeight = FontWeight.Bold, fontSize = 16.sp,
                                textAlign = TextAlign.End, modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Enunciado, apartados y fórmulas
        InfoSectionCard("Enunciado", ex.statement)
        info?.sections?.forEach { InfoSectionCard(it.title, it.text) }
        if (info != null && info.formulas.isNotEmpty()) FormulasCard(info.formulas)
    }
}

@Composable
fun TheoryList(onOpen: (Int) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(20.dp, 8.dp, 20.dp, 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text("Temas y fórmulas", style = MaterialTheme.typography.headlineSmall, color = Ink)
                Text("Elige un tema para ver su explicación.", fontSize = 13.sp, color = Muted)
            }
        }
        itemsIndexed(topics) { i, tp ->
            AppCard(onClick = { onOpen(i) }) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconBubble(topicIcons.getOrElse(i) { "📘" }, accentFor(i))
                    Spacer(Modifier.width(12.dp))
                    Text(tp.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Ink, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    Text("›", fontSize = 26.sp, color = Teal)
                }
            }
        }
    }
}

@Composable
fun TopicScreen(idx: Int, onView: (PdfFile) -> Unit) {
    val tp = topics[idx]
    Column(
        Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(top = 4.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        AppCard {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconBubble(topicIcons.getOrElse(idx) { "📘" }, accentFor(idx))
                    Spacer(Modifier.width(12.dp))
                    Text(tp.title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Ink, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
                Text("¿Qué es?", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TealDark)
                Spacer(Modifier.height(2.dp))
                Text(tp.description, fontSize = 14.sp, color = Ink, lineHeight = 20.sp)
            }
        }
        tp.videoId?.let { VideoCard(it) }
        tp.sections.forEach { InfoSectionCard(it.title, it.text) }
        if (tp.formulas.isNotEmpty()) FormulasCard(tp.formulas)
        if (tp.pdfs.isNotEmpty()) MaterialCard(tp.pdfs, onView)
    }
}
