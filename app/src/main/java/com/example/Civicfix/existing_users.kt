package com.example.Civicfix

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Civicfix.databinding.ActivityExistingUsersBinding
import com.google.firebase.database.*

class existing_users : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var userArrayList: ArrayList<UserData>
    private lateinit var binding: ActivityExistingUsersBinding
    private lateinit var existingUserAdapter: ExistingUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExistingUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase and UI components
        database = FirebaseDatabase.getInstance()
        userRecyclerview = binding.existingUserUsersList

        // RecyclerView Setup
        userRecyclerview.setHasFixedSize(true)
        userRecyclerview.layoutManager = LinearLayoutManager(this)

        userArrayList = ArrayList()
        existingUserAdapter = ExistingUserAdapter(userArrayList)
        userRecyclerview.adapter = existingUserAdapter

        // Fetch all users from Firebase
        fetchAllUsers()
    }

    private fun fetchAllUsers() {
        val userRef = database.getReference("UserData")

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userArrayList.clear() // Clear existing data
                for (userSnapshot in snapshot.children) {
                    val userData = userSnapshot.getValue(UserData::class.java)
                    userData?.let { userArrayList.add(it) }
                }
                existingUserAdapter.notifyDataSetChanged() // Refresh UI
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@existing_users, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
