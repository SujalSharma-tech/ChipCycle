//package com.example.chipcycle
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.navigation.fragment.findNavController
//import com.example.chipcycle.databinding.FragmentHomeTestBinding
//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions
//import com.google.firebase.auth.FirebaseAuth
//
//
//class HomeTestFragment : Fragment() {
//
//   private lateinit var binding : FragmentHomeTestBinding
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//
//        binding = FragmentHomeTestBinding.inflate(layoutInflater)
//
//        binding.mbLogoutBtn.setOnClickListener {
//            signOut()
//        }
//        return binding.root
//    }
//
//
//    private fun signOut() {
//
//        val googleSignInClient = GoogleSignIn.getClient(requireContext(), GoogleSignInOptions.DEFAULT_SIGN_IN)
//        val auth = FirebaseAuth.getInstance()
//        googleSignInClient.signOut().addOnCompleteListener {
//            FirebaseAuth.getInstance().signOut()
//            Utils.showToast("Logging Out",requireContext())
//            findNavController().navigate(R.id.action_homeTestFragment_to_registerFragment)
//        }
//    }
//
//
//}