package com.txwstudio.app.roadreport.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.txwstudio.app.roadreport.databinding.RowLiveCamSelectBinding
import com.txwstudio.app.roadreport.model.LiveCamSource

class LiveCamSelectCardAdapter :
    ListAdapter<LiveCamSource, RecyclerView.ViewHolder>(LiveCamDiffCallback()) {

    class LiveCamSelectCardViewHolder(private val binding: RowLiveCamSelectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LiveCamSource) {
            binding.apply {
                liveCamSource = item
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LiveCamSelectCardViewHolder(
            RowLiveCamSelectBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val liveCamSource = getItem(position)
        (holder as LiveCamSelectCardViewHolder).bind(liveCamSource)
    }


}

class LiveCamDiffCallback : DiffUtil.ItemCallback<LiveCamSource>() {
    override fun areItemsTheSame(oldItem: LiveCamSource, newItem: LiveCamSource): Boolean {
        return oldItem.camName == newItem.camName
    }

    override fun areContentsTheSame(oldItem: LiveCamSource, newItem: LiveCamSource): Boolean {
        return oldItem == newItem
    }

}
