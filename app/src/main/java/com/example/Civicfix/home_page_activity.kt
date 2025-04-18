package com.example.Civicfix

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.Civicfix.databinding.HomePageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class home_page_activity : AppCompatActivity() {
    private lateinit var binding: HomePageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        val userId = intent.getStringExtra("userId")

        // Fetch and display admin name
        if (!userId.isNullOrEmpty()) {
            fetchAdminName(userId)
        }

        // Existing button implementations
        binding.rectangle8Ek2.setOnClickListener {
            val intent = Intent(this, edit_user_activity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        binding.rectangle10.setOnClickListener {
            val intent = Intent(this, edit_staff_activity::class.java)
            startActivity(intent)
        }

        binding.rectangle8.setOnClickListener {
            val intent = Intent(this, admin_complain_view::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
            finish()
        }

        // Notification button implementation
        binding.imageButton.setOnClickListener {
            Toast.makeText(this, "Notifications will be shown here", Toast.LENGTH_SHORT).show()
        }

        // Contact Us button
        binding.relativeLayout.getChildAt(0).setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@example.com")
                putExtra(Intent.EXTRA_SUBJECT, "Contact Us - Complaint Management")
            }
            try {
                startActivity(emailIntent)
            } catch (e: Exception) {
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
            }
        }

        // Rate Us button
        binding.relativeLayout.getChildAt(2).setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=${applicationContext.packageName}")))
            } catch (e: android.content.ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=${applicationContext.packageName}")))
            }
        }

        // User menu button with PopupMenu implementation
        binding.userMenuButton.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    private fun fetchAdminName(userId: String) {
        val adminRef = database.reference.child("Admin").child(userId)
        adminRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").getValue(String::class.java)
                    name?.let {
                        binding.textView.text = it.uppercase() // Display name in uppercase
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@home_page_activity,
                    "Failed to load admin data",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.user_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_logout -> {
                    performLogout()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun performLogout() {
        auth.signOut()
        Intent(this, SignInActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(this)
        }
        finish()
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }
}