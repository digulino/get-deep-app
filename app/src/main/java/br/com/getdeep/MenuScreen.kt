package br.com.getdeep

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info

import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import br.com.getdeep.data.GameRepository
import br.com.getdeep.data.Questions

@Composable
fun MenuScreen(
    onBack: () -> Unit,
    onStartGame: () -> Unit,
    onShowStatistics: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val repository = remember { GameRepository(context) }

    var showStatistics by remember { mutableStateOf(false) }
    var showResetConfirmation by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6B46C1),
                        Color(0xFF9333EA)
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
                    text = "Menu Principal",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(48.dp)) // Balance the back button
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Cards do menu
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Card Continuar Jogo (só aparece se há jogo salvo)
                if (repository.hasGameInProgress()) {
                    MenuCard(
                        title = "Continuar Jogo",
                        subtitle = repository.getGameProgress(),
                        icon = Icons.Default.PlayArrow,
                        backgroundColor = Color(0xFF34D399),
                        onClick = onStartGame
                    )
                }

                // Card Novo Jogo
                MenuCard(
                    title = if (repository.hasGameInProgress()) "Novo Jogo" else "Iniciar Jogo",
                    subtitle = if (repository.hasGameInProgress())
                        "Começar do zero (apaga progresso atual)"
                    else
                        "Começar uma nova sessão de perguntas",
                    icon = Icons.Default.Add,
                    backgroundColor = Color(0xFF60A5FA),
                    onClick = {
                        if (repository.hasGameInProgress()) {
                            showResetConfirmation = true
                        } else {
                            onStartGame()
                        }
                    }
                )

                // Card Estatísticas Detalhadas
                MenuCard(
                    title = "Estatísticas Detalhadas",
                    subtitle = "Ver histórico completo com perguntas respondidas",
                    icon = Icons.Default.Info,
                    backgroundColor = Color(0xFFEC4899),
                    onClick = onShowStatistics
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }

    // Diálogos
    if (showStatistics) {
        StatisticsDialog(
            repository = repository,
            onDismiss = { showStatistics = false }
        )
    }

    if (showResetConfirmation) {
        ResetGameDialog(
            onConfirm = {
                repository.resetGame()
                showResetConfirmation = false
                onStartGame()
            },
            onDismiss = { showResetConfirmation = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    // Animação de escala para o efeito de pressão
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(
            durationMillis = 100,
            easing = EaseInOut
        ),
        label = "card_scale"
    )

    Card(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .scale(scale),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        // Reset da animação após um tempo
        LaunchedEffect(isPressed) {
            if (isPressed) {
                kotlinx.coroutines.delay(150)
                isPressed = false
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 16.sp,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun StatisticsDialog(
    repository: GameRepository,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📊 Estatísticas",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Estatísticas por categoria
                StatisticRow(
                    category = "Quebra-Gelo",
                    used = repository.getUsedQuestionsCount("ice_breaker"),
                    total = repository.getTotalQuestionsCount("ice_breaker"),
                    color = Color(0xFF34D399)
                )

                Spacer(modifier = Modifier.height(16.dp))

                StatisticRow(
                    category = "Profundo",
                    used = repository.getUsedQuestionsCount("deep"),
                    total = repository.getTotalQuestionsCount("deep"),
                    color = Color(0xFF60A5FA)
                )

                Spacer(modifier = Modifier.height(16.dp))

                StatisticRow(
                    category = "Mais Profundo",
                    used = repository.getUsedQuestionsCount("deeper"),
                    total = repository.getTotalQuestionsCount("deeper"),
                    color = Color(0xFFEC4899)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Total geral
                val totalUsed = repository.getUsedQuestionsCount("ice_breaker") +
                        repository.getUsedQuestionsCount("deep") +
                        repository.getUsedQuestionsCount("deeper")
                val totalQuestions = repository.getTotalQuestionsCount("ice_breaker") +
                        repository.getTotalQuestionsCount("deep") +
                        repository.getTotalQuestionsCount("deeper")

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF6B46C1).copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Total Geral",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6B46C1)
                        )
                        Text(
                            text = "$totalUsed / $totalQuestions",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6B46C1)
                        )
                        val percentage = if (totalQuestions > 0) (totalUsed * 100) / totalQuestions else 0
                        Text(
                            text = "$percentage% explorado",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6B46C1)
                    )
                ) {
                    Text("FECHAR", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun StatisticRow(
    category: String,
    used: Int,
    total: Int,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(color, RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = category,
                fontSize = 16.sp,
                color = Color.Black
            )
        }

        Text(
            text = "$used / $total",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun ResetGameDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "⚠️",
                    fontSize = 48.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Novo Jogo",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Isso irá apagar todo o progresso atual e reiniciar o histórico de perguntas. Tem certeza?",
                    fontSize = 16.sp,
                    color = Color.Black.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF6B46C1)
                        )
                    ) {
                        Text(
                            "NÃO",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFDC2626),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            "SIM",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}