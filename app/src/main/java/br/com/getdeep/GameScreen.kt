package br.com.getdeep

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.getdeep.data.GameState
import br.com.getdeep.data.GameRepository

@Composable
fun GameScreen(
    category: String, // Mantemos por compatibilidade, mas não usamos mais
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val repository = remember { GameRepository(context) }
    val gameState = remember { GameState(repository) }

    // Inicializa o jogo na primeira composição
    LaunchedEffect(Unit) {
        gameState.initializeGame()
    }

    // Cor baseada na pergunta atual, não na fase do round
    val currentQuestionCategory = remember(gameState.currentQuestion, gameState.gamePhase) {
        when (gameState.gamePhase) {
            "category_transition" -> gameState.currentRound.getCurrentPhase()
            else -> {
                // Se há pergunta ativa, usa a categoria da pergunta
                if (gameState.currentQuestion != null) {
                    getCurrentQuestionCategory(gameState)
                } else {
                    gameState.currentRound.getCurrentPhase()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        getCategoryColorByPhase(currentQuestionCategory),
                        getCategoryColorByPhase(currentQuestionCategory).copy(alpha = 0.8f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Top bar - Sem botão de refresh
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

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Round ${gameState.currentRound.roundNumber}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    if (gameState.gamePhase != "round_complete") {
                        Text(
                            text = gameState.currentRound.getPhaseProgress(),
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Espaço vazio para balancear (sem botão de refresh)
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Conteúdo principal baseado no estado do jogo
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when (gameState.gamePhase) {
                    "category_transition" -> CategoryTransitionContent(gameState)
                    "question_drawn" -> QuestionDrawnContent(gameState)
                    "question_playing" -> QuestionPlayingContent(gameState)
                    "round_complete" -> RoundCompleteContent(gameState)
                    else -> QuestionDrawnContent(gameState) // Default para question_drawn
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Estatísticas das perguntas usadas
            GameStatistics(gameState)
        }
    }
}

@Composable
fun CategoryTransitionContent(gameState: GameState) {
    val currentPhase = gameState.currentRound.getCurrentPhase()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = getCategoryColorByPhase(currentPhase).copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ícone da categoria
            Text(
                text = getCategoryIcon(currentPhase),
                fontSize = 64.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = getCategoryTitle(currentPhase).uppercase(),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = getCategoryDescription(currentPhase),
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedButton(
                onClick = { gameState.proceedFromTransition() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                backgroundColor = Color.White,
                contentColor = getCategoryColorByPhase(currentPhase),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "CONTINUAR",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun QuestionDrawnContent(gameState: GameState) {
    val questionCategory = getCurrentQuestionCategory(gameState)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // CORRIGIDO: Branco sólido sem transparência
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = getCategoryTitle(questionCategory),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = getCategoryColorByPhase(questionCategory),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = gameState.currentQuestion ?: "",
                fontSize = 20.sp,
                color = Color(0xFF1A1A1A), // CORRIGIDO: Preto sólido para máxima legibilidade
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Querem jogar esta pergunta?",
                fontSize = 16.sp,
                color = Color(0xFF666666), // CORRIGIDO: Cinza escuro para boa legibilidade
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { gameState.rejectQuestion() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF666666) // CORRIGIDO: Cinza escuro
                    )
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PULAR")
                }

                AnimatedButton(
                    onClick = { gameState.acceptQuestion() },
                    modifier = Modifier.weight(1f),
                    backgroundColor = getCategoryColorByPhase(questionCategory),
                    contentColor = Color.White
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("JOGAR")
                }
            }
        }
    }
}

@Composable
fun QuestionPlayingContent(gameState: GameState) {
    val questionCategory = getCurrentQuestionCategory(gameState)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // CORRIGIDO: Branco sólido sem transparência
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = getCategoryTitle(questionCategory),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = getCategoryColorByPhase(questionCategory),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = gameState.currentQuestion ?: "",
                fontSize = 20.sp,
                color = Color(0xFF1A1A1A), // CORRIGIDO: Preto sólido
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Ambos devem responder esta pergunta",
                fontSize = 16.sp,
                color = Color(0xFF666666), // CORRIGIDO: Cinza escuro
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedButton(
                onClick = { gameState.finishQuestion() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                backgroundColor = getCategoryColorByPhase(questionCategory),
                contentColor = Color.White,
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "PRÓXIMA",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun RoundCompleteContent(gameState: GameState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // CORRIGIDO: Branco sólido sem transparência
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎉",
                fontSize = 48.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Round ${gameState.currentRound.roundNumber} Completo!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A), // CORRIGIDO: Preto sólido
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Vocês completaram todas as ${gameState.currentRound.getTotalQuestionsInRound()} perguntas deste round. Prontos para o próximo?",
                fontSize = 16.sp,
                color = Color(0xFF666666), // CORRIGIDO: Cinza escuro
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedButton(
                onClick = { gameState.startNextRound() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                backgroundColor = Color(0xFF6B46C1),
                contentColor = Color.White,
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "PRÓXIMO ROUND",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AnimatedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    contentColor: Color,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
    content: @Composable RowScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    // Animação de escala
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )

    Button(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = modifier.scale(scale),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        shape = shape
    ) {
        // Reset da animação
        LaunchedEffect(isPressed) {
            if (isPressed) {
                kotlinx.coroutines.delay(100)
                isPressed = false
            }
        }

        content()
    }
}

@Composable
fun GameStatistics(gameState: GameState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatisticItem(
            title = "Quebra-Gelo",
            used = gameState.getUsedQuestionsCount("ice_breaker"),
            total = gameState.getTotalQuestionsCount("ice_breaker"),
            color = Color(0xFF34D399)
        )
        StatisticItem(
            title = "Profundo",
            used = gameState.getUsedQuestionsCount("deep"),
            total = gameState.getTotalQuestionsCount("deep"),
            color = Color(0xFF60A5FA)
        )
        StatisticItem(
            title = "Mais Profundo",
            used = gameState.getUsedQuestionsCount("deeper"),
            total = gameState.getTotalQuestionsCount("deeper"),
            color = Color(0xFFEC4899)
        )
    }
}

@Composable
fun StatisticItem(title: String, used: Int, total: Int, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$used/$total",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = title,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

fun getCurrentQuestionCategory(gameState: GameState): String {
    // Determina a categoria baseada no progresso atual (antes de incrementar)
    val completed = gameState.currentRound.getCompletedQuestions()

    return when {
        completed < gameState.currentRound.iceBreakersNeeded -> "ice_breaker"
        completed < gameState.currentRound.iceBreakersNeeded + gameState.currentRound.deepNeeded -> "deep"
        else -> "deeper"
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

fun getCategoryIcon(category: String): String {
    return when (category) {
        "ice_breaker" -> "🧊"
        "deep" -> "🌊"
        "deeper" -> "💕"
        else -> "❓"
    }
}

fun getCategoryDescription(category: String): String {
    return when (category) {
        "ice_breaker" -> "Perguntas leves e divertidas para esquentar"
        "deep" -> "Perguntas mais pessoais para se conhecerem melhor"
        "deeper" -> "Perguntas íntimas para casais prontos para ir mais fundo"
        else -> "Perguntas para se conhecerem"
    }
}

fun getCategoryColorByPhase(phase: String): Color {
    return when (phase) {
        "ice_breaker" -> Color(0xFF34D399)
        "deep" -> Color(0xFF60A5FA)
        "deeper" -> Color(0xFFEC4899)
        else -> Color(0xFF6B46C1)
    }
}