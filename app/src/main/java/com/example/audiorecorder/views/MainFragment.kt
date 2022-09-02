package com.example.audiorecorder.views

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.audiorecorder.R
import com.example.audiorecorder.RecordService
import com.example.audiorecorder.databinding.FragmentMainBinding
import com.example.audiorecorder.viewmodels.VoiceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainFragment : Fragment() {

    companion object {
        private const val TAG: String = "로그"
    }

    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: VoiceViewModel
    private lateinit var recordService: Intent
    private lateinit var directoryOfVoice: String
    private lateinit var intentOfPickAudio: ActivityResultLauncher<String>
    private lateinit var originalName: String
    private lateinit var aBuilder: AlertDialog.Builder


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"MainFragment - onCreate() called")
        super.onCreate(savedInstanceState)
        // 파일 명 작명용 알림창 빌더
        aBuilder = AlertDialog.Builder(requireContext())
        // 뷰모델 초기화
        viewModel = VoiceViewModel()
        // 음성파일 저장할 공간
        directoryOfVoice = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        registerIntentForPickAudio()
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

        // 녹음할 오디오 파일의 URI 조회
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
        originalName = "${System.currentTimeMillis()}.mp3"
        recordService = Intent(requireActivity(), RecordService::class.java)
        recordService.putExtra("originalName", originalName)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().startForegroundService(recordService)
        } else {
            requireActivity().startService(recordService)
        }
    }

    // 정지 = 아예 정지하고 파일을 저장함.
    private fun stopRecord() {
        Log.d(TAG,"MainFragment - stopRecord() called")

        try {
            requireActivity().stopService(recordService)
            val editViewForFileName = EditText(requireContext())
            editViewForFileName.maxLines = 1
            editViewForFileName.inputType = InputType.TYPE_CLASS_TEXT
            aBuilder.apply {
                setTitle("파일명을 입력해주세요.")
                setView(editViewForFileName)
                setPositiveButton("결정") { _, _ ->
                    setFileName("${editViewForFileName.text}.mp3")
                }
            }
            aBuilder.setTitle("파일명을 뭐라고 지으시겠습니다./")
            aBuilder.create().show()
        } catch (e: UninitializedPropertyAccessException) {
            Log.w(TAG, "stopRecord: recorder 가 아직 초기화되지 않음", e)
        } catch (e: Exception) {
            Log.w(TAG, "stopRecord: ", e)
        }
    }

    // 사용자가 웝하는 이름으로 파일명을 변경
    private fun setFileName(fileName: String) {
        Log.d(TAG,"MainFragment - setFileName() called")
        Log.d(TAG,"MainFragment - originalName : $originalName fileName : $fileName called")
        val dir = File(directoryOfVoice)
        if (!dir.exists()) {
            Log.d(TAG,"MainFragment - dir not exist() called")
            dir.mkdirs()
        }

        val from = File(dir, originalName)
        val to = File(dir, fileName)
        from.renameTo(to)
    }

    // 올릴 오디오 파일에 URI 를 조회하는 기능.
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
    private fun registerIntentForPickAudio() {
        Log.d(TAG,"MainFragment - registerIntent() called")
        intentOfPickAudio = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            Log.d(TAG,"MainFragment - uri : $uri")
            uri?.let { it ->
                uploadVoice(it)
            }
        }
    }
}