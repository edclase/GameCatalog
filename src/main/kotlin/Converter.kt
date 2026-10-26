package esq.dam

import java.nio.file.Files
import java.nio.file.Path

// Convierte un fichero de un formato a otro, lee con una función read de los managers y escribe con write
class Converter {

    fun convert(origin: Path, destiny: Path,
        read: (Path) -> List<Videogame>, write: (Path, List<Videogame>) -> Unit
    ) {
        if (!Files.exists(origin)) {
            return println("El fichero de origen $origin no existe")
        }
        try {
            val games = read(origin)
            Files.createDirectories(destiny.parent)
            write(destiny, games)
            println("Conversión completada: ${games.size} registros escritos en $destiny")
        } catch (e: Exception) {
            println("Error durante la conversión: ${e.message}")
        }
    }
}
