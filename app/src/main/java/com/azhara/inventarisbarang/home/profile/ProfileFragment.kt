package com.azhara.inventarisbarang.home.profile

import android.content.Intent
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
import com.azhara.inventarisbarang.auth.AuthActivity
import com.azhara.inventarisbarang.home.profile.viewmodel.ProfileViewModel
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var profileViewModel: ProfileViewModel
    private var name: String? = null
    private var email: String? = null
    private var phone: String? = null
    private var position: String? = null
    private var imgUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_to_edit_profile.setOnClickListener(this)
        btn_to_change_password.setOnClickListener(this)
        btn_logout.setOnClickListener(this)
        back_button.setOnClickListener(this)
        profileViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ProfileViewModel::class.java]
        dataUser()
        getMessage()
    }

    private fun dataUser(){
        loading(true)
        profileViewModel.loadDataUser()

        profileViewModel.dataUser().observe(viewLifecycleOwner, Observer { data->
            if (data != null){
                loading(false)
                if (data.imgUrl != null){
                    context?.let { Glide.with(it).load(data.imgUrl).into(img_profile) }
                    imgUrl = data.imgUrl
                }
                tv_name_profile.text = "${data.name}"
                tv_email_profile.text = "${data.email}"
                tv_phone_profile.text = "${data.telephone}"
                tv_position_profile.text = "${data.position}"

                name = data.name
                email = data.email
                phone = data.telephone
                position = data.position
            }else{
                loading(false)
                context?.let { Toasty.error(it, "Error load data", Toast.LENGTH_LONG, true).show() }
            }
        })
    }

    private fun loading(state: Boolean){
        if (state){
            loading_profile.visibility = View.VISIBLE
        }else{
            loading_profile.visibility = View.INVISIBLE
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_to_edit_profile -> {
                toEditProfile()
            }
            R.id.btn_to_change_password -> {
                view?.findNavController()?.navigate(R.id.action_navigation_profile_fragment_to_navigation_change_password_fragment)
            }
            R.id.btn_logout -> {
                signOut()
            }
            R.id.back_button -> {
                activity?.onBackPressed()
            }
        }
    }

    private fun signOut(){
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(context, AuthActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        })
        activity?.finish()
    }

    private fun getMessage(){
        val message = ProfileFragmentArgs.fromBundle(arguments as Bundle).successMessage.toString()

        if (message != "success"){
            view?.let {
                Snackbar.make(it, message, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok") {}
                    .setBackgroundTint(resources.getColor(R.color.colorGreen))
                    .setActionTextColor(resources.getColor(R.color.colorWhite))
                    .show()
            }
        }
    }

    private fun toEditProfile(){
        if (name != null && email != null && position != null && phone != null){
            val toEditProfile = ProfileFragmentDirections
                .actionNavigationProfileFragmentToNavigationEditProfileFragment()
            if (imgUrl != null){
                toEditProfile.imgUrl = imgUrl
            }
            toEditProfile.name = name
            toEditProfile.phone = phone
            toEditProfile.position = position
            toEditProfile.email = email
            view?.findNavController()?.navigate(toEditProfile)

        }
    }
}