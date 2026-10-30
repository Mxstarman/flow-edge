package com.ecrisar.flow.edge.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecrisar.flow.edge.ui.theme.OneUIBlue
import java.text.DecimalFormat

/**
 * Calculator Edge Panel:
 * Sleek Samsung One UI quick calculator with instant arithmetic evaluation.
 */
@Composable
fun CalculatorPanel(
    modifier: Modifier = Modifier
) {
    var expression by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("0") }

    fun onButtonClick(btn: String) {
        when (btn) {
            "C" -> {
                expression = ""
                result = "0"
            }
            "=" -> {
                if (expression.isNotEmpty()) {
                    val evaluated = evaluateSimpleExpression(expression)
                    result = evaluated
                    expression = evaluated
                }
            }
            "⌫" -> {
                if (expression.isNotEmpty()) {
                    expression = expression.dropLast(1)
                    result = if (expression.isEmpty()) "0" else evaluateSimpleExpression(expression)
                }
            }
            "+/-" -> {
                if (expression.isNotEmpty()) {
                    expression = if (expression.startsWith("-")) {
                        expression.removePrefix("-")
                    } else {
                        "-$expression"
                    }
                    result = evaluateSimpleExpression(expression)
                }
            }
            else -> {
                expression += btn
                result = evaluateSimpleExpression(expression)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Display Screen
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF141518),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x22FFFFFF)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = expression.ifEmpty { "0" },
                    color = Color(0xFF8E929B),
                    fontSize = 14.sp,
                    maxLines = 1,
                    textAlign = TextAlign.End
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = result,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    textAlign = TextAlign.End
                )
            }
        }

        // Keypad Grid
        val buttonRows = listOf(
            listOf("C", "+/-", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("⌫", "0", ".", "=")
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            buttonRows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    row.forEach { label ->
                        CalcButton(
                            label = label,
                            onClick = { onButtonClick(label) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalcButton(
    label: String,
    onClick: () -> Unit
) {
    val isOperator = label in listOf("÷", "×", "-", "+", "=")
    val isAction = label in listOf("C", "+/-", "%", "⌫")

    val bg = when {
        label == "=" -> OneUIBlue
        isOperator -> Color(0xFF2C3240)
        isAction -> Color(0xFF23252B)
        else -> Color(0xFF1B1D22)
    }

    val textColor = when {
        label == "=" -> Color.White
        isOperator -> OneUIBlue
        isAction -> Color(0xFFFF9800)
        else -> Color.White
    }

    Box(
        modifier = Modifier
            .size(42.dp)
            .shadow(2.dp, CircleShape)
            .clip(CircleShape)
            .background(bg)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 15.sp,
            fontWeight = if (isOperator || label == "=") FontWeight.Bold else FontWeight.Medium
        )
    }
}

/**
 * Basic expression evaluator supporting +, -, ×, ÷, %
 */
private fun evaluateSimpleExpression(input: String): String {
    try {
        val sanitized = input.replace("×", "*").replace("÷", "/")
        if (sanitized.isBlank()) return "0"

        // Tokenize numbers and operators
        val tokens = mutableListOf<String>()
        var currentNumber = StringBuilder()

        for (i in sanitized.indices) {
            val c = sanitized[i]
            if (c in '0'..'9' || c == '.') {
                currentNumber.append(c)
            } else if (c in listOf('+', '-', '*', '/', '%')) {
                if (currentNumber.isNotEmpty()) {
                    tokens.add(currentNumber.toString())
                    currentNumber = StringBuilder()
                } else if (c == '-' && (tokens.isEmpty() || tokens.last() in listOf("+", "-", "*", "/"))) {
                    // Unary minus
                    currentNumber.append(c)
                    continue
                }
                tokens.add(c.toString())
            }
        }
        if (currentNumber.isNotEmpty()) {
            tokens.add(currentNumber.toString())
        }

        if (tokens.isEmpty()) return "0"

        // First pass: multiplication, division, modulo
        val pass1 = mutableListOf<String>()
        var idx = 0
        while (idx < tokens.size) {
            val token = tokens[idx]
            if (token in listOf("*", "/", "%")) {
                val prev = pass1.removeAt(pass1.lastIndex).toDoubleOrNull() ?: 0.0
                val next = tokens.getOrNull(idx + 1)?.toDoubleOrNull() ?: 1.0
                val res = when (token) {
                    "*" -> prev * next
                    "/" -> if (next != 0.0) prev / next else 0.0
                    "%" -> prev * (next / 100.0)
                    else -> 0.0
                }
                pass1.add(res.toString())
                idx += 2
            } else {
                pass1.add(token)
                idx++
            }
        }

        // Second pass: addition and subtraction
        var finalResult = pass1.firstOrNull()?.toDoubleOrNull() ?: 0.0
        var pIdx = 1
        while (pIdx < pass1.size) {
            val op = pass1[pIdx]
            val nextVal = pass1.getOrNull(pIdx + 1)?.toDoubleOrNull() ?: 0.0
            if (op == "+") {
                finalResult += nextVal
            } else if (op == "-") {
                finalResult -= nextVal
            }
            pIdx += 2
        }

        val df = DecimalFormat("#.######")
        return df.format(finalResult)
    } catch (e: Exception) {
        return "..."
    }
}
