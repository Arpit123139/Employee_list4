package com.example.employeeexample.data.detail

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.employeeexample.data.Employee


@Dao
interface EmployeeDetailDao{
    @Query("SELECT * FROM employee WHERE `id` = :id")
    fun getEmployee(id: Long): LiveData<Employee>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEmployee(employee: Employee): Long

    @Update
    suspend fun updateEmployee(employee: Employee)

    @Delete
    suspend fun deleteEmployee(employee: Employee)

}