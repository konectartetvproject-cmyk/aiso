package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.speech.tts.TextToSpeech
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.model.Affirmation
import com.example.model.QuotesCatalog
import com.example.model.sampleAffirmations
import com.example.ui.theme.*
import com.example.viewmodel.OratoryQuote
import com.example.viewmodel.QuotesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import kotlin.random.Random

enum class InspirationTab {
    FRASES,
    AFIRMACIONES,
    TIPS
}

@Composable
fun QuotesScreen(
    onBack: () -> Unit,
    showBackButton: Boolean = true,
    initialTab: InspirationTab = InspirationTab.FRASES,
    viewModel: QuotesViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onPracticeAffirmation: ((String) -> Unit)? = null
) {
    if (showBackButton) {
        BackHandler { onBack() }
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // TTS para escuchar la entonación de cualquier frase o afirmación
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(context) {
        var instance: TextToSpeech? = null
        instance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                instance?.language = Locale("es", "ES")
                instance?.setSpeechRate(0.92f)
            }
        }
        tts = instance
        onDispose {
            instance?.stop()
            instance?.shutdown()
        }
    }

    var currentTab by remember { mutableStateOf(initialTab) }
    var isCatalogMode by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }

    val quotes by viewModel.quotes.collectAsState()
    val selectedQuoteCategory by viewModel.selectedCategory.collectAsState()
    val selectedAuthor by viewModel.selectedAuthor.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val quoteCategories = QuotesCatalog.categories

    val affirmationCategories = remember {
        listOf("Todas") + sampleAffirmations.map { it.category }.distinct()
    }
    var selectedAffirmationCategory by remember { mutableStateOf("Todas") }

    val tipCategories = remember {
        listOf("Todas") + recommendationsList.map { it.category }.distinct()
    }
    var selectedTipCategory by remember { mutableStateOf("Todas") }

    // Lista de autores disponibles dentro de la categoría actual
    val availableAuthors = remember(quotes, selectedQuoteCategory) {
        val baseList = when (selectedQuoteCategory) {
            "Todas" -> quotes
            "Favoritas ❤️" -> quotes.filter { it.isFavorite }
            else -> quotes.filter { it.category == selectedQuoteCategory }
        }
        listOf("Todos") + baseList.map { it.author }.distinct()
    }

    val filteredQuotes = remember(quotes, selectedQuoteCategory, selectedAuthor, searchQuery) {
        quotes.filter { quote ->
            val matchesCategory = when (selectedQuoteCategory) {
                "Todas" -> true
                "Favoritas ❤️" -> quote.isFavorite
                else -> quote.category == selectedQuoteCategory
            }
            val matchesAuthor = selectedAuthor == "Todos" || quote.author == selectedAuthor
            val query = searchQuery.trim().lowercase()
            val matchesSearch = query.isEmpty() ||
                quote.text.lowercase().contains(query) ||
                quote.author.lowercase().contains(query) ||
                quote.source.lowercase().contains(query) ||
                quote.category.lowercase().contains(query) ||
                quote.usageTip.lowercase().contains(query)

            matchesCategory && matchesAuthor && matchesSearch
        }
    }

    val filteredAffirmations = remember(sampleAffirmations, selectedAffirmationCategory, searchQuery) {
        val query = searchQuery.trim().lowercase()
        sampleAffirmations.filter { aff ->
            val matchesCategory = selectedAffirmationCategory == "Todas" || aff.category == selectedAffirmationCategory
            val matchesSearch = query.isEmpty() ||
                aff.title.lowercase().contains(query) ||
                aff.text.lowercase().contains(query) ||
                aff.category.lowercase().contains(query)
            matchesCategory && matchesSearch
        }
    }

    val filteredTips = remember(recommendationsList, selectedTipCategory, searchQuery) {
        val query = searchQuery.trim().lowercase()
        recommendationsList.filter { tip ->
            val matchesCategory = selectedTipCategory == "Todas" || tip.category == selectedTipCategory
            val matchesSearch = query.isEmpty() ||
                tip.title.lowercase().contains(query) ||
                tip.text.lowercase().contains(query) ||
                tip.category.lowercase().contains(query)
            matchesCategory && matchesSearch
        }
    }

    val quotesPagerState = rememberPagerState(pageCount = { filteredQuotes.size })
    val affirmationsPagerState = rememberPagerState(pageCount = { filteredAffirmations.size })
    val tipsPagerState = rememberPagerState(pageCount = { filteredTips.size })

    LaunchedEffect(selectedQuoteCategory, selectedAuthor, searchQuery) {
        if (filteredQuotes.isNotEmpty()) quotesPagerState.scrollToPage(0)
    }

    LaunchedEffect(selectedAffirmationCategory, searchQuery) {
        if (filteredAffirmations.isNotEmpty()) affirmationsPagerState.scrollToPage(0)
    }

    LaunchedEffect(selectedTipCategory, searchQuery) {
        if (filteredTips.isNotEmpty()) tipsPagerState.scrollToPage(0)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B101E))
    ) {
        // Contenido Principal: Modo Historia (Swipe) o Modo Biblioteca (Catálogo)
        when (currentTab) {
            InspirationTab.FRASES -> {
                if (filteredQuotes.isEmpty()) {
                    EmptyInspirationState(
                        message = if (selectedQuoteCategory == "Favoritas ❤️") {
                            "Aún no has marcado frases favoritas ❤️.\nToca el corazón en cualquier frase para guardarla aquí."
                        } else {
                            "No se encontraron frases con esos filtros."
                        },
                        onResetFilters = {
                            viewModel.selectCategory("Todas")
                            viewModel.selectAuthor("Todos")
                            viewModel.setSearchQuery("")
                        }
                    )
                } else if (isCatalogMode) {
                    QuotesCatalogListView(
                        quotes = filteredQuotes,
                        onSelectQuote = { index ->
                            isCatalogMode = false
                            coroutineScope.launch {
                                quotesPagerState.scrollToPage(index)
                            }
                        },
                        onToggleFavorite = { id -> viewModel.toggleFavorite(id) },
                        onSpeakQuote = { quote ->
                            tts?.speak("${quote.text}. Por ${quote.author}", TextToSpeech.QUEUE_FLUSH, null, quote.id)
                        }
                    )
                } else {
                    HorizontalPager(
                        state = quotesPagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        val quote = filteredQuotes[page]
                        QuoteStoryPage(
                            quote = quote,
                            onToggleFavorite = { viewModel.toggleFavorite(quote.id) },
                            onSpeak = {
                                tts?.speak(
                                    "${quote.text}. ${quote.author}.",
                                    TextToSpeech.QUEUE_FLUSH,
                                    null,
                                    quote.id
                                )
                            }
                        )
                    }
                }
            }
            InspirationTab.AFIRMACIONES -> {
                if (filteredAffirmations.isEmpty()) {
                    EmptyInspirationState(
                        message = "No hay afirmaciones que coincidan con la búsqueda.",
                        onResetFilters = {
                            selectedAffirmationCategory = "Todas"
                            viewModel.setSearchQuery("")
                        }
                    )
                } else {
                    HorizontalPager(
                        state = affirmationsPagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        val affirmation = filteredAffirmations[page]
                        AffirmationStoryPage(
                            affirmation = affirmation,
                            onPractice = { onPracticeAffirmation?.invoke(affirmation.id) },
                            onSpeak = {
                                tts?.speak(affirmation.text, TextToSpeech.QUEUE_FLUSH, null, affirmation.id)
                            }
                        )
                    }
                }
            }
            InspirationTab.TIPS -> {
                if (filteredTips.isEmpty()) {
                    EmptyInspirationState(
                        message = "No hay tips en esta categoría.",
                        onResetFilters = {
                            selectedTipCategory = "Todas"
                            viewModel.setSearchQuery("")
                        }
                    )
                } else {
                    HorizontalPager(
                        state = tipsPagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        val tip = filteredTips[page]
                        TipStoryPage(tip = tip)
                    }
                }
            }
        }

        // Overlay Superior con diseño unificado de Historias + Buscador + Filtros de Autor
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.82f),
                            Color.Black.copy(alpha = 0.45f),
                            Color.Transparent
                        )
                    )
                )
                .statusBarsPadding()
                .padding(bottom = 12.dp)
        ) {
            // Rayitas de progreso segmentadas (Instagram Story bars)
            val currentItemCount = when (currentTab) {
                InspirationTab.FRASES -> filteredQuotes.size
                InspirationTab.AFIRMACIONES -> filteredAffirmations.size
                InspirationTab.TIPS -> filteredTips.size
            }
            val currentPage = when (currentTab) {
                InspirationTab.FRASES -> quotesPagerState.currentPage
                InspirationTab.AFIRMACIONES -> affirmationsPagerState.currentPage
                InspirationTab.TIPS -> tipsPagerState.currentPage
            }

            if (currentItemCount > 0 && !isCatalogMode) {
                val totalBars = currentItemCount.coerceAtMost(16)
                val currentBarIndex = (currentPage * totalBars) / currentItemCount.coerceAtLeast(1)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(totalBars) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (index <= currentBarIndex) AmberGold
                                    else Color.White.copy(alpha = 0.28f)
                                )
                        )
                    }
                }
            }

            // Barra superior: Botón volver + Selector de 3 pestañas + Buscador + Toggle Biblioteca/Historia + Contador
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (showBackButton) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.55f))
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    // Selector de 3 Secciones (Pills Switch)
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.Black.copy(alpha = 0.65f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                    ) {
                        Row(modifier = Modifier.padding(3.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (currentTab == InspirationTab.FRASES) AmberGold else Color.Transparent)
                                    .clickable { currentTab = InspirationTab.FRASES }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.FormatQuote,
                                        contentDescription = null,
                                        tint = if (currentTab == InspirationTab.FRASES) MidnightIndigo else Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Frases",
                                        color = if (currentTab == InspirationTab.FRASES) MidnightIndigo else Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (currentTab == InspirationTab.AFIRMACIONES) AmberGold else Color.Transparent)
                                    .clickable {
                                        currentTab = InspirationTab.AFIRMACIONES
                                        isCatalogMode = false
                                    }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = if (currentTab == InspirationTab.AFIRMACIONES) MidnightIndigo else Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Afirmaciones",
                                        color = if (currentTab == InspirationTab.AFIRMACIONES) MidnightIndigo else Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (currentTab == InspirationTab.TIPS) AmberGold else Color.Transparent)
                                    .clickable {
                                        currentTab = InspirationTab.TIPS
                                        isCatalogMode = false
                                    }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = if (currentTab == InspirationTab.TIPS) MidnightIndigo else Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Tips",
                                        color = if (currentTab == InspirationTab.TIPS) MidnightIndigo else Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Acciones derechas: Buscar + Alternar Vista Catálogo + Contador
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = { showSearchBar = !showSearchBar },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (showSearchBar || searchQuery.isNotEmpty()) AmberGold else Color.Black.copy(alpha = 0.55f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar frase o autor",
                            tint = if (showSearchBar || searchQuery.isNotEmpty()) MidnightIndigo else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (currentTab == InspirationTab.FRASES) {
                        IconButton(
                            onClick = { isCatalogMode = !isCatalogMode },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isCatalogMode) AmberGold else Color.Black.copy(alpha = 0.55f))
                        ) {
                            Icon(
                                imageVector = if (isCatalogMode) Icons.Default.ViewCarousel else Icons.Default.ViewList,
                                contentDescription = if (isCatalogMode) "Modo Historias" else "Ver Catálogo Completo",
                                tint = if (isCatalogMode) MidnightIndigo else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (isCatalogMode) "${currentItemCount}" else "${if (currentItemCount == 0) 0 else currentPage + 1}/$currentItemCount",
                            color = AmberGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Barra de búsqueda desplegable
            AnimatedVisibility(visible = showSearchBar) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = {
                        Text(
                            "Buscar por autor, libro, película, tema o palabra...",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = AmberGold)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpiar búsqueda", tint = Color.White)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Black.copy(alpha = 0.7f),
                        unfocusedContainerColor = Color.Black.copy(alpha = 0.55f),
                        focusedBorderColor = AmberGold,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            // Carrusel horizontal de áreas / categorías
            val activeCategories = when (currentTab) {
                InspirationTab.FRASES -> quoteCategories
                InspirationTab.AFIRMACIONES -> affirmationCategories
                InspirationTab.TIPS -> tipCategories
            }
            val activeSelected = when (currentTab) {
                InspirationTab.FRASES -> selectedQuoteCategory
                InspirationTab.AFIRMACIONES -> selectedAffirmationCategory
                InspirationTab.TIPS -> selectedTipCategory
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(activeCategories) { category ->
                    val isSelected = activeSelected == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (isSelected) Color.White else Color.Black.copy(alpha = 0.55f))
                            .border(
                                width = if (isSelected) 0.dp else 1.dp,
                                color = Color.White.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable {
                                when (currentTab) {
                                    InspirationTab.FRASES -> viewModel.selectCategory(category)
                                    InspirationTab.AFIRMACIONES -> selectedAffirmationCategory = category
                                    InspirationTab.TIPS -> selectedTipCategory = category
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = category,
                            color = if (isSelected) Color.Black else Color.White,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Segundo carrusel de Autores / Orígenes cuando estamos en la pestaña FRASES
            if (currentTab == InspirationTab.FRASES && availableAuthors.size > 2) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(availableAuthors) { author ->
                        val isAuthorSelected = selectedAuthor == author
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isAuthorSelected) AmberGold.copy(alpha = 0.9f)
                                    else Color.White.copy(alpha = 0.12f)
                                )
                                .clickable { viewModel.selectAuthor(author) }
                                .padding(horizontal = 11.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (author == "Todos") "✍️ Todos los autores" else author,
                                color = if (isAuthorSelected) MidnightIndigo else Color.White.copy(alpha = 0.9f),
                                fontWeight = if (isAuthorSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyInspirationState(
    message: String,
    onResetFilters: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.FormatQuote,
                contentDescription = null,
                tint = AmberGold,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = message,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onResetFilters,
                colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = MidnightIndigo),
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver todas las frases", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun QuotesCatalogListView(
    quotes: List<OratoryQuote>,
    onSelectQuote: (Int) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onSpeakQuote: (OratoryQuote) -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF0F172A))
                )
            ),
        contentPadding = PaddingValues(top = 175.dp, bottom = 36.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Surface(
                color = AmberGold.copy(alpha = 0.14f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = AmberGold)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Biblioteca de Citas, Libros y Cine (${quotes.size})",
                            color = AmberGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Toca cualquier tarjeta para abrirla en pantalla completa o copiarla para tu discurso.",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        itemsIndexed(quotes, key = { _, q -> q.id }) { index, quote ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectQuote(index) },
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                color = AmberGold.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = quote.category,
                                    color = AmberGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            if (quote.source.isNotBlank()) {
                                Text(
                                    text = "• ${quote.source}",
                                    color = Color.White.copy(alpha = 0.68f),
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Row {
                            IconButton(
                                onClick = { onSpeakQuote(quote) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Escuchar",
                                    tint = Color.White.copy(alpha = 0.85f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    copyQuoteToClipboard(context, quote)
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copiar",
                                    tint = Color.White.copy(alpha = 0.85f),
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                            IconButton(
                                onClick = { onToggleFavorite(quote.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (quote.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorito",
                                    tint = if (quote.isFavorite) CoralWarning else Color.White.copy(alpha = 0.85f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "«${quote.text}»",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Serif,
                        lineHeight = 23.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "— ${quote.author}",
                        color = AmberGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    if (quote.usageTip.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = Color.Black.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TipsAndUpdates,
                                    contentDescription = null,
                                    tint = AmberGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = quote.usageTip,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuoteStoryPage(
    quote: OratoryQuote,
    onToggleFavorite: () -> Unit,
    onSpeak: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isSharing by remember { mutableStateOf(false) }

    val gradientPalettes = listOf(
        listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364)),
        listOf(Color(0xFF4A0E4E), Color(0xFF811331), Color(0xFFB83B5E)),
        listOf(Color(0xFF141E30), Color(0xFF243B55)),
        listOf(Color(0xFF1D2671), Color(0xFFC33764)),
        listOf(Color(0xFF1A2980), Color(0xFF1E6F72)),
        listOf(Color(0xFF134E5E), Color(0xFF2E7D5B)),
        listOf(Color(0xFF2B1055), Color(0xFF552586)),
        listOf(Color(0xFF3A1C71), Color(0xFF8E3B46), Color(0xFF4A2545)),
        listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF172554)),
        listOf(Color(0xFF23074D), Color(0xFF753A88))
    )

    val paletteIndex = Math.abs(quote.id.hashCode()) % gradientPalettes.size
    val currentPalette = gradientPalettes[paletteIndex]
    val seed = Math.abs(quote.id.hashCode().toLong())
    val random = remember(quote.id) { Random(seed) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(currentPalette))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            for (i in 1..4) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.04f),
                    radius = i * 230f,
                    center = Offset(canvasWidth * 0.5f, canvasHeight * 0.46f),
                    style = Stroke(width = 3f)
                )
            }

            for (i in 0..18) {
                val radius = random.nextFloat() * 10f + 3f
                val alpha = random.nextFloat() * 0.30f + 0.06f
                drawCircle(
                    color = Color.White.copy(alpha = alpha),
                    radius = radius,
                    center = Offset(random.nextFloat() * canvasWidth, random.nextFloat() * canvasHeight)
                )
            }
        }

        // Tarjeta central estilo Historia de Instagram
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 20.dp)
                .padding(top = 80.dp, bottom = 74.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(Color.Black.copy(alpha = 0.48f))
                .border(
                    width = 1.2.dp,
                    brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.45f), Color.White.copy(alpha = 0.05f))),
                    shape = RoundedCornerShape(32.dp)
                )
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(AmberGold.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FormatQuote,
                    contentDescription = null,
                    tint = AmberGold,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Etiqueta de Categoría
            Surface(
                color = AmberGold.copy(alpha = 0.18f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = quote.category.uppercase(),
                    color = AmberGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "«${quote.text}»",
                color = Color.White,
                fontSize = if (quote.text.length > 165) 18.sp else 21.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                lineHeight = if (quote.text.length > 165) 27.sp else 31.sp,
                modifier = Modifier.graphicsLayer { shadowElevation = 8.dp.toPx() }
            )

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(2.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(AmberGold)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "— ${quote.author}",
                color = AmberGold,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                letterSpacing = 0.6.sp
            )

            // Fuente: Libro, Película, Obra o Discurso
            if (quote.source.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when {
                                quote.category.contains("Cine") -> Icons.Default.Movie
                                quote.category.contains("Libros") -> Icons.Default.MenuBook
                                quote.category.contains("Discursos") -> Icons.Default.RecordVoiceOver
                                else -> Icons.Default.AutoAwesome
                            },
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = quote.source,
                            color = Color.White.copy(alpha = 0.88f),
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Tip Retórico: Cómo usar esta frase en un discurso
            if (quote.usageTip.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = AmberGold.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TipsAndUpdates,
                            contentDescription = null,
                            tint = AmberGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "USO EN EL ESCENARIO",
                                color = AmberGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = quote.usageTip,
                                color = Color.White.copy(alpha = 0.92f),
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }

        // Barra inferior con Favorito, Escuchar TTS, Copiar y Compartir Historia
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f))
                        .border(1.dp, Color.White.copy(alpha = 0.22f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (quote.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (quote.isFavorite) CoralWarning else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = onSpeak,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f))
                        .border(1.dp, Color.White.copy(alpha = 0.22f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Escuchar Entonación",
                        tint = AmberGold,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = { copyQuoteToClipboard(context, quote) },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f))
                        .border(1.dp, Color.White.copy(alpha = 0.22f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copiar Frase",
                        tint = Color.White,
                        modifier = Modifier.size(21.dp)
                    )
                }

                Button(
                    onClick = {
                        if (!isSharing) {
                            isSharing = true
                            coroutineScope.launch {
                                shareQuoteAsImage(context, quote) { isSharing = false }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = MidnightIndigo),
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    if (isSharing) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MidnightIndigo, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Creando...", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    } else {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Compartir", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Swipe, contentDescription = null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Desliza para explorar más frases o usa el botón de lista arriba", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            }
        }
    }
}

private fun copyQuoteToClipboard(context: Context, quote: OratoryQuote) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val sourceSuffix = if (quote.source.isNotBlank()) " (${quote.source})" else ""
    val formatted = "«${quote.text}» — ${quote.author}$sourceSuffix"
    clipboard.setPrimaryClip(ClipData.newPlainText("Frase AISO", formatted))
    Toast.makeText(context, "Frase copiada al portapapeles", Toast.LENGTH_SHORT).show()
}

@Composable
fun AffirmationStoryPage(
    affirmation: Affirmation,
    onPractice: () -> Unit,
    onSpeak: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isSharing by remember { mutableStateOf(false) }

    val gradientPalettes = listOf(
        listOf(Color(0xFF4A154B), Color(0xFF834D9B)),
        listOf(Color(0xFF0F2027), Color(0xFF0083B0)),
        listOf(Color(0xFF0F4C3A), Color(0xFF11998E)),
        listOf(Color(0xFF7C2D12), Color(0xFFD97706)),
        listOf(Color(0xFF4C1D95), Color(0xFF7C3AED)),
        listOf(Color(0xFF1E3C72), Color(0xFF2A5298))
    )

    val paletteIndex = Math.abs(affirmation.category.hashCode()) % gradientPalettes.size
    val currentPalette = gradientPalettes[paletteIndex]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(currentPalette))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            for (i in 1..4) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = i * 220f,
                    center = Offset(canvasWidth * 0.5f, canvasHeight * 0.44f),
                    style = Stroke(width = 3f)
                )
            }
        }

        // Tarjeta central estilo Historia de Instagram
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(Color.Black.copy(alpha = 0.45f))
                .border(
                    width = 1.2.dp,
                    brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.45f), Color.White.copy(alpha = 0.05f))),
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(horizontal = 26.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = Color.White.copy(alpha = 0.18f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "✨ ${affirmation.category.uppercase()}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = affirmation.title,
                color = AmberGold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "«${affirmation.text}»",
                color = Color.White,
                fontSize = 21.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                lineHeight = 31.sp,
                modifier = Modifier.graphicsLayer { shadowElevation = 8.dp.toPx() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(2.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(Color.White.copy(alpha = 0.6f))
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Afirmación Diaria para Oradores",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }

        // Barra inferior
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onSpeak,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Escuchar afirmación",
                        tint = AmberGold
                    )
                }

                Button(
                    onClick = {
                        if (!isSharing) {
                            isSharing = true
                            coroutineScope.launch {
                                shareAffirmationAsImage(context, affirmation) { isSharing = false }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.25f), contentColor = Color.White),
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier.height(50.dp)
                ) {
                    if (isSharing) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Creando...", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Compartir", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Button(
                    onClick = onPractice,
                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = MidnightIndigo),
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier.height(50.dp)
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Practicar con Voz", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Swipe, contentDescription = null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Desliza horizontalmente para ver la siguiente afirmación", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun TipStoryPage(tip: Recommendation) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isSharing by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(tip.colors))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            for (i in 1..4) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = i * 220f,
                    center = Offset(canvasWidth * 0.5f, canvasHeight * 0.44f),
                    style = Stroke(width = 3f)
                )
            }
        }

        // Tarjeta central estilo Historia de Instagram
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(Color.Black.copy(alpha = 0.48f))
                .border(
                    width = 1.2.dp,
                    brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.45f), Color.White.copy(alpha = 0.05f))),
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(horizontal = 26.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(AmberGold.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = AmberGold,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = AmberGold.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "💡 TIP DE ORATORIA • ${tip.category.uppercase()}",
                    color = AmberGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = tip.title,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = tip.text,
                color = Color.White.copy(alpha = 0.95f),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(26.dp))

            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(2.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(AmberGold)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Técnica Práctica de Escenario",
                color = AmberGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }

        // Barra inferior
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    if (!isSharing) {
                        isSharing = true
                        coroutineScope.launch {
                            shareTipAsImage(context, tip) { isSharing = false }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = MidnightIndigo),
                shape = RoundedCornerShape(26.dp),
                modifier = Modifier.height(50.dp)
            ) {
                if (isSharing) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MidnightIndigo, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Creando...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Compartir Tip en Historias", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Swipe, contentDescription = null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Desliza horizontalmente para ver el siguiente tip", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            }
        }
    }
}

