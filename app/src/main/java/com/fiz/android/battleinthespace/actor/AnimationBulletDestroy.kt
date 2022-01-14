package com.fiz.android.battleinthespace.actor

import com.fiz.android.battleinthespace.Vec

data class AnimationBulletDestroy(
    var center: Vec,

    var timeShowMax: Int = 1000,
    var timeShow: Int = timeShowMax

) {
    constructor(bullet: Bullet):this(bullet.center)
}

