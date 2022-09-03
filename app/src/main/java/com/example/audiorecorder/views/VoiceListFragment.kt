package com.example.audiorecorder.views

import android.content.Intent
import android.net.Uri
import android.os.Build
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
import com.example.audiorecorder.adapter.VoiceRecyclerViewAdapter
import com.example.audiorecorder.databinding.FragmentVoiceListBinding
import com.example.audiorecorder.service.PlayerService
import com.example.audiorecorder.utils.Resource
import com.example.audiorecorder.viewmodels.VoiceViewModel

class VoiceListFragment : Fragment() {

    companion object {
        private const val TAG: String = "로그"
    }
    private lateinit var binding: FragmentVoiceListBinding
    private val voiceViewModel: VoiceViewModel by viewModels()
    private lateinit var playerIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"VoiceListFragment - onCreate() called")
        super.onCreate(savedInstanceState)
        setObserver()
        refreshListOfVoice()
        setPlayerIntent()
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

    private fun setPlayerIntent() {
        playerIntent = Intent(requireContext(), PlayerService::class.java)
    }

    private fun setObserver() {
        Log.d(TAG,"VoiceListFragment - setObserver() called")
        voiceViewModel.voices.observe(this) {
            Log.d(TAG,"VoiceListFragment - setObserver() data is changed")
            binding.list.adapter?.notifyDataSetChanged()
        }

        voiceViewModel.linkOfVoice.observe(this) { resource ->
            Log.d(TAG,"VoiceListFragment - linkOfVoice Changed() called")
            when (resource) {
                is Resource.Success -> {
                    resource.data?.fileName?.let { startMediaPlayerService(title = it, uri = Uri.parse(
                        resource.data.uri
                    )) }
                }
                is Resource.Loading -> {

                }
                is Resource.Error -> {

                }
            }
        }
    }

    override fun onDestroy() {
        Log.d(TAG,"VoiceListFragment - onDestroy() called")
        super.onDestroy()
    }

    private fun setRecyclerView() {
        Log.d(TAG,"VoiceListFragment - setRecyclerView() called")
        val adapter = VoiceRecyclerViewAdapter(voiceViewModel.voices) { title, uri ->
            refreshLinkOfVoice(title, Uri.parse(uri))
        }

        binding.list.adapter = adapter
        binding.list.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun refreshListOfVoice() {
        Log.d(TAG,"VoiceListFragment - refreshListOfVoice() called")
        voiceViewModel.getAllVoices()
    }

    private fun refreshLinkOfVoice(title: String, uri: Uri) {
        Log.d(TAG,"VoiceListFragment - refreshLinkOfVoice() called")
        // uri 를 가져오는 메소드와 음악을 재생하는 메소드를 분리시키자.
        val decodeUri = Uri.parse(Uri.decode(uri.toString()))
        voiceViewModel.refreshLinkOfVoice(title, decodeUri)
    }

    private fun startMediaPlayerService(title: String, uri: Uri) {
        Log.d(TAG,"VoiceListFragment - startMediaPlayer() called")
        playerIntent.putExtra("uri", uri.toString())
        playerIntent.putExtra("title", title)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().startForegroundService(playerIntent)
        } else {
            requireActivity().startService(playerIntent)
        }
    }
}