package com.example.opsc_poe_part_3

import android.net.Uri

data class User(
    val userID : String,
    val  username : String,
    val email : String,
    val pictureReference : Uri?
)
