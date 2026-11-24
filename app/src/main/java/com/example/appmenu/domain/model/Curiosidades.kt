package com.example.appmenu.domain.model

/**
 * Tema de la trivia: nombre que ve el niño, articulo de Wikipedia en español
 * y las palabras que delatarian la respuesta (se ocultan en la pista).
 */
data class TemaCuriosidad(
    val nombre: String,
    val articulo: String,
    val categoria: String,
    val palabrasOcultas: List<String>
)

/** Temas y reglas del desafio. Los temas siguen los OA de Ciencias Naturales de 2.° basico. */
object Curiosidades {

    const val ANIMALES = "Animales"
    const val CUERPO = "Cuerpo humano"
    const val NATURALEZA = "Naturaleza y espacio"

    val temas: List<TemaCuriosidad> = listOf(
        // animales (con enfasis en fauna nativa de Chile)
        TemaCuriosidad("Cóndor", "Vultur_gryphus", ANIMALES, listOf("cóndor", "condor")),
        TemaCuriosidad("Pudú", "Pudu", ANIMALES, listOf("pudú", "pudu")),
        TemaCuriosidad("Huemul", "Hippocamelus_bisulcus", ANIMALES, listOf("huemul", "güemul")),
        TemaCuriosidad("Pingüino", "Spheniscus_humboldti", ANIMALES, listOf("pingüino", "pájaro niño")),
        TemaCuriosidad("Zorro culpeo", "Lycalopex_culpaeus", ANIMALES, listOf("zorro", "culpeo", "lycalopex")),
        TemaCuriosidad("Puma", "Puma_concolor", ANIMALES, listOf("puma")),
        TemaCuriosidad("Picaflor", "Trochilidae", ANIMALES, listOf("picaflor", "colibrí", "troquílido")),
        TemaCuriosidad("Ballena azul", "Balaenoptera_musculus", ANIMALES, listOf("ballena")),
        TemaCuriosidad("Abeja", "Anthophila", ANIMALES, listOf("abeja")),
        // cuerpo humano
        TemaCuriosidad("Corazón", "Corazón", CUERPO, listOf("corazón", "corazones")),
        TemaCuriosidad("Pulmón", "Pulmón", CUERPO, listOf("pulmón", "pulmones")),
        TemaCuriosidad("Cerebro", "Cerebro", CUERPO, listOf("cerebro")),
        TemaCuriosidad("Estómago", "Estómago", CUERPO, listOf("estómago")),
        TemaCuriosidad("Diente", "Diente", CUERPO, listOf("diente")),
        // naturaleza y espacio
        TemaCuriosidad("Luna", "Luna", NATURALEZA, listOf("luna")),
        TemaCuriosidad("Sol", "Sol", NATURALEZA, listOf("sol")),
        TemaCuriosidad("Arcoíris", "Arcoíris", NATURALEZA, listOf("arcoíris", "arco iris")),
        TemaCuriosidad("Volcán", "Volcán", NATURALEZA, listOf("volcán", "volcanes")),
        TemaCuriosidad("Lluvia", "Lluvia", NATURALEZA, listOf("lluvia")),
        TemaCuriosidad("Araucaria", "Araucaria_araucana", NATURALEZA, listOf("araucaria", "pehuén", "pewén"))
    )

    const val PREGUNTA = "¿De qué se trata este dato?"
    const val OCULTO = "___"
    private const val LARGO_MAXIMO = 230

    /** Primera oracion del resumen, sin el contenido entre parentesis y con un largo maximo. */
    fun primeraFrase(resumen: String): String {
        val sinParentesis = resumen.replace(Regex("\\s*\\([^)]*\\)"), "").trim()
        val frase = sinParentesis.split(Regex("(?<=\\.)\\s+")).firstOrNull().orEmpty()
        return if (frase.length <= LARGO_MAXIMO) frase else frase.take(LARGO_MAXIMO).substringBeforeLast(' ') + "…"
    }

    /** Reemplaza por "___" cada palabra que empiece con alguna de las palabras ocultas (sin distinguir mayusculas). */
    fun ocultar(texto: String, palabras: List<String>): String =
        palabras.sortedByDescending { it.length }.fold(texto) { acumulado, palabra ->
            // [\p{L}\p{N}] = letra o numero en cualquier idioma (incluye á, ñ, ü); IGNORE_CASE en Kotlin es Unicode
            Regex("(?<![\\p{L}\\p{N}])" + Regex.escape(palabra) + "[\\p{L}\\p{N}]*", RegexOption.IGNORE_CASE)
                .replace(acumulado, OCULTO)
        }

    /** Tres alternativas incorrectas de la misma categoria que el tema. */
    fun alternativasIncorrectas(tema: TemaCuriosidad, aleatorio: kotlin.random.Random): List<String> =
        temas.filter { it.categoria == tema.categoria && it.nombre != tema.nombre }
            .shuffled(aleatorio)
            .take(3)
            .map { it.nombre }
}
