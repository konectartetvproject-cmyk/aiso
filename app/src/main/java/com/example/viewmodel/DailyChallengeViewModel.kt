package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

data class DailyChallenge(
    val title: String,
    val description: String,
    val route: String
)

class DailyChallengeViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("daily_challenge_prefs", Context.MODE_PRIVATE)

    private val _streak = MutableStateFlow(prefs.getInt("streak_count", 0))
    val streak: StateFlow<Int> = _streak.asStateFlow()

    private val _isCompletedToday = MutableStateFlow(checkIfCompletedToday())
    val isCompletedToday: StateFlow<Boolean> = _isCompletedToday.asStateFlow()

    private val _currentChallenge = MutableStateFlow(getChallengeForToday())
    val currentChallenge: StateFlow<DailyChallenge> = _currentChallenge.asStateFlow()

    private fun getChallengeForToday(): DailyChallenge {
        val challenges = listOf(
            DailyChallenge("Graba un Discurso Improvisado", "Habla por 1 minuto sobre un tema aleatorio", "impromptu_speech"),
            DailyChallenge("Batalla de Trabalenguas", "Mejora tu dicción con el modo offline de trabalenguas", "tongue_twister_offline"),
            DailyChallenge("Simulador de Audiencia", "Responde a una pregunta sorpresa de tu audiencia virtual", "audience_simulator"),
            DailyChallenge("Juego de Mímica", "Comunícate sin usar palabras", "charades"),
            DailyChallenge("Elimina tus Muletillas", "Practica hablar sin usar palabras de relleno", "filler_words_game"),
            DailyChallenge("Domina tu Lenguaje Corporal", "Grábate y analiza tu postura y contacto visual", "body_language")
        )
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        return challenges[dayOfYear % challenges.size]
    }

    private fun checkIfCompletedToday(): Boolean {
        val lastCompletedDay = prefs.getInt("last_completed_day", -1)
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val lastCompletedYear = prefs.getInt("last_completed_year", -1)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        
        return lastCompletedDay == currentDay && lastCompletedYear == currentYear
    }

    fun markAsCompleted() {
        if (_isCompletedToday.value) return

        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        
        val lastCompletedDay = prefs.getInt("last_completed_day", -1)
        val lastCompletedYear = prefs.getInt("last_completed_year", -1)

        var newStreak = _streak.value

        val calYesterday = Calendar.getInstance()
        calYesterday.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayDay = calYesterday.get(Calendar.DAY_OF_YEAR)
        val yesterdayYear = calYesterday.get(Calendar.YEAR)

        if (lastCompletedDay == yesterdayDay && lastCompletedYear == yesterdayYear) {
            newStreak++
        } else {
            newStreak = 1
        }

        prefs.edit()
            .putInt("last_completed_day", currentDay)
            .putInt("last_completed_year", currentYear)
            .putInt("streak_count", newStreak)
            .apply()

        _streak.value = newStreak
        _isCompletedToday.value = true
    }
}
