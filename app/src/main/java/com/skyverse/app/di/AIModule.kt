package com.skyverse.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.skyverse.app.core.voice.SpeakerVerificationEngine

@Module
@InstallIn(SingletonComponent::class)
object AIModule {

    @Provides
    @Singleton
    fun provideSpeakerVerificationEngine(): SpeakerVerificationEngine {
        return SpeakerVerificationEngine()
    }
    
    // TODO: Add LlamaEngine, WhisperEngine, WakeWordEngine as they are implemented in Phase 2 & 3
}
