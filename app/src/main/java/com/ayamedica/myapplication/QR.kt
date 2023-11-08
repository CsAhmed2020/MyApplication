package com.ayamedica.myapplication

import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

/*
private fun setUpCamera() {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
    val previewView = binding.previewView

    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        // Set up the camera selector
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        // Set up the Preview
        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(previewView.surfaceProvider)

        // Set up the ImageAnalysis
        imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalyzer.setAnalyzer(cameraExecutor, BarCodeAnalyser { barcodes ->
            // Handle barcode detection here
            for (barcode in barcodes) {
                val barcodeValue = barcode.rawValue
                if (barcodeValue != null) {
                    Toast.makeText(
                        this,
                        "Search by this key to find the user: $barcodeValue",
                        Toast.LENGTH_LONG
                    ).show()
                    // Handle navigation using NavController
                    // navController.previousBackStackEntry?.savedStateHandle?.set("userKey", barcodeValue)
                    // navController.popBackStack()
                }
            }
        })

        try {
            // Unbind any previous use cases
            cameraProvider.unbindAll()

            // Bind the use cases to the camera
            cameraProvider.bindToLifecycle(
                this as LifecycleOwner,
                cameraSelector,
                preview,
                imageAnalyzer
            )
        } catch (e: Exception) {
            Log.e("ScanActivity", "Error setting up camera: ${e.localizedMessage}")
        }
    }, ContextCompat.getMainExecutor(this))
}

private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
    ContextCompat.checkSelfPermission(
        baseContext, it
    ) == PackageManager.PERMISSION_GRANTED
}

override fun onDestroy() {
    super.onDestroy()
    //cameraExecutor.shutdown()
}

companion object {
    private const val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
}

private fun bindCameraUseCases(
    cameraProvider: ProcessCameraProvider,
    previewView: PreviewView
) {
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()

    val preview = Preview.Builder().build()
    preview.setSurfaceProvider(previewView.surfaceProvider)

    val barCodeAnalyser = BarCodeAnalyser { barcodes ->
        barcodes.forEach { barcode ->
            val barcodeValue = barcode.rawValue
            if (barcodeValue != null) {
                Toast.makeText(
                    this,
                    "Search by this key to find the user: $barcodeValue",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    val imageAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
    imageAnalysis.setAnalyzer(cameraExecutor, barCodeAnalyser)

    try {
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}*/
