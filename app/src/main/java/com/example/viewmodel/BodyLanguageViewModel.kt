package com.example.viewmodel

import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.api.Content
import com.example.api.GenerateContentRequest
import com.example.api.InlineData
import com.example.api.Part
import com.example.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class BodyLanguageViewModel : ViewModel() {
    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _feedback = MutableStateFlow<String?>(null)
    val feedback: StateFlow<String?> = _feedback.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun analyzeFrame(bitmap: Bitmap) {
        if (_isAnalyzing.value) return // Avoid overlapping analyses

        _isAnalyzing.value = true
        _error.value = null
        
        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    _error.value = "Por favor, configura tu API Key de Gemini en la pestaña de Secrets."
                    _isAnalyzing.value = false
                    return@launch
                }

                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                val base64Image = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

                val prompt = """
                    Eres un entrenador de oratoria. Analiza esta imagen del orador.
                    Evalúa:
                    1. Contacto visual (¿Está mirando hacia adelante/a la cámara o distraído?).
                    2. Postura (¿Está erguido, encorvado, relajado, tenso?).
                    3. Expresión facial (¿Transmite seguridad, nerviosismo, neutralidad?).
                    
                    Dame un feedback MUY BREVE y directo en 2-3 frases cortas.
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(
                        Content(
                            parts = listOf(
                                Part(text = prompt),
                                Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64Image))
                            )
                        )
                    )
                )

                val response = RetrofitClient.service.generateContent(apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                
                if (text != null) {
                    _feedback.value = text
                } else {
                    _error.value = "No se recibió respuesta del análisis."
                }
            } catch (e: Exception) {
                _error.value = "Error al analizar imagen: ${e.message}"
            } finally {
                kotlinx.coroutines.delay(3000) // Wait 3 seconds before next analysis
                _isAnalyzing.value = false
            }
        }
    }
}
