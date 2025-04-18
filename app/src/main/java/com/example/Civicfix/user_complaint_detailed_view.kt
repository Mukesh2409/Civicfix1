package com.example.Civicfix

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.example.Civicfix.databinding.ActivityUserComplaintDetailedViewBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class user_complaint_detailed_view : AppCompatActivity() {
    private lateinit var binding:ActivityUserComplaintDetailedViewBinding
    private lateinit var database:FirebaseDatabase
    private lateinit var image: ImageView
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserComplaintDetailedViewBinding.inflate(layoutInflater)
        database=FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef=storage.getReferenceFromUrl("gs://androidproject-48ff7.firebasestorage.app")
        val complainId=intent.getStringExtra("complainId")
        val userId=intent.getStringExtra("userId")
        Toast.makeText(this@user_complaint_detailed_view,complainId,Toast.LENGTH_SHORT).show()
        Toast.makeText(this@user_complaint_detailed_view,userId,Toast.LENGTH_SHORT).show()

        setContentView(binding.root)
        val userRef=database.reference.child("UserData").child(userId!!)
        val complaintUserRef=database.reference.child("ComplainUser").child(userId!!).child(complainId!!)
        val type=binding.userComplaintDetailedViewTypeText
        val subject=binding.userComplaintDetailedViewSubjectText
        val description=binding.userComplaintDetailedViewDescriptionText
        val complaintRegisteredTime=binding.userComplaintDetailedViewComplaintRegisteredText
        val complaintSolvedTime=binding.userComplaintDetailedViewComplaintResolvedDateText
        image=binding.userComplaintDetailedViewImageSubmittedImageView
        val complaintSolvedSubmitButton=binding.userComplaintDetailedViewComplaintResolvedSuccessfullyButton
        complaintUserRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val complainData=snapshot.getValue(UserComplain::class.java)
                type.text= complainData?.ComplainType.toString()

                subject.text=complainData?.ComplainSubject.toString()
                description.text=complainData?.ComplainDescription.toString()
                complaintRegisteredTime.text=complainData?.complain_time.toString()
                complaintSolvedTime.text=complainData?.completed_solved_time.toString()
                val imageUrl= storageRef.child("$complainId.jpg")
                Toast.makeText(this@user_complaint_detailed_view,imageUrl.toString(),Toast.LENGTH_SHORT).show()
                imageUrl.downloadUrl.addOnSuccessListener { uri ->
                    // Load the image into the ImageView using an image loading library like Glide or Picasso
                    Picasso.get().load(uri).into(image)
                }.addOnFailureListener { exception ->
                    // Handle any errors that occur during the download
                    Log.e(TAG, "Error downloading image: ${exception.message}")
                }
                val complainSolved=complainData?.Complainsolved.toString()
                complaintSolvedSubmitButton.visibility=View.GONE
                if(complainSolved=="No"){
                    complaintSolvedSubmitButton.visibility= View.VISIBLE

                    complaintSolvedSubmitButton.setOnClickListener {
                        val currentTimeMillis = System.currentTimeMillis()

                        // Define the date format
                        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())

                        // Format the timestamp into a string
                        val formattedDate = dateFormat.format(Date(currentTimeMillis))
                        val solved="Yes"

                        val updates=HashMap<String,Any>()
                        updates["complainsolved"]=solved
                        updates["completed_solved_time"]=formattedDate
                        complaintUserRef.updateChildren(updates)
                            .addOnCompleteListener {
                                Toast.makeText(this@user_complaint_detailed_view, "Values updated successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                // Handle the update failure
                                Toast.makeText(this@user_complaint_detailed_view, "Failed to update values", Toast.LENGTH_SHORT).show()
                            }
                        userRef.addValueEventListener(object:ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val userData=snapshot.getValue(UserData::class.java)
                                val currentPendingCount = (userData?.ComplainsPending ) ?: 0
                                val newPendingCount = currentPendingCount - 1
                                val currentCompletedCount = (userData?.complainResolved ) ?: 0
                                val newCompletedCount = currentCompletedCount + 1

                                val updatesUser = HashMap<String, Any>()
                                updatesUser["complainsPending"] = newPendingCount
                                updatesUser["complainResolved"]=newCompletedCount

                                userRef.updateChildren(updatesUser)
                                    .addOnCompleteListener {
                                        Toast.makeText(this@user_complaint_detailed_view,it.toString(),Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this@user_complaint_detailed_view,it.message,Toast.LENGTH_SHORT).show()
                                    }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })

                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@user_complaint_detailed_view, error.message, Toast.LENGTH_SHORT).show()
            }
        })



    }

}