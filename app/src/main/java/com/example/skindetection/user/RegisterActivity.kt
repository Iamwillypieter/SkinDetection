package com.example.skindetection.user

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import com.example.skindetection.R
import com.example.skindetection.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.tvToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.btnRegister.setOnClickListener {
            val username = binding.edtUsernameRegister.text.toString().trim()
            val email = binding.edtEmailRegister.text.toString().trim()
            val password = binding.edtPasswordRegister.text.toString().trim()
            val confirmPassword = binding.edtConfirmPasswordRegister.text.toString().trim()
            val phoneNumber = binding.edtPhoneRegister.text.toString().trim()

            if (!validateInput(username, email, password, confirmPassword, phoneNumber)) return@setOnClickListener

            registerFirebase(username, email, password, phoneNumber)
        }

        // Toggle Password Visibility
        binding.edtPasswordRegister.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP &&
                event.rawX >= (binding.edtPasswordRegister.right - binding.edtPasswordRegister.compoundDrawables[2].bounds.width())
            ) {
                togglePasswordVisibility()
                return@setOnTouchListener true
            }
            false
        }

        // Toggle Confirm Password Visibility
        binding.edtConfirmPasswordRegister.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP &&
                event.rawX >= (binding.edtConfirmPasswordRegister.right - binding.edtConfirmPasswordRegister.compoundDrawables[2].bounds.width())
            ) {
                toggleConfirmPasswordVisibility()
                return@setOnTouchListener true
            }
            false
        }
    }


    private fun validateInput(username: String, email: String, password: String, confirmPassword: String, phone: String): Boolean {
        when {
            username.isEmpty() -> {
                binding.edtUsernameRegister.error = "Username is required!"
                binding.edtUsernameRegister.requestFocus()
                return false
            }
            email.isEmpty() -> {
                binding.edtEmailRegister.error = "Email is required!"
                binding.edtEmailRegister.requestFocus()
                return false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.edtEmailRegister.error = "Email is not valid!"
                binding.edtEmailRegister.requestFocus()
                return false
            }
            password.isEmpty() -> {
                binding.edtPasswordRegister.error = "Password is required!"
                binding.edtPasswordRegister.requestFocus()
                return false
            }
            password.length < 8 -> {
                binding.edtPasswordRegister.error = "Password must be at least 8 characters!"
                binding.edtPasswordRegister.requestFocus()
                return false
            }
            confirmPassword.isEmpty() -> {
                binding.edtConfirmPasswordRegister.error = "Confirm password is required!"
                binding.edtConfirmPasswordRegister.requestFocus()
                return false
            }
            confirmPassword != password -> {
                binding.edtConfirmPasswordRegister.error = "Passwords do not match!"
                binding.edtConfirmPasswordRegister.requestFocus()
                return false
            }
            phone.isEmpty() -> {
                binding.edtPhoneRegister.error = "Phone number is required!"
                binding.edtPhoneRegister.requestFocus()
                return false
            }
            phone.length < 10 -> {
                binding.edtPhoneRegister.error = "Phone number must be at least 10 digits!"
                binding.edtPhoneRegister.requestFocus()
                return false
            }
            else -> return true
        }
    }

    private fun registerFirebase(username: String, email: String, password: String, phone: String) {
        binding.btnRegister.isEnabled = false // Mencegah klik berulang
        Toast.makeText(this, "Registering...", Toast.LENGTH_SHORT).show()

        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { checkTask ->
            if (checkTask.isSuccessful) {
                val signInMethods = checkTask.result?.signInMethods
                if (!signInMethods.isNullOrEmpty()) {
                    Toast.makeText(this, "Email already registered!", Toast.LENGTH_SHORT).show()
                    binding.btnRegister.isEnabled = true
                    return@addOnCompleteListener
                }

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                            val user = hashMapOf(
                                "userId" to userId,
                                "username" to username,
                                "email" to email,
                                "phone" to phone
                            )

                            db.collection("users").document(userId)
                                .set(user)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Success Register", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, LoginActivity::class.java))
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Firestore Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    binding.btnRegister.isEnabled = true
                                }
                        } else {
                            Toast.makeText(this, "Auth Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            binding.btnRegister.isEnabled = true
                        }
                    }
            } else {
                Toast.makeText(this, "Failed to check email: ${checkTask.exception?.message}", Toast.LENGTH_SHORT).show()
                binding.btnRegister.isEnabled = true
            }
        }
    }



    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Ubah ke mode password (disembunyikan)
            binding.edtPasswordRegister.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.edtPasswordRegister.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_lock, 0)
        } else {
            // Ubah ke mode teks (terlihat)
            binding.edtPasswordRegister.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.edtPasswordRegister.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_open, 0)
        }
        isPasswordVisible = !isPasswordVisible
        binding.edtPasswordRegister.setSelection(binding.edtPasswordRegister.text?.length ?: 0)
    }

    private fun toggleConfirmPasswordVisibility() {
        if (isConfirmPasswordVisible) {
            binding.edtConfirmPasswordRegister.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.edtConfirmPasswordRegister.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_lock, 0)
        } else {
            binding.edtConfirmPasswordRegister.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.edtConfirmPasswordRegister.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_open, 0)
        }
        isConfirmPasswordVisible = !isConfirmPasswordVisible
        binding.edtConfirmPasswordRegister.setSelection(binding.edtConfirmPasswordRegister.text?.length ?: 0)
    }
}