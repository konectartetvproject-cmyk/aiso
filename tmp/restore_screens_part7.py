import os

screens = {}

screens["app/src/main/java/com/example/ui/screens/SpellCheckerScreen.kt"] = '''package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.SpellCheckerViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SpellCheckerScreen(
    viewModel: SpellCheckerViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Corrector", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Revisa tu texto",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Pega o escribe tu texto aquí para corregir ortografía y gramática.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { 
                            inputText = it
                            if (it.isBlank() && state.correctedText != null) {
                                viewModel.clear()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        placeholder = { Text("Escribe o pega el texto...") },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        trailingIcon = {
                            if (inputText.isNotEmpty()) {
                                IconButton(onClick = { 
                                    inputText = ""
                                    viewModel.clear()
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "Limpiar")
                                }
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            keyboardController?.hide()
                            viewModel.analyzeText(inputText)
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = inputText.isNotBlank() && !state.isLoading,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Revisando...")
                        } else {
                            Icon(Icons.Default.Spellcheck, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Corregir Texto")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }

                if (state.error != null) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = state.error!!,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                } else if (state.correctedText != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Texto Corregido",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Texto Corregido", state.correctedText)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Copiado al portapapeles", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copiar", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Text(
                                    text = state.correctedText!!,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 24.sp
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                    
                    if (!state.explanation.isNullOrBlank()) {
                        item {
                            Text(
                                text = "Explicación de cambios",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = state.explanation!!,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(16.dp),
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/SpeechAnalysisScreen.kt"] = '''package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.viewmodel.SpeechAnalysisViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeechAnalysisScreen(
    viewModel: SpeechAnalysisViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var hasRecordPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasRecordPermission = isGranted
        if (isGranted) {
            viewModel.startRecording(context)
        }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "recordingIndicator")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Análisis de Discurso", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Graba y Analiza",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Graba un fragmento de tu discurso. AISO analizará tu ritmo, tono y claridad para darte feedback inmediato.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(if (state.isRecording) MaterialTheme.colorScheme.errorContainer.copy(alpha = alpha) else MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        if (state.isRecording) {
                            viewModel.stopRecordingAndAnalyze()
                        } else {
                            if (hasRecordPermission) {
                                viewModel.startRecording(context)
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    },
                    modifier = Modifier.size(100.dp),
                    enabled = !state.isAnalyzing
                ) {
                    Icon(
                        imageVector = if (state.isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (state.isRecording) "Detener grabación" else "Comenzar grabación",
                        modifier = Modifier.size(64.dp),
                        tint = if (state.isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (state.isRecording) {
                Text(
                    text = "Grabando... (Toca para detener y analizar)",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            } else if (state.isAnalyzing) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "AISO está analizando tu discurso...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "Presiona el micrófono para comenzar",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                if (state.error != null) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = state.error!!,
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
                
                if (state.feedback != null) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Feedback del Coach",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = state.feedback!!,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/SpeechDraftListScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.SpeechDraft
import com.example.viewmodel.SpeechDraftViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeechDraftListScreen(
    viewModel: SpeechDraftViewModel,
    onBack: () -> Unit,
    onNavigateToEdit: (Int?) -> Unit
) {
    val drafts by viewModel.allDrafts.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Discursos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToEdit(null) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Borrador")
            }
        }
    ) { paddingValues ->
        if (drafts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text(
                    text = "Aún no tienes borradores de discursos.\\nPresiona + para crear uno.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(32.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(drafts) { draft ->
                    SpeechDraftItem(
                        draft = draft,
                        onClick = { onNavigateToEdit(draft.id) },
                        onDelete = { viewModel.deleteDraft(draft) }
                    )
                }
            }
        }
    }
}

@Composable
fun SpeechDraftItem(
    draft: SpeechDraft,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val sdf = SimpleDateFormat("dd MMM, yyyy - HH:mm", Locale.getDefault())
    val dateString = sdf.format(Date(draft.lastModified))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = draft.title.ifEmpty { "Sin Título" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = draft.content.ifEmpty { "..." },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/SpeechDraftEditorScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.viewmodel.SpeechDraftViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeechDraftEditorScreen(
    viewModel: SpeechDraftViewModel,
    draftId: Int?,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(draftId) {
        if (draftId != null && draftId != 0) {
            val draft = viewModel.allDrafts.firstOrNull()?.find { it.id == draftId }
            if (draft != null) {
                title = draft.title
                content = draft.content
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (draftId == null || draftId == 0) "Nuevo Borrador" else "Editar Borrador", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.saveDraft(draftId, title, content)
                            onBack()
                        },
                        enabled = title.isNotBlank() || content.isNotBlank()
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Guardar", tint = MaterialTheme.colorScheme.primary)
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
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título de tu Discurso") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Contenido / Notas / Guion") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/GeneralCultureScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.CultureScreenState
import com.example.viewmodel.GeneralCultureViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralCultureScreen(
    viewModel: GeneralCultureViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cultura General", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (state.screenState != CultureScreenState.FACTS) {
                            viewModel.restart()
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state.screenState) {
                CultureScreenState.FACTS -> {
                    FactsSection(viewModel)
                }
                CultureScreenState.QUIZ -> {
                    QuizSection(viewModel)
                }
                CultureScreenState.RESULTS -> {
                    ResultsSection(viewModel)
                }
            }
        }
    }
}

@Composable
fun FactsSection(viewModel: GeneralCultureViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Datos Curiosos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "Lee atentamente estos datos de cultura general que te servirán como grandes anécdotas para tus discursos.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(viewModel.facts) { fact ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = fact.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = fact.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        Button(
            onClick = { viewModel.startQuiz() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("¡Ir a la Prueba!", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun QuizSection(viewModel: GeneralCultureViewModel) {
    val state by viewModel.state.collectAsState()
    val question = viewModel.questions[state.currentQuestionIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        LinearProgressIndicator(
            progress = { (state.currentQuestionIndex + 1).toFloat() / viewModel.questions.size },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primaryContainer,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Pregunta ${state.currentQuestionIndex + 1} de ${viewModel.questions.size}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = question.text,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            lineHeight = 28.sp
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            question.options.forEachIndexed { index, option ->
                val isSelected = state.selectedOptionIndex == index
                val isCorrect = index == question.correctOptionIndex
                
                val backgroundColor = when {
                    !state.isAnswerChecked && isSelected -> MaterialTheme.colorScheme.primaryContainer
                    !state.isAnswerChecked -> MaterialTheme.colorScheme.surfaceVariant
                    state.isAnswerChecked && isCorrect -> MaterialTheme.colorScheme.tertiaryContainer
                    state.isAnswerChecked && isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
                
                val contentColor = when {
                    !state.isAnswerChecked && isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                    !state.isAnswerChecked -> MaterialTheme.colorScheme.onSurfaceVariant
                    state.isAnswerChecked && isCorrect -> MaterialTheme.colorScheme.onTertiaryContainer
                    state.isAnswerChecked && isSelected && !isCorrect -> MaterialTheme.colorScheme.onErrorContainer
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !state.isAnswerChecked) { viewModel.selectOption(index) },
                    colors = CardDefaults.cardColors(containerColor = backgroundColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge,
                            color = contentColor,
                            modifier = Modifier.weight(1f)
                        )
                        
                        if (state.isAnswerChecked) {
                            if (isCorrect) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Correcto", tint = contentColor)
                            } else if (isSelected) {
                                Icon(Icons.Default.Cancel, contentDescription = "Incorrecto", tint = contentColor)
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        if (state.selectedOptionIndex != null) {
            Button(
                onClick = {
                    if (state.isAnswerChecked) viewModel.nextQuestion() else viewModel.checkAnswer()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(if (state.isAnswerChecked) "Siguiente" else "Comprobar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ResultsSection(viewModel: GeneralCultureViewModel) {
    val state by viewModel.state.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.School,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "¡Prueba Completada!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Tu puntuación:",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "${state.score} / ${viewModel.questions.size}",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = { viewModel.restart() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Volver a leer los datos", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/PublicSpeakingGuideScreen.kt"] = '''package com.example.ui.screens

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
'''

screens["app/src/main/java/com/example/ui/screens/finance/FinanceScreen.kt"] = '''package com.example.ui.screens.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.FinanceTransaction
import com.example.viewmodel.FinanceViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    viewModel: FinanceViewModel,
    onBack: () -> Unit
) {
    val transactions by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    val totalBalance = transactions.sumOf { if (it.isIncome) it.amount else -it.amount }
    val totalIncome = transactions.filter { it.isIncome }.sumOf { it.amount }
    val totalExpense = transactions.filter { !it.isIncome }.sumOf { it.amount }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finanzas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            BalanceCard(totalBalance, totalIncome, totalExpense)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Historial de Transacciones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            if (transactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay transacciones aún.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(transactions) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onDelete = { viewModel.deleteTransaction(transaction) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddTransactionDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { description, amount, isIncome ->
                viewModel.addTransaction(description, amount, isIncome)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun BalanceCard(balance: Double, income: Double, expense: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Balance Total", style = MaterialTheme.typography.titleMedium)
            Text(
                text = formatCurrency(balance),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = if (balance >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Ingresos", style = MaterialTheme.typography.bodyMedium)
                    Text(formatCurrency(income), color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Gastos", style = MaterialTheme.typography.bodyMedium)
                    Text(formatCurrency(expense), color = Color(0xFFF44336), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: FinanceTransaction, onDelete: () -> Unit) {
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val dateString = formatter.format(Date(transaction.timestamp))
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = transaction.description, fontWeight = FontWeight.Bold)
                Text(text = dateString, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Text(
                text = (if (transaction.isIncome) "+" else "-") + formatCurrency(transaction.amount),
                fontWeight = FontWeight.Bold,
                color = if (transaction.isIncome) Color(0xFF4CAF50) else Color(0xFFF44336),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Gray)
            }
        }
    }
}

@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, Boolean) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Transacción") },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    FilterChip(
                        selected = !isIncome,
                        onClick = { isIncome = false },
                        label = { Text("Gasto") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = isIncome,
                        onClick = { isIncome = true },
                        label = { Text("Ingreso") }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Monto") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.replace(",", ".").toDoubleOrNull()
                    if (description.isNotBlank() && amount != null && amount > 0) {
                        onConfirm(description, amount, isIncome)
                    }
                },
                enabled = description.isNotBlank() && amountText.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
    return format.format(amount)
}
'''

screens["app/src/main/java/com/example/ui/screens/MainScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.viewmodel.OratoriaViewModel
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.platform.LocalContext

data class DailyTip(
    val title: String,
    val description: String,
    val icon: ImageVector
)

val dailyTips = listOf(
    DailyTip("Contacto Visual", "Mantén contacto visual con tu audiencia en forma de 'Z' o 'W' para incluir a todos.", Icons.Default.Visibility),
    DailyTip("Pausas Estratégicas", "Utiliza pausas antes de un punto clave para generar expectativa y después para que se asimile.", Icons.Default.HourglassEmpty),
    DailyTip("Sonrisa Genuina", "Una sonrisa inicial relaja a tu audiencia y proyecta confianza y carisma.", Icons.Default.SentimentSatisfiedAlt),
    DailyTip("Volumen Dinámico", "Sube el volumen para enfatizar ideas importantes y bájalo para crear intimidad.", Icons.Default.VolumeUp),
    DailyTip("Postura Abierta", "Evita cruzar los brazos; mantén tus manos visibles y abiertas para proyectar honestidad.", Icons.Default.AccessibilityNew),
    DailyTip("Velocidad Controlada", "Habla más rápido para inyectar energía y entusiasmo, y más lento para enfatizar puntos graves.", Icons.Default.Speed)
)

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
    val context = LocalContext.current
    var selectedTab: Int? by remember { mutableStateOf(null) }
    var showDailyTip by remember { mutableStateOf(true) }
    val randomTip = remember { dailyTips.random() }

    BackHandler(enabled = selectedTab != null) {
        selectedTab = null
    }

    if (showDailyTip) {
        AlertDialog(
            onDismissRequest = { showDailyTip = false },
            icon = { Icon(Icons.Default.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp)) },
            title = { Text("Consejo del Día", fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(randomTip.icon, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.secondary)
                    Spacer(Modifier.height(16.dp))
                    Text(randomTip.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    Text(randomTip.description, textAlign = TextAlign.Center)
                }
            },
            confirmButton = {
                TextButton(onClick = { showDailyTip = false }) {
                    Text("¡Entendido!")
                }
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    Scaffold(
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (selectedTab == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(32.dp))
                    Text(
                        "¿Qué quieres hacer hoy?",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(24.dp))
                    
                    data class DashboardOption(val title: String, val icon: ImageVector, val onClick: () -> Unit)
                    data class DashboardCategory(val title: String, val options: List<DashboardOption>)
                    val categories = listOf(
                        DashboardCategory(
                            "Aprender algo nuevo", 
                            listOf(
                                DashboardOption("Taller", Icons.Default.AutoFixHigh) { selectedTab = 0 },
                                DashboardOption("Tips", Icons.Default.Info) { selectedTab = 5 },
                                DashboardOption("Guía Oratoria", Icons.Default.MenuBook) { onNavigateToRoute("public_speaking_guide") },
                                DashboardOption("Cultura Gral.", Icons.Default.LocalLibrary) { onNavigateToRoute("general_culture") },
                                DashboardOption("Inglés", Icons.Default.School) { selectedTab = 4 }
                            )
                        ),
                        DashboardCategory(
                            "Practicar en vivo",
                            listOf(
                                DashboardOption("AISO IA", Icons.Default.AutoAwesome) { onNavigateToRoute("aiso_chat") },
                                DashboardOption("En Vivo", Icons.Default.Forum) { selectedTab = 1 },
                                DashboardOption("Análisis de Discurso", Icons.Default.GraphicEq) { onNavigateToRoute("speech_analysis") },
                                DashboardOption("Históricos", Icons.Default.Public) { onNavigateToRoute("historical_speeches_list") },
                                DashboardOption("Poemas", Icons.Default.MenuBook) { onNavigateToRoute("poems_list") },
                                DashboardOption("Duelo Traba.", Icons.Default.Mic) { onNavigateToTongueTwisterDuel() },
                                DashboardOption("Prác. Dicción", Icons.Default.VolumeUp) { onNavigateToRoute("diction_practice") },
                                DashboardOption("Leng. Corporal", Icons.Default.AccessibilityNew) { onNavigateToBodyLanguage() },
                                DashboardOption("Trabalenguas", Icons.Default.Mic) { onNavigateToTongueTwisters() },
                                DashboardOption("Respiración", Icons.Default.Air) { onNavigateToRoute("breathing_exercise") }
                            )
                        ),
                        DashboardCategory(
                            "Escribir discurso",
                            listOf(
                                DashboardOption("Mis Discursos", Icons.Default.NoteAlt) { onNavigateToRoute("speech_drafts_list") },
                                DashboardOption("Notas IA", Icons.Default.EditNote) { onNavigateToRoute("ai_notes") },
                                DashboardOption("Corrector", Icons.Default.FactCheck) { onNavigateToRoute("spell_checker") }
                            )
                        ),
                        DashboardCategory(
                            "Inspiración",
                            listOf(
                                DashboardOption("Frases", Icons.Default.FormatQuote) { selectedTab = 3 },
                                DashboardOption("Tips", Icons.Default.Info) { selectedTab = 5 },
                                DashboardOption("Afirmaciones", Icons.Default.Favorite) { onNavigateToRoute("affirmations_list") }
                            )
                        ),
                        DashboardCategory(
                            "Herramientas",
                            listOf(
                                DashboardOption("Cronómetro", Icons.Default.Timer) { selectedTab = 6 },
                                DashboardOption("Diccionario", Icons.Default.Spellcheck) { onNavigateToRoute("dictionary") },
                                DashboardOption("Calculadora", Icons.Default.Calculate) { onNavigateToRoute("calculator") }
                            )
                        ),
                        DashboardCategory(
                            "Educación financiera",
                            listOf(
                                DashboardOption("Finanzas", Icons.Default.AttachMoney) { onNavigateToRoute("finances") }
                            )
                        ),
                        DashboardCategory(
                            "Juegos",
                            listOf(
                                DashboardOption("Juegos", Icons.Default.VideogameAsset) { selectedTab = 2 }
                            )
                        )
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        items(categories) { category ->
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    category.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                                )
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(category.options) { option ->
                                        Card(
                                            onClick = option.onClick,
                                            modifier = Modifier
                                                .width(140.dp)
                                                .height(120.dp),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.fillMaxSize().padding(8.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Icon(option.icon, contentDescription = option.title, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                                                Spacer(Modifier.height(12.dp))
                                                Text(option.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            Spacer(Modifier.height(32.dp))
                        }
                    }
                }
            } else {
                when (selectedTab) {
                    0 -> {
                        TechniquesListScreen(
                            viewModel = viewModel,
                            onTechniqueClick = { technique ->
                                viewModel.startTechniquePractice(technique)
                                onNavigateToTechniques()
                            },
                            onToxicParticipantsClick = onNavigateToToxic,
                            onAnecdotesClick = onNavigateToAnecdotes,
                            onQuotesClick = onNavigateToQuotes,
                            onNavigateToQuiz = onNavigateToQuiz,
                            onNavigateToRoute = onNavigateToRoute
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
                            onCustomerServiceQuizClick = { onNavigateToRoute("customer_service_quiz") }
                        )
                    }
                    3 -> {
                        QuotesScreen(onBack = { selectedTab = null }, showBackButton = false)
                    }
                    4 -> {
                        com.example.ui.screens.english.EnglishLearningMainScreen(
                            onNavigateToRoute = onNavigateToRoute
                        )
                    }
                    5 -> {
                        RecommendationsScreen(onBack = { selectedTab = null }, showBackButton = false)
                    }
                    6 -> {
                        TimersListScreen(
                            onNavigateToSpeechTimer = { onNavigateToRoute("speech_timer") },
                            onNavigateToNormalTimer = { onNavigateToRoute("normal_timer") },
                            onNavigateToDebateTimer = { onNavigateToRoute("debate_timer") }
                        )
                    }
                }
            }
        }
    }
}
'''

for path, content in screens.items():
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        f.write(content)

print("Part 7 written successfully")
