package com.example.viewmodel.english

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EnglishCategoryViewModel : ViewModel() {
    private val _state = MutableStateFlow(EnglishGame12State())
    val state: StateFlow<EnglishGame12State> = _state.asStateFlow()

    fun loadCategory(categoryId: String) {
        val words = when (categoryId) {
            "basics" -> listOf(
                Flashcard("Hello", "Hola", "həˈloʊ"),
                Flashcard("Thank you", "Gracias", "θæŋk juː"),
                Flashcard("Please", "Por favor", "pliz"),
                Flashcard("Yes", "Sí", "jɛs"),
                Flashcard("No", "No", "noʊ"),
                Flashcard("Excuse me", "Disculpe", "ɪkˈskjus mi"),
                Flashcard("Sorry", "Perdón", "ˈsɑri")
            )
            "food" -> listOf(
                Flashcard("Apple", "Manzana", "æpl"),
                Flashcard("Bread", "Pan", "brɛd"),
                Flashcard("Water", "Agua", "wɔːtər"),
                Flashcard("Coffee", "Café", "ˈkɔfi"),
                Flashcard("Chicken", "Pollo", "ˈtʃɪkɪn"),
                Flashcard("Cheese", "Queso", "tʃiz"),
                Flashcard("Restaurant", "Restaurante", "ˈrɛstərənt")
            )
            "travel" -> listOf(
                Flashcard("Airport", "Aeropuerto", "ˈɛrpɔrt"),
                Flashcard("Hotel", "Hotel", "hoʊˈtɛl"),
                Flashcard("Ticket", "Boleto", "ˈtɪkɪt"),
                Flashcard("Luggage", "Equipaje", "ˈlʌɡɪdʒ"),
                Flashcard("Passport", "Pasaporte", "ˈpæspɔrt"),
                Flashcard("Train", "Tren", "treɪn"),
                Flashcard("Map", "Mapa", "mæp")
            )
            "home" -> listOf(
                Flashcard("House", "Casa", "haʊs"),
                Flashcard("Room", "Habitación", "rum"),
                Flashcard("Bed", "Cama", "bɛd"),
                Flashcard("Kitchen", "Cocina", "ˈkɪtʃɪn"),
                Flashcard("Bathroom", "Baño", "ˈbæθrum"),
                Flashcard("Window", "Ventana", "ˈwɪndoʊ"),
                Flashcard("Door", "Puerta", "dɔr")
            )
            "work" -> listOf(
                Flashcard("Office", "Oficina", "ˈɔfɪs"),
                Flashcard("Meeting", "Reunión", "ˈmitɪŋ"),
                Flashcard("Job", "Trabajo", "dʒɑb"),
                Flashcard("Boss", "Jefe", "bɔs"),
                Flashcard("Computer", "Computadora", "kəmˈpjutər"),
                Flashcard("Email", "Correo", "ˈimeɪl"),
                Flashcard("Money", "Dinero", "ˈmʌni")
            )
            "health" -> listOf(
                Flashcard("Hospital", "Hospital", "ˈhɑspɪtl"),
                Flashcard("Doctor", "Doctor", "ˈdɑktər"),
                Flashcard("Pain", "Dolor", "peɪn"),
                Flashcard("Medicine", "Medicina", "ˈmɛdɪsɪn"),
                Flashcard("Pharmacy", "Farmacia", "ˈfɑrməsi"),
                Flashcard("Help", "Ayuda", "hɛlp"),
                Flashcard("Emergency", "Emergencia", "ɪˈmɜrdʒənsi")
            )
            else -> emptyList()
        }
        _state.value = EnglishGame12State(flashcards = words)
    }

    fun flipCard() {
        _state.update { it.copy(isShowingTranslation = !it.isShowingTranslation) }
    }

    fun nextCard() {
        _state.update { 
            if (it.currentIndex + 1 < it.flashcards.size) {
                it.copy(currentIndex = it.currentIndex + 1, isShowingTranslation = false)
            } else {
                it
            }
        }
    }

    fun previousCard() {
        _state.update { 
            if (it.currentIndex > 0) {
                it.copy(currentIndex = it.currentIndex - 1, isShowingTranslation = false)
            } else {
                it
            }
        }
    }
}
