package br.com.todolist.ui

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import br.com.todolist.databinding.ActivityAddTaskBinding
import br.com.todolist.datasource.TaskDataSource
import br.com.todolist.extensions.format
import br.com.todolist.extensions.text
import br.com.todolist.model.Task
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private  lateinit var binding: ActivityAddTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(TASK_ID)) {
            val taskId = intent.getIntExtra(TASK_ID, 0)
            TaskDataSource.findById(taskId)?.let {
                binding.tiTitle.text = it.title
                binding.tiDate.text = it.date
                binding.tiTimer.text = it.hour
            }
        }

        insertListeners()
    }

    private fun insertListeners() {
        binding.tiDate.editText?.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.addOnPositiveButtonClickListener {
                val timeZone = TimeZone.getDefault()
                val offset = timeZone.getOffset(Date().time) * -1
                binding.tiDate.text = Date(it + offset).format()
            }
            datePicker.show(supportFragmentManager, "DATE_PICKER_TAG")
        }

        binding.tiTimer.editText?.setOnClickListener {
            val timerPicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .build()
            timerPicker.addOnPositiveButtonClickListener {
                val minuto = if (timerPicker.minute in 0..9) "0${timerPicker.minute}" else timerPicker.minute
                val hora = if (timerPicker.hour in 0..9) "0${timerPicker.hour}" else timerPicker.hour

                binding.tiTimer.text = "$hora:$minuto"
            }

            timerPicker.show(supportFragmentManager, null)
        }
        binding.btnCriar.setOnClickListener {
            val task = Task(
                title = binding.tiTitle.text,
                date = binding.tiDate.text,
                hour = binding.tiTimer.text,
                id = intent.getIntExtra(TASK_ID, 0)
            )
            TaskDataSource.insertTask(task)
            setResult(Activity.RESULT_OK)
            finish()
        }
        binding.btnCancelar.setOnClickListener {
            finish()
        }
    }

    companion object {
        const val TASK_ID = "task_id"
    }

}