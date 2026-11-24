package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Affirmation
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import java.util.Random

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AffirmationListScreen(
    affirmations: List<Affirmation>,
    onBack: () -> Unit,
    onAffirmationClick: (String) -> Unit
) {
    val categories = affirmations.map { it.category }.distinct()
    var selectedCategory by remember { mutableStateOf(if (categories.isNotEmpty()) categories[0] else "") }
    
    val filteredAffirmations = remember(selectedCategory, affirmations) {
        affirmations.filter { it.category == selectedCategory }
    }
    val pagerState = rememberPagerState(pageCount = { filteredAffirmations.size })

    LaunchedEffect(selectedCategory) {
        if (filteredAffirmations.isNotEmpty()) {
            pagerState.scrollToPage(0)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (filteredAffirmations.isNotEmpty()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val affirmation = filteredAffirmations[page]
                AffirmationCard(affirmation = affirmation, onPracticeClick = { onAffirmationClick(affirmation.id) })
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                }
                Text("Afirmaciones", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            if (categories.isNotEmpty()) {
                androidx.compose.foundation.lazy.LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(categories.size) { index ->
                        val category = categories[index]
                        val isSelected = category == selectedCategory
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) Color.White else Color.White.copy(alpha = 0.22f))
                                .clickable { selectedCategory = category }
                                .padding(horizontal = 18.dp, vertical = 9.dp)
                        ) {
                            Text(
                                text = category,
                                color = if (isSelected) Color.Black else Color.White,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AffirmationCard(affirmation: Affirmation, onPracticeClick: () -> Unit) {
    val randomSeed = Math.abs(affirmation.text.hashCode())
    val random = Random(randomSeed.toLong())
    
    val colors = listOf(
        listOf(Color(0xFF834D9B), Color(0xFFD04ED6)),
        listOf(Color(0xFF00B4DB), Color(0xFF0083B0)),
        listOf(Color(0xFF11998E), Color(0xFF38EF7D)),
        listOf(Color(0xFFFDC830), Color(0xFFF37335)),
        listOf(Color(0xFFff9966), Color(0xFFff5e62)),
        listOf(Color(0xFF1E3C72), Color(0xFF2A5298))
    )
    val colorPair = colors[Math.abs(affirmation.category.hashCode()) % colors.size]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colorPair))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            
            for (i in 0..15) {
                drawCircle(
                    color = Color.White.copy(alpha = random.nextFloat() * 0.4f + 0.1f),
                    radius = random.nextFloat() * 12f + 4f,
                    center = Offset(random.nextFloat() * canvasWidth, random.nextFloat() * canvasHeight)
                )
            }
            
            for (i in 1..3) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = i * 250f,
                    center = Offset(canvasWidth / 2, canvasHeight / 2),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(top = 128.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .padding(horizontal = 26.dp, vertical = 36.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier
                            .size(48.dp)
                            .offset(y = (-8).dp)
                    )
                    Text(
                        text = affirmation.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = affirmation.text,
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp,
                        modifier = Modifier.graphicsLayer {
                            shadowElevation = 4.dp.toPx()
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier
                            .width(60.dp)
                            .padding(bottom = 14.dp)
                    )
                    Text(
                        text = affirmation.category.uppercase(),
                        fontSize = 13.sp,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onPracticeClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.height(54.dp)
            ) {
                Icon(Icons.Default.Mic, contentDescription = "Practicar", modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Practicar Afirmación", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}
