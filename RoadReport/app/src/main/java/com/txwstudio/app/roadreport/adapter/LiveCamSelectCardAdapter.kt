package com.txwstudio.app.roadreport.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.txwstudio.app.roadreport.databinding.RowLiveCamSelectBinding
import com.txwstudio.app.roadreport.json.DynamicLiveCamSource

class LiveCamSelectCardAdapter :
    ListAdapter<DynamicLiveCamSource, RecyclerView.ViewHolder>(LiveCamDiffCallback()) {

    var camNames = MutableLiveData<String>()
    var streamUrls = MutableLiveData<String>()

    inner class LiveCamSelectCardViewHolder(private val binding: RowLiveCamSelectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.setClickListener {
                binding.dynamicLiveCamSource?.let {
                    changeStreamUrl(it)
                }
            }
        }

        private fun changeStreamUrl(it: DynamicLiveCamSource) {
            Log.i("LiveCamLog", "正在更新串流資料 | ${it.camName} | ${it.url}")
            camNames.value = it.camName
            streamUrls.value = it.url
        }

        fun bind(item: DynamicLiveCamSource) {
            binding.apply {
                dynamicLiveCamSource = item
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

class LiveCamDiffCallback : DiffUtil.ItemCallback<DynamicLiveCamSource>() {

    override fun areItemsTheSame(oldItem: DynamicLiveCamSource, newItem: DynamicLiveCamSource): Boolean {
        return oldItem.camName == newItem.camName
    }

    override fun areContentsTheSame(oldItem: DynamicLiveCamSource, newItem: DynamicLiveCamSource): Boolean {
        return oldItem == newItem
    }

}
