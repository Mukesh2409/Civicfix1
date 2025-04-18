package com.example.Civicfix

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.Civicfix.databinding.ActivitySignInBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.TimeUnit

class SignInActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

       
        binding.signUpUserButton.setOnClickListener {
            val intent = Intent(this@SignInActivity, add_user_activity::class.java)
            startActivity(intent)
            finish()
        }


        firebaseAuth = FirebaseAuth.getInstance()

        binding.signInButton.setOnClickListener {
            val email = binding.signInEmailText.text.toString()
            val pass = binding.signInPasswordText.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val firebaseUser = FirebaseAuth.getInstance().currentUser
                        val userId = firebaseUser?.uid
                        val typeRef = FirebaseDatabase.getInstance().getReference("Verification")
                            .child(userId.toString())

                        typeRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    val verificationData = dataSnapshot.getValue(Verification::class.java)
                                    if (verificationData != null) {
                                        when (verificationData.type) {
                                            "User" -> {
                                                when (verificationData.verified) {
                                                    "No" -> {
                                                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                                            "+91${verificationData.phone}",
                                                            60,
                                                            TimeUnit.SECONDS,
                                                            this@SignInActivity,
                                                            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                                                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                                                    signInWithPhoneAuthCredential(credential)
                                                                }

                                                                override fun onVerificationFailed(e: FirebaseException) {
                                                                    Toast.makeText(this@SignInActivity, "Verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                                                }

                                                                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                                                                    val intent = Intent(this@SignInActivity, OtpVerification::class.java)
                                                                    intent.putExtra("userId", userId)
                                                                    intent.putExtra("Token", verificationId)
                                                                    intent.putExtra("Phone", "+91${verificationData.phone}")
                                                                    intent.putExtra("Type", "User")
                                                                    startActivity(intent)
                                                                    finish()
                                                                }
                                                            }
                                                        )
                                                    }
                                                    "Yes" -> {
                                                        val intent = Intent(this@SignInActivity, user_home_page_activity::class.java)
                                                        intent.putExtra("userId", userId)
                                                        startActivity(intent)
                                                        finish()
                                                    }
                                                    else -> {
                                                        Toast.makeText(this@SignInActivity, "Unexpected verified type", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                            "Admin" -> {
                                                val intent = Intent(this@SignInActivity, home_page_activity::class.java)
                                                intent.putExtra("userId", userId)
                                                startActivity(intent)
                                                finish()
                                            }
                                            else -> {
                                                Toast.makeText(this@SignInActivity, "Unexpected user type", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        Toast.makeText(this@SignInActivity, "Admin data is null", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(this@SignInActivity, "User node does not exist", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(this@SignInActivity, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    } else {
                        Toast.makeText(this@SignInActivity, "Sign-in failed: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@SignInActivity, "Verification successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@SignInActivity, "Verification failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}