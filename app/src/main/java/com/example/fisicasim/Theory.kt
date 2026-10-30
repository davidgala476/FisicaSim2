package com.example.fisicasim

data class Topic(val title: String, val body: String)

val topics = listOf(
    Topic(
        "Leyes de Newton",
        "1ª (inercia): un cuerpo mantiene su reposo o MRU si ΣF = 0.\n" +
            "2ª: ΣF = m·a (ecuación vectorial; F y a tienen la misma dirección). En general F = dp/dt.\n" +
            "3ª: a toda acción corresponde una reacción igual y opuesta, aplicadas a cuerpos distintos.\n" +
            "Peso: W = m·g (g = 9,8 m/s²)."
    ),
    Topic(
        "Diagrama de cuerpo libre (DCL)",
        "1. Aislar el cuerpo.  2. Elegir ejes y el sentido de a.  3. Dibujar fuerzas: peso (abajo), normal (⊥ a la superficie), " +
            "tensión (se aleja del cuerpo a lo largo de la cuerda), fricción (opuesta al movimiento), fuerza aplicada.  " +
            "4. Plantear ΣFx = m·ax y ΣFy = m·ay.\n" +
            "Regla de signos: la fuerza a favor de a es positiva, la opuesta es negativa."
    ),
    Topic(
        "Fricción y planos inclinados",
        "Normal en rampa: N = m·g·cosθ.  Componente del peso a lo largo: m·g·senθ.\n" +
            "Fricción estática: f ≤ μs·N.  Cinética: fk = μk·N.\n" +
            "Equilibrio con masa colgante m: M(senθ − μs·cosθ) ≤ m ≤ M(senθ + μs·cosθ).\n" +
            "Aceleración al subir con fricción: a = g(senθ + μk·cosθ) (frena); al bajar: a = g(senθ − μk·cosθ)."
    ),
    Topic(
        "Poleas y máquina de Atwood",
        "Cuerda ideal: misma tensión en toda la cuerda. Atwood: a = (m2 − m1)g/(m1 + m2); T = 2·m1·m2·g/(m1 + m2).\n" +
            "Polea móvil con n ramales: F = M(g + a)/n; el cabo recorre d = n·h y su aceleración es n·a.\n" +
            "Con eficiencia η: F = M·g/(η·n). Cuerdas distintas pueden tener tensiones distintas."
    ),
    Topic(
        "Ley de Hooke",
        "F = k·x (x = alargamiento Lf − L0, en metros; k en N/m). Fuerza recuperadora: F = −k·x.\n" +
            "Válida hasta el límite elástico. Serie: 1/keq = 1/k1 + 1/k2 (misma fuerza). Paralelo: keq = k1 + k2 (mismo desplazamiento).\n" +
            "Colgando una masa: x = m·g/k; en un ascensor que acelera: F = m(g + a)."
    ),
    Topic(
        "Movimiento armónico simple",
        "x(t) = A·cos(ωt + δ);  v = −Aω·sen(ωt + δ);  a = −ω²·x.\n" +
            "ω = √(k/m);  T = 2π/ω;  f = 1/T;  v máx = Aω (en x = 0);  a máx = Aω² (en x = ±A).\n" +
            "Péndulo (ángulos pequeños): T = 2π√(L/g), independiente de la masa."
    ),
    Topic(
        "Cinemática",
        "MRU: x = x0 + v·t.   MRUV: v = v0 + a·t;  Δx = v0·t + ½·a·t²;  v² = v0² + 2a·Δx;  Δx = ½(v0 + v)·t.\n" +
            "Elegir la ecuación según la variable que falta. Unificar unidades (36 km/h = 10 m/s).\n" +
            "Proyectiles: horizontal MRU, vertical con a = −g, ambos independientes."
    ),
    Topic(
        "Trabajo y energía",
        "W = F·d·cosφ.  Ec = ½·m·v².  Ep gravitatoria = m·g·h.  Ep elástica = ½·k·x².\n" +
            "Sin fricción: Ec + Ep se conserva. Con fricción: E final = E inicial − fk·d.\n" +
            "Bloque contra resorte en rampa: m·g(h + x·senθ) = ½·k·x²."
    ),
    Topic(
        "Movimiento circular y curva peraltada",
        "Aceleración centrípeta: ac = v²/r.  En una curva peraltada: N·senθ + μs·N·cosθ = m·v²/r y N·cosθ − μs·N·senθ = m·g.\n" +
            "v máx = √( r·g(senθ + μs·cosθ) / (cosθ − μs·senθ) );  sin fricción: √(r·g·tanθ);  plana: √(r·g·μs)."
    )
)
