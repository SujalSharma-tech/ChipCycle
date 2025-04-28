package com.example.chipcycle.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chipcycle.R
import com.example.chipcycle.models.PopularGuide

class PopularGuideAdapter(
    private val popularGuides: List<PopularGuide>,
    private val onItemClick: (PopularGuide) -> Unit
) : RecyclerView.Adapter<PopularGuideAdapter.PopularGuideViewHolder>() {

    inner class PopularGuideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.popularGuideTitleTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.popularGuideDescriptionTextView)
        val readButton: TextView = itemView.findViewById(R.id.popularGuideReadButton)

        init {
            readButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(popularGuides[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularGuideViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.popular_guide_item, parent, false)
        return PopularGuideViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PopularGuideViewHolder, position: Int) {
        val currentGuide = popularGuides[position]
        holder.titleTextView.text = currentGuide.title
        holder.descriptionTextView.text = currentGuide.description
    }

    override fun getItemCount() = popularGuides.size
}