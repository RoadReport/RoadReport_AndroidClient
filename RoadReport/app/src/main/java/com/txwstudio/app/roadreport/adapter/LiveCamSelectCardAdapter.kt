package com.txwstudio.app.roadreport.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.txwstudio.app.roadreport.databinding.RowLiveCamSelectBinding
import com.txwstudio.app.roadreport.model.LiveCamSource

class LiveCamSelectCardAdapter :
    ListAdapter<LiveCamSource, RecyclerView.ViewHolder>(LiveCamDiffCallback()) {

    companion object {
        var camName = MutableLiveData<String>()
        var streamUrl = MutableLiveData<String>()
    }

    var camNames = MutableLiveData<String>()
    var streamUrls = MutableLiveData<String>()

    inner class LiveCamSelectCardViewHolder(private val binding: RowLiveCamSelectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.setClickListener {
                binding.liveCamSource?.let {
                    changeStreamUrl(it)
                }
            }
        }

        private fun changeStreamUrl(it: LiveCamSource) {
            Log.i("LiveCamLog", "正在更新串流資料 | ${it.camName} | ${it.url}")
            camNames.value = it.camName
            streamUrls.value = it.url
        }

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
