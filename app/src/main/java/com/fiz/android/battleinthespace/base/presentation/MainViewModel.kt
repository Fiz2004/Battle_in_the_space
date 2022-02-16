package com.fiz.android.battleinthespace.base.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.base.data.Player
import com.fiz.android.battleinthespace.base.data.PlayerRepository
import com.fiz.android.battleinthespace.base.data.StateProduct
import com.fiz.android.battleinthespace.base.data.TypeItems
import com.fiz.android.battleinthespace.base.data.database.PlayerTypeConverters
import com.fiz.android.battleinthespace.base.domain.accounthelper.AccountHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*

class MainViewModel(private val playerRepository: PlayerRepository) : ViewModel() {
    val mAuth = FirebaseAuth.getInstance()

    var user = MutableLiveData<FirebaseUser?>(null)

    var playerListLiveData: LiveData<List<Player>?> = playerRepository.getPlayers()

    var player: Player = Player(money = 666)

    private var _type = MutableLiveData(0)
    val type: LiveData<Int>
        get() = _type

    private var _errorTextToToast = MutableLiveData<String?>(null)
    val errorTextToToast: LiveData<String?>
        get() = _errorTextToToast

    private val countPlayer: MutableLiveData<Int> = MutableLiveData(playerRepository.getCountPlayers())

    fun getItems(): List<TypeItems> {
        return player.items
    }

    fun getMoney(): Int {
        return player.money
    }

    fun changeItems(key: Int, type: Int, value: StateProduct) {
        player.items[type].items[key].state = value
    }

    fun fillInitValue() {
        playerRepository.fillInitValue()
    }

    fun setCountPlayers(numberRadioButton: Int) {
        countPlayer.value = numberRadioButton
    }

    fun initPlayer(newPlayer: Player) {
        player = newPlayer
    }

    fun addMoney(value: Int) {
        player.money += value
    }

    fun changeMission(value: Int) {
        player.mission = value
    }

    fun setType(value: Int) {
        _type.value = value

    }

    fun savePlayers() {
        playerRepository.saveCountPlayers(countPlayer.value!!)
        playerRepository.updatePlayer(player)
    }

    fun countPlayerLiveDataEquals(value: Int): Boolean {
        return countPlayer.value == value
    }

    fun countPlayerLiveDataCompare(value: Int): Boolean {
        return countPlayer.value!! >= value
    }

    fun onClickReset(count: Int) {
        val player1 = Player(id = 0, name = "Player 1")
        val player2 = Player(id = 1, name = "Player 2", controllerPlayer = false)
        val player3 = Player(id = 2, name = "Player 3", controllerPlayer = false)
        val player4 = Player(id = 3, name = "Player 4", controllerPlayer = false)

        val player = when (count) {
            1 -> player1
            2 -> player2
            3 -> player3
            else -> player4
        }

        playerRepository.updatePlayer(player)
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(
            "countPlayers",
            countPlayer.value!!)
        for (n in 0 until 4) {
            val value = (playerListLiveData.value)?.get(n) ?: throw Error("Не доступна LiveData playerListLiveData")

            outState.putString("name$n", value.name)
            outState.putBoolean("playerControllerPlayer$n", value.controllerPlayer)
            outState.putInt("mission$n", value.mission)
            outState.putString("items$n", PlayerTypeConverters().fromItems(value.items))
        }
    }

    fun getDataForIntent(intent: Intent): Intent {
        intent.putExtra("countPlayers", countPlayer.value)
        for (n in 0 until 4) {
            val value = (playerListLiveData.value)?.get(n) ?: throw Error("Не доступна LiveData playerListLiveData")

            intent.putExtra("name$n", value.name)
            intent.putExtra("playerControllerPlayer$n", value.controllerPlayer)
            intent.putExtra("mission$n", value.mission)
            intent.putExtra("items$n", PlayerTypeConverters().fromItems(value.items))
        }
        return intent
    }

    fun buyItem(
        index: Int,
        indexType: Int) {
        val money = getMoney()
        if (money - getItems()[indexType].items[index].cost >= 0) {
            player.money -= getItems()[indexType].items[index].cost
            changeItems(index, indexType, StateProduct.BUY)
        }
    }

    fun setErrorTextToToast(value: String) {
        _errorTextToToast.value = value
    }


