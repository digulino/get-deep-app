package br.com.getdeep.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.random.Random

data class GameRound(
    val roundNumber: Int,
    val iceBreakersNeeded: Int = 2,
    val deepNeeded: Int = 2,
    val deeperNeeded: Int = 1,
    var iceBreakersPlayed: Int = 0,
    var deepPlayed: Int = 0,
    var deeperPlayed: Int = 0
) {
    fun getCurrentPhase(): String {
        return when {
            iceBreakersPlayed < iceBreakersNeeded -> "ice_breaker"
            deepPlayed < deepNeeded -> "deep"
            deeperPlayed < deeperNeeded -> "deeper"
            else -> "completed"
        }
    }

    fun isCompleted(): Boolean = getCurrentPhase() == "completed"

    fun getPhaseProgress(): String {
        return when (getCurrentPhase()) {
            "ice_breaker" -> "Quebra-Gelo ${iceBreakersPlayed + 1}/$iceBreakersNeeded"
            "deep" -> "Profundo ${deepPlayed + 1}/$deepNeeded"
            "deeper" -> "Mais Profundo ${deeperPlayed + 1}/$deeperNeeded"
            else -> "Round Completo!"
        }
    }

    fun getTotalQuestionsInRound(): Int = iceBreakersNeeded + deepNeeded + deeperNeeded

    fun getCompletedQuestions(): Int = iceBreakersPlayed + deepPlayed + deeperPlayed
}

class GameState(private val repository: GameRepository) {

    // Estado atual do jogo
    var currentRound by mutableStateOf(repository.currentRound.value)
        private set

    var currentQuestion by mutableStateOf<String?>(null)
        private set

    var currentQuestionIndex by mutableStateOf(-1)
        private set

    var gamePhase by mutableStateOf("waiting") // waiting, category_transition, question_drawn, question_playing, round_complete
        private set

    init {
        // Observa mudanças no repositório
        currentRound = repository.currentRound.value

        // Se não há pergunta ativa, sorteia automaticamente uma
        if (currentQuestion == null && !currentRound.isCompleted()) {
            drawQuestion()
        }
    }

    fun initializeGame() {
        // Força o sorteio de uma nova pergunta quando o jogo inicia
        if (!currentRound.isCompleted()) {
            drawQuestion()
        }
    }

    fun drawQuestion(): Boolean {
        val category = currentRound.getCurrentPhase()
        if (category == "completed") return false

        val availableQuestions = repository.getAvailableQuestions(category)
        if (availableQuestions.isEmpty()) {
            // Se não há mais perguntas disponíveis, pula para a próxima fase
            skipToNextPhase()
            return drawQuestion()
        }

        val randomIndex = availableQuestions.random()
        currentQuestionIndex = randomIndex
        currentQuestion = getQuestionByIndex(category, randomIndex)
        gamePhase = "question_drawn"

        return true
    }

    private fun checkForCategoryTransition(): Boolean {
        // Verifica se após completar a pergunta atual, vamos mudar de categoria
        val currentPhase = currentRound.getCurrentPhase()
        val nextTotalCompleted = currentRound.getCompletedQuestions() + 1

        // Se após essa pergunta completarmos os ice breakers, próxima é deep
        if (currentPhase == "ice_breaker" && nextTotalCompleted == currentRound.iceBreakersNeeded) {
            return true
        }

        // Se após essa pergunta completarmos os deep, próxima é deeper
        if (currentPhase == "deep" && nextTotalCompleted == currentRound.iceBreakersNeeded + currentRound.deepNeeded) {
            return true
        }

        return false
    }

    fun acceptQuestion() {
        val category = currentRound.getCurrentPhase()

        // Salva a pergunta como usada no repositório
        repository.addUsedQuestion(category, currentQuestionIndex)

        // Vai para o modo de jogar a pergunta (SEM atualizar o round ainda)
        gamePhase = "question_playing"
    }

    fun finishQuestion() {
        // Primeiro, atualiza o round com a pergunta completada
        val category = currentRound.getCurrentPhase()
        val updatedRound = when (category) {
            "ice_breaker" -> currentRound.copy(iceBreakersPlayed = currentRound.iceBreakersPlayed + 1)
            "deep" -> currentRound.copy(deepPlayed = currentRound.deepPlayed + 1)
            "deeper" -> currentRound.copy(deeperPlayed = currentRound.deeperPlayed + 1)
            else -> currentRound
        }

        currentRound = updatedRound
        repository.updateRound(updatedRound)

        // Depois verifica o que fazer
        if (currentRound.isCompleted()) {
            gamePhase = "round_complete"
        } else {
            // Verifica se a PRÓXIMA pergunta será de uma categoria diferente
            val nextPhase = currentRound.getCurrentPhase()
            if (nextPhase != category) {
                // Mudança de categoria - mostra transição
                gamePhase = "category_transition"
                currentQuestion = null
                currentQuestionIndex = -1
            } else {
                // Mesma categoria - sorteia próxima pergunta
                drawQuestion()
            }
        }
    }

    fun proceedFromTransition() {
        // Chama após a tela de transição, para sortear pergunta da nova categoria
        drawQuestion()
    }

    fun startNextRound() {
        val newRound = GameRound(roundNumber = currentRound.roundNumber + 1)
        currentRound = newRound
        repository.updateRound(newRound)

        // Automaticamente sorteia a primeira pergunta do novo round
        drawQuestion()
    }

    fun rejectQuestion() {
        // Sorteia uma nova pergunta automaticamente
        drawQuestion()
    }

    private fun getQuestionByIndex(category: String, index: Int): String {
        return when (category) {
            "ice_breaker" -> Questions.iceBreaker[index]
            "deep" -> Questions.deep[index]
            "deeper" -> Questions.deeper[index]
            else -> ""
        }
    }

    private fun skipToNextPhase() {
        val updatedRound = when (currentRound.getCurrentPhase()) {
            "ice_breaker" -> currentRound.copy(iceBreakersPlayed = currentRound.iceBreakersNeeded)
            "deep" -> currentRound.copy(deepPlayed = currentRound.deepNeeded)
            "deeper" -> currentRound.copy(deeperPlayed = currentRound.deeperNeeded)
            else -> currentRound
        }
        currentRound = updatedRound
        repository.updateRound(updatedRound)
    }

    fun resetGame() {
        repository.resetGame()
        currentRound = GameRound(roundNumber = 1)
        currentQuestion = null
        currentQuestionIndex = -1
        gamePhase = "waiting"
    }

    fun getUsedQuestionsCount(category: String): Int {
        return repository.getUsedQuestionsCount(category)
    }

    fun getTotalQuestionsCount(category: String): Int {
        return repository.getTotalQuestionsCount(category)
    }

    fun hasGameInProgress(): Boolean {
        return repository.hasGameInProgress()
    }

    fun getGameProgress(): String {
        return repository.getGameProgress()
    }
}