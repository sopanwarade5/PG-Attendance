package com.example.pgattendance.util
import android.content.Context
import java.io.File
import java.io.FileWriter
object CsvExporter {
    fun exportStudentsToCsv(context: Context, rows: List<List<String>>, filename: String): File {
        val dir = File(context.getExternalFilesDir(null), "exports")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, filename)
        val fw = FileWriter(file)
        rows.forEach { row -> fw.append(row.joinToString(",") { escapeCsv(it) }); fw.append('\n') }
        fw.flush(); fw.close(); return file
    }
    private fun escapeCsv(s: String): String {
        return if (s.contains(',') || s.contains('\n') || s.contains('"')) { """ + s.replace(""", """") + """ } else s
    }
}
