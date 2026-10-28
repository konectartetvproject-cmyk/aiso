package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

// Paleta Premium Luxury & Dark Elegance
private val ObsidianBlack = Color(0xFF090A0F)
private val DeepSlate = Color(0xFF111420)
private val LuxuryCardBg = Color(0xFF161A29)
private val GoldAccent = Color(0xFFD4AF37) // Oro clásico
private val GoldGleam = Color(0xFFFFDF73)  // Resplandor dorado
private val BronzeGlow = Color(0xFF996515)
private val RoyalViolet = Color(0xFF4C1D95)
private val ElectricCyan = Color(0xFF06B6D4)

@Composable
fun HomeScreen(
    onNavigateToChat: () -> Unit,
    onNavigateToLive: () -> Unit,
    onNavigateToQuotes: () -> Unit,
    onNavigateToHub: () -> Unit = {}
) {
    // Animación de pulso continuo de lujo en el fondo
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowAnim by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        // Fondo decorativo con halos de luz dorada y púrpura real
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(GoldAccent.copy(alpha = 0.12f * glowAnim), Color.Transparent),
                    center = Offset(width * 0.85f, height * 0.12f),
                    radius = 350f
                ),
                radius = 350f,
                center = Offset(width * 0.85f, height * 0.12f)
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(RoyalViolet.copy(alpha = 0.18f * glowAnim), Color.Transparent),
                    center = Offset(width * 0.15f, height * 0.45f),
                    radius = 420f
                ),
                radius = 420f,
                center = Offset(width * 0.15f, height * 0.45f)
            )
        }

        Scaffold(
            containerColor = Color.Transparent
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item { Spacer(modifier = Modifier.height(6.dp)) }

                // 1. HEADER DE LUJO Y BIENVENIDA
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "AISO",
                                    fontSize = 34.sp,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    letterSpacing = 2.sp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = GoldAccent.copy(alpha = 0.18f),
                                    border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.6f))
                                ) {
                                    Text(
                                        text = "ÉLITE",
                                        color = GoldGleam,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.2.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "El Arte de la Oratoria y la Persuasión",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.65f),
                                letterSpacing = 0.5.sp
                            )
                        }

                        // Avatar distintivo con corona dorada
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(GoldAccent, BronzeGlow, ObsidianBlack))
                                )
                                .border(1.5.dp, GoldGleam, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = "Insignia Élite",
                                tint = GoldGleam,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                // 2. HERO CARD PREMIUM: EN VIVO CON IA MULTIMODAL
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(26.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF1E1035),
                                        Color(0xFF2A1647),
                                        Color(0xFF130E26)
                                    )
                                )
                            )
                            .border(
                                width = 1.2.dp,
                                brush = Brush.linearGradient(
                                    listOf(GoldGleam.copy(alpha = 0.8f), RoyalViolet.copy(alpha = 0.4f), Color.Transparent)
                                ),
                                shape = RoundedCornerShape(26.dp)
                            )
                            .clickable { onNavigateToLive() }
                            .padding(22.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = CoralWarning.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, CoralWarning.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(CoralWarning)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "VIDEO EN VIVO • IA",
                                            color = CoralWarning,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(GoldAccent.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Videocam,
                                        contentDescription = null,
                                        tint = GoldGleam,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = "Entrenador Escénico en Tiempo Real",
                                fontSize = 21.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Perfecciona tu postura, mirada en 180°, zona de influencia y modulación vocal con visión artificial de Gemini.",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                lineHeight = 19.sp
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = onNavigateToLive,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldAccent,
                                    contentColor = ObsidianBlack
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Comenzar Práctica con Cámara",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }

                // 3. TARJETAS DUALES DE ALTA GAMA (TUTOR IA & HISTORIAS)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Tarjeta: AISO Tutor Inteligente
                        LuxuryMiniCard(
                            title = "Tutor AISO",
                            subtitle = "Consultas estratégicas basadas en el libro",
                            badgeText = "MENTOR IA",
                            icon = Icons.Default.AutoAwesome,
                            iconTint = GoldGleam,
                            borderBrush = Brush.verticalGradient(listOf(GoldAccent.copy(alpha = 0.6f), Color.Transparent)),
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToChat
                        )

                        // Tarjeta: Historias de Inspiración
                        LuxuryMiniCard(
                            title = "Historias & Tips",
                            subtitle = "Frases, afirmaciones y tips en formato Instagram",
                            badgeText = "FORMATO STORY",
                            icon = Icons.Default.FormatQuote,
                            iconTint = ElectricCyan,
                            borderBrush = Brush.verticalGradient(listOf(ElectricCyan.copy(alpha = 0.6f), Color.Transparent)),
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToQuotes
                        )
                    }
                }

                // 4. ACCESO AL HUB COMPLETO: CATÁLOGO MAESTRO & JUEGOS
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        DeepSlate,
                                        Color(0xFF181C2E),
                                        DeepSlate
                                    )
                                )
                            )
                            .border(
                                width = 1.dp,
                                brush = Brush.horizontalGradient(
                                    listOf(GoldAccent.copy(alpha = 0.5f), Color.White.copy(alpha = 0.1f), GoldAccent.copy(alpha = 0.5f))
                                ),
                                shape = RoundedCornerShape(22.dp)
                            )
                            .clickable { onNavigateToHub() }
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        Brush.linearGradient(listOf(GoldAccent, BronzeGlow))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GridView,
                                    contentDescription = null,
                                    tint = ObsidianBlack,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Centro de Maestría & Dinámicas",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Taller, discursos históricos, juegos escénicos, cronómetros y módulo de finanzas.",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = "Entrar al Hub",
                                tint = GoldGleam,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // 5. PILARES DE LA TRINIDAD DE LA INFLUENCIA (FILOSOFÍA DEL LIBRO)
                item {
                    Text(
                        text = "LA TRINIDAD DE LA INFLUENCIA",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 6.dp)
                    )
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PillarRow(
                            number = "I",
                            title = "Inteligencia Intrapersonal",
                            description = "Dominio del miedo escénico, respiración diafragmática y autoconfianza.",
                            icon = Icons.Default.Psychology
                        )
                        PillarRow(
                            number = "II",
                            title = "Inteligencia Interpersonal",
                            description = "Contacto visual 180°, empatía inmediata y lectura certera del auditorio.",
                            icon = Icons.Default.Groups
                        )
                        PillarRow(
                            number = "III",
                            title = "Inteligencia Emocional",
                            description = "Manejo de participantes difíciles, calma bajo presión y pasión magnética.",
                            icon = Icons.Default.Shield
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun LuxuryMiniCard(
    title: String,
    subtitle: String,
    badgeText: String,
    icon: ImageVector,
    iconTint: Color,
    borderBrush: Brush,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(LuxuryCardBg)
            .border(1.dp, borderBrush, RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = iconTint.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = badgeText,
                        color = iconTint,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                fontSize = 16.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.65f),
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
private fun PillarRow(
    number: String,
    title: String,
    description: String,
    icon: ImageVector
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = DeepSlate,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(GoldAccent.copy(alpha = 0.15f))
                    .border(1.dp, GoldAccent.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number,
                    color = GoldGleam,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
