package com.example.weathersnap.utils

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.util.concurrent.Executor

/**
 * Result returned after a successful [CameraUtils.takePicture] call.
 */
data class CaptureResult(
    /** The raw JPEG file saved in the app's cache directory. */
    val file: File,
    /** Size of the saved file in bytes — used as the "original size" before compression. */
    val originalSizeBytes: Long
)

/**
 * Utility object that wraps CameraX to handle:
 *  - Binding the camera to a [LifecycleOwner].
 *  - Capturing a JPEG and saving it to the cache directory.
 *
 * Uses CameraX directly (NO device intents — as required by the assignment).
 */
object CameraUtils {

    /**
     * Binds a back-facing camera to [lifecycleOwner] with a [Preview] use-case
     * connected to [previewView] and an [ImageCapture] use-case.
     *
     * @return The bound [ImageCapture] instance, used later in [takePicture].
     */
    fun startCamera(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        onReady: (ImageCapture) -> Unit,
        onError: (String) -> Unit
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            runCatching {
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                val imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )

                onReady(imageCapture)
            }.onFailure { e ->
                onError(e.message ?: "Failed to start camera")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    /**
     * Captures a photo using [imageCapture] and saves it to the app cache directory.
     *
     * @param imageCapture  The [ImageCapture] use-case from [startCamera].
     * @param executor      Executor for the capture callback (use main executor for UI updates).
     * @param onSuccess     Called with [CaptureResult] on the provided [executor].
     * @param onError       Called with an error message string.
     */
    fun takePicture(
        context: Context,
        imageCapture: ImageCapture,
        executor: Executor,
        onSuccess: (CaptureResult) -> Unit,
        onError: (String) -> Unit
    ) {
        val photoFile = createOutputFile(context)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    onSuccess(
                        CaptureResult(
                            file = photoFile,
                            originalSizeBytes = photoFile.length()
                        )
                    )
                }

                override fun onError(exc: ImageCaptureException) {
                    onError(exc.message ?: "Image capture failed")
                }
            }
        )
    }

    /**
     * Creates a uniquely named output [File] in the app's cache directory.
     */
    private fun createOutputFile(context: Context): File {
        val timestamp = System.currentTimeMillis()
        return File(
            context.cacheDir,
            "WEATHERSNAP_RAW_${timestamp}.jpg"
        )
    }
}
