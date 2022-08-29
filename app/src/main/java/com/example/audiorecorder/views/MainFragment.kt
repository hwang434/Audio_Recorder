package com.example.audiorecorder.views

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.audiorecorder.R
import com.example.audiorecorder.databinding.FragmentMainBinding
import com.example.audiorecorder.ui.main.MainViewModel
import java.io.File

class MainFragment : Fragment() {

    companion object {
        private const val TAG: String = "로그"
        fun newInstance() = MainFragment()
    }

    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var recorder: MediaRecorder

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"MainFragment - onCreate() called")
        super.onCreate(savedInstanceState)
        viewModel = MainViewModel()
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(requireContext())
        } else {
            MediaRecorder()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG,"MainFragment - onCreateView() called")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        setEvent()
        return binding.root
    }

    private fun setEvent() {
        Log.d(TAG,"MainFragment - setEvent() called")

        // 녹음 시작
        binding.buttonRecordVoiceButton.setOnClickListener {
            startRecord()
        }

        // 녹음 일시 정지
        binding.buttonPauseRecord.setOnClickListener {
            pauseRecord()
        }

        // 녹음 정지
        binding.buttonStopRecord.setOnClickListener {
            stopRecord()
        }
    }

    // 녹음 시작
    private fun startRecord() {
        Log.d(TAG,"MainFragment - startRecord() called")
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),  arrayOf(Manifest.permission.RECORD_AUDIO), 0)
            return
        }

        val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_DCIM).toString() + "/" + System.currentTimeMillis() + ".mp3";
        } else {
            Environment.getExternalStorageDirectory().toString() + "/" + System.currentTimeMillis() + ".mp3";
        }
        recorder = MediaRecorder()
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFile(file)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

        try {
            recorder.prepare()
        } catch (e: Exception) {
            Log.w(TAG, "startRecord: ", e)
            Toast.makeText(requireContext(), "레코더 준비 실패", Toast.LENGTH_SHORT).show()
            return
        }

        recorder.start()
    }

    // 정지 = 아예 정지하고 업로드함.
    private fun stopRecord() {
        Log.d(TAG,"MainFragment - stopRecord() called")
        recorder.stop()
        recorder.release()
    }

    // 녹음 일시 정지
    private fun pauseRecord() {
        Log.d(TAG,"MainFragment - pauseRecord() called")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            recorder.pause()
        } else {
            recorder.stop()
        }
    }
}