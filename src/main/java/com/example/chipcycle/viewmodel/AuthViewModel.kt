package com.example.chipcycle.auth

import android.app.Application
import android.content.Intent
import android.util.Log
import com.example.chipcycle.R
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chipcycle.Utils
import com.example.chipcycle.models.Users
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user


    private val _authStatus = MutableLiveData<String>()
    val authStatus: LiveData<String> = _authStatus

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isBtnLoading = MutableLiveData<Boolean>()
    val isBtnLoading : LiveData<Boolean> = _isBtnLoading

    val googleSignInClient: GoogleSignInClient

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(application.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(application.applicationContext, gso)


        _user.value = auth.currentUser
    }

    fun registerUser(email: String, password: String) {
        _isBtnLoading.value = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isBtnLoading.value = false
                if (task.isSuccessful) {
                    sendVerificationEmail(auth.currentUser!!)
                } else {
                    _authStatus.value = "Error: ${task.exception?.message}"
                }
            }
    }

    private fun sendVerificationEmail(user: FirebaseUser) {
        user.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authStatus.value = "Verification Mail Sent. Please check your email."

                } else {
                    _authStatus.value = "Error Sending Mail"
                }
            }
    }

    fun loginUser(email: String, password: String) {

        _isBtnLoading.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isBtnLoading.value = false
                if (task.isSuccessful) {
                    val loggedInUser = auth.currentUser
                    _user.value = auth.currentUser
                    if (loggedInUser != null && loggedInUser.isEmailVerified) {
                        val user = Users(uid = FirebaseAuth.getInstance().currentUser!!.uid, email = loggedInUser.email, fullName = "Eco Friend")
                        FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(user.uid!!).setValue(user)

                        _user.value = auth.currentUser
                        _authStatus.value = "Login Successful"
                    } else {
                        _authStatus.value = "Please verify your email before logging in."
                        auth.signOut()
                    }
                } else {
                    _authStatus.value = "Login Failed: ${task.exception?.message}"
                }
            }
    }

    fun signInWithGoogle(data: Intent?) {

        _isLoading.value = true
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
            if (account != null) {


                firebaseAuthWithGoogle(account)
            }
        } catch (e: ApiException) {
            _authStatus.value = "Google Sign In failed: ${e.message}"
            _isLoading.value = false
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            _isLoading.value = false
            if (task.isSuccessful) {
                _user.value = auth.currentUser
                val user = Users(uid = FirebaseAuth.getInstance().currentUser!!.uid, email = account.email, fullName = account.displayName)
                FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(user.uid!!).setValue(user)
                _authStatus.value = "Google Sign In Successful"
            } else {
                _authStatus.value = "Authentication Failed: ${task.exception?.message}"
            }
        }
    }

    fun logout() {
        auth.signOut()
        googleSignInClient.signOut()
        _user.value = null
        _authStatus.value = "Logged Out"
    }
}
