package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.viewmodel.OratoriaViewModel

data class DailyTip(
    val title: String,
    val description: String,
    val icon: ImageVector
)

val dailyTips = listOf(
    DailyTip("Contacto Visual", "Mantén contacto visual con tu audiencia en forma de 'Z' o 'W' para incluir a todos.", Icons.Default.Visibility),
    DailyTip("Pausas Estratégicas", "Utiliza pausas antes de un punto clave para generar expectativa y después para que se asimile.", Icons.Default.HourglassEmpty),
    DailyTip("Sonrisa Genuina", "Una sonrisa inicial relaja a tu audiencia y proyecta confianza y carisma.", Icons.Default.SentimentSatisfiedAlt),
    DailyTip("Volumen Dinámico", "Sube el volumen para enfatizar ideas importantes y bájalo para crear intimidad.", Icons.Default.VolumeUp),
    DailyTip("Postura Abierta", "Evita cruzar los brazos; mantén tus manos visibles y abiertas para proyectar honestidad.", Icons.Default.AccessibilityNew),
    DailyTip("Velocidad Controlada", "Habla más rápido para inyectar energía y entusiasmo, y más lento para enfatizar puntos graves.", Icons.Default.Speed)
)

@Composable
fun MainScreen(
    viewModel: OratoriaViewModel,
    onNavigateToExercise: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToTechniques: () -> Unit,
    onNavigateToToxic: () -> Unit,
    onNavigateToAnecdotes: () -> Unit,
    onNavigateToLiveDebate: () -> Unit,
    onNavigateToImpromptu: () -> Unit,
    onNavigateToPracticeList: () -> Unit,
    onNavigateToStageMovement: () -> Unit,
    onNavigateToFillerWords: () -> Unit,
    onNavigateToRhetoricalFigures: () -> Unit,
    onNavigateToOpeningClosing: () -> Unit,
    onNavigateToEsquivaMuletillas: () -> Unit,
    onNavigateToOratoryRunner: () -> Unit,
    onNavigateToLiveSpeechPractice: () -> Unit,
    onNavigateToCustomerServicePractice: () -> Unit,
    onNavigateToSalesPractice: () -> Unit,
    onNavigateToElGranImpostor: () -> Unit,
    onNavigateToQuotes: () -> Unit,
    onNavigateToCharades: () -> Unit,
    onNavigateToTongueTwisters: () -> Unit,
    onNavigateToTongueTwisterDuel: () -> Unit,
    onNavigateToBodyLanguage: () -> Unit,
    onNavigateToTongueTwisterOffline: () -> Unit,
    onNavigateToAudienceSimulator: () -> Unit,
    onNavigateToRoute: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var showDailyTip by remember { mutableStateOf(true) }
    val randomTip = remember { dailyTips.random() }

    if (showDailyTip) {
        AlertDialog(
            onDismissRequest = { showDailyTip = false },
            icon = { Icon(Icons.Default.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp)) },
            title = { Text("Consejo del Día", fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(randomTip.icon, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.secondary)
                    Spacer(Modifier.height(16.dp))
                    Text(randomTip.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    Text(randomTip.description, textAlign = TextAlign.Center)
                }
            },
            confirmButton = {
                TextButton(onClick = { showDailyTip = false }) {
                    Text("¡Entendido!")
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AutoFixHigh, contentDescription = "Taller") },
                    label = { Text("Taller") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Forum, contentDescription = "En Vivo") },
                    label = { Text("En Vivo") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.VideogameAsset, contentDescription = "Juegos") },
                    label = { Text("Juegos") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.FormatQuote, contentDescription = "Frases") },
                    label = { Text("Frases") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = "Tips") },
                    label = { Text("Tips") },
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> {
                    TechniquesListScreen(
                        viewModel = viewModel,
                        onTechniqueClick = { technique ->
                            viewModel.startTechniquePractice(technique)
                            onNavigateToTechniques()
                        },
                        onToxicParticipantsClick = onNavigateToToxic,
                        onAnecdotesClick = onNavigateToAnecdotes,
                        onQuotesClick = onNavigateToQuotes,
                        onNavigateToQuiz = onNavigateToQuiz,
                        onNavigateToRoute = onNavigateToRoute
                    )
                }
                1 -> {
                    LiveSpeechesListScreen(
                        viewModel = viewModel,
                        onNavigateToLiveDebate = onNavigateToLiveDebate,
                        onNavigateToImpromptu = onNavigateToImpromptu,
                        onNavigateToPracticeList = onNavigateToPracticeList,
                        onNavigateToExercise = onNavigateToExercise,
                        onNavigateToToxicVideo = { onNavigateToRoute("toxic_video") },
                        onNavigateToSpeechTimer = { onNavigateToRoute("speech_timer") },
                        onNavigateToLiveSpeechPractice = { onNavigateToRoute("live_speech_practice") },
                        onNavigateToCustomerServicePractice = { onNavigateToRoute("customer_service_practice") },
                        onNavigateToSalesPractice = { onNavigateToRoute("sales_practice") }
                    )
                }
                2 -> {
                    GamesListScreen(
                        onStageMovementClick = onNavigateToStageMovement,
                        onFillerWordsClick = onNavigateToFillerWords,
                        onRhetoricalFiguresClick = onNavigateToRhetoricalFigures,
                        onOpeningClosingClick = onNavigateToOpeningClosing,
                        onEsquivaMuletillasClick = onNavigateToEsquivaMuletillas,
                        onElGranImpostorClick = onNavigateToElGranImpostor,
                        onCharadesClick = onNavigateToCharades,
                        onTongueTwistersClick = onNavigateToTongueTwisters,
                        onTongueTwisterDuelClick = onNavigateToTongueTwisterDuel,
                        onBodyLanguageClick = onNavigateToBodyLanguage,
                        onTongueTwisterOfflineClick = onNavigateToTongueTwisterOffline,
                        onAudienceSimulatorClick = onNavigateToAudienceSimulator,
                        onDictionPracticeClick = { onNavigateToRoute("diction_practice") },
                        onOratoryDefenseClick = { onNavigateToRoute("oratory_defense") },
                        onOratoryRunnerClick = onNavigateToOratoryRunner,
                        onOratoryMatchClick = { onNavigateToRoute("oratory_match") },
                        onCustomerServiceQuizClick = { onNavigateToRoute("customer_service_quiz") }
                    )
                }
                3 -> {
                    QuotesScreen(onBack = {}, showBackButton = false)
                }
                4 -> {
                    RecommendationsScreen(onBack = {}, showBackButton = false)
                }
            }
        }
    }
}

