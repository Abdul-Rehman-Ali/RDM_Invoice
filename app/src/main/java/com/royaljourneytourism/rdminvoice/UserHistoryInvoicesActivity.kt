package com.royaljourneytourism.rdminvoice

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
                for (document in documents) {
                    val user = document.toObject(userHistoryInvoice::class.java)
                    invoices.add(user)
                }
                adapter.notifyDataSetChanged() // Notify adapter of data change
            }
            .addOnFailureListener { exception ->
                // Handle error
                exception.printStackTrace()
            }
    }
}
