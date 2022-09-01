package com.example.employeeexample.data.show

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.employeeexample.data.Employee

@Dao
interface EmployeeShowDao{
    @Query("SELECT * FROM employee WHERE `id` = :id")
    fun getEmployee(id: Long): LiveData<Employee>
}