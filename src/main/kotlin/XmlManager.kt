package esq.dam

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.nio.file.Files
import java.nio.file.Path

// Lectura y escritura de videojuegos en formato XML (Jackson)
class XmlManager {
    private val mapper = XmlMapper().registerKotlinModule().enable(SerializationFeature.INDENT_OUTPUT)

    fun read(file: Path): List<Videogame> {
        if (!Files.isReadable(file)) {
            println("No se puede leer el fichero $file")
            return emptyList()
        }
        return try {
            val gamesXML = mapper.readValue<VideogamesXML>(file.toFile()).videogames
            gamesXML.map { it.toVideogame() }
        } catch (e: Exception) {
            println("Error al leer el XML: ${e.message}")
            emptyList()
        }
    }

    fun write(file: Path, games: List<Videogame>) {
        val gamesXML = games.map { VideogameXML(it.id, it.title, it.genre, it.year, it.grade) }
        mapper.writeValue(file.toFile(), VideogamesXML(gamesXML))
    }
}
