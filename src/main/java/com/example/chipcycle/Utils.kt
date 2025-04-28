package com.example.chipcycle

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

object Utils {

     fun showToast(str:String, cntx : Context){
        Toast.makeText(cntx,str, Toast.LENGTH_LONG).show()
    }

    private var firebaseAuthInstance : FirebaseAuth? = null
    fun getFirebaseAuthInstance() : FirebaseAuth? {
        if(firebaseAuthInstance==null){
            firebaseAuthInstance = FirebaseAuth.getInstance()
        }
        return firebaseAuthInstance!!
    }

    fun getCurrentUserId() : String{
        return FirebaseAuth.getInstance().currentUser?.uid.toString()
    }
}