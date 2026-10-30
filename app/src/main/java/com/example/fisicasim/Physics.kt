package com.example.fisicasim

import kotlin.math.*

const val G = 9.8

data class Param(val key: String, val label: String, val unit: String, val default: Double)
data class Result(val label: String, val value: String)
data class Exercise(
    val title: String,
    val statement: String,
    val params: List<Param>,
    val solve: (Map<String, Double>) -> List<Result>
)

private fun fmt(x: Double) = "%.3f".format(x)
private fun rad(deg: Double) = Math.toRadians(deg)

val exercises = listOf(
    Exercise(
        "Máquina de Atwood",
        "Dos masas cuelgan de una polea ideal. a = (m2−m1)g/(m1+m2); T = 2·m1·m2·g/(m1+m2).",
        listOf(Param("m1", "Masa 1", "kg", 15.0), Param("m2", "Masa 2", "kg", 28.0))
    ) { p ->
        val m1 = p["m1"]!!; val m2 = p["m2"]!!
        val a = abs(m2 - m1) * G / (m1 + m2)
        listOf(
            Result("Aceleración", fmt(a) + " m/s²"),
            Result("Tensión", fmt(2 * m1 * m2 * G / (m1 + m2)) + " N"),
            Result("Sentido", if (m2 > m1) "m2 baja, m1 sube" else if (m1 > m2) "m1 baja, m2 sube" else "equilibrio")
        )
    },
    Exercise(
        "Bloque en rampa con masa colgante",
        "Bloque M en rampa de ángulo θ unido por cuerda a una masa colgante m. Calcula el rango de reposo y, si se mueve, la aceleración y la tensión.",
        listOf(
            Param("M", "Masa del bloque", "kg", 8.0), Param("th", "Ángulo", "°", 35.0),
            Param("mus", "μs", "", 0.30), Param("muk", "μk", "", 0.20), Param("m", "Masa colgante", "kg", 7.5)
        )
    ) { p ->
        val M = p["M"]!!; val t = rad(p["th"]!!); val ms = p["mus"]!!; val mk = p["muk"]!!; val m = p["m"]!!
        val lo = M * (sin(t) - ms * cos(t)); val hi = M * (sin(t) + ms * cos(t))
        val r = mutableListOf(Result("Rango de reposo", "${fmt(max(lo, 0.0))} ≤ m ≤ ${fmt(hi)} kg"))
        when {
            m > hi -> {
                val a = (m * G - M * G * sin(t) - mk * M * G * cos(t)) / (M + m)
                r += Result("Movimiento", "el bloque sube, m desciende")
                r += Result("Aceleración", fmt(a) + " m/s²")
                r += Result("Tensión", fmt(m * (G - a)) + " N")
            }
            m < lo -> {
                val a = (M * G * sin(t) - m * G - mk * M * G * cos(t)) / (M + m)
                r += Result("Movimiento", "el bloque baja, m sube")
                r += Result("Aceleración", fmt(a) + " m/s²")
                r += Result("Tensión", fmt(m * (G + a)) + " N")
            }
            else -> r += Result("Estado", "en reposo")
        }
        r
    },
    Exercise(
        "Bloques superpuestos",
        "A sobre B, mesa lisa, fricción solo entre A y B. F actúa sobre B.",
        listOf(
            Param("mA", "Masa A", "kg", 2.0), Param("mB", "Masa B", "kg", 8.0),
            Param("mus", "μs", "", 0.40), Param("muk", "μk", "", 0.30), Param("F", "Fuerza F", "N", 55.0)
        )
    ) { p ->
        val mA = p["mA"]!!; val mB = p["mB"]!!; val ms = p["mus"]!!; val mk = p["muk"]!!; val F = p["F"]!!
        val fMax = ms * G * (mA + mB)
        val r = mutableListOf(Result("F máxima sin deslizar", fmt(fMax) + " N"))
        if (F <= fMax) {
            r += Result("Aceleración común", fmt(F / (mA + mB)) + " m/s²")
        } else {
            val fk = mk * mA * G
            r += Result("Fricción cinética", fmt(fk) + " N")
            r += Result("a de A", fmt(fk / mA) + " m/s²")
            r += Result("a de B", fmt((F - fk) / mB) + " m/s²")
        }
        r
    },
    Exercise(
        "Polipasto de n ramales",
        "F = M(g+a)/(η·n); el cabo libre recorre d = n·h. M = carga + bloque móvil.",
        listOf(
            Param("m", "Carga", "kg", 160.0), Param("mb", "Bloque móvil", "kg", 10.0),
            Param("n", "Ramales", "", 4.0), Param("a", "Aceleración", "m/s²", 0.0),
            Param("eta", "Eficiencia (0-1)", "", 1.0), Param("h", "Altura a elevar", "m", 0.8)
        )
    ) { p ->
        val M = p["m"]!! + p["mb"]!!; val n = p["n"]!!; val a = p["a"]!!; val eta = p["eta"]!!; val h = p["h"]!!
        val F = M * (G + a) / (eta * n)
        listOf(
            Result("Fuerza de tracción", fmt(F) + " N"),
            Result("Cuerda a recoger", fmt(n * h) + " m"),
            Result("Ventaja mecánica real", fmt(M * G / F)),
            Result("Aceleración del cabo", fmt(n * a) + " m/s²")
        )
    },
    Exercise(
        "Ley de Hooke y M.A.S.",
        "Masa colgada de un resorte: x = mg/k. Si oscila: ω = √(k/m), T = 2π/ω.",
        listOf(
            Param("m", "Masa", "kg", 2.0), Param("k", "Constante k", "N/m", 653.3), Param("L0", "Longitud natural", "m", 0.15)
        )
    ) { p ->
        val m = p["m"]!!; val k = p["k"]!!; val L0 = p["L0"]!!
        val x = m * G / k; val w = sqrt(k / m)
        listOf(
            Result("Alargamiento", fmt(x) + " m"),
            Result("Longitud final", fmt(L0 + x) + " m"),
            Result("ω", fmt(w) + " rad/s"),
            Result("Periodo", fmt(2 * PI / w) + " s"),
            Result("Frecuencia", fmt(w / (2 * PI)) + " Hz")
        )
    },
    Exercise(
        "Resortes en serie en ascensor",
        "F = m(g+a) es igual en ambos resortes; 1/keq = 1/k1 + 1/k2.",
        listOf(
            Param("m", "Masa", "kg", 6.0), Param("k1", "k1", "N/m", 400.0),
            Param("k2", "k2", "N/m", 600.0), Param("a", "Aceleración ascensor", "m/s²", 1.5)
        )
    ) { p ->
        val m = p["m"]!!; val k1 = p["k1"]!!; val k2 = p["k2"]!!; val a = p["a"]!!
        val F = m * (G + a)
        listOf(
            Result("k equivalente", fmt(k1 * k2 / (k1 + k2)) + " N/m"),
            Result("Fuerza en cada resorte", fmt(F) + " N"),
            Result("Alargamiento 1", fmt(F / k1) + " m"),
            Result("Alargamiento 2", fmt(F / k2) + " m")
        )
    },
    Exercise(
        "Rampa con rozamiento (prob. 19 y 69)",
        "Bloque lanzado hacia arriba por una rampa. Con μk = 0 es el problema 19 (conservación de energía).",
        listOf(
            Param("m", "Masa", "kg", 2.0), Param("v0", "Velocidad inicial", "m/s", 3.0),
            Param("th", "Ángulo", "°", 60.0), Param("muk", "μk", "", 0.3)
        )
    ) { p ->
        val m = p["m"]!!; val v0 = p["v0"]!!; val t = rad(p["th"]!!); val mu = p["muk"]!!
        val d = v0 * v0 / (2 * G * (sin(t) + mu * cos(t)))
        val k = sin(t) - mu * cos(t)
        listOf(
            Result("Altura alcanzada", fmt(d * sin(t)) + " m"),
            Result("Distancia por la rampa", fmt(d) + " m"),
            Result("Energía disipada (subida)", fmt(mu * m * G * cos(t) * d) + " J"),
            Result("Velocidad al volver", if (k > 0) fmt(sqrt(2 * G * k * d)) + " m/s" else "no regresa (se queda)")
        )
    },
    Exercise(
        "Rampa curva lisa + superficie rugosa (prob. 47)",
        "Bloque parte del reposo a altura h y se detiene tras recorrer d sobre la superficie rugosa.",
        listOf(Param("m", "Masa", "kg", 2.0), Param("h", "Altura", "m", 3.0), Param("d", "Distancia rugosa", "m", 9.0))
    ) { p ->
        val m = p["m"]!!; val h = p["h"]!!; val d = p["d"]!!
        listOf(
            Result("Velocidad al pie de la rampa", fmt(sqrt(2 * G * h)) + " m/s"),
            Result("Energía disipada", fmt(m * G * h) + " J"),
            Result("Coeficiente μ", fmt(h / d))
        )
    },
    Exercise(
        "Dos bloques con fricción (prob. 49)",
        "m1 sobre la plataforma (μk), m2 cuelga y cae una distancia y. E = −μk·m1·g·y con E inicial = 0.",
        listOf(
            Param("m1", "Bloque sobre plataforma", "kg", 4.0), Param("m2", "Bloque colgante", "kg", 2.0),
            Param("muk", "μk", "", 0.35), Param("y", "Caída y", "m", 2.0)
        )
    ) { p ->
        val m1 = p["m1"]!!; val m2 = p["m2"]!!; val mk = p["muk"]!!; val y = p["y"]!!
        val ek = (m2 - mk * m1) * G * y
        listOf(
            Result("Energía disipada", fmt(mk * m1 * G * y) + " J"),
            Result("Energía mecánica E", fmt(-mk * m1 * G * y) + " J"),
            Result("Velocidad", if (ek > 0) fmt(sqrt(2 * ek / (m1 + m2))) + " m/s" else "no se mueve")
        )
    },
    Exercise(
        "Curva peraltada",
        "v máx = √( r·g·(sinθ + μs·cosθ) / (cosθ − μs·sinθ) ).",
        listOf(Param("r", "Radio", "m", 50.0), Param("th", "Ángulo", "°", 15.0), Param("mus", "μs", "", 0.5))
    ) { p ->
        val r = p["r"]!!; val t = rad(p["th"]!!); val ms = p["mus"]!!
        val den = cos(t) - ms * sin(t)
        listOf(
            Result("v máxima", if (den > 0) fmt(sqrt(r * G * (sin(t) + ms * cos(t)) / den)) + " m/s" else "sin límite (no desliza)"),
            Result("Sin fricción", fmt(sqrt(r * G * tan(t))) + " m/s"),
            Result("Calzada plana", fmt(sqrt(r * G * ms)) + " m/s")
        )
    },
    Exercise(
        "Persecución (MRU)",
        "El perseguidor parte a d0 metros detrás. Igualando tiempos: t = d0/(v1−v2).",
        listOf(Param("v1", "Velocidad perseguidor", "m/s", 12.0), Param("v2", "Velocidad perseguido", "m/s", 10.0), Param("d0", "Ventaja inicial", "m", 20.0))
    ) { p ->
        val v1 = p["v1"]!!; val v2 = p["v2"]!!; val d0 = p["d0"]!!
        if (v1 <= v2) listOf(Result("Resultado", "nunca lo alcanza"))
        else {
            val t = d0 / (v1 - v2)
            listOf(
                Result("Tiempo", fmt(t) + " s"),
                Result("Recorre el perseguido", fmt(v2 * t) + " m"),
                Result("Recorre el perseguidor", fmt(v1 * t) + " m")
            )
        }
    }
)
