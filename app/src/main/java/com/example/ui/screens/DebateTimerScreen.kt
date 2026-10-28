package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebateTimerScreen(
    onBack: () -> Unit
) {
    var teamASeconds by remember { mutableStateOf(180) }
    var teamBSeconds by remember { mutableStateOf(180) }
    var activeTeam by remember { mutableStateOf(0) }

    LaunchedEffect(activeTeam) {
        while (activeTeam != 0) {
            delay(1000L)
            if (activeTeam == 1 && teamASeconds > 0) {
                teamASeconds--
            } else if (activeTeam == 2 && teamBSeconds > 0) {
                teamBSeconds--
            } else {
                activeTeam = 0
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cronómetro de Debate", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        activeTeam = 0
                        teamASeconds = 180
                        teamBSeconds = 180
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reiniciar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (activeTeam == 2) Color(0xFFFF9800) else Color(0xFFFF9800).copy(alpha = 0.3f))
                    .clickable { activeTeam = if (activeTeam == 2) 0 else 2 },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Equipo B", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                    val minutesB = teamBSeconds / 60
                    val secondsB = teamBSeconds % 60
                    Text(
                        text = String.format("%02d:%02d", minutesB, secondsB),
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { activeTeam = 0 }) {
                    Icon(if (activeTeam == 0) Icons.Default.PlayArrow else Icons.Default.Pause, contentDescription = "Pausar", modifier = Modifier.size(32.dp))
                }
            }
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (activeTeam == 1) Color(0xFF2196F3) else Color(0xFF2196F3).copy(alpha = 0.3f))
                    .clickable { activeTeam = if (activeTeam == 1) 0 else 1 },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Equipo A", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                    val minutesA = teamASeconds / 60
                    val secondsA = teamASeconds % 60
                    Text(
                        text = String.format("%02d:%02d", minutesA, secondsA),
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
