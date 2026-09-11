package com.skyverse.app.core.ai

class ConversationManager(private val maxHistoryTurns: Int = 5) {
    
    // Stores pairs of (User Query, Assistant Response)
    private val history = mutableListOf<Pair<String, String>>()
    
    fun getHistory(): List<Pair<String, String>> {
        return history.toList()
    }
    
    fun addTurn(userQuery: String, assistantResponse: String) {
        history.add(Pair(userQuery, assistantResponse))
        if (history.size > maxHistoryTurns) {
            history.removeAt(0)
        }
    }
    
    fun clearHistory() {
        history.clear()
    }
    
    fun adjustMaxTurns(tier: RamTier) {
        // We dynamically adjust context window based on device tier
        // LITE tier gets 2 turns of memory to save RAM. ULTRA gets 10.
        val newMax = when(tier) {
            RamTier.LITE -> 2
            RamTier.STANDARD -> 5
            RamTier.POWER -> 8
            RamTier.ULTRA -> 15
        }
        
        while (history.size > newMax) {
            history.removeAt(0)
        }
    }
}
