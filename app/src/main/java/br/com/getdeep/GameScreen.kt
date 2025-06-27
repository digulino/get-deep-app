package br.com.getdeep

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
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
                        .pointerInput(Unit) {
                            detectDragGestures { change, _ ->
                                if (abs(change.x) > 50 || abs(change.y) > 50) {
                                    isFlipped = !isFlipped
                                }
                            }
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
                                    text = "Tap or swipe to reveal answer side",
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
                                    text = "Time to Answer!",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Share your thoughts and feelings about this question",
                                    fontSize = 16.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 22.sp
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = "Tap or swipe to go back",
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
                    Text("Previous")
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
                    Text("Next")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "#LetsGetDeep",
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
        "ice_breaker" -> "Ice Breaker"
        "deep" -> "Deep"
        "deeper" -> "Deeper"
        else -> "Questions"
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
        "ice_breaker" -> listOf(
            "Fall or spring?",
            "Chocolate or vanilla?",
            "What was the greatest decade for fashion?",
            "Is it better to be a kid or an adult?",
            "What scent reminds you of home?",
            "If you could cancel one major holiday, which one would you eliminate?",
            "What is your favorite place you've visited?",
            "What's your favorite band or musical artist?",
            "What is the best chain restaurant?",
            "What do you think is the coolest accent to have?",
            "Bunge jumping or hot air balloon?",
            "What's one unusual skill you have?",
            "What's your favorite season?",
            "What was your first job?",
            "Extrovert or introvert?",
            "Where's the most unusual place you've fallen asleep?",
            "Would you ever switch to being vegetarian or vegan?",
            "What your go-to snack at the movies?",
            "Do you sing in the shower?",
            "Which year of your life so far has been your favorite? Why?",
            "What's your favorite holiday?",
            "What's your go-to drunk food?",
            "Have you ever met a celebrity?",
            "Take a silly picture together of you both playing the game.",
            "What's your favorite song of all time?",
            "Beer, wine, or cocktail?",
            "What is your favorite thing to do in the fall?"
        )
        "deep" -> listOf(
            "What's your favorite thing to do during your time off or weekend?",
            "Between us, who has the crazier family?",
            "What did you used to do with all your free time before we started dating?",
            "Have you ever had a near-death experience?",
            "Lose the ability to lie or believe everything you're told? Why?",
            "What's your favorite memory of you and your friends? Why?",
            "What does your family value most?",
            "What traits do you look for in a partner?",
            "What's something you're weirdly good at?",
            "If you could be fluent in another language, which would it be? Why?",
            "When have you been the unluckiest?",
            "Where do you see yourself in 5 years?",
            "What's your go-to de-stress activity?",
            "What is your most prized possession? Why?",
            "Who in your family are you closest to? Why?",
            "What's your guilty pleasure?",
            "What is the most important lesson you learned as a kid?",
            "What's your go-to snack at the airport?",
            "What's the craziest thing you've done while drunk?",
            "What does being happy mean to you?",
            "What is the last thing you do before bed?",
            "What food have you never tried but want to?",
            "Do you believe opposites attract, or do you prefer being with someone who is similar to you?",
            "What was your favorite meal growing up? Why?",
            "Do you prefer home-cooked meals, or restaurant food?",
            "Who or what taught you what love means?",
            "What's a movie you hate but everyone else loves. Why?"
        )
        "deeper" -> listOf(
            "Who was your role model growing up? Why?",
            "If you were to die today with no chance to communicate with anyone, what would you most regret not having tell someone? Why haven't you told them yet?",
            "How would your 10-year-old self react to who you are today?",
            "How would your parents describe you?",
            "What first attracted you to me?",
            "Do you believe in second chances? Why or why not?",
            "What is the most important trait in a friend? Why?",
            "If a fortune-teller could tell you 1 thing about your future, what would you ask?",
            "What is the most important lesson you learned as a child?",
            "If I could role-play any character in the bedroom, who would you like to see?",
            "Is there anyone you envy enough to want to trade lives with? Who?",
            "Who in your life do you feel most judged by?",
            "What white lie do you tell most often? Why?",
            "Is there anything your significant other's family does that you do not agree with?",
            "If we switched lives for a day, what do you think you would do first?",
            "What's something you're insecure about? Why?",
            "Give an example of a time you may have been too critical and harsh towards yourself.",
            "Is there anything that you feel is \"missing\" from our relationship?",
            "What are you extremely opinionated about?",
            "Have you ever done drugs? Which ones?",
            "What is the grossest thing I've seen you do?",
            "What does being happy mean to you?",
            "What's a decision you made that you wish you could redo?",
            "What's your favorite kind of foreplay?",
            "What hurts you the most during a fight/argument with your partner?",
            "How would you define trust?",
            "What's a non-negotiable for you in a relationship?",
            "What do your friends/siblings think about me?",
            "Do you feel like we spend too much time together, apart, or have a good balance?",
            "What slang have I picked up from you?",
            "For $1 Million, would you be willing to never see or talk to your bestfriend again?",
            "What characteristics about your partner's family do you find appealing?",
            "In what way are we the most different?",
            "What was the first thing you bought with your own money?",
            "What is one aspect of yourself you've seen improve since dating me?",
            "What is the biggest deal-breaker in a significant other?",
            "What's one thing you've always wanted to do, but never had the cuts to?",
            "What are you the most grateful for in your life?",
            "If you knew you were going to die tomorrow, what would you do in your last day?",
            "Would you accept 20 years of perfect happiness if it mean that you would die at the end of that period?",
            "What characteristics about your partner's family do you find appealing?",
            "You have plans with a friend, but on the day before you get invited to do something more exciting, What do you do?",
            "Who had the biggest impact on your self-esteem growing up?",
            "What's one place you want to have sex, that we haven't yet?",
            "What's your least favorite sex position?",
            "What is something you can't be trusted with, or something you can't trust your partner with?",
            "Describe a time you felt heard and listened to by your partner. What did they do?",
            "What is a moment you get nostalgic about?",
            "Who is your go-to person for advice? Why?",
            "How important is it to surround yourself with people who have the same political views as you?",
            "What is something about you that you think is important for your partner to know?",
            "Between us, who has better taste in music?",
            "Between us, who takes longer to order at a restaurant?",
            "Who makes more of the decisions in the relationship?",
            "What 3 words would you use to describe yourself?",
            "If you could change 1 thing about your childhood, what would you change? Why?",
            "If you could choose how you die, would you? What would you choose?",
            "What is your family's opinion on having kids?",
            "Give your partner a passionate kiss for several seconds.",
            "What's my favorite memory of us together?",
            "What's one thing I do because I love you, and not because I enjoy doing it?",
            "What do you think happens after death?",
            "If you could name 3 life rules, what would they be?"
        )
        else -> emptyList()
    }
}