package com.example.employeeexample.ui.show


import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.employeeexample.R
import com.example.employeeexample.data.Employee
import com.example.employeeexample.data.Gender
import com.example.employeeexample.data.Role
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_employee_show.*

/**
 * A simple [Fragment] subclass.
 */
class EmployeeShowFragment : Fragment() {

    private lateinit var viewModel: EmployeeShowViewModel

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)


        viewModel = ViewModelProviders.of(this)
            .get(EmployeeShowViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employee_show, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val navController = NavHostFragment.findNavController(nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar_show
            .setupWithNavController(navController, appBarConfiguration)


        employee_photo.setImageResource(R.drawable.blank_photo)
        employee_photo.tag = ""

        val id = EmployeeShowFragmentArgs.fromBundle(
            requireArguments()
        ).id
        viewModel.setEmployeeId(id)

        viewModel.employee.observe(viewLifecycleOwner, Observer {
            it?.let{ setData(it) }
        })
    }

    private fun setData(employee: Employee){
        with(employee.photo){
            if(isNotEmpty()){
                employee_photo.setImageURI(Uri.parse(this))
                employee_photo.tag = this

            } else{
                employee_photo.setImageResource(R.drawable.blank_photo)
                employee_photo.tag = ""
            }
        }
        collapsing_toolbar.title = employee.name


        run loop@{
            Role.values().forEach {
                if (it.ordinal == employee.role) {
                    employee_role.text = it.name
                    return@loop
                }
            }
        }

        employee_age.text = getString (R.string.years, employee.age)

        when (employee.gender) {
            Gender.Male.ordinal -> {
                employee_gender.text = Gender.Male.name
            }
            Gender.Female.ordinal -> {
                employee_gender.text = Gender.Female.name
            }
            else -> {
                employee_gender.text = Gender.Other.name
            }
        }

        employee_responsibilities.text = employee.responsibility
        employee_experience.text = employee.experience
        employee_education.text = employee.education
        if(employee.phone > 0) {
            employee_phone.setText(employee.phone.toString())
        }
        employee_email.text = employee.email
        employee_address.text = employee.address
    }

}
