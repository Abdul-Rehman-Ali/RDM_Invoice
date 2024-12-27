package com.royaljourneytourism.rdminvoice

import android.app.DatePickerDialog
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UserHistoryInvoicesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserHistoryInvoicesBinding
    private lateinit var adapter: UsersHistoryInvoicesAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val invoices = ArrayList<userHistoryInvoice>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

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

        // Calendar Icon Click Listener
        binding.calendarIcon.setOnClickListener {
            showDateRangePicker()
        }
    }

    private fun fetchWebNameAndData(documentId: String) {
        firestore.collection("Users")
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val webName = document.getString("webName")
                    if (webName != null) {
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
                invoices.clear()

                var totalRevenue = 0.0
                val totalCount = documents.size()

                binding.totalInvoiceCount.text = "$totalCount"

                for (document in documents) {
                    val user = document.toObject(userHistoryInvoice::class.java)
                    invoices.add(user)

                    val price = document.getDouble("totalPrice") ?: 0.0
                    totalRevenue += price
                }

                binding.totalRevenueCount.text = "AED: $totalRevenue"
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }

    private fun showDateRangePicker() {
        val calendar = Calendar.getInstance()

        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()

        val dateRangePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                startDate.set(year, month, dayOfMonth)

                DatePickerDialog(
                    this,
                    { _, endYear, endMonth, endDay ->
                        endDate.set(endYear, endMonth, endDay)
                        filterInvoicesByDateRange(startDate.time, endDate.time)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).apply {
                    datePicker.maxDate = calendar.timeInMillis
                    show()
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dateRangePicker.datePicker.maxDate = calendar.timeInMillis
        dateRangePicker.show()
    }

    private fun filterInvoicesByDateRange(startDate: Date, endDate: Date) {
        val filteredInvoices = invoices.filter { invoice ->
            try {
                val invoiceDate = dateFormat.parse(invoice.currentDate)
                invoiceDate.after(startDate) && invoiceDate.before(endDate) ||
                        invoiceDate == startDate || invoiceDate == endDate
            } catch (e: Exception) {
                false
            }
        }

        adapter.updateList(filteredInvoices)
        binding.totalInvoiceCount.text = "${filteredInvoices.size}"

        val totalRevenue = filteredInvoices.sumOf { invoice ->
            try {
                invoice.totalPrice ?: 0.0
            } catch (e: Exception) {
                0.0
            }
        }

        binding.totalRevenueCount.text = "AED: $totalRevenue"
    }
}
