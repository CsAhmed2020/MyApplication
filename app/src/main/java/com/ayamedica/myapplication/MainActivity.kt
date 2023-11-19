package com.ayamedica.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
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
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.ayamedica.myapplication.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern


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

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allPermissionsGranted = permissions.entries.all { it.value }
            if (allPermissionsGranted) {
                imageResultLauncher()
            } else {
                showToast("You need to grant all permissions to use this feature")
                Snackbar.make(binding.root,"Permissions Required",Snackbar.LENGTH_SHORT).setAction("Grand") {
                    checkAndRequestPermissions()
                }.show()
            }
        }

    private val galleryActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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

    private val cameraActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            binding.img.setImageURI(imageUri)
            val bitmap: Bitmap? = getYourInputImageBitmap(imageUri)
            if (bitmap != null) {
                recognizeText(bitmap)
            } else {
                showToast("Failed to load image. Please choose another image.")
            }
        }else {
            showToast("Cancelled")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.img.setOnClickListener {
            checkAndRequestPermissions()
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
        val contentList = mutableListOf<String>()

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                // Task completed successfully
                visionText.textBlocks.forEach {
                    it.lines.forEach {
                        Log.d("TAG", "recognizeText33: ${it.text}")
                        contentList.add(it.text)
                    }
                }

                val resultText = visionText.text

                textView.text = resultText
                val info = extractInformation(contentList.toList())
                Log.d("TAG", "recognizeText44: $info")
                textView.text = info.entries.toString()
                //textView.text = info.toString()
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

        return BitmapFactory.decodeResource(resources, R.drawable.img)
    }

    fun extractInformation(items: List<String>): Map<String, String?> {

        // Patterns to match the required fields
        val namePattern = Pattern.compile("^[a-zA-Z0-9]{4,10}\$")
        val phonePattern = Pattern.compile("\\+?(?:\\d\\s*){9,}")
        val emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        val positionPattern = Pattern.compile("(?i)(Eng|Director|Junior|Senior|President|Chef|Officer|Manager|Engineer|Specialist|Coordinator|Administrator|Executive|Associate|Analyst|Clerk|Consultant|Technician|Supervisor|Lead|Head)[\\w\\s]*(?=\\n|\\r|$)")

        val informationMap = mutableMapOf<String, String?>()

        for (line in items) {
            when {
                positionPattern.matcher(line).find() -> if (informationMap["Position"].isNullOrEmpty()) informationMap["Position"] = line.trim()
                namePattern.matcher(line).find() -> if (informationMap["Name"].isNullOrEmpty()) informationMap["Name"] = line.trim()
                Patterns.PHONE.matcher(line).matches() ->  if (informationMap["Phone"].isNullOrEmpty()) informationMap["Phone"] = line.trim()
                Patterns.EMAIL_ADDRESS.matcher(line).matches() -> if (informationMap["Email"].isNullOrEmpty()) informationMap["Email"] = line.trim()
                Patterns.WEB_URL.matcher(line).matches() -> if (informationMap["Website"].isNullOrEmpty()) informationMap["Website"] = line
            }
        }

        return informationMap
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
                    val values = ContentValues()
                    values.put(MediaStore.Images.Media.TITLE,"Image Title")
                    values.put(MediaStore.Images.Media.DESCRIPTION,"Images Desc")
                    imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)

                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)
                    cameraActivityResultLauncher.launch(takePictureIntent)

                    /*if (takePictureIntent.resolveActivity(packageManager) != null) {
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
                    }*/
                }

                options[item] == "Choose from Gallery" -> {
                    // Launch gallery intent to choose image
                    val galleryIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    galleryActivityResultLauncher.launch(galleryIntent)
                }

                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    private fun checkAndRequestPermissions() {
        val neededPermissions = mutableListOf<String>()
        val cameraPermissionGranted =
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val writeStoragePermissionGranted =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        if (!cameraPermissionGranted) neededPermissions.add(Manifest.permission.CAMERA)
        if (!writeStoragePermissionGranted) neededPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (neededPermissions.isNotEmpty()) {
            Log.d("TAG", "checkAndRequestPermissions: 1")
            requestPermissionsLauncher.launch(neededPermissions.toTypedArray())
        } else {
            Log.d("TAG", "checkAndRequestPermissions: 2")
            imageResultLauncher()
        }
    }

    //-----------------OCR END----------------------------------------

    //findViews()
    //loadAnimations()
    //changeCameraDistance()

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










