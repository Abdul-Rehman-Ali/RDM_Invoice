package com.royaljourneytourism.rdminvoice

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.royaljourneytourism.rdminvoice.Adapter.Recyclerview.UsersHistoryAdapter
import com.royaljourneytourism.rdminvoice.Model.userHistory
import com.royaljourneytourism.rdminvoice.databinding.ActivityUsersHistoryBinding

class UsersHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsersHistoryBinding
    private lateinit var adapter: UsersHistoryAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val userList = ArrayList<userHistory>() // Initialize the list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize View Binding
        binding = ActivityUsersHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply window insets to the root view
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up RecyclerView
        setupRecyclerView()

        // Fetch data from Firestore
        fetchUsersFromFirestore()
    }

    private fun setupRecyclerView() {
        // Initialize the adapter
        adapter = UsersHistoryAdapter(this, userList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun fetchUsersFromFirestore() {
        firestore.collection("Users")
            .get()
            .addOnSuccessListener { documents ->
                userList.clear() // Clear existing list
                for (document in documents) {
                    val user = document.toObject(userHistory::class.java)
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
