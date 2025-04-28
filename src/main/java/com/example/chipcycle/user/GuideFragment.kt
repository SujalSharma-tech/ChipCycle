package com.example.chipcycle.user

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chipcycle.adapter.GuideAdapter
import com.example.chipcycle.adapter.PopularGuideAdapter
import com.example.chipcycle.R
import com.example.chipcycle.databinding.FragmentGuideBinding
import com.example.chipcycle.models.Guide
import com.example.chipcycle.models.PopularGuide

class GuideFragment : Fragment() {

    private var _binding: FragmentGuideBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGuideBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_guideFragment_to_homeFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainGuides = listOf(
            Guide(1, "Computers & Laptops", R.drawable.ic_computer, "https://www.recyclenow.com/recycle-an-item/computers"),
            Guide(2, "Mobile Devices", R.drawable.ic_mobile, "https://www.recyclenow.com/recycle-an-item/mobile-phones"),
            Guide(3, "Home Appliances", R.drawable.ic_bulb2, "https://www.recyclenow.com/recycle-an-item/electrical-items"),
            Guide(4, "Batteries & Power", R.drawable.ic_battery, "https://www.recyclenow.com/recycle-an-item/batteries"),
            Guide(5, "Peripherals", R.drawable.ic_pheripheral, "https://www.recyclenow.com/recycle-an-item/electrical-items")
        )

        binding.prepChecklist.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://recyclemyelectronics.ca/bc/how-to-prepare-your-device"))
            startActivity(intent)
        }


        val popularGuides = listOf(
            PopularGuide(101, "Smartphone Recycling", "Safe disposal of mobile devices", 8),
            PopularGuide(102, "Battery Disposal", "Proper handling of used batteries", 5)
        )

        binding.guidesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        val guideAdapter = GuideAdapter(mainGuides)
        binding.guidesRecyclerView.adapter = guideAdapter

        binding.popularGuidesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        val popularGuideAdapter = PopularGuideAdapter(popularGuides) { popularGuide ->

            Toast.makeText(requireContext(), "Read: ${popularGuide.title}, Steps: ${popularGuide.steps_count}",
                Toast.LENGTH_SHORT).show()
        }
        binding.popularGuidesRecyclerView.adapter = popularGuideAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}