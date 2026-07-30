package com.example.util

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File

class AudioRecorderHelper(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var audioFile: File? = null

    fun startRecording() {
        try {
            audioFile = File(context.cacheDir, "audio_practice_${System.currentTimeMillis()}.m4a")
            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFile?.absolutePath)
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            recorder?.release()
            recorder = null
        }
    }

    fun stopRecording(): ByteArray? {
        return try {
            recorder?.stop()
            recorder?.release()
            recorder = null
            audioFile?.readBytes()
        } catch (e: Exception) {
            e.printStackTrace()
            recorder?.release()
            recorder = null
            null
        }
    }
}
