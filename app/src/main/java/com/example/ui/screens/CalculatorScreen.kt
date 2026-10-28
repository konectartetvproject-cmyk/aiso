package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(onBack: () -> Unit) {
    var display by remember { mutableStateOf("0") }
    var previousOperand by remember { mutableStateOf("") }
    var operation by remember { mutableStateOf<String?>(null) }
    var isNewOperand by remember { mutableStateOf(true) }

    fun onClear() {
        display = "0"
        previousOperand = ""
        operation = null
        isNewOperand = true
    }

    fun onBackspace() {
        if (isNewOperand) return
        display = if (display.length > 1) display.dropLast(1) else "0"
    }

    fun onDigit(digit: String) {
        if (isNewOperand) {
            display = digit
            isNewOperand = false
        } else {
            if (display == "0" && digit != ".") {
                display = digit
            } else if (digit == "." && display.contains(".")) {
                return
            } else {
                display += digit
            }
        }
    }

    fun calculateResult(): String {
        val num1 = previousOperand.toDoubleOrNull() ?: return display
        val num2 = display.toDoubleOrNull() ?: return display
        val result = when (operation) {
            "+" -> num1 + num2
            "-" -> num1 - num2
            "×" -> num1 * num2
            "÷" -> if (num2 != 0.0) num1 / num2 else Double.NaN
            else -> num2
        }
        
        return if (result.isNaN()) "Error"
        else if (result % 1.0 == 0.0) result.toLong().toString()
        else result.toString()
    }

    fun onOperation(op: String) {
        if (operation != null && !isNewOperand) {
            display = calculateResult()
        }
        previousOperand = display
        operation = op
        isNewOperand = true
    }

    fun onEqual() {
        if (operation != null && !isNewOperand) {
            display = calculateResult()
            operation = null
            previousOperand = ""
            isNewOperand = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calculadora", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    if (previousOperand.isNotEmpty() && operation != null) {
                        Text(
                            text = "$previousOperand $operation",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = display,
                        fontSize = if (display.length > 10) 48.sp else 64.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.End,
                        maxLines = 1
                    )
                }
            }

            val buttons = listOf(
                listOf("C", "DEL", "%", "÷"),
                listOf("7", "8", "9", "×"),
                listOf("4", "5", "6", "-"),
                listOf("1", "2", "3", "+"),
                listOf("00", "0", ".", "=")
            )

            buttons.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    row.forEach { btn ->
                        CalculatorButton(
                            text = btn,
                            modifier = Modifier.weight(1f).aspectRatio(1f).padding(4.dp),
                            onClick = {
                                when (btn) {
                                    "C" -> onClear()
                                    "DEL" -> onBackspace()
                                    "%" -> {
                                        val num = display.toDoubleOrNull()
                                        if (num != null) {
                                            display = (num / 100).toString()
                                            isNewOperand = true
                                        }
                                    }
                                    "÷", "×", "-", "+" -> onOperation(btn)
                                    "=" -> onEqual()
                                    else -> onDigit(btn)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isOperation = text in listOf("÷", "×", "-", "+", "=")
    val isAction = text in listOf("C", "DEL", "%")
    
    val containerColor = when {
        isOperation -> MaterialTheme.colorScheme.primary
        isAction -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    val contentColor = when {
        isOperation -> MaterialTheme.colorScheme.onPrimary
        isAction -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(containerColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (text == "DEL") {
            Icon(Icons.Default.Backspace, contentDescription = "Borrar", tint = contentColor)
        } else {
            Text(
                text = text,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}
