package com.azhara.inventarisbarang.home.profile.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.azhara.inventarisbarang.entity.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileViewModel : ViewModel(){

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val user = auth.currentUser
    private val userId = user?.uid
    private val tag = ProfileViewModel::class.java.simpleName
    private val dataUser = MutableLiveData<User>()
    var changePasswordMsgError: String? = null
    private val changePasswordState = MutableLiveData<Boolean?>()
    private val editProfileState = MutableLiveData<Boolean?>()

    fun loadDataUser(){
        val userDb = db.collection("users").document("$userId")
        userDb.addSnapshotListener { value, error ->
            if (error != null){
                Log.e(tag, "Error load data user: ${error.message}")
            }
            if (value != null && value.exists()){
                val data = value.toObject(User::class.java)
                dataUser.postValue(data)
            }
        }
    }

    fun dataUser(): LiveData<User> = dataUser

    fun changePassword(oldPassword: String, newPassword: String) {
        changePasswordMsgError = null
        val credential = user?.email?.let { EmailAuthProvider.getCredential(it, oldPassword) }
        if (credential != null) {
            user?.reauthenticate(credential)?.addOnSuccessListener {
                Log.d(tag, "email: ${user.email} Success authenticated")
                user.updatePassword(newPassword).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(tag, "email:${user.email} Success Update password")
                        changePasswordState.postValue(true)
                    } else {
                        Log.e(
                            tag,
                            "email: ${user.email}, changePassword, exception: ${task.exception?.message}"
                        )
                        changePasswordMsgError = task.exception?.message
                        changePasswordState.postValue(false)
                    }
                }

            }?.addOnFailureListener { e ->
                Log.e(tag, "email: ${user.email}, changePassword, exception: ${e.message}")
                when (e.message) {
                    "The password is invalid or the user does not have a password." -> {
                        changePasswordMsgError = "Password lama salah"
                        changePasswordState.postValue(false)
                    }
                    "A network error (such as timeout, interrupted connection or unreachable host) has occurred." -> {
                        changePasswordMsgError = "Kesalahan jaringan, silahkan cek jaringan anda!"
                        changePasswordState.postValue(false)
                    }
                    else -> {
                        changePasswordMsgError = e.message
                        changePasswordState.postValue(false)
                    }
                }
            }
        }
    }

    fun changePasswordState(): LiveData<Boolean?> = changePasswordState

    fun editProfileWithoutImage(name: String?, phone: String?, position: String?){
        val userDb = db.collection("users").document("$userId")
        val data = hashMapOf<String?, Any?>(
            "name" to name,
            "position" to position,
            "telephone" to phone
        )
        userDb.update(data).addOnCompleteListener { task ->
            if (task.isSuccessful){
                editProfileState.postValue(true)
            }else{
                Log.e(tag, "Error edit profile: ${task.exception?.message}")
                editProfileState.postValue(false)
            }
        }
    }

    private fun editProfileDb(name: String?, phone: String?, position: String?, imgUrl: String?){
        val userDb = db.collection("users").document("$userId")

        val data = hashMapOf<String?, Any?>(
            "name" to name,
            "position" to position,
            "telephone" to phone,
            "imgUrl" to imgUrl
        )

        userDb.update(data).addOnCompleteListener { task ->
            if (task.isSuccessful){
                editProfileState.postValue(true)
            }else{
                Log.e(tag, "Error edit profile: ${task.exception?.message}")
                editProfileState.postValue(false)
            }
        }
    }

    fun editProfileWithImage(name: String?, phone: String?, position: String?, byteArrayImg: ByteArray?){
        val imgStorage = storage.reference.child("users")
            .child("imageProfile").child("$userId").child("$userId")

        val uploadTask = byteArrayImg?.let { imgStorage.putBytes(it) }
        uploadTask?.addOnSuccessListener { taskSnapshot ->
            (uploadTask).continueWithTask { task ->
                if (!task.isSuccessful) {
                    Log.e(
                        tag,
                        "email: ${user?.email}, editDataAndImgUser, exception: ${task.exception?.message}"
                    )
                }
                imgStorage.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val urlImg = task.result
                    editProfileDb(name, phone, position, urlImg.toString())
                } else {
                    Log.d(tag, "${user?.email} ${task.exception?.message}")
                }
            }
        }
    }

    fun editProfileState(): LiveData<Boolean?> = editProfileState

}