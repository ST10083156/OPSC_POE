package com.example.opsc_poe_part_3

import android.net.Uri
import java.util.Date
import kotlin.time.Duration
import java.time.LocalDate
import java.time.ZoneId


data class TimesheetEntry(
    val userEmail: String = "",
    val name: String = "",
    var date: Date = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
    val category: Category = Category(),
    val description: String = "",
    val timeSpent: Duration = Duration.ZERO,
    val image: Uri? = null
) {
    constructor() : this("", "", Date(), Category(), "", Duration.ZERO, null)
}

