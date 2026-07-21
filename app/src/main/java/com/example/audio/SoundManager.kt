package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class SoundManager {
    private val scope = CoroutineScope(Dispatchers.Default)

    fun playClick() {
        scope.launch {
            generateAndPlayTone(
                startFreq = 800.0,
                endFreq = 400.0,
                durationMs = 35,
                volume = 0.4f
            )
        }
    }

    fun playStartupChime(context: Context? = null) {
        scope.launch {
            // Check if raw mp3 exists or play synthesized berrydeep program installation audio
            if (context != null) {
                try {
                    val resId = context.resources.getIdentifier("berrydeep_program_installation", "raw", context.packageName)
                    if (resId != 0) {
                        val mediaPlayer = MediaPlayer.create(context, resId)
                        mediaPlayer?.start()
                        mediaPlayer?.setOnCompletionListener { mp -> mp.release() }
                        return@launch
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Berrydeep program installation synthesized sequence: Deep sub bass + installation frequency sweeps
            playBerryDeepProgramInstallationSound()
        }
    }

    fun playBerryDeepProgramInstallationSound() {
        // Deep sub-bass installation chime sequence
        generateAndPlayTone(
            startFreq = 90.0,
            endFreq = 480.0,
            durationMs = 650,
            volume = 0.7f,
            harmonic = true
        )
    }

    fun playLaunch(context: Context? = null) {
        scope.launch {
            if (context != null) {
                try {
                    var resId = context.resources.getIdentifier(
                        "berrydeep_program_installation_logo_503284",
                        "raw",
                        context.packageName
                    )
                    if (resId == 0) {
                        resId = context.resources.getIdentifier(
                            "berrydeep_program_installation",
                            "raw",
                            context.packageName
                        )
                    }
                    if (resId != 0) {
                        val mediaPlayer = MediaPlayer.create(context, resId)
                        mediaPlayer?.start()
                        mediaPlayer?.setOnCompletionListener { mp -> mp.release() }
                        return@launch
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Energetic game launch sound fallback
            generateAndPlayTone(
                startFreq = 300.0,
                endFreq = 1200.0,
                durationMs = 350,
                volume = 0.7f,
                harmonic = true
            )
        }
    }

    fun playRamClean() {
        scope.launch {
            // Vacuum pulse audio effect
            generateAndPlayTone(
                startFreq = 1000.0,
                endFreq = 150.0,
                durationMs = 300,
                volume = 0.5f,
                harmonic = true
            )
        }
    }

    private fun generateAndPlayTone(
        startFreq: Double,
        endFreq: Double,
        durationMs: Int,
        volume: Float,
        harmonic: Boolean = false
    ) {
        try {
            val sampleRate = 44100
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val progress = i.toDouble() / numSamples
                val currentFreq = startFreq + (endFreq - startFreq) * progress
                val angle = 2.0 * Math.PI * currentFreq * t
                var sample = sin(angle)
                if (harmonic) {
                    sample += 0.5 * sin(2.0 * angle) + 0.25 * sin(3.0 * angle) + 0.15 * sin(0.5 * angle)
                }

                // Smooth envelope (fade in / out to avoid clicks)
                val envelope = when {
                    progress < 0.1 -> progress / 0.1
                    progress > 0.8 -> (1.0 - progress) / 0.2
                    else -> 1.0
                }

                val finalVal = (sample * envelope * volume * 32767).toInt()
                buffer[i] = finalVal.coerceIn(-32768, 32767).toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()

            Thread.sleep(durationMs.toLong() + 30)
            audioTrack.stop()
            audioTrack.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
