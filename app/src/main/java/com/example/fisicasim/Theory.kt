package com.example.fisicasim

data class Topic(
    val title: String,
    val description: String,
    val sections: List<InfoSection>,
    val formulas: List<String>,
    val videoId: String? = null
)

val topics = listOf(
    Topic(
        "Leyes de Newton",
        "Tres principios que relacionan las fuerzas que actúan sobre un cuerpo con su movimiento.",
        listOf(
            InfoSection("1ª ley (inercia)", "Un cuerpo mantiene su reposo o su movimiento rectilíneo uniforme si la fuerza neta sobre él es cero."),
            InfoSection("2ª ley", "La fuerza neta y la aceleración tienen la misma dirección (es una ecuación vectorial). En general, F = dp/dt."),
            InfoSection("3ª ley (acción y reacción)", "A toda acción corresponde una reacción igual y opuesta, aplicadas a cuerpos distintos.")
        ),
        listOf("ΣF = 0  →  a = 0", "ΣF = m·a", "W = m·g   (g = 9,8 m/s²)"),
        "DAtcTgJB66s"
    ),
    Topic(
        "Diagrama de cuerpo libre (DCL)",
        "Esquema que aísla un cuerpo y muestra todas las fuerzas que actúan sobre él, para plantear las ecuaciones de Newton.",
        listOf(
            InfoSection("Pasos", "1. Aislar el cuerpo.\n2. Elegir ejes y el sentido de la aceleración.\n3. Dibujar las fuerzas.\n4. Plantear las ecuaciones en cada eje."),
            InfoSection("Fuerzas típicas", "Peso (hacia abajo), normal (perpendicular a la superficie), tensión (se aleja del cuerpo a lo largo de la cuerda), fricción (opuesta al movimiento) y fuerza aplicada."),
            InfoSection("Regla de signos", "La fuerza a favor de la aceleración es positiva; la opuesta es negativa.")
        ),
        listOf("ΣFx = m·ax", "ΣFy = m·ay")
    ),
    Topic(
        "Fricción y planos inclinados",
        "Estudia cómo la fricción y la inclinación de una rampa determinan si un cuerpo se queda quieto o se mueve.",
        listOf(
            InfoSection("Fuerzas en la rampa", "La normal es menor que el peso, y el peso se descompone en una parte a lo largo de la rampa y otra perpendicular a ella."),
            InfoSection("Tipos de fricción", "La estática evita que empiece el movimiento. La cinética actúa mientras el cuerpo se desliza."),
            InfoSection("Con masa colgante", "El reposo se mantiene solo para un rango de valores de la masa colgante m."),
            InfoSection("Subir y bajar", "Al subir, la fricción ayuda a frenar el cuerpo; al bajar, se opone a su movimiento.")
        ),
        listOf(
            "N = m·g·cosθ",
            "Peso a lo largo de la rampa = m·g·senθ",
            "Estática: f ≤ μs·N\nCinética: fk = μk·N",
            "Reposo: M(senθ − μs·cosθ) ≤ m ≤ M(senθ + μs·cosθ)",
            "Subiendo: a = g(senθ + μk·cosθ)",
            "Bajando: a = g(senθ − μk·cosθ)"
        )
    ),
    Topic(
        "Poleas y máquina de Atwood",
        "Las poleas cambian la dirección de la fuerza y, si son móviles, la reducen. La máquina de Atwood es el caso básico con dos masas.",
        listOf(
            InfoSection("Cuerda ideal", "Tiene la misma tensión en toda su longitud. Cuerdas distintas pueden tener tensiones distintas."),
            InfoSection("Polea móvil", "Con n ramales, la fuerza necesaria se divide entre n, pero el cabo recorre n veces más distancia."),
            InfoSection("Eficiencia", "Si la eficiencia η es menor que 1, hace falta más fuerza por las pérdidas.")
        ),
        listOf(
            "Atwood: a = (m2 − m1)·g / (m1 + m2)",
            "Atwood: T = 2·m1·m2·g / (m1 + m2)",
            "F = M(g + a) / n",
            "d = n·h     a del cabo = n·a",
            "Con eficiencia: F = M·g / (η·n)"
        )
    ),
    Topic(
        "Ley de Hooke",
        "Describe la fuerza de un resorte, que es proporcional a cuánto se estira o se comprime.",
        listOf(
            InfoSection("Alargamiento", "x = Lf − L0, en metros, y k en N/m. La ley es válida hasta el límite elástico."),
            InfoSection("Asociación de resortes", "En serie: misma fuerza y se suman los alargamientos. En paralelo: mismo desplazamiento y se suman las fuerzas."),
            InfoSection("Masa colgada", "En equilibrio el resorte soporta el peso. En un ascensor que acelera, la fuerza cambia.")
        ),
        listOf(
            "F = k·x",
            "Fuerza recuperadora: F = −k·x",
            "Serie: 1/keq = 1/k1 + 1/k2",
            "Paralelo: keq = k1 + k2",
            "Masa colgada: x = m·g / k",
            "Ascensor: F = m(g + a)"
        )
    ),
    Topic(
        "Movimiento armónico simple",
        "Oscilación en la que la fuerza recuperadora es proporcional al desplazamiento, como una masa en un resorte o un péndulo con ángulos pequeños.",
        listOf(
            InfoSection("Posición, velocidad y aceleración", "La velocidad es máxima en x = 0 y la aceleración es máxima en x = ±A."),
            InfoSection("Péndulo", "Para ángulos pequeños, el periodo no depende de la masa.")
        ),
        listOf(
            "x(t) = A·cos(ωt + δ)",
            "v = −A·ω·sen(ωt + δ)",
            "a = −ω²·x",
            "ω = √(k/m)     T = 2π/ω     f = 1/T",
            "v máx = A·ω     a máx = A·ω²",
            "Péndulo: T = 2π√(L/g)"
        )
    ),
    Topic(
        "Cinemática",
        "Describe el movimiento sin considerar sus causas: posición, velocidad y aceleración en función del tiempo.",
        listOf(
            InfoSection("MRU y MRUV", "En el MRU la velocidad es constante. En el MRUV la aceleración es constante."),
            InfoSection("Cómo elegir la ecuación", "Se elige según la variable que falta. Hay que unificar unidades (36 km/h = 10 m/s)."),
            InfoSection("Proyectiles", "El movimiento horizontal es MRU y el vertical tiene a = −g; ambos son independientes.")
        ),
        listOf(
            "MRU: x = x0 + v·t",
            "MRUV: v = v0 + a·t",
            "Δx = v0·t + ½·a·t²",
            "v² = v0² + 2a·Δx",
            "Δx = ½(v0 + v)·t"
        )
    ),
    Topic(
        "Trabajo y energía",
        "Relaciona el trabajo de las fuerzas con los cambios de energía de un cuerpo.",
        listOf(
            InfoSection("Conservación", "Sin fricción, Ec + Ep se conserva. Con fricción, parte de la energía se disipa."),
            InfoSection("Resorte en rampa", "Un bloque que comprime un resorte en una rampa iguala su energía potencial gravitatoria con la elástica.")
        ),
        listOf(
            "W = F·d·cosφ",
            "Ec = ½·m·v²",
            "Ep gravitatoria = m·g·h",
            "Ep elástica = ½·k·x²",
            "Con fricción: E final = E inicial − fk·d",
            "Resorte en rampa: m·g(h + x·senθ) = ½·k·x²"
        )
    ),
    Topic(
        "Movimiento circular y curva peraltada",
        "Cuando un cuerpo describe una curva necesita una fuerza dirigida al centro, llamada fuerza centrípeta.",
        listOf(
            InfoSection("Curva peraltada", "La componente de la normal hacia el centro, junto con la fricción, aporta la fuerza centrípeta; en vertical hay equilibrio."),
            InfoSection("Casos particulares", "Sin fricción, el peralte fija una velocidad ideal. En calzada plana, la fricción es la única fuerza centrípeta.")
        ),
        listOf(
            "ac = v² / r",
            "N·senθ + μs·N·cosθ = m·v²/r",
            "N·cosθ − μs·N·senθ = m·g",
            "v máx = √( r·g(senθ + μs·cosθ) / (cosθ − μs·senθ) )",
            "Sin fricción: v = √(r·g·tanθ)",
            "Plana: v = √(r·g·μs)"
        )
    )
)
