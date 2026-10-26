package esq.dam

import java.nio.file.Path

// Todo lo que interactúa con el usuario por consola
class Menu(private val repo: VideogameRepository, private val directories: Directories) {
    private val csvManager = CsvManager()
    private val jsonManager = JsonManager()
    private val xmlManager = XmlManager()
    private val binPath = directories.datosIni.resolve(directories.binFile)
    private val binManager = BinManager(binPath)
    private val converter = Converter()

    fun showMainMenu() {
        while (true) {
            println("===== CATÁLOGO DE VIDEOJUEGOS =====")
            println("1. Gestión CSV")
            println("2. Importar catálogo desde XML")
            println("3. Importar catálogo desde JSON")
            println("4. Conversión entre formatos")
            println("5. Gestión fichero BIN")
            println("0. Salir")
            println("====================================")
            when (readInt()) {
                1 -> csvMenu()
                2 -> importFrom(directories.datosIni.resolve(directories.xmlFile), xmlManager::read)
                3 -> importFrom(directories.datosIni.resolve(directories.jsonFile), jsonManager::read)
                4 -> converterMenu()
                5 -> binMenu()
                0 -> return
                else -> println("Opción no válida")
            }
        }
    }

    // ---------- Gestión CSV (CRUD) ----------

    private fun csvMenu() {
        while (true) {
            println("--- Gestión CSV ---")
            println("1. Listar todos los videojuegos")
            println("2. Buscar videojuego por ID")
            println("3. Añadir nuevo videojuego")
            println("4. Modificar nota de un videojuego")
            println("5. Eliminar videojuego")
            println("0. Volver al menú principal")
            when (readInt()) {
                1 -> listGames()
                2 -> searchGameById()
                3 -> addGame()
                4 -> modGameGrade()
                5 -> delGameById()
                0 -> return
                else -> println("Opción no válida")
            }
        }
    }
    private fun listGames() {
        if (repo.games.isEmpty()) println("No hay videojuegos")
        repo.games.forEach { println(it) }
    }

    private fun searchGameById() {
        println("Introduce la ID: ")
        val id = readInt() ?: return println("ID no válida")
        println(repo.findById(id) ?: "No existe ningún videojuego con ID $id")
    }

    private fun addGame() {
        println("Introduce título: ")
        val title = readlnOrNull()?.trim().orEmpty()
        println("Introduce género: ")
        val genre = readlnOrNull()?.trim().orEmpty()
        if (title.isEmpty() || genre.isEmpty()) return println("Título y género no pueden estar vacíos")
        println("Introduce el año:")
        val year = readInt() ?: return println("Año no válido")
        println("Introduce nota (0.0 - 10.0): ")
        val grade = readGrade() ?: return println("Nota no válida")

        val game = repo.add(title, genre, year, grade)
        println("Videojuego añadido con ID ${game.id}")
    }

    private fun modGameGrade() {
        listGames()
        println("Introduce la ID del juego a modificar: ")
        val id = readInt() ?: return println("ID no válida")
        if (repo.findById(id) == null) return println("El juego no existe")
        println("Introduce nueva nota (0.0 - 10.0): ")
        val grade = readGrade() ?: return println("Nota no válida")

        val game = repo.updateGrade(id, grade)
        println("Juego ${game?.title} actualizado")
    }

    private fun delGameById() {
        listGames()
        println("Introduce la ID del juego a borrar: ")
        val id = readInt() ?: return println("ID no válida")
        val game = repo.findById(id) ?: return println("El juego no existe")
        println("¿Seguro que quieres borrar ${game.title}? (s/n)")
        if (readlnOrNull()?.trim()?.lowercase() != "s") return println("Borrado cancelado")

        repo.delete(game)
        println("Juego ${game.title} borrado")
    }

    // ---------- Gestión BIN (CRUD) ----------

    private fun binMenu() {
        while (true) {
            println("===== CRUD fichero BIN =====")
            println("1. Importar juegos del CSV (reemplaza el BIN)")
            println("2. Leer información del fichero binario")
            println("3. Añadir un registro nuevo")
            println("4. Modificar un registro existente (por ID)")
            println("5. Eliminar un registro existente (por ID)")
            println("0. Volver")
            when (readInt()) {
                1 -> importBinGames()
                2 -> showBinGames()
                3 -> addBinGameFromMenu()
                4 -> modifyBinGameById()
                5 -> deleteBinGameById()
                0 -> return
                else -> println("Opción no válida")
            }
        }
    }

