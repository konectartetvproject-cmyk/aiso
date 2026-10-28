package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import kotlinx.coroutines.isActive
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.OratoriaViewModel

data class MainDailyTip(val title: String, val description: String, val icon: ImageVector)

val mainDailyTips = listOf(
    MainDailyTip("Contacto Visual en 180°", "Reparte tu mirada en forma de 'W' o 'Z' para conectar con todos los cuadrantes del auditorio.", Icons.Default.Visibility),
    MainDailyTip("Pausas Estratégicas", "Guarda 2 segundos de silencio antes de una revelación clave para generar máxima expectativa.", Icons.Default.HourglassEmpty),
    MainDailyTip("Sonrisa de Conexión", "Una sonrisa inicial sincera disminuye tu cortisol y relaja inmediatamente al público.", Icons.Default.SentimentSatisfiedAlt),
    MainDailyTip("Volumen Dinámico", "Eleva la voz para enfatizar victorias o conclusiones, y bájala para crear intimidad y reflexión.", Icons.Default.VolumeUp),
    MainDailyTip("Zona de Influencia", "Mantén tus manos visibles entre el pecho y la cintura con palmas abiertas para proyectar autoridad.", Icons.Default.AccessibilityNew),
    MainDailyTip("Ritmo y Cadencia", "Varía la velocidad de dicción para evitar un tono monótono y mantener despierto el interés.", Icons.Default.Speed),
    MainDailyTip("Respiración Diafragmática", "Inhala en 4 segundos inflando el vientre antes de subir al podio para oxigenar tu mente.", Icons.Default.Air),
    MainDailyTip("Anclaje Emocional", "Abre tu discurso con una historia humana o pregunta reflexiva en vez de un saludo frío.", Icons.Default.AutoAwesome),
    MainDailyTip("Evita las Muletillas", "Reemplaza el 'ehhh' o 'este' por una pausa en silencio. El silencio denota seguridad.", Icons.Default.Shield),
    MainDailyTip("Postura de Poder", "Pies alineados con tus hombros y espalda erguida; el cuerpo transmite confianza antes que la voz.", Icons.Default.EmojiPeople)
)

data class FeatureItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val badge: String? = null,
    val cardGradient: List<Color>,
    val iconBgColor: Color,
    val onClick: () -> Unit
)

