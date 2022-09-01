package com.example.employeeexample.data.detail

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.employeeexample.data.Employee
import com.example.employeeexample.data.EmployeeDatabase


class EmployeeDetailRepository(context: Application){
    private val employeeDetailDao: EmployeeDetailDao = EmployeeDatabase.getDatabase(
        context
    ).employeeDetailDao()

    fun getEmployee(id: Long): LiveData<Employee> {
        return employeeDetailDao.getEmployee(id)
    }

    suspend fun insertEmployee(employee: Employee): Long{
        return employeeDetailDao.insertEmployee(employee)
    }

    suspend fun updateEmployee(employee: Employee){
        employeeDetailDao.updateEmployee(employee)
    }

    suspend fun deleteEmployee(employee: Employee){
        employeeDetailDao.deleteEmployee(employee)
    }
}