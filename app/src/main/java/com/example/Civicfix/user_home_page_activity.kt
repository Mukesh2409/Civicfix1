package com.example.Civicfix

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.Civicfix.databinding.UserHomePageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class user_home_page_activity : AppCompatActivity() {
    private lateinit var binding: UserHomePageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        val userId = intent.getStringExtra("userId")

        // Fetch and display user name
        if (!userId.isNullOrEmpty()) {
            fetchUserName(userId)
        }

        binding.userMenuButton.setOnClickListener { view ->
            showPopupMenu(view)
        }

        binding.userHomePageRegisterComplaintBox.setOnClickListener {
            val intent = Intent(this, Register_Complaint::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        binding.userHomePageViewComplaintsBox.setOnClickListener {
            val intent = Intent(this, User_View_Complain::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("Type", "totalComplains")
            startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fetchUserName(userId: String) {
        val userRef = database.reference.child("UserData").child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").getValue(String::class.java)
                    name?.let {
                        binding.userTextView.text = it.uppercase() // Display name in uppercase
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@user_home_page_activity,
                    "Failed to load user data",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.user_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_logout -> {
                    auth.signOut()
                    startActivity(Intent(this, SignInActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
}