    fun signUpWithEmail(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) return
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { onCompleteListenerSignUpWithEmail(it, email, password) }
    }

    private fun onCompleteListenerSignUpWithEmail(task: Task<AuthResult>, email: String, password: String) {
        if (task.isSuccessful) {
            sendEmailVerification(task.result?.user!!)
            user.value = task.result?.user!!
        } else {
            if (printInfoExceptionAndResolveProcess(task))
                linkEmailToG(email, password)
        }
    }

    private fun linkEmailToG(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        mAuth.currentUser?.linkWithCredential(credential)
            ?.addOnCompleteListener { onCompleteListenerLinkEmailToG(it) }
    }

    private fun onCompleteListenerLinkEmailToG(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            setErrorTextToToast("Link ok")
        } else {
            printInfoExceptionAndResolveProcess(task)
        }
    }

    fun signInWithGoogle(act: MainActivity) {
        val signInClient = getSignInClient(act)
        val intent = signInClient.signInIntent
        act.googleSignInActivityLauncher.launch(intent)
    }

    fun signInOutG(act: MainActivity) {
        getSignInClient(act).signOut()
    }

    //TODO Разобраться почему R.string не найден classpath 'com.google.gms:google-services:4.3.10'
    private fun getSignInClient(act: MainActivity): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(act.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(act, gso)
    }

    fun signInFirebaseWithGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        if (mAuth != null) {
            mAuth.signInWithCredential(credential)
                .addOnCompleteListener { onCompleteListenerSignInFirebaseWithGoogle(it) }
        } else {
            setErrorTextToToast("У вас уже есть аккаунт с таким email, войдите сначала через почту")
        }
    }

    private fun onCompleteListenerSignInFirebaseWithGoogle(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            setErrorTextToToast("Sign in done")
            user.value = task.result?.user
        } else {
            Log.d("MyLog", "Google sign in exception: ${task.exception}")
            setErrorTextToToast("Error Sign in done")
        }
    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) return
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { onCompleteListenerSignInWithEmail(it) }
    }

    private fun onCompleteListenerSignInWithEmail(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            user.value = task.result?.user!!
        } else {
            printInfoExceptionAndResolveProcess(task)
        }
    }

    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification()
            .addOnCompleteListener { onCompleteListenerSendEmailVerification(it) }
    }

    private fun onCompleteListenerSendEmailVerification(task: Task<Void>) {
        if (task.isSuccessful) {
            setErrorTextToToast("send email")
        } else {
            setErrorTextToToast("Error send email")
        }
    }

    private fun printInfoExceptionAndResolveProcess(task: Task<AuthResult>): Boolean {
        if (task.exception is FirebaseAuthUserCollisionException) {
            val exception = task.exception as FirebaseAuthUserCollisionException
            if (exception.errorCode == AccountHelper.Companion.FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
                setErrorTextToToast(AccountHelper.Companion.FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE)
                return true
            }
        }
        if (task.exception is FirebaseAuthInvalidCredentialsException) {
            val exception = task.exception as FirebaseAuthInvalidCredentialsException
            if (exception.errorCode == AccountHelper.Companion.FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                setErrorTextToToast(AccountHelper.Companion.FirebaseAuthConstants.ERROR_INVALID_EMAIL)
            }
            if (exception.errorCode == AccountHelper.Companion.FirebaseAuthConstants.ERROR_WRONG_PASSWORD) {
                setErrorTextToToast(AccountHelper.Companion.FirebaseAuthConstants.ERROR_WRONG_PASSWORD)
            }
        }
        if (task.exception is FirebaseAuthWeakPasswordException) {
            val exception = task.exception as FirebaseAuthWeakPasswordException
            if (exception.errorCode == AccountHelper.Companion.FirebaseAuthConstants.ERROR_WEAK_PASSWORD) {
                setErrorTextToToast(AccountHelper.Companion.FirebaseAuthConstants.ERROR_WEAK_PASSWORD)
            }
        }
        if (task.exception is FirebaseAuthInvalidUserException) {
            val exception = task.exception as FirebaseAuthInvalidCredentialsException
            // Если пользователя нет с таким email
            if (exception.errorCode == AccountHelper.Companion.FirebaseAuthConstants.ERROR_USER_NOT_FOUND) {
                setErrorTextToToast(AccountHelper.Companion.FirebaseAuthConstants.ERROR_USER_NOT_FOUND)
            }
        }
        return false
    }


}

class MainViewModelFactory(private val dataSource: PlayerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(dataSource) as T
    }
}