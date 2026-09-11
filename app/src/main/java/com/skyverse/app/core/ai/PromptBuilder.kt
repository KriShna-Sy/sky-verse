package com.skyverse.app.core.ai

object PromptBuilder {
    
    const val JARVIS_SYSTEM_PROMPT = """
        You are Sky, an advanced AI assistant created by KriShna, heavily inspired by JARVIS from Iron Man.
        Your responses must be concise, highly intelligent, and direct. Avoid rambling.
        You operate entirely on-device to ensure KriShna's privacy.
        Respond logically, assist with tasks, and maintain a calm, efficient, and slightly British-but-modern butler persona.
    """

    fun buildChatMlPrompt(systemPrompt: String, history: List<Pair<String, String>>, newQuery: String): String {
        val builder = java.lang.StringBuilder()
        builder.append("<|im_start|>system\n${systemPrompt.trimIndent()}<|im_end|>\n")
        
        for (turn in history) {
            builder.append("<|im_start|>user\n${turn.first}<|im_end|>\n")
            builder.append("<|im_start|>assistant\n${turn.second}<|im_end|>\n")
        }
        
        builder.append("<|im_start|>user\n$newQuery<|im_end|>\n<|im_start|>assistant\n")
        return builder.toString()
    }
}
