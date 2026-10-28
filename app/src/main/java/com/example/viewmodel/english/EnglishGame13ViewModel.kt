package com.example.viewmodel.english

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class Story(
    val title: String,
    val content: String,
    val dictionary: Map<String, String>
)

data class EnglishGame13State(
    val stories: List<Story> = emptyList(),
    val currentStoryIndex: Int = 0,
    val selectedWord: String? = null,
    val selectedTranslation: String? = null
)

class EnglishGame13ViewModel : ViewModel() {
    private val _state = MutableStateFlow(EnglishGame13State())
    val state: StateFlow<EnglishGame13State> = _state.asStateFlow()

    init {
        val story1 = Story(
            title = "The Magic Forest",
            content = "Once upon a time, there was a small village near a magic forest. The trees in the forest were very tall and had silver leaves. One day, a young boy named Leo decided to explore the forest. He found a glowing stone on the ground. When he picked it up, he could hear the animals talking! The birds said 'hello' and the rabbits offered him some carrots. It was the best day of his life.",
            dictionary = mapOf(
                "once" to "una vez", "upon" to "sobre", "time" to "tiempo", "there" to "allí", "was" to "había / era",
                "small" to "pequeño", "village" to "pueblo / aldea", "near" to "cerca de", "magic" to "mágico",
                "forest" to "bosque", "trees" to "árboles", "were" to "eran / estaban", "very" to "muy", "tall" to "altos",
                "and" to "y", "had" to "tenían", "silver" to "plateadas (plata)", "leaves" to "hojas", "one" to "un",
                "day" to "día", "young" to "joven", "boy" to "niño / chico", "named" to "llamado", "decided" to "decidió",
                "to" to "a / para", "explore" to "explorar", "the" to "el / la", "he" to "él", "found" to "encontró",
                "a" to "un / una", "glowing" to "brillante", "stone" to "piedra", "on" to "en / sobre", "ground" to "suelo",
                "when" to "cuando", "picked" to "recogió", "it" to "lo", "up" to "arriba", "could" to "podía",
                "hear" to "escuchar", "animals" to "animales", "talking" to "hablando", "birds" to "pájaros",
                "said" to "dijeron", "hello" to "hola", "rabbits" to "conejos", "offered" to "ofrecieron",
                "him" to "a él", "some" to "algunas", "carrots" to "zanahorias", "best" to "mejor", "life" to "vida",
                "of" to "de", "his" to "su"
            )
        )
        val story2 = Story(
            title = "The Space Adventure",
            content = "Captain Maya steered her spaceship through the dark galaxy. She was looking for a new planet. Suddenly, a bright light appeared on the radar. It was a beautiful green planet with blue rings. She landed the ship safely. Strange but friendly aliens came to greet her. They shared their food and taught her a new song.",
            dictionary = mapOf(
                "captain" to "capitana", "steered" to "pilotó / dirigió", "her" to "su (de ella)",
                "spaceship" to "nave espacial", "through" to "a través de", "the" to "el / la", "dark" to "oscura",
                "galaxy" to "galaxia", "she" to "ella", "was" to "estaba / era", "looking" to "buscando",
                "for" to "por / para", "a" to "un / una", "new" to "nuevo", "planet" to "planeta",
                "suddenly" to "de repente", "bright" to "brillante", "light" to "luz", "appeared" to "apareció",
                "on" to "en", "radar" to "radar", "it" to "eso", "beautiful" to "hermoso", "green" to "verde",
                "with" to "con", "blue" to "azules", "rings" to "anillos", "landed" to "aterrizó", "ship" to "nave",
                "safely" to "con seguridad", "strange" to "extraños", "but" to "pero", "friendly" to "amigables",
                "aliens" to "extraterrestres", "came" to "vinieron", "to" to "a / para", "greet" to "saludar",
                "they" to "ellos", "shared" to "compartieron", "their" to "su (de ellos)", "food" to "comida",
                "and" to "y", "taught" to "enseñaron", "song" to "canción"
            )
        )
        _state.value = EnglishGame13State(
            stories = listOf(story1, story2)
        )
    }

    fun onWordClicked(word: String) {
        val cleanWord = word.lowercase().replace(Regex("[^a-z]"), "")
        if (cleanWord.isEmpty()) return
        
        val currentStory = _state.value.stories[_state.value.currentStoryIndex]
        val translation = currentStory.dictionary[cleanWord] ?: "Traducción no disponible"
        
        _state.update { 
            it.copy(
                selectedWord = cleanWord,
                selectedTranslation = translation
            ) 
        }
    }

    fun nextStory() {
        _state.update {
            val nextIndex = (it.currentStoryIndex + 1) % it.stories.size
            it.copy(
                currentStoryIndex = nextIndex,
                selectedWord = null,
                selectedTranslation = null
            )
        }
    }

    fun previousStory() {
        _state.update {
            val prevIndex = if (it.currentStoryIndex > 0) it.currentStoryIndex - 1 else it.stories.size - 1
            it.copy(
                currentStoryIndex = prevIndex,
                selectedWord = null,
                selectedTranslation = null
            )
        }
    }
}
