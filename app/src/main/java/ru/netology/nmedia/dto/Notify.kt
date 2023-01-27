package ru.netology.nmedia.dto

data class Notify (
    val content: String,
    val recipientId: Long?
)