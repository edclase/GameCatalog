package esq.dam

import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path

// Lectura y escritura de videojuegos en formato JSON (kotlinx.serialization)
class JsonManager {
    private val json = Json { prettyPrint = true }

    fun read(file: Path): List<Videogame> {
        if (!Files.isReadable(file)) {
            println("No se puede leer el fichero $file")
            return emptyList()
        }
        return try {
            val gamesJSON = json.decodeFromString<List<VideogameJSON>>(Files.readString(file))
            gamesJSON.map { it.toVideogame() }
        } catch (e: Exception) {
            println("Error al leer el JSON: ${e.message}")
            emptyList()
        }
    }

    fun write(file: Path, games: List<Videogame>) {
        val gamesJSON = games.map { VideogameJSON(it.id, it.title, it.genre, it.year, it.grade) }
        Files.writeString(file, json.encodeToString(gamesJSON))
    }
}
