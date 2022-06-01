package com.azhara.inventarisbarang.home.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.azhara.inventarisbarang.R
import com.azhara.inventarisbarang.home.profile.viewmodel.ProfileViewModel
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import java.io.ByteArrayOutputStream

class EditProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var profileViewModel: ProfileViewModel
    private var imgUri: Uri? = null
    private var bitmapImage: Bitmap? = null
    private var requestImg = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ProfileViewModel::class.java]

        setData()
        btn_edit_profile.setOnClickListener(this)
        fab_choose_pic_edit_product_item.setOnClickListener(this)
        back_button_edit_profile.setOnClickListener(this)
    }

    private fun setData(){
        val name = EditProfileFragmentArgs.fromBundle(arguments as Bundle).name
        val email = EditProfileFragmentArgs.fromBundle(arguments as Bundle).email
        val phone = EditProfileFragmentArgs.fromBundle(arguments as Bundle).phone
        val position = EditProfileFragmentArgs.fromBundle(arguments as Bundle).position
        val imgUrl = EditProfileFragmentArgs.fromBundle(arguments as Bundle).imgUrl

        tv_email_edit_profile.text = email
        edt_edit_name.setText(name)
        edt_edit_phone.setText(phone)
        edt_edit_position.setText(position)
        if (imgUrl != "img"){
            context?.let { Glide.with(it).load(imgUrl).into(img_edit_profile) }
        }
    }

    private fun editProfileCheck(){
        loading(true)
        input_layout_edit_name.error = null
        input_layout_edit_phone.error = null
        input_layout_edit_name_product_item.error = null

        val name = edt_edit_name.text.toString().trim()
        val phone = edt_edit_phone.text.toString().trim()
        val position = edt_edit_position.text.toString().trim()

        if (name.isEmpty()){
            loading(false)
            input_layout_edit_name.error = getString(R.string.name_empty)
            return
        }

        if (phone.isEmpty()){
            loading(false)
            input_layout_edit_phone.error = getString(R.string.phone_empty)
            return
        }

        if (position.isEmpty()){
            loading(false)
            input_layout_edit_name_product_item.error = getString(R.string.position_empty)
            return
        }

        if (name.isNotEmpty() && phone.isNotEmpty() && position.isNotEmpty()){
            editProfile(name, phone, position)
        }
    }

    private fun editProfile(
        name: String?,
        phone: String?,
        position: String?
    ) {
        if (imgUri != null){
            profileViewModel.editProfileWithImage(name, phone, position, imageByteArray(bitmapImage))
        }else{
            profileViewModel.editProfileWithoutImage(name, phone, position)
        }

        profileViewModel.editProfileState().observe(viewLifecycleOwner, Observer { editState ->
            if (editState == true){
                val successRegister = EditProfileFragmentDirections
                    .actionNavigationEditProfileFragmentToNavigationProfileFragment()
                successRegister.successMessage = getString(R.string.edit_profile_message)
                view?.findNavController()?.navigate(successRegister)
                loading(false)
            }else{
                loading(false)
                view?.let {
                    Snackbar.make(it, "Edit profil gagal!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Coba lagi"){}
                        .setBackgroundTint(resources.getColor(R.color.colorRed))
                        .show()
                }
            }
        })
    }

    private fun loading(state: Boolean){
        if (state){
            fab_choose_pic_edit_product_item.isEnabled = false
            btn_edit_profile.isEnabled = false
            back_button_edit_profile.isEnabled = false
            loading_edit_profile.visibility = View.VISIBLE
        }else{
            fab_choose_pic_edit_product_item.isEnabled = true
            btn_edit_profile.isEnabled = true
            back_button_edit_profile.isEnabled = true
            loading_edit_profile.visibility = View.INVISIBLE
        }
    }

    // Intent open gallery
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, requestImg)
    }

    private fun imageByteArray(bitmap: Bitmap?): ByteArray {
        val bitmapCompress = bitmap?.let { resizeBitmap(it, 200) } //resize bitmap file
        val baos = ByteArrayOutputStream()
        bitmapCompress?.compress(
            Bitmap.CompressFormat.JPEG,
            100,
            baos
        ) //compress bitmap extension to JPEG

        return baos.toByteArray()
    }

    // resize filebitmap with specific size
    private fun resizeBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width //get width image
        var height = image.height //get heigh image

        val bitMapRatio = width.toFloat() / height.toFloat()
        if (bitMapRatio > 1) {
            width = maxSize
            height = (width / bitMapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitMapRatio).toInt()
        }

        return Bitmap.createScaledBitmap(image, width, height, true)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == requestImg && data != null && data.data != null) {
            imgUri = data.data!!
            activity?.let { Glide.with(it).load(imgUri).into(img_edit_profile) }

            if (imgUri != null) {
                try {
                    if (Build.VERSION.SDK_INT < 28) {
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(activity?.contentResolver, imgUri)
                        Log.d("EditProfileFragment", "bitmap android sdk < 28 $bitmap")
                        bitmapImage = bitmap

                    } else {
                        val source =
                            activity?.contentResolver?.let {
                                ImageDecoder.createSource(
                                    it,
                                    imgUri!!
                                )
                            }
                        val bitmap = source?.let { ImageDecoder.decodeBitmap(it) }
                        Log.d("EditProfileFragment", "bitmap android sdk 28 $bitmap")
                        bitmapImage = bitmap
                    }
                } catch (e: Exception) {
                    Log.e("EditProfileFragment", "${e.message}")
                }
            }

        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_edit_profile -> {
                editProfileCheck()
            }
            R.id.fab_choose_pic_edit_product_item -> {
                openGallery()
            }
            R.id.back_button_edit_profile -> {
                activity?.onBackPressed()
            }
        }
    }
}