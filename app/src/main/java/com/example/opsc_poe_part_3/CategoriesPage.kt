package com.example.opsc_poe_part_3

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.opsc_poe_part_3.databinding.ActivityCategoriesPageBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CategoriesPage : AppCompatActivity() {
    private lateinit var binding : ActivityCategoriesPageBinding
    private lateinit var scrollView: ScrollView
    private lateinit var adapter: TimesheetEntriesDisplay.MyAdapter
    private lateinit var userEmail :String
    private lateinit var category : Category
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCategoriesPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            //if user is found, user id is extracted
            userEmail = user.email.toString()
        }

        val linearLayout =binding.scrollViewLayout
        binding.addBtn.setOnClickListener{
            if(binding.categoryET.text!=null){
                category = Category(binding.categoryET.text.toString(),userEmail)
                addCategory(category)
                binding.categoryET.text.clear()
                var categoryNameTV = TextView(this)
                categoryNameTV.text= category.name
                linearLayout.addView(categoryNameTV)



            }
            else {
                Toast.makeText(this,"Please fill in the category name", Toast.LENGTH_LONG)
            }
        }
}

    private fun addCategory(category: Category)
    {
        val db = FirebaseFirestore.getInstance()
        val categoryCollection = db.collection("categories")

        categoryCollection.add(category)
            .addOnSuccessListener { document ->

                Log.d(ContentValues.TAG, "Category added ")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding category", e)
            }

    }




}