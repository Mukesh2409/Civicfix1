package com.example.Civicfix

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Civicfix.databinding.ActivityAdminComplainViewBinding
import com.google.firebase.database.*

class admin_complain_view : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var adminRecyclerview: RecyclerView
    private lateinit var adminArrayList: ArrayList<AdminComplain>
    private lateinit var binding: ActivityAdminComplainViewBinding
    private lateinit var adminComplainViewAdapter: AdminComplainViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminComplainViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()
        val complainUserRef = database.getReference("ComplainUser")
        val userRef = database.getReference("UserData")

        // Initialize RecyclerView
        adminRecyclerview = binding.adminComplainList
        adminRecyclerview.setHasFixedSize(true)
        adminRecyclerview.layoutManager = LinearLayoutManager(this)

        // Initialize ArrayList and Adapter
        adminArrayList = ArrayList()
        adminComplainViewAdapter = AdminComplainViewAdapter(adminArrayList)
        adminRecyclerview.adapter = adminComplainViewAdapter

        // Fetch all user complaints
        fetchAllUserComplaints(complainUserRef, userRef)

        // Button to view complaints user-wise
        binding.adminComplaintViewViewComplainListUserWiseButton.setOnClickListener {
            val intent = Intent(this@admin_complain_view, admin_view_complaint_user_wise::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun fetchAllUserComplaints(complainUserRef: DatabaseReference, userRef: DatabaseReference) {
        complainUserRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(complainDataSnapshot: DataSnapshot) {
                adminArrayList.clear() // Clear old data before adding new

                for (userSnapshot in complainDataSnapshot.children) {
                    val userId = userSnapshot.key // Get user ID

                    for (complaintSnapshot in userSnapshot.children) {
                        val userComplain = complaintSnapshot.getValue(UserComplain::class.java)

                        // Fetch user details
                        userRef.child(userId!!).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(userDataSnapshot: DataSnapshot) {
                                val userData = userDataSnapshot.getValue(UserData::class.java)
                                val adminComplain = AdminComplain(
                                    userComplain = userComplain ?: UserComplain(),
                                    userData = userData ?: UserData()
                                )

                                // Add only unresolved complaints
                                if (userComplain?.Complainsolved == "No") {
                                    adminArrayList.add(adminComplain)
                                }

                                // Notify adapter after updating list
                                adminComplainViewAdapter.notifyDataSetChanged()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(this@admin_complain_view, "Error fetching user data", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@admin_complain_view, "Error fetching complaints", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
