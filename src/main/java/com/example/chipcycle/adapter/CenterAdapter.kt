package com.example.chipcycle.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chipcycle.databinding.CardCentreBinding
import com.example.chipcycle.models.LocationResponseCenters

class CenterAdapter(
    private var centerList: List<LocationResponseCenters>,
    private val onCenterClicked: (LocationResponseCenters) -> Unit
) : RecyclerView.Adapter<CenterAdapter.CenterViewHolder>() {

    class CenterViewHolder(private val binding: CardCentreBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(center: LocationResponseCenters, onCenterClicked: (LocationResponseCenters) -> Unit) {
            binding.tvCenterName.text = center.centername
            binding.tvState.text = center.state
            binding.tvAddress.text = center.address
            if(center.isContactAvailable && center.contactonly1 != null) {
                binding.tvContact.text = "Contact: ${center.contactonly1.toString()}"
            } else {
                binding.tvContact.text = "Contact Details Not Available"
            }
            if (center.isContactAvailable == true && !center.contactonly1.toString().isNullOrEmpty()) {
                binding.callButton.visibility = View.VISIBLE
                binding.callButton.setOnClickListener {
                    val phoneNumber = center.contactonly1
                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                    binding.root.context.startActivity(dialIntent)
                }
            } else {
                binding.callButton.visibility = View.GONE
                binding.callButton.setOnClickListener(null)
            }

            binding.root.setOnClickListener {
                onCenterClicked(center)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CenterViewHolder {
        val binding = CardCentreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CenterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CenterViewHolder, position: Int) {
        holder.bind(centerList[position], onCenterClicked)
    }

    override fun getItemCount(): Int = centerList.size

    fun updateData(newCenterList: List<LocationResponseCenters>) {
        centerList = newCenterList
        notifyDataSetChanged()
    }
}