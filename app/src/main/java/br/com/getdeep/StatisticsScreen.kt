package br.com.getdeep

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.getdeep.data.GameRepository
import br.com.getdeep.data.Questions

@Composable
fun StatisticsScreen(
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val repository = remember { GameRepository(context) }

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
                    text = "Estatísticas e Histórico",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Resumo geral
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📊 Resumo Geral",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val totalUsed = repository.getUsedQuestionsCount("ice_breaker") +
                            repository.getUsedQuestionsCount("deep") +
                            repository.getUsedQuestionsCount("deeper")
                    val totalQuestions = repository.getTotalQuestionsCount("ice_breaker") +
                            repository.getTotalQuestionsCount("deep") +
                            repository.getTotalQuestionsCount("deeper")

                    Text(
                        text = "$totalUsed",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B46C1)
                    )

                    Text(
                        text = "de $totalQuestions perguntas respondidas",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val percentage = if (totalQuestions > 0) (totalUsed * 100) / totalQuestions else 0
                    Text(
                        text = "$percentage% do jogo explorado",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Barra de progresso visual
                    LinearProgressIndicator(
                        progress = if (totalQuestions > 0) totalUsed.toFloat() / totalQuestions.toFloat() else 0f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Color(0xFF6B46C1),
                        trackColor = Color.Gray.copy(alpha = 0.3f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Lista de categorias com perguntas
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Quebra-Gelo
                item {
                    CategoryDetailCard(
                        title = "🧊 Quebra-Gelo",
                        description = "Perguntas leves e divertidas para esquentar",
                        usedCount = repository.getUsedQuestionsCount("ice_breaker"),
                        totalCount = repository.getTotalQuestionsCount("ice_breaker"),
                        usedQuestions = repository.getUsedQuestions("ice_breaker").map { Questions.iceBreaker[it] },
                        color = Color(0xFF34D399)
                    )
                }

                // Profundo
                item {
                    CategoryDetailCard(
                        title = "🌊 Profundo",
                        description = "Perguntas mais pessoais para se conhecerem melhor",
                        usedCount = repository.getUsedQuestionsCount("deep"),
                        totalCount = repository.getTotalQuestionsCount("deep"),
                        usedQuestions = repository.getUsedQuestions("deep").map { Questions.deep[it] },
                        color = Color(0xFF60A5FA)
                    )
                }

                // Mais Profundo
                item {
                    CategoryDetailCard(
                        title = "💕 Mais Profundo",
                        description = "Perguntas íntimas para casais prontos para ir mais fundo",
                        usedCount = repository.getUsedQuestionsCount("deeper"),
                        totalCount = repository.getTotalQuestionsCount("deeper"),
                        usedQuestions = repository.getUsedQuestions("deeper").map { Questions.deeper[it] },
                        color = Color(0xFFEC4899)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mensagem motivacional
            if (repository.hasGameInProgress()) {
                Text(
                    text = "Continue jogando para descobrir mais sobre vocês! 💜",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = "Comece um jogo para ver suas estatísticas aqui!",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun CategoryDetailCard(
    title: String,
    description: String,
    usedCount: Int,
    totalCount: Int,
    usedQuestions: List<String>,
    color: Color
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header da categoria
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 16.sp
                    )
                }

                Text(
                    text = "$usedCount/$totalCount",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de progresso da categoria
            LinearProgressIndicator(
                progress = if (totalCount > 0) usedCount.toFloat() / totalCount.toFloat() else 0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = color,
                trackColor = color.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Botão para expandir/contrair
            if (usedQuestions.isNotEmpty()) {
                TextButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isExpanded) "Ocultar perguntas respondidas" else "Ver perguntas respondidas (${usedQuestions.size})",
                        color = color,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Lista de perguntas (quando expandida)
                if (isExpanded) {
                    Spacer(modifier = Modifier.height(8.dp))

                    usedQuestions.forEachIndexed { index, question ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = color.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "${index + 1}.",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = color,
                                    modifier = Modifier.padding(top = 1.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = question,
                                    fontSize = 13.sp,
                                    color = Color.Black,
                                    lineHeight = 17.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            } else {
                // Mensagem quando não há perguntas respondidas
                Text(
                    text = "Nenhuma pergunta desta categoria foi respondida ainda",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}