package com.example.model

data class SpeechStep(
    val id: Int,
    val title: String,
    val shortName: String,
    val description: String,
    val recommendation: String,
    val examplePhrases: List<String>,
    val detected: Boolean = false,
    val detectionTimestamp: Long? = null
)

data class LiveCoachAlert(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: AlertType,
    val title: String,
    val message: String,
    val bookReference: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class AlertType {
    BODY_LANGUAGE,
    VOICE_VOLUME,
    VOICE_RHYTHM,
    FILLER_WORD,
    STRUCTURE,
    POSITIVE_REINFORCEMENT
}

data class LiveDiagnosisReport(
    val overallScore: Int,
    val bodyLanguageScore: Int,
    val voiceScore: Int,
    val structureScore: Int,
    val durationSeconds: Long,
    val totalWords: Int,
    val averageWpm: Int,
    val muletillasDetected: Map<String, Int>,
    val totalMuletillas: Int,
    val completedSteps: List<SpeechStep>,
    val pendingSteps: List<SpeechStep>,
    val strengths: List<String>,
    val corrections: List<String>,
    val recommendedBookExercises: List<String>
)
