package com.example.model

data class HistoricalSpeech(
    val id: String,
    val title: String,
    val author: String,
    val context: String,
    val excerpt: String
)

val sampleHistoricalSpeeches = listOf(
    HistoricalSpeech(
        id = "hs1",
        title = "Tengo un sueño (I Have a Dream)",
        author = "Martin Luther King Jr.",
        context = "Pronunciado el 28 de agosto de 1963 en los escalones del Monumento a Lincoln durante la Marcha en Washington por el Trabajo y la Libertad.",
        excerpt = "Yo tengo un sueño que un día esta nación se elevará y vivirá el verdadero significado de su credo: \"Creemos que estas verdades son evidentes: que todos los hombres son creados iguales\".\n\nYo tengo un sueño que mis cuatro hijos pequeños vivirán un día en una nación donde no serán juzgados por el color de su piel sino por el contenido de su carácter. ¡Yo tengo un sueño hoy!"
    ),
    HistoricalSpeech(
        id = "hs2",
        title = "Discurso en Stanford",
        author = "Steve Jobs",
        context = "Pronunciado en 2005 en la Universidad de Stanford. Una reflexión sobre la vida, la pérdida y el descubrimiento del propósito.",
        excerpt = "Su tiempo es limitado, así que no lo desperdicien viviendo la vida de alguien más. No se dejen atrapar por el dogma, que es vivir con los resultados del pensamiento de otras personas. No dejen que el ruido de las opiniones de los demás ahogue su propia voz interior. Y lo más importante, tengan el coraje de seguir a su corazón y su intuición.\n\nManténganse hambrientos. Manténganse alocados."
    ),
    HistoricalSpeech(
        id = "hs3",
        title = "Lucharemos en las playas",
        author = "Winston Churchill",
        context = "Pronunciado el 4 de junio de 1940 ante la Cámara de los Comunes, infundiendo valor a la nación británica durante la Segunda Guerra Mundial.",
        excerpt = "Llegaremos hasta el final, lucharemos en Francia, lucharemos en los mares y océanos, lucharemos con creciente confianza y creciente fuerza en el aire, defenderemos nuestra isla, cualquiera que sea el costo, lucharemos en las playas, lucharemos en las pistas de aterrizaje, lucharemos en los campos y en las calles, lucharemos en las colinas; nunca nos rendiremos."
    ),
    HistoricalSpeech(
        id = "hs4",
        title = "El Deber de la Mujer",
        author = "Eva Perón",
        context = "Fragmento de sus potentes discursos dirigidos a los trabajadores y mujeres, marcando una era en la política argentina.",
        excerpt = "Yo no quise ni quiero nada para mí. Mi gloria es y será siempre el escudo de Perón y la bandera de mi pueblo. Y aunque deje en el camino jirones de mi vida, yo sé que ustedes recogerán mi nombre y lo llevarán como bandera a la victoria."
    )
)
