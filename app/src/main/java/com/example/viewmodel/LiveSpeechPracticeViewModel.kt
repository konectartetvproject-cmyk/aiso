package com.example.viewmodel

import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.gemini.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

data class LiveSpeechState(
    val isRecording: Boolean = false,
    val isAnalyzing: Boolean = false,
    val currentTip: String = "Preparado. Comienza a hablar para recibir consejos...",
    val allTips: List<String> = emptyList(),
    val globalRecommendation: String? = null,
    val fullTranscript: String = ""
)

class LiveSpeechPracticeViewModel : ViewModel() {
    private val _state = MutableStateFlow(LiveSpeechState())
    val state: StateFlow<LiveSpeechState> = _state.asStateFlow()

    private var analysisJob: Job? = null
    private var lastAnalyzedTextIndex = 0

    fun toggleRecording() {
        if (_state.value.isRecording) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    private fun startRecording() {
        _state.update { 
            it.copy(
                isRecording = true, 
                currentTip = "Escuchando...", 
                allTips = emptyList(), 
                globalRecommendation = null,
                fullTranscript = ""
            ) 
        }
        lastAnalyzedTextIndex = 0
    }

    fun onNewText(text: String) {
        _state.update { it.copy(fullTranscript = it.fullTranscript + " " + text) }
    }

    fun analyzeFrame(bitmap: Bitmap) {
        val currentState = _state.value
        if (!currentState.isRecording || currentState.isAnalyzing) return

        val transcriptToAnalyze = currentState.fullTranscript.substring(lastAnalyzedTextIndex).trim()
        if (transcriptToAnalyze.isEmpty()) return

        lastAnalyzedTextIndex = currentState.fullTranscript.length
        
        _state.update { it.copy(isAnalyzing = true) }
        viewModelScope.launch {
            try {
                val base64Image = bitmapToBase64(bitmap)
                val prompt = "Eres un coach de oratoria. Analiza esta imagen del orador y lo que acaba de decir: \"$transcriptToAnalyze\". Dame un consejo muy breve (máximo 15 palabras) sobre su lenguaje corporal (si ves algo relevante) y su argumento o tono. Sé directo y útil."
                
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

                val response = GeminiRetrofitClient.service.generateContent(
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    request = request
                )
                
                val textResponse = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                if (textResponse.isNotEmpty()) {
                    _state.update { 
                        it.copy(
                            currentTip = textResponse,
                            allTips = it.allTips + textResponse,
                            isAnalyzing = false
                        )
                    }
                } else {
                    _state.update { it.copy(isAnalyzing = false) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { it.copy(isAnalyzing = false) }
            }
        }
    }

    private fun stopRecording() {
        _state.update { it.copy(isRecording = false, currentTip = "Generando resumen global...") }
        generateGlobalRecommendation()
    }

    private fun generateGlobalRecommendation() {
        val transcript = _state.value.fullTranscript
        if (transcript.trim().isEmpty()) {
            _state.update { it.copy(globalRecommendation = "No se detectó discurso.", currentTip = "Finalizado") }
            return
        }

        viewModelScope.launch {
            try {
                val prompt = "Eres un coach de oratoria. Un alumno ha terminado su discurso. Esta es la transcripción completa: \"$transcript\". Dame una recomendación global detallada, destacando puntos fuertes y áreas de mejora en estructura, argumentos y tono estimado. Usa formato de lista."
                
                val request = GenerateContentRequest(
                    contents = listOf(
                        Content(
                            parts = listOf(Part(text = prompt))
                        )
                    )
                )

                val response = GeminiRetrofitClient.service.generateContent(
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    request = request
                )
                
                val textResponse = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No se pudo generar la recomendación."
                _state.update { it.copy(globalRecommendation = textResponse, currentTip = "Finalizado") }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { it.copy(globalRecommendation = "Error al generar recomendación.", currentTip = "Finalizado") }
            }
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        // Resize bitmap to save bandwidth
        val scaled = Bitmap.createScaledBitmap(bitmap, 480, 640, true)
        scaled.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}
