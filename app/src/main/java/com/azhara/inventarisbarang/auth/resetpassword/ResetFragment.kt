package com.azhara.inventarisbarang.auth.resetpassword

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
import kotlinx.android.synthetic.main.fragment_reset.*

class ResetFragment : Fragment() {

    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[AuthViewModel::class.java]

        btn_reset_password.setOnClickListener {
            checkReset()
        }
    }

    private fun resetPassword(email: String?){
        authViewModel.resetPassword(email)

        authViewModel.resetState().observe(viewLifecycleOwner, Observer { resetState ->
            if (resetState == true){
                loading(false)
                view?.findNavController()?.navigate(R.id.action_navigation_reset_fragment_to_navigation_check_email_fragment2)
            }else{
                context?.let { Toasty.error(it, "Gagal reset password!", Toast.LENGTH_LONG, true).show() }
            }
        })
    }

    private fun checkReset(){
        loading(true)
        val email = edt_email_reset_password.text.toString().trim()
        if (email.isEmpty()){
            loading(false)
            input_layout_email_reset_password.error = "Email tidak boleh kosong"
            return
        }
        if (email.isNotEmpty()){
            resetPassword(email)
        }
    }

    private fun loading(state: Boolean){
        if (state){
            loading_reset_password.visibility = View.VISIBLE
        }else{
            loading_reset_password.visibility = View.INVISIBLE
        }
    }

}