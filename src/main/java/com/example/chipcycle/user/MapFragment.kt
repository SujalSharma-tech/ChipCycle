package com.example.chipcycle.user

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.chipcycle.R
import com.example.chipcycle.databinding.FragmentMapBinding
import com.example.chipcycle.models.LocationResponseCenters
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.OnMapsSdkInitializedCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback,  OnMapsSdkInitializedCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private var googleMap: GoogleMap? = null
    private var centerData: LocationResponseCenters? = null

    companion object {
        private const val CENTER_DATA = "center"

        fun newInstance(center: LocationResponseCenters): MapFragment {
            val fragment = MapFragment()
            val args = Bundle()
            args.putParcelable(CENTER_DATA, center)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            Log.i("ONCREATECENTER",CENTER_DATA.toString())
            centerData = it.getParcelable(CENTER_DATA)
        }
    }
    override fun onMapsSdkInitialized(renderer: MapsInitializer.Renderer) {
        when (renderer) {
            MapsInitializer.Renderer.LATEST -> Log.d("MapFragment", "Using latest Maps SDK renderer.")
            MapsInitializer.Renderer.LEGACY -> Log.d("MapFragment", "Using legacy Maps SDK renderer.")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            MapsInitializer.initialize(requireContext(), MapsInitializer.Renderer.LATEST,this)
            Log.d("MapFragment", "Maps initialized")
        } catch (e: Exception) {
            Log.e("MapFragment", "Maps initialization failed: ${e.message}")
            Toast.makeText(requireContext(), "Maps initialization failed", Toast.LENGTH_LONG).show()
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        if (mapFragment == null) {
            Log.e("MapFragment", "Map fragment is null")
            Toast.makeText(requireContext(), "Map fragment not found", Toast.LENGTH_LONG).show()
        } else {
            mapFragment.getMapAsync(this)
            setupUI()
            Log.d("MapFragment", "getMapAsync called")
        }
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupUI() {
        binding.tvCenterName.text = centerData?.centername
        binding.tvAddress.text = centerData?.address
        binding.tvContact.text = if (centerData?.isContactAvailable == true)
            "Contact: ${centerData?.contactonly1}" else "Contact Details Not Available"

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.btnGetDirections.setOnClickListener {
            centerData?.let { center ->
                if (center.lat != null && center.long != null) {
                    val latitude = center.lat.toDouble()
                    val longitude = center.long.toDouble()

                    // Construct the Google Maps Intent for directions
                    val gmmIntentUri =
                        Uri.parse("google.navigation:q=$latitude,$longitude")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    mapIntent.resolveActivity(requireActivity().packageManager)?.let {
                        startActivity(mapIntent)
                    } ?: run {
                        Toast.makeText(
                            requireContext(),
                            "Google Maps app not found on your device.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Center coordinates are not available.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


override fun onMapReady(googleMap: GoogleMap) {

    this.googleMap = googleMap
    googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

    googleMap.uiSettings.isZoomControlsEnabled = true
    googleMap.uiSettings.isCompassEnabled = true


    try {

        centerData?.let { center ->
            Log.i("CENTER",center.toString())
            if (center.lat != null && center.long != null) {
                val centerLocation = LatLng(center.lat.toDouble(), center.long.toDouble())
                Log.i("CLOC",centerLocation.toString())
                googleMap?.addMarker(
                    MarkerOptions()
                        .position(centerLocation)
                        .title(centerData?.centername.toString())
                )
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLocation, 16f))
            }
        }

    } catch (e: Exception) {
        Log.e("MapFragment", "Error setting map location: ${e.message}")
    }
}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}