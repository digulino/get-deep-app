package br.com.getdeep.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("game_data", Context.MODE_PRIVATE)

    // StateFlows para observar mudanças
    private val _usedIceBreakers = MutableStateFlow(loadUsedQuestions("used_ice_breakers"))
    val usedIceBreakers: StateFlow<Set<Int>> = _usedIceBreakers.asStateFlow()

    private val _usedDeep = MutableStateFlow(loadUsedQuestions("used_deep"))
    val usedDeep: StateFlow<Set<Int>> = _usedDeep.asStateFlow()

    private val _usedDeeper = MutableStateFlow(loadUsedQuestions("used_deeper"))
    val usedDeeper: StateFlow<Set<Int>> = _usedDeeper.asStateFlow()

    private val _currentRound = MutableStateFlow(loadCurrentRound())
    val currentRound: StateFlow<GameRound> = _currentRound.asStateFlow()

    init {
        // Carrega dados salvos na inicialização
        loadGameState()
    }

    private fun loadUsedQuestions(key: String): Set<Int> {
        val saved = prefs.getStringSet(key, emptySet()) ?: emptySet()
        return saved.mapNotNull { it.toIntOrNull() }.toSet()
    }

    private fun saveUsedQuestions(key: String, questions: Set<Int>) {
        prefs.edit()
            .putStringSet(key, questions.map { it.toString() }.toSet())
            .apply()
    }

    private fun loadCurrentRound(): GameRound {
        return GameRound(
            roundNumber = prefs.getInt("round_number", 1),
            iceBreakersPlayed = prefs.getInt("ice_breakers_played", 0),
            deepPlayed = prefs.getInt("deep_played", 0),
            deeperPlayed = prefs.getInt("deeper_played", 0)
        )
    }

    private fun saveCurrentRound(round: GameRound) {
        prefs.edit()
            .putInt("round_number", round.roundNumber)
            .putInt("ice_breakers_played", round.iceBreakersPlayed)
            .putInt("deep_played", round.deepPlayed)
            .putInt("deeper_played", round.deeperPlayed)
            .apply()
    }

    fun addUsedQuestion(category: String, questionIndex: Int) {
        when (category) {
            "ice_breaker" -> {
                val current = _usedIceBreakers.value.toMutableSet()
                current.add(questionIndex)
                _usedIceBreakers.value = current
                saveUsedQuestions("used_ice_breakers", current)
            }
            "deep" -> {
                val current = _usedDeep.value.toMutableSet()
                current.add(questionIndex)
                _usedDeep.value = current
                saveUsedQuestions("used_deep", current)
            }
            "deeper" -> {
                val current = _usedDeeper.value.toMutableSet()
                current.add(questionIndex)
                _usedDeeper.value = current
                saveUsedQuestions("used_deeper", current)
            }
        }
    }

    fun updateRound(round: GameRound) {
        _currentRound.value = round
        saveCurrentRound(round)
    }

    fun getAvailableQuestions(category: String): List<Int> {
        val allQuestions = when (category) {
            "ice_breaker" -> Questions.iceBreaker
            "deep" -> Questions.deep
            "deeper" -> Questions.deeper
            else -> emptyList()
        }

        val usedQuestions = when (category) {
            "ice_breaker" -> _usedIceBreakers.value
            "deep" -> _usedDeep.value
            "deeper" -> _usedDeeper.value
            else -> emptySet()
        }

        return allQuestions.indices.filter { it !in usedQuestions }
    }

    fun getUsedQuestionsCount(category: String): Int {
        return when (category) {
            "ice_breaker" -> _usedIceBreakers.value.size
            "deep" -> _usedDeep.value.size
            "deeper" -> _usedDeeper.value.size
            else -> 0
        }
    }

    fun getTotalQuestionsCount(category: String): Int {
        return when (category) {
            "ice_breaker" -> Questions.iceBreaker.size
            "deep" -> Questions.deep.size
            "deeper" -> Questions.deeper.size
            else -> 0
        }
    }

    // Novo método para obter os índices das perguntas usadas
    fun getUsedQuestions(category: String): List<Int> {
        return when (category) {
            "ice_breaker" -> _usedIceBreakers.value.sorted()
            "deep" -> _usedDeep.value.sorted()
            "deeper" -> _usedDeeper.value.sorted()
            else -> emptyList()
        }
    }

    // Método para obter todas as perguntas usadas organizadas por categoria
    fun getAllUsedQuestions(): Map<String, List<String>> {
        val result = mutableMapOf<String, List<String>>()

        val usedIceBreakers = _usedIceBreakers.value.sorted().map { Questions.iceBreaker[it] }
        if (usedIceBreakers.isNotEmpty()) {
            result["ice_breaker"] = usedIceBreakers
        }

        val usedDeep = _usedDeep.value.sorted().map { Questions.deep[it] }
        if (usedDeep.isNotEmpty()) {
            result["deep"] = usedDeep
        }

        val usedDeeper = _usedDeeper.value.sorted().map { Questions.deeper[it] }
        if (usedDeeper.isNotEmpty()) {
            result["deeper"] = usedDeeper
        }

        return result
    }

    fun resetGame() {
        // Limpa todos os dados salvos
        prefs.edit().clear().apply()

        // Reseta os StateFlows
        _usedIceBreakers.value = emptySet()
        _usedDeep.value = emptySet()
        _usedDeeper.value = emptySet()
        _currentRound.value = GameRound(roundNumber = 1)
    }

    private fun loadGameState() {
        _usedIceBreakers.value = loadUsedQuestions("used_ice_breakers")
        _usedDeep.value = loadUsedQuestions("used_deep")
        _usedDeeper.value = loadUsedQuestions("used_deeper")
        _currentRound.value = loadCurrentRound()
    }

    // Método para verificar se existe um jogo salvo
    fun hasGameInProgress(): Boolean {
        return prefs.getInt("round_number", 1) > 1 ||
                prefs.getInt("ice_breakers_played", 0) > 0 ||
                prefs.getInt("deep_played", 0) > 0 ||
                prefs.getInt("deeper_played", 0) > 0 ||
                _usedIceBreakers.value.isNotEmpty() ||
                _usedDeep.value.isNotEmpty() ||
                _usedDeeper.value.isNotEmpty()
    }

    // Método para obter resumo do progresso
    fun getGameProgress(): String {
        val round = _currentRound.value
        val totalUsed = _usedIceBreakers.value.size + _usedDeep.value.size + _usedDeeper.value.size
        return "Round ${round.roundNumber} • $totalUsed perguntas jogadas"
    }

    // Método para obter estatísticas detalhadas
    fun getDetailedStats(): Map<String, Any> {
        val totalUsed = _usedIceBreakers.value.size + _usedDeep.value.size + _usedDeeper.value.size
        val totalQuestions = Questions.iceBreaker.size + Questions.deep.size + Questions.deeper.size
        val percentage = if (totalQuestions > 0) (totalUsed * 100) / totalQuestions else 0

        return mapOf(
            "totalUsed" to totalUsed,
            "totalQuestions" to totalQuestions,
            "percentage" to percentage,
            "currentRound" to _currentRound.value.roundNumber,
            "iceBreakersUsed" to _usedIceBreakers.value.size,
            "deepUsed" to _usedDeep.value.size,
            "deeperUsed" to _usedDeeper.value.size
        )
    }

    // Método para verificar se uma pergunta específica já foi usada
    fun isQuestionUsed(category: String, questionIndex: Int): Boolean {
        return when (category) {
            "ice_breaker" -> questionIndex in _usedIceBreakers.value
            "deep" -> questionIndex in _usedDeep.value
            "deeper" -> questionIndex in _usedDeeper.value
            else -> false
        }
    }

    // Método para exportar dados (útil para backup/debug)
    fun exportGameData(): Map<String, Any> {
        return mapOf(
            "usedIceBreakers" to _usedIceBreakers.value.toList(),
            "usedDeep" to _usedDeep.value.toList(),
            "usedDeeper" to _usedDeeper.value.toList(),
            "currentRound" to mapOf(
                "roundNumber" to _currentRound.value.roundNumber,
                "iceBreakersPlayed" to _currentRound.value.iceBreakersPlayed,
                "deepPlayed" to _currentRound.value.deepPlayed,
                "deeperPlayed" to _currentRound.value.deeperPlayed
            )
        )
    }
}