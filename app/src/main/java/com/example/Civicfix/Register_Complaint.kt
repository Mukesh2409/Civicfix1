package com.example.Civicfix

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.Manifest
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.Civicfix.databinding.ActivityRegisterComplaintBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class Register_Complaint : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterComplaintBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    // Register a launcher for requesting camera permission
    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Camera permission granted, proceed with camera-related operations
                captureImage()
            } else {
                requestCameraPermission()
                // Camera permission denied, handle accordingly
                // You might want to show a message to the user or request the permission again
            }
        }
    private var imageUri: Uri? = null
    // Register a launcher for capturing an image
    private val takePicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { intent ->
                // Check if the image data is directly available in the Intent
                val dataUri: Uri? = intent.data
                if (dataUri != null) {
                    // Image data is directly available in the Intent
                    imageUri = dataUri
                } else {
                    // Image data might be stored in a file, check the file path
                    val imageBitmap = intent.extras?.get("data") as Bitmap?
                    imageBitmap?.let {
                        imageUri = saveImageToGallery(it)
                    }
                }

                // Show the captured image
                showCapturedImage(imageUri)
            }
        }
    }
    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                // Handle the selected image from the gallery
                imageUri = uri
                showCapturedImage(imageUri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout using ViewBinding
        binding= ActivityRegisterComplaintBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userId=intent.getStringExtra("userId")

        // Initialize Firebase database
        database=FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference
        // Set a click listener for the capture image button
        binding.requestComplaintScrollableContent.requestComplaintCaptureImage.setOnClickListener {
            // Check and request camera permission
            checkAndRequestCameraPermission()
        }
        binding.requestComplaintScrollableContent.requestComplaintSelectImage.setOnClickListener {
            openGallery()
        }
        binding.requestComplaintScrollableContent.requestComplaintSubmitComplaint.setOnClickListener{
            val complainType=binding.requestComplaintScrollableContent.complaintTypeInput.text.toString()
            val complainSubject=binding.requestComplaintScrollableContent.complaintSubjectInput.text.toString()
            val complainDescription=binding.requestComplaintScrollableContent.complaintDescInput.text.toString()
            if(complainType.isEmpty() || complainDescription.isEmpty() || complainSubject.isEmpty()){
                if(complainType.isEmpty()){
                    Toast.makeText(this@Register_Complaint,"Please Enter Complain Type",Toast.LENGTH_SHORT).show()
                }
                if(complainSubject.isEmpty()){
                    Toast.makeText(this@Register_Complaint,"Please Enter Complain Subject",Toast.LENGTH_SHORT).show()
                }
                if(complainDescription.isEmpty()){
                    Toast.makeText(this@Register_Complaint,"Please Enter Complain Description",Toast.LENGTH_SHORT).show()
                }
            }else{
                savetodatabase(complainType,complainSubject,complainDescription,imageUri,userId.toString())
            }

        }
        if (imageUri != null) {
            Toast.makeText(this@Register_Complaint,imageUri.toString(),Toast.LENGTH_SHORT).show()
        } else {
            // Captured image is not stored or was canceled
        }
    }
    // Check and request camera permission
    private fun checkAndRequestCameraPermission() {
        if (checkCameraPermission()) {
            // Camera permission already granted, proceed with camera-related operations
            captureImage()
        } else {
            // Request camera permission

            requestCameraPermission()
        }
    }
    // Check if camera permission is granted
    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
    // Request camera permission using the launcher
    private fun requestCameraPermission() {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }
    // Capture image using the launcher
    private fun captureImage(){
        val captureIntent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePicture.launch(captureIntent)

    }
    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImage.launch(galleryIntent)
    }
    // Display the captured image in an ImageView
    private fun showCapturedImage(imageUri:Uri?){
        Toast.makeText(this@Register_Complaint,imageUri.toString()+"hi",Toast.LENGTH_SHORT).show()
        imageUri?.let {
            Toast.makeText(this@Register_Complaint,it.toString(),Toast.LENGTH_SHORT).show()
            binding.requestComplaintScrollableContent.requestComplaintImage.setImageURI(it)
            binding.requestComplaintScrollableContent.requestComplaintImage.visibility=ImageView.VISIBLE
        }
    }

    private fun savetodatabase(complainType:String,complainSubject:String,complainDescription:String,imageUri:Uri?,userId:String){
        Toast.makeText(this@Register_Complaint,userId.toString(),Toast.LENGTH_SHORT).show()
        val userComplainRef=database.reference.child("ComplainUser")

        val compainkey=userComplainRef.child(userId!!).push().key
        val currentTimeMillis = System.currentTimeMillis()

// Define the date format
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
    storageRef=storage.getReferenceFromUrl("gs://androidproject-48ff7.firebasestorage.app")
// Format the timestamp into a string
        val formattedDate = dateFormat.format(Date(currentTimeMillis))

        imageUri?.let {
            val imageRef = storageRef.child("${compainkey}.jpg")

            // Upload the image to Firebase Storage
            imageRef.putFile(imageUri)
                .addOnSuccessListener {
                    // Image uploaded successfully, get the download URL
                    Toast.makeText(this, "upload Image", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Failed to upload Image", Toast.LENGTH_SHORT).show()
                }

        }


                    val complainData=UserComplain(
            ComplainId = compainkey,
            userId=userId,
            ComplainType = complainType,
            ComplainSubject = complainSubject,
            ComplainDescription = complainDescription,
            complain_time = formattedDate,
            Complainsolved = "No",

        )


        if(compainkey!=null){
            userComplainRef.child(userId).child(compainkey).setValue(complainData)
                .addOnCompleteListener {
                    updateComplainsPending(userId)

                Toast.makeText(this@Register_Complaint, "Registered Successfully", Toast.LENGTH_SHORT).show()
                val intent=Intent(this@Register_Complaint,user_home_page_activity::class.java)
                intent.putExtra("userId",userId)
                startActivity(intent)
                finish()
            }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to register", Toast.LENGTH_SHORT).show()
                }
        }

    }
    private fun updateComplainsPending(userId:String){
        val userRef=database.reference.child("UserData").child(userId)
        userRef.child("ComplainsPending").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentPendingCount = (snapshot.value as? Long) ?: 0
                val newPendingCount = currentPendingCount + 1

                val updates = mapOf<String, Any>(
                    "ComplainsPending" to newPendingCount
                        )

                userRef.updateChildren(updates)
                    .addOnCompleteListener {
                        Toast.makeText(this@Register_Complaint,it.toString(),Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@Register_Complaint,it.message,Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Register_Complaint,error.toString(),Toast.LENGTH_SHORT).show()
            }
        })
    }
            private fun saveImageToGallery(bitmap: Bitmap): Uri? {
        // Implement the logic to save the Bitmap to a file and return the file's Uri
        // This is a sample implementation, you might need to handle file operations properly
        // For simplicity, you can use getExternalFilesDir or create a file in the app's cache directory
        val imagesDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "images")
        imagesDir.mkdirs()

        val file = File(imagesDir, "captured_image.jpg")

        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            return Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }
}