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

class MainFragment : Fragment() {

    companion object {
        private const val TAG: String = "로그"
        fun newInstance() = MainFragment()
    }

    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var recorder: MediaRecorder
    private lateinit var directoryOfVoice: String

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"MainFragment - onCreate() called")
        super.onCreate(savedInstanceState)

        viewModel = MainViewModel()
        directoryOfVoice = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
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
            if (!checkPermission()) {
                return@setOnClickListener
            }

            startRecord()
            binding.buttonRecordVoiceButton.visibility = View.GONE
            binding.buttonStopRecord.visibility = View.VISIBLE
        }

        // 녹음 종료
        binding.buttonStopRecord.setOnClickListener {
            stopRecord()
            binding.buttonStopRecord.visibility = View.GONE
            binding.buttonRecordVoiceButton.visibility = View.VISIBLE
        }

        // 녹음 파일 업로드
        binding.buttonUploadVoice.setOnClickListener {
            uploadVoice()
        }
    }

    private fun checkPermission(): Boolean {
        Log.d(TAG,"MainFragment - checkPermission() called")
        // if : 권한 요청하면 return false
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),  arrayOf(Manifest.permission.RECORD_AUDIO), 0)
            return false
        }

        // 이미 권한을 요청했으므로 true
        return true
    }

    // 녹음 시작
    private fun startRecord() {
        Log.d(TAG,"MainFragment - startRecord() called")
        // if : 권한 부여 안했으면 -> 녹음 안함.
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(requireContext())
        } else {
            MediaRecorder()
        }

        // 저장 될 파일 명
        val nameOfFile = "/" + System.currentTimeMillis() + ".3gp"
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFile(directoryOfVoice + nameOfFile)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

        Log.d(TAG,"MainFragment - 파일 이름 ${directoryOfVoice + nameOfFile}")

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

        try {
            recorder.reset()
            recorder.release()
        } catch (e: UninitializedPropertyAccessException) {
            Log.w(TAG, "stopRecord: recorder가 아직 초기화되지 않음", e)
        } catch (e: Exception) {
            Log.w(TAG, "stopRecord: ", e)
        }
    }

    private fun uploadVoice() {
        Log.d(TAG,"MainFragment - uploadVoice() called")
        viewModel.uploadVoice()
    }
}