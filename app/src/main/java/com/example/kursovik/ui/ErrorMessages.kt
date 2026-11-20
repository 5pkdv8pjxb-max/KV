package com.example.kursovik.ui

import java.io.IOException

/** Преобразует исключение в понятное пользователю сообщение на русском языке. */
fun Throwable.toUserMessage(default: String): String = when (this) {
    is IOException -> "Нет подключения к интернету. Проверьте сеть и повторите попытку."
    is IllegalArgumentException -> message ?: default
    else -> default
}
