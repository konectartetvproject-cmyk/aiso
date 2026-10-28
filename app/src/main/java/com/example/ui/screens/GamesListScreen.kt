package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class GameCardData(
    val title: String,
    val subtitle: String,
    val badge: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesListScreen(
    onStageMovementClick: () -> Unit,
    onFillerWordsClick: () -> Unit,
    onRhetoricalFiguresClick: () -> Unit,
    onOpeningClosingClick: () -> Unit,
    onEsquivaMuletillasClick: () -> Unit,
    onElGranImpostorClick: () -> Unit,
    onCharadesClick: () -> Unit,
    onTongueTwistersClick: () -> Unit,
    onTongueTwisterDuelClick: () -> Unit,
    onBodyLanguageClick: () -> Unit,
    onTongueTwisterOfflineClick: () -> Unit,
    onAudienceSimulatorClick: () -> Unit,
    onDictionPracticeClick: () -> Unit,
    onOratoryDefenseClick: () -> Unit,
    onOratoryRunnerClick: () -> Unit,
    onOratoryMatchClick: () -> Unit,
    onCustomerServiceQuizClick: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    val voiceAndActionGames = listOf(
        GameCardData(
            title = "Esquiva Muletillas",
            subtitle = "Habla fluido sin decir 'eh', 'este' o 'bueno'",
            badge = "VOZ EN VIVO",
            icon = Icons.Default.Mic,
            color = Color(0xFFE91E63),
            onClick = onEsquivaMuletillasClick
        ),
        GameCardData(
            title = "Oratory Runner",
            subtitle = "Esquiva distracciones y recolecta técnicas",
            badge = "ARCADE",
            icon = Icons.AutoMirrored.Filled.DirectionsRun,
            color = Color(0xFFFF9800),
            onClick = onOratoryRunnerClick
        ),
        GameCardData(
            title = "Defensa del Discurso",
            subtitle = "Estrategia: coloca técnicas contra los nervios",
            badge = "ESTRATEGIA",
            icon = Icons.Default.Shield,
            color = Color(0xFF4CAF50),
            onClick = onOratoryDefenseClick
        ),
        GameCardData(
            title = "Desplazamiento Escénico",
            subtitle = "Domina las zonas de poder en el escenario",
            badge = "ESCENARIO",
            icon = Icons.Default.OpenWith,
            color = Color(0xFF00897B),
            onClick = onStageMovementClick
        ),
        GameCardData(
            title = "Práctica de Dicción",
            subtitle = "Evalúa tu articulación y claridad fonética",
            badge = "DICCIÓN",
            icon = Icons.Default.RecordVoiceOver,
            color = Color(0xFF1E88E5),
            onClick = onDictionPracticeClick
        ),
        GameCardData(
            title = "Trabalenguas IA",
            subtitle = "Agilidad lingual y velocidad de pronunciación",
            badge = "AGILIDAD",
            icon = Icons.Default.GraphicEq,
            color = Color(0xFF8E24AA),
            onClick = onTongueTwistersClick
        )
    )

    val rhetoricAndStructureGames = listOf(
        GameCardData(
            title = "Caza Muletillas",
            subtitle = "Limpia oraciones detectando palabras parásito",
            badge = "LIMPIEZA",
            icon = Icons.Default.CleaningServices,
            color = Color(0xFFD81B60),
            onClick = onFillerWordsClick
        ),
        GameCardData(
            title = "Figuras Retóricas",
            subtitle = "Identifica metáforas, anáforas, símiles e hipérboles",
            badge = "RETÓRICA",
            icon = Icons.Default.AutoFixHigh,
            color = Color(0xFF5E35B1),
            onClick = onRhetoricalFiguresClick
        ),
        GameCardData(
            title = "Inicios y Cierres",
            subtitle = "Domina las 10 técnicas de apertura y 7 de cierre",
            badge = "ESTRUCTURA",
            icon = Icons.Default.Flag,
            color = Color(0xFF3949AB),
            onClick = onOpeningClosingClick
        ),
        GameCardData(
            title = "Oratory Match",
            subtitle = "Conecta conceptos clave del orador exitoso",
            badge = "MEMORIA",
            icon = Icons.Default.Extension,
            color = Color(0xFF00ACC1),
            onClick = onOratoryMatchClick
        ),
        GameCardData(
            title = "Quiz Atención al Cliente",
            subtitle = "Resolución empática de objeciones y reclamos",
            badge = "EMPATÍA",
            icon = Icons.Default.SupportAgent,
            color = Color(0xFF43A047),
            onClick = onCustomerServiceQuizClick
        ),
        GameCardData(
            title = "Simulador de Audiencia",
            subtitle = "Responde preguntas difíciles con el método E.R.R.G.",
            badge = "PÚBLICO",
            icon = Icons.Default.QuestionAnswer,
            color = Color(0xFFF4511E),
            onClick = onAudienceSimulatorClick
        )
    )

    val groupAndPartyGames = listOf(
        GameCardData(
            title = "El Gran Impostor",
            subtitle = "Debate y descubre quién miente con argumentos",
            badge = "GRUPAL",
            icon = Icons.Default.Groups,
            color = Color(0xFF6D4C41),
            onClick = onElGranImpostorClick
        ),
        GameCardData(
            title = "Charadas de Oratoria",
            subtitle = "Expresión corporal y lenguaje no verbal en equipo",
            badge = "EXPRESIÓN",
            icon = Icons.Default.Face,
            color = Color(0xFFFB8C00),
            onClick = onCharadesClick
        ),
        GameCardData(
            title = "Duelo de Trabalenguas",
            subtitle = "Compite 1 vs 1 por la mejor dicción",
            badge = "DUELO 1v1",
            icon = Icons.Default.EmojiEvents,
            color = Color(0xFFC0CA33),
            onClick = onTongueTwisterDuelClick
        ),
        GameCardData(
            title = "Lenguaje Corporal (Cámara)",
            subtitle = "Entrena postura, gestos y presencia visual",
            badge = "VISIÓN IA",
            icon = Icons.Default.Videocam,
            color = Color(0xFFE53935),
            onClick = onBodyLanguageClick
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Juegos y Dinámicas Prácticas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                GameCategorySection(
                    title = "1. Acción, Voz y Escenario",
                    subtitle = "Entrena fluidez, desplazamiento y control de nervios",
                    games = voiceAndActionGames
                )
            }
            item {
                GameCategorySection(
                    title = "2. Retórica, Estructura y Público",
                    subtitle = "Domina las técnicas del libro 'Los secretos del orador exitoso'",
                    games = rhetoricAndStructureGames
                )
            }
            item {
                GameCategorySection(
                    title = "3. Dinámicas Grupales y Expresión",
                    subtitle = "Juegos interactivos para perder el miedo escénico",
                    games = groupAndPartyGames
                )
            }
        }
    }
}

@Composable
private fun GameCategorySection(
    title: String,
    subtitle: String,
    games: List<GameCardData>
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        val rows = games.chunked(2)
        rows.forEach { rowGames ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowGames.forEach { game ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = game.onClick),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(game.color.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = game.icon,
                                        contentDescription = game.title,
                                        tint = game.color,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Surface(
                                    color = game.color.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = game.badge,
                                        color = game.color,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Text(
                                text = game.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = game.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
                if (rowGames.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
