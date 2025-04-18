package com.example.Civicfix

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.Civicfix.databinding.ActivityUserProfileViewBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class user_profile_view : AppCompatActivity() {
    private lateinit var binding:ActivityUserProfileViewBinding
    private lateinit var database:FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUserProfileViewBinding.inflate(layoutInflater)

        val adminId=intent.getStringExtra("userId")
        val userId=intent.getStringExtra("currentUserId")
        Toast.makeText(this@user_profile_view,"$userId.toString()  $adminId",Toast.LENGTH_SHORT).show()
        database=FirebaseDatabase.getInstance()
        val userRef=database.getReference("UserData").child(userId!!)
        val name=binding.userProfileViewNameText
        val phone=binding.userProfileViewPhoneNoText
        val email=binding.userProfileViewEmailText
        val age=binding.userProfileViewAgeText
        val totalComplains=binding.userProfileViewTotalComplainsText
        val pendingComplains=binding.userProfileViewPendingComplainsText
        val resolvedComplains=binding.userProfileViewResolvedComplainsText
        val flatNo=binding.userProfileViewFlatNoText
        val buildingNo=binding.userProfileViewBuildingNoText
        val buildingName=binding.userProfileViewBuildingNameText
        setContentView(binding.root)
        userRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData=snapshot.getValue(UserData::class.java)
                name.text=userData?.name.toString()
                phone.text=userData?.number.toString()
                email.text=userData?.email.toString()
                age.text=userData?.age.toString()
                totalComplains.text=(userData?.complainResolved?:0 +(userData?.ComplainsPending?.toInt()?:0)).toString()
                pendingComplains.text=userData?.ComplainsPending.toString()
                resolvedComplains.text=userData?.complainResolved.toString()
                flatNo.text=userData?.flatNo.toString()
                buildingName.text=userData?.buildingName.toString()
                buildingNo.text=userData?.buildingNo.toString()
                totalComplains.setOnClickListener {
                    val intent= Intent(this@user_profile_view,User_View_Complain::class.java)
                    intent.putExtra("userId",userId)
                    intent.putExtra("Type","totalComplains")
                    startActivity(intent)
                    finish()
                }
                pendingComplains.setOnClickListener {
                    val intent=Intent(this@user_profile_view,User_View_Complain::class.java)
                    intent.putExtra("userId",userId)
                    intent.putExtra("Type","pendingComplains")
                    startActivity(intent)
                    finish()
                }
                resolvedComplains.setOnClickListener {
                    val intent=Intent(this@user_profile_view,User_View_Complain::class.java)
                    intent.putExtra("userId",userId)
                    intent.putExtra("Type","resolvedComplains")
                    startActivity(intent)
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}