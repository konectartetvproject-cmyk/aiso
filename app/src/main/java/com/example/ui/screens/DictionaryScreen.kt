package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.rememberInAppSpeechController
import com.example.viewmodel.DictionaryEntry
import com.example.viewmodel.DictionaryViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class)
@Composable
fun DictionaryScreen(
    viewModel: DictionaryViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var searchQuery by remember { mutableStateOf("") }

    BackHandler {
        if (state.entry != null) {
            viewModel.clearCurrentEntry()
        } else {
            onBack()
        }
    }

    // Motor de pronunciación TextToSpeech
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsReady = true
            }
        }
        tts = engine
        onDispose {
            engine.stop()
            engine.shutdown()
        }
    }

    fun speakText(text: String, lang: String, slow: Boolean = false) {
        if (!isTtsReady) return
        val locale = when (lang) {
            "Inglés" -> Locale.US
            "Latín" -> Locale("it", "IT")
            else -> Locale("es", "ES")
        }
        tts?.language = locale
        tts?.setSpeechRate(if (slow) 0.68f else 1.0f)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "dict_tts")
    }

    // Reconocimiento de voz integrado en pantalla (sin diálogo de Google)
    val speechController = rememberInAppSpeechController(
        language = when (state.language) {
            "Inglés" -> "en-US"
            else -> "es-ES"
        },
        continuous = false,
        onTranscriptChanged = { partial ->
            if (partial.isNotBlank()) {
                searchQuery = partial
            }
        },
        onFinalized = { finalSpoken ->
            if (finalSpoken.isNotBlank()) {
                searchQuery = finalSpoken
                keyboardController?.hide()
                viewModel.searchWord(finalSpoken)
            }
        }
    )

    val autocompleteSuggestions = remember(searchQuery) {
        viewModel.getAutocompleteSuggestions(searchQuery)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Diccionario Retórico y Léxico AISO",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                        Text(
                            "Palabras de Poder • Oratoria • IA & Glosario",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (state.entry != null) {
                                viewModel.clearCurrentEntry()
                            } else {
                                onBack()
                            }
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (state.entry != null) {
                        TextButton(onClick = { viewModel.clearCurrentEntry() }) {
                            Icon(Icons.Default.GridView, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Glosario", fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Barra de Búsqueda con Voz y Botón Directo
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                placeholder = {
                    Text(
                        if (speechController.isListening) "Escuchando palabra..."
                        else "Escribe o di cualquier palabra o concepto..."
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    searchQuery = ""
                                    viewModel.clearCurrentEntry()
                                }
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Limpiar")
                            }
                        }
                        IconButton(
                            onClick = {
                                if (speechController.isListening) {
                                    speechController.stopAndFinish()
                                } else {
                                    speechController.start()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (speechController.isListening) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = "Buscar con voz",
                                tint = if (speechController.isListening) Color(0xFFDC2626) else Color(0xFF0D9488)
                            )
                        }
                        if (searchQuery.isNotBlank()) {
                            FilledIconButton(
                                onClick = {
                                    keyboardController?.hide()
                                    viewModel.searchWord(searchQuery)
                                },
                                modifier = Modifier.size(36.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    Icons.Default.ArrowForward,
                                    contentDescription = "Consultar",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        keyboardController?.hide()
                        if (searchQuery.isNotBlank()) {
                            viewModel.searchWord(searchQuery)
                        }
                    }
                ),
                shape = RoundedCornerShape(20.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                )
            )

            // Sugerencias de Autocompletado en Vivo mientras escribe
            AnimatedVisibility(
                visible = searchQuery.isNotBlank() &&
                    autocompleteSuggestions.isNotEmpty() &&
                    state.entry?.word?.equals(searchQuery.trim(), ignoreCase = true) != true
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(autocompleteSuggestions) { suggestion ->
                        SuggestionChip(
                            onClick = {
                                searchQuery = suggestion.word
                                keyboardController?.hide()
                                viewModel.searchWord(suggestion.word)
                            },
                            icon = {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp),
                                    tint = Color(0xFFD97706)
                                )
                            },
                            label = {
                                Text(
                                    suggestion.word,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }
                }
            }

            // Selector de Idioma / Contexto Lingüístico
            val languages = listOf("Español", "Inglés", "Latín")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Idioma:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                languages.forEach { lang ->
                    FilterChip(
                        selected = state.language == lang,
                        onClick = {
                            if (state.language != lang) {
                                viewModel.setLanguage(lang)
                                val currentWord = searchQuery.ifBlank { state.entry?.word ?: "" }
                                if (currentWord.isNotBlank()) {
                                    searchQuery = currentWord
                                    viewModel.searchWord(currentWord)
                                }
                            }
                        },
                        label = {
                            Text(
                                text = when (lang) {
                                    "Español" -> "🇪🇸 Español"
                                    "Inglés" -> "🇺🇸 Inglés"
                                    else -> "🏛️ Latín"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            // Contenido Principal
            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Analizando definición, fonética y uso en oratoria...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                state.entry != null -> {
                    val entry = state.entry!!
                    val isFav = state.favorites.any { it.equals(entry.word, ignoreCase = true) }
                    DictionaryEntryDetailView(
                        entry = entry,
                        isFavorite = isFav,
                        onToggleFavorite = { viewModel.toggleFavorite(entry.word) },
                        onSpeakNormal = { speakText(entry.word, entry.language, slow = false) },
                        onSpeakSlow = { speakText(entry.word, entry.language, slow = true) },
                        onSpeakExample = { speakText(entry.exampleSentence, entry.language, slow = false) },
                        onCopyExample = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Ejemplo AISO", entry.exampleSentence))
                            Toast.makeText(context, "Frase copiada al portapapeles", Toast.LENGTH_SHORT).show()
                        },
                        onWordClick = { word ->
                            searchQuery = word
                            viewModel.searchWord(word)
                        }
                    )
                }

                else -> {
                    DictionaryHomeExplorerView(
                        viewModel = viewModel,
                        favorites = state.favorites,
                        searchHistory = state.searchHistory,
                        selectedCategory = state.selectedCategoryFilter,
                        onSelectCategory = { viewModel.setCategoryFilter(it) },
                        onSelectWord = { word ->
                            searchQuery = word
                            keyboardController?.hide()
                            viewModel.searchWord(word)
                        },
                        onClearHistory = { viewModel.clearHistory() },
                        onSpeakWord = { word, lang -> speakText(word, lang, slow = false) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DictionaryEntryDetailView(
    entry: DictionaryEntry,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onSpeakNormal: () -> Unit,
    onSpeakSlow: () -> Unit,
    onSpeakExample: () -> Unit,
    onCopyExample: () -> Unit,
    onWordClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Cabecera Ejecutiva de la Palabra
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF0F766E))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color(0xFFFBBF24),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = entry.category.uppercase(),
                                    color = Color(0xFF0F172A),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            IconButton(onClick = onToggleFavorite) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Guardar en mi léxico",
                                    tint = if (isFavorite) Color(0xFFFBBF24) else Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = entry.word,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )

                        if (entry.phonetic.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Pronunciación y énfasis: ${entry.phonetic}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF5EEAD4)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            FilledTonalButton(
                                onClick = onSpeakNormal,
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color.White.copy(alpha = 0.16f),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Escuchar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            FilledTonalButton(
                                onClick = onSpeakSlow,
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color.White.copy(alpha = 0.16f),
                                    contentColor = Color(0xFFFDE68A)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("🐢 Articulación Lenta", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // 2. Definición Clara y Rigurosa
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DEFINICIÓN Y SIGNIFICADO",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = entry.definition,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // 3. Aplicación en el Escenario (Método AISO)
        if (entry.oratoryUsage.isNotBlank()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDFA)),
                    border = BorderStroke(1.dp, Color(0xFF99F6E4))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = Color(0xFF0F766E),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CÓMO USARLA EN TU DISCURSO (MÉTODO AISO)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0F766E)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = entry.oratoryUsage,
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                            color = Color(0xFF134E4A),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 4. Ejemplo de Alto Impacto en un Discurso Real
        if (entry.exampleSentence.isNotBlank()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.FormatQuote,
                                    contentDescription = null,
                                    tint = Color(0xFFB45309),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "EJEMPLO EN UN DISCURSO REAL",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFB45309)
                                )
                            }
                            Row {
                                IconButton(onClick = onSpeakExample, modifier = Modifier.size(32.dp)) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Escuchar ejemplo",
                                        tint = Color(0xFFB45309),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(onClick = onCopyExample, modifier = Modifier.size(32.dp)) {
                                    Icon(
                                        Icons.Default.ContentCopy,
                                        contentDescription = "Copiar ejemplo",
                                        tint = Color(0xFFB45309),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = entry.exampleSentence,
                            fontSize = 16.sp,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 23.sp,
                            color = Color(0xFF78350F)
                        )
                    }
                }
            }
        }

        // 5. Reemplazo Léxico (Eleva tu Vocabulario)
        if (entry.upgradeTip.isNotBlank()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF2FF)),
                    border = BorderStroke(1.dp, Color(0xFFC7D2FE))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = Color(0xFF4338CA),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "ELEVA TU LÉXICO EJECUTIVO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF4338CA)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = entry.upgradeTip,
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = Color(0xFF312E81)
                            )
                        }
                    }
                }
            }
        }

        // 6. Sinónimos y Antónimos Interactivos
        if (entry.synonyms.isNotEmpty()) {
            item {
                Text(
                    text = "Sinónimos para no repetir palabras (Toca para consultar):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    entry.synonyms.forEach { synonym ->
                        ElevatedAssistChip(
                            onClick = { onWordClick(synonym) },
                            label = { Text(synonym, fontWeight = FontWeight.SemiBold) },
                            colors = AssistChipDefaults.elevatedAssistChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }
                }
            }
        }

        if (entry.antonyms.isNotEmpty()) {
            item {
                Text(
                    text = "Antónimos para crear contraste (Antítesis):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    entry.antonyms.forEach { antonym ->
                        ElevatedAssistChip(
                            onClick = { onWordClick(antonym) },
                            label = { Text(antonym, fontWeight = FontWeight.SemiBold) },
                            colors = AssistChipDefaults.elevatedAssistChipColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                labelColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DictionaryHomeExplorerView(
    viewModel: DictionaryViewModel,
    favorites: List<String>,
    searchHistory: List<String>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    onSelectWord: (String) -> Unit,
    onClearHistory: () -> Unit,
    onSpeakWord: (String, String) -> Unit
) {
    val wordOfDay = viewModel.wordOfTheDay
    val categories = listOf(
        "Todos",
        "Método AISO",
        "Estructura 10-80-10",
        "Figuras Retóricas",
        "Léxico de Poder",
        "Voz y Escenario",
        "Inglés Oratorio",
        "Locución Latina"
    )

    val filteredEntries = remember(selectedCategory) {
        if (selectedCategory == "Todos") {
            viewModel.curatedLexicon
        } else {
            viewModel.curatedLexicon.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Palabra de Poder del Día
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectWord(wordOfDay.word) },
                shape = RoundedCornerShape(22.dp),
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
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color(0xFFFBBF24),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "🌟 PALABRA DE PODER DEL DÍA",
                                    color = Color(0xFF0F172A),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            IconButton(
                                onClick = { onSpeakWord(wordOfDay.word, wordOfDay.language) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Escuchar palabra del día",
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = wordOfDay.word,
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = wordOfDay.phonetic,
                                color = Color(0xFF5EEAD4),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = wordOfDay.definition,
                            color = Color.White.copy(alpha = 0.88f),
                            fontSize = 13.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Toca para ver ejemplo en discurso y aplicación AISO →",
                            color = Color(0xFFFDE68A),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2. Mi Léxico Guardado (Favoritos)
        if (favorites.isNotEmpty()) {
            item {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Mi Léxico Guardado (${favorites.size})",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(favorites) { fav ->
                            ElevatedAssistChip(
                                onClick = { onSelectWord(fav) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFD97706),
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                label = { Text(fav, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }
                }
            }
        }

        // 3. Historial de Consultas Recientes
        if (searchHistory.isNotEmpty()) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Consultas Recientes",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        TextButton(onClick = onClearHistory) {
                            Text("Limpiar", fontSize = 12.sp)
                        }
                    }
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(searchHistory) { item ->
                            SuggestionChip(
                                onClick = { onSelectWord(item) },
                                label = { Text(item, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }
        }

        // 4. Filtros por Categoría del Glosario AISO
        item {
            Column {
                Text(
                    text = "Explorar Glosario del Orador Exitoso",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { onSelectCategory(cat) },
                            label = {
                                Text(
                                    text = cat,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }
                }
            }
        }

        // 5. Tarjetas del Léxico Curado
        items(filteredEntries) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectWord(item.word) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = item.word,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.phonetic,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = item.category,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = item.definition,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