data class HubCategory(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color,
    val items: List<FeatureItem>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: OratoriaViewModel,
    onNavigateToExercise: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToTechniques: () -> Unit,
    onNavigateToToxic: () -> Unit,
    onNavigateToAnecdotes: () -> Unit,
    onNavigateToLiveDebate: () -> Unit,
    onNavigateToHistory: () -> Unit,
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
    var selectedTab: Int? by remember { mutableStateOf(null) }
    var tipIndex by remember { mutableIntStateOf(0) }
    val currentTip = remember(tipIndex) { mainDailyTips[tipIndex % mainDailyTips.size] }

    // Rotación automática cada 10 segundos
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(10_000L)
            tipIndex++
        }
    }

    BackHandler(enabled = selectedTab != null) {
        selectedTab = null
    }

    if (selectedTab != null) {
        when (selectedTab) {
            0 -> {
                TechniquesListScreen(
                    viewModel = viewModel,
                    onTechniqueClick = { technique ->
                        viewModel.startTechniquePractice(technique)
                    },
                    onToxicParticipantsClick = onNavigateToToxic,
                    onAnecdotesClick = onNavigateToAnecdotes,
                    onQuotesClick = onNavigateToQuotes,
                    onNavigateToQuiz = { onNavigateToRoute("lesson_content") },
                    onNavigateToRoute = onNavigateToRoute,
                    onBack = { selectedTab = null }
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
                    onNavigateToLiveSpeechPractice = { onNavigateToRoute("live_speech_practice") },
                    onNavigateToCustomerServicePractice = { onNavigateToRoute("customer_service_practice") },
                    onNavigateToSalesPractice = { onNavigateToRoute("sales_practice") },
                    onNavigateToAisoLive = { onNavigateToRoute("aiso_live") },
                    onNavigateToBodyLanguage = { onNavigateToRoute("body_language") }
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
                    onCustomerServiceQuizClick = { onNavigateToRoute("customer_service_quiz") },
                    onBack = { selectedTab = null }
                )
            }
            3 -> QuotesScreen(onBack = { selectedTab = null }, showBackButton = true)
            4 -> com.example.ui.screens.english.EnglishLearningMainScreen(
                onNavigateToRoute = onNavigateToRoute,
                onBack = { selectedTab = null }
            )
            5 -> RecommendationsScreen(onBack = { selectedTab = null }, showBackButton = true)
            6 -> TimersListScreen(
                onNavigateToSpeechTimer = { onNavigateToRoute("speech_timer") },
                onNavigateToNormalTimer = { onNavigateToRoute("normal_timer") },
                onNavigateToDebateTimer = { onNavigateToRoute("debate_timer") },
                onBack = { selectedTab = null }
            )
        }
        return
    }

    // 6 Secciones principales organizadas a lo largo de la pantalla, con desplazamiento lateral en cada una (5-6 elementos por sección)
    val categories = remember {
        listOf(
            // 1. ENTRENAMIENTO EN VIVO & IA
            HubCategory(
                title = "1. Práctica en Vivo & IA",
                subtitle = "Cámara frontal, visión artificial y coaching",
                icon = Icons.Default.Videocam,
                accentColor = Color(0xFFE11D48),
                items = listOf(
                    FeatureItem(
                        title = "AISO Live + Teleprompter",
                        subtitle = "Cámara, teleprompter bajo lente, PPM y detector de muletillas",
                        icon = Icons.Default.Videocam,
                        badge = "TELEPROMPTER",
                        cardGradient = listOf(Color(0xFF881337), Color(0xFFBE123C)),
                        iconBgColor = Color(0xFFFB7185),
                        onClick = { onNavigateToRoute("aiso_live") }
                    ),
                    FeatureItem(
                        title = "AISO Tutor (Chat)",
                        subtitle = "Mentoría directa con respuestas concisas de Gemini",
                        icon = Icons.Default.AutoAwesome,
                        badge = "IA TUTOR",
                        cardGradient = listOf(Color(0xFF78350F), Color(0xFFB45309)),
                        iconBgColor = Color(0xFFFBBF24),
                        onClick = { onNavigateToRoute("aiso_chat") }
                    ),
                    FeatureItem(
                        title = "Espejo Corporal 180°",
                        subtitle = "Entrena postura de poder y columna de oro",
                        icon = Icons.Default.AccessibilityNew,
                        badge = null,
                        cardGradient = listOf(Color(0xFF0369A1), Color(0xFF0284C7)),
                        iconBgColor = Color(0xFF38BDF8),
                        onClick = { onNavigateToBodyLanguage() }
                    ),
                    FeatureItem(
                        title = "Debate en Vivo",
                        subtitle = "Defiende tu postura contra un público interactivo",
                        icon = Icons.Default.Forum,
                        badge = null,
                        cardGradient = listOf(Color(0xFF581C87), Color(0xFF7E22CE)),
                        iconBgColor = Color(0xFFA855F7),
                        onClick = { onNavigateToLiveDebate() }
                    ),
                    FeatureItem(
                        title = "Discurso Improvisado",
                        subtitle = "Temas al azar y medición de agilidad mental",
                        icon = Icons.Default.Campaign,
                        badge = null,
                        cardGradient = listOf(Color(0xFF9A3412), Color(0xFFC2410C)),
                        iconBgColor = Color(0xFFFB923C),
                        onClick = { onNavigateToImpromptu() }
                    ),
                    FeatureItem(
                        title = "Público Tóxico",
                        subtitle = "Simulador de interrupciones y objeciones difíciles",
                        icon = Icons.Default.Shield,
                        badge = null,
                        cardGradient = listOf(Color(0xFF991B1B), Color(0xFFDC2626)),
                        iconBgColor = Color(0xFFF87171),
                        onClick = { onNavigateToRoute("toxic_video") }
                    )
                )
            ),

            // 2. INSPIRACIÓN & HISTORIAS
            HubCategory(
                title = "2. Inspiración & Historias",
                subtitle = "Historias visuales, afirmaciones y discursos",
                icon = Icons.Default.FormatQuote,
                accentColor = Color(0xFFD97706),
                items = listOf(
                    FeatureItem(
                        title = "Historias de Inspiración",
                        subtitle = "Frases, afirmaciones y tips en formato Instagram",
                        icon = Icons.Default.FormatQuote,
                        badge = "STORY",
                        cardGradient = listOf(Color(0xFF831843), Color(0xFFBE185D)),
                        iconBgColor = Color(0xFFF472B6),
                        onClick = { onNavigateToQuotes() }
                    ),
                    FeatureItem(
                        title = "Afirmaciones con Voz",
                        subtitle = "Practica frente al micrófono para fortalecer convicción",
                        icon = Icons.Default.Mic,
                        badge = "MICRÓFONO",
                        cardGradient = listOf(Color(0xFF4C1D95), Color(0xFF6D28D9)),
                        iconBgColor = Color(0xFFA78BFA),
                        onClick = { onNavigateToRoute("affirmations_list") }
                    ),
                    FeatureItem(
                        title = "Discursos Históricos",
                        subtitle = "Aprende de Martin Luther King Jr., Steve Jobs y Churchill",
                        icon = Icons.Default.HistoryEdu,
                        badge = null,
                        cardGradient = listOf(Color(0xFF1E293B), Color(0xFF334155)),
                        iconBgColor = Color(0xFF94A3B8),
                        onClick = { onNavigateToRoute("historical_speeches_list") }
                    ),
                    FeatureItem(
                        title = "Poemas & Declamación",
                        subtitle = "Textos poéticos para modular ritmo y cadencia vocal",
                        icon = Icons.Default.LibraryBooks,
                        badge = null,
                        cardGradient = listOf(Color(0xFF1E1B4B), Color(0xFF4338CA)),
                        iconBgColor = Color(0xFF818CF8),
                        onClick = { onNavigateToRoute("poems_list") }
                    ),
                    FeatureItem(
                        title = "Anecdotario Escénico",
                        subtitle = "Casos reales y lecciones aprendidas de oradores",
                        icon = Icons.Default.MenuBook,
                        badge = null,
                        cardGradient = listOf(Color(0xFF134E4A), Color(0xFF0F766E)),
                        iconBgColor = Color(0xFF2DD4BF),
                        onClick = { onNavigateToAnecdotes() }
                    ),
                    FeatureItem(
                        title = "Tips de Oratoria",
                        subtitle = "Consejos rápidos para inicio, cuerpo y remate",
                        icon = Icons.Default.Lightbulb,
                        badge = null,
                        cardGradient = listOf(Color(0xFF713F12), Color(0xFFA16207)),
                        iconBgColor = Color(0xFFFACC15),
                        onClick = { selectedTab = 5 }
                    )
                )
            ),

            // 3. APRENDER & FORMACIÓN TEÓRICA
            HubCategory(
                title = "3. Academia & Formación",
                subtitle = "Metodología del libro 'Los secretos del orador exitoso'",
                icon = Icons.Default.School,
                accentColor = Color(0xFF4338CA),
                items = listOf(
                    FeatureItem(
                        title = "Taller de Oratoria",
                        subtitle = "Ruta de aprendizaje guiada paso a paso por módulos",
                        icon = Icons.Default.AutoFixHigh,
                        badge = "CURSO",
                        cardGradient = listOf(Color(0xFF312E81), Color(0xFF4338CA)),
                        iconBgColor = Color(0xFF818CF8),
                        onClick = { selectedTab = 0 }
                    ),
                    FeatureItem(
                        title = "Guía Paso a Paso (11 Fases)",
                        subtitle = "Desde la preparación psicológica hasta los aplausos",
                        icon = Icons.Default.MenuBook,
                        badge = null,
                        cardGradient = listOf(Color(0xFF065F46), Color(0xFF059669)),
                        iconBgColor = Color(0xFF34D399),
                        onClick = { onNavigateToRoute("public_speaking_guide") }
                    ),
                    FeatureItem(
                        title = "Cultura General",
                        subtitle = "Referencias de historia y ciencia para nutrir tus ideas",
                        icon = Icons.Default.LocalLibrary,
                        badge = null,
                        cardGradient = listOf(Color(0xFF1E3A8A), Color(0xFF2563EB)),
                        iconBgColor = Color(0xFF60A5FA),
                        onClick = { onNavigateToRoute("general_culture") }
                    ),
                    FeatureItem(
                        title = "Oratoria en Inglés",
                        subtitle = "Presentaciones internacionales y dicción en inglés",
                        icon = Icons.Default.Language,
                        badge = null,
                        cardGradient = listOf(Color(0xFF581C87), Color(0xFF7C3AED)),
                        iconBgColor = Color(0xFFA78BFA),
                        onClick = { selectedTab = 4 }
                    ),
                    FeatureItem(
                        title = "Simulador de Audiencias",
                        subtitle = "Preguntas difíciles con voz (Ernesto, Susana y Nicanor)",
                        icon = Icons.Default.Groups,
                        badge = "CON VOZ",
                        cardGradient = listOf(Color(0xFF0F766E), Color(0xFF0D9488)),
                        iconBgColor = Color(0xFF2DD4BF),
                        onClick = { onNavigateToAudienceSimulator() }
                    ),
                    FeatureItem(
                        title = "Participantes Tóxicos",
                        subtitle = "Identifica a Ernesto el Erudito y Susana la Sniper",
                        icon = Icons.Default.PersonOff,
                        badge = null,
                        cardGradient = listOf(Color(0xFF991B1B), Color(0xFFB91C1C)),
                        iconBgColor = Color(0xFFF87171),
                        onClick = { onNavigateToToxic() }
                    )
                )
            ),

            // 4. VOZ, RESPIRACIÓN & DICCIÓN
            HubCategory(
                title = "4. Técnica Vocal & Respiración",
                subtitle = "Voz diafragmática, dicción y agilidad lingual",
                icon = Icons.Default.RecordVoiceOver,
                accentColor = Color(0xFF0284C7),
                items = listOf(
                    FeatureItem(
                        title = "Respiración y Ritual Vocal",
                        subtitle = "Caja 4x4, diafragmática y calentamiento pre-escenario 2 min",
                        icon = Icons.Default.Air,
                        badge = "3 MODOS",
                        cardGradient = listOf(Color(0xFF075985), Color(0xFF0284C7)),
                        iconBgColor = Color(0xFF38BDF8),
                        onClick = { onNavigateToRoute("breathing_exercise") }
                    ),
                    FeatureItem(
                        title = "Gimnasia Vocal & Dicción",
                        subtitle = "Técnica del lápiz y proyección clara de sílabas",
                        icon = Icons.Default.VolumeUp,
                        badge = null,
                        cardGradient = listOf(Color(0xFF1E40AF), Color(0xFF1D4ED8)),
                        iconBgColor = Color(0xFF60A5FA),
                        onClick = { onNavigateToRoute("diction_practice") }
                    ),
                    FeatureItem(
                        title = "Trabalenguas Dinámicos",
                        subtitle = "Agilidad lingual y destrabe a contrarreloj",
                        icon = Icons.Default.GraphicEq,
                        badge = null,
                        cardGradient = listOf(Color(0xFF6B21A8), Color(0xFF9333EA)),
                        iconBgColor = Color(0xFFC084FC),
                        onClick = { onNavigateToTongueTwisters() }
                    ),
                    FeatureItem(
                        title = "Duelo de Trabalenguas",
                        subtitle = "Desafía tu velocidad de pronunciación sin tropezar",
                        icon = Icons.Default.FlashOn,
                        badge = null,
                        cardGradient = listOf(Color(0xFFC2410C), Color(0xFFEA580C)),
                        iconBgColor = Color(0xFFFB923C),
                        onClick = { onNavigateToTongueTwisterDuel() }
                    ),
                    FeatureItem(
                        title = "Análisis de Velocidad",
                        subtitle = "Mide tus palabras por minuto y ritmo escénico",
                        icon = Icons.Default.Speed,
                        badge = null,
                        cardGradient = listOf(Color(0xFF065F46), Color(0xFF047857)),
                        iconBgColor = Color(0xFF34D399),
                        onClick = { onNavigateToRoute("speech_analysis") }
                    ),
                    FeatureItem(
                        title = "Trabalenguas Offline",
                        subtitle = "Calentamiento bucal rápido sin conexión",
                        icon = Icons.Default.WifiOff,
                        badge = null,
                        cardGradient = listOf(Color(0xFF334155), Color(0xFF475569)),
                        iconBgColor = Color(0xFF94A3B8),
                        onClick = { onNavigateToTongueTwisterOffline() }
                    )
                )
            ),

            // 5. JUEGOS DE ORATORIA & DINÁMICAS
            HubCategory(
                title = "5. Juegos & Dinámicas",
                subtitle = "Refuerza reflejos y elocuencia mediante retos divertidos",
                icon = Icons.Default.SportsEsports,
                accentColor = Color(0xFF7C3AED),
                items = listOf(
                    FeatureItem(
                        title = "Esquiva Muletillas",
                        subtitle = "Elimina los 'eh' y 'este' mediante reflejos rápidos",
                        icon = Icons.Default.Shield,
                        badge = "POPULAR",
                        cardGradient = listOf(Color(0xFF991B1B), Color(0xFFDC2626)),
                        iconBgColor = Color(0xFFF87171),
                        onClick = { onNavigateToEsquivaMuletillas() }
                    ),
                    FeatureItem(
                        title = "Oratory Runner",
                        subtitle = "Responde conceptos retóricos a gran velocidad",
                        icon = Icons.Default.DirectionsRun,
                        badge = null,
                        cardGradient = listOf(Color(0xFF0C4A6E), Color(0xFF0284C7)),
                        iconBgColor = Color(0xFF38BDF8),
                        onClick = { onNavigateToOratoryRunner() }
                    ),
                    FeatureItem(
                        title = "El Gran Impostor",
                        subtitle = "Descubre al orador que finge sus argumentos",
                        icon = Icons.Default.PsychologyAlt,
                        badge = null,
                        cardGradient = listOf(Color(0xFF831843), Color(0xFFBE185D)),
                        iconBgColor = Color(0xFFF472B6),
                        onClick = { onNavigateToElGranImpostor() }
                    ),
                    FeatureItem(
                        title = "Charadas Escénicas",
                        subtitle = "Adivina expresiones y términos de oratoria",
                        icon = Icons.Default.TheaterComedy,
                        badge = null,
                        cardGradient = listOf(Color(0xFF78350F), Color(0xFFB45309)),
                        iconBgColor = Color(0xFFFBBF24),
                        onClick = { onNavigateToCharades() }
                    ),
                    FeatureItem(
                        title = "Desplazamiento Escénico",
                        subtitle = "Muévete con intención sin parecer enjaulado",
                        icon = Icons.Default.TransferWithinAStation,
                        badge = null,
                        cardGradient = listOf(Color(0xFF065F46), Color(0xFF059669)),
                        iconBgColor = Color(0xFF34D399),
                        onClick = { onNavigateToStageMovement() }
                    ),
                    FeatureItem(
                        title = "Catálogo de Juegos",
                        subtitle = "Explora todas las dinámicas de escenario",
                        icon = Icons.Default.SportsEsports,
                        badge = "SALA",
                        cardGradient = listOf(Color(0xFF4C1D95), Color(0xFF6D28D9)),
                        iconBgColor = Color(0xFFA78BFA),
                        onClick = { selectedTab = 2 }
                    )
                )
            ),

            // 6. LABORATORIO, HERRAMIENTAS & FINANZAS
            HubCategory(
                title = "6. Herramientas & Finanzas",
                subtitle = "Cronómetros oficiales, borradores y educación financiera",
                icon = Icons.Default.Construction,
                accentColor = Color(0xFF0D9488),
                items = listOf(
                    FeatureItem(
                        title = "Cronómetros Oficiales",
                        subtitle = "3 modos: cronómetro de discurso, debate y normal",
                        icon = Icons.Default.Timer,
                        badge = "3 MODOS",
                        cardGradient = listOf(Color(0xFF9A3412), Color(0xFFC2410C)),
                        iconBgColor = Color(0xFFFB923C),
                        onClick = { selectedTab = 6 }
                    ),
                    FeatureItem(
                        title = "Mis Discursos",
                        subtitle = "Escribe y guarda tus manuscritos en la base de datos",
                        icon = Icons.Default.NoteAlt,
                        badge = "GUARDADOS",
                        cardGradient = listOf(Color(0xFF1E1B4B), Color(0xFF3730A3)),
                        iconBgColor = Color(0xFF818CF8),
                        onClick = { onNavigateToRoute("speech_drafts_list") }
                    ),
                    FeatureItem(
                        title = "Notas IA & Esquemas",
                        subtitle = "Esquematiza puntos de impacto antes de subir al podio",
                        icon = Icons.Default.EditNote,
                        badge = null,
                        cardGradient = listOf(Color(0xFF78350F), Color(0xFF92400E)),
                        iconBgColor = Color(0xFFFBBF24),
                        onClick = { onNavigateToRoute("ai_notes") }
                    ),
                    FeatureItem(
                        title = "Corrector de Estilo",
                        subtitle = "Revisa ortografía y claridad de tu discurso",
                        icon = Icons.Default.FactCheck,
                        badge = null,
                        cardGradient = listOf(Color(0xFF065F46), Color(0xFF047857)),
                        iconBgColor = Color(0xFF34D399),
                        onClick = { onNavigateToRoute("spell_checker") }
                    ),
                    FeatureItem(
                        title = "Diccionario Retórico",
                        subtitle = "Glosario completo de términos de oratoria y retórica",
                        icon = Icons.Default.Spellcheck,
                        badge = null,
                        cardGradient = listOf(Color(0xFF312E81), Color(0xFF4338CA)),
                        iconBgColor = Color(0xFF818CF8),
                        onClick = { onNavigateToRoute("dictionary") }
                    ),
                    FeatureItem(
                        title = "Educación Financiera",
                        subtitle = "Gestión de ingresos y presupuestos para oradores",
                        icon = Icons.Default.AttachMoney,
                        badge = "FINANZAS",
                        cardGradient = listOf(Color(0xFF14532D), Color(0xFF166534)),
                        iconBgColor = Color(0xFF4ADE80),
                        onClick = { onNavigateToRoute("finances") }
                    )
                )
            )
        )
    }

    val verticalListState = rememberLazyListState()

    // Solo se mueve la sección que se ubique en el centro de la pantalla al hacer scroll
    val activeCategoryIndex by remember {
        derivedStateOf {
            val layoutInfo = verticalListState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty() || categories.isEmpty()) return@derivedStateOf 0

            val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2

            // Los items 0 y 1 corresponden al Banner y Consejo del Día. Los items >= 2 son las categorías
            val categoryItems = visibleItems.filter { it.index >= 2 }
            if (categoryItems.isEmpty()) return@derivedStateOf 0

            val closest = categoryItems.minByOrNull { item ->
                val itemCenter = item.offset + (item.size / 2)
                kotlin.math.abs(itemCenter - viewportCenter)
            }

            (closest?.index?.minus(2))?.coerceIn(0, (categories.size - 1).coerceAtLeast(0)) ?: 0
        }
    }

    Scaffold(
        containerColor = LightAppBackground,
        topBar = {
            Surface(
                color = Color.White,
                shadowElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Título / Marca
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "AISO",
                            fontSize = 24.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Black,
                            color = DarkTextTitle,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFEEF2FF),
                            border = BorderStroke(1.dp, Color(0xFFC7D2FE))
                        ) {
                            Text(
                                text = "PRO",
                                color = Color(0xFF4338CA),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Acciones a la Derecha: Botón Destacado de IA + Botón Consejo
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // BOTÓN DE ACCESO DIRECTO A LA IA - RESALTADO Y 100% VISIBLE
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF4338CA),
                            shadowElevation = 4.dp,
                            border = BorderStroke(1.5.dp, Color(0xFF818CF8)),
                            modifier = Modifier
                                .testTag("top_btn_aiso_ia")
                                .clickable { onNavigateToRoute("aiso_chat") }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AISO IA",
                                    tint = Color(0xFFFDE047),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "AISO IA",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        // Botón para rotar el Consejo del Día
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF1F5F9),
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                            modifier = Modifier
                                .size(38.dp)
                                .clickable { tipIndex++ }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = "Siguiente Consejo",
                                    tint = Color(0xFF6366F1),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigateToRoute("aiso_chat") },
                containerColor = Color(0xFF4338CA),
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                modifier = Modifier.testTag("fab_aiso_ia")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFFFDE047),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AISO IA",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = verticalListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. BANNER DESTACADO DE ACCESO A AISO IA (TUTOR VIRTUAL)
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clickable { onNavigateToRoute("aiso_chat") }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF312E81), Color(0xFF4338CA), Color(0xFF6366F1))
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .border(1.5.dp, Color(0xFFFDE047), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFFFDE047),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "TUTOR AISO IA",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White,
                                            letterSpacing = 0.5.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFFFDE047)
                                        ) {
                                            Text(
                                                text = "EN VIVO",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFF1E1B4B),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Tu mentor de oratoria y discursos 24/7",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                shadowElevation = 2.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "CHATEAR",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4338CA)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = Color(0xFF4338CA),
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. CONSEJO DEL DÍA INTEGRADO (COLOR ÍNDIGO / ORO CÁLIDO) - CAMBIO DINÁMICO CADA 10S
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        shadowElevation = 3.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { tipIndex++ }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF3730A3))
                                    )
                                )
                                .padding(18.dp)
                        ) {
                            AnimatedContent(
                                targetState = currentTip,
                                transitionSpec = {
                                    (fadeIn() + slideInVertically { height -> height / 2 })
                                        .togetherWith(fadeOut() + slideOutVertically { height -> -height / 2 })
                                },
                                label = "DailyTipAnimation"
                            ) { tip ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color(0xFF4F46E5).copy(alpha = 0.45f))
                                            .border(1.2.dp, Color(0xFFA5B4FC), RoundedCornerShape(16.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = tip.icon,
                                            contentDescription = null,
                                            tint = Color(0xFFFDE047),
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFFFDE047).copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = "💡 CONSEJO DEL DÍA",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFFDE047),
                                                    letterSpacing = 1.sp,
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                                )
                                            }

                                            Text(
                                                text = "Cada 10s • Toca",
                                                fontSize = 10.sp,
                                                color = Color.White.copy(alpha = 0.65f)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = tip.title,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = tip.description,
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.85f),
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. LAS 6 SECCIONES A LO LARGO - SOLO SE DESPLAZA LA QUE ESTÉ CENTRADA EN PANTALLA
            itemsIndexed(categories) { index, category ->
                AutoScrollingCategorySection(
                    category = category,
                    isAutoScrolling = (activeCategoryIndex == index)
                )
            }
        }
    }
}

