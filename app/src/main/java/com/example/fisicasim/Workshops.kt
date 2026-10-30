package com.example.fisicasim

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Workshop(
    val title: String,
    val description: String,
    val instructions: String,
    val problems: List<String>,
    val source: String,
    val formulas: List<String>,
    val pdf: PdfFile
)

val workshops = listOf(
    Workshop(
        "Taller 1: Conservación de la energía",
        "Taller de práctica con 5 problemas de conservación de la energía mecánica: pistas sin fricción, bucles, péndulos y sistemas con poleas.",
        "Resuelve cada problema en tu cuaderno: identifica el sistema, plantea el balance energético y justifica tu respuesta. El taller no incluye soluciones.",
        listOf(
            "1. Bolita en un bucle (Serway 8.3): rapidez en A y fuerza normal.",
            "2. Partícula sobre una pista (Serway 8.4): rapideces en B y C, y trabajo de la gravedad.",
            "3. Trapecio y fuerza de sujeción (Serway 8.6): demostrar F = mg(3 cos θ − 2 cos θi) y hallar θi.",
            "4. Dos masas y una polea (Serway 8.7): rapidez de m2 y altura máxima.",
            "5. Polea fija y polea móvil (Serway 8.12): rapidez de A cuando la separación vertical es h."
        ),
        "Serway, R. A. y Jewett, J. W., Jr. (2008). Física para ciencias e ingeniería, vol. 1, 7.ª ed., Cengage, cap. 8, pp. 218-219.",
        listOf(
            "Ec = ½·m·v²",
            "Ep gravitatoria = m·g·h",
            "Ep elástica = ½·k·x²",
            "Sin fricción: Ec + Ep = constante",
            "Con fricción: E final = E inicial − fk·d"
        ),
        Pdfs.taller1
    )
)

@Composable
fun WorkshopList(onOpen: (Int) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(20.dp, 8.dp, 20.dp, 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text("Talleres de práctica", style = MaterialTheme.typography.headlineSmall, color = Ink)
                Text("Elige un taller para verlo, descargarlo y resolverlo.", fontSize = 13.sp, color = Muted)
            }
        }
        itemsIndexed(workshops) { i, w ->
            AppCard(onClick = { onOpen(i) }) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconBubble("📝", accentFor(i))
                    Spacer(Modifier.width(12.dp))
                    Text(w.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Ink, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    Text("›", fontSize = 26.sp, color = Teal)
                }
            }
        }
    }
}

@Composable
fun WorkshopScreen(idx: Int, onView: (PdfFile) -> Unit) {
    val w = workshops[idx]
    Column(
        Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(top = 4.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        AppCard {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconBubble("📝", accentFor(idx))
                    Spacer(Modifier.width(12.dp))
                    Text(w.title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Ink, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
                Text("¿Qué es?", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TealDark)
                Spacer(Modifier.height(2.dp))
                Text(w.description, fontSize = 14.sp, color = Ink, lineHeight = 20.sp)
            }
        }
        MaterialCard(listOf(w.pdf), onView, title = "Descargar el taller")
        InfoSectionCard("Instrucciones", w.instructions)
        InfoSectionCard("Ejercicios", w.problems.joinToString("\n\n"))
        FormulasCard(w.formulas)
        InfoSectionCard("Fuente", w.source)
    }
}
