package br.com.getdeep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.getdeep.ui.theme.GetDeepAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GetDeepAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GetDeepApp()
                }
            }
        }
    }
}

@Composable
fun GetDeepApp() {
    var currentScreen by remember { mutableStateOf("home") }

    when (currentScreen) {
        "home" -> HomeScreen(
            onStartGame = { currentScreen = "menu" }
        )
        "menu" -> MenuScreen(
            onBack = { currentScreen = "home" },
            onStartGame = { currentScreen = "game" },
            onShowStatistics = { currentScreen = "statistics" }
        )
        "game" -> GameScreen(
            category = "", // Não precisa mais da categoria
            onBack = { currentScreen = "menu" }
        )
        "statistics" -> StatisticsScreen(
            onBack = { currentScreen = "menu" }
        )
    }
}

@Composable
fun HomeScreen(onStartGame: () -> Unit) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6B46C1), // Purple
                        Color(0xFF9333EA)  // Lighter purple
                    )
                )
            )
    ) {
        val isSmallScreen = maxHeight < 600.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 24.dp,
                    vertical = if (isSmallScreen) 16.dp else 32.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = if (isSmallScreen)
                Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
            else
                Arrangement.Center
        ) {
            // Title
            Text(
                text = "Let's Get Deep",
                fontSize = if (isSmallScreen) 36.sp else 56.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = if (isSmallScreen) 40.sp else 60.sp
            )

            if (!isSmallScreen) {
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = "Instruções",
                fontSize = if (isSmallScreen) 16.sp else 20.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            if (!isSmallScreen) {
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Instructions
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .padding(horizontal = if (isSmallScreen) 8.dp else 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(
                        if (isSmallScreen) 16.dp else 24.dp
                    )
                ) {
                    Text(
                        text = "Hora de largar o celular e conhecer melhor a pessoa com quem você passa 99% do seu tempo.",
                        fontSize = if (isSmallScreen) 13.sp else 16.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = if (isSmallScreen) 18.sp else 22.sp
                    )

                    Spacer(modifier = Modifier.height(if (isSmallScreen) 8.dp else 16.dp))

                    Text(
                        text = "Dividam as cartas em 3 pilhas: \"Quebra-Gelo\", \"Profundo\" e \"Mais Profundo\".",
                        fontSize = if (isSmallScreen) 11.sp else 14.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        lineHeight = if (isSmallScreen) 16.sp else 20.sp
                    )

                    Spacer(modifier = Modifier.height(if (isSmallScreen) 6.dp else 12.dp))

                    Text(
                        text = "Os jogadores se alternam lendo e respondendo as perguntas, começando por 2 Quebra-Gelo, depois 2 Profundas e 1 Mais Profunda.",
                        fontSize = if (isSmallScreen) 11.sp else 14.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        lineHeight = if (isSmallScreen) 16.sp else 20.sp
                    )

                    Spacer(modifier = Modifier.height(if (isSmallScreen) 6.dp else 12.dp))

                    Text(
                        text = "Joguem até saberem demais um do outro... ou até ser hora de ir para o quarto.",
                        fontSize = if (isSmallScreen) 11.sp else 14.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        lineHeight = if (isSmallScreen) 16.sp else 20.sp
                    )
                }
            }

            if (!isSmallScreen) {
                Spacer(modifier = Modifier.height(48.dp))
            }

            // Start button - SEMPRE VISÍVEL
            Button(
                onClick = onStartGame,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isSmallScreen) 48.dp else 56.dp)
                    .padding(horizontal = if (isSmallScreen) 16.dp else 32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF6B46C1)
                ),
                shape = RoundedCornerShape(if (isSmallScreen) 24.dp else 28.dp)
            ) {
                Text(
                    text = "COMEÇAR JOGO",
                    fontSize = if (isSmallScreen) 14.sp else 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    GetDeepAppTheme {
        HomeScreen(onStartGame = {})
    }
}