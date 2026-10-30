package com.ecrisar.flow.edge.model

/**
 * Model representing a quick note in the Notes Edge Panel.
 */
data class NoteItem(
    val id: String,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val colorHex: Long = 0xFF2D3035
)
