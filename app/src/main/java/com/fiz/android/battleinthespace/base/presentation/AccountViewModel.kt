package com.fiz.android.battleinthespace.base.presentation

import android.content.Intent
import androidx.lifecycle.*
import com.fiz.android.battleinthespace.base.domain.accounthelper.AccountHelper

class AccountViewModel(
    private val accountHelper: AccountHelper) : ViewModel() {

    val user = Transformations.map(accountHelper.user) { it }
    val mAuth = accountHelper.mAuth

    private var _errorTextToToast = MutableLiveData<String?>(null)
    val errorTextToToast: LiveData<String?>
        get() = _errorTextToToast

    init {
        accountHelper.initErrorTextToToast(_errorTextToToast)
    }

    fun signUpWithEmail(email: String, password: String) {
        accountHelper.signUpWithEmail(email, password)
    }

    fun signInWithEmail(email: String, password: String) {
        accountHelper.signInWithEmail(email, password)
    }

    fun signInWithGoogle(act: MainActivity) {
        accountHelper.signInWithGoogle(act)
    }

    fun signInFirebaseWithGoogle(result: Intent) {
        accountHelper.signInFirebaseWithGoogle(result)
    }

    fun resetPassword(email: String) {
        accountHelper.resetPassword(email)
    }

    fun signInOutG(act: MainActivity) {
        accountHelper.signInOutG(act)
    }
}

class AccountViewModelFactory : ViewModelProvider.Factory {
    private val accountHelper = AccountHelper()

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AccountViewModel(accountHelper) as T
    }
}