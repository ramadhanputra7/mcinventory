package com.azhara.inventarisbarang.auth.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.azhara.inventarisbarang.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel(){

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val loginState = MutableLiveData<Boolean>()
    private val resetState = MutableLiveData<Boolean>()
    private val registerState = MutableLiveData<Boolean>()
    private val tag = AuthViewModel::class.java.simpleName
    var loginMessage: String? = null

    fun login(email: String, password: String){
        loginMessage = null
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                loginState.postValue(true)
            }else{
                Log.e(tag, "Error login: ${task.exception}")
                loginState.postValue(false)
                loginMessage = when (task.exception?.message) {
                    "The email address is badly formatted." -> {
                        "Format email salah!"
                    }
                    "There is no user record corresponding to this identifier. The user may have been deleted." -> {
                        "Email tidak terdaftar!"
                    }
                    "The password is invalid or the user does not have a password." -> {
                        "Password salah!"
                    }
                    "A network error (such as timeout, interrupted connection or unreachable host) has occurred." -> {
                        "Terjadi kesalahan pada jaringan!"
                    }
                    else -> {
                        task.exception?.message
                    }
                }
            }
        }
    }

    fun loginState(): LiveData<Boolean> = loginState

    fun resetPassword(email: String?){
        auth.sendPasswordResetEmail("$email").addOnCompleteListener { task ->
            if (task.isSuccessful){
                resetState.postValue(true)
            }
        }
    }

    fun resetState(): LiveData<Boolean> = resetState

    fun register(name: String?, position: String?, email: String?, telephone: String?, password: String?){
        auth.createUserWithEmailAndPassword("$email", "$password").addOnCompleteListener { task ->
            if (task.isSuccessful){
                val userId = auth.currentUser?.uid
                createUserDb(name, position, email, telephone, userId)
            }else{
                Log.e(tag, "Error register: ${task.exception?.message}")
                registerState.postValue(false)
            }
        }
    }

    private fun createUserDb(name: String?, position: String?, email: String?, telephone: String?, userId: String?){
        val user = User(name, position, email, telephone, null)
        val userDb = db.collection("users").document("$userId")
        userDb.set(user).addOnCompleteListener { task ->
            if (task.isSuccessful){
                registerState.postValue(true)
            }else{
                Log.e(tag, "Error add data user to db: ${task.exception?.message}")
                registerState.postValue(false)
            }
        }
    }

    fun registerState(): LiveData<Boolean> = registerState

}

