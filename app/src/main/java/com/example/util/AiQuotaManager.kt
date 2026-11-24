package com.example.util

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

data class QuotaInfo(
    val totalTokens: Long = 0,
    val totalSpentUsd: Double = 0.0,
    val maxQuotaUsd: Double = 2.00, // Límite estricto por usuario: $2.00 USD (invisible hasta agotarse)
    val isPaidKeyMode: Boolean = false,
    val isQuotaExceeded: Boolean = false
) {
    val formattedSpent: String
        get() = String.format(Locale.US, "$%.4f", totalSpentUsd)

    val remainingUsd: Double
        get() = if (isPaidKeyMode) Double.POSITIVE_INFINITY else (maxQuotaUsd - totalSpentUsd).coerceAtLeast(0.0)

    val remainingPercent: Float
        get() = if (maxQuotaUsd <= 0.0) 0f else ((1.0 - (totalSpentUsd / maxQuotaUsd)).coerceIn(0.0, 1.0)).toFloat()
}

object AiQuotaManager {
    // Costo estimado para Gemini Flash / Pro (~$0.15 por 1M tokens)
    private const val PRICE_PER_1K_TOKENS_USD = 0.00015
    private const val PREFS_NAME = "aiso_ai_quota_prefs"
    private const val KEY_TOTAL_TOKENS = "total_tokens"
    private const val KEY_TOTAL_SPENT_BITS = "total_spent_bits"
    private const val KEY_PAID_MODE = "is_paid_key_mode"

    private var prefs: SharedPreferences? = null

    private val _quotaState = MutableStateFlow(
        QuotaInfo(
            totalTokens = 0,
            totalSpentUsd = 0.0,
            maxQuotaUsd = 2.00,
            isPaidKeyMode = false,
            isQuotaExceeded = false
        )
    )
    val quotaState: StateFlow<QuotaInfo> = _quotaState.asStateFlow()

    fun init(context: Context) {
        if (prefs != null) return
        val sp = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs = sp
        val savedTokens = sp.getLong(KEY_TOTAL_TOKENS, 0L)
        val savedSpent = Double.fromBits(sp.getLong(KEY_TOTAL_SPENT_BITS, (0.0).toRawBits()))
        val savedPaidMode = sp.getBoolean(KEY_PAID_MODE, false)
        val maxUsd = _quotaState.value.maxQuotaUsd
        val exceeded = !savedPaidMode && savedSpent >= maxUsd
        _quotaState.value = QuotaInfo(
            totalTokens = savedTokens,
            totalSpentUsd = savedSpent,
            maxQuotaUsd = maxUsd,
            isPaidKeyMode = savedPaidMode,
            isQuotaExceeded = exceeded
        )
    }

    fun recordUsage(inputTokens: Int, outputTokens: Int) {
        val totalNewTokens = (inputTokens.coerceAtLeast(1) + outputTokens.coerceAtLeast(1)).toLong()
        val cost = (totalNewTokens.toDouble() / 1000.0) * PRICE_PER_1K_TOKENS_USD
        val current = _quotaState.value
        val newSpent = current.totalSpentUsd + cost
        val newTokens = current.totalTokens + totalNewTokens
        val exceeded = !current.isPaidKeyMode && newSpent >= current.maxQuotaUsd

        _quotaState.value = current.copy(
            totalTokens = newTokens,
            totalSpentUsd = newSpent,
            isQuotaExceeded = exceeded
        )

        prefs?.edit()
            ?.putLong(KEY_TOTAL_TOKENS, newTokens)
            ?.putLong(KEY_TOTAL_SPENT_BITS, newSpent.toRawBits())
            ?.apply()
    }

    fun recordTextUsage(promptText: String, responseText: String) {
        val estimatedInputTokens = (promptText.length / 4).coerceAtLeast(120)
        val estimatedOutputTokens = (responseText.length / 4).coerceAtLeast(250)
        recordUsage(estimatedInputTokens, estimatedOutputTokens)
    }

    fun canMakeRequest(): Boolean {
        val current = _quotaState.value
        return current.isPaidKeyMode || (!current.isQuotaExceeded && current.totalSpentUsd < current.maxQuotaUsd)
    }

    fun setPaidKeyMode(enabled: Boolean) {
        val current = _quotaState.value
        _quotaState.value = current.copy(
            isPaidKeyMode = enabled,
            isQuotaExceeded = if (enabled) false else current.totalSpentUsd >= current.maxQuotaUsd
        )
        prefs?.edit()?.putBoolean(KEY_PAID_MODE, enabled)?.apply()
    }

    fun setMaxQuotaUsd(maxUsd: Double) {
        val current = _quotaState.value
        val exceeded = !current.isPaidKeyMode && current.totalSpentUsd >= maxUsd
        _quotaState.value = current.copy(
            maxQuotaUsd = maxUsd,
            isQuotaExceeded = exceeded
        )
    }

    fun resetQuota() {
        val current = _quotaState.value
        _quotaState.value = current.copy(
            totalTokens = 0,
            totalSpentUsd = 0.0,
            isQuotaExceeded = false
        )
        prefs?.edit()
            ?.putLong(KEY_TOTAL_TOKENS, 0L)
            ?.putLong(KEY_TOTAL_SPENT_BITS, (0.0).toRawBits())
            ?.apply()
    }
}