    private fun importBinGames() {
        try {
            binManager.write(binPath, repo.games)
            println("${repo.games.size} videojuegos escritos en $binPath")
        } catch (e: Exception) {
            println("Error al importar videojuegos al BIN: ${e.message}")
        }
    }

    private fun showBinGames() {
        try {
            val games = binManager.readBinGames()
            if (games.isEmpty()) return println("No hay videojuegos en el fichero BIN")
            games.forEach { println(it) }
        } catch (e: Exception) {
            println("Error al leer el BIN: ${e.message}")
        }
    }

    private fun addBinGameFromMenu() {
        val title = readBinText("título", TITLE_SIZE) ?: return
        val genre = readBinText("género", GENRE_SIZE) ?: return
        println("Introduce el año:")
        val year = readInt() ?: return println("Año no válido")
        println("Introduce nota (0.0 - 10.0):")
        val grade = readGrade() ?: return println("Nota no válida")

        try {
            val maxId = binManager.readBinGames().maxOfOrNull { it.id } ?: 0L
            if (maxId >= Int.MAX_VALUE) return println("No quedan ID disponibles para el BIN")
            val id = maxId + 1L
            binManager.addBinGame(VideogameBIN(id, title, genre, year, grade))
            println("Videojuego añadido al BIN con ID $id")
        } catch (e: Exception) {
            println("Error al añadir el videojuego al BIN: ${e.message}")
        }
    }

    private fun modifyBinGameById() {
        println("Introduce el ID del videojuego a modificar:")
        val id = readInt() ?: return println("ID no válido")
        try {
            val game = binManager.getBinGameById(id)
                ?: return println("No existe ningún videojuego en el BIN con ID $id")
            println("Juego actual: $game")
            println("1. Título")
            println("2. Género")
            println("3. Año")
            println("4. Nota")
            println("0. Cancelar")
            val updated = when (readInt()) {
                1 -> game.copy(titulo = readBinText("nuevo título", TITLE_SIZE) ?: return)
                2 -> game.copy(genero = readBinText("nuevo género", GENRE_SIZE) ?: return)
                3 -> {
                    println("Introduce el nuevo año:")
                    game.copy(anio = readInt() ?: return println("Año no válido"))
                }
                4 -> {
                    println("Introduce la nueva nota (0.0 - 10.0):")
                    game.copy(nota = readGrade() ?: return println("Nota no válida"))
                }
                0 -> return println("Modificación cancelada")
                else -> return println("Opción no válida")
            }
            if (binManager.modBinGame(updated)) println("Videojuego $id actualizado en el BIN")
            else println("El videojuego $id ya no existe en el BIN")
        } catch (e: Exception) {
            println("Error al modificar el BIN: ${e.message}")
        }
    }

    private fun deleteBinGameById() {
        println("Introduce el ID del videojuego a eliminar:")
        val id = readInt() ?: return println("ID no válido")
        try {
            val game = binManager.getBinGameById(id)
                ?: return println("No existe ningún videojuego en el BIN con ID $id")
            println("¿Seguro que quieres borrar '${game.titulo}' del BIN? (s/n)")
            if (readlnOrNull()?.trim()?.lowercase() != "s") return println("Borrado cancelado")
            if (binManager.delBinGameById(id)) println("Videojuego $id eliminado del BIN")
            else println("El videojuego $id ya no existe en el BIN")
        } catch (e: Exception) {
            println("Error al eliminar del BIN: ${e.message}")
        }
    }

    private fun readBinText(field: String, maxBytes: Int): String? {
        println("Introduce $field:")
        val value = readlnOrNull()?.trim().orEmpty()
        if (value.isEmpty()) {
            println("El campo $field no puede estar vacío")
            return null
        }
        if (value.toByteArray(Charsets.ISO_8859_1).size > maxBytes) {
            println("El campo $field supera el máximo de $maxBytes bytes")
            return null
        }
        return value
    }

    // ---------- Importar desde XML / JSON ----------