suspend fun shareTipAsImage(context: Context, tip: Recommendation, onComplete: () -> Unit) {
    withContext(Dispatchers.IO) {
        try {
            val width = 1080
            val height = 1920
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            val shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                intArrayOf(
                    android.graphics.Color.parseColor("#0F172A"),
                    android.graphics.Color.parseColor("#1E293B"),
                    android.graphics.Color.parseColor("#334155")
                ),
                null,
                Shader.TileMode.CLAMP
            )
            val bgPaint = Paint().apply { this.shader = shader }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

            val cardRect = RectF(80f, 400f, (width - 80).toFloat(), (height - 400).toFloat())
            val cardPaint = Paint().apply {
                color = android.graphics.Color.parseColor("#000000")
                alpha = 130
            }
            canvas.drawRoundRect(cardRect, 48f, 48f, cardPaint)

            val tagPaint = TextPaint().apply {
                color = android.graphics.Color.parseColor("#F59E0B")
                textSize = 34f
                isAntiAlias = true
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("💡 TIP • ${tip.category.uppercase()}", (width / 2).toFloat(), 530f, tagPaint)

            val titlePaint = TextPaint().apply {
                color = android.graphics.Color.WHITE
                textSize = 50f
                isAntiAlias = true
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText(tip.title, (width / 2).toFloat(), 640f, titlePaint)

            val textPaint = TextPaint().apply {
                color = android.graphics.Color.parseColor("#E2E8F0")
                textSize = 44f
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }

            val textWidth = width - 260
            val staticLayout = StaticLayout.Builder.obtain(tip.text, 0, tip.text.length, textPaint, textWidth)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(16f, 1.25f)
                .build()

            canvas.save()
            canvas.translate((width / 2).toFloat(), 760f)
            staticLayout.draw(canvas)
            canvas.restore()

            val watermarkPaint = TextPaint().apply {
                color = android.graphics.Color.parseColor("#CBD5E1")
                textSize = 28f
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("AISO • Los Secretos del Orador Exitoso", (width / 2).toFloat(), (height - 180).toFloat(), watermarkPaint)

            val imagesFolder = File(context.cacheDir, "shared_quotes")
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "aiso_tip_${Math.abs(tip.title.hashCode())}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "💡 ${tip.title}: ${tip.text}\n✨ Consejos prácticos en AISO")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            withContext(Dispatchers.Main) {
                onComplete()
                context.startActivity(Intent.createChooser(intent, "Compartir Tip en Historias"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                onComplete()
                Toast.makeText(context, "Error al generar imagen: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

suspend fun shareQuoteAsImage(context: Context, quote: OratoryQuote, onComplete: () -> Unit) {
    withContext(Dispatchers.IO) {
        try {
            val width = 1080
            val height = 1920
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            val shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                intArrayOf(
                    android.graphics.Color.parseColor("#0F172A"),
                    android.graphics.Color.parseColor("#1E1B4B"),
                    android.graphics.Color.parseColor("#312E81")
                ),
                null,
                Shader.TileMode.CLAMP
            )
            val bgPaint = Paint().apply { this.shader = shader }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

            val decoPaint = Paint().apply {
                color = android.graphics.Color.parseColor("#FFFFFF")
                alpha = 15
                style = Paint.Style.STROKE
                strokeWidth = 4f
            }
            canvas.drawCircle(width / 2f, height / 2f, 400f, decoPaint)
            canvas.drawCircle(width / 2f, height / 2f, 650f, decoPaint)

            val cardRect = RectF(80f, 360f, (width - 80).toFloat(), (height - 360).toFloat())
            val cardPaint = Paint().apply {
                color = android.graphics.Color.parseColor("#000000")
                alpha = 120
            }
            canvas.drawRoundRect(cardRect, 48f, 48f, cardPaint)

            val borderPaint = Paint().apply {
                color = android.graphics.Color.parseColor("#D97706")
                alpha = 180
                style = Paint.Style.STROKE
                strokeWidth = 3f
            }
            canvas.drawRoundRect(cardRect, 48f, 48f, borderPaint)

            val tagPaint = TextPaint().apply {
                color = android.graphics.Color.parseColor("#F59E0B")
                textSize = 34f
                isAntiAlias = true
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("ORATORIA & PERSUASIÓN • ${quote.category.uppercase()}", (width / 2).toFloat(), 500f, tagPaint)

            val quoteMarkPaint = TextPaint().apply {
                color = android.graphics.Color.parseColor("#D97706")
                textSize = 140f
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("“", (width / 2).toFloat(), 650f, quoteMarkPaint)

            val textPaint = TextPaint().apply {
                color = android.graphics.Color.WHITE
                textSize = 48f
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }

            val textWidth = width - 260
            val quoteText = "«${quote.text}»"
            val staticLayout = StaticLayout.Builder.obtain(
                quoteText, 0, quoteText.length, textPaint, textWidth
            )
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(16f, 1.25f)
                .build()

            canvas.save()
            val textY = 730f
            canvas.translate((width / 2).toFloat(), textY)
            staticLayout.draw(canvas)
            canvas.restore()

            val authorPaint = TextPaint().apply {
                color = android.graphics.Color.parseColor("#F59E0B")
                textSize = 40f
                isAntiAlias = true
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("— ${quote.author}", (width / 2).toFloat(), (height - 520).toFloat(), authorPaint)

            if (quote.source.isNotBlank()) {
                val sourcePaint = TextPaint().apply {
                    color = android.graphics.Color.parseColor("#CBD5E1")
                    textSize = 30f
                    isAntiAlias = true
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText(quote.source, (width / 2).toFloat(), (height - 460).toFloat(), sourcePaint)
            }

            val watermarkPaint = TextPaint().apply {
                color = android.graphics.Color.parseColor("#CBD5E1")
                textSize = 28f
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("AISO • Los Secretos del Orador Exitoso", (width / 2).toFloat(), (height - 180).toFloat(), watermarkPaint)

            val imagesFolder = File(context.cacheDir, "shared_quotes")
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "aiso_quote_${quote.id}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "«${quote.text}» — ${quote.author}\n✨ Entrena tu oratoria con AISO")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            withContext(Dispatchers.Main) {
                onComplete()
                context.startActivity(Intent.createChooser(intent, "Compartir en Historia de Instagram"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                onComplete()
                Toast.makeText(context, "Error al generar imagen: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

suspend fun shareAffirmationAsImage(context: Context, affirmation: Affirmation, onComplete: () -> Unit) {
    withContext(Dispatchers.IO) {
        try {
            val width = 1080
            val height = 1920
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            val shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                intArrayOf(
                    android.graphics.Color.parseColor("#4C1D95"),
                    android.graphics.Color.parseColor("#7C3AED"),
                    android.graphics.Color.parseColor("#C026D3")
                ),
                null,
                Shader.TileMode.CLAMP
            )
            val bgPaint = Paint().apply { this.shader = shader }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

            val cardRect = RectF(80f, 400f, (width - 80).toFloat(), (height - 400).toFloat())
            val cardPaint = Paint().apply {
                color = android.graphics.Color.parseColor("#000000")
                alpha = 130
            }
            canvas.drawRoundRect(cardRect, 48f, 48f, cardPaint)

            val tagPaint = TextPaint().apply {
                color = android.graphics.Color.parseColor("#FDE047")
                textSize = 36f
                isAntiAlias = true
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("✨ AFIRMACIÓN • ${affirmation.category.uppercase()}", (width / 2).toFloat(), 530f, tagPaint)

            val titlePaint = TextPaint().apply {
                color = android.graphics.Color.parseColor("#FBBF24")
                textSize = 48f
                isAntiAlias = true
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText(affirmation.title, (width / 2).toFloat(), 640f, titlePaint)

            val textPaint = TextPaint().apply {
                color = android.graphics.Color.WHITE
                textSize = 48f
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }

            val textWidth = width - 260
            val text = "«${affirmation.text}»"
            val staticLayout = StaticLayout.Builder.obtain(text, 0, text.length, textPaint, textWidth)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(16f, 1.25f)
                .build()

            canvas.save()
            canvas.translate((width / 2).toFloat(), 740f)
            staticLayout.draw(canvas)
            canvas.restore()

            val watermarkPaint = TextPaint().apply {
                color = android.graphics.Color.parseColor("#E2E8F0")
                textSize = 28f
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("AISO • Mentalidad de Orador Exitoso", (width / 2).toFloat(), (height - 180).toFloat(), watermarkPaint)

            val imagesFolder = File(context.cacheDir, "shared_quotes")
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "aiso_affirmation_${affirmation.id}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "«${affirmation.text}»\n✨ Afirmación de poder en AISO")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            withContext(Dispatchers.Main) {
                onComplete()
                context.startActivity(Intent.createChooser(intent, "Compartir Afirmación en Historias"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                onComplete()
                Toast.makeText(context, "Error al generar imagen: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
