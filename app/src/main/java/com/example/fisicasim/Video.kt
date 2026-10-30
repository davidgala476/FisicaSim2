package com.example.fisicasim

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubeVideo(videoId: String, modifier: Modifier = Modifier) {
    val html = """
        <html><head><meta name="viewport" content="width=device-width, initial-scale=1">
        <style>html,body{margin:0;height:100%;background:#000}</style></head>
        <body><iframe style="position:absolute;top:0;left:0;width:100%;height:100%;border:0"
        src="https://www.youtube.com/embed/$videoId?playsinline=1&rel=0"
        allow="accelerometer; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe></body></html>
    """.trimIndent()
    AndroidView(
        modifier = modifier.fillMaxWidth().aspectRatio(16f / 9f).clip(RoundedCornerShape(16.dp)),
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                webChromeClient = WebChromeClient()
                webViewClient = WebViewClient()
                setBackgroundColor(android.graphics.Color.BLACK)
                loadDataWithBaseURL("https://www.youtube.com", html, "text/html", "utf-8", null)
            }
        },
        onRelease = { it.destroy() }
    )
}

@Composable
fun VideoCard(videoId: String) {
    val uriHandler = LocalUriHandler.current
    AppCard {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionTitle("Video explicativo")
            YouTubeVideo(videoId)
            PillButton(
                "Abrir en YouTube",
                { uriHandler.openUri("https://www.youtube.com/watch?v=$videoId") },
                filled = false
            )
        }
    }
}
