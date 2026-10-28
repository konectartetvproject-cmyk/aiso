package com.example.ui.screens.english

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.RoyalPurple

data class EnglishModuleItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val route: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnglishLearningMainScreen(
    onNavigateToRoute: (String) -> Unit,
    onBack: (() -> Unit)? = null
) {
    val modules = listOf(
        EnglishModuleItem(
            id = "vocab",
            title = "Vocabulario por Categorías",
            subtitle = "Tarjetas de estudio, pronunciación y fonética",
            icon = Icons.Default.Translate,
            color = Color(0xFF1976D2)
        ),
        EnglishModuleItem(
            id = "pitch",
            title = "Business Pitch en Inglés",
            subtitle = "Frases clave para presentaciones ejecutivas y discursos",
            icon = Icons.Default.Work,
            color = RoyalPurple
        ),
        EnglishModuleItem(
            id = "pronunciation",
            title = "Práctica de Pronunciación y Dicción",
            subtitle = "Entrena sonidos difíciles (th, r, v/b, short vowels)",
            icon = Icons.Default.RecordVoiceOver,
            color = AmberGold
        ),
        EnglishModuleItem(
            id = "impromptu_en",
            title = "Impromptu Speech (English Mode)",
            subtitle = "Habla durante 60 segundos sobre temas aleatorios en inglés",
            icon = Icons.Default.Mic,
            color = Color(0xFF2E7D32)
        ),
        EnglishModuleItem(
            id = "defense_en",
            title = "Q&A & Defense in English",
            subtitle = "Responde preguntas imprevistas con soltura y seguridad",
            icon = Icons.Default.Shield,
            color = Color(0xFFD32F2F)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = RoyalPurple,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "English for Public Speaking",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
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
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = RoyalPurple.copy(alpha = 0.1f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Domina la Oratoria Global 🌐",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = RoyalPurple
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Aprende vocabulario profesional, estructuras retóricas y técnicas de delivery en inglés para llevar tus conferencias al nivel internacional.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            items(modules) { module ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            when (module.id) {
                                "vocab" -> onNavigateToRoute("dictionary")
                                "pitch" -> onNavigateToRoute("techniques")
                                "pronunciation" -> onNavigateToRoute("diction_practice")
                                "impromptu_en" -> onNavigateToRoute("impromptu")
                                "defense_en" -> onNavigateToRoute("oratory_defense")
                                else -> {}
                            }
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(module.color.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = module.icon,
                                contentDescription = null,
                                tint = module.color,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = module.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = module.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
