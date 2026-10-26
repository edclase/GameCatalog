# Catálogo de videojuegos

Programa de consola en Kotlin para gestionar un catálogo de videojuegos y convertir sus datos entre los formatos JSON, CSV, XML y BIN. Cada registro contiene un identificador, título, género, año y nota.

## 1. Estructura de datos

### **Data classes**

```kotlin
data class Videogame(
    val id: Int,
    val titulo: String,
    val genero: String,
    val anio: Int,
    val nota: Double
)
data class VideogameBIN(
    val id: Long,
    val titulo: String,
    val genero: String,
    val anio: Int,
    val nota: Double
)
data class VideogameCSV(
    val id: Int,
    val titulo: String,
    val genero: String,
    val anio: Int,
    val nota: Double
)
@Serializable
data class VideogameJSON(
    val id: Int,
    val titulo: String,
    val genero: String,
    val anio: Int,
    val nota: Double
)
```

### **Estructura del registro binario**

- **ID**: Int - 4 bytes
- **Título**: String - 64 bytes (longitud fija)
- **Género**: String - 16 bytes (longitud fija)
- **Año**: Int - 4 bytes
- **Nota**: Double - 8 bytes
- **Tamaño total del registro**: 4 + 64 + 16 + 4 + 8 = 96 bytes

Los campos de texto ocupan un espacio fijo en cada registro BIN. Esto permite mantener un tamaño constante y localizar los registros por su posición.

## 2. Instrucciones de ejecución

- **Requisitos previos**: JDK 17 o superior instalado.
- **Compilación**: Abre el proyecto en IntelliJ IDEA y deja que Gradle sincronice las dependencias.
- **Fichero de datos inicial**: Coloca `videojuegos.csv` en la carpeta `datos_ini` antes de ejecutar.
- **Ejecución principal**: Ejecuta `Main.kt` para iniciar el catálogo con acceso aleatorio.
- **Conversión entre formatos**: El conversor está disponible desde el menú de la aplicación; desde allí se puede elegir la operación correspondiente.

## 3. Decisiones de diseño

- Elegí CSV como formato inicial porque es sencillo de crear y editar manualmente con cualquier hoja de cálculo.
- Reservé 64 bytes para el título porque algunos títulos largos, como "World of Warcraft: Wrath of the Lich King", tienen 41 caracteres y quería dejar margen para otros de tamaño similar.
- Reservé 16 bytes para el género. Los géneros más largos que manejo ("Plataformas") tienen 11 caracteres, así que queda espacio adicional para otros géneros.
- Mantengo los mismos campos principales en los distintos formatos para facilitar su conversión y conservar la información del catálogo.
- Uso registros binarios de tamaño fijo para que cada videojuego ocupe siempre el mismo espacio y resulte más sencillo acceder a sus datos por posición.



