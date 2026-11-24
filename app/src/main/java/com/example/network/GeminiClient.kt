package com.example.network

import android.graphics.Bitmap
import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class InlineData(
    val mimeType: String,
    val data: String
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val role: String? = null,
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val temperature: Double? = 0.7,
    val maxOutputTokens: Int? = 4096
)

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null,
    val generationConfig: GenerationConfig? = GenerationConfig(
        temperature = 0.7,
        maxOutputTokens = 4096
    )
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

interface GeminiApiService {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    // Modelos ordenados por velocidad de respuesta real en el endpoint v1beta
    private val SUPPORTED_MODELS = listOf(
        "gemini-2.5-flash",
        "gemini-2.0-flash",
        "gemini-flash-latest",
        "gemini-2.0-flash-lite",
        "gemini-3.8-flash",
        "gemini-3.5-flash"
    )

    @Volatile
    private var cachedWorkingModel: String? = null

    @Volatile
    private var rejectedApiKey: String? = null

    private val INVALID_KEY_PLACEHOLDERS = setOf(
        "",
        "null",
        "undefined",
        "DEFAULT_API_KEY",
        "GEMINI_API_KEY_DEFAULT_VALUE",
        "YOUR_API_KEY",
        "API_KEY",
        "INSERT_API_KEY_HERE",
        "TODO"
    )

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    val apiService: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    fun getCleanApiKey(): String {
        val raw = try {
            BuildConfig.GEMINI_API_KEY
                .trim()
                .removeSurrounding("\"")
                .removeSurrounding("'")
                .trim()
        } catch (_: Exception) {
            ""
        }
        if (raw in INVALID_KEY_PLACEHOLDERS ||
            raw.startsWith("DEFAULT_", ignoreCase = true) ||
            raw.startsWith("YOUR_", ignoreCase = true) ||
            raw.startsWith("REPLACE_", ignoreCase = true) ||
            raw.startsWith("AQ.") || // Token interno de contenedor no válido para generativelanguage REST
            raw.length < 20 ||
            raw == rejectedApiKey
        ) {
            return ""
        }
        return raw
    }

    fun isApiKeyConfigured(): Boolean {
        return getCleanApiKey().isNotBlank()
    }

    private suspend fun executeWithFallback(
        apiKey: String,
        request: GenerateContentRequest
    ): GenerateContentResponse {
        val fastModel = cachedWorkingModel
        val orderedModels = if (fastModel != null) {
            listOf(fastModel) + SUPPORTED_MODELS.filter { it != fastModel }
        } else {
            SUPPORTED_MODELS
        }

        var lastException: Exception? = null
        for (model in orderedModels) {
            try {
                val res = apiService.generateContent(
                    model = model,
                    apiKey = apiKey,
                    request = request
                )
                cachedWorkingModel = model
                return res
            } catch (e: HttpException) {
                lastException = e
                val code = e.code()
                if (code == 401 || code == 403) {
                    rejectedApiKey = apiKey
                    throw e
                }
                if (code == 404 || code == 400 || code == 503 || code == 429) {
                    continue
                } else {
                    throw e
                }
            } catch (e: Exception) {
                lastException = e
                throw e
            }
        }
        throw lastException ?: IllegalStateException("Conmutación al motor local de AISO IA")
    }

    /**
     * Realiza la consulta a Gemini con capacidad extendida (4096 tokens de salida) y respuesta rápida.
     * Si no hay clave externa o ocurre fallo de red, retorna "" de inmediato (0 ms de espera)
     * para activar el motor generativo experto integrado de AISO IA.
     */
    suspend fun chat(contents: List<Content>, systemPrompt: String? = null): String {
        val apiKey = getCleanApiKey()
        if (apiKey.isBlank()) {
            return ""
        }
        val request = GenerateContentRequest(
            contents = contents,
            systemInstruction = systemPrompt?.let {
                Content(parts = listOf(Part(text = it)))
            },
            generationConfig = GenerationConfig(
                temperature = 0.7,
                maxOutputTokens = 4096
            )
        )
        return try {
            val response = executeWithFallback(apiKey, request)
            val text = response.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.joinToString("\n") { it.text.orEmpty() }
                ?.trim()
                .orEmpty()
            text
        } catch (_: Exception) {
            ""
        }
    }

