# Catálogo de Videojuegos

Programa de consola en Kotlin para gestionar o convertir archivos JSON, CSV, XML Y BIN

## 1. Estructura de datos

### **Data Class:**

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

### **Estructura del registro binario:**

- **ID**: Int - 4 bytes
- **titulo**: String - 64 bytes (longitud fija)
- **genero**: String - 16 bytes (longitud fija)
- **anio**: Int - 4 bytes
- **nota**: Double - 8 bytes
- **Tamaño Total del Registro**: 4 + 64 + 16 + 4 + 8 = 96 bytes

## 2. Instrucciones de ejecución

- **Requisitos previos**: JDK 17 o superior instalado.
- **Compilación**: Abre el proyecto en IntelliJ IDEA y deja que Gradle sincronice las dependencias.
- **Fichero de datos inicial**: Coloca `videojuegos.csv` en la carpeta `datos_ini` antes de ejecutar.
- **Ejecución principal**: Ejecuta `Main.kt` para el catálogo con acceso aleatorio.
- **Conversión entre formatos**: El conversor es accesible desde el menu de la aplicación.

## 3. Decisiones de diseño

- Elegí CSV como formato inicial porque es sencillo de crear y editar manualmente con cualquier hoja de cálculo.
- Reservé 64 bytes para el título porque algunos títulos largos como "World of Warcraft : Wrath of the Lich King" tiene 41 caracteres y quiero tener margen para otros titulos de tamaño similar.
- Reservé 20 bytes para el género porque los géneros más largos que manejo ("Plataformas") tienen 11 caracteres y quiero margen para futuros géneros.