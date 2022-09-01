package com.example.employeeexample.data.show

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.employeeexample.data.Employee
import com.example.employeeexample.data.EmployeeDatabase
import com.example.employeeexample.data.detail.EmployeeDetailDao

class EmployeeShowRepository(context: Application) {
    private val employeeShowDao: EmployeeDetailDao = EmployeeDatabase.getDatabase(
        context
    ).employeeDetailDao()

    fun getEmployee(id: Long): LiveData<Employee> {
        return employeeShowDao.getEmployee(id)
    }
}