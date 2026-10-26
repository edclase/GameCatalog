package esq.dam

import java.nio.file.Files
import java.nio.file.Path

// Todas las rutas del proyecto están aquí, para no repetirlas por el código
class Directories {
    val datosPath: Path = Path.of("proyecto/datos")
    val datosIni: Path = datosPath.resolve("datos_ini")
    val datosFin: Path = datosPath.resolve("datos_fin")

    val csvFile = "videojuegos.csv"
    val jsonFile = "videojuegos.json"
    val xmlFile = "videojuegos.xml"
    val binFile = "videogames.bin"
    fun reviewDirectoryStructure() {
        println("PROYECTO DE CREACION DE RUTAS")
        createIfMissing(datosIni)
        createIfMissing(datosFin)
    }

    private fun createIfMissing(dir: Path) {
        if (Files.isDirectory(dir)) {
            println("${dir.fileName} ya existe")
        } else {
            Files.createDirectories(dir)
            println("Directorio ${dir.fileName} creado")
        }
    }

    fun printTree() {
        try {
            Files.walk(datosPath).use { stream ->
                stream.sorted().forEach { path ->
                    val prof = path.nameCount - datosPath.nameCount
                    val indentation = "\t".repeat(prof)
                    val prefix = if (Files.isDirectory(path)) "Directorio" else "Archivo"
                    if (prof > 0) {
                        println("$indentation$prefix ${path.fileName}")
                    }
                }
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }
}
