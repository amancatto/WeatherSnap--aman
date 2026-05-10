package com.example.weathersnap.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.weathersnap.utils.CameraUtils

@Composable
fun CustomCameraScreen(
    onImageCaptured: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var permissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> permissionGranted = granted }

    LaunchedEffect(Unit) {
        if (!permissionGranted) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    val previewView = remember { PreviewView(context) }

    LaunchedEffect(permissionGranted) {
        if (permissionGranted) {
            CameraUtils.startCamera(
                context        = context,
                lifecycleOwner = lifecycleOwner,
                previewView    = previewView,
                onReady        = { imageCapture = it },
                onError        = { errorMessage = it }
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        // ── Camera Preview ───────────────────────────────────────────────────
        if (permissionGranted) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Camera permission required",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }

        // ── Close Button ─────────────────────────────────────────────────────
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
        }

        // ── Error Snackbar ───────────────────────────────────────────────────
        errorMessage?.let { msg ->
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 64.dp, start = 16.dp, end = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFB00020))
            ) {
                Text(msg, color = Color.White, modifier = Modifier.padding(12.dp), fontSize = 14.sp)
            }
        }

        // ── Capture Button ───────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        ) {
            IconButton(
                onClick = {
                    val capture = imageCapture ?: return@IconButton
                    CameraUtils.takePicture(
                        context       = context,
                        imageCapture  = capture,
                        executor      = ContextCompat.getMainExecutor(context),
                        onSuccess     = { result -> onImageCaptured(result.file.absolutePath) },
                        onError       = { errorMessage = it }
                    )
                },
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFFCDE2A2))
                    )
                }
            }
        }
    }
}
