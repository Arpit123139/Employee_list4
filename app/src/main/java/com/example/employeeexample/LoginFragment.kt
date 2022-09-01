package com.example.employeeexample

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {


    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth= FirebaseAuth.getInstance()
        if(auth.currentUser!=null){                              // which means user has succesflly logged in
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToEmployeeListFragment()
            )
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        register1.setOnClickListener{
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            )
        }

        login1.setOnClickListener{
            //
            //
            val email=user_email1.text.toString()
            val pass=user_pass1.text.toString()

            //  if(validateInput(email,pass)){
            progress.visibility=View.VISIBLE

            auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(requireActivity()){
                progress.visibility=View.INVISIBLE
                if(it.isSuccessful){
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                    )
                }else{
                    val toast= Toast.makeText(
                        requireActivity(),
                        "Authentication failed:${it.exception?.message}",
                        Toast.LENGTH_LONG
                    )
                    toast.setGravity(Gravity.CENTER_VERTICAL,0,0)
                    toast.show()
                }
            }

            //  }
        }
    }

//    private fun validateInput(email:String,pass:String):Boolean{
//        var valid=true
//        if(email.isBlank()){
//            user_email_container.error="Please enter an emailAddress"
//            valid=false
//        }
//        if(pass.isBlank()){
//            user_pass_container.error="Please Enter Password"
//            valid=false
//        }else if(pass.length<8){
//            user_pass_container.error="Password show be 8 character or more"
//            valid=false
//        }
//        return valid
//    }


}