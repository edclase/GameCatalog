package esq.dam

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

data class VideogameXML(
    @JacksonXmlProperty(localName = "id") val id: Int,
    @JacksonXmlProperty(localName = "titulo") val titulo: String,
    @JacksonXmlProperty(localName = "genero") val genero: String,
    @JacksonXmlProperty(localName = "anio") val anio: Int,
    @JacksonXmlProperty(localName = "nota") val nota: Double
) {
    fun toVideogame(): Videogame = Videogame(id, titulo, genero, anio, nota)
}

@JacksonXmlRootElement(localName = "videojuegos")
data class VideogamesXML(
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "videojuego")
    val videogames: List<VideogameXML> = emptyList()
)
