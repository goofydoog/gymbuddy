package com.example.gymbuddy

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlanWorkOutFragment : Fragment() {

    private lateinit var addWorkoutButton: FloatingActionButton
    private val requestCodeMap = mutableMapOf<String, Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_planworkout, container, false)

        addWorkoutButton = view.findViewById(R.id.circle_add_button)
        addWorkoutButton.setOnClickListener {
            showDateTimePicker(view)
        }

        val containerForDateTimeViews = view.findViewById<LinearLayout>(R.id.containerForDateTimeViews)
        containerForDateTimeViews.visibility = View.VISIBLE


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Plan Your Workout"


    }


    private fun showDateTimePicker(parentView: View) {
        val currentCalendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            currentCalendar.set(year, month, dayOfMonth)
            val selectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(currentCalendar.time)

            val timePickerDialog = TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                currentCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                currentCalendar.set(Calendar.MINUTE, minute)
                val selectedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(currentCalendar.time)

                val notificationTime = currentCalendar.apply {
                    add(Calendar.HOUR_OF_DAY, -2)
                }.timeInMillis

                val requestCode = generateRequestCode(selectedDate, selectedTime)
                requestCodeMap["$selectedDate $selectedTime"] = requestCode

                if (hasScheduleExactAlarmPermission()) {
                    scheduleNotification(notificationTime, requestCode)
                } else {
                    requestScheduleExactAlarmPermission()
                }

                addDateTimeView(selectedDate, selectedTime)
            }, currentCalendar.get(Calendar.HOUR_OF_DAY), currentCalendar.get(Calendar.MINUTE), true)
            timePickerDialog.show()
        }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }

    private fun addDateTimeView(date: String, time: String) {
        val container = view?.findViewById<LinearLayout>(R.id.containerForDateTimeViews)
        val dateTimeView = LayoutInflater.from(context).inflate(R.layout.plan_workout_date_and_time, container, false)

        dateTimeView.findViewById<TextView>(R.id.textViewDateItem).text = date
        dateTimeView.findViewById<TextView>(R.id.textViewTimeItem).text = time

        dateTimeView.setOnLongClickListener { view ->
            requestCodeMap["$date $time"]?.let { requestCode ->
                cancelAlarm(requestCode)
                requestCodeMap.remove("$date $time")
            }
            container?.removeView(view)
            true
        }

        container?.addView(dateTimeView)
    }

    private fun scheduleNotification(notificationTime: Long, requestCode: Int) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, flags)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent)
        Toast.makeText(requireContext(), "You will receive a notification two hours before training", Toast.LENGTH_LONG).show()
    }

    private fun cancelAlarm(requestCode: Int) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent,
            PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
    }

    private fun generateRequestCode(date: String, time: String): Int {
        return "$date $time".hashCode()
    }

    private fun hasScheduleExactAlarmPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    private fun requestScheduleExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            startActivity(intent)
        }
    }
}
