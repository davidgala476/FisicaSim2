package com.example.fisicasim

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color as AColor
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.roundToInt

data class PdfFile(val title: String, val asset: String, val fileName: String)

object Pdfs {
    val oscilatorio = PdfFile(
        "Movimiento oscilatorio: de la Ley de Hooke al péndulo simple",
        "pdfs/movimiento_oscilatorio.pdf", "Movimiento_Oscilatorio.pdf"
    )
    val polipastos = PdfFile(
        "Polipastos: fuerzas, equilibrio y movimiento",
        "pdfs/polipastos.pdf", "Polipastos.pdf"
    )
    val cinematica = PdfFile(
        "Cinemática: la física como herramienta de predicción",
        "pdfs/cinematica.pdf", "Cinematica.pdf"
    )
    val taller1 = PdfFile(
        "Taller 1: Conservación de la energía",
        "pdfs/taller_1_conservacion_energia.pdf", "Taller_1_Conservacion_de_la_energia.pdf"
    )
}

fun allPdfs(): List<PdfFile> = (topics.flatMap { it.pdfs } + workshops.map { it.pdf }).distinctBy { it.asset }

fun downloadPdf(context: Context, pdf: PdfFile) {
    val app = context.applicationContext
    Thread {
        val msg = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, pdf.fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = app.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    ?: throw IllegalStateException("sin uri")
                app.contentResolver.openOutputStream(uri)!!.use { out ->
                    app.assets.open(pdf.asset).use { it.copyTo(out) }
                }
                "Guardado en Descargas: ${pdf.fileName}"
            } else {
                val dir = app.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: app.filesDir
                dir.mkdirs()
                val f = File(dir, pdf.fileName)
                app.assets.open(pdf.asset).use { i -> f.outputStream().use { o -> i.copyTo(o) } }
                "Guardado en: ${f.absolutePath}"
            }
        } catch (e: Exception) {
            "No se pudo guardar el archivo"
        }
        Handler(Looper.getMainLooper()).post { Toast.makeText(app, msg, Toast.LENGTH_LONG).show() }
    }.start()
}

class PdfSession(context: Context, asset: String) {
    private val file = File(context.cacheDir, "pdf_" + asset.substringAfterLast('/'))
    private val pfd: ParcelFileDescriptor
    private val renderer: PdfRenderer
    private val mutex = Mutex()
    val pageCount: Int

    init {
        if (!file.exists() || file.length() == 0L) {
            context.assets.open(asset).use { i -> file.outputStream().use { o -> i.copyTo(o) } }
        }
        pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        renderer = PdfRenderer(pfd)
        pageCount = renderer.pageCount
    }

    suspend fun render(index: Int, widthPx: Int): Bitmap = mutex.withLock {
        withContext(Dispatchers.IO) {
            val page = renderer.openPage(index)
            try {
                val w = widthPx.coerceAtLeast(200)
                val h = (w.toFloat() * page.height / page.width).roundToInt().coerceAtLeast(1)
                val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                bmp.eraseColor(AColor.WHITE)
                page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bmp
            } finally {
                page.close()
            }
        }
    }

    fun close() {
        try { renderer.close() } catch (_: Exception) {}
        try { pfd.close() } catch (_: Exception) {}
    }
}

@Composable
fun PdfViewerScreen(pdf: PdfFile) {
    val context = LocalContext.current
    var session by remember(pdf.asset) { mutableStateOf<PdfSession?>(null) }
    var failed by remember(pdf.asset) { mutableStateOf(false) }
    var zoom by rememberSaveable(pdf.asset) { mutableStateOf(1f) }

    LaunchedEffect(pdf.asset) {
        try {
            session = withContext(Dispatchers.IO) { PdfSession(context, pdf.asset) }
        } catch (e: Exception) {
            failed = true
        }
    }
    DisposableEffect(pdf.asset) { onDispose { session?.close() } }

    Column(Modifier.fillMaxSize()) {
        Text(
            pdf.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Ink,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PillButton("−", { zoom = (zoom - 0.5f).coerceAtLeast(1f) }, filled = false)
            Text("${(zoom * 100).roundToInt()}%", fontSize = 13.sp, color = Muted)
            PillButton("+", { zoom = (zoom + 0.5f).coerceAtMost(3f) }, filled = false)
            Spacer(Modifier.weight(1f))
            PillButton("Descargar", { downloadPdf(context, pdf) })
        }
        val s = session
        when {
            failed -> Text(
                "No se pudo abrir el PDF.", color = Coral,
                modifier = Modifier.padding(20.dp)
            )
            s == null -> Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Teal)
            }
            else -> BoxWithConstraints(Modifier.fillMaxSize()) {
                val viewport = maxWidth
                Box(Modifier.fillMaxSize().horizontalScroll(rememberScrollState())) {
                    LazyColumn(
                        Modifier.width(viewport * zoom),
                        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(s.pageCount) { i -> PdfPageView(s, i) }
                    }
                }
            }
        }
    }
}

@Composable
fun PdfPageView(session: PdfSession, index: Int) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val widthPx = with(LocalDensity.current) { maxWidth.roundToPx() }.coerceIn(200, 2400)
        val bmp by produceState<Bitmap?>(null, session, index, widthPx) {
            value = try { session.render(index, widthPx) } catch (e: Exception) { null }
        }
        val b = bmp
        if (b != null) {
            Image(
                bitmap = b.asImageBitmap(),
                contentDescription = "Página ${index + 1}",
                modifier = Modifier.fillMaxWidth().aspectRatio(b.width.toFloat() / b.height)
            )
        } else {
            Box(Modifier.fillMaxWidth().aspectRatio(16f / 9f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Teal)
            }
        }
    }
}

@Composable
fun MaterialCard(pdfs: List<PdfFile>, onView: (PdfFile) -> Unit, title: String = "Material para descargar") {
    val ctx = LocalContext.current
    AppCard {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            SectionTitle(title)
            pdfs.forEach { pdf ->
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconBubble("📄", Coral, 40)
                        Spacer(Modifier.width(12.dp))
                        Text(pdf.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Ink, modifier = Modifier.weight(1f))
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)) {
                        PillButton("Ver", { onView(pdf) })
                        PillButton("Descargar", { downloadPdf(ctx, pdf) }, filled = false)
                    }
                }
            }
        }
    }
}
