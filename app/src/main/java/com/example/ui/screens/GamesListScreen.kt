package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
    var selectedFilter by remember { mutableIntStateOf(0) } // 0 = Todos, 1 = Arcade y Voz, 2 = Retórica, 3 = Grupal

    val voiceAndActionGames = listOf(
        GameCardData(
            title = "Esquiva Muletillas",
            subtitle = "Modo Arcade Pop + Reto de Voz sin muletillas en vivo",
            badge = "ARCADE + VOZ",
            icon = Icons.Default.Mic,
            color = Color(0xFFE11D48),
            onClick = onEsquivaMuletillasClick
        ),
        GameCardData(
            title = "Oratory Runner",
            subtitle = "Corre por los 3 sectores, activa escudos y esquiva el pánico",
            badge = "3 CARRILES",
            icon = Icons.AutoMirrored.Filled.DirectionsRun,
            color = Color(0xFFF59E0B),
            onClick = onOratoryRunnerClick
        ),
        GameCardData(
            title = "Defensa del Discurso",
            subtitle = "Estrategia 6x3: despliega respiración y voz contra los nervios",
            badge = "ESTRATEGIA",
            icon = Icons.Default.Shield,
            color = Color(0xFF10B981),
            onClick = onOratoryDefenseClick
        ),
        GameCardData(
            title = "Desplazamiento Escénico",
            subtitle = "Domina las 9 zonas del escenario y el reto contrarreloj",
            badge = "9 ZONAS",
            icon = Icons.Default.OpenWith,
            color = Color(0xFF0D9488),
            onClick = onStageMovementClick
        ),
        GameCardData(
            title = "Práctica de Dicción",
            subtitle = "Evalúa tu articulación y precisión fonética al hablar",
            badge = "DICCIÓN",
            icon = Icons.Default.RecordVoiceOver,
            color = Color(0xFF2563EB),
            onClick = onDictionPracticeClick
        ),
        GameCardData(
            title = "Trabalenguas AISO",
            subtitle = "Agilidad lingual con consignas de ritmo y proyección",
            badge = "AGILIDAD",
            icon = Icons.Default.GraphicEq,
            color = Color(0xFF9333EA),
            onClick = onTongueTwistersClick
        )
    )

    val rhetoricAndStructureGames = listOf(
        GameCardData(
            title = "Caza Muletillas",
            subtitle = "Limpia discursos y escucha la versión ejecutiva impecable",
            badge = "EDITORIAL",
            icon = Icons.Default.CleaningServices,
            color = Color(0xFFDB2777),
            onClick = onFillerWordsClick
        ),
        GameCardData(
            title = "Figuras Retóricas",
            subtitle = "Metáforas, anáforas, tricolon y preguntas retóricas",
            badge = "RETÓRICA",
            icon = Icons.Default.AutoFixHigh,
            color = Color(0xFF7C3AED),
            onClick = onRhetoricalFiguresClick
        ),
        GameCardData(
            title = "Inicios y Cierres",
            subtitle = "Aplica la regla 10-80-10 en aperturas y conclusiones",
            badge = "10-80-10",
            icon = Icons.Default.Flag,
            color = Color(0xFF3B82F6),
            onClick = onOpeningClosingClick
        ),
        GameCardData(
            title = "Oratory Match",
            subtitle = "Conecta símbolos en cascada con pistas y principios del libro",
            badge = "PUZZLE 6x6",
            icon = Icons.Default.Extension,
            color = Color(0xFF0891B2),
            onClick = onOratoryMatchClick
        ),
        GameCardData(
            title = "Simulador de Audiencia",
            subtitle = "Enfrenta a Ernesto, Susana y Nicanor con el método E.R.R.G.",
            badge = "PÚBLICO IA",
            icon = Icons.Default.QuestionAnswer,
            color = Color(0xFFEA580C),
            onClick = onAudienceSimulatorClick
        ),
        GameCardData(
            title = "Quiz Atención y Empatía",
            subtitle = "Resolución persuasiva de objeciones y clientes difíciles",
            badge = "EMPATÍA",
            icon = Icons.Default.SupportAgent,
            color = Color(0xFF16A34A),
            onClick = onCustomerServiceQuizClick
        )
    )

    val groupAndPartyGames = listOf(
        GameCardData(
            title = "El Gran Impostor",
            subtitle = "Roles secretos, retos retóricos y votación del jurado",
            badge = "3-10 JUGADORES",
            icon = Icons.Default.Groups,
            color = Color(0xFFEF4444),
            onClick = onElGranImpostorClick
        ),
        GameCardData(
            title = "Charadas de Oratoria",
            subtitle = "Actúa emociones, arquetipos del libro y crisis en escena",
            badge = "NO VERBAL",
            icon = Icons.Default.Face,
            color = Color(0xFFF59E0B),
            onClick = onCharadesClick
        ),
        GameCardData(
            title = "Duelo de Trabalenguas",
            subtitle = "Compite 1 vs 1 por la dicción más clara y veloz",
            badge = "DUELO 1v1",
            icon = Icons.Default.EmojiEvents,
            color = Color(0xFFCA8A04),
            onClick = onTongueTwisterDuelClick
        ),
        GameCardData(
            title = "Lenguaje Corporal",
            subtitle = "Entrena postura, gestos de poder y presencia visual",
            badge = "CÁMARA IA",
            icon = Icons.Default.Videocam,
            color = Color(0xFFDC2626),
            onClick = onBodyLanguageClick
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Arena de Juegos AISO", fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
                        Text(
                            "16 Entrenamientos Interactivos de Oratoria",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
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
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            // Banner Hero Gamificado
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF0D9488))
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = Color(0xFFFBBF24),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "APRENDIZAJE ACTIVO",
                                        color = Color(0xFF0F172A),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Domina el Escenario Jugando",
                                    color = Color.White,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Elimina muletillas, memoriza aperturas 10-80-10 y entrena reflejos escénicos.",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.14f),
                                modifier = Modifier.size(56.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.SportsEsports,
                                        contentDescription = null,
                                        tint = Color(0xFFFBBF24),
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Chips de Filtro por Categoría
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filters = listOf(
                        "Todos (16)",
                        "⚡ Acción y Voz",
                        "🧠 Retórica y Mente",
                        "🎭 Grupal y Expresión"
                    )
                    filters.forEachIndexed { idx, label ->
                        FilterChip(
                            selected = selectedFilter == idx,
                            onClick = { selectedFilter = idx },
                            label = {
                                Text(
                                    text = label,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }
                }
            }

            if (selectedFilter == 0 || selectedFilter == 1) {
                item {
                    GameCategorySection(
                        title = "1. Acción, Voz y Escenario",
                        subtitle = "Entrena fluidez, desplazamiento en 9 zonas y control de nervios",
                        games = voiceAndActionGames
                    )
                }
            }
            if (selectedFilter == 0 || selectedFilter == 2) {
                item {
                    GameCategorySection(
                        title = "2. Retórica, Estructura y Público",
                        subtitle = "Domina las técnicas del libro «Los secretos del orador exitoso»",
                        games = rhetoricAndStructureGames
                    )
                }
            }
            if (selectedFilter == 0 || selectedFilter == 3) {
                item {
                    GameCategorySection(
                        title = "3. Dinámicas Grupales y Expresión",
                        subtitle = "Juegos interactivos para perder el miedo escénico en equipo",
                        games = groupAndPartyGames
                    )
                }
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
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, game.color.copy(alpha = 0.28f)),
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
                                        .clip(RoundedCornerShape(12.dp))
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
                                    color = game.color.copy(alpha = 0.14f),
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
                                fontWeight = FontWeight.ExtraBold,
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
