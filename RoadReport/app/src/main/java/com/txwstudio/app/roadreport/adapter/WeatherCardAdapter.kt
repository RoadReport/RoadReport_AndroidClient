package com.txwstudio.app.roadreport.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.txwstudio.app.roadreport.databinding.WeatherDataRowBinding
import com.txwstudio.app.roadreport.model.WeatherData

/**
 * Modify from sunflower sample project
 *
 * @link https://github.com/android/sunflower
 * */
class WeatherCardAdapter :
    ListAdapter<WeatherData, RecyclerView.ViewHolder>(WeatherDiffCallback()) {

    class WeatherViewHolder(private val binding: WeatherDataRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WeatherData) {
            binding.apply {
                weather = item
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return WeatherViewHolder(
            WeatherDataRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val weather = getItem(position)
        (holder as WeatherViewHolder).bind(weather)
    }
}

private class WeatherDiffCallback : DiffUtil.ItemCallback<WeatherData>() {

    override fun areItemsTheSame(oldItem: WeatherData, newItem: WeatherData): Boolean {
        return oldItem.stationId == newItem.stationId
    }

    override fun areContentsTheSame(oldItem: WeatherData, newItem: WeatherData): Boolean {
        return oldItem == newItem
    }
}