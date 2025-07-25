package com.example.o1mlkit4


import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import android.app.Notification
import android.app.NotificationChannel
import android.content.pm.ServiceInfo
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import okio.AsyncTimeout.Companion.lock
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date


class ScreenRecordingService : Service() {

    private var mediaProjection: MediaProjection? = null
    private var mediaRecorder: MediaRecorder? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var outputPath: String? = null
    private var projectionCallback: MediaProjection.Callback? = null
    private var virtualDisplayCallback: VirtualDisplay.Callback? = null

    companion object {
        const val CHANNEL_ID = "ScreenRecordingChannel"
        const val NOTIFICATION_ID = 1
        var videoFilePath: String? = null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    @SuppressLint("ForegroundServiceType")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        val resultCode = intent?.getIntExtra("resultCode", Activity.RESULT_CANCELED) ?: Activity.RESULT_CANCELED
        val data = intent?.getParcelableExtra<Intent>("data")

        if (resultCode == Activity.RESULT_OK && data != null) {
            val projectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            mediaProjection = projectionManager.getMediaProjection(resultCode, data)
            if (mediaProjection == null) {
                Log.e("ScreenRecordingService", "MediaProjection is null")
                stopSelf()
                return START_STICKY
            }
            startRecording()
        } else {
            Log.e("ScreenRecordingService", "Invalid resultCode or data: $resultCode")
            stopSelf()
        }
        return START_STICKY
    }

    private fun startRecording() {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics().apply {
            windowManager.defaultDisplay.getMetrics(this)
        }

        // Initialize MediaRecorder
        try {
            mediaRecorder = MediaRecorder().apply {
                setVideoSource(MediaRecorder.VideoSource.SURFACE)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setVideoEncoder(MediaRecorder.VideoEncoder.H264)
                setVideoEncodingBitRate(5_000_000)
                setVideoFrameRate(30)
                setVideoSize(metrics.widthPixels, metrics.heightPixels)
                outputPath = createOutputFile()
                setOutputFile(File(applicationContext.filesDir, outputPath).absolutePath)
                prepare()
            }
        } catch (e: Exception) {
            Log.e("ScreenRecordingService", "MediaRecorder setup failed: $e")
            stopRecording()
            return
        }

        // Register MediaProjection callback
        val handler = Handler(Looper.getMainLooper())
        projectionCallback = object : MediaProjection.Callback() {
            override fun onStop() {
                Log.d("ScreenRecordingService", "MediaProjection stopped")
                stopRecording()
            }
        }
        mediaProjection?.registerCallback(projectionCallback!!, handler)

        // Set up VirtualDisplay
        virtualDisplayCallback = object : VirtualDisplay.Callback() {
            override fun onPaused() {
                Log.d("ScreenRecorder", "VirtualDisplay paused")
            }

            override fun onResumed() {
                Log.d("ScreenRecorder", "VirtualDisplay resumed")
            }

            override fun onStopped() {
                Log.d("ScreenRecorder", "VirtualDisplay stopped")
                stopRecording()
            }
        }

        // Create VirtualDisplay
        try {
            virtualDisplay = mediaProjection?.createVirtualDisplay(
                "ScreenRecorder",
                metrics.widthPixels,
                metrics.heightPixels,
                metrics.densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mediaRecorder?.surface,
                virtualDisplayCallback,
                handler
            )
            if (virtualDisplay == null) {
                Log.e("ScreenRecordingService", "Failed to create VirtualDisplay")
                stopRecording()
                return
            }

            // Start recording
            mediaRecorder?.start()
            Log.d("ScreenRecordingService", "Recording started: $outputPath")
            outputPath?.let { VideoFilePathPassing.setString(it) }

            videoFilePath = outputPath
        } catch (e: Exception) {
            Log.e("ScreenRecordingService", "Failed to start recording: $e")
            stopRecording()
        }
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.let { recorder ->
                try {
                    recorder.stop()
                    Log.d("ScreenRecorder", "Recording stopped")
                } catch (e: IllegalStateException) {
                    Log.e("ScreenRecorder", "MediaRecorder stop failed: $e")
                } finally {
                    recorder.reset()
                    recorder.release()
                }
            }
            virtualDisplay?.release()
            mediaProjection?.let { projection ->
                projectionCallback?.let { projection.unregisterCallback(it) }
                projection.stop()
            }
        } catch (e: Exception) {
            Log.e("ScreenRecorder", "Error during stop: $e")
        } finally {
            outputPath?.let { VideoFilePathPassing.setString(it) }
            mediaRecorder = null
            virtualDisplay = null
            virtualDisplayCallback = null
            projectionCallback = null
            mediaProjection = null
            videoFilePath = outputPath
            stopSelf()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun createOutputFile(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "ScreenRec_${timeStamp}.mp4"
        Log.d("ScreenRecordingService", "Output file: $fileName")
        return fileName
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(): Notification {
        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Recording Screen")
            .setContentText("Your screen recording is in progress")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Screen Recording",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        Log.d("ScreenRecordingService", "Service destroyed")
        stopRecording()
        super.onDestroy()
    }
}