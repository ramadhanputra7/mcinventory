package com.azhara.inventarisbarang.home.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.azhara.inventarisbarang.R
import com.azhara.inventarisbarang.home.profile.viewmodel.ProfileViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_change_password.*

class ChangePasswordFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ProfileViewModel::class.java]
        btn_change_password.setOnClickListener {
            checkResetPassword()
        }
    }

    private fun resetPassword(password: String?, newPassword: String?){
        password?.let {
            if (newPassword != null) {
                profileViewModel.changePassword(it, newPassword)
            }
        }

        profileViewModel.changePasswordState().observe(viewLifecycleOwner, Observer { data ->
            val errorMessage = profileViewModel.changePasswordMsgError
            if (data == true){
                val successChangePassword = ChangePasswordFragmentDirections
                    .actionNavigationChangePasswordFragmentToNavigationProfileFragment()
                successChangePassword.successMessage = getString(R.string.change_password_message)
                view?.findNavController()?.navigate(successChangePassword)
                loading(false)
            }
            if (data == false && errorMessage != null){
                loading(false)
                view?.let {
                    Snackbar.make(it, "$errorMessage", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Coba Lagi") {}
                        .setBackgroundTint(resources.getColor(R.color.colorRed))
                        .show()
                }
            }

        })
    }

    private fun checkResetPassword(){
        loading(true)
        input_layout_password_old.error = null
        input_layout_new_password.error = null
        input_layout_confirm_new_password.error = null
        val oldPassword = edt_password_old.text.toString().trim()
        val newPassword = edt_new_password.text.toString().trim()
        val confirmNewPassword = edt_confirm_new_password.text.toString().trim()

        if (oldPassword.isEmpty()){
            loading(false)
            input_layout_password_old.error = getString(R.string.password_old_empty)
            return
        }

        if (newPassword.isEmpty()){
            loading(false)
            input_layout_new_password.error = getString(R.string.password_new_empty)
            return
        }

        if (confirmNewPassword.isEmpty()){
            loading(false)
            input_layout_confirm_new_password.error = getString(R.string.password_confirm_empty)
            return
        }

        if (newPassword != confirmNewPassword){
            loading(false)
            input_layout_confirm_new_password.error = getString(R.string.password_confirm_no_match)
            return
        }

        if (oldPassword.isNotEmpty() && newPassword.isNotEmpty()
            && confirmNewPassword.isNotEmpty() && newPassword == confirmNewPassword){
            resetPassword(oldPassword, newPassword)
        }
    }

    private fun loading(state: Boolean){
        if (state){
            btn_change_password.isEnabled = false
            back_button.isEnabled = false
            loading_change_password.visibility = View.VISIBLE
        }else{
            btn_change_password.isEnabled = true
            back_button.isEnabled = true
            loading_change_password.visibility = View.INVISIBLE
        }
    }

}