package com.fiz.battleinthespace.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.realm.Realm
import io.realm.RealmConfiguration

private const val SHARED_PREFS_NAME = "shared_prefs_player"

private const val DATABASE_NAME = "BITS.realm"

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    fun provideRealm(@ApplicationContext context: Context): Realm {
        Realm.init(context)

        val config = RealmConfiguration.Builder()
            .name(DATABASE_NAME)
            .allowWritesOnUiThread(true)
            .allowQueriesOnUiThread(true)
            .build()

        return Realm.getInstance(config)
    }
}