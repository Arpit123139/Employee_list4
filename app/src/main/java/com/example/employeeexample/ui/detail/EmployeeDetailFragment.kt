package com.example.employeeexample.ui.detail


import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.employeeexample.BuildConfig
import com.example.employeeexample.R
import com.example.employeeexample.data.Employee
import com.example.employeeexample.data.Gender
import com.example.employeeexample.data.Role
import com.example.employeeexample.ui.detail.EmployeeDetailFragmentArgs
import com.example.employeeexample.ui.createFile
import com.example.employeeexample.ui.list.LATEST_EMPLOYEE_NAME_KEY
import com.example.employeeexample.ui.showToast
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_employee_detail.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


const val PERMISSION_REQUEST_CAMERA = 0
const val CAMERA_PHOTO_REQUEST = 1
const val GALLERY_PHOTO_REQUEST = 2

class EmployeeDetailFragment : Fragment() {

    private lateinit var viewModel: EmployeeDetailViewModel
    //For simplicity this variable is here but should be moved to ViewModel
    private var selectedPhotoPath: String = ""

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)


        viewModel = ViewModelProviders.of(this)
            .get(EmployeeDetailViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employee_detail, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val navController = findNavController(nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar_detail
            .setupWithNavController(navController, appBarConfiguration)



        toolbar_detail.inflateMenu(R.menu.detail_menu)
        toolbar_detail.setOnMenuItemClickListener {
            handleMenuItem(it)
        }

        val roles = mutableListOf<String>()
        Role.values().forEach { roles.add(it.name)}
        val arrayAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, roles)
        employee_role.adapter = arrayAdapter

        val ages = mutableListOf<Int>()
        for(i in 18 until 81){ ages.add(i) }
        employee_age.adapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, ages)

        employee_photo.setImageResource(R.drawable.blank_photo)
        employee_photo.tag = ""

        val id = EmployeeDetailFragmentArgs.fromBundle(
            requireArguments()
        ).id
        viewModel.setEmployeeId(id)

        viewModel.employee.observe(viewLifecycleOwner, Observer {
            it?.let{ setData(it) }
        })

        save_employee.setOnClickListener {
            saveEmployee()
        }

        delete_employee.setOnClickListener {
            deleteEmployee()
        }

        employee_photo.setOnClickListener{
            employee_photo.setImageResource(R.drawable.blank_photo)
            employee_photo.tag = ""
        }

        photo_from_camera.setOnClickListener{
            clickPhotoAfterPermission(it)
        }

        photo_from_gallery.setOnClickListener{
            pickPhoto()
        }
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

        employee_name.setText(employee.name)

        employee_role.setSelection(employee.role)
        employee_age.setSelection(employee.age - 18)


        when (employee.gender) {
            Gender.Male.ordinal -> {
                gender_male.isChecked = true
            }
            Gender.Female.ordinal -> {
                gender_female.isChecked = true
            }
            else -> {
                gender_other.isChecked = true
            }
        }

        employee_responsibilities.setText(employee.responsibility)
        employee_experience.setText(employee.experience)
        employee_education.setText(employee.education)
        if(employee.phone > 0) {
            employee_phone.setText(employee.phone.toString())
        }
        employee_email.setText(employee.email)
        employee_address.setText(employee.address)


    }

    private fun saveEmployee(){
        val name = employee_name.text.toString()
        val role = employee_role.selectedItemPosition
        val age = employee_age.selectedItemPosition + 18

        val selectedStatusButton =  gender_group.findViewById<RadioButton>(gender_group.checkedRadioButtonId)
        var gender = Gender.Other.ordinal
        if(selectedStatusButton.text == Gender.Male.name){
            gender = Gender.Male.ordinal
        } else if(selectedStatusButton.text == Gender.Female.name) {
            gender = Gender.Female.ordinal
        }
        val photo = employee_photo.tag as String

        val responsibilities = employee_responsibilities.text.toString()
        val experience = employee_experience.text.toString()
        val education = employee_education.text.toString()
        val email = employee_email.text.toString()
        val address = employee_address.text.toString()


        //do the validation here for empty values or values in specific formats
        if(name.isBlank()){
            activity!!.showToast(getString(R.string.employee_validate_error_message, "name"))
            return
        }
        if(responsibilities.isBlank()){
            activity!!.showToast(getString(R.string.employee_validate_error_message, "responsibilities"))
            return
        }
        if(experience.isBlank()){
            activity!!.showToast(getString(R.string.employee_validate_error_message, "experience"))
            return
        }
        if(education.isBlank()){
            activity!!.showToast(getString(R.string.employee_validate_error_message, "education"))
            return
        }
        val phone: Long
        try {
            phone = employee_phone.text.toString().toLong()
        } catch(ex: NumberFormatException){
            activity!!.showToast(getString(R.string.employee_validate_error_message, "valid phone"))
            return
        }

        if(email.isBlank()){
            activity!!.showToast(getString(R.string.employee_validate_error_message, "email"))
            return
        }
        //simple email validation
        if(email.indexOf("@") < 0){
            activity!!.showToast(getString(R.string.employee_validate_error_message, "valid email"))
            return
        }
        if(address.isBlank()){
            activity!!.showToast(getString(R.string.employee_validate_error_message, "address"))
            return
        }


        val employee = Employee(viewModel.employeeId.value!!, name, role, age, gender,
            responsibilities, experience, education, phone, email, address, photo)
        viewModel.saveEmployee(employee)

        val sharedPref = activity!!.getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()){
            putString(LATEST_EMPLOYEE_NAME_KEY, name)
            commit()
        }

        activity!!.onBackPressed()
    }

    private fun deleteEmployee(){
        viewModel.deleteEmployee()

        activity!!.onBackPressed()
    }

    private fun clickPhotoAfterPermission(view: View){
        if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED) {
            clickPhoto()
        } else {
            requestCameraPermission(view)
        }
    }

    private fun requestCameraPermission(view: View) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, Manifest.permission.CAMERA)) {
            val snack = Snackbar.make(view, "We need your permission to take a photo. " +
                    "When asked please give the permission", Snackbar.LENGTH_INDEFINITE)
            snack.setAction("OK", View.OnClickListener {
                requestPermissions(arrayOf(Manifest.permission.CAMERA),
                    PERMISSION_REQUEST_CAMERA
                )
            })
            snack.show()
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CAMERA
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                clickPhoto()
            } else {
                Toast.makeText(activity!!, "Permission denied to use camera",
                    Toast.LENGTH_SHORT). show()
            }
        }
    }

    private fun clickPhoto(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                val photoFile: File? = try {
                    createFile(
                        activity!!,
                        Environment.DIRECTORY_PICTURES,
                        "jpg"
                    )
                } catch (ex: IOException) {

                    Toast.makeText(activity!!, getString(R.string.create_file_error, ex.message),
                        Toast.LENGTH_SHORT).show()

                    null
                }
                photoFile?.also {
                    selectedPhotoPath = it.absolutePath
                    val photoURI: Uri = FileProvider.getUriForFile(
                        activity!!,
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent,
                        CAMERA_PHOTO_REQUEST
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == RESULT_OK){
            when(requestCode){
                CAMERA_PHOTO_REQUEST -> {
                    val file = File(selectedPhotoPath)
                    val uri = Uri.fromFile(file)
                    employee_photo.setImageURI(uri)
                    employee_photo.tag = uri.toString()
                }
                GALLERY_PHOTO_REQUEST ->{
                    val photoFile: File? = try {
                        createFile(
                            activity!!,
                            Environment.DIRECTORY_PICTURES,
                            "jpg"
                        )
                    } catch (ex: IOException) {
                        Toast.makeText(activity!!, getString(R.string.create_file_error, ex.message),
                            Toast.LENGTH_SHORT).show()
                        null
                    }
                    photoFile?.also {
                        selectedPhotoPath = it.absolutePath
                        val resolver = activity!!.applicationContext.contentResolver
                        resolver.openInputStream(data!!.data!!).use { stream ->
                            val output = FileOutputStream(photoFile)
                            stream!!.copyTo(output)
                        }
                        val uri = Uri.fromFile(photoFile)
                        employee_photo.setImageURI(uri)
                        employee_photo.tag = uri.toString()
                    }
                }
            }
        }
    }

    private fun pickPhoto(){
        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhotoIntent,
            GALLERY_PHOTO_REQUEST
        )

    }

    private fun handleMenuItem(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_share_data -> {
                shareEmployeeData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun shareEmployeeData(){
        val name = employee_name.text.toString()
        val role = employee_role.selectedItem.toString()
        val age = employee_age.selectedItemPosition + 18

        val selectedStatusButton =  gender_group.findViewById<RadioButton>(gender_group.checkedRadioButtonId)
        val gender = selectedStatusButton.text

        val shareText = getString(R.string.share_text, name,role,age,gender)

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, getString(R.string.share_data_title))
        startActivity(shareIntent)
    }
}
