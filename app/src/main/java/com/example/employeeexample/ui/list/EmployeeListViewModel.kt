package com.example.employeeexample.ui.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.employeeexample.data.Employee
import com.example.employeeexample.data.list.EmployeeListRepository

class EmployeeListViewModel(application: Application): AndroidViewModel(application){
    private val repo: EmployeeListRepository =
        EmployeeListRepository(application)

    val employees: LiveData<List<Employee>> = repo.getEmployees()

    suspend fun insertEmployees(employees: List<Employee>){
        repo.insertEmployees(employees)
    }

    suspend fun getEmployeeList(): List<Employee>{
        return repo.getEmployeeList()
    }

}