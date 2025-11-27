package com.example.pgattendance.data
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "students")
data class Student(@PrimaryKey(autoGenerate = true) val id: Long = 0, val name: String, val mobile: String, val room: String, val lastPresentDate: Long? = null)
