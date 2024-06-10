package com.example.opsc_poe_part_3

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.opsc_poe_part_3.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var userEmail : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        //on button click to redirect to registration page
        binding.regPageBtn.setOnClickListener{var intent = Intent(this, RegistrationPage::class.java)
            startActivity(intent)
            finish()}
        binding.loginBtn.setOnClickListener{login()}


    }
    private fun login(){
        val email = binding.emailET.text.toString()
        val password = binding.passwordET.text.toString()

        //attempts to sign user in with email and password
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful)
            {val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    //if user is found, user id is extracted
                   userEmail = user.email.toString()
                }
                Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_SHORT).show()
                val db = FirebaseFirestore.getInstance()
                db.collection("categories").whereEqualTo("userEmail", userEmail).get().addOnSuccessListener {
                        categories -> if (categories.isEmpty)
                {
                    val intent = Intent (this, CategoriesPage::class.java)
                    startActivity(intent)
                    finish()
                }
                else
                {
                        val intent = Intent (this, Dashboard::class.java)
                        startActivity(intent)
                        finish()
                }
                }

            }
            else
            {
                Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
            }
        }

    }
}