    private fun importFrom(file: Path, read: (Path) -> List<Videogame>) {
        val games = read(file)
        if (games.isEmpty()) return println("No se han leído videojuegos")
        println("Videojuegos leídos de ${file.fileName}:")
        games.forEach { println(it) }

        val added = repo.import(games)
        println("$added videojuegos nuevos integrados (${games.size - added} omitidos por ID duplicado)")
    }

    // ---------- Conversor ----------

    private fun converterMenu() {
        while (true) {
            println("===== CONVERSOR DE FICHEROS =====")
            println("1. XML ->")
            println("2. CSV ->")
            println("3. JSON ->")
            println("4. BIN ->")
            println("0. Atrás")
            println("====================================")
            when (readInt()) {
                1 -> xmlConverterMenu()
                2 -> csvConverterMenu()
                3 -> jsonConverterMenu()
                4 -> binConverterMenu()
                0 -> return
                else -> println("Opción no válida")
            }
        }
    }

    private fun xmlConverterMenu() {
        val csv = directories.csvFile
        val json = directories.jsonFile
        val xml = directories.xmlFile
        val bin = directories.binFile
        while (true) {
            println("===== XML -> =====")
            println("1. CSV")
            println("2. JSON")
            println("3. BIN")
            println("0. Volver")
            when (readInt()) {
                1 -> convert(xml, csv, xmlManager::read, csvManager::write)
                2 -> convert(xml, json, xmlManager::read, jsonManager::write)
                3 -> convert(xml, bin, xmlManager::read, binManager::write)
                0 -> return
                else -> println("Opción no válida")
            }
        }
    }

    private fun csvConverterMenu() {
        val csv = directories.csvFile
        val json = directories.jsonFile
        val xml = directories.xmlFile
        val bin = directories.binFile
        while (true) {
            println("===== CSV -> =====")
            println("1. XML")
            println("2. JSON")
            println("3. BIN")
            println("0. Volver")
            when (readInt()) {
                1 -> convert(csv, xml, csvManager::read, xmlManager::write)
                2 -> convert(csv, json, csvManager::read, jsonManager::write)
                3 -> convert(csv, bin, csvManager::read, binManager::write)
                0 -> return
                else -> println("Opción no válida")
            }
        }
    }

    private fun jsonConverterMenu() {
        val csv = directories.csvFile
        val json = directories.jsonFile
        val xml = directories.xmlFile
        val bin = directories.binFile
        while (true) {
            println("===== JSON -> =====")
            println("1. XML")
            println("2. CSV")
            println("3. BIN")
            println("0. Volver")
            when (readInt()) {
                1 -> convert(json, xml, jsonManager::read, xmlManager::write)
                2 -> convert(json, csv, jsonManager::read, csvManager::write)
                3 -> convert(json, bin, jsonManager::read, binManager::write)
                0 -> return
                else -> println("Opción no válida")
            }
        }
    }

    private fun binConverterMenu() {
        val csv = directories.csvFile
        val json = directories.jsonFile
        val xml = directories.xmlFile
        val bin = directories.binFile
        while (true) {
            println("===== BIN -> =====")
            println("1. XML")
            println("2. CSV")
            println("3. JSON")
            println("0. Volver")
            when (readInt()) {
                1 -> convert(bin, xml, binManager::read, xmlManager::write)
                2 -> convert(bin, csv, binManager::read, csvManager::write)
                3 -> convert(bin, json, binManager::read, jsonManager::write)
                0 -> return
                else -> println("Opción no válida")
            }
        }
    }

    // Los BIN del catálogo se guardan en datos_ini; las demás conversiones, en datos_fin.
    private fun convert(
        from: String,
        to: String,
        read: (Path) -> List<Videogame>,
        write: (Path, List<Videogame>) -> Unit
    ) = converter.convert(
        directories.datosIni.resolve(from),
        (if (to == directories.binFile) directories.datosIni else directories.datosFin).resolve(to),
        read,
        write
    )

    // ---------- Lectura segura de teclado ----------

    // Devuelve null si lo escrito no es un número, en vez de lanzar una excepción
    private fun readInt(): Int? = readlnOrNull()?.trim()?.toIntOrNull()

    private fun readGrade(): Double? =
        readlnOrNull()?.trim()?.replace(',', '.')?.toDoubleOrNull()?.takeIf { it in 0.0..10.0 }
}
