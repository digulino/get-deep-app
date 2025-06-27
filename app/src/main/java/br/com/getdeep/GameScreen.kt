package br.com.getdeep

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.getdeep.data.Questions
import kotlin.random.Random

@Composable
fun GameScreen(
    category: String,
    onBack: () -> Unit
) {
    val questions = getQuestionsForCategory(category)
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    // Animation for card flip
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "card_flip"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        getCategoryColor(category),
                        getCategoryColor(category).copy(alpha = 0.8f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White
                    )
                }

                Text(
                    text = getCategoryTitle(category),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                IconButton(
                    onClick = {
                        currentQuestionIndex = Random.nextInt(questions.size)
                        isFlipped = false
                    }
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Nova pergunta",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Progress indicator
            Text(
                text = "${currentQuestionIndex + 1} / ${questions.size}",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Question card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(rotation)
                        .clickable {
                            isFlipped = !isFlipped
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (rotation > 90f)
                            getCategoryColor(category).copy(alpha = 0.9f)
                        else
                            Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (rotation < 90f) {
                            // Front of card - Question
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = getCategoryTitle(category),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = getCategoryColor(category),
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = questions[currentQuestionIndex],
                                    fontSize = 20.sp,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 28.sp
                                )

                                Spacer(modifier = Modifier.height(32.dp))

                                Text(
                                    text = "Toque para revelar o lado da resposta",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            // Back of card - Answer space
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.rotate(180f) // Flip text back
                            ) {
                                Text(
                                    text = "Hora de Responder!",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Compartilhe seus pensamentos e sentimentos sobre esta pergunta",
                                    fontSize = 16.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 22.sp
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = "Toque para voltar",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        if (currentQuestionIndex > 0) {
                            currentQuestionIndex--
                            isFlipped = false
                        }
                    },
                    enabled = currentQuestionIndex > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.2f),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Anterior")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        if (currentQuestionIndex < questions.size - 1) {
                            currentQuestionIndex++
                            isFlipped = false
                        }
                    },
                    enabled = currentQuestionIndex < questions.size - 1,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = getCategoryColor(category)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Próxima")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "#VamosAprofundar",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

fun getCategoryTitle(category: String): String {
    return when (category) {
        "ice_breaker" -> "Quebra-Gelo"
        "deep" -> "Profundo"
        "deeper" -> "Mais Profundo"
        else -> "Perguntas"
    }
}

fun getCategoryColor(category: String): Color {
    return when (category) {
        "ice_breaker" -> Color(0xFF34D399)
        "deep" -> Color(0xFF60A5FA)
        "deeper" -> Color(0xFFEC4899)
        else -> Color(0xFF6B46C1)
    }
}

fun getQuestionsForCategory(category: String): List<String> {
    return when (category) {
        "ice_breaker" -> Questions.iceBreaker
        "deep" -> Questions.deep
        "deeper" -> Questions.deeper
        else -> emptyList()
    }
}