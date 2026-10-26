package esq.dam
// Tamaños del registro binario
const val ID_SIZE = Int.SIZE_BYTES // 4 bytes
const val TITLE_SIZE = 64 // 64 bytes para el título
const val GENRE_SIZE = 16 // 16 bytes para el género
const val YEAR_SIZE = Int.SIZE_BYTES // 4 bytes
const val GRADE_SIZE = Double.SIZE_BYTES // 8 bytes
const val REGSIZE = ID_SIZE + TITLE_SIZE + GENRE_SIZE + YEAR_SIZE + GRADE_SIZE
fun main() {
    val directories = Directories()
    directories.reviewDirectoryStructure()
    directories.printTree()

    val repo = VideogameRepository(directories)
    repo.load()

    val menu = Menu(repo, directories)
    menu.showMainMenu()
}
