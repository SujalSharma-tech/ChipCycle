package com.example.chipcycle.user

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chipcycle.adapter.CenterAdapter
import com.example.chipcycle.databinding.FragmentHomeBinding
import com.example.chipcycle.R
import com.example.chipcycle.models.LocationByStateData
import com.example.chipcycle.models.LocationData
import com.example.chipcycle.models.LocationResponseCenters
import com.example.chipcycle.viewmodel.CenterViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson


class HomeFragment : Fragment() {


    private lateinit var binding : FragmentHomeBinding
    private lateinit var centerAdapter: CenterAdapter

    private lateinit var centerviewModel: CenterViewModel
    private var progressDialog: ProgressDialog? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)

        changeStatusBarColor()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        centerviewModel = ViewModelProvider(this)[CenterViewModel::class.java]


        val sharedPreferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val json = sharedPreferences?.getString("location_data", null)
        val state = sharedPreferences?.getString("saved_state",null)

        val locationData: LocationData? = if (json != null) {
            Gson().fromJson(json, LocationData::class.java)
        } else {
            null
        }
        if(locationData?.state!=null){
            centerviewModel.sendLocationToApi(locationData)
        }else if(state!=null && state.isNotEmpty()){
            val locationData  = LocationByStateData(
                    state=state
            )
            centerviewModel.sendState(locationData)
        }


        val appNameTextView = binding.appName

        val paint = appNameTextView.paint
        val width = paint.measureText(appNameTextView.text.toString())

        val textShader = LinearGradient(
            0f, 0f, width, appNameTextView.textSize,
            intArrayOf(
                Color.parseColor("#8A2BE2"),  // Violet
                Color.parseColor("#4B0082")   // Dark Violet
            ),
            null,
            Shader.TileMode.CLAMP
        )

        appNameTextView.paint.shader = textShader

        val bellIcon = binding.bellIcon
        val profilePicture = binding.profilePicture




        setupRecyclerView()

        setupObservers()


        bellIcon.setOnClickListener {
            Toast.makeText(requireContext(), "Notifications Clicked", Toast.LENGTH_SHORT).show()
        }

        profilePicture.setOnClickListener {
            Toast.makeText(requireContext(), "Profile Clicked", Toast.LENGTH_SHORT).show()
        }
        val bottomNavigationView = binding.bottomNavigation

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(requireContext(), "Home clicked", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.nav_list -> {
                    findNavController().navigate(R.id.action_homeFragment_to_centerBottomNavFragment)
                    true
                }
                R.id.nav_guide -> {
                    findNavController().navigate(R.id.action_homeFragment_to_guideFragment)
                    true
                }
                R.id.nav_profile -> {
                    findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
                    true
                }
                else -> false
            }
        }

        val tipText = binding.tipText


        val tips = listOf(
            "Always remove batteries before recycling electronics.",
            "Old smartphones can be donated to schools or NGOs.",
            "Proper e-waste disposal prevents harmful chemicals leaking.",
            "Repurpose old devices before discarding.",
            "Always wipe your data before recycling gadgets."
        )

        tipText.text = tips.random()


        binding.profilePicture.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }

        binding.iGuide1.root.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.recyclenow.com/recycle-an-item/mobile-phones"))
            startActivity(intent)
        }

        binding.iGuide2.root.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.recyclenow.com/recycle-an-item/computers"))
            startActivity(intent)

        }

        return binding.root
    }

    private fun setupRecyclerView() {

        centerAdapter = CenterAdapter(emptyList()) { center ->
            navigateToMapFragment(center)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
            adapter = centerAdapter
        }
    }

    private fun setupObservers() {
        centerviewModel.centerList.observe(requireActivity()) { center ->
            centerAdapter.updateData(center)
        }

        centerviewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading) {
                binding.shimmerViewContainer.startShimmer()
                binding.shimmerViewContainer.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.INVISIBLE

            } else {
               binding.shimmerViewContainer.stopShimmer()
                binding.shimmerViewContainer.visibility = View.INVISIBLE
                binding.recyclerView.visibility = View.VISIBLE
            }
        }
        centerviewModel.statusMessage.observe(requireActivity()) { message ->
            if (message.isNotEmpty() && progressDialog?.isShowing == true) {
                updateProgressDialogMessage(message)
            }
        }

        centerviewModel.errorMessage.observe(requireActivity()) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }

        centerviewModel.responseData.observe(requireActivity()) { responseData ->
            if (responseData.success) {
//                Toast.makeText(requireContext(), "Location sent successfully", Toast.LENGTH_SHORT).show()

            }
        }
    }
    private fun navigateToMapFragment(center: LocationResponseCenters) {

        Log.i("Nav","${center.lat.toString()} and ${center.long.toString()}")
        if (center.lat == null || center.long == null) {

            return
        }

        try {

            val bundle = Bundle().apply {
                putParcelable("center", center)
            }
            Log.i("Navigate","Yes")
            findNavController().navigate(R.id.action_homeFragment_to_mapFragment, bundle)
        } catch (e: Exception) {

            Log.e("Navigation", "Navigation failed: ${e.message}")
            val mapFragment = MapFragment.newInstance(center)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mapFragment)
                .addToBackStack("map")
                .commit()
        }
    }

    private fun showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(requireContext()).apply {
                setMessage("Please Wait")
                setCancelable(false)
            }
        }
        progressDialog?.show()
    }




    private fun updateProgressDialogMessage(message: String) {
        progressDialog?.setMessage(message)
    }

    private fun showProgressDialog(message: String) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(requireContext()).apply {
                setMessage(message)
                setCancelable(false)
                show()
            }
        } else {
            progressDialog?.setMessage(message)
            if (!progressDialog!!.isShowing) progressDialog?.show()
        }
    }



    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissProgressDialog()
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


}