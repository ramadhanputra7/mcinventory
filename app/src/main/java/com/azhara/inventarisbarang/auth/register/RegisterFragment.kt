package com.azhara.inventarisbarang.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.azhara.inventarisbarang.R
import com.azhara.inventarisbarang.auth.viewmodel.AuthViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment(), View.OnClickListener {

    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[AuthViewModel::class.java]
        btn_register.setOnClickListener(this)
        tv_to_login.setOnClickListener(this)
    }

    private fun register(
        name: String?,
        position: String?,
        email: String?,
        telephone: String,
        password: String?
    ) {
        authViewModel.register(name, position, email, telephone, password)

        authViewModel.registerState().observe(viewLifecycleOwner, Observer { registerState ->
            if (registerState == true){
                loading(false)
                view?.findNavController()?.navigate(R.id.action_navigation_register_fragment_to_navigation_register_success_fragment)
            }else{
                loading(false)
                context?.let { Toasty.error(it, "Registrasi gagal", Toast.LENGTH_LONG, true).show() }
            }
        })
    }

    private fun registerCheck(){
        loading(true)
        input_layout_name_register.error = null
        input_layout_position_register.error = null
        input_layout_email_register.error = null
        input_layout_phone_register.error = null
        input_layout_password_register.error = null
        input_layout_password_confirm_register.error = null

        val name = edt_name_register.text.toString().trim()
        val position = edt_position_register.text.toString().trim()
        val email = edt_email_register.text.toString().trim()
        val telephone = edt_phone_register.text.toString().trim()
        val password = edt_password_register.text.toString().trim()
        val passwordConfirm = edt_password_confirm_register.text.toString().trim()

        if (name.isEmpty()){
            loading(false)
            input_layout_name_register.error = getString(R.string.name_empty)
            return
        }

        if (position.isEmpty()){
            loading(false)
            input_layout_position_register.error = getString(R.string.position_empty)
            return
        }

        if (email.isEmpty()){
            loading(false)
            input_layout_email_register.error = getString(R.string.email_empty)
            return
        }

        if (telephone.isEmpty()){
            loading(false)
            input_layout_phone_register.error = getString(R.string.phone_empty)
            return
        }

        if (password.isEmpty()){
            loading(false)
            input_layout_password_register.error = getString(R.string.password_empty)
            return
        }

        if (passwordConfirm.isEmpty()){
            loading(false)
            input_layout_password_confirm_register.error = getString(R.string.password_confirm_empty)
            return
        }

        if (password != passwordConfirm){
            loading(false)
            input_layout_password_confirm_register.error = getString(R.string.password_confirm_no_match)
            return
        }

        if (name.isNotEmpty() && position.isNotEmpty()
            && email.isNotEmpty() && telephone.isNotEmpty()
            && password.isNotEmpty() && passwordConfirm.isNotEmpty()
            && password == passwordConfirm){
            register(name, position, email, telephone, password)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_register -> {
                registerCheck()
            }
            R.id.tv_to_login ->{
                view?.findNavController()?.navigate(R.id.action_navigation_register_fragment_to_navigation_login_fragment)
            }
        }
    }

    private fun loading(state: Boolean){
        if (state){
            btn_register.isEnabled = false
            tv_to_login.isEnabled = false
            loading_register.visibility = View.VISIBLE
        }else{
            loading_register.visibility = View.INVISIBLE
            btn_register.isEnabled = true
            tv_to_login.isEnabled = true
        }
    }

}