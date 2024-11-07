package com.ejemplomvvm.navigationcomponentexample.ui

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ejemplomvvm.navigationcomponentexample.databinding.ActivityGrabadoraBinding
import java.io.IOException

class GrabadoraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGrabadoraBinding
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var salida: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGrabadoraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissions()

        // botones para iniciar, detener y reproducir la grabación
        binding.btnIniciar.setOnClickListener {
            iniciar()
        }

        binding.btnDetener.setOnClickListener {
            detener()
        }

        binding.btnReproducir.setOnClickListener {
            reproducir()
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 0
            )
        }
    }

    private fun iniciar() {
        salida = "${externalCacheDir?.absolutePath}/grabacion_audio.3gp"
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(salida)

            try {
                prepare()
                start()
                binding.tvEstado.text = "Grabando..."
                binding.btnIniciar.isEnabled = false
                binding.btnDetener.isEnabled = true
                binding.btnReproducir.isEnabled = false
                Toast.makeText(this@GrabadoraActivity, "Grabación iniciada", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this@GrabadoraActivity, "Grabación fallida", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun detener() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        binding.tvEstado.text = "La grabación se detuvo...Listo para reproducir"
        binding.btnIniciar.isEnabled = true
        binding.btnDetener.isEnabled = false
        binding.btnReproducir.isEnabled = true
        Toast.makeText(this, "Grabación guardada", Toast.LENGTH_SHORT).show()
    }

    private fun reproducir() {
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(salida)
                prepare()
                start()
                binding.tvEstado.text = "Reproduciendo grabación..."
                Toast.makeText(this@GrabadoraActivity, "Reproduciendo grabación", Toast.LENGTH_SHORT).show()

                setOnCompletionListener {
                    binding.tvEstado.text = "Reproducción completada"
                    Toast.makeText(this@GrabadoraActivity, "Reproducción completada", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this@GrabadoraActivity, "Reproducción fallida", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaRecorder?.release()
        mediaPlayer?.release()
        mediaRecorder = null
        mediaPlayer = null
    }
}
