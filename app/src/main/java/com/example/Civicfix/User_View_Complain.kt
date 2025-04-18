package com.example.Civicfix

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Civicfix.databinding.ActivityUserViewComplainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class User_View_Complain : AppCompatActivity(){

    private lateinit var database:FirebaseDatabase
    private lateinit var userRecyclerview:RecyclerView
    private lateinit var userArrayList:ArrayList<UserComplain>
    private lateinit var binding:ActivityUserViewComplainBinding
    private lateinit var userComplainViewAdapter: UserComplainViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserViewComplainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userRecyclerview=binding.userComplainList
        database= FirebaseDatabase.getInstance()
        val userId=intent.getStringExtra("userId")
        val type=intent.getStringExtra("Type")
        val complainRef=database.getReference("ComplainUser").child(userId!!)
        userRecyclerview.setHasFixedSize(true);
        userRecyclerview.layoutManager=LinearLayoutManager(this);

        userArrayList= ArrayList()
        userComplainViewAdapter= UserComplainViewAdapter(userArrayList)
        userRecyclerview.adapter=userComplainViewAdapter
        complainRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot){
                userArrayList.clear()

                for(compainSnapshot in dataSnapshot.children){
                    val userComplain=compainSnapshot.getValue(UserComplain::class.java)

                    userComplain?.let{
                        if(type=="totalComplains"){
                            userArrayList.add(it)
                        }else if(type=="pendingComplains"){
                            if(it.Complainsolved=="No"){
                                userArrayList.add(it)
                            }else{

                            }
                        }else if(type=="resolvedComplains"){
                            if(it.Complainsolved=="Yes"){
                                userArrayList.add(it)
                            }else{

                            }
                        }else{
                            Toast.makeText(this@User_View_Complain,"Wrong Type",Toast.LENGTH_SHORT).show()
                        }

                    }
                }
                userComplainViewAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError){
                Toast.makeText(this@User_View_Complain,"error",Toast.LENGTH_SHORT).show()
            }
        })

    }
}