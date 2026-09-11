package com.skyverse.app.di

import android.content.Context
import com.skyverse.app.core.context.ContextAwarenessEngine
import com.skyverse.app.core.memory.MemoryManager
import com.skyverse.app.core.performance.LowMemoryProfiler
import com.skyverse.app.tools.ToolManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMemoryManager(@ApplicationContext context: Context): MemoryManager {
        return MemoryManager(context)
    }

    @Provides
    @Singleton
    fun provideToolManager(@ApplicationContext context: Context): ToolManager {
        return ToolManager(context)
    }

    @Provides
    @Singleton
    fun provideLowMemoryProfiler(@ApplicationContext context: Context): LowMemoryProfiler {
        return LowMemoryProfiler(context)
    }

    @Provides
    @Singleton
    fun provideContextAwarenessEngine(@ApplicationContext context: Context): ContextAwarenessEngine {
        return ContextAwarenessEngine(context)
    }

    @Provides
    @Singleton
    fun provideSkyOrchestrator(@ApplicationContext context: Context): com.skyverse.app.core.ai.SkyOrchestrator {
        return com.skyverse.app.core.ai.SkyOrchestrator(context)
    }
}
