package esq.dam

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.nio.file.Files
import java.nio.file.Path

class CsvManager {

    fun read(file: Path): List<Videogame> {
        if (!Files.isReadable(file)) {
            println("No se puede leer el fichero $file")
            return emptyList()
        }
        val lines: List<List<String>> = csvReader { delimiter = ';' }.readAll(file.toFile())
        val gamesCSV = lines.mapNotNull { column ->
            try {
                VideogameCSV(column[0].toInt(), column[1], column[2], column[3].toInt(), column[4].toDouble())
            } catch (e: Exception) {
                println("Línea CSV no válida, se omite: $column")
                null
            }
        }
        return gamesCSV.map { it.toVideogame() }
    }

    fun write(file: Path, games: List<Videogame>) {
        val gamesCSV = games.map { VideogameCSV(it.id, it.title, it.genre, it.year, it.grade) }
        val rows = gamesCSV.map { it.toRow() }
        csvWriter { delimiter = ';' }.writeAll(rows, file.toFile())
    }
}
