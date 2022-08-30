package com.example.audiorecorder.views

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.audiorecorder.R
import com.example.audiorecorder.databinding.FragmentVoiceItemBinding
import com.example.audiorecorder.dto.Voice

class VoiceRecyclerViewAdapter(
    private val values: List<Voice> = listOf(Voice("파일명 1", "작성자"), Voice("파일명 2", "작성자"))
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
        Log.d(TAG,"VoiceRecyclerViewAdapter - values[position] : ${values[position]}")
        val item = values[position]
        holder.binding.textviewNameOfVoice.text = item.fileName
    }

    override fun getItemCount(): Int {
        Log.d(TAG,"VoiceRecyclerViewAdapterVoiceRecyclerViewAdapter - getItemCount() size : ${values.size}called")
        return values.size
    }

    inner class ViewHolder(val binding: FragmentVoiceItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
//            binding.root.
        }
    }
}