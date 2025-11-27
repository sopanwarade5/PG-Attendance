package com.example.pgattendance
import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pgattendance.data.AppDatabase
import com.example.pgattendance.data.Student
import com.example.pgattendance.databinding.ActivityMainBinding
import com.example.pgattendance.ui.StudentAdapter
import com.example.pgattendance.util.CsvExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StudentAdapter
    private val db by lazy { AppDatabase.getDatabase(this).studentDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = StudentAdapter(mutableListOf(), onPresentClick = { student -> markPresent(student) },
            onEditClick = { student -> showEditDialog(student) },
            onDeleteClick = { student -> deleteStudent(student) })

        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.adapter = adapter
        binding.btnAdd.setOnClickListener { showAddDialog() }
        binding.btnExport.setOnClickListener { exportCsv() }
        loadStudents()
    }

    private fun loadStudents() {
        lifecycleScope.launch {
            val list = withContext(Dispatchers.IO) { db.getAll() }
            adapter.update(list)
        }
    }

    private fun showAddDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_add_student, null)
        val nameEt = view.findViewById<android.widget.EditText>(R.id.etName)
        val mobileEt = view.findViewById<android.widget.EditText>(R.id.etMobile)
        val roomEt = view.findViewById<android.widget.EditText>(R.id.etRoom)
        AlertDialog.Builder(this).setTitle("Add Student").setView(view)
            .setPositiveButton("Save") { _, _ ->
                val name = nameEt.text.toString().trim()
                val mobile = mobileEt.text.toString().trim()
                val room = roomEt.text.toString().trim()
                if (name.isEmpty()) { Toast.makeText(this, "Name required", Toast.LENGTH_SHORT).show(); return@setPositiveButton }
                val s = Student(name = name, mobile = mobile, room = room)
                lifecycleScope.launch(Dispatchers.IO) { db.insert(s); loadStudents() }
            }.setNegativeButton("Cancel", null).show()
    }

    private fun showEditDialog(student: Student) {
        val view = layoutInflater.inflate(R.layout.dialog_add_student, null)
        val nameEt = view.findViewById<android.widget.EditText>(R.id.etName)
        val mobileEt = view.findViewById<android.widget.EditText>(R.id.etMobile)
        val roomEt = view.findViewById<android.widget.EditText>(R.id.etRoom)
        nameEt.setText(student.name); mobileEt.setText(student.mobile); roomEt.setText(student.room)
        AlertDialog.Builder(this).setTitle("Edit Student").setView(view)
            .setPositiveButton("Save") { _, _ ->
                val new = student.copy(name = nameEt.text.toString(), mobile = mobileEt.text.toString(), room = roomEt.text.toString())
                lifecycleScope.launch(Dispatchers.IO) { db.update(new); loadStudents() }
            }.setNegativeButton("Cancel", null).show()
    }

    private fun deleteStudent(student: Student) {
        AlertDialog.Builder(this).setTitle("Delete").setMessage("Delete ${'$'}{student.name}?")
            .setPositiveButton("Yes") { _, _ -> lifecycleScope.launch(Dispatchers.IO) { db.delete(student); loadStudents() } }
            .setNegativeButton("No", null).show()
    }

    private fun markPresent(student: Student) {
        val now = System.currentTimeMillis()
        val updated = student.copy(lastPresentDate = now)
        lifecycleScope.launch(Dispatchers.IO) { db.update(updated); loadStudents() }
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        Toast.makeText(this, "${'$'}{student.name} marked present at ${'$'}{sdf.format(Date(now))}", Toast.LENGTH_SHORT).show()
    }

    private fun exportCsv() {
        lifecycleScope.launch(Dispatchers.IO) {
            val students = db.getAll()
            val rows = mutableListOf<List<String>>()
            rows.add(listOf("ID","Name","Mobile","Room","LastPresent"))
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            students.forEach { rows.add(listOf(it.id.toString(), it.name, it.mobile, it.room, it.lastPresentDate?.let { d -> sdf.format(Date(d)) } ?: "")) }
            val file = CsvExporter.exportStudentsToCsv(this@MainActivity, rows, "students_${System.currentTimeMillis()}.csv")
            withContext(Dispatchers.Main) { Toast.makeText(this@MainActivity, "Exported: ${'$'}{file.absolutePath}", Toast.LENGTH_LONG).show() }
        }
    }
}
