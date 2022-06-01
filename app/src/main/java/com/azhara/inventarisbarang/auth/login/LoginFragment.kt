package com.azhara.inventarisbarang.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.azhara.inventarisbarang.R
import com.azhara.inventarisbarang.auth.viewmodel.AuthViewModel
import com.azhara.inventarisbarang.home.HomeActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment(), View.OnClickListener {

    private lateinit var authViewModel: AuthViewModel

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            intentActivity()
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
        authViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[AuthViewModel::class.java]
        btn_login.setOnClickListener(this)
        btn_to_register.setOnClickListener(this)
        tv_forgot_password.setOnClickListener(this)
    }

    private fun login(email: String, password: String){
        authViewModel.login(email, password)

        authViewModel.loginState().observe(viewLifecycleOwner, Observer { loginState ->
            if (loginState == true){
                loading(false)
                intentActivity()
            }else{
                loading(false)
                if (authViewModel.loginMessage != null){
                    view?.let {
                        Snackbar.make(it, "${authViewModel.loginMessage}", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Coba Lagi") {}
                            .setBackgroundTint(resources.getColor(R.color.colorRed))
                            .show()
                    }
                }
            }
        })
    }

    private fun checkInput(){
        loading(true)
        val email = edt_email_login.text.toString().trim()
        val password = edt_password_login.text.toString().trim()

        if (email.isEmpty()){
            loading(false)
            input_layout_email_login.error = "Email tidak boleh kosong!"
            return
        }

        if (password.isEmpty()){
            loading(false)
            input_layout_password_login.error = "Password tidak boleh kosong!"
            return
        }

        if (email.isNotEmpty() && password.isNotEmpty()){
            login(email, password)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_login -> {
                input_layout_email_login.error = null
                input_layout_password_login.error = null
                checkInput()
            }
            R.id.btn_to_register -> {
                view?.findNavController()?.navigate(R.id.action_navigation_login_fragment_to_navigation_register_fragment)
            }
            R.id.tv_forgot_password -> {
                view?.findNavController()?.navigate(R.id.action_navigation_login_fragment_to_navigation_reset_fragment)
            }
        }
    }

    private fun loading(state: Boolean){
        if (state){
            loading_login.visibility = View.VISIBLE
            tv_forgot_password.isEnabled = false
            btn_login.isEnabled = false
            btn_to_register.isEnabled = false
        }else{
            loading_login.visibility = View.INVISIBLE
            tv_forgot_password.isEnabled = true
            btn_login.isEnabled = true
            btn_to_register.isEnabled = true
        }
    }

    private fun intentActivity(){
        startActivity(Intent(context, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        })
        activity?.finish()
        loading(false)
    }

}