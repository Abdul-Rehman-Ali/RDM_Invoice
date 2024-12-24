package com.royaljourneytourism.rdminvoice

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.royaljourneytourism.rdminvoice.databinding.ActivityUserHistoryInvoicesBinding

class UserHistoryInvoicesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserHistoryInvoicesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize View Binding
        binding = ActivityUserHistoryInvoicesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply WindowInsets for edge-to-edge UI
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
