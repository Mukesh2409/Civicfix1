package com.example.Civicfix

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Civicfix.databinding.ActivityAdminViewComplaintUserWiseBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class admin_view_complaint_user_wise : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var adminUserWiseArrayList:ArrayList<UserData>
    private lateinit var binding: ActivityAdminViewComplaintUserWiseBinding
    private lateinit var adminViewComplainUserWiseAdapter: AdminViewComplaintUserWiseAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminViewComplaintUserWiseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userRecyclerview=binding.adminViewComplaintUserWiseList
        database= FirebaseDatabase.getInstance()
        val userId=intent.getStringExtra("userId")
        Toast.makeText(this@admin_view_complaint_user_wise,userId.toString(),Toast.LENGTH_SHORT).show()
        userRecyclerview.setHasFixedSize(true);
        userRecyclerview.layoutManager= LinearLayoutManager(this);
        val adminRef=database.getReference("Admin").child(userId!!)
        val userRef=database.getReference("UserData")
        adminUserWiseArrayList= ArrayList()
        adminViewComplainUserWiseAdapter= AdminViewComplaintUserWiseAdapter(adminUserWiseArrayList)
        userRecyclerview.adapter=adminViewComplainUserWiseAdapter
        binding.adminViewComplaintUserWiseButton.setOnClickListener {
            val intent= Intent(this@admin_view_complaint_user_wise,admin_complain_view::class.java)
            intent.putExtra("userId",userId)
            startActivity(intent)
            finish()
        }
        adminRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val adminData = dataSnapshot.getValue(AdminData::class.java)
                adminUserWiseArrayList.clear()
                if (adminData?.uid != null) {
                    val userIdList = adminData.uid
                    for (userId in userIdList!!) {
                        if (userId != null) {
                            userRef.child(userId)
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val userData=snapshot.getValue(UserData::class.java)
                                        userData?.let{
                                            if(it.ComplainsPending?:0>0){
                                                adminUserWiseArrayList.add(it)
                                                adminUserWiseArrayList.sortByDescending { it -> it.ComplainsPending ?: 0 }
                                                adminViewComplainUserWiseAdapter.notifyDataSetChanged()
                                            }

                                        }

                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }
                                })
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}