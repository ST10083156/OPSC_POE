package com.example.opsc_poe_part_3

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.opsc_poe_part_3.databinding.ActivityTimesheetEntriesDisplayBinding
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TimesheetEntriesDisplay : AppCompatActivity() {
    private lateinit var userEmail: String
    private lateinit var binding: ActivityTimesheetEntriesDisplayBinding
    private lateinit var timesheetList: MutableList<TimesheetEntry>
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
        var emptyTimesheetList = mutableListOf<TimesheetEntry>()
        val layoutManager = LinearLayoutManager(this)
       /* var adapter = MyAdapter(emptyTimesheetList,this, object:MyAdapter.OnItemClickListener{
            override fun onItemClick(item: TimesheetEntry) {
                var intent = Intent(applicationContext,TimesheetDetails::class.java)
                intent.putExtra("name", item.name)
                intent.putExtra("category", item.category.name)
                intent.putExtra("description", item.description)
                intent.putExtra("date", item.date.time)
                intent.putExtra("timeSpent", item.timeSpent.inWholeMilliseconds)
                intent.putExtra("image", item.image.toString())
                startActivity(intent)
                finish()
            }
        })*/
        binding.entriesRecyclerView.layoutManager = layoutManager
        getUserEntries(userEmail) { entries ->
           var adapter = MyAdapter(entries,this, object:MyAdapter.OnItemClickListener{
                override fun onItemClick(item: TimesheetEntry) {
                    var intent = Intent(applicationContext,TimesheetDetails::class.java)
                    intent.putExtra("name", item.name)
                    intent.putExtra("category", item.category.name)
                    intent.putExtra("description", item.description)
                    intent.putExtra("date", item.date.time)
                    intent.putExtra("timeSpent", item.timeSpent.inWholeMilliseconds)
                    intent.putExtra("image", item.image.toString())
                    startActivity(intent)
                    finish()

                }
            })
            binding.entriesRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()

        }
        binding.newEntryBtn.setOnClickListener{
            val intent = Intent(this,NewTimesheetEntry::class.java)
            startActivity(intent)
            finish()
        }
        binding.categoryBtn.setOnClickListener{
            val intent = Intent(this, CategoriesPage::class.java)
            startActivity(intent)
            finish()
        }
    }


    fun getUserEntries(userEmail: String, callback: (List<TimesheetEntry>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val categoriesRef = db.collection("timesheetEntries").whereEqualTo("userEmail", userEmail)

        categoriesRef.get()
            .addOnSuccessListener { documents ->
                timesheetList = mutableListOf<TimesheetEntry>()
                for (document in documents) {
                    document.toObject<TimesheetEntry>()?.let { entry ->
                        timesheetList.add(entry)
                    }
                }
                callback(timesheetList)
            }
            .addOnFailureListener { exception ->
                // Handle any errors here
                println("Error getting documents: $exception")
                callback(emptyList())
            }
    }

    //ViewHolder class just to hold references to each view in the list_item layout
    class ItemViewHolder(itemView: View,private val listener: MyAdapter.OnItemClickListener) : RecyclerView.ViewHolder(itemView) {

        val image: ShapeableImageView = itemView.findViewById(R.id.titleImage)
        val name: TextView = itemView.findViewById(R.id.timesheetNameTV)
        var category: TextView = itemView.findViewById(R.id.categoryTV)
        val description: TextView = itemView.findViewById(R.id.timesheetDescTV)
        val date: TextView = itemView.findViewById(R.id.dateTV)
        val button : Button = itemView.findViewById(R.id.itemDisplayBtn)


        fun bind(item: TimesheetEntry) {
            // Bind item data to views here
            name.text = item.name
            category.text = item.category.name
            description.text = item.description
            date.text = item.date.toString()
            button.setOnClickListener {
                listener.onItemClick(item)
            }

        }
    }
    //Adapter class to work with the views and assign values based on user data
    class MyAdapter(private val data: List<TimesheetEntry>,
                    private val activity: Activity,
                    private val listener: OnItemClickListener) :
        RecyclerView.Adapter<ItemViewHolder>() {
        interface OnItemClickListener {
            fun onItemClick(item: TimesheetEntry)
        }
        //creates viewholder objects to hold the views for manipulation
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.timesheet_list_item, parent, false)
            return ItemViewHolder(view, listener)
        }

        //used to bind values to the viewholder's properties
        override fun onBindViewHolder(holder: TimesheetEntriesDisplay.ItemViewHolder, position: Int) {
            val targetDateFormat = "dd/MM/yyyy"
            val currentItem = data[position]
            Picasso.get().load(currentItem.image).into(holder.image)
            holder.name.text = currentItem.name
            holder.category.text = "Category: ${currentItem.category.name}"
            holder.description.text = "Description: ${currentItem.description}"
            holder.date.text= "Date : ${convertToSimpleDateFormat(currentItem.date.toString(),"EEE MMM dd HH:mm:ss zzz yyyy",targetDateFormat)}"
            holder.bind(currentItem)
        }


        //returns the amount of entries in the data list
        override fun getItemCount(): Int {
            return data.size
        }

        fun convertTextToDate(inputText: String): Date {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return dateFormat.parse(inputText) ?: Date()
        }
        fun convertToSimpleDateFormat(originalDateString: String, originalFormat: String, targetFormat: String): String {
            try {
                // Define the original date format
                val originalDateFormat = SimpleDateFormat(originalFormat, Locale.getDefault())
                // Parse the original date string into a Date object
                val date = originalDateFormat.parse(originalDateString)
                // Define the target date format
                val targetDateFormat = SimpleDateFormat(targetFormat, Locale.getDefault())
                // Format the Date object using the target format and return the formatted date string
                return targetDateFormat.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the parsing error, for example, return the original string
                return originalDateString
            }
        }


    }

}

