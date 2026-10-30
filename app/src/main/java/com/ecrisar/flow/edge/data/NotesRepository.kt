package com.ecrisar.flow.edge.data

import android.content.Context
import com.ecrisar.flow.edge.model.NoteItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class NotesRepository(context: Context) {
    private val prefs = context.getSharedPreferences("edge_panel_notes", Context.MODE_PRIVATE)

    private val _notes = MutableStateFlow<List<NoteItem>>(loadNotes())
    val notes: StateFlow<List<NoteItem>> = _notes.asStateFlow()

    private fun loadNotes(): List<NoteItem> {
        val raw = prefs.getString(KEY_NOTES, null)
        if (raw.isNullOrEmpty()) {
            return listOf(
                NoteItem(
                    id = UUID.randomUUID().toString(),
                    title = "Meeting Notes",
                    content = "Discuss Edge panel gesture animations and freeform pop-up window scaling.",
                    colorHex = 0xFF1E3A5F
                ),
                NoteItem(
                    id = UUID.randomUUID().toString(),
                    title = "Groceries",
                    content = "• Oat milk\n• Avocados\n• Dark roast beans\n• Sparkling water",
                    colorHex = 0xFF2B3A2C
                ),
                NoteItem(
                    id = UUID.randomUUID().toString(),
                    title = "Ideas",
                    content = "Add haptic slider when adjusting the edge handle position.",
                    colorHex = 0xFF3D2645
                )
            )
        }

        val list = mutableListOf<NoteItem>()
        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    NoteItem(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        content = obj.getString("content"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        colorHex = obj.optLong("colorHex", 0xFF2D3035)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private fun persist(list: List<NoteItem>) {
        _notes.value = list
        val array = JSONArray()
        list.forEach { note ->
            val obj = JSONObject().apply {
                put("id", note.id)
                put("title", note.title)
                put("content", note.content)
                put("timestamp", note.timestamp)
                put("colorHex", note.colorHex)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_NOTES, array.toString()).apply()
    }

    fun addNote(title: String, content: String, colorHex: Long = 0xFF1E3A5F) {
        val newNote = NoteItem(
            id = UUID.randomUUID().toString(),
            title = title.ifBlank { "Untitled Note" },
            content = content,
            timestamp = System.currentTimeMillis(),
            colorHex = colorHex
        )
        val updated = listOf(newNote) + _notes.value
        persist(updated)
    }

    fun updateNote(note: NoteItem) {
        val updated = _notes.value.map { if (it.id == note.id) note else it }
        persist(updated)
    }

    fun deleteNote(id: String) {
        val updated = _notes.value.filterNot { it.id == id }
        persist(updated)
    }

    companion object {
        private const val KEY_NOTES = "notes_json"
    }
}
