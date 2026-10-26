package esq.dam

import kotlinx.serialization.Serializable

@Serializable
data class VideogameJSON(
    val id: Int,
    val titulo: String,
    val genero: String,
    val anio: Int,
    val nota: Double
) {
    fun toVideogame(): Videogame = Videogame(id, titulo, genero, anio, nota)
}
