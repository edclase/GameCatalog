package esq.dam

data class Videogame(
    val id: Int,
    val title: String,
    val genre: String,
    val year: Int,
    val grade: Double
) {
    constructor() : this(0, "", "", 0, 0.0)
}