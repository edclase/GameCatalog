package esq.dam

data class VideogameCSV(
    val id: Int,
    val titulo: String,
    val genero: String,
    val anio: Int,
    val nota: Double
) {
    fun toVideogame(): Videogame = Videogame(id, titulo, genero, anio, nota)

    fun toRow(): List<String> = listOf(id.toString(), titulo, genero, anio.toString(), nota.toString())
}
