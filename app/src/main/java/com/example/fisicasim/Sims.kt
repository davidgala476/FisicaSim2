package com.example.fisicasim

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.*

typealias SimFn = DrawScope.(Map<String, Double>, Float) -> Unit

/** Metadatos visuales de cada ejercicio (mismo orden que la lista `exercises`). */
data class Meta(val icon: String, val tag: String, val color: Color, val legend: String, val sim: SimFn)

private val INK = Color(0xFF37474F)
private val BLUE = Color(0xFF1E88E5)
private val ORANGE = Color(0xFFFB8C00)
private val GREEN = Color(0xFF43A047)
private val RED = Color(0xFFE53935)
private val GREY = Color(0xFFB0BEC5)

// ---------- utilidades de dibujo ----------
private fun DrawScope.ln(a: Offset, b: Offset, c: Color = INK, w: Float = 4f) = drawLine(c, a, b, w)

private fun DrawScope.box(bc: Offset, w: Float, h: Float, col: Color) {
    drawRect(col, Offset(bc.x - w / 2, bc.y - h), Size(w, h))
    drawRect(INK, Offset(bc.x - w / 2, bc.y - h), Size(w, h), style = Stroke(3f))
}

private fun DrawScope.arrow(a: Offset, b: Offset, col: Color) {
    ln(a, b, col, 5f)
    val ang = atan2(b.y - a.y, b.x - a.x)
    for (s in listOf(-0.5f, 0.5f)) ln(b, Offset(b.x - 16f * cos(ang + s), b.y - 16f * sin(ang + s)), col, 5f)
}

private fun DrawScope.spring(a: Offset, b: Offset, coils: Int = 8, amp: Float = 12f) {
    val dx = b.x - a.x; val dy = b.y - a.y
    val len = hypot(dx, dy).coerceAtLeast(1f)
    val nx = -dy / len; val ny = dx / len
    val n = coils * 2
    val p = Path(); p.moveTo(a.x, a.y)
    for (i in 1 until n) {
        val f = i / n.toFloat(); val s = if (i % 2 == 0) -amp else amp
        p.lineTo(a.x + dx * f + nx * s, a.y + dy * f + ny * s)
    }
    p.lineTo(b.x, b.y)
    drawPath(p, INK, style = Stroke(4f))
}

private fun DrawScope.ground(y: Float, x0: Float = 0f, x1: Float = size.width) {
    ln(Offset(x0, y), Offset(x1, y), INK, 5f)
    var x = x0 + 12f
    while (x < x1) { ln(Offset(x, y), Offset(x - 10f, y + 10f), GREY, 3f); x += 18f }
}

private fun DrawScope.sz(m: Double) =
    (size.height * 0.045f * sqrt(m.toFloat())).coerceIn(size.height * 0.1f, size.height * 0.28f)

private class Slope(val a: Offset, val th: Float, val len: Float) {
    val c = Offset(a.x + len * cos(th), a.y - len * sin(th))
    fun at(s: Float) = Offset(a.x + s * cos(th), a.y - s * sin(th))
}

private fun DrawScope.slope(deg: Double): Slope {
    val t = Math.toRadians(deg).toFloat().coerceIn(0.05f, 1.4f)
    val len = min(size.width * 0.6f / cos(t), size.height * 0.7f / sin(t))
    val s = Slope(Offset(size.width * 0.08f, size.height * 0.88f), t, len)
    val p = Path(); p.moveTo(s.a.x, s.a.y); p.lineTo(s.c.x, s.a.y); p.lineTo(s.c.x, s.c.y); p.close()
    drawPath(p, Color(0xFFECEFF1)); drawPath(p, INK, style = Stroke(4f))
    return s
}

private fun DrawScope.onSlope(s: Slope, d: Float, w: Float, col: Color) {
    val p = s.at(d)
    rotate(-Math.toDegrees(s.th.toDouble()).toFloat(), p) {
        drawRect(col, Offset(p.x - w / 2, p.y - w), Size(w, w))
        drawRect(INK, Offset(p.x - w / 2, p.y - w), Size(w, w), style = Stroke(3f))
    }
}

