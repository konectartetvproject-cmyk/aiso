package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class PublicSpeakingStep(
    val number: Int,
    val title: String,
    val description: String,
    val icon: ImageVector
)

val guideSteps = listOf(
    PublicSpeakingStep(1, "Entrar al Escenario", "Camina con confianza y una postura erguida para establecer el tono de la presentación.", Icons.Default.ArrowForward),
    PublicSpeakingStep(2, "Mirar al Público", "Haz contacto visual con la audiencia para crear una conexión y demostrar que estás comprometido.", Icons.Default.Visibility),
    PublicSpeakingStep(3, "Saludar", "Un saludo sencillo ayuda a romper el hielo y establecer una relación amigable.", Icons.Default.Face),
    PublicSpeakingStep(4, "Técnica de Inicio", "Usa una anécdota, pregunta o cita para captar la atención del público desde el principio.", Icons.Default.Lightbulb),
    PublicSpeakingStep(5, "Nombre del Orador", "Preséntate claramente para establecer tu identidad y credibilidad.", Icons.Default.Person),
    PublicSpeakingStep(6, "Nombre del Tema", "Introduce el tema del discurso para que la audiencia sepa qué esperar.", Icons.Default.Info),
    PublicSpeakingStep(7, "Desarrollo del Discurso", "Organiza el contenido de manera lógica y utiliza ejemplos para mantener el interés.", Icons.Default.List),
    PublicSpeakingStep(8, "Anunciar Conclusión", "Avisa a la audiencia que estás por terminar para que presten atención a tus últimas palabras.", Icons.Default.Notifications),
    PublicSpeakingStep(9, "Técnica de Conclusión", "Cierra el discurso de manera efectiva con un resumen, una llamada a la acción o una cita poderosa.", Icons.Default.Done),
    PublicSpeakingStep(10, "Decir Gracias", "Agradece al público por su atención para cerrar la presentación de manera respetuosa.", Icons.Default.Favorite),
    PublicSpeakingStep(11, "Recibir Aplausos", "Mantén una postura relajada y sonríe para disfrutar y reconocer el esfuerzo de tu presentación.", Icons.Default.Star)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicSpeakingGuideScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Guía de Oratoria", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Los 11 Pasos para Hablar en Público",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sigue esta estructura probada para entregar presentaciones seguras y exitosas, basadas en los principios de AISO IA.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            items(guideSteps) { step ->
                StepCard(step)
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun StepCard(step: PublicSpeakingStep) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
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
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = step.number.toString(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = step.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = step.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = step.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
