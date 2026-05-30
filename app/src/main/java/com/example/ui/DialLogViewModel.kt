package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.DialLog
import com.example.data.DialLogRepository
import com.example.data.KeystrokeEvent
import com.example.data.KeystrokeParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DialLogViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DialLogRepository
    val allLogs: StateFlow<List<DialLog>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = DialLogRepository(database.dialLogDao())
        allLogs = repository.allLogs.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    // Modern VM State Flow for the interactive key logger dialer
    val enteredNumber = MutableStateFlow("")
    val keystrokeBuff = MutableStateFlow<List<KeystrokeEvent>>(emptyList())
    
    // Tracking times for keystroke speed analytics
    private var firstKeyPressTime: Long = 0L

    fun onKeyPress(key: String) {
        val currentNum = enteredNumber.value
        // Max dial characters check
        if (currentNum.length >= 24) return

        val now = System.currentTimeMillis()
        if (currentNum.isEmpty()) {
            firstKeyPressTime = now
            val event = KeystrokeEvent(key, 0L)
            keystrokeBuff.value = listOf(event)
        } else {
            val offset = now - firstKeyPressTime
            val event = KeystrokeEvent(key, offset)
            keystrokeBuff.value = keystrokeBuff.value + event
        }

        enteredNumber.value = currentNum + key
    }

    fun onBackspace() {
        val num = enteredNumber.value
        if (num.isNotEmpty()) {
            enteredNumber.value = num.dropLast(1)
            val buff = keystrokeBuff.value
            if (buff.isNotEmpty()) {
                keystrokeBuff.value = buff.dropLast(1)
            }
        }
        if (enteredNumber.value.isEmpty()) {
            firstKeyPressTime = 0L
            keystrokeBuff.value = emptyList()
        }
    }

    fun clearDialer() {
        enteredNumber.value = ""
        keystrokeBuff.value = emptyList()
        firstKeyPressTime = 0L
    }

    fun saveCurrentSession(context: Context, label: String, isDialed: Boolean) {
        val number = enteredNumber.value
        if (number.isBlank()) {
            Toast.makeText(context, "Dialer is empty!", Toast.LENGTH_SHORT).show()
            return
        }

        val rawStr = KeystrokeParser.format(keystrokeBuff.value)
        
        viewModelScope.launch {
            repository.insert(
                DialLog(
                    phoneNumber = number,
                    isDialed = isDialed,
                    label = label,
                    rawKeystrokes = rawStr
                )
            )
            clearDialer()
            Toast.makeText(context, "Logged dialed sequence securely", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteLog(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }

    fun triggerSystemDial(context: Context) {
        val number = enteredNumber.value
        if (number.isBlank()) return

        // 1) Save and log the interactive layout keystrokes
        saveCurrentSession(context, "System Dialed", true)

        // 2) Open systemic call intent
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${Uri.encode(number)}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not launch system dialer", Toast.LENGTH_SHORT).show()
        }
    }
}
