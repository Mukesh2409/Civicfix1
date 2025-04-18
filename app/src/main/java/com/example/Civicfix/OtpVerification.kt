package com.example.Civicfix

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.Civicfix.databinding.ActivityOtpVerificationBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

class OtpVerification : AppCompatActivity() {
    private lateinit var binding: ActivityOtpVerificationBinding
    private lateinit var firebaseAuth: FirebaseAuth
    var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val type = intent.getStringExtra("Type")
        val userId = intent.getStringExtra("userId")
        token = intent.getStringExtra("Token")
        firebaseAuth = FirebaseAuth.getInstance()
        val phone = intent.getStringExtra("Phone")

        binding.buttonsubmitotp.setOnClickListener {
            val otp = binding.inputotp1.text.toString() +
                    binding.inputotp2.text.toString() +
                    binding.inputotp3.text.toString() +
                    binding.inputotp4.text.toString() +
                    binding.inputotp5.text.toString() +
                    binding.inputotp6.text.toString()

            if (otp.length == 6 && token != null) {
                binding.progressbarVerifyOtp.visibility = View.VISIBLE
                val credential = PhoneAuthProvider.getCredential(token!!, otp)
                firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                    binding.progressbarVerifyOtp.visibility = View.GONE
                    if (task.isSuccessful) {
                        val updateMap = mapOf("verified" to "Yes")
                        FirebaseDatabase.getInstance().getReference("Verification")
                            .child(userId!!)
                            .updateChildren(updateMap)
                            .addOnCompleteListener {
                                when (type) {
                                    "User" -> {
                                        val intent = Intent(this@OtpVerification, user_home_page_activity::class.java)
                                        intent.putExtra("userId", userId)
                                        startActivity(intent)
                                        finish()
                                    }
                                    "Admin" -> {
                                        val intent = Intent(this@OtpVerification, home_page_activity::class.java)
                                        intent.putExtra("userId", userId)
                                        startActivity(intent)
                                        finish()
                                    }
                                    else -> {
                                        Toast.makeText(this@OtpVerification, "Unexpected Type", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                    } else {
                        Toast.makeText(this@OtpVerification, "Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this@OtpVerification, "Please enter a valid OTP", Toast.LENGTH_SHORT).show()
            }
        }

        binding.textresendotp.setOnClickListener {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone.toString(),
                60,
                TimeUnit.SECONDS,
                this@OtpVerification,
                object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        signInWithPhoneAuthCredential(credential)
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        Toast.makeText(this@OtpVerification, "Verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

                    override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                        this@OtpVerification.token = verificationId
                        Toast.makeText(this@OtpVerification, "OTP resent", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@OtpVerification, "Verification successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@OtpVerification, "Verification failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}