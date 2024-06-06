package com.example.opsc_poe_part_3

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.opsc_poe_part_3.databinding.ActivityRegistrationPageBinding
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class RegistrationPage : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationPageBinding
    private lateinit var auth: FirebaseAuth

    //registration activity to create a new user auth in firebase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        binding.regBtn.setOnClickListener { register() }

        //redirects to login page should user already have an account
        binding.loginPageBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    //register function to create new user in firebase
    private fun register() {

        var email = binding.emailET.text.toString()
        var password = binding.passwordET.text.toString()


        //attempts to create new user in firebase auth using email and password
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {

            if (it.isSuccessful) {
                Toast.makeText(this, "Successfully Singed Up", Toast.LENGTH_SHORT).show()
                finish()
                var intent = Intent(this, Dashboard::class.java)
                startActivity(intent)
                finish()

            }
            else {
                Toast.makeText(this, "Sign Up Failed!", Toast.LENGTH_SHORT).show()

                Toast.makeText(this, "Email must be unique", Toast.LENGTH_SHORT).show()

                Toast.makeText(this, "Password must be 6 characters long!", Toast.LENGTH_SHORT).show()

            }
        }
    }
    }




