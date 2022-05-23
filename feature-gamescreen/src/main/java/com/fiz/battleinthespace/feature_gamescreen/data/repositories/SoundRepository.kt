package com.fiz.battleinthespace.feature_gamescreen.data.repositories

import android.content.Context
import android.media.SoundPool
import android.util.SparseIntArray
import com.fiz.battleinthespace.feature_gamescreen.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundRepository @Inject constructor(@ApplicationContext context: Context) {
    var soundPool: SoundPool = SoundPool.Builder().build()
    var soundMap: SparseIntArray = SparseIntArray(2)

    init {
        soundMap.put(0, soundPool.load(context, R.raw.fire, 1))
        soundMap.put(1, soundPool.load(context, R.raw.collision, 1))
    }
}