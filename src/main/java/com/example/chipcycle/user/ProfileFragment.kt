package com.example.chipcycle.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.chipcycle.R
import com.example.chipcycle.Utils
import com.example.chipcycle.activity.MainActivity
import com.example.chipcycle.databinding.FragmentProfileBinding
import com.example.chipcycle.models.Users
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater)

        val auth = FirebaseAuth.getInstance()
        val sharedPreferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val state = sharedPreferences?.getString("saved_state",null)

        val currentUser: FirebaseUser? = auth.currentUser
        Log.i("CurrentUser", currentUser?.uid.toString())
        if(currentUser!=null){
            binding.etName.text = currentUser?.displayName.toString()
            binding.etEmail.text = currentUser?.email.toString()
            binding.etLocation.text = state.toString()

        }
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
        }

        binding.btnLogout.setOnClickListener {
            signOut()
        }

        return binding.root
    }

    private fun signOut() {

        val googleSignInClient = GoogleSignIn.getClient(requireContext(), GoogleSignInOptions.DEFAULT_SIGN_IN)
        val auth = FirebaseAuth.getInstance()
        googleSignInClient.signOut().addOnCompleteListener {
            FirebaseAuth.getInstance().signOut()
            Utils.showToast("Logging Out",requireContext())
            val sharedPrefs = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            sharedPrefs?.edit()?.remove("saved_state")?.apply()
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun fetchUserDetails(uid: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(uid)
        Log.i("USERR", "Ref: $databaseRef")


        databaseRef.get()
            .addOnCompleteListener { task ->
                Log.i("EXEC","ee")
                if (task.isSuccessful) {
                    val dataSnapshot = task.result
                    Log.i("RES",dataSnapshot.toString())
                    if (dataSnapshot.exists()) {
                        val user = dataSnapshot.getValue(Users::class.java)
                        Log.i("USER_DATA", "Fetched: $user")
                    } else {
                        Log.w("USER_DATA", "No data exists at path.")
                    }
                } else {
                    Log.e("USER_DATA", "Task failed", task.exception)
                }
            }
    }




}