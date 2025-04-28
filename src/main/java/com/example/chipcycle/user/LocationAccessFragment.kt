package com.example.chipcycle.user

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.chipcycle.adapter.CenterAdapter
import com.example.chipcycle.R
import com.example.chipcycle.databinding.FragmentLocationAccessBinding
import com.example.chipcycle.models.LocationData
import com.example.chipcycle.viewmodel.CenterViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.gson.Gson
import java.util.Locale


class LocationAccessFragment : Fragment() {

    private lateinit var binding : FragmentLocationAccessBinding
    private lateinit var centerAdapter: CenterAdapter

    private lateinit var centerviewModel: CenterViewModel
    private var progressDialog: ProgressDialog? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentLocationAccessBinding.inflate(layoutInflater)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        centerviewModel = ViewModelProvider(this)[CenterViewModel::class.java]

        val sharedPref = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val savedState = sharedPref?.getString("saved_state", null)

        if (savedState != null) {
            findNavController().navigate(R.id.action_locationAccessFragment_to_homeFragment2)

        }

        binding.buttonDetect?.setOnClickListener {
            checkGpsEnabled()
        }

        binding.buttonProceed.setOnClickListener {
            if(binding.autoCompleteState.text.isNotEmpty()){
                val sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                sharedPref.edit().putString("saved_state", binding.autoCompleteState.text.toString()).apply()
                findNavController().navigate(R.id.action_locationAccessFragment_to_homeFragment2)
            }
        }


        return binding.root
    }

    private fun checkGpsEnabled() {
        val locationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPS is not enabled, show dialog to enable it
            AlertDialog.Builder(requireContext())
                .setTitle("GPS Required")
                .setMessage("Please enable GPS to get your current location")
                .setPositiveButton("Open Settings") { _, _ ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .setNegativeButton("Cancel", null)
                .show()
            return
        }

        // GPS is enabled, proceed with location request
        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        when {
            // If we have permission, proceed
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED -> {
                fetchLocation()
            }

            // If we should show rationale
            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // Show explanation if needed
                Toast.makeText(requireContext(), "Location permission is needed to show your current location",
                    Toast.LENGTH_LONG).show()
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }

            // Otherwise just request
            else -> {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun fetchLocation() {
        try {
            showProgressDialog("Getting location...")

            // Add timeout handler
            val timeoutHandler = Handler(Looper.getMainLooper())
            val timeoutRunnable = Runnable {
                dismissProgressDialog()
                Toast.makeText(requireContext(), "Location request timed out. Please check your GPS settings.",
                    Toast.LENGTH_LONG).show()
            }
            // Set 15 seconds timeout
            timeoutHandler.postDelayed(timeoutRunnable, 15000)

            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(5000)
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    // Cancel timeout
                    timeoutHandler.removeCallbacks(timeoutRunnable)

                    locationResult.lastLocation?.let { location ->
                        fusedLocationClient.removeLocationUpdates(this)
                        val latitude = location.latitude
                        val longitude = location.longitude
                        getAddressFromLocation(latitude, longitude)
//                    sendLocationToApi(latitude, longitude)
                    } ?: run {
                        dismissProgressDialog()
                        Toast.makeText(requireContext(), "Location unavailable. Please check your GPS settings.",
                            Toast.LENGTH_LONG).show()
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

            // Try to get last location as backup
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        // Cancel timeout
                        timeoutHandler.removeCallbacks(timeoutRunnable)

                        val latitude = location.latitude
                        val longitude = location.longitude
                        Log.i("LAST LOC","Recieved Last loc")
                        getAddressFromLocation(latitude,longitude)
//                    sendLocationToApi(latitude, longitude)
                        fusedLocationClient.removeLocationUpdates(locationCallback)
                    }
                }

        } catch (e: SecurityException) {
            dismissProgressDialog()
            Toast.makeText(requireContext(), "Location permission needed", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        // Show progress while fetching address
        Log.i("Location","Reached Get Address from latlon")

        showProgressDialog("Getting address information...")

        try {
            // Create geocoder
            val geocoder = Geocoder(requireContext(), Locale.getDefault())

            // For Android API level 33 (Tiramisu) and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Use the new callback approach
                geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                    processAddresses(addresses)
                }
            } else {
                // For older Android versions
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                processAddresses(addresses)
            }
        } catch (e: Exception) {
            dismissProgressDialog()
            Toast.makeText(requireContext(), "Error getting address: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun processAddresses(addresses: List<Address>?) {
        // Hide progress
        requireActivity().runOnUiThread {

            dismissProgressDialog()

            if (addresses.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Couldn't get address information", Toast.LENGTH_SHORT).show()
                return@runOnUiThread
            }

            val address = addresses[0]
            val state = address.adminArea           // State name
            val country = address.countryName       // Country name
            val postalCode = address.postalCode     // Postal/ZIP code
            val locality = address.locality         // City name

            // Display the state or send it to your API
            Toast.makeText(requireContext(), "State: $state", Toast.LENGTH_SHORT).show()

            // Create a data object with the address components
            val locationData = LocationData(
                latitude = address.latitude,
                longitude = address.longitude,
                state = state ?: "",
                country = country ?: "",
                city = locality ?: "",
                zipCode = postalCode ?: ""
            )

            // Send this data to your API
            centerviewModel.sendLocationToApi(locationData)
            val sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            sharedPref.edit().putString("saved_state", state.toString()).apply()
            val sharedPreferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()

            val gson = Gson()
            val json = gson.toJson(locationData)

            editor?.putString("location_data", json)
            editor?.apply()
            findNavController().navigate(R.id.action_locationAccessFragment_to_homeFragment2)

        }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get location
                getCurrentLocation()
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissProgressDialog()
    }


}