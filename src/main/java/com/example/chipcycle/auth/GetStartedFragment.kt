package com.example.chipcycle.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chipcycle.R
import com.example.chipcycle.databinding.FragmentGetStartedBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class GetStartedFragment : Fragment() {
    private lateinit var binding : FragmentGetStartedBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentGetStartedBinding.inflate(layoutInflater)
        binding.getstartedbtn.setOnClickListener {
            findNavController().navigate(R.id.action_getStartedFragment_to_registerFragment)
        }
        return binding.root
    }

}