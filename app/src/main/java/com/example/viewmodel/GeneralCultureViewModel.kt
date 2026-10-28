package com.example.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CultureFact(val title: String, val content: String)
data class CultureQuestion(val text: String, val options: List<String>, val correctOptionIndex: Int)
enum class CultureScreenState { FACTS, QUIZ, RESULTS }

data class GeneralCultureState(
    val screenState: CultureScreenState = CultureScreenState.FACTS,
    val currentQuestionIndex: Int = 0,
    val score: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswerChecked: Boolean = false
)

class GeneralCultureViewModel : ViewModel() {
    private val _state = MutableStateFlow(GeneralCultureState())
    val state: StateFlow<GeneralCultureState> = _state.asStateFlow()

    val facts = listOf(
        CultureFact("El origen de la manzana", "Steve Jobs y Steve Wozniak fundaron Apple Computer en 1976 en el garaje de la casa de los padres de Jobs. Eligieron el nombre 'Apple' porque Jobs venía de trabajar en un huerto de manzanas y le parecía un nombre 'divertido, animado y nada intimidante'."),
        CultureFact("La Torre de Hierro", "La Torre Eiffel fue construida por el ingeniero Gustave Eiffel para la Exposición Universal de París de 1889. Inicialmente, muchos artistas parisinos la odiaban y querían derribarla, pero hoy es el mayor símbolo de Francia."),
        CultureFact("La sonrisa enigmática", "Leonardo da Vinci, un polímata del Renacimiento, pintó La Mona Lisa a principios del siglo XVI. Su revolucionaria técnica del 'sfumato' es lo que le da a la pintura esa expresión tan ambigua y famosa."),
        CultureFact("Pionera de la ciencia", "Marie Curie fue la primera mujer en ganar un Premio Nobel y la única persona en la historia en ganar este premio en dos disciplinas científicas distintas: Física y Química, gracias a sus revolucionarios trabajos con la radiactividad."),
        CultureFact("El coloso de agua", "El río Amazonas es el río más caudaloso del mundo. Contiene más agua que el Nilo, el Yangtsé y el Misisipi juntos, y atraviesa gran parte de Sudamérica albergando la mayor biodiversidad del planeta.")
    )

    val questions = listOf(
        CultureQuestion(
            text = "¿Quiénes fueron los principales creadores de la famosa marca tecnológica representada por una fruta mordida?",
            options = listOf("Steve Jobs y Steve Wozniak", "Bill Gates y Paul Allen", "Mark Zuckerberg y Eduardo Saverin", "Larry Page y Sergey Brin"),
            correctOptionIndex = 0
        ),
        CultureQuestion(
            text = "¿Qué ingeniero fue el responsable de la enorme estructura de celosía de hierro que sirvió de entrada monumental para la Exposición Universal de 1889 en la capital francesa?",
            options = listOf("Antoni Gaudí", "Gustave Eiffel", "Frank Lloyd Wright", "Le Corbusier"),
            correctOptionIndex = 1
        ),
        CultureQuestion(
            text = "¿Qué genio del Renacimiento utilizó la técnica del 'sfumato' para pintar el retrato de la mujer con la sonrisa más famosa y enigmática del mundo?",
            options = listOf("Michelangelo", "Raphael", "Leonardo da Vinci", "Donatello"),
            correctOptionIndex = 2
        ),
        CultureQuestion(
            text = "¿Qué investigadora logró la hazaña histórica de ser galardonada con el Premio Nobel en dos disciplinas científicas diferentes tras sus peligrosos estudios sobre elementos radiactivos?",
            options = listOf("Rosalind Franklin", "Ada Lovelace", "Lise Meitner", "Marie Curie"),
            correctOptionIndex = 3
        ),
        CultureQuestion(
            text = "Si navegas por la corriente de agua dulce que descarga la mayor cantidad de volumen en el océano, rodeado de la selva tropical más extensa, ¿en qué río estás?",
            options = listOf("Río Amazonas", "Río Nilo", "Río Yangtsé", "Río Misisipi"),
            correctOptionIndex = 0
        )
    )

    fun startQuiz() {
        _state.update { GeneralCultureState(screenState = CultureScreenState.QUIZ) }
    }

    fun selectOption(index: Int) {
        if (!_state.value.isAnswerChecked) {
            _state.update { it.copy(selectedOptionIndex = index) }
        }
    }

    fun checkAnswer() {
        val currentState = _state.value
        if (currentState.selectedOptionIndex != null && !currentState.isAnswerChecked) {
            val isCorrect = currentState.selectedOptionIndex == questions[currentState.currentQuestionIndex].correctOptionIndex
            _state.update {
                it.copy(
                    isAnswerChecked = true,
                    score = if (isCorrect) it.score + 1 else it.score
                )
            }
        }
    }

    fun nextQuestion() {
        val currentState = _state.value
        if (currentState.currentQuestionIndex < questions.size - 1) {
            _state.update {
                it.copy(
                    currentQuestionIndex = it.currentQuestionIndex + 1,
                    selectedOptionIndex = null,
                    isAnswerChecked = false
                )
            }
        } else {
            _state.update { it.copy(screenState = CultureScreenState.RESULTS) }
        }
    }

    fun restart() {
        _state.update { GeneralCultureState() }
    }
}
