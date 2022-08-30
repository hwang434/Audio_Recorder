package com.example.audiorecorder.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.audiorecorder.R
import com.example.audiorecorder.databinding.FragmentVoiceListBinding
import com.example.audiorecorder.dto.Voice

/**
 * A fragment representing a list of Items.
 */
class VoiceListFragment : Fragment() {

    companion object {
        private val TAG: String = "로그"
    }
    private lateinit var binding: FragmentVoiceListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"VoiceListFragment - onCreate() called")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG,"VoiceListFragment - onCreateView() called")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_voice_list, container, false)
        setRecyclerView()

        return binding.root
    }

    private fun setRecyclerView() {
        Log.d(TAG,"VoiceListFragment - setRecyclerView() called")
        val adapter = VoiceRecyclerViewAdapter(listOf(Voice(), Voice()))
        binding.list.adapter = adapter
        binding.list.layoutManager = LinearLayoutManager(requireContext())
    }
}