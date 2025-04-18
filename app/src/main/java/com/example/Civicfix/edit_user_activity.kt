package com.example.Civicfix

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.Civicfix.databinding.EditUserBinding

class edit_user_activity : AppCompatActivity() {
    private lateinit var binding:EditUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_user)
        binding = EditUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userId = intent.getStringExtra("userId")
        binding.editUserAddUserBox.setOnClickListener{
            val intent= Intent(this@edit_user_activity,add_user_activity::class.java)
            intent.putExtra("userId",userId)
            startActivity(intent)
            finish()
        }
        binding.editUserExistingUserBox.setOnClickListener {
            val intent=Intent(this@edit_user_activity,existing_users::class.java)
            intent.putExtra("userId",userId)
            startActivity(intent)
            finish()
        }
    }
}