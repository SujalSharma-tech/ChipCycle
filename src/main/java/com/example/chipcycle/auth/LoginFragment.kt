package com.example.chipcycle.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.chipcycle.R
import com.example.chipcycle.Utils
import com.example.chipcycle.activity.UsersMainActivity
import com.example.chipcycle.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import kotlin.getValue

class LoginFragment : Fragment() {

    private lateinit var binding : FragmentLoginBinding
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(layoutInflater)
        binding.toolbar.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.loginBtn.setOnClickListener {
            val email = binding.emailField.text.toString()
            val password = binding.passwordField.text.toString()
            if(email.isEmpty() || password.isEmpty()){
                Utils.showToast("Please enter your Email and Password.",requireContext())
            }else {
                authViewModel.loginUser(email, password)
            }
        }
        observeModel()
        return binding.root
    }

    private fun observeModel(){

        authViewModel.user.observe(viewLifecycleOwner, Observer { user ->
            Log.i("USER",user?.uid.toString())
            if (user?.uid != null && user.uid == Utils.getCurrentUserId() && user.isEmailVerified) {
                if (findNavController().currentDestination?.id == R.id.loginFragment) {
//                    findNavController().navigate(R.id.action_loginFragment_to_homeTestFragment)
                    startActivity(Intent(requireActivity(), UsersMainActivity::class.java))
                    requireActivity().finish()
                }
            }
        })

        authViewModel.authStatus.observe(viewLifecycleOwner, Observer { status ->
            Utils.showToast(status, requireContext())
        })

        authViewModel.isBtnLoading.observe(viewLifecycleOwner, Observer { isBtnLoading ->
            binding.loginBtn.visibility = if(isBtnLoading) View.GONE else View.VISIBLE
            binding.pbLoginbtn.visibility = if(isBtnLoading) View.VISIBLE else View.GONE

        })


    }


}