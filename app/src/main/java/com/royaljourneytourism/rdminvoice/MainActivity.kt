package com.royaljourneytourism.rdminvoice

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.royaljourneytourism.rdminvoice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply window insets to the root view
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Example usage of binding
        binding.cvCreateUser.setOnClickListener {
            val intent = Intent(this, AddNewUserActivity::class.java)
            startActivity(intent)
        }

        binding.cvEditUser.setOnClickListener {
            val intent = Intent(this, EditUserActivity::class.java)
            startActivity(intent)
        }

        binding.cvHistory.setOnClickListener {
            val intent = Intent(this, UsersHistoryActivity::class.java)
            startActivity(intent)
        }
    }
}
