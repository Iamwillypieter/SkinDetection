package com.example.skindetection.user

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.MotionEvent
import android.widget.Toast
import com.example.skindetection.R
import com.example.skindetection.databinding.ActivityLoginBinding
import com.example.skindetection.home.HomeActivity
import com.example.skindetection.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var auth: FirebaseAuth
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        //Login Session
        val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putLong("lastLoginTime", System.currentTimeMillis())
        editor.apply()

        binding.tvToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmailLogin.text.toString()
            val password = binding.edtPasswordLogin.text.toString()

            //Validasi Email
            if(email.isEmpty()){
                binding.edtEmailLogin.error = "Email is required!!!"
                binding.edtEmailLogin.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.edtEmailLogin.error = "Email is not valid"
                binding.edtEmailLogin.requestFocus()
                return@setOnClickListener
            }

            //Validasi Password
            if(password.isEmpty()){
                binding.edtPasswordLogin.error = "Password is required!!!"
                binding.edtPasswordLogin.requestFocus()
                return@setOnClickListener
            }

            LoginFirebase(email,password)
        }

        binding.edtPasswordLogin.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.edtPasswordLogin.right - binding.edtPasswordLogin.compoundDrawables[2].bounds.width())) {
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun LoginFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    SessionManager.saveLoginTime(this)
                    Toast.makeText(this, "$email Selamat Datang", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Sembunyikan password
            binding.edtPasswordLogin.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.edtPasswordLogin.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_lock, 0)
        } else {
            // Tampilkan password
            binding.edtPasswordLogin.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.edtPasswordLogin.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_open, 0)
        }
        isPasswordVisible = !isPasswordVisible
        binding.edtPasswordLogin.setSelection(binding.edtPasswordLogin.text.length) // Pindah cursor ke akhir teks
    }
}