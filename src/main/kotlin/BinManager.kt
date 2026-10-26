package esq.dam

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption


class BinManager(private val archivoPath: Path = Path.of("proyecto", "datos", "datos_ini", "videogames.bin")) {

    fun createBinFile(submittedPath: Path) {
        var pathCheck = submittedPath.toString()
        //Buscar si el path pasa por datos o no, cortando los directorios segun su '/' o '\'
        val splitted = pathCheck.split(Regex("[\\\\/]"))
        if (splitted.any { it.equals("datos", ignoreCase = true) }) {
            createCleanBinFile(submittedPath)
        } else {
            println("No puedes crear archivos BIN fuera del directorio datos")
        }
    }


    fun createCleanBinFile(path: Path) {
        try {
            Files.createDirectories(path.parent)
            FileChannel.open(
                path,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            ).close()
            println("--- El fichero BIN '${path.fileName}' se ha creado y está vacío.")
        } catch (e: Exception) {
            println("Error al vaciar o crear el fichero: ${e.message}")
        }
    }

    // Añade un registro de videojuego al final del fichero
    fun addBinGame(game: VideogameBIN) {
        require(game.id in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong()) { "El ID no cabe en el registro BIN" }
        addBinGame(game.toVideogame())
    }

    fun addBinGame(game: Videogame, path: Path = archivoPath) {
        try {
            Files.createDirectories(path.parent)
            FileChannel.open(
                path,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            ).use { canal ->
                val buffer = encodeGame(game)
                while (buffer.hasRemaining()) {
                    check(canal.write(buffer) > 0) { "No se pudo escribir el registro BIN" }
                }
                println("- VideojuegoBIN '${game.title}' añadido.")
            }
        } catch (e: Exception) {
            throw IllegalStateException("Error al añadir ${game.title} al BIN: ${e.message}", e)
        }
    }

    // Lee todos los registros de manera secuencial de inicio a fin
    fun readBinGames(): List<VideogameBIN> = readBinGames(archivoPath)

    private fun readBinGames(file: Path): List<VideogameBIN> {
        val games = mutableListOf<VideogameBIN>()

        if (!Files.isReadable(file)) return emptyList()

        FileChannel.open(file, StandardOpenOption.READ).use { canal ->
            val buffer = ByteBuffer.allocate(REGSIZE)

            while (canal.position() < canal.size()) {
                buffer.clear()
                while (buffer.hasRemaining()) {
                    if (canal.read(buffer) == -1) {
                        error("Registro binario incompleto")
                    }
                }
                buffer.flip()

                // 1. Leemos el ID (4 bytes).
                val id = buffer.getInt()

                // 2. Leemos el título (64 bytes) y el género (16 bytes).
                val titleBytes = ByteArray(TITLE_SIZE)
                buffer.get(titleBytes)
                val title = String(titleBytes, Charsets.ISO_8859_1).trimEnd()
                val genreBytes = ByteArray(GENRE_SIZE)
                buffer.get(genreBytes)
                val genre = String(genreBytes, Charsets.ISO_8859_1).trimEnd()

                // 3. Leemos el año (4 bytes) y la nota (8 bytes).
                val year = buffer.getInt()
                val grade = buffer.getDouble()

                games.add(VideogameBIN(id.toLong(), title, genre, year, grade))
            }
        }
        return games
    }

    fun read(file: Path): List<Videogame> {
        if (!Files.isReadable(file)) {
            println("No se puede leer el fichero $file")
            return emptyList()
        }
        return readBinGames(file).map { it.toVideogame() }
    }

    fun write(file: Path, games: List<Videogame>) {
        Files.createDirectories(file.parent)
        FileChannel.open(
            file,
            StandardOpenOption.WRITE,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        ).close()
        games.forEach { game ->
            addBinGame(game, file)
        }
    }

    fun getBinGameById(id: Int): VideogameBIN? = readBinGames().find { it.id == id.toLong() }

