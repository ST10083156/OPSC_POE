package com.example.opsc_poe_part_3

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.opsc_poe_part_3.databinding.ActivityTimesheetDetailsBinding
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

class TimesheetDetails : AppCompatActivity() {
    private lateinit var binding:ActivityTimesheetDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityTimesheetDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val receivedName = intent.getStringExtra("name")
        val receivedDate = intent.getLongExtra("date",0)
        val receivedCategory = intent.getStringExtra("category")
        val receivedDescription = intent.getStringExtra("description")
        val receivedTimeSpent = intent.getLongExtra("timeSpent",0)
        val receivedImage = intent.getStringExtra("image")
        val finalReceivedDate = Date(receivedDate)
        var timeInMinutes = receivedTimeSpent?.toLong()?.div(1000)?.div(60)
        binding.nameTV.text = receivedName.toString()
        binding.dateTV.text = "Date: ${getDateFromDateObject(finalReceivedDate)}"
        binding.categoryTV.text = "Category: ${receivedCategory.toString()}"
        binding.descriptionTV.text = "Description: ${receivedDescription.toString()}"
        binding.timeSpentTV.text = "Minutes worked on: ${timeInMinutes.toString()}"
        binding.entryImageView.setImageURI(Uri.parse(receivedImage))
        if(receivedImage==null){
            binding.entryImageView.setImageDrawable(R.drawable.ic_launcher_foreground.toDrawable())
        }

        binding.returnBtn.setOnClickListener{
            var intent = Intent(this,TimesheetEntriesDisplay::class.java)
            startActivity(intent)
            finish()
        }


    }


    fun getDateFromDateObject(date: Date): String {
        val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        return localDate.format(dateFormatter)
    }


}