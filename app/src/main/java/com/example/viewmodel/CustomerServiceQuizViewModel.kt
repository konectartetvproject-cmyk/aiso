package com.example.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CSQuizQuestion(
    val situation: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String
)

data class CSQuizState(
    val questions: List<CSQuizQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val showExplanation: Boolean = false,
    val isFinished: Boolean = false,
    val score: Int = 0
)

class CustomerServiceQuizViewModel : ViewModel() {
    private val _state = MutableStateFlow(CSQuizState())
    val state: StateFlow<CSQuizState> = _state.asStateFlow()

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        val defaultQuestions = listOf(
            CSQuizQuestion(
                situation = "Un cliente está muy enojado porque su pedido llegó con retraso de una semana.",
                options = listOf(
                    "Cálmese, no es mi culpa que la paquetería sea lenta.",
                    "Entiendo su molestia. Pido una disculpa por el retraso, vamos a revisar el estado de su pedido de inmediato.",
                    "Pues así pasa a veces, ¿quiere que le cancele el pedido?",
                    "No se preocupe, seguro llega mañana."
                ),
                correctOptionIndex = 1,
                explanation = "Es importante mostrar empatía, asumir la responsabilidad en nombre de la empresa y ofrecer una solución inmediata."
            ),
            CSQuizQuestion(
                situation = "El cliente pregunta por una característica que tu producto NO tiene.",
                options = listOf(
                    "Sí, claro que lo tiene, no se preocupe.",
                    "No tenemos eso. Busque en otra parte.",
                    "Actualmente no contamos con esa característica, pero nuestro producto ofrece beneficios que podrían ayudarle de esta manera...",
                    "Déjeme revisar... no, no lo tiene."
                ),
                correctOptionIndex = 2,
                explanation = "Sé honesto, pero enfócate en los beneficios que sí ofrece tu producto para mantener el interés del cliente."
            ),
            CSQuizQuestion(
                situation = "Un cliente te contacta porque no sabe cómo instalar el producto que acaba de comprar.",
                options = listOf(
                    "Viene en el manual, léalo por favor.",
                    "¡Claro! Con gusto le ayudo. ¿En qué paso de la instalación se encuentra atascado?",
                    "Es muy fácil, cualquier persona puede hacerlo.",
                    "Debe contratar a un técnico, nosotros no hacemos eso."
                ),
                correctOptionIndex = 1,
                explanation = "Ofrece ayuda proactiva y amigable, guiando al cliente paso a paso."
            ),
            CSQuizQuestion(
                situation = "El cliente pide un descuento adicional porque ha comprado mucho en el pasado, pero tu empresa no lo permite.",
                options = listOf(
                    "No. Las reglas son las reglas.",
                    "Me encantaría ofrecerle un descuento, pero actualmente no tenemos promociones. Sin embargo, valoro su lealtad y le puedo ofrecer un beneficio alternativo.",
                    "Déjeme hablar con mi jefe a ver si él quiere.",
                    "Usted ya tiene buen precio, no pida más."
                ),
                correctOptionIndex = 1,
                explanation = "Mantén la política de la empresa, pero muestra aprecio por el cliente y ofrece alternativas viables si es posible."
            )
        )
        
        _state.update { 
            it.copy(
                questions = defaultQuestions.shuffled(),
                currentQuestionIndex = 0,
                selectedOptionIndex = null,
                showExplanation = false,
                isFinished = false,
                score = 0
            ) 
        }
    }

    fun selectOption(index: Int) {
        if (_state.value.showExplanation) return
        _state.update { it.copy(selectedOptionIndex = index) }
    }

    fun checkAnswer() {
        val currentState = _state.value
        if (currentState.selectedOptionIndex == null) return
        
        val isCorrect = currentState.selectedOptionIndex == currentState.questions[currentState.currentQuestionIndex].correctOptionIndex
        
        _state.update { 
            it.copy(
                showExplanation = true,
                score = if (isCorrect) it.score + 1 else it.score
            ) 
        }
    }

    fun nextQuestion() {
        val currentState = _state.value
        val nextIndex = currentState.currentQuestionIndex + 1
        
        if (nextIndex < currentState.questions.size) {
            _state.update { 
                it.copy(
                    currentQuestionIndex = nextIndex,
                    selectedOptionIndex = null,
                    showExplanation = false
                ) 
            }
        } else {
            _state.update { it.copy(isFinished = true) }
        }
    }

    fun restartQuiz() {
        loadQuestions()
    }
}
