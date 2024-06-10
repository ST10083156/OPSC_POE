package com.example.opsc_poe_part_3

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.opsc_poe_part_3.databinding.ActivityDashboardBinding

class Dashboard : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.timesheetBtn.setOnClickListener{ val intent = Intent(this, TimesheetEntriesDisplay::class.java)
                startActivity(intent)
                finish()}
        binding.dailyGoalsBtn.setOnClickListener{
            val intent = Intent(this, DailyGoals::class.java)
            startActivity(intent)
            finish()}
        binding.graphsBtn.setOnClickListener{
            val intent = Intent(this, CategoriesPage::class.java)
            startActivity(intent)
            finish()}




    }
}