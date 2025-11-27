package com.example.pgattendance.data
import androidx.room.*
@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY name") suspend fun getAll(): List<Student>
    @Insert suspend fun insert(student: Student): Long
    @Update suspend fun update(student: Student)
    @Delete suspend fun delete(student: Student)
    @Query("DELETE FROM students") suspend fun deleteAll()
}