    fun modBinGame(game: VideogameBIN): Boolean {
        if (!Files.isRegularFile(archivoPath)) return false
        FileChannel.open(archivoPath, StandardOpenOption.READ, StandardOpenOption.WRITE).use { channel ->
            val position = findRecordPosition(channel, game.id) ?: return false
            writeGameAtPosition(channel, position, game.toVideogame())
            return true
        }
    }

    fun delBinGameById(id: Int): Boolean {
        if (!Files.isRegularFile(archivoPath)) return false
        FileChannel.open(archivoPath, StandardOpenOption.READ, StandardOpenOption.WRITE).use { channel ->
            val position = findRecordPosition(channel, id.toLong()) ?: return false
            val recordSize = REGSIZE.toLong()
            val size = channel.size()
            val buffer = ByteBuffer.allocate(REGSIZE)
            var source = position + recordSize

            while (source < size) {
                buffer.clear()
                readFully(channel, buffer, source)
                buffer.flip()
                while (buffer.hasRemaining()) {
                    check(channel.write(buffer, source - recordSize + buffer.position()) > 0) {
                        "No se pudo desplazar un registro BIN"
                    }
                }
                source += recordSize
            }
            channel.truncate(size - recordSize)
            return true
        }
    }

    fun writeGameAtPosition(channel: FileChannel, position: Long, game: Videogame) {
        require(position >= 0L && position % REGSIZE.toLong() == 0L) { "Posición de registro no válida" }
        require(position + REGSIZE.toLong() <= channel.size()) { "El registro no existe en el fichero BIN" }
        val buffer = encodeGame(game)
        while (buffer.hasRemaining()) {
            check(channel.write(buffer, position + buffer.position()) > 0) { "No se pudo escribir el registro BIN" }
        }
    }

    private fun findRecordPosition(channel: FileChannel, id: Long): Long? {
        val recordSize = REGSIZE.toLong()
        val size = channel.size()
        check(size % recordSize == 0L) { "El fichero BIN contiene un registro incompleto" }
        val idBuffer = ByteBuffer.allocate(ID_SIZE).order(ByteOrder.BIG_ENDIAN)
        var position = 0L
        while (position < size) {
            idBuffer.clear()
            readFully(channel, idBuffer, position)
            idBuffer.flip()
            if (idBuffer.int.toLong() == id) return position
            position += recordSize
        }
        return null
    }

    private fun readFully(channel: FileChannel, buffer: ByteBuffer, position: Long) {
        while (buffer.hasRemaining()) {
            check(channel.read(buffer, position + buffer.position()) > 0) { "Registro binario incompleto" }
        }
    }

    private fun encodeGame(game: Videogame): ByteBuffer {
        val encodedTitle = game.title.toByteArray(Charsets.ISO_8859_1)
        val encodedGenre = game.genre.toByteArray(Charsets.ISO_8859_1)
        require(encodedTitle.size <= TITLE_SIZE && encodedGenre.size <= GENRE_SIZE) {
            "El título o el género excede el tamaño del registro BIN"
        }

        val buffer = ByteBuffer.allocate(REGSIZE).order(ByteOrder.BIG_ENDIAN)
        val titleBytes = ByteArray(TITLE_SIZE) { ' '.code.toByte() }
        val genreBytes = ByteArray(GENRE_SIZE) { ' '.code.toByte() }
        encodedTitle.copyInto(titleBytes)
        encodedGenre.copyInto(genreBytes)
        buffer.putInt(game.id)
        buffer.put(titleBytes)
        buffer.put(genreBytes)
        buffer.putInt(game.year)
        buffer.putDouble(game.grade)
        buffer.flip()
        return buffer
    }

    fun mostrarInfo() {
        // Mostramos la información
        println("\n--- Videojuegos leídos secuencialmente del fichero .bin: ---")
        val games = readBinGames()
        for (game in games) {
            println(" - ID: ${game.id}, Título: ${game.titulo}, Género: ${game.genero}, Año: ${game.anio}, Nota: ${game.nota}")
        }
    }

}

