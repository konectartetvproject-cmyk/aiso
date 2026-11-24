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
    PublicSpeakingStep(1, "Entrar al Escenario (Momento I)", "Camina con serenidad, sitúate con comodidad y aparta hojas, fichas o tarjetas de lectura.", Icons.Default.ArrowForward),
    PublicSpeakingStep(2, "Mirar al Público (Momento I)", "Distribuye la atención visual con naturalidad por los sectores de la sala sin convertirla en inspección rígida.", Icons.Default.Visibility),
    PublicSpeakingStep(3, "Saludar (Momento I)", "Saludo breve, cordial y acorde al momento del día («Muy buenos días a todos»).", Icons.Default.Face),
    PublicSpeakingStep(4, "Técnica de Inicio I01–I10 (Momento II)", "Capta el interés antes de decir tu nombre usando una de las 10 técnicas de inicio del Capítulo 4.", Icons.Default.Lightbulb),
    PublicSpeakingStep(5, "Nombre del Orador (Momento II)", "Preséntate después de la técnica de inicio («Mi nombre es...»), mencionando solo lo pertinente para el tema.", Icons.Default.Person),
    PublicSpeakingStep(6, "Nombre del Tema (Momento II)", "Delimita con claridad qué se trabajará («Hoy hablaremos sobre...»).", Icons.Default.Info),
    PublicSpeakingStep(7, "Desarrollo con el Método AISO (Momento II)", "Recorre con tus propias palabras y sin leer las 3 fases: CONCEPTO → HISTORIA → TÉCNICAS O CONSEJOS, unidas por sus puentes de transición.", Icons.Default.List),
    PublicSpeakingStep(8, "Anunciar Conclusión (Momento III)", "Avisa que tu intervención llega al cierre («Para concluir...» o «En conclusión...») sin abrir un tema nuevo.", Icons.Default.Notifications),
    PublicSpeakingStep(9, "Técnica de Conclusión C01–C09 (Momento III)", "Deja la idea final con sentido claro mediante una de las 9 técnicas de conclusión del Capítulo 5.", Icons.Default.Done),
    PublicSpeakingStep(10, "Decir Gracias (Momento III)", "Cierra tus palabras con sobriedad y respeto («Muchas gracias»).", Icons.Default.Favorite),
    PublicSpeakingStep(11, "Recibir Aplausos (Momento III)", "Permanece con serenidad recibiendo los aplausos o la respuesta final de la sala antes de retirarte sin prisa.", Icons.Default.Star)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicSpeakingGuideScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Guía AISO: Los 11 Pasos (v07)", fontWeight = FontWeight.Bold) },
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
                    text = "Los 11 Pasos y el Método AISO",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Regla de práctica AISO (Carlos Enrique Lopez Adames): La preparación puede escribirse, pero al subir a exponer se apartan el papel, las fichas y el teleprompter para hablar desde el corazón y con palabras propias.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
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
