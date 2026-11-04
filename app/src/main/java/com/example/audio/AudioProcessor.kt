package com.example.audio

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sin

enum class AudioMode {
    OFF,
    SIMULATED_RHYTHM,
    MICROPHONE
}

class AudioProcessor(private val context: Context, private val scope: CoroutineScope) {
    private val _audioLevel = MutableStateFlow(0f)
    val audioLevel: StateFlow<Float> = _audioLevel.asStateFlow()

    private val _audioMode = MutableStateFlow(AudioMode.SIMULATED_RHYTHM)
    val audioMode: StateFlow<AudioMode> = _audioMode.asStateFlow()

    private var audioJob: Job? = null
    private var audioRecord: AudioRecord? = null

    fun setAudioMode(mode: AudioMode) {
        _audioMode.value = mode
        restartAudioCapture()
    }

    fun start() {
        restartAudioCapture()
    }

    fun stop() {
        audioJob?.cancel()
        audioJob = null
        stopMicrophone()
        _audioLevel.value = 0f
    }

    private fun restartAudioCapture() {
        audioJob?.cancel()
        stopMicrophone()

        when (_audioMode.value) {
            AudioMode.OFF -> {
                _audioLevel.value = 0f
            }
            AudioMode.SIMULATED_RHYTHM -> {
                startSimulation()
            }
            AudioMode.MICROPHONE -> {
                if (hasMicrophonePermission()) {
                    startMicrophone()
                } else {
                    // Fallback to simulation if permission not granted
                    startSimulation()
                }
            }
        }
    }

    private fun startSimulation() {
        audioJob = scope.launch(Dispatchers.Default) {
            var simTime = 0.0
            var currentLevel = 0.2f
            while (isActive) {
                simTime += 0.05
                // Organic multi-harmonic simulation simulating voice rhythm + ambient beats
                val baseBeat = abs(sin(simTime * 2.2)).toFloat() * 0.4f
                val voiceFluctuation = abs(sin(simTime * 5.7) * sin(simTime * 1.3)).toFloat() * 0.45f
                val subBass = (if ((simTime % 2.5) < 0.25) 0.35f else 0.0f)
                val target = (baseBeat + voiceFluctuation + subBass).coerceIn(0f, 1f)

                // Smooth exponential moving average
                currentLevel = currentLevel * 0.75f + target * 0.25f
                _audioLevel.value = currentLevel
                delay(30)
            }
        }
    }

    private fun hasMicrophonePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun startMicrophone() {
        audioJob = scope.launch(Dispatchers.IO) {
            val sampleRate = 44100
            val channelConfig = AudioFormat.CHANNEL_IN_MONO
            val audioFormat = AudioFormat.ENCODING_PCM_16BIT
            val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
            val bufferSize = (minBufferSize * 2).coerceAtLeast(2048)

            try {
                audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    channelConfig,
                    audioFormat,
                    bufferSize
                )

                if (audioRecord?.state == AudioRecord.STATE_INITIALIZED) {
                    audioRecord?.startRecording()
                    val audioBuffer = ShortArray(bufferSize / 2)
                    var smoothed = 0f

                    while (isActive && audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                        val readSize = audioRecord?.read(audioBuffer, 0, audioBuffer.size) ?: 0
                        if (readSize > 0) {
                            var sum = 0.0
                            for (i in 0 until readSize) {
                                sum += abs(audioBuffer[i].toInt())
                            }
                            val avg = (sum / readSize) / 32768.0 // Normalize 16-bit PCM
                            val amplitude = (avg * 5.0).coerceIn(0.0, 1.0).toFloat()
                            smoothed = smoothed * 0.7f + amplitude * 0.3f
                            _audioLevel.value = smoothed
                        }
                        delay(25)
                    }
                } else {
                    // Fallback to simulation if init failed
                    startSimulation()
                }
            } catch (e: Exception) {
                startSimulation()
            }
        }
    }

    private fun stopMicrophone() {
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (_: Exception) {}
        audioRecord = null
    }
}
