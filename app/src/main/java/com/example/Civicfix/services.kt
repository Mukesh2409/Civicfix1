package com.example.Civicfix

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.Civicfix.databinding.ActivityServicesBinding
import com.google.firebase.database.FirebaseDatabase

class services : AppCompatActivity() {
    private lateinit var binding:ActivityServicesBinding
    private lateinit var database: FirebaseDatabase
    private var selectedService: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServicesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance()
        val items = arrayOf("One", "Two", "Three", "Four", "Five")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        val userId = intent.getStringExtra("userId")
        val autoCompleteTextView = binding.autoCompleteTxt
        autoCompleteTextView.setAdapter(adapter)

        // Check for selection using Text Length before button click
        binding.ssi1.isEnabled = binding.autoCompleteTxt.text.toString().isNotEmpty()
        Toast.makeText(this, "Services updated successfully {$userId}", Toast.LENGTH_SHORT).show()
        //Update button state based on selection changes
        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                binding.ssi1.isEnabled = s.toString().isNotEmpty()
                selectedService = s.toString() // Update selected service variable
            }
        })
        binding.ssi1.setOnClickListener {
            if (selectedService?.isNotEmpty() == true) {
//                Toast.makeText(this, "Error updating services251", Toast.LENGTH_SHORT).show()
                // Add logic to store the selected service to Firebase based on userId
                val userRef = database.getReference("admin").child(userId!!)
                userRef.get().addOnSuccessListener { snapshot ->
                    val updatedServices: MutableList<String> = mutableListOf()
                    // Add existing services from user data (if available)
                    if (snapshot.exists() && snapshot.child("admin_services").exists()) {
                        val existingServices = snapshot.child("admin_services").children.map { it.getValue(String::class.java)!! }
                        updatedServices.addAll(existingServices)
                    }
                        updatedServices.add(selectedService!!)
                        userRef.child("admin_services").setValue(updatedServices)
                        Toast.makeText(this, "Services updated successfully", Toast.LENGTH_SHORT).show()
                    // Success: navigate to HomePage and show a success message (optional)
                    val intent = Intent(this,services::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.putExtra("userId", userId)
                    startActivity(intent)
                    finish()
        }.addOnFailureListener {
                        // Handle error: show an error message and take appropriate action
                        Toast.makeText(this, "Error updating services: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
        binding.ssi2.setOnClickListener{
            val intent=Intent(this,home_page_activity::class.java)
            intent.putExtra("userId",userId)
            startActivity(intent)
            finish()
        }
    }
}