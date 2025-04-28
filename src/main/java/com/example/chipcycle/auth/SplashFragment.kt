package com.example.chipcycle.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.chipcycle.R
import com.example.chipcycle.activity.UsersMainActivity
import com.example.chipcycle.databinding.FragmentSplashBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SplashFragment : Fragment() {

    private lateinit var binding : FragmentSplashBinding
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(layoutInflater)
        changeStatusBarColor()
        Handler(Looper.getMainLooper()).postDelayed({
            val auth = FirebaseAuth.getInstance()
            val currentUser: FirebaseUser? = auth.currentUser
            if(currentUser!=null){

                Log.i("USER","reached here")
                navigatetoHome(currentUser)
            }else {

                findNavController().navigate(R.id.action_splashFragment_to_getStartedFragment)
            } },2000)
        return binding.root
    }

    private fun changeStatusBarColor(){
        activity?.window?.apply {
            val statusBarcolors = ContextCompat.getColor(requireContext(), R.color.backgroundColor)
            statusBarColor = statusBarcolors
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    fun navigatetoHome(currentUser : FirebaseUser){

        if (GoogleSignIn.getLastSignedInAccount(requireContext()) != null ||
            (currentUser != null && currentUser.isEmailVerified)) {
            Log.v("USERR","reached")
//            findNavController().navigate(R.id.action_splashFragment_to_homeTestFragment)
            startActivity(Intent(requireActivity(), UsersMainActivity::class.java))
            requireActivity().finish()
        }else{
            authViewModel.logout()
        }
    }
}