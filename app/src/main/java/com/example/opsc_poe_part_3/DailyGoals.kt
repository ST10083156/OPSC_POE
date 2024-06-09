package com.example.opsc_poe_part_3

import DailyGoal
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class DailyGoals : AppCompatActivity() {
    private lateinit var userEmail: String
    private lateinit var goalsList: MutableList<DailyGoal>
    private lateinit var goalsCountTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_goals)
        enableEdgeToEdge()

        goalsCountTextView = findViewById(R.id.goalsCountTextView)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            userEmail = user.email.toString()
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val layoutManager = LinearLayoutManager(this)
        val recyclerView: RecyclerView = findViewById(R.id.goalsRecyclerView)
        recyclerView.layoutManager = layoutManager

        getUserGoals(userEmail) { goals ->
            goalsList = goals.toMutableList()
            updateGoalsCount()
            val adapter = GoalsAdapter(goalsList, this)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }

        val addGoalButton: Button = findViewById(R.id.addGoalButton)
        addGoalButton.setOnClickListener {
            showAddGoalDialog()
        }
    }

    private fun getUserGoals(userEmail: String, callback: (List<DailyGoal>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val goalsRef = db.collection("dailyGoals").whereEqualTo("userEmail", userEmail)

        goalsRef.get()
            .addOnSuccessListener { documents ->
                val goals = mutableListOf<DailyGoal>()
                for (document in documents) {
                    document.toObject(DailyGoal::class.java)?.let { goal ->
                        goals.add(goal)
                    }
                }
                callback(goals)
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
                callback(emptyList())
            }
    }

    private fun showAddGoalDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.activity_dialog_add_goal, null)
        val goalEditText = dialogLayout.findViewById<EditText>(R.id.goalEditText)

        builder.apply {
            setTitle("Add Daily Goal")
            setPositiveButton("Add") { dialog, which ->
                val goalText = goalEditText.text.toString()
                if (goalText.isNotEmpty()) {
                    addGoal(DailyGoal(goal = goalText, date = Date().time))
                }
            }
            setNegativeButton("Cancel", null)
            setView(dialogLayout)
            show()
        }
    }

    private fun addGoal(goal: DailyGoal) {
        val db = FirebaseFirestore.getInstance()
        val goalWithUser = hashMapOf(
            "userEmail" to userEmail,
            "goal" to goal.goal,
            "date" to goal.date,
            "status" to goal.status
        )

        db.collection("dailyGoals")
            .add(goalWithUser)
            .addOnSuccessListener { documentReference ->
                goalWithUser["id"] = documentReference.id
                goalsList.add(goal)
                updateGoalsCount()
                val recyclerView: RecyclerView = findViewById(R.id.goalsRecyclerView)
                recyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }


    private fun updateGoalsCount() {
        val doneCount = goalsList.count { it.status == "Done" }
        goalsCountTextView.text = "Goals: $doneCount/${goalsList.size}"
    }

    class GoalsAdapter(private val data: List<DailyGoal>, private val activity: AppCompatActivity) :
        RecyclerView.Adapter<GoalsAdapter.GoalViewHolder>() {
        class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val goalText: TextView = itemView.findViewById(R.id.goalTextView)
            val dateText: TextView = itemView.findViewById(R.id.dateTextView)
            val statusButton: Button = itemView.findViewById(R.id.statusButton)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_goal_list_item, parent, false)
            return GoalViewHolder(view)
        }

        override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
            val currentItem = data[position]
            holder.goalText.text = currentItem.goal
            holder.dateText.text =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(currentItem.date))
            holder.statusButton.text = currentItem.status

            holder.statusButton.setOnClickListener {
                showStatusDialog(position)
            }
        }

        override fun getItemCount() = data.size

        private fun showStatusDialog(position: Int) {
            val statusOptions = arrayOf("In Progress", "Done")
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Select Status")
            builder.setItems(statusOptions) { dialog, which ->
                val selectedStatus = statusOptions[which]
                updateStatus(position, selectedStatus)
            }
            builder.setNegativeButton("Cancel", null)
            builder.show()
        }

        private fun updateStatus(position: Int, status: String) {
            data[position].status = status
            notifyItemChanged(position)
            (activity as DailyGoals).updateGoalsCount() // Update the goals count in the main activity
        }
    }
}
