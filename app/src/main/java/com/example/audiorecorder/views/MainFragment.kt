package com.example.audiorecorder.views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.audiorecorder.R
import com.example.audiorecorder.databinding.FragmentMainBinding
import com.example.audiorecorder.viewmodels.VoiceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment() {

    companion object {
        private const val TAG: String = "로그"
        fun newInstance() = MainFragment()
    }

    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: VoiceViewModel
    private lateinit var recorder: MediaRecorder
    private lateinit var directoryOfVoice: String
    private lateinit var intentOfPickAudio: ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"MainFragment - onCreate() called")
        super.onCreate(savedInstanceState)

        // 뷰모델 초기화
        viewModel = VoiceViewModel()
        // 음성파일 저장할 공간
        directoryOfVoice = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        registerIntent()
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
            getAudioURI()
        }

        // 녹음 파일 목록으로 이동
        binding.buttonToVoiceList.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_voiceListFragment)
        }
    }

    // 권한 있는지 확인
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
        val nameOfFile = "/" + SimpleDateFormat("MM월 dd일 HH시 MM분 ss초", Locale.KOREA).format(System.currentTimeMillis()) + ".mp3"
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

    // 정지 = 아예 정지하고 파일을 저장함.
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

    // 올릴 오디오 파일에 URI를 조회하는 기능.
    private fun getAudioURI() {
        Log.d(TAG,"MainFragment - uploadVoice() called")
        intentOfPickAudio.launch("audio/*")
    }

    // 오디오 파일 업로드
    private fun uploadVoice(uri: Uri) {
        try {
            viewModel.viewModelScope.launch(Dispatchers.IO) {
                viewModel.uploadVoice(uri)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "오디오 파일 업로드에 성공했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "uploadVoice: ", e)
            Toast.makeText(requireContext(), "오디오 파일 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 오디오 파일 URI 조회 및 업로드를 위해 시작할 인텐트 초기화.
    private fun registerIntent() {
        Log.d(TAG,"MainFragment - registerIntent() called")
        val intent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        intent.type = "audio/*"

        intentOfPickAudio = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            Log.d(TAG,"MainFragment - uri : $uri")
            uri?.let { it ->
                uploadVoice(it)
            }
        }
    }
}