package esq.dam

// Guarda la lista de videojuegos en memoria y la sincroniza con el CSV de datos_ini
class VideogameRepository(directories: Directories) {
    private val csvManager = CsvManager()
    private val csvFile = directories.datosIni.resolve(directories.csvFile)
    val games: MutableList<Videogame> = mutableListOf()

    fun load() {
        games.clear()
        games += csvManager.read(csvFile)
        println("${games.size} videojuegos cargados")
    }

    fun save() {
        csvManager.write(csvFile, games)
    }

    fun findById(id: Int): Videogame? = games.find { it.id == id }

    fun add(title: String, genre: String, year: Int, grade: Double): Videogame {
        val id = (games.maxOfOrNull { it.id } ?: 0) + 1
        val game = Videogame(id, title, genre, year, grade)
        games += game
        save()
        return game
    }

    fun updateGrade(id: Int, grade: Double): Videogame? {
        val index = games.indexOfFirst { it.id == id }
        if (index == -1) return null
        games[index] = games[index].copy(grade = grade)
        save()
        return games[index]
    }

    fun delete(game: Videogame) {
        games.remove(game)
        save()
    }

    // Añade los juegos cuyo ID no exista todavía. Devuelve cuántos se han añadido.
    fun import(newGames: List<Videogame>): Int {
        val ids = games.map { it.id }.toSet()
        val toAdd = newGames.filter { it.id !in ids }.distinctBy { it.id }
        games += toAdd
        save()
        return toAdd.size
    }
}
