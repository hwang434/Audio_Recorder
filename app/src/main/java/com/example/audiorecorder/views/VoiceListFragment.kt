package com.example.audiorecorder.views

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.audiorecorder.R
import com.example.audiorecorder.databinding.FragmentVoiceListBinding
import com.example.audiorecorder.viewmodels.VoiceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VoiceListFragment : Fragment(), MediaPlayer.OnPreparedListener {

    companion object {
        private val TAG: String = "로그"
    }
    private lateinit var binding: FragmentVoiceListBinding
    private lateinit var mediaPlayer: MediaPlayer
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
        val adapter = VoiceRecyclerViewAdapter(voiceViewModel._liveDataOfVoices) {
            startVoice(Uri.parse(it))
        }

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

    private fun startVoice(uri: Uri) {
        Log.d(TAG,"VoiceListFragment - startVoice() called")
        // uri를 가져오는 메소드와 음악을 재생하는 메소드를 분리시키자.
        try {
            voiceViewModel.viewModelScope.launch(Dispatchers.IO) {
                val decodeUri = Uri.parse(Uri.decode(uri.toString()))
                val downloadLink = voiceViewModel.getDownloadLinkOfVoice(decodeUri)
                Log.d(TAG,"VoiceListFragment - downloadLink : $downloadLink")
                startMediaPlayer(downloadLink)
            }
        } catch (e: Exception) {
            Log.w(TAG, "startVoice: ", e)
        }
    }

    private fun startMediaPlayer(uri: Uri) {
        Log.d(TAG,"VoiceListFragment - startMediaPlayer() called")
        mediaPlayer = MediaPlayer()
        mediaPlayer.apply {
            setDataSource(uri.toString())
            setOnPreparedListener(this@VoiceListFragment)
            prepareAsync()
        }
    }

    override fun onPrepared(p0: MediaPlayer?) {
        Log.d(TAG,"VoiceListFragment - onPrepared() called")
        mediaPlayer.start()
    }
}