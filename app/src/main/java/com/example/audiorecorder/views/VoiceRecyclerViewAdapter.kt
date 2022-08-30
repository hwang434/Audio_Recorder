package com.example.audiorecorder.views

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import com.example.audiorecorder.R
import com.example.audiorecorder.databinding.FragmentVoiceItemBinding
import com.example.audiorecorder.dto.Voice

class VoiceRecyclerViewAdapter(
    private val livedataOfVoices: LiveData<List<Voice>>
) : RecyclerView.Adapter<VoiceRecyclerViewAdapter.ViewHolder>() {
    private val TAG: String = "로그"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d(TAG,"VoiceRecyclerViewAdapter - onCreateViewHolder() called")
        val binding = DataBindingUtil.inflate<FragmentVoiceItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.fragment_voice_item, parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG,"VoiceRecyclerViewAdapter - onBindViewHolder() called")
        val item = livedataOfVoices.value?.get(position)
        holder.binding.textviewNameOfVoice.text = item?.fileName
    }

    override fun getItemCount(): Int {
        return livedataOfVoices.value?.size ?: 0
    }

    inner class ViewHolder(val binding: FragmentVoiceItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
//            binding.root.
        }
    }
}