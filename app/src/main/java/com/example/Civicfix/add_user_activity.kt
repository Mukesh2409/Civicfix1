package com.example.Civicfix

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.Civicfix.databinding.AddUserBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

class add_user_activity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: AddUserBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("userId") ?: ""
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.scrollableContent.addUserSubmitButton.setOnClickListener {
            val name = binding.scrollableContent.enterName.text.toString().trim()
            val age = binding.scrollableContent.enterAge.text.toString().trim()
            val email = binding.scrollableContent.enterEMailId.text.toString().trim()
            val number = binding.scrollableContent.enterMobileNumber.text.toString().trim()
            val flatNo = binding.scrollableContent.enterFlatNo.text.toString().trim()
            val buildingNo = binding.scrollableContent.enterBuildingNo.text.toString().trim()
            val buildingName = binding.scrollableContent.enterBuildingName.text.toString().trim()

            // Validate inputs
            if (name.isEmpty() || age.isEmpty() || email.isEmpty() || number.isEmpty() ||
                flatNo.isEmpty() || buildingNo.isEmpty() || buildingName.isEmpty()) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!number.matches(Regex("\\d{10}"))) {
                Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Register user with email & password
            auth.createUserWithEmailAndPassword(email, email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userUID = auth.currentUser!!.uid
                    val userData = UserData(
                        userId = userUID,
                        name = name,
                        age = age,
                        email = email,
                        flatNo = flatNo,
                        buildingNo = buildingNo,
                        buildingName = buildingName,
                        number = number
                    )

                    val verificationData = Verification(
                        type = "User",
                        verified = "No",
                        phone = number
                    )

                    // Save user data in Firebase
                    val userRef = database.reference.child("UserData").child(userUID)
                    userRef.setValue(userData)

                    val verificationRef = database.reference.child("Verification").child(userUID)
                    verificationRef.setValue(verificationData)

                    // Start phone verification
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91$number",
                        60, // Timeout duration
                        TimeUnit.SECONDS,
                        this@add_user_activity,
                        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                Log.d("AddUser", "Verification completed")
                                signInWithPhoneAuthCredential(credential)
                            }

                            override fun onVerificationFailed(e: FirebaseException) {
                                Log.e("AddUser", "Verification failed: ${e.message}")
                                Toast.makeText(
                                    this@add_user_activity,
                                    "Verification failed: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            override fun onCodeSent(
                                verificationId: String,
                                token: PhoneAuthProvider.ForceResendingToken
                            ) {
                                Log.d("AddUser", "onCodeSent triggered with verificationId: $verificationId")
                                val intent = Intent(this@add_user_activity, OtpVerification::class.java).apply {
                                    putExtra("userId", userUID)
                                    putExtra("Token", verificationId)
                                    putExtra("Phone", "+91$number")
                                    putExtra("Type", "User")
                                    putExtra("AdminId", userId) // Pass admin ID if needed
                                }
                                startActivity(intent)
                                finish()
                            }
                        }
                    )
                } else {
                    Toast.makeText(
                        this,
                        "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this@add_user_activity,
                        "Verification successful",
                        Toast.LENGTH_SHORT
                    ).show()
                    // You can navigate to another activity here if needed
                } else {
                    Toast.makeText(
                        this@add_user_activity,
                        "Verification failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}