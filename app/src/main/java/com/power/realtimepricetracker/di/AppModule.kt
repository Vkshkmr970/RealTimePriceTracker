package com.power.realtimepricetracker.di

import com.power.realtimepricetracker.data.websocket.PriceRepository
import com.power.realtimepricetracker.data.websocket.WebSocketManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWebSocketManager(): WebSocketManager = WebSocketManager()

    @Provides
    @Singleton
    fun providePriceRepository(manager: WebSocketManager): PriceRepository =
        PriceRepository(manager)

}