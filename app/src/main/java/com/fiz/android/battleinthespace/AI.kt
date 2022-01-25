package com.fiz.android.battleinthespace

class AI(private var state: State) {
    fun update(controller: Controller) {
        controller.fire = true
    }
}