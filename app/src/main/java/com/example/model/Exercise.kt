package com.example.model

data class Exercise(
    val id: String,
    val title: String,
    val description: String,
    val icon: String
)

val sampleExercises = listOf(
    Exercise("1", "Elevator Pitch", "Convence a un inversor en menos de 1 minuto.", "lightbulb"),
    Exercise("2", "Presentación Personal", "Preséntate en una nueva empresa o evento de networking.", "person"),
    Exercise("3", "Discurso Motivacional", "Inspira a tu equipo antes de un gran lanzamiento.", "star"),
    Exercise("4", "Manejo de Crisis", "Explica un error crítico a tus clientes con empatía.", "warning")
)
