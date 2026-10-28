package com.example.model

data class Poem(
    val id: String,
    val title: String,
    val author: String,
    val content: String
)

val samplePoems = listOf(
    Poem(
        id = "p1",
        title = "Rima LIII (Volverán las oscuras golondrinas)",
        author = "Gustavo Adolfo Bécquer",
        content = """
            Volverán las oscuras golondrinas
            en tu balcón sus nidos a colgar,
            y otra vez con el ala a sus cristales
            jugando llamarán.
            Pero aquellas que el vuelo refrenaban
            tu hermosura y mi dicha a contemplar,
            aquellas que aprendieron nuestros nombres...
            ¡esas... no volverán!
        """.trimIndent()
    ),
    Poem(
        id = "p2",
        title = "Poema 20",
        author = "Pablo Neruda",
        content = """
            Puedo escribir los versos más tristes esta noche.
            Escribir, por ejemplo: "La noche está estrellada,
            y tiritan, azules, los astros, a lo lejos."
            El viento de la noche gira en el cielo y canta.
            Puedo escribir los versos más tristes esta noche.
            Yo la quise, y a veces ella también me quiso.
        """.trimIndent()
    ),
    Poem(
        id = "p3",
        title = "Cultivo una rosa blanca",
        author = "José Martí",
        content = """
            Cultivo una rosa blanca
            en junio como en enero
            para el amigo sincero
            que me da su mano franca.
            
            Y para el cruel que me arranca
            el corazón con que vivo,
            cardo ni ortiga cultivo;
            cultivo la rosa blanca.
        """.trimIndent()
    ),
    Poem(
        id = "p4",
        title = "Caminante, no hay camino",
        author = "Antonio Machado",
        content = """
            Caminante, son tus huellas
            el camino y nada más;
            Caminante, no hay camino,
            se hace camino al andar.
            Al andar se hace el camino,
            y al volver la vista atrás
            se ve la senda que nunca
            se ha de volver a pisar.
        """.trimIndent()
    )
)
