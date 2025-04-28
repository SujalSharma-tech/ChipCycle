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
import com.example.chipcycle.databinding.FragmentRegisterBinding
import com.example.chipcycle.models.Users

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.toolbar.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_getStartedFragment)
        }
        binding.tvSigninBtn.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        binding.ivGoogleBtn.setOnClickListener {
            Utils.showToast("Logging in", requireContext())
            binding.pbLoad.visibility = View.VISIBLE
            binding.ivGoogleBtn.visibility = View.GONE
            val signInIntent = authViewModel.googleSignInClient.signInIntent
            startActivityForResult(signInIntent, 123)
        }


        binding.nextButton.setOnClickListener {
            val email = binding.emailField.text.toString()
            val password = binding.passwordField.text.toString()
            val confirmPassword = binding.confirmPasswordField.text.toString()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Utils.showToast("Please Fill All Fields", requireContext())
            } else if (password != confirmPassword) {
                Utils.showToast("Passwords do not match", requireContext())
            } else if (!binding.cbCheck.isChecked) {
                Utils.showToast("You must agree to terms & conditions", requireContext())
            } else {
                authViewModel.registerUser(email, password)
            }
        }

        observeViewModel()

        return binding.root
    }

    private fun observeViewModel() {
        authViewModel.authStatus.observe(viewLifecycleOwner, Observer { status ->
            Utils.showToast(status, requireContext())
            if (status == "Verification Mail Sent. Please check your email.") {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        })

        authViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.pbLoad.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.ivGoogleBtn.visibility = if (isLoading) View.GONE else View.VISIBLE
        })

        authViewModel.isBtnLoading.observe(viewLifecycleOwner, Observer { isBtnLoading->
            binding.pbBtnload.visibility = if(isBtnLoading) View.VISIBLE else View.GONE
            binding.nextButton.visibility = if(isBtnLoading) View.GONE else View.VISIBLE
        })

        authViewModel.user.observe(viewLifecycleOwner, Observer { user ->
            if (user?.uid != null && user?.uid == Utils.getCurrentUserId() && user.isEmailVerified ) {
                Log.w("EMAILCHECK",user.isEmailVerified.toString())
//                findNavController().navigate(R.id.action_registerFragment_to_homeTestFragment)
                startActivity(Intent(requireActivity(), UsersMainActivity::class.java))
                requireActivity().finish()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            authViewModel.signInWithGoogle(data)
        }
    }
}
