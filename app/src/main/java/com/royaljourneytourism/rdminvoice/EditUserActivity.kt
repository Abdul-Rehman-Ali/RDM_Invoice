package com.royaljourneytourism.rdminvoice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.royaljourneytourism.rdminvoice.Adapter.Recyclerview.EditUserAdapter
import com.royaljourneytourism.rdminvoice.Model.editUser
import com.royaljourneytourism.rdminvoice.databinding.ActivityEditUserBinding

class EditUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditUserBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val userList = ArrayList<editUser>() // Use ArrayList as per the adapter
    private lateinit var adapter: EditUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityEditUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up RecyclerView
        setupRecyclerView()

        // Fetch data from Firestore
        fetchUsersFromFirestore()
    }

    private fun setupRecyclerView() {
        // Pass the context (this) to the adapter
        adapter = EditUserAdapter(this, userList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun fetchUsersFromFirestore() {
        firestore.collection("Users")
            .get()
            .addOnSuccessListener { documents ->
                userList.clear() // Clear existing list
                for (document in documents) {
                    val user = document.toObject(editUser::class.java) // Convert Firestore document to User object
                    userList.add(user)
                }
                adapter.notifyDataSetChanged() // Notify adapter of data change
            }
            .addOnFailureListener { exception ->
                // Handle error
                exception.printStackTrace()
            }
    }
}
