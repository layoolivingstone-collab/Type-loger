package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dial_logs")
data class DialLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val phoneNumber: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isDialed: Boolean, // True if actual cellular dial intent triggered, false if just saved
    val label: String = "", // Optional tag/label (e.g., Work, Home, Alert)
    val rawKeystrokes: String // Format: "key+offset|key+offset|..."
)

data class KeystrokeEvent(
    val key: String,
    val offsetMs: Long
) {
    override fun toString(): String = "$key+$offsetMs"
}

object KeystrokeParser {
    fun parse(raw: String): List<KeystrokeEvent> {
        if (raw.isBlank()) return emptyList()
        return raw.split("|").mapNotNull { block ->
            val parts = block.split("+")
            if (parts.size == 2) {
                val key = parts[0]
                val offset = parts[1].toLongOrNull() ?: 0L
                KeystrokeEvent(key, offset)
            } else {
                null
            }
        }
    }

    fun format(events: List<KeystrokeEvent>): String {
        return events.joinToString("|") { "${it.key}+${it.offsetMs}" }
    }
}
