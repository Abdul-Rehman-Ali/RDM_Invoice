package com.royaljourneytourism.rdminvoice

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.royaljourneytourism.rdminvoice.databinding.ActivityAddNewUserBinding
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

class AddNewUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewUserBinding

    // Variables to hold user inputs
    private var username: String? = null
    private var email: String? = null
    private var phoneNumber: String? = null
    private var password: String? = null
    private var websiteURL: String? = null
    private var websiteName: String? = null
    private var pickedColor: String? = null
    private var uploadedPhotoUri: String? = null

    // Firebase instances
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance().reference }
    private val auth by lazy { FirebaseAuth.getInstance() }

    // File picker for photo upload
    private val pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            uploadedPhotoUri = uri.toString()
            uploadPhotoToFirebaseStorage(uri)
        } else {
            Toast.makeText(this, "No photo selected.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize the binding object
        binding = ActivityAddNewUserBinding.inflate(layoutInflater)

        // Set the content view using view binding
        setContentView(binding.root)

        // Use the binding to access views
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Handle "Pick Color" button click
//        binding.btnPickColor.setOnClickListener {
//            showColorPickerDialog()
//        }

        // Handle "Upload Photo" button click
        binding.btnUploadLogo.setOnClickListener {
            pickPhotoLauncher.launch("image/*")
        }

        // Collect data from fields when user clicks "Save" button
        binding.btnSave.setOnClickListener {
            collectUserData()
            saveUserDataToFirestore()
        }
    }

    private fun uploadPhotoToFirebaseStorage(uri: Uri) {
        val fileName = "logos/${System.currentTimeMillis()}.jpg"
        val photoRef: StorageReference = storage.child(fileName)

        photoRef.putFile(uri)
            .addOnSuccessListener {
                photoRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    uploadedPhotoUri = downloadUrl.toString()
                    Toast.makeText(this, "Photo uploaded successfully!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to upload photo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

//    private fun showColorPickerDialog() {
//        val colorPickerDialog = ColorPickerDialog.Builder(this)
//            .setTitle("Pick a Color")
//            .setPositiveButton("OK", ColorEnvelopeListener { envelope, _ ->
//                pickedColor = "#${envelope.hexCode}"
//                binding.btnPickColor.setBackgroundColor(Color.parseColor(pickedColor))
//                Toast.makeText(this, "Color Selected: $pickedColor", Toast.LENGTH_SHORT).show()
//            })
//            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
//            .attachAlphaSlideBar(true)
//            .attachBrightnessSlideBar(true)
//            .create()
//
//        colorPickerDialog.show()
//    }

    private fun collectUserData() {
        username = binding.etUsername.text.toString()
        email = binding.etEmail.text.toString()
        phoneNumber = binding.etPhone.text.toString()
        password = binding.etPassword.text.toString()
        websiteURL = binding.etWebsiteURL.text.toString()
        websiteName = binding.etWebsite.text.toString()
        pickedColor = binding.btnPickColor.text.toString()
    }

    private fun saveUserDataToFirestore() {
        // Validate email and password
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            Toast.makeText(this, "Email and Password are required", Toast.LENGTH_SHORT).show()
            return
        }

        // Sign up the user with Firebase Authentication
        auth.createUserWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // If signup is successful, save the user data to Firestore
                    val userId = task.result?.user?.uid
                    val userData = hashMapOf(
                        "userId" to userId,
                        "username" to username,
                        "email" to email,
                        "phoneNo" to phoneNumber,
                        "password" to password,
                        "webURL" to websiteURL,
                        "webName" to websiteName,
                        "color" to pickedColor,
                        "logoURL" to uploadedPhotoUri // Add the uploaded photo URL here
                    )

                    // Save the user data in Firestore with the userId as the document ID
                    firestore.collection("Users")
                        .document(userId!!)
                        .set(userData)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "User Created and Data Saved Successfully",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "Failed to save user data: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                } else {
                    // If signup fails, show error message
                    Toast.makeText(
                        this,
                        "Signup Failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}
