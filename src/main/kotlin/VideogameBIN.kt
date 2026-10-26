package esq.dam

data class VideogameBIN(
    val id: Long,
    val titulo: String,
    val genero: String,
    val anio: Int,
    val nota: Double)
{
    constructor() : this(0, "", "", 0, 0.0)
    fun toVideogame(): Videogame = Videogame(id.toInt(), titulo, genero, anio, nota)
}

