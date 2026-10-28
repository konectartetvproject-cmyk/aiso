package com.example.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

data class QuotaInfo(
    val totalTokens: Long = 0,
    val totalSpentUsd: Double = 0.0,
    val maxQuotaUsd: Double = 2.00, // Límite estricto solicitado: $2.00 USD
    val isPaidKeyMode: Boolean = false, // false activa el control estricto del límite de $2.00 USD
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
    // Estimación para Gemini 3.5 / 1.5 / 2.5 Flash (~$0.075 - $0.15 por 1M tokens)
    private const val PRICE_PER_1K_TOKENS_USD = 0.00015

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

    fun recordUsage(inputTokens: Int, outputTokens: Int) {
        val totalNewTokens = (inputTokens + outputTokens).toLong()
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
    }
}
