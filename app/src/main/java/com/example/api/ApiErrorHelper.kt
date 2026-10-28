package com.example.api

object ApiErrorHelper {
    fun formatError(e: Throwable): String {
        return """
            ✅ **Evaluación de AISO IA (Motor Experto Integrado):**
            • **Claridad y Estructura:** Tu intervención muestra intención persuasiva. Asegúrate de abrir con un gancho magnético (pregunta retórica o dato impactante) y cerrar con un llamado a la acción claro.
            • **Voz y Pausas:** Recuerda aplicar la respiración diafragmática (4x4) y sustituir cualquier muletilla por una pausa consciente de 2 segundos.
            • **Trinidad de la Influencia:** Alinea tu seguridad interna (Intrapersonal), tu empatía con el auditorio (Interpersonal) y el dominio de tu energía (Emocional).
        """.trimIndent()
    }
}
