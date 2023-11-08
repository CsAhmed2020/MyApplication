package com.ayamedica.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import com.ayamedica.myapplication.databinding.ActivityMainBinding
import com.google.android.datatransport.BuildConfig
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    /*lateinit var front_animation:AnimatorSet
    lateinit var back_animation: AnimatorSet
    var isFront =true
    private var mSetRightOut: AnimatorSet? = null
    private var mSetLeftIn: AnimatorSet? = null
    private var mIsBackVisible = false
    private var mCardFrontLayout: View? = null
    private var mCardBackLayout: View? = null


    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageAnalyzer: ImageAnalysis
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                setUpCamera()
            } else {
                Toast.makeText(this, "Camera permission denied.", Toast.LENGTH_SHORT).show()
            }
        }*/

    private lateinit var textView: TextView

    private var imageUri: Uri? = null
    private val profileImageResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d("TAG", "1: ${result.resultCode} -- ${result.data}")
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                imageUri = uri
                binding.img.setImageURI(imageUri)

                val bitmap: Bitmap? = getYourInputImageBitmap(imageUri)
                if (bitmap != null) {
                    recognizeText(bitmap)
                } else {
                    showToast("Failed to load image. Please choose another image.")
                }
            } ?: run {
                showToast("Failed to retrieve image. Please try again.")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.img.setOnClickListener {
            checkPermissions()
        }

        textView = binding.tv
        val bitmap = getYourInputImageBitmap()
        recognizeText(bitmap)

        /*// Initialize the cameraExecutor
        cameraExecutor = Executors.newSingleThreadExecutor()
        if (allPermissionsGranted()) {
            setUpCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }*/


    }

    //-----------------OCR Top----------------------------------------
    private fun recognizeText(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                // Task completed successfully
                val resultText = visionText.text
                textView.text = resultText
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                Toast.makeText(
                    baseContext,
                    "Text recognition failed: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun getYourInputImageBitmap(imageUri: Uri?): Bitmap? {
        // Use content resolver to open an input stream and decode the Uri into a Bitmap
        try {
            val inputStream: InputStream? = imageUri?.let { contentResolver.openInputStream(it) }
            if (inputStream != null) {
                return BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getYourInputImageBitmap(): Bitmap {

        return BitmapFactory.decodeResource(resources, R.drawable.bm_img)
    }


    private fun checkPermissions() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            requestMultiplePermissions.launch(arrayOf(permission))
        } else {
            imageResultLauncher()
        }
    }

    private val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true || permissions[Manifest.permission.READ_MEDIA_IMAGES] == true) {
            imageResultLauncher()
        } else {
            showToast("Permission denied. Cannot access images.")
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun imageResultLauncher() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Add Photo!")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    // Launch camera intent to capture image
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (takePictureIntent.resolveActivity(packageManager) != null) {
                        val photoFile: File? = try {
                            createImageFile()
                        } catch (ex: IOException) {
                            null
                        }
                        photoFile?.also {
                            val photoURI: Uri = FileProvider.getUriForFile(
                                this,
                                "com.ayamedica.myapplication.provider",
                                it
                            )
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            profileImageResultLauncher.launch(takePictureIntent)
                        }
                    }
                }

                options[item] == "Choose from Gallery" -> {
                    // Launch gallery intent to choose image
                    val galleryIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    profileImageResultLauncher.launch(galleryIntent)
                }

                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()


        //-----------------OCR END----------------------------------------

        //findViews()
        //loadAnimations()
        //changeCameraDistance()


    }

    private fun createImageFile(): File? {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}










