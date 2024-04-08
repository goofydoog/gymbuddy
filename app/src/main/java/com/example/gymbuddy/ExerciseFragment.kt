package com.example.gymbuddy

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class ExerciseFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val exerciseList = mutableListOf<Exercise>()
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exercise, container, false)

        db = FirebaseFirestore.getInstance()

        // Konfiguracja RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewExercises)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = ExerciseAdapter(exerciseList) { position ->
            //  usuwanie ćwiczenia z listy
            exerciseList.removeAt(position)
            recyclerView.adapter?.notifyItemRemoved(position)
        }
        recyclerView.adapter = adapter

        // Konfiguracja przycisku dodawania ćwiczeń
        val addButton = view.findViewById<FloatingActionButton>(R.id.circle_add_button)
        addButton.setOnClickListener {
            showAddExerciseDialog()
        }

        return view
    }

    private fun showAddExerciseDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.layout_add_exercise)

        val exerciseNameEditText = dialog.findViewById<EditText>(R.id.editTextExerciseName)
        val exerciseResultEditText = dialog.findViewById<EditText>(R.id.editTextExerciseResult)
        val addExerciseButton = dialog.findViewById<Button>(R.id.buttonAddExercise)

        addExerciseButton.setOnClickListener {
            // Dodawanie nowego ćwiczenia do listy
            val exerciseName = exerciseNameEditText.text.toString()
            val exerciseResult = exerciseResultEditText.text.toString()

            exerciseList.add(Exercise(exerciseName, exerciseResult))
            recyclerView.adapter?.notifyDataSetChanged()

            dialog.dismiss() // Zamknięcie okna dialogowego
        }

        dialog.show() // Wyświetlenie okna dialogowego
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Exercise"
    }

    data class Exercise(val name: String, val result: String)

    class ExerciseAdapter(private val exerciseList: List<Exercise>, private val onLongClick: (Int) -> Unit) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
            // Tworzenie widoku pojedynczego elementu listy
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.exercise_item, parent, false)
            return ExerciseViewHolder(view, onLongClick)
        }

        override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
            // Powiązanie danych ćwiczenia z widokiem
            val exercise = exerciseList[position]
            holder.bind(exercise)
        }

        override fun getItemCount(): Int {
            // Liczba elementów w liście
            return exerciseList.size
        }

        class ExerciseViewHolder(private val view: View, private val onLongClick: (Int) -> Unit) : RecyclerView.ViewHolder(view) {
            fun bind(exercise: Exercise) {
                // Przypisanie danych ćwiczenia do widoku
                val textViewName = view.findViewById<TextView>(R.id.textViewExerciseName)
                val textViewResult = view.findViewById<TextView>(R.id.textViewExerciseResult)
                textViewName.text = exercise.name
                textViewResult.text = exercise.result

                // usuwa dlugim kliknieciem
                view.setOnLongClickListener {
                    onLongClick(adapterPosition)
                    true
                }
            }
        }
    }
}

