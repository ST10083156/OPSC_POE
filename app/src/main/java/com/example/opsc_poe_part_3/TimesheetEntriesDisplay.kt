package com.example.opsc_poe_part_3

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.opsc_poe_part_3.databinding.ActivityTimesheetEntriesDisplayBinding
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TimesheetEntriesDisplay : AppCompatActivity() {
    private lateinit var userEmail: String
    private lateinit var binding: ActivityTimesheetEntriesDisplayBinding
    private lateinit var timesheetList: List<TimesheetEntry>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimesheetEntriesDisplayBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            //if user is found, user id is extracted
            userEmail = user.email.toString()
        } else {
            //if user not found then user will be returned to login screen
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        getUserEntries(userEmail) { entries ->
            val adapter = MyAdapter(entries)
            binding.entriesRecyclerView.adapter = adapter
        }
        binding.newEntryBtn.setOnClickListener{
            val intent = Intent(this,NewTimesheetEntry::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun getUserEntries(userID: String, callback: (List<TimesheetEntry>) -> Unit) {
        val data = mutableListOf<TimesheetEntry>()
        val db = FirebaseFirestore.getInstance()

        db.collection("timesheetEntries")
            .whereEqualTo("userEmail", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val timesheetEntry = document.toObject(TimesheetEntry::class.java)
                    data.add(timesheetEntry)
                }
                // Callback is called with the data list
                callback(data)
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                //If failed the callback is called with an empty list
                callback(emptyList())
            }
    }

    //ViewHolder class just to hold references to each view in the list_item layout
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image: ShapeableImageView = itemView.findViewById(R.id.titleImage)
        val name: TextView = itemView.findViewById(R.id.timesheetNameTV)
        val category: TextView = itemView.findViewById(R.id.categoryTV)
        val description: TextView = itemView.findViewById(R.id.timesheetDescTV)
        val date: TextView = itemView.findViewById(R.id.dateTV)

    }

    //Adapter class to work with the views and assign values based on user data
    class MyAdapter(private val data: List<TimesheetEntry>) :
        RecyclerView.Adapter<ItemViewHolder>() {
        //creates viewholder objects to hold the views for manipulation
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.timesheet_list_item, parent, false)
            return ItemViewHolder(view)
        }

        //used to bind values to the viewholder's properties
        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val currentItem = data[position]
            Picasso.get().load(currentItem.image).into(holder.image)
            holder.name.text = currentItem.name
            //holder.category.text = currentItem.Category
            holder.description.text = currentItem.description
            currentItem.date = convertTextToDate(holder.date.text.toString())

        }

        //returns the amount of entries in the data list
        override fun getItemCount(): Int {
            return data.size
        }

        //formatter for string to date conversion
        fun convertTextToDate(inputText: String): Date {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return dateFormat.parse(inputText) ?: Date()
        }
    }
}