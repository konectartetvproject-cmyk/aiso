package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Recommendation(
    val title: String,
    val text: String,
    val category: String,
    val colors: List<Color>
)

val recommendationsList = listOf(
    // Inicio
    Recommendation("Conexión Inicial", "Empieza con una historia breve, una pregunta retórica o un dato impactante para captar la atención desde el primer segundo.", "Inicio", listOf(Color(0xFF1E3C72), Color(0xFF2A5298))),
    Recommendation("El Silencio Atrapa", "Antes de pronunciar tu primera palabra, haz contacto visual y sonríe ligeramente durante 3 segundos. Ese silencio crea expectativa.", "Inicio", listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))),
    Recommendation("Promesa de Valor", "Dile a tu audiencia exactamente qué van a ganar o aprender al escucharte en los próximos minutos.", "Inicio", listOf(Color(0xFFFF416C), Color(0xFFFF4B2B))),
    Recommendation("Declaración Sorprendente", "Inicia con una afirmación contraintuitiva o sorprendente que desafíe las creencias comunes y despierte la curiosidad.", "Inicio", listOf(Color(0xFF11998E), Color(0xFF38EF7D))),
    Recommendation("Interacción Inmediata", "Haz que la audiencia levante la mano o responda a una pregunta sencilla para romper el hielo e involucrarlos activamente.", "Inicio", listOf(Color(0xFF834D9B), Color(0xFFD04ED6))),
    
    // Cierre
    Recommendation("Cierre Circular", "Termina tu presentación haciendo referencia a la misma historia o concepto con el que empezaste.", "Cierre", listOf(Color(0xFF00B4DB), Color(0xFF0083B0))),
    Recommendation("Llamado a la Acción (Call to Action)", "No dejes a tu audiencia a la deriva. Diles exactamente qué acción quieres que tomen con la información que les diste.", "Cierre", listOf(Color(0xFFFDC830), Color(0xFFF37335))),
    Recommendation("La Última Frase", "La última oración que digas debe ser breve, poderosa y fácil de recordar. Es lo que se llevarán a casa.", "Cierre", listOf(Color(0xFF00C9FF), Color(0xFF92FE9D))),
    Recommendation("Cita Inspiradora", "Termina con una frase célebre bien elegida que encapsule perfectamente tu mensaje principal.", "Cierre", listOf(Color(0xFFff9966), Color(0xFFff5e62))),
    Recommendation("Visión del Futuro", "Pinta una imagen mental clara y positiva de cómo será el mundo o la vida de la audiencia si aplican tu mensaje.", "Cierre", listOf(Color(0xFFDA22FF), Color(0xFF9733EE))),
    
    // Voz
    Recommendation("Matices y Volumen", "Evita hablar siempre al mismo volumen. Usa un tono más bajo para crear intimidad y sube el volumen para enfatizar puntos clave.", "Voz", listOf(Color(0xFFf12711), Color(0xFFf5af19))),
    Recommendation("Pausas Estratégicas", "Las pausas son la puntuación de tu voz. Úsalas antes de revelar algo importante y después para dejar que se asimile.", "Voz", listOf(Color(0xFF11998E), Color(0xFF38EF7D))),
    Recommendation("Articulación Clara", "Mueve bien los músculos de tu boca al hablar. Si no se te entiende, el mensaje se pierde.", "Voz", listOf(Color(0xFFff9966), Color(0xFFff5e62))),
    Recommendation("Velocidad Controlada", "Habla más rápido para inyectar energía y entusiasmo, y más lento para enfatizar puntos graves o complejos.", "Voz", listOf(Color(0xFF1E3C72), Color(0xFF2A5298))),
    Recommendation("Inflexión de Autoridad", "Evita terminar tus oraciones afirmativas con un tono ascendente (como si fuera pregunta). Un tono final descendente denota seguridad.", "Voz", listOf(Color(0xFFFDC830), Color(0xFFF37335))),
    
    // Lenguaje Corporal
    Recommendation("Contacto Visual Constante", "No mires al suelo ni al techo. Reparte tu mirada en forma de 'W' o 'Z' para incluir a toda la audiencia.", "Lenguaje Corporal", listOf(Color(0xFF4CB8C4), Color(0xFF3CD3AD))),
    Recommendation("Manos Abiertas", "Mostrar las palmas de las manos transmite honestidad y confianza. Evita cruzarte de brazos.", "Lenguaje Corporal", listOf(Color(0xFF834D9B), Color(0xFFD04ED6))),
    Recommendation("Uso del Espacio", "No te quedes estático como una estatua, pero tampoco camines sin sentido (efecto león enjaulado). Muévete con un propósito.", "Lenguaje Corporal", listOf(Color(0xFFDA22FF), Color(0xFF9733EE))),
    Recommendation("Sonrisa Genuina", "Una sonrisa en momentos apropiados relaja a la audiencia y te hace parecer más accesible, carismático y seguro.", "Lenguaje Corporal", listOf(Color(0xFF00B4DB), Color(0xFF0083B0))),
    Recommendation("Postura de Poder", "Mantén la espalda recta, los hombros relajados y los pies firmes para proyectar confianza y facilitar una mejor respiración.", "Lenguaje Corporal", listOf(Color(0xFFFF416C), Color(0xFFFF4B2B))),
    
    // Muletillas
    Recommendation("El Poder del Silencio", "La mejor cura para las muletillas ('eh', 'mmm', 'este') es reemplazarlas por silencio. Acostúmbrate a las pausas.", "Muletillas", listOf(Color(0xFF141E30), Color(0xFF243B55))),
    Recommendation("Grabación Personal", "Grábate hablando 2 minutos sobre cualquier tema. Escúchate para identificar cuáles son tus muletillas más frecuentes.", "Muletillas", listOf(Color(0xFF00B4DB), Color(0xFF0083B0))),
    Recommendation("Respiración Profunda", "Las muletillas a veces aparecen cuando nos falta aire o estamos nerviosos. Respira profundamente desde el diafragma.", "Muletillas", listOf(Color(0xFF1E3C72), Color(0xFF2A5298))),
    Recommendation("Mapeo de Transiciones", "Las muletillas ocurren frecuentemente al pasar de una idea a otra. Ensaya especialmente tus frases de transición.", "Muletillas", listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))),
    Recommendation("Contacto Visual como Freno", "Cuando sientas que vas a usar una muletilla, mira fijamente a una persona. Esto fomenta una pausa limpia en lugar de ruido.", "Muletillas", listOf(Color(0xFF4CB8C4), Color(0xFF3CD3AD))),
    
    // Inteligencia Emocional
    Recommendation("Convierte los Nervios en Entusiasmo", "Físicamente, los nervios y la emoción son casi idénticos (palpitaciones, sudoración). Dile a tu cerebro 'estoy emocionado'.", "Inteligencia Emocional", listOf(Color(0xFFFF416C), Color(0xFFFF4B2B))),
    Recommendation("Empatía con la Audiencia", "Piensa en las necesidades y preocupaciones de quienes te escuchan. Háblales a ellos, no a ti mismo.", "Inteligencia Emocional", listOf(Color(0xFFDA22FF), Color(0xFF9733EE))),
    Recommendation("Manejo del Error", "Si te equivocas, no te disculpes en exceso ni entres en pánico. Corrige rápidamente o haz una pequeña broma y continúa con confianza.", "Inteligencia Emocional", listOf(Color(0xFFf12711), Color(0xFFf5af19))),
    Recommendation("Lectura de Sala (Read the Room)", "Observa las expresiones de tu audiencia. Si parecen aburridos, cambia el ritmo; si parecen confundidos, simplifica tu mensaje.", "Inteligencia Emocional", listOf(Color(0xFF00C9FF), Color(0xFF92FE9D))),
    Recommendation("Autocompasión", "No seas tu peor crítico. Si olvidas una palabra o tropiezas, sé amable contigo mismo. La audiencia quiere que tengas éxito.", "Inteligencia Emocional", listOf(Color(0xFF11998E), Color(0xFF38EF7D))),
    
    // Persuasión
    Recommendation("La Regla de Tres", "Las personas recuerdan mejor la información presentada en grupos de tres. Estructura tus puntos clave en tres partes.", "Persuasión", listOf(Color(0xFF11998E), Color(0xFF38EF7D))),
    Recommendation("Apelación Emocional (Pathos)", "La lógica (Logos) hace pensar, pero la emoción hace actuar. Conecta tu mensaje con emociones universales.", "Persuasión", listOf(Color(0xFF834D9B), Color(0xFFD04ED6))),
    Recommendation("Contraste", "Presenta el problema ('Lo que es') y contrástalo con la solución ('Lo que podría ser'). Ese salto genera interés y persuasión.", "Persuasión", listOf(Color(0xFFFDC830), Color(0xFFF37335))),
    Recommendation("Prueba Social", "Menciona ejemplos de otras personas, testimonios o casos de éxito para validar tu propuesta y generar confianza.", "Persuasión", listOf(Color(0xFF1E3C72), Color(0xFF2A5298))),
    Recommendation("El Poder de 'Porque'", "Al pedir algo a la audiencia, da una razón usando la palabra 'porque'. Esto aumenta significativamente la probabilidad de aceptación.", "Persuasión", listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0)))
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecommendationsScreen(onBack: () -> Unit, showBackButton: Boolean = true) {
    val categories = listOf("Inicio", "Cierre", "Voz", "Lenguaje Corporal", "Muletillas", "Inteligencia Emocional", "Persuasión")
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    
    val filteredRecommendations = remember(selectedCategory) {
        recommendationsList.filter { it.category == selectedCategory }
    }

    val pagerState = rememberPagerState(pageCount = { filteredRecommendations.size })

    LaunchedEffect(selectedCategory) {
        pagerState.scrollToPage(0)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val recommendation = filteredRecommendations[page]
            RecommendationCard(recommendation = recommendation)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            if (showBackButton) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = Color.White
                    )
                }
            }

            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = if (showBackButton) 0.dp else 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories.size) { index ->
                    val category = categories[index]
                    Surface(
                        modifier = Modifier.clickable { selectedCategory = category },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        color = if (selectedCategory == category) Color.White.copy(alpha = 0.2f) else Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedCategory == category) Color.White else Color.White.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = category,
                            color = if (selectedCategory == category) Color.White else Color.White.copy(alpha = 0.7f),
                            fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendationCard(recommendation: Recommendation) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(recommendation.colors))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(32.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.4f),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(32.dp)
                )
                .padding(horizontal = 24.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "💡",
                fontSize = 60.sp,
                modifier = Modifier.offset(y = 10.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = recommendation.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 34.sp,
                modifier = Modifier.graphicsLayer {
                    shadowElevation = 8.dp.toPx()
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = recommendation.text,
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                lineHeight = 26.sp
            )
        }
        
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Desliza para más",
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}