// ---------- simulaciones (mismo orden que `exercises`) ----------
val metas: List<Meta> = listOf(

    // 0 · Atwood
    Meta("⚖️", "Poleas", BLUE, "Azul = m1 · Naranja = m2. La masa mayor desciende con a = (m2−m1)g/(m1+m2).") { p, t ->
        val m1 = p["m1"]!!; val m2 = p["m2"]!!
        val a = abs(m2 - m1) * G / (m1 + m2)
        val dir = sign(m2 - m1).toFloat()
        val trav = min(0.5 * a * t * t, 1.0).toFloat() * size.height * 0.18f
        val cx = size.width / 2; val top = size.height * 0.17f; val r = size.height * 0.1f
        val xl = cx - r * 1.35f; val xr = cx + r * 1.35f; val y0 = size.height * 0.62f
        val y1 = y0 - dir * trav; val y2 = y0 + dir * trav
        ln(Offset(cx, 0f), Offset(cx, top - r), INK, 6f)
        ln(Offset(xl, top), Offset(xl, y1 - sz(m1))); ln(Offset(xr, top), Offset(xr, y2 - sz(m2)))
        drawCircle(INK, r, Offset(cx, top), style = Stroke(6f))
        box(Offset(xl, y1), sz(m1), sz(m1), BLUE); box(Offset(xr, y2), sz(m2), sz(m2), ORANGE)
        ground(size.height * 0.95f)
    },

    // 1 · Rampa con masa colgante
    Meta("📐", "Fricción", BLUE, "Bloque M en la rampa, masa m colgante. Si m está fuera del rango de reposo, el sistema se mueve.") { p, t ->
        val M = p["M"]!!; val th = p["th"]!!; val ms = p["mus"]!!; val mk = p["muk"]!!; val m = p["m"]!!
        val s = Math.toRadians(th)
        val lo = M * (sin(s) - ms * cos(s)); val hi = M * (sin(s) + ms * cos(s))
        val (a, dir) = when {
            m > hi -> Pair((m * G - M * G * sin(s) - mk * M * G * cos(s)) / (M + m), 1f)
            m < lo -> Pair((M * G * sin(s) - m * G - mk * M * G * cos(s)) / (M + m), -1f)
            else -> Pair(0.0, 0f)
        }
        val sl = slope(th)
        val trav = min(0.5 * max(a, 0.0) * t * t, 1.0).toFloat() * sl.len / 4f
        val d = sl.len * 0.5f + dir * trav
        val w = sz(M).coerceAtMost(sl.len * 0.2f)
        onSlope(sl, d, w, BLUE)
        val front = sl.at(d) + Offset((cos(sl.th) - sin(sl.th)) * w / 2f, -(sin(sl.th) + cos(sl.th)) * w / 2f)
        val rx = sl.c.x + 18f
        val sm = sz(m)
        val my = min(sl.c.y + size.height * 0.05f + dir * trav, size.height * 0.97f - sm)
        ln(front, sl.c, INK, 3f); ln(sl.c, Offset(rx, sl.c.y), INK, 3f); ln(Offset(rx, sl.c.y), Offset(rx, my), INK, 3f)
        drawCircle(INK, 12f, sl.c, style = Stroke(4f))
        box(Offset(rx, my + sm), sm, sm, ORANGE)
    },

    // 2 · Bloques superpuestos
    Meta("🧱", "Fricción", ORANGE, "Naranja = B (se empuja con F) · Azul = A. Si F supera la fricción máxima, A se queda atrás.") { p, t ->
        val mA = p["mA"]!!; val mB = p["mB"]!!; val ms = p["mus"]!!; val mk = p["muk"]!!; val f = p["F"]!!
        val slip = f > ms * G * (mA + mB)
        val aB = if (slip) (f - mk * mA * G) / mB else f / (mA + mB)
        val aA = if (slip) mk * G else aB
        val tt = min(t, 0.8f)
        val pxm = size.width * 0.07f
        val xa = (0.5 * aA * tt * tt).toFloat() * pxm; val xb = (0.5 * aB * tt * tt).toFloat() * pxm
        val gy = size.height * 0.78f; ground(gy)
        val bw = size.width * 0.3f; val bh = size.height * 0.14f; val x0 = size.width * 0.2f
        drawRect(ORANGE, Offset(x0 + xb, gy - bh), Size(bw, bh)); drawRect(INK, Offset(x0 + xb, gy - bh), Size(bw, bh), style = Stroke(3f))
        val aw = bw * 0.35f; val ah = size.height * 0.12f
        val ax = x0 + (bw - aw) / 2 + xa
        drawRect(BLUE, Offset(ax, gy - bh - ah), Size(aw, ah)); drawRect(INK, Offset(ax, gy - bh - ah), Size(aw, ah), style = Stroke(3f))
        arrow(Offset(x0 + xb - size.width * 0.14f, gy - bh / 2), Offset(x0 + xb, gy - bh / 2), RED)
    },

    // 3 · Polipasto
    Meta("🏗️", "Poleas", ORANGE, "Cada ramal sostiene parte de la carga. El cabo libre se mueve n veces más que la carga.") { p, t ->
        val n = p["n"]!!.toInt().coerceIn(1, 8); val a = p["a"]!!; val h = p["h"]!!
        val y = (if (a > 0.05) min(0.5 * a * t * t, h) else min(0.4 * t, h)).toFloat()
        val pxm = size.height * 0.3f; val ceil = size.height * 0.08f
        ln(Offset(size.width * 0.15f, ceil), Offset(size.width * 0.85f, ceil), INK, 8f)
        val bx = size.width * 0.3f; val bw = size.width * 0.3f; val bh = size.height * 0.14f
        val bTop = size.height * 0.5f - y * pxm
        for (i in 0 until n) {
            val x = bx + bw * (if (n == 1) 0.5f else i / (n - 1f))
            ln(Offset(x, ceil), Offset(x, bTop), BLUE, 3f)
        }
        drawRect(ORANGE, Offset(bx - 8f, bTop), Size(bw + 16f, bh)); drawRect(INK, Offset(bx - 8f, bTop), Size(bw + 16f, bh), style = Stroke(3f))
        ln(Offset(bx + bw / 2, bTop + bh), Offset(bx + bw / 2, bTop + bh + 24f))
        box(Offset(bx + bw / 2, bTop + bh + 24f + size.height * 0.16f), size.width * 0.16f, size.height * 0.16f, GREEN)
        val ex = size.width * 0.85f
        val hy = min(ceil + size.height * 0.08f + n * y * pxm * 0.4f, size.height * 0.9f)
        ln(Offset(ex, ceil), Offset(ex, hy), INK, 3f)
        arrow(Offset(ex, hy), Offset(ex, hy + size.height * 0.1f), RED)
    },

    // 4 · Hooke y M.A.S.
    Meta("🌀", "Hooke", GREEN, "Masa oscilando en cámara lenta. Línea verde = equilibrio (x = mg/k). A la derecha: posición vs. tiempo.") { p, t ->
        val m = p["m"]!!; val k = p["k"]!!; val l0 = p["L0"]!!
        val w = sqrt(k / m); val ts = t * 0.35
        val nat = size.height * 0.28f
        val eq = (m * G / k / l0 * nat).toFloat().coerceIn(size.height * 0.05f, size.height * 0.24f)
        val amp = eq * 0.5f; val top = size.height * 0.06f; val x = size.width * 0.28f
        fun yAt(tt: Double) = top + nat + eq + amp * cos(w * tt).toFloat()
        ln(Offset(x - 60f, top), Offset(x + 60f, top), INK, 8f)
        val ye = yAt(ts)
        spring(Offset(x, top), Offset(x, ye), 9, 14f)
        box(Offset(x, ye + sz(m)), sz(m), sz(m), BLUE)
        ln(Offset(x - 70f, top + nat + eq), Offset(x + 70f, top + nat + eq), GREEN, 2f)
        val gx = size.width * 0.55f; val gw = size.width * 0.4f; val base = top + nat + eq
        ln(Offset(gx, base), Offset(gx + gw, base), GREY, 2f)
        val path = Path(); var started = false
        for (i in 0..150) {
            val tt = ts - (150 - i) * 0.01
            if (tt < 0) continue
            val px = gx + gw * i / 150f; val py = yAt(tt)
            if (!started) { path.moveTo(px, py); started = true } else path.lineTo(px, py)
        }
        drawPath(path, RED, style = Stroke(4f))
    },

    // 5 · Resortes en serie en ascensor
    Meta("🛗", "Hooke", GREEN, "La cabina queda fija: solo se estiran los resortes. Línea verde = posición de la masa con el ascensor en reposo; al acelerar hacia arriba la masa baja más (F = m(g+a)).") { p, t ->
        val m = p["m"]!!; val k1 = p["k1"]!!; val k2 = p["k2"]!!; val a = p["a"]!!
        val fStatic = m * G
        val fFinal = m * (G + a)
        // La fuerza en los resortes oscila (amortiguada) desde m·g hasta m(g+a)
        val fNow = fFinal + (fStatic - fFinal) * exp(-0.7 * t) * cos(3.2 * t)
        val inv = 1.0 / k1 + 1.0 / k2
        val h = size.height
        val pxm = min(h * 0.9, h * 0.2 / (max(fStatic, fFinal) * inv * 1.25)).toFloat()
        val nat = h * 0.1f
        val e1 = (fNow / k1).toFloat() * pxm; val e2 = (fNow / k2).toFloat() * pxm
        val bx = size.width * 0.3f; val bw = size.width * 0.4f; val cx = bx + bw / 2
        val top = h * 0.07f; val bh = h * 0.87f
        // cabina fija con su cable
        ln(Offset(cx, 0f), Offset(cx, top), INK, 5f)
        drawRect(INK, Offset(bx, top), Size(bw, bh), style = Stroke(5f))
        // resortes
        val n1 = top + nat + e1; val n2 = n1 + nat + e2
        spring(Offset(cx, top), Offset(cx, n1), 6, 10f); drawCircle(INK, 6f, Offset(cx, n1))
        spring(Offset(cx, n1), Offset(cx, n2), 6, 10f)
        val bs = sz(m) * 0.9f
        // línea de referencia: posición de la masa con el ascensor en reposo
        val yRef = top + 2 * nat + (fStatic * inv).toFloat() * pxm
        ln(Offset(bx + 6f, yRef), Offset(bx + bw - 6f, yRef), GREEN, 2f)
        box(Offset(cx, n2 + bs), bs, bs, BLUE)
        if (a > 0) arrow(Offset(bx + bw + 30f, top + bh * 0.55f), Offset(bx + bw + 30f, top + bh * 0.2f), RED)
    },

    // 6 · Rampa con rozamiento (prob. 19 y 69)
    Meta("⛰️", "Energía", RED, "El bloque sube hasta detenerse (punto rojo) y vuelve a bajar. Con μk = 0 no se pierde energía.") { p, t ->
        val v0 = p["v0"]!!; val th = p["th"]!!; val mu = p["muk"]!!
        val s = Math.toRadians(th)
        val aUp = G * (sin(s) + mu * cos(s)); val d = v0 * v0 / (2 * aUp)
        val k = sin(s) - mu * cos(s); val tUp = v0 / aUp
        val dist = when {
            t <= tUp -> v0 * t - 0.5 * aUp * t * t
            k > 0 -> d - 0.5 * G * k * (t - tUp) * (t - tUp)
            else -> d
        }
        val sl = slope(th)
        val pxm = (sl.len * 0.7f / max(d, 0.05)).toFloat()
        val w = sz(2.0).coerceAtMost(sl.len * 0.12f)
        drawCircle(RED, 7f, sl.at(sl.len * 0.1f + d.toFloat() * pxm))
        onSlope(sl, sl.len * 0.1f + max(dist, 0.0).toFloat() * pxm, w, BLUE)
    },

    // 7 · Rampa curva lisa + superficie rugosa (prob. 47)
    Meta("🛝", "Energía", RED, "Cae por la curva lisa (Ep → Ec) y luego frena por el rozamiento hasta detenerse (bandera).") { p, t ->
        val h = p["h"]!!; val d = p["d"]!!
        val fl = size.height * 0.85f; val rr = size.height * 0.5f; val x0 = size.width * 0.06f
        val cx = x0 + rr; val cy = fl - rr
        val arc = Path()
        for (i in 0..30) {
            val phi = PI - PI / 2 * i / 30.0
            val px = cx + rr * cos(phi).toFloat(); val py = cy + rr * sin(phi).toFloat()
            if (i == 0) arc.moveTo(px, py) else arc.lineTo(px, py)
        }
        drawPath(arc, INK, style = Stroke(5f))
        ground(fl, cx, size.width * 0.95f)
        val t1 = sqrt(2 * h / G) * 1.5; val v = sqrt(2 * G * h); val mu = h / d; val tStop = v / (mu * G)
        val bw = size.height * 0.09f
        val pxm = ((size.width * 0.9f - cx) / max(d, 1.0)).toFloat()
        ln(Offset(cx + d.toFloat() * pxm, fl), Offset(cx + d.toFloat() * pxm, fl - 40f), RED, 4f)
        drawCircle(RED, 7f, Offset(cx + d.toFloat() * pxm, fl - 40f))
        if (t < t1) {
            val u = t / t1
            val phi = PI - PI / 2 * u * u
            val cp = Offset(cx + rr * cos(phi).toFloat(), cy + rr * sin(phi).toFloat())
            rotate(Math.toDegrees(phi - PI / 2).toFloat(), cp) {
                drawRect(BLUE, Offset(cp.x - bw / 2, cp.y - bw), Size(bw, bw))
                drawRect(INK, Offset(cp.x - bw / 2, cp.y - bw), Size(bw, bw), style = Stroke(3f))
            }
        } else {
            val tt = t - t1
            val x = if (tt <= tStop) v * tt - 0.5 * mu * G * tt * tt else d
            box(Offset(cx + x.toFloat() * pxm, fl), bw, bw, BLUE)
        }
    },

    // 8 · Dos bloques con fricción (prob. 49)
    Meta("🔗", "Energía", RED, "Azul = m1 sobre la plataforma (con fricción) · Naranja = m2 que cae. Se detiene al caer y metros.") { p, t ->
        val m1 = p["m1"]!!; val m2 = p["m2"]!!; val mk = p["muk"]!!; val yMax = max(p["y"]!!, 0.1)
        val a = max(0.0, (m2 - mk * m1) * G / (m1 + m2))
        val frac = (min(0.5 * a * t * t, yMax) / yMax).toFloat()
        val ty = size.height * 0.5f; val xr = size.width * 0.72f
        drawRect(Color(0xFFECEFF1), Offset(size.width * 0.04f, ty), Size(xr - size.width * 0.04f, size.height * 0.1f))
        ln(Offset(size.width * 0.04f, ty), Offset(xr, ty), INK, 5f)
        val bw = size.width * 0.14f; val bh = size.height * 0.16f
        val x1 = size.width * 0.12f + bw / 2 + frac * size.width * 0.38f
        box(Offset(x1, ty), bw, bh, BLUE)
        val py = ty - bh / 2
        drawCircle(INK, 10f, Offset(xr, py), style = Stroke(4f))
        ln(Offset(x1 + bw / 2, py), Offset(xr, py - 10f), INK, 3f)
        val rx = xr + 10f; val sm = sz(m2)
        val my = py + size.height * 0.06f + frac * size.height * 0.3f
        ln(Offset(rx, py), Offset(rx, my), INK, 3f)
        box(Offset(rx, my + sm), sm, sm, ORANGE)
    },

    // 9 · Persecución MRU
    Meta("🚓", "Cinemática", BLUE, "Azul = perseguidor · Naranja = perseguido. El tiempo está comprimido para que la captura (línea roja) ocurra en ~4 s.") { p, t ->
        val v1 = p["v1"]!!; val v2 = p["v2"]!!; val d0 = p["d0"]!!
        val tc = if (v1 > v2) d0 / (v1 - v2) else 10.0
        val ts = min(t * tc / 4.0, tc)
        val xCatch = d0 + v2 * tc
        val pxm = (size.width * 0.78f / max(xCatch, 1.0)).toFloat()
        val x0 = size.width * 0.08f; val l1 = size.height * 0.45f; val l2 = size.height * 0.8f
        ln(Offset(0f, l1), Offset(size.width, l1), GREY, 3f); ln(Offset(0f, l2), Offset(size.width, l2), GREY, 3f)
        val cw = size.width * 0.1f; val ch = size.height * 0.12f
        box(Offset(x0 + (v1 * ts).toFloat() * pxm, l1), cw, ch, BLUE)
        box(Offset(x0 + (d0 + v2 * ts).toFloat() * pxm, l2), cw, ch, ORANGE)
        if (v1 > v2) {
            val cxp = x0 + xCatch.toFloat() * pxm
            ln(Offset(cxp, l1 - ch - 10f), Offset(cxp, l2 + 10f), RED, 3f)
        }
    }
)
