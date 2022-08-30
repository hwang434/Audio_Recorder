package com.example.audiorecorder.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.audiorecorder.R
import com.example.audiorecorder.databinding.FragmentVoiceListBinding
import com.example.audiorecorder.viewmodels.VoiceViewModel

/**
 * A fragment representing a list of Items.
 */
class VoiceListFragment : Fragment() {

    companion object {
        private val TAG: String = "로그"
    }
    private lateinit var binding: FragmentVoiceListBinding
    private val voiceViewModel: VoiceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"VoiceListFragment - onCreate() called")
        super.onCreate(savedInstanceState)
        setObserver()
        getAllVoices()
    }

    private fun setObserver() {
        Log.d(TAG,"VoiceListFragment - setObserver() called")
        voiceViewModel._liveDataOfVoices.observe(this) {
            Log.d(TAG,"VoiceListFragment - setObserver() data is changed")

            //////////////////////////////////////////////
            ///// notifyDataSetChanged는 모든 UI를 새로 그리기 때문에 비효율적이다.
            ////  임시방편으로만 쓰고, Flow 도입과 함께 바꾸자.
            binding.list.adapter?.notifyDataSetChanged()
        }
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
        val adapter = VoiceRecyclerViewAdapter(voiceViewModel._liveDataOfVoices)


        binding.list.adapter = adapter
        binding.list.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun getAllVoices() {
        Log.d(TAG,"VoiceListFragment - getAllVoices() called")
        try {
            voiceViewModel.getAllVoices()
        } catch (e: Exception) {
            Log.w(TAG, "getAllVoices: ", e)
        }
    }
}