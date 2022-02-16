package com.fiz.android.battleinthespace.base.presentation.helpers

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class ActivityContract : ActivityResultContract<Intent, Intent?>() {

    override fun createIntent(context: Context, input: Intent): Intent {
        return input
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Intent? = when {
        resultCode != Activity.RESULT_OK -> null
        else -> intent
    }
}