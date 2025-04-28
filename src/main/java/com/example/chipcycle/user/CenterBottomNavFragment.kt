package com.example.chipcycle.user

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chipcycle.databinding.FragmentCenterBottomNavBinding
import com.example.chipcycle.models.LocationByStateData
import com.example.chipcycle.models.LocationResponseCenters
import com.example.chipcycle.viewmodel.CenterViewModel
import com.example.chipcycle.adapter.CenterAdapterNav
import com.example.chipcycle.R

class CenterBottomNavFragment : Fragment() {
    private lateinit var binding : FragmentCenterBottomNavBinding
    private lateinit var centerAdapter: CenterAdapterNav

    private lateinit var centerviewModel: CenterViewModel
    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCenterBottomNavBinding.inflate(inflater)
        centerviewModel = ViewModelProvider(this)[CenterViewModel::class.java]
        setupRecyclerView()

        setupObservers()

        binding.toolbar.setOnClickListener {
            findNavController().navigate(R.id.action_centerBottomNavFragment_to_homeFragment)
        }

        binding.btnSearch.setOnClickListener {
            val state = binding.etSearchLocation.text.toString()
            if(state!=null && state.isNotEmpty()){

                val locationData = LocationByStateData(
                    state=state
                )
                Log.i("STATE",locationData.toString())
                centerviewModel.sendState(locationData)

            }else{
                Toast.makeText(requireContext(),"Please Enter valid state", Toast.LENGTH_SHORT).show()
            }

        }


        return binding.root
    }

    private fun setupRecyclerView() {
        centerAdapter = CenterAdapterNav(emptyList()) { center->
            navigateToMapFragment(center)
        }
        binding.rvCenters.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
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
                binding.rvCenters.visibility = View.INVISIBLE

            } else {
                binding.shimmerViewContainer.stopShimmer()
                binding.shimmerViewContainer.visibility = View.INVISIBLE
                binding.rvCenters.visibility = View.VISIBLE
            }
        }
        centerviewModel.statusMessage.observe(requireActivity()) { message ->
            if (message.isNotEmpty() && progressDialog?.isShowing == true) {
                updateProgressDialogMessage(message)
            }
        }

        centerviewModel.centersLength.observe(requireActivity()) {length ->
            if(length!=null){
                binding.tvResultsCount.text = "Centers Found : ${length.toString()}"
            }
        }

        centerviewModel.errorMessage.observe(requireActivity()) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }

        centerviewModel.responseData.observe(requireActivity()) { responseData ->
            if (responseData.success) {
                Toast.makeText(requireContext(), "Location sent successfully", Toast.LENGTH_SHORT).show()
                // Handle centers list here if needed
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
            findNavController().navigate(R.id.action_centerBottomNavFragment_to_mapFragment, bundle)
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


}