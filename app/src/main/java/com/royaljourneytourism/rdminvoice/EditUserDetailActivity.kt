package com.royaljourneytourism.rdminvoice

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.royaljourneytourism.rdminvoice.databinding.ActivityEditUserDetailBinding
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

class EditUserDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditUserDetailBinding
    private lateinit var documentId: String
    private val firestore = FirebaseFirestore.getInstance()
    private val storage: StorageReference by lazy { FirebaseStorage.getInstance().reference }
    private var selectedColor: String? = null

    // File picker for logo upload
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            uploadLogoToStorage(uri)
        } else {
            Toast.makeText(this, "No image selected.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize View Binding
        binding = ActivityEditUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the document ID from the Intent
        documentId = intent.getStringExtra("DOCUMENT_ID") ?: ""

        // Apply window insets to the root view
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Fetch and display the user details
        fetchUserDetails()

        // Set up the update buttons
        binding.btnUpdate.setOnClickListener { updateUserDetails() }
        binding.btnUpdateColor.setOnClickListener { openColorPicker() }
        binding.btnUpdateLogo.setOnClickListener { pickImageLauncher.launch("image/*") }
    }

    private fun fetchUserDetails() {
        if (documentId.isNotEmpty()) {
            firestore.collection("Users").document(documentId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Populate the fields with data
                        binding.etUsername.setText(document.getString("username"))
                        binding.etEmail.setText(document.getString("email"))
                        binding.etPhone.setText(document.getString("phoneNo"))
                        binding.etPassword.setText(document.getString("password"))
                        binding.etWebsiteURL.setText(document.getString("webURL"))
                        binding.etWebsite.setText(document.getString("webName"))

                        // Set color and logo if available
                        selectedColor = document.getString("color")
                        selectedColor?.let {
                            binding.btnUpdateColor.setBackgroundColor(Color.parseColor(it))
                        }
                    } else {
                        Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error fetching user: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Invalid document ID!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserDetails() {
        if (documentId.isNotEmpty()) {
            val updatedData = hashMapOf(
                "username" to binding.etUsername.text.toString(),
                "email" to binding.etEmail.text.toString(),
                "phoneNo" to binding.etPhone.text.toString(),
                "password" to binding.etPassword.text.toString(),
                "webURL" to binding.etWebsiteURL.text.toString(),
                "webName" to binding.etWebsite.text.toString(),
                "color" to selectedColor
            )

            firestore.collection("Users").document(documentId)
                .update(updatedData as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(this, "User details updated successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to update user: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Invalid document ID!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openColorPicker() {
        ColorPickerDialog.Builder(this)
            .setTitle("Pick a Color")
            .setPositiveButton("Select", ColorEnvelopeListener { envelope, _ ->
                selectedColor = "#${envelope.hexCode}"
                binding.btnUpdateColor.setBackgroundColor(Color.parseColor(selectedColor))
                Toast.makeText(this, "Color selected: $selectedColor", Toast.LENGTH_SHORT).show()
            })
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .attachAlphaSlideBar(true)
            .attachBrightnessSlideBar(true)
            .create()
            .show()
    }

    private fun uploadLogoToStorage(uri: Uri) {
        val fileName = "logos/${System.currentTimeMillis()}.jpg"
        val logoRef = storage.child(fileName)

        logoRef.putFile(uri)
            .addOnSuccessListener {
                logoRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    updateLogoURLInFirestore(downloadUrl.toString())
                    Toast.makeText(this, "Logo uploaded successfully!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to upload logo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateLogoURLInFirestore(logoURL: String) {
        firestore.collection("Users").document(documentId)
            .update("logoURL", logoURL)
            .addOnSuccessListener {
                Toast.makeText(this, "Logo URL updated successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update logo URL: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
