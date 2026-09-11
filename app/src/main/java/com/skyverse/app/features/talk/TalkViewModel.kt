package com.skyverse.app.features.talk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyverse.app.core.ai.SkyOrchestrator
import com.skyverse.app.core.ai.SkyResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TalkState {
    IDLE, LISTENING, THINKING, SPEAKING, ERROR
}

data class ChatMessage(val isUser: Boolean, val text: String)

data class TalkUiState(
    val state: TalkState = TalkState.IDLE,
    val history: List<ChatMessage> = emptyList(),
    val currentResponse: SkyResponse? = null
)

@HiltViewModel
class TalkViewModel @Inject constructor(
    private val orchestrator: SkyOrchestrator
) : ViewModel() {

    private val _uiState = MutableStateFlow(TalkUiState())
    val uiState: StateFlow<TalkUiState> = _uiState.asStateFlow()

    fun processQuery(query: String) {
        if (query.isBlank()) return
        
        val newHistory = _uiState.value.history + ChatMessage(isUser = true, text = query)
        _uiState.value = _uiState.value.copy(
            state = TalkState.THINKING,
            history = newHistory
        )

        viewModelScope.launch {
            try {
                // In Phase 2, this will stream tokens via Flow.
                val response = orchestrator.processQuery(query)
                _uiState.value = _uiState.value.copy(
                    state = TalkState.SPEAKING,
                    currentResponse = response,
                    history = _uiState.value.history + ChatMessage(isUser = false, text = response.text)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(state = TalkState.ERROR)
            }
        }
    }
    
    fun setIdle() {
        _uiState.value = _uiState.value.copy(state = TalkState.IDLE)
    }
}
