package com.example.fisicasim

/** Un apartado explicativo (título + texto). */
data class InfoSection(val title: String, val text: String)

/** Información que se muestra DENTRO de cada simulador (mismo orden que `exercises`). */
data class ExerciseInfo(val description: String, val sections: List<InfoSection>, val formulas: List<String>)

val exerciseInfos: List<ExerciseInfo> = listOf(
    // 0 · Atwood
    ExerciseInfo(
        "Dos masas cuelgan de los extremos de una cuerda que pasa por una polea ideal (sin masa ni rozamiento). La masa mayor baja y la menor sube con la misma aceleración.",
        listOf(
            InfoSection("Idea clave", "Como la cuerda es ideal, la tensión es la misma a ambos lados y las dos masas tienen igual aceleración. Se aplica la 2ª ley de Newton a cada masa."),
            InfoSection("Qué cambia", "Si m1 = m2 hay equilibrio. Cuanto mayor es la diferencia de masas, mayor es la aceleración, que siempre es menor que g.")
        ),
        listOf("a = (m2 − m1)·g / (m1 + m2)", "T = 2·m1·m2·g / (m1 + m2)", "g = 9,8 m/s²")
    ),
    // 1 · Rampa con masa colgante
    ExerciseInfo(
        "Un bloque M sobre un plano inclinado con fricción está unido, por una cuerda y una polea, a una masa m que cuelga. Según el valor de m, el sistema se queda quieto o se mueve.",
        listOf(
            InfoSection("Idea clave", "Primero se calcula el rango de masas colgantes que mantiene el reposo (fricción estática). Fuera de ese rango se usa la fricción cinética para hallar la aceleración y la tensión."),
            InfoSection("Casos", "Si m supera el límite superior, m baja y el bloque sube. Si es menor que el límite inferior, el bloque baja y m sube.")
        ),
        listOf(
            "N = M·g·cosθ",
            "Reposo: M(senθ − μs·cosθ) ≤ m ≤ M(senθ + μs·cosθ)",
            "m baja: a = (m·g − M·g·senθ − μk·M·g·cosθ)/(M + m)\nT = m(g − a)",
            "m sube: a = (M·g·senθ − m·g − μk·M·g·cosθ)/(M + m)\nT = m(g + a)"
        )
    ),
    // 2 · Bloques superpuestos
    ExerciseInfo(
        "El bloque A está sobre el bloque B y B se apoya en una mesa lisa. Se empuja B con una fuerza F y solo hay fricción entre A y B.",
        listOf(
            InfoSection("Idea clave", "Mientras la fricción estática alcanza, A y B se mueven juntos con la misma aceleración. Si F supera el máximo, A resbala sobre B y aparece fricción cinética."),
            InfoSection("Por qué A se queda atrás", "La única fuerza horizontal sobre A es la fricción, así que su aceleración nunca supera la que esa fricción puede darle.")
        ),
        listOf(
            "F máx sin deslizar = μs·g·(mA + mB)",
            "Juntos: a = F / (mA + mB)",
            "Deslizan: fk = μk·mA·g\naA = fk / mA\naB = (F − fk) / mB"
        )
    ),
    // 3 · Polipasto
    ExerciseInfo(
        "Sistema de poleas con n ramales de cuerda que sostienen la carga. Permite levantar pesos grandes con una fuerza menor, a cambio de recoger más cuerda.",
        listOf(
            InfoSection("Idea clave", "Cada ramal sostiene una parte de la carga: con n ramales la fuerza se reparte entre n."),
            InfoSection("El compromiso", "Lo que se gana en fuerza se paga en distancia: el cabo libre recorre n veces la altura que sube la carga.")
        ),
        listOf(
            "F = M(g + a) / (η·n)",
            "d = n·h",
            "Ventaja mecánica real = M·g / F",
            "a del cabo = n·a",
            "M = carga + bloque móvil"
        )
    ),
    // 4 · Hooke y MAS
    ExerciseInfo(
        "Una masa colgada de un resorte lo estira hasta quedar en equilibrio. Si se la suelta desde otra posición, oscila con movimiento armónico simple.",
        listOf(
            InfoSection("Idea clave", "En el equilibrio, la fuerza del resorte iguala al peso. La oscilación ocurre alrededor de esa posición y su periodo depende de m y k, no de la amplitud.")
        ),
        listOf("x = m·g / k", "L final = L0 + x", "ω = √(k/m)", "T = 2π / ω", "f = 1 / T")
    ),
    // 5 · Resortes en serie en ascensor
    ExerciseInfo(
        "Dos resortes unidos en serie sostienen una masa dentro de un ascensor que acelera hacia arriba, por lo que se estiran más que en reposo.",
        listOf(
            InfoSection("Idea clave", "En serie, los dos resortes soportan la misma fuerza y sus alargamientos se suman. La aceleración del ascensor aumenta la fuerza que deben soportar (peso aparente).")
        ),
        listOf("F = m(g + a)", "1/keq = 1/k1 + 1/k2\nkeq = k1·k2 / (k1 + k2)", "x1 = F / k1\nx2 = F / k2")
    ),
    // 6 · Rampa con rozamiento
    ExerciseInfo(
        "Un bloque es lanzado hacia arriba por una rampa y se detiene tras recorrer cierta distancia. Con μk = 0 la energía mecánica se conserva; con fricción, parte se disipa.",
        listOf(
            InfoSection("Idea clave", "Se usa trabajo y energía: la energía cinética inicial se convierte en energía potencial y en calor por fricción."),
            InfoSection("El regreso", "El bloque vuelve a bajar solo si senθ > μk·cosθ; si no, se queda detenido en la rampa.")
        ),
        listOf(
            "d = v0² / (2g(senθ + μk·cosθ))",
            "h = d·senθ",
            "E disipada = μk·m·g·cosθ·d",
            "v al volver = √(2g(senθ − μk·cosθ)·d)"
        )
    ),
    // 7 · Rampa curva + superficie rugosa
    ExerciseInfo(
        "Un bloque parte del reposo a una altura h, baja por una curva lisa y luego se frena sobre una superficie rugosa horizontal hasta detenerse.",
        listOf(
            InfoSection("Idea clave", "En la curva lisa la energía potencial se convierte en cinética. En la zona rugosa toda esa energía se disipa por fricción, y de ahí se obtiene μ.")
        ),
        listOf("v = √(2·g·h)", "E disipada = m·g·h", "μk·m·g·d = m·g·h\nμk = h / d")
    ),
    // 8 · Dos bloques con fricción
    ExerciseInfo(
        "Un bloque m1 sobre una plataforma con fricción está unido por una cuerda a un bloque m2 que cuelga y cae una distancia y.",
        listOf(
            InfoSection("Idea clave", "La energía que pierde m2 al caer se reparte entre la energía cinética de ambos bloques y la energía disipada por la fricción.")
        ),
        listOf(
            "E disipada = μk·m1·g·y",
            "E mecánica = −μk·m1·g·y   (E inicial = 0)",
            "½(m1 + m2)·v² = (m2 − μk·m1)·g·y"
        )
    ),
    // 9 · Curva peraltada
    ExerciseInfo(
        "Un auto toma una curva circular con la calzada inclinada (peralte). La fricción estática y la componente de la normal aportan la fuerza centrípeta.",
        listOf(
            InfoSection("Idea clave", "Se descomponen las fuerzas en horizontal (hacia el centro de la curva) y vertical (equilibrio). De ahí sale la velocidad máxima sin derrapar."),
            InfoSection("Casos particulares", "Sin fricción, el peralte fija una velocidad ideal. En calzada plana, la fricción es la única fuerza centrípeta.")
        ),
        listOf(
            "N·senθ + μs·N·cosθ = m·v²/r",
            "N·cosθ − μs·N·senθ = m·g",
            "v máx = √( r·g(senθ + μs·cosθ) / (cosθ − μs·senθ) )",
            "Sin fricción: v = √(r·g·tanθ)",
            "Calzada plana: v = √(r·g·μs)"
        )
    ),
    // 10 · Persecución
    ExerciseInfo(
        "Un perseguidor sale detrás de otro objeto que ya lleva una ventaja de d0 metros. Ambos se mueven con velocidad constante (MRU).",
        listOf(
            InfoSection("Idea clave", "Se alcanzan cuando sus posiciones coinciden: el perseguidor debe recuperar d0 usando la diferencia de velocidades."),
            InfoSection("Condición", "Solo hay alcance si v1 > v2; si no, nunca lo alcanza.")
        ),
        listOf("x1 = v1·t\nx2 = d0 + v2·t", "t = d0 / (v1 − v2)")
    )
)