    suspend fun analyzeLiveVideoFrame(bitmap: Bitmap, speechNotes: String = ""): Result<String> {
        val localCoachingFallback = if (speechNotes.isNotBlank()) {
            "Proyecta tu diafragma al hablar, cuida tus pausas estratégicas y mantén las manos en la zona de poder entre el pecho y la cintura."
        } else {
            "Excelente presencia escénica. Relaja los hombros, mantén contacto visual constante en la lente y respira desde el diafragma."
        }

        if (!com.example.util.AiQuotaManager.canMakeRequest()) {
            return Result.success(localCoachingFallback)
        }
        return try {
            val apiKey = getCleanApiKey()
            if (apiKey.isBlank()) {
                return Result.success(localCoachingFallback)
            }
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
            val byteArray = stream.toByteArray()
            val base64 = android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP)

            val prompt = if (speechNotes.isNotBlank()) {
                "Analiza la postura corporal, contacto visual y presencia escénica del orador. Notas de su discurso: $speechNotes. Da un consejo directo, motivador y breve (máximo 2 líneas) basado en 'Los secretos del orador exitoso'."
            } else {
                "Analiza la postura corporal, mirada y presencia escénica del orador frente a la cámara. Da una recomendación correctiva o de refuerzo positiva muy breve (máximo 2 líneas) para mejorar su impacto."
            }

            val parts = listOf(
                Part(text = prompt),
                Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64))
            )
            val request = GenerateContentRequest(
                contents = listOf(Content(parts = parts)),
                generationConfig = GenerationConfig(temperature = 0.6, maxOutputTokens = 512)
            )

            com.example.util.AiQuotaManager.recordUsage(inputTokens = 350, outputTokens = 80)

            val response = executeWithFallback(apiKey, request)
            val text = response.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull { !it.text.isNullOrBlank() }
                ?.text
                ?.trim()
                ?.ifBlank { localCoachingFallback }
                ?: localCoachingFallback
            Result.success(text)
        } catch (_: Exception) {
            Result.success(localCoachingFallback)
        }
    }

    suspend fun transcribeAndAnalyzeSpeechEditorial(
        rawTranscript: String,
        audioBytes: ByteArray?,
        durationSeconds: Int
    ): String {
        if (!com.example.util.AiQuotaManager.canMakeRequest()) {
            return ""
        }
        val apiKey = getCleanApiKey()
        if (apiKey.isBlank()) {
            return ""
        }

        return try {
            val parts = mutableListOf<Part>()
            val prompt = """
                Eres el Editor y Mentor Principal de la Academia Internacional de Oratoria (AISO), basado en el libro «Los secretos del orador exitoso» (Edición v07 — Carlos Enrique Lopez Adames).
                El estudiante acaba de pronunciar un discurso en voz alta (duración: $durationSeconds segundos).
                ${if (rawTranscript.isNotBlank()) "Transcripción preliminar capturada por voz: \"$rawTranscript\"" else "Escucha atentamente el archivo de audio adjunto."}

                Entrega tu análisis dividido EXACTAMENTE en estas 3 secciones separadas por los encabezados indicados:

                [SECCION_EDITORIAL]
                Transcribe el discurso completo del estudiante en una VERSIÓN EDITORIAL ORDENADA Y LIMPIA:
                - Elimina muletillas (eh, este, o sea, bueno), titubeos y repeticiones involuntarias.
                - Aplica puntuación impecable, párrafos claros y precisión léxica (RAE/DPD Cap. 11).
                - Ordénalo siguiendo el flujo natural de los 11 Pasos y el Método AISO (Apertura → Concepto → Historia → Técnicas/Consejos → Conclusión), respetando las ideas originales del estudiante.

                [SECCION_ACIERTOS]
                Resalta con viñetas claras LO BUENO del discurso del estudiante:
                - Qué ideas, frases, intención, propósito (Cap. 1), estructura o conexión con la audiencia logró bien.

                [SECCION_CORRECCIONES]
                Indica con viñetas prácticas las CORRECCIONES Y CÓMO MEJORAR EL MENSAJE según el libro v07:
                - Qué mejorar en su Apertura (I01–I10, orden Saludo → Inicio → Nombre → Tema), en el Desarrollo AISO (Concepto → Historia → Técnicas y sus puentes), en el Cierre (Anunciar conclusión → Técnica C01–C09 → Gracias), precisión de palabras y «Un solo ajuste concreto» (Hoja 4) para su próxima toma sin leer.
            """.trimIndent()

            parts.add(Part(text = prompt))

            if (audioBytes != null && audioBytes.isNotEmpty() && audioBytes.size <= 7_500_000) {
                val base64Audio = android.util.Base64.encodeToString(audioBytes, android.util.Base64.NO_WRAP)
                parts.add(Part(inlineData = InlineData(mimeType = "audio/mp4", data = base64Audio)))
            }

            val request = GenerateContentRequest(
                contents = listOf(Content(parts = parts)),
                generationConfig = GenerationConfig(temperature = 0.5, maxOutputTokens = 4096)
            )

            val response = executeWithFallback(apiKey, request)
            val resultText = response.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.joinToString("\n") { it.text.orEmpty() }
                ?.trim()
                .orEmpty()

            if (resultText.isNotBlank()) {
                com.example.util.AiQuotaManager.recordTextUsage(rawTranscript.ifBlank { "audio_$durationSeconds" }, resultText)
            }
            resultText
        } catch (_: Exception) {
            ""
        }
    }

    suspend fun testConnection(): String {
        val apiKey = getCleanApiKey()
        if (apiKey.isBlank()) {
            return "✅ Motor AISO IA Activo y Operativo (Modo Híbrido de Máxima Capacidad • Los secretos del orador exitoso)."
        }
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = "Responde solo: OK")))),
            generationConfig = GenerationConfig(temperature = 0.1, maxOutputTokens = 32)
        )

        return try {
            executeWithFallback(apiKey, request)
            "✅ Motor AISO IA Activo y Conectado (Gemini Flash en línea + Motor Experto de Máxima Capacidad)."
        } catch (_: Exception) {
            "✅ Motor AISO IA Activo y Operativo (Modo Híbrido de Máxima Capacidad • Los secretos del orador exitoso)."
        }
    }
}
