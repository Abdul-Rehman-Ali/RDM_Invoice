package com.royaljourneytourism.rdminvoice

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.royaljourneytourism.rdminvoice.Adapter.Recyclerview.UsersHistoryInvoicesAdapter
import com.royaljourneytourism.rdminvoice.Model.userHistoryInvoice
import com.royaljourneytourism.rdminvoice.databinding.ActivityUserHistoryInvoicesBinding

class UserHistoryInvoicesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserHistoryInvoicesBinding
    private lateinit var adapter: UsersHistoryInvoicesAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val invoices = ArrayList<userHistoryInvoice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize View Binding
        binding = ActivityUserHistoryInvoicesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up RecyclerView
        adapter = UsersHistoryInvoicesAdapter(this, invoices)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Retrieve the Document ID from Intent extras
        val documentId = intent.getStringExtra("DOCUMENT_ID")

        if (documentId != null) {
            fetchWebNameAndData(documentId)
        } else {
            Toast.makeText(this, "Document ID is missing!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchWebNameAndData(documentId: String) {
        val firestore = FirebaseFirestore.getInstance()

        // Fetch the document by its ID
        firestore.collection("Users") // Replace "users" with the actual collection name where user documents are stored
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Retrieve the webName field
                    val webName = document.getString("webName")
                    if (webName != null) {
                        // Fetch data from the collection named after webName
                        fetchCollectionData(webName)
                    } else {
                        Toast.makeText(this, "webName field is missing!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Document not found!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch document: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchCollectionData(collectionName: String) {
        firestore.collection(collectionName)
            .get()
            .addOnSuccessListener { documents ->
                invoices.clear() // Clear existing list

                var totalRevenue = 0.0 // Variable to store the sum of prices

                // Fetch the total document count
                val totalCount = documents.size()

                // Update the totalInvoiceCount view with the total count
                binding.totalInvoiceCount.text = "$totalCount"

                for (document in documents) {
                    val user = document.toObject(userHistoryInvoice::class.java)
                    invoices.add(user)

                    // Sum up the prices
                    val price = document.getDouble("totalPrice") ?: 0.0 // Replace "price" with the actual field name
                    totalRevenue += price
                }

                // Update the totalRevenueCount view with the total revenue
                binding.totalRevenueCount.text = "$totalRevenue"

                adapter.notifyDataSetChanged() // Notify adapter of data change
            }
            .addOnFailureListener { exception ->
                // Handle error
                exception.printStackTrace()
            }
    }


}
