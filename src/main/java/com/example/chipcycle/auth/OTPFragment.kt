package com.example.chipcycle.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chipcycle.R
import com.example.chipcycle.Utils
import com.example.chipcycle.databinding.FragmentOTPBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class OTPFragment : Fragment() {

    private lateinit var binding : FragmentOTPBinding
    private lateinit var userEmail : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOTPBinding.inflate(layoutInflater)
        binding.btnLogout.setOnClickListener {
            signOut()
        }

        getUserEmail()
        customizingAutoFocus()
        onBackButtonClicked()
        return binding.root
    }

    private fun onBackButtonClicked() {
        binding.toolbarotp.setNavigationIcon(R.drawable.baseline_arrow_back_24)

        binding.toolbarotp.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_OTPFragment_to_registerFragment)
        }
    }

    private fun customizingAutoFocus() {
        var listEditText = arrayOf(binding.etOtp1,binding.etOtp2,binding.etOtp3,binding.etOtp4,binding.etOtp5,binding.etOtp6)

        for(i in listEditText.indices){
            listEditText[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int
                ) {



                }

                override fun onTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int
                ) {

                }

                override fun afterTextChanged(textbox: Editable?) {
                    if(textbox?.length==1){
                        if(i < listEditText.size - 1){
                            listEditText[i+1].requestFocus()
                        }
                    }
                    else if(textbox?.length==0){
                        if(i>0){
                            listEditText[i-1].requestFocus()
                        }
                    }

                }
            } )
        }
    }

    private fun getUserEmail() {
        var bundle = arguments
        userEmail = bundle?.getString("email").toString()
        binding.tvEmail.text = userEmail
    }

    private fun signOut() {

        val googleSignInClient = GoogleSignIn.getClient(requireContext(), GoogleSignInOptions.DEFAULT_SIGN_IN)
        val auth = FirebaseAuth.getInstance()
        googleSignInClient.signOut().addOnCompleteListener {
            FirebaseAuth.getInstance().signOut()
            Utils.showToast("Logging Out",requireContext())
            findNavController().navigate(R.id.action_OTPFragment_to_registerFragment)
        }
    }



}