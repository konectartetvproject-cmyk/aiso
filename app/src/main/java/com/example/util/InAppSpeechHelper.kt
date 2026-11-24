package com.example.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

class InAppSpeechController(
    private val context: Context,
    private val language: String,
    private val continuous: Boolean,
    private val onTranscriptChanged: (String) -> Unit,
    private val onFinalized: (String) -> Unit,
    private val onRequestPermission: () -> Unit
) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private var speechRecognizer: SpeechRecognizer? = null
    private val committedSegments = mutableListOf<String>()
    private var currentPartial: String = ""

    var isListening by mutableStateOf(false)
        private set

    var liveText by mutableStateOf("")
        private set

    fun start(resetText: Boolean = true) {
        val hasPerm = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPerm) {
            onRequestPermission()
            return
        }

        if (resetText) {
            committedSegments.clear()
            currentPartial = ""
            liveText = ""
            onTranscriptChanged("")
        }

        isListening = true
        startRecognizerInternal()
    }

    fun stopAndFinish() {
        if (!isListening) return
        isListening = false
        mainHandler.removeCallbacksAndMessages(null)
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
        } catch (_: Exception) {
        }
        speechRecognizer = null

        if (currentPartial.isNotBlank()) {
            committedSegments.add(currentPartial.trim())
            currentPartial = ""
        }
        val fullText = committedSegments.joinToString(" ").replace(Regex("\\s+"), " ").trim()
        liveText = fullText
        onTranscriptChanged(fullText)
        onFinalized(fullText)
    }

    fun cancel() {
        isListening = false
        mainHandler.removeCallbacksAndMessages(null)
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
        } catch (_: Exception) {
        }
        speechRecognizer = null
    }

    private fun updateCombinedText() {
        val base = committedSegments.joinToString(" ").trim()
        val combined = when {
            base.isEmpty() -> currentPartial.trim()
            currentPartial.isBlank() -> base
            else -> "$base ${currentPartial.trim()}"
        }
        liveText = combined
        onTranscriptChanged(combined)
    }

    private fun startRecognizerInternal() {
        mainHandler.post {
            if (!isListening) return@post
            try {
                speechRecognizer?.destroy()
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) {}
                        override fun onBeginningOfSpeech() {}
                        override fun onRmsChanged(rmsdB: Float) {}
                        override fun onBufferReceived(buffer: ByteArray?) {}
                        override fun onEndOfSpeech() {}

                        override fun onError(error: Int) {
                            if (currentPartial.isNotBlank()) {
                                committedSegments.add(currentPartial.trim())
                                currentPartial = ""
                                updateCombinedText()
                            }
                            if (isListening && continuous) {
                                mainHandler.postDelayed({
                                    if (isListening) startRecognizerInternal()
                                }, 250L)
                            } else if (isListening && !continuous) {
                                stopAndFinish()
                            }
                        }

                        override fun onResults(results: Bundle?) {
                            val match = results
                                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                                ?.firstOrNull()
                                ?.trim()
                                .orEmpty()

                            if (match.isNotBlank()) {
                                committedSegments.add(match)
                            } else if (currentPartial.isNotBlank()) {
                                committedSegments.add(currentPartial.trim())
                            }
                            currentPartial = ""
                            updateCombinedText()

                            if (isListening && continuous) {
                                mainHandler.postDelayed({
                                    if (isListening) startRecognizerInternal()
                                }, 180L)
                            } else if (isListening && !continuous) {
                                stopAndFinish()
                            }
                        }

                        override fun onPartialResults(partialResults: Bundle?) {
                            val partial = partialResults
                                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                                ?.firstOrNull()
                                ?.trim()
                                .orEmpty()
                            if (partial.isNotBlank()) {
                                currentPartial = partial
                                updateCombinedText()
                            }
                        }

                        override fun onEvent(eventType: Int, params: Bundle?) {}
                    })
                }

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 3500L)
                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2800L)
                }
                speechRecognizer?.startListening(intent)
            } catch (_: Exception) {
                isListening = false
            }
        }
    }
}

@Composable
fun rememberInAppSpeechController(
    language: String = "es-ES",
    continuous: Boolean = true,
    onTranscriptChanged: (String) -> Unit = {},
    onFinalized: (String) -> Unit = {}
): InAppSpeechController {
    val context = LocalContext.current
    val currentOnChanged by rememberUpdatedState(onTranscriptChanged)
    val currentOnFinalized by rememberUpdatedState(onFinalized)

    lateinit var controller: InAppSpeechController

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            controller.start(resetText = true)
        } else {
            Toast.makeText(
                context,
                "Concede el permiso de micrófono para escuchar tu voz sin salir de la pantalla",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    controller = remember(context, language, continuous) {
        InAppSpeechController(
            context = context,
            language = language,
            continuous = continuous,
            onTranscriptChanged = { currentOnChanged(it) },
            onFinalized = { currentOnFinalized(it) },
            onRequestPermission = {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        )
    }

    DisposableEffect(controller) {
        onDispose {
            controller.cancel()
        }
    }

    return controller
}
