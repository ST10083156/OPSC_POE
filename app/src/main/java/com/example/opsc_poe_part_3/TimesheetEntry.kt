package com.example.opsc_poe_part_3

import android.net.Uri
import java.util.Date
import kotlin.time.Duration

data class TimesheetEntry(
    val userEmail:String,
    val name:String,
    var date:Date,
    val category: Category,
    val description:String,
    val timeSpent: Duration,
    val image: Uri?,
)
