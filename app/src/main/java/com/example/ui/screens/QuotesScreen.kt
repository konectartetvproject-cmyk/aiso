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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Share
import java.io.File
import java.io.FileOutputStream
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.border

data class Quote(
    val text: String,
    val author: String,
    val category: String,
    val colors: List<Color>
)

val quotesList = listOf(
    // Oratoria
    Quote("El que sabe pensar pero no sabe expresar lo que piensa, está en el mismo nivel del que no sabe pensar.", "Pericles", "Oratoria", listOf(Color(0xFF1E3C72), Color(0xFF2A5298))),
    Quote("La oratoria es el arte de persuadir.", "Cicerón", "Oratoria", listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))),
    Quote("No hay nada tan increíble que la oratoria no pueda volverlo aceptable.", "Cicerón", "Oratoria", listOf(Color(0xFFFF416C), Color(0xFFFF4B2B))),
    Quote("Un discurso es como una historia de amor. Cualquier tonto puede empezarla, pero para terminarla se requiere de mucha habilidad.", "Lord Mancroft", "Oratoria", listOf(Color(0xFF00B4DB), Color(0xFF0083B0))),
    Quote("El secreto de un buen discurso es tener un buen comienzo y un buen final, y luego tratar de que ambos estén lo más cerca posible.", "George Burns", "Oratoria", listOf(Color(0xFFFDC830), Color(0xFFF37335))),
    Quote("Habla para que yo te conozca.", "Sócrates", "Oratoria", listOf(Color(0xFF00C9FF), Color(0xFF92FE9D))),
    Quote("El éxito de tu presentación no se juzgará por el conocimiento que envíes, sino por el que el oyente reciba.", "Lilly Walters", "Oratoria", listOf(Color(0xFFf12711), Color(0xFFf5af19))),
    
    // Motivacional
    Quote("No dejes que el miedo a equivocarte te impida expresar tu genialidad.", "Desconocido", "Motivacional", listOf(Color(0xFF11998E), Color(0xFF38EF7D))),
    Quote("El éxito no es el final, el fracaso no es fatal: es el coraje para continuar lo que cuenta.", "Winston Churchill", "Motivacional", listOf(Color(0xFFff9966), Color(0xFFff5e62))),
    Quote("Cree que puedes y estarás a mitad de camino.", "Theodore Roosevelt", "Motivacional", listOf(Color(0xFF4CB8C4), Color(0xFF3CD3AD))),
    Quote("No mires el reloj; haz lo que él hace. Sigue moviéndote.", "Sam Levenson", "Motivacional", listOf(Color(0xFF834D9B), Color(0xFFD04ED6))),
    
    // Ley de Atracción
    Quote("Lo que piensas, lo serás. Lo que sientas, lo atraerás. Lo que imagines, lo crearás.", "Buda", "Ley de Atracción", listOf(Color(0xFF834D9B), Color(0xFFD04ED6))),
    Quote("Todo lo que somos es el resultado de lo que hemos pensado.", "Buda", "Ley de Atracción", listOf(Color(0xFF00C9FF), Color(0xFF92FE9D))),
    Quote("Tus pensamientos son las semillas de lo que plantarás en tu vida.", "Desconocido", "Ley de Atracción", listOf(Color(0xFF1E3C72), Color(0xFF2A5298))),
    Quote("Pide, cree, recibe.", "El Secreto", "Ley de Atracción", listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))),
    
    // Fe
    Quote("La fe es dar el primer paso incluso cuando no ves toda la escalera.", "Martin Luther King Jr.", "Fe", listOf(Color(0xFFFDC830), Color(0xFFF37335))),
    Quote("Creer en ti mismo es el primer secreto del éxito.", "Ralph Waldo Emerson", "Fe", listOf(Color(0xFF56AB2F), Color(0xFFA8E063))),
    Quote("Donde hay fe, hay amor; donde hay amor, hay paz.", "Proverbio", "Fe", listOf(Color(0xFFDA22FF), Color(0xFF9733EE))),
    Quote("No pierdas la esperanza. Cuando se pone el sol, salen las estrellas.", "Desconocido", "Fe", listOf(Color(0xFF00C9FF), Color(0xFF92FE9D))),
    
    // Negocios
    Quote("El secreto del éxito en los negocios está en detectar hacia dónde va el mundo y llegar ahí primero.", "Bill Gates", "Negocios", listOf(Color(0xFF141E30), Color(0xFF243B55))),
    Quote("Tu cliente más insatisfecho es tu mejor fuente de aprendizaje.", "Bill Gates", "Negocios", listOf(Color(0xFF00B4DB), Color(0xFF0083B0))),
    Quote("Las oportunidades de negocio son como los autobuses; siempre hay otro en camino.", "Richard Branson", "Negocios", listOf(Color(0xFF1E3C72), Color(0xFF2A5298))),
    Quote("El riesgo más grande es no tomar ninguno.", "Mark Zuckerberg", "Negocios", listOf(Color(0xFFFF416C), Color(0xFFFF4B2B))),
    
    // Liderazgo
    Quote("Un líder es alguien que conoce el camino, recorre el camino y muestra el camino.", "John C. Maxwell", "Liderazgo", listOf(Color(0xFFDA22FF), Color(0xFF9733EE))),
    Quote("La innovación distingue a un líder de un seguidor.", "Steve Jobs", "Liderazgo", listOf(Color(0xFFf12711), Color(0xFFf5af19))),
    Quote("Si tus acciones inspiran a otros a soñar más, aprender más, hacer más y ser más, eres un líder.", "John Quincy Adams", "Liderazgo", listOf(Color(0xFF11998E), Color(0xFF38EF7D))),
    Quote("El liderazgo es la capacidad de traducir la visión en realidad.", "Warren Bennis", "Liderazgo", listOf(Color(0xFF834D9B), Color(0xFFD04ED6))),

    // Biblia
    Quote("La blanda respuesta quita la ira; mas la palabra áspera hace subir el furor.", "Proverbios 15:1", "Biblia", listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))),
    Quote("Manzana de oro con figuras de plata es la palabra dicha como conviene.", "Proverbios 25:11", "Biblia", listOf(Color(0xFFFDC830), Color(0xFFF37335))),
    Quote("Ninguna palabra corrompida salga de vuestra boca, sino la que sea buena para la necesaria edificación.", "Efesios 4:29", "Biblia", listOf(Color(0xFF11998E), Color(0xFF38EF7D))),
    Quote("Airaos, pero no pequéis; no se ponga el sol sobre vuestro enojo.", "Efesios 4:26", "Biblia", listOf(Color(0xFF00C9FF), Color(0xFF92FE9D))),
    Quote("El que guarda su boca y su lengua, su alma guarda de angustias.", "Proverbios 21:23", "Biblia", listOf(Color(0xFFff9966), Color(0xFFff5e62))),
    Quote("Porque no nos ha dado Dios espíritu de cobardía, sino de poder, de amor y de dominio propio.", "2 Timoteo 1:7", "Biblia", listOf(Color(0xFF4CB8C4), Color(0xFF3CD3AD)))
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuotesScreen(onBack: () -> Unit, showBackButton: Boolean = true) {
    val categories = listOf("Motivacional", "Oratoria", "Ley de Atracción", "Fe", "Negocios", "Liderazgo", "Biblia")
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    
    val filteredQuotes = remember(selectedCategory) {
        quotesList.filter { it.category == selectedCategory }
    }

    val pagerState = rememberPagerState(pageCount = { filteredQuotes.size })

    LaunchedEffect(selectedCategory) {
        pagerState.scrollToPage(0)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val quote = filteredQuotes[page]
            QuoteCard(quote = quote)
        }
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            if (showBackButton) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                    Text("Frases", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
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
fun QuoteCard(quote: Quote) {
    val randomSeed = Math.abs(quote.text.hashCode())
    val random = java.util.Random(randomSeed.toLong())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(quote.colors))
    ) {
        // Decorative background elements
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            
            when (quote.category) {
                "Oratoria" -> {
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(0f, canvasHeight * 0.4f)
                        cubicTo(
                            canvasWidth * 0.25f, canvasHeight * 0.3f, 
                            canvasWidth * 0.75f, canvasHeight * 0.5f, 
                            canvasWidth, canvasHeight * 0.4f
                        )
                        lineTo(canvasWidth, 0f)
                        lineTo(0f, 0f)
                        close()
                    }
                    drawPath(path, color = Color.White.copy(alpha = 0.15f))
                    
                    val path2 = androidx.compose.ui.graphics.Path().apply {
                        moveTo(0f, canvasHeight)
                        cubicTo(
                            canvasWidth * 0.3f, canvasHeight * 0.8f, 
                            canvasWidth * 0.6f, canvasHeight * 0.9f, 
                            canvasWidth, canvasHeight * 0.75f
                        )
                        lineTo(canvasWidth, canvasHeight)
                        close()
                    }
                    drawPath(path2, color = Color.White.copy(alpha = 0.1f))
                }
                "Motivacional" -> {
                    for (i in 0..11) {
                        rotate(i * 30f) {
                            drawRoundRect(
                                color = Color.White.copy(alpha = 0.05f),
                                topLeft = Offset(canvasWidth / 2 - 20f, -canvasHeight * 0.2f),
                                size = androidx.compose.ui.geometry.Size(40f, canvasHeight * 1.4f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f)
                            )
                        }
                    }
                }
                "Ley de Atracción" -> {
                    for (i in 1..4) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.05f),
                            radius = i * 200f,
                            center = Offset(canvasWidth / 2, canvasHeight / 2),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                        )
                    }
                    for (i in 0..15) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.6f),
                            radius = random.nextFloat() * 12f + 4f,
                            center = Offset(random.nextFloat() * canvasWidth, random.nextFloat() * canvasHeight)
                        )
                    }
                }
                "Fe" -> {
                    val centerTop = Offset(canvasWidth / 2, -100f)
                    for (i in 0..8) {
                        val angle = (i * 15 - 60) * (Math.PI / 180f)
                        val endX = centerTop.x + Math.sin(angle).toFloat() * canvasHeight * 1.5f
                        val endY = centerTop.y + Math.cos(angle).toFloat() * canvasHeight * 1.5f
                        drawLine(
                            color = Color.White.copy(alpha = 0.15f),
                            start = centerTop,
                            end = Offset(endX.toFloat(), endY.toFloat()),
                            strokeWidth = random.nextFloat() * 60f + 20f
                        )
                    }
                }
                "Negocios" -> {
                    val step = 150f
                    var x = 0f
                    while (x < canvasWidth) {
                        drawLine(
                            color = Color.White.copy(alpha = 0.1f),
                            start = Offset(x, 0f),
                            end = Offset(x, canvasHeight),
                            strokeWidth = 2f
                        )
                        x += step
                    }
                    var y = 0f
                    while (y < canvasHeight) {
                        drawLine(
                            color = Color.White.copy(alpha = 0.1f),
                            start = Offset(0f, y),
                            end = Offset(canvasWidth, y),
                            strokeWidth = 2f
                        )
                        y += step
                    }
                    val polyPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(canvasWidth * 0.8f, canvasHeight * 0.2f)
                        lineTo(canvasWidth, canvasHeight * 0.5f)
                        lineTo(canvasWidth * 0.6f, canvasHeight * 0.7f)
                        close()
                    }
                    drawPath(polyPath, color = Color.White.copy(alpha = 0.15f))
                }
                "Liderazgo" -> {
                    for (i in -2..5) {
                        val startY = i * 400f
                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(0f, startY)
                            lineTo(canvasWidth, startY - canvasWidth)
                            lineTo(canvasWidth, startY - canvasWidth + 150f)
                            lineTo(0f, startY + 150f)
                            close()
                        }
                        drawPath(path, color = Color.White.copy(alpha = 0.08f))
                    }
                }
                "Biblia" -> {
                    val centerOffset = Offset(canvasWidth / 2, canvasHeight / 2)
                    for (i in 0..3) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.05f),
                            radius = (i * 200f) + 150f,
                            center = centerOffset,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                        )
                    }
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(canvasWidth * 0.3f, canvasHeight * 0.8f)
                        quadraticBezierTo(canvasWidth * 0.5f, canvasHeight * 0.85f, canvasWidth * 0.7f, canvasHeight * 0.8f)
                        lineTo(canvasWidth * 0.7f, canvasHeight)
                        lineTo(canvasWidth * 0.3f, canvasHeight)
                        close()
                    }
                    drawPath(path, color = Color.White.copy(alpha = 0.15f))
                    for(i in 0..10) {
                        val x = random.nextFloat() * canvasWidth
                        val y = random.nextFloat() * canvasHeight
                        drawCircle(
                            color = Color.White.copy(alpha = random.nextFloat() * 0.3f + 0.1f),
                            radius = random.nextFloat() * 8f + 2f,
                            center = Offset(x, y)
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(32.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.2f),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(32.dp)
                )
                .padding(horizontal = 28.dp, vertical = 48.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.FormatQuote,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(56.dp).offset(y = (-16).dp)
            )
            Text(
                text = quote.text,
                fontSize = 26.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp,
                modifier = Modifier.graphicsLayer {
                    shadowElevation = 4.dp.toPx()
                }
            )
            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.width(60.dp).padding(bottom = 16.dp)
            )
            Text(
                text = quote.author.uppercase(),
                fontSize = 16.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = quote.category,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
        
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val context = LocalContext.current
            var isSharing by remember { mutableStateOf(false) }
            
            if (isSharing) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(48.dp).padding(8.dp)
                )
            } else {
                IconButton(
                    onClick = { 
                        isSharing = true
                        shareQuoteAsImage(context, quote) {
                            isSharing = false
                        }
                    },
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), shape = androidx.compose.foundation.shape.CircleShape)
                        .padding(8.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Compartir", tint = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Desliza para más",
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

fun shareQuoteAsImage(context: Context, quote: Quote, onComplete: () -> Unit = {}) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val width = 1080
            val height = 1080
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = AndroidCanvas(bitmap)

            val colorStart = quote.colors[0].toArgb()
            val colorEnd = quote.colors.last().toArgb()

            val gradient = LinearGradient(
                0f, 0f, 0f, height.toFloat(),
                colorStart, colorEnd,
                Shader.TileMode.CLAMP
            )
            val bgPaint = Paint().apply { shader = gradient }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
            
            val random = java.util.Random(quote.text.hashCode().toLong())
            
            // Draw creative background based on category
            val w = width.toFloat()
            val h = height.toFloat()
            
            when (quote.category) {
                "Oratoria" -> {
                    val pathPaint = Paint().apply { color = android.graphics.Color.WHITE; alpha = 38; isAntiAlias = true }
                    val path = android.graphics.Path().apply {
                        moveTo(0f, h * 0.4f)
                        cubicTo(w * 0.25f, h * 0.3f, w * 0.75f, h * 0.5f, w, h * 0.4f)
                        lineTo(w, 0f)
                        lineTo(0f, 0f)
                        close()
                    }
                    canvas.drawPath(path, pathPaint)
                }
                "Motivacional" -> {
                    val rayPaint = Paint().apply { color = android.graphics.Color.WHITE; alpha = 15; isAntiAlias = true }
                    canvas.save()
                    canvas.translate(w / 2f, h / 2f)
                    for (i in 0..11) {
                        canvas.rotate(30f)
                        canvas.drawRoundRect(-30f, -h * 0.7f, 30f, h * 0.7f, 30f, 30f, rayPaint)
                    }
                    canvas.restore()
                }
                "Ley de Atracción" -> {
                    val circlePaint = Paint().apply { color = android.graphics.Color.WHITE; alpha = 15; style = Paint.Style.STROKE; strokeWidth = 10f; isAntiAlias = true }
                    for (i in 1..5) {
                        canvas.drawCircle(w / 2f, h / 2f, i * 250f, circlePaint)
                    }
                    val sparkPaint = Paint().apply { color = android.graphics.Color.WHITE; alpha = 150; isAntiAlias = true }
                    for (i in 0..25) {
                        canvas.drawCircle(random.nextFloat() * w, random.nextFloat() * h, random.nextFloat() * 15f + 5f, sparkPaint)
                    }
                }
                "Fe" -> {
                    val rayPaint = Paint().apply { color = android.graphics.Color.WHITE; alpha = 38; isAntiAlias = true }
                    for (i in 0..10) {
                        val angle = (i * 12 - 60) * (Math.PI / 180f)
                        val startX = w / 2f
                        val startY = -200f
                        val endX = startX + Math.sin(angle).toFloat() * h * 1.5f
                        val endY = startY + Math.cos(angle).toFloat() * h * 1.5f
                        rayPaint.strokeWidth = random.nextFloat() * 80f + 30f
                        canvas.drawLine(startX, startY, endX, endY, rayPaint)
                    }
                }
                "Negocios" -> {
                    val gridPaint = Paint().apply { color = android.graphics.Color.WHITE; alpha = 25; strokeWidth = 4f; isAntiAlias = true }
                    var x = 0f
                    while (x < w) { canvas.drawLine(x, 0f, x, h, gridPaint); x += 200f }
                    var y = 0f
                    while (y < h) { canvas.drawLine(0f, y, w, y, gridPaint); y += 200f }
                }
                "Liderazgo" -> {
                    val stripePaint = Paint().apply { color = android.graphics.Color.WHITE; alpha = 20; isAntiAlias = true }
                    for (i in -2..6) {
                        val startY = i * 450f
                        val path = android.graphics.Path().apply {
                            moveTo(0f, startY)
                            lineTo(w, startY - w)
                            lineTo(w, startY - w + 200f)
                            lineTo(0f, startY + 200f)
                            close()
                        }
                        canvas.drawPath(path, stripePaint)
                    }
                }
                "Biblia" -> {
                    val circlePaint = Paint().apply { color = android.graphics.Color.WHITE; alpha = 15; style = Paint.Style.STROKE; strokeWidth = 5f; isAntiAlias = true }
                    for (i in 0..3) {
                        canvas.drawCircle(w / 2f, h / 2f, (i * 200f) + 150f, circlePaint)
                    }
                    val bookPaint = Paint().apply { color = android.graphics.Color.WHITE; alpha = 38; isAntiAlias = true }
                    val bookPath = android.graphics.Path().apply {
                        moveTo(w * 0.3f, h * 0.8f)
                        quadTo(w * 0.5f, h * 0.85f, w * 0.7f, h * 0.8f)
                        lineTo(w * 0.7f, h)
                        lineTo(w * 0.3f, h)
                        close()
                    }
                    canvas.drawPath(bookPath, bookPaint)
                    val starPaint = Paint().apply { color = android.graphics.Color.WHITE; alpha = 100; isAntiAlias = true }
                    for(i in 0..10) {
                        canvas.drawCircle(random.nextFloat() * w, random.nextFloat() * h, random.nextFloat() * 10f + 5f, starPaint)
                    }
                }
            }
            
            // Unicolor square for the text to live in
            val cardRect = android.graphics.RectF(80f, 160f, width - 80f, height - 160f)
            val cardPaint = Paint().apply {
                color = android.graphics.Color.BLACK
                alpha = 150 // Translúcido pero constante (unicolor effect)
                isAntiAlias = true
            }
            canvas.drawRoundRect(cardRect, 80f, 80f, cardPaint)
            
            val borderPaint = Paint().apply {
                color = android.graphics.Color.WHITE
                alpha = 50
                style = Paint.Style.STROKE
                strokeWidth = 4f
                isAntiAlias = true
            }
            canvas.drawRoundRect(cardRect, 80f, 80f, borderPaint)

            val quoteMarkPaint = Paint().apply {
                color = android.graphics.Color.WHITE
                alpha = 200
                textSize = 180f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText("“", width / 2f, 320f, quoteMarkPaint)

            val textPaint = TextPaint().apply {
                color = android.graphics.Color.WHITE
                textSize = 64f
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }

            val staticLayout = StaticLayout.Builder.obtain(quote.text, 0, quote.text.length, textPaint, width - 240)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setLineSpacing(0f, 1.3f)
                .build()

            canvas.save()
            canvas.translate(width / 2f, height / 2f - staticLayout.height / 2f)
            staticLayout.draw(canvas)
            canvas.restore()

            val authorPaint = Paint().apply {
                color = android.graphics.Color.WHITE
                alpha = 255
                textSize = 42f
                letterSpacing = 0.05f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText(quote.author.uppercase(), width / 2f, height / 2f + staticLayout.height / 2f + 120f, authorPaint)
            
            val catPaint = Paint().apply {
                color = android.graphics.Color.WHITE
                alpha = 180
                textSize = 32f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText(quote.category, width / 2f, height / 2f + staticLayout.height / 2f + 180f, catPaint)
            
            
            val brandingPaint = Paint().apply {
                color = android.graphics.Color.WHITE
                alpha = 150
                textSize = 36f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText("Oratoria & Liderazgo", width / 2f, height - 60f, brandingPaint)

            val imagesDir = File(context.cacheDir, "images")
            imagesDir.mkdirs()
            val file = File(imagesDir, "quote.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "✨ \"${quote.text}\" - ${quote.author}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Compartir Frase"))
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }
}
