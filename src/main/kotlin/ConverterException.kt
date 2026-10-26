package esq.dam

sealed class ConverterException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)