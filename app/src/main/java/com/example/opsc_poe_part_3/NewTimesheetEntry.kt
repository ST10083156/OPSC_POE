package com.example.opsc_poe_part_3

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.opsc_poe_part_3.databinding.ActivityNewTimesheetEntryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import java.io.ByteArrayOutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

class NewTimesheetEntry : AppCompatActivity() {
    private var seconds = 0
    private var timerRunning = false
    private var lastRunningState = false
    private var startTime = 0L
    private var elapsedTime = 0L
    private lateinit var buttonTimer: Button
    private lateinit var timesheetEntry: TimesheetEntry
    private lateinit var currentUserID: String
    private lateinit var binding: ActivityNewTimesheetEntryBinding
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var spinner: Spinner
    private lateinit var selectedDate: Date
    private var imageUri: Uri? = null
    private lateinit var categoriesList: MutableList<Category>

    private lateinit var userEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNewTimesheetEntryBinding.inflate(layoutInflater)
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
        } else {
            //if user not found then user will be returned to login screen
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

categoriesList = mutableListOf()
        getCategories(userEmail) { categoriesList ->
            val categoryNames = categoriesList.map { it.name }
            setupSpinner(categoryNames)
        }
        val categoryNames =
            categoriesList.map { it.name }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categoryNames
        )

        val buttonAddPicture: Button = binding.buttonAddPicture
        buttonAddPicture.setOnClickListener {
            pictureAdd()
        }
selectedDate = localDateToDate(LocalDate.now())
        val calendarView = binding.dateCalendar
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = convertTextToDate(calendarToSimpleDate(calendar))
        }
        buttonTimer = binding.buttonTimer
        buttonTimer.setOnClickListener {
            if (timerRunning) {
                // Stop the timer
                elapsedTime += System.currentTimeMillis() - startTime
                timerRunning = false
                buttonTimer.text = "Start Timer"
            } else {
                // Start the timer
                startTime = System.currentTimeMillis()
                timerRunning = true
                buttonTimer.text = "Stop Timer"
                runTimer()

            }
        }
        binding.doneBtn.setOnClickListener {
            if (binding.editTextName.text == null ||
                binding.categorySpinner.selectedItem == null ||
                binding.editTextDescription.text == null ||
                selectedDate == null ||
                binding.imageView.drawable == R.drawable.ic_launcher_foreground.toDrawable()
            ) {
                Toast.makeText(
                    this,
                    "Please fill all the relevant fields",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                if (binding.editTextName.text != null &&
                    binding.categorySpinner.selectedItem != null &&
                    binding.editTextDescription.text != null &&
                    selectedDate == null &&
                    binding.imageView.drawable == R.drawable.ic_launcher_foreground.toDrawable()
                ) {
                    timesheetEntry = TimesheetEntry(
                        name = binding.editTextName.text.toString(),
                        category = categoriesList[binding.categorySpinner.selectedItemPosition],
                        date = selectedDate,
                        description = binding.editTextDescription.text.toString(),
                        timeSpent = elapsedTime.milliseconds,
                        userEmail = userEmail,
                        image = null
                    )
                    addEntry(timesheetEntry)

                    val intent = Intent(this, TimesheetEntriesDisplay::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    timesheetEntry = TimesheetEntry(
                        name = binding.editTextName.text.toString(),
                        category = categoriesList[binding.categorySpinner.selectedItemPosition],
                        date = selectedDate,
                        description = binding.editTextDescription.text.toString(),
                        timeSpent = elapsedTime.milliseconds,
                        userEmail = userEmail,
                        image = imageUri
                    )

                    addEntry(timesheetEntry)

                    val intent = Intent(this, TimesheetEntriesDisplay::class.java)
                    startActivity(intent)
                    finish()
                }

            }
        }
    }

    private fun setupSpinner(categoryNames: List<String>) {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categoryNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinner = binding.categorySpinner
        spinner.adapter = adapter
    }

    fun localDateToDate(localDate: LocalDate): Date {
        val localDateTime: LocalDateTime = localDate.atStartOfDay()
        val instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant()
        return Date.from(instant)
    }

    fun convertToSimpleDate(originalDateString: String): String {
        // Define the original date format
        val originalDateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault())
        // Parse the original date string into a Date object
        val date = originalDateFormat.parse(originalDateString)
        // Define the desired date format
        val desiredDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        // Format the date into the desired format
        return desiredDateFormat.format(date)
    }

    fun calendarToSimpleDate(calendar: Calendar): String {
        // Get the Date object from the Calendar
        val date = calendar.time

        // Define the desired date format
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Format the Date object using the SimpleDateFormat
        return dateFormat.format(date)
    }

    fun convertTextToDate(inputText: String): Date {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        try {
            return dateFormat.parse(inputText) ?: throw ParseException("Invalid date format", 0)
        } catch (e: ParseException) {
            e.printStackTrace()
            // Handle the parsing error, for example, return a default date
            return Date() // Return current date as default
        }
    }
    private fun getImageUri(imageView: ImageView): Uri? {
        val drawable = imageView.drawable
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            val uri = getImageUriFromBitmap(imageView.context, bitmap)
            return uri
        }
        return null
    }

    private fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }

    //this function is used to add a picture from the gallery to the imageview
    fun pictureAdd() {

        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(intent, PICK_IMAGE_REQUEST)

    }

    //if the picture adds successfully, this function sets the imageview to the picture selected
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            binding.imageView.setImageURI(selectedImageUri)
            imageUri = selectedImageUri
        }
    }

    private fun runTimer() {
        val handler = android.os.Handler()

        handler.postDelayed(
            {
                var hours = seconds / 3600
                var minutes = (seconds % 3600) / 60
                var newSeconds = seconds % 60

                val time =
                    String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, newSeconds)
                binding.timerTV.text = time

                if (timerRunning) {
                    seconds++

                }


                runTimer() // Call runTimer again for recursive execution after 1 second delay
            },
            1000
        )

    }


    fun getCategories(userEmail: String, callback: (List<Category>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val categoriesRef = db.collection("categories").whereEqualTo("userEmail", userEmail)

        categoriesRef.get()
            .addOnSuccessListener { documents ->
                 categoriesList = mutableListOf<Category>()
                for (document in documents) {
                    document.toObject<Category>()?.let { category ->
                        categoriesList.add(category)
                    }
                }
                callback(categoriesList)
            }
            .addOnFailureListener { exception ->
                // Handle any errors here
                println("Error getting documents: $exception")
                callback(emptyList())
            }
    }



    private fun addEntry(timesheetEntry: TimesheetEntry) {
        val db = FirebaseFirestore.getInstance()
        val categoryCollection = db.collection("timesheetEntries")

        categoryCollection.add(timesheetEntry)
            .addOnSuccessListener { document ->

                Log.d(ContentValues.TAG, "Entry added ")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding entry", e)
            }

    }
}