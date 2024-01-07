package com.fiz.battleinthespace.di

import com.fiz.battleinthespace.common.AppDispatchers
import com.fiz.battleinthespace.common.DefaultDispatchersImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal class AppModule {

    @Provides
    fun provideCoroutineDispatcher(): AppDispatchers {
        return DefaultDispatchersImpl()
    }
}