@Composable
fun AutoScrollingCategorySection(
    category: HubCategory,
    isAutoScrolling: Boolean
) {
    val listState = rememberLazyListState()

    // Desplazamiento lateral suave y continuo SOLAMENTE cuando esta sección está centrada
    LaunchedEffect(isAutoScrolling) {
        if (!isAutoScrolling) return@LaunchedEffect

        // Pausa breve para estabilizar tras el desplazamiento vertical
        kotlinx.coroutines.delay(300L)
        var movingForward = true

        while (isActive && isAutoScrolling) {
            if (!listState.isScrollInProgress) {
                if (movingForward) {
                    if (listState.canScrollForward) {
                        listState.dispatchRawDelta(2.5f)
                    } else if (listState.canScrollBackward) {
                        // Llegó al extremo derecho: pausar y regresar hacia la izquierda
                        kotlinx.coroutines.delay(1400L)
                        movingForward = false
                    }
                } else {
                    if (listState.canScrollBackward) {
                        listState.dispatchRawDelta(-2.5f)
                    } else if (listState.canScrollForward) {
                        // Llegó al extremo izquierdo: pausar y volver a avanzar hacia la derecha
                        kotlinx.coroutines.delay(1400L)
                        movingForward = true
                    }
                }
            }
            kotlinx.coroutines.delay(20L)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Encabezado de la Sección
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(category.accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    tint = category.accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkTextTitle
                )
                Text(
                    text = category.subtitle,
                    fontSize = 12.sp,
                    color = DarkTextSubtitle
                )
            }

            if (isAutoScrolling) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = category.accentColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, category.accentColor.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(category.accentColor)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "En foco",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = category.accentColor
                        )
                    }
                }
            } else {
                Text(
                    text = "Desliza →",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkTextSubtitle.copy(alpha = 0.7f)
                )
            }
        }

        // Carrusel con Desplazamiento Lateral Automático y Manual
        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(category.items) { item ->
                HorizontalScrollCard(item = item)
            }
        }
    }
}

@Composable
fun HorizontalScrollCard(
    item: FeatureItem
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 3.dp,
        modifier = Modifier
            .width(280.dp) // Ancho cómodo y espacioso que permite leer bien sin amontonar
            .height(140.dp)
            .clickable(onClick = item.onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(item.cardGradient))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icono Grande y Destacado
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(item.iconBgColor.copy(alpha = 0.35f))
                            .border(1.2.dp, item.iconBgColor, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    if (item.badge != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.22f)
                        ) {
                            Text(
                                text = item.badge,
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Abrir",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.subtitle,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 15.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
