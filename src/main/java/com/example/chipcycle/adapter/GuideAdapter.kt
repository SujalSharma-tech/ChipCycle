package com.example.chipcycle.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chipcycle.R
import com.example.chipcycle.models.Guide

class GuideAdapter(private val guides: List<Guide>) :
    RecyclerView.Adapter<GuideAdapter.GuideViewHolder>() {

    inner class GuideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.guideIconImageView)
        val titleTextView: TextView = itemView.findViewById(R.id.guideTitleTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val currentGuide = guides[position]
                    openLinkInBrowser(currentGuide.link)
                }
            }
        }

        private fun openLinkInBrowser(url: String) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            itemView.context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.guide_card_layout, parent, false)
        return GuideViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GuideViewHolder, position: Int) {
        val currentGuide = guides[position]
        holder.titleTextView.text = currentGuide.title
        holder.iconImageView.setImageResource(currentGuide.iconResourceId)
    }

    override fun getItemCount() = guides.size
}