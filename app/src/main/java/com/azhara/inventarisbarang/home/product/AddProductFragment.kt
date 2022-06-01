package com.azhara.inventarisbarang.home.product

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
import com.azhara.inventarisbarang.home.product.viewmodel.ProductViewModel
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_add_product.*
import java.io.ByteArrayOutputStream

class AddProductFragment : Fragment(), View.OnClickListener {

    private lateinit var productViewModel: ProductViewModel
    private var imgUri: Uri? = null
    private var bitmapImage: Bitmap? = null
    private var requestImg = 1

    companion object{
        var EXTRA_MESSAGE: String? = "message"
    }

    override fun onStart() {
        super.onStart()
        EXTRA_MESSAGE = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab_choose_pic_add_product.setOnClickListener(this)
        btn_add_product.setOnClickListener(this)
        back_button_add_product.setOnClickListener(this)
        productViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ProductViewModel::class.java]

        checkAddProductState()
        errorMessage()
    }

    private fun addProduct(){
        loading(true)
        val productName = edt_add_name_product_item.text.toString().trim()
        val totalItem = edt_add_number_product.text.toString().trim()

        if (productName.isEmpty()){
            loading(false)
            input_layout_add_name_product.error = getString(R.string.product_name_empty)
            return
        }

        if (totalItem.isEmpty()){
            loading(false)
            input_layout_add_number_product.error = getString(R.string.total_item_product_empty)
            return
        }

        if (productName.isNotEmpty() && totalItem.isNotEmpty()){
            if (imgUri != null){
                productViewModel.addProductWithImage(productName, totalItem.toInt(), imageByteArray(bitmapImage))
            }else{
                productViewModel.addProductWithOutImage(productName, totalItem.toInt())
            }
        }
    }

    private fun checkAddProductState(){
        productViewModel.addProductState().observe(viewLifecycleOwner, Observer { state ->
            if (state == true){
                val bundle = Bundle()
                bundle.putString(EXTRA_MESSAGE, getString(R.string.add_product_message))
                view?.findNavController()?.navigate(R.id.action_navigation_add_product_fragment_to_navigation_product_fragment, bundle)
                loading(false)
            }else{
                loading(false)
                view?.let {
                    Snackbar.make(it, "Tambah produk gagal!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Coba lagi"){}
                        .setBackgroundTint(resources.getColor(R.color.colorRed))
                        .show()
                }
            }
        })
    }

    // Intent open gallery
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, requestImg)
    }

    private fun imageByteArray(bitmap: Bitmap?): ByteArray {
        val bitmapCompress = bitmap?.let { resizeBitmap(it, 300) } //resize bitmap file
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
            activity?.let { Glide.with(it).load(imgUri).into(img_add_product) }

            if (imgUri != null) {
                try {
                    if (Build.VERSION.SDK_INT < 28) {
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(activity?.contentResolver, imgUri)
                        Log.d("AddProductFragment", "bitmap android sdk < 28 $bitmap")
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
                        Log.d("AddProductFragment", "bitmap android sdk 28 $bitmap")
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
            R.id.fab_choose_pic_add_product -> {
                openGallery()
            }
            R.id.btn_add_product -> {
                addProduct()
            }
            R.id.back_button_add_product -> {
                activity?.onBackPressed()
            }
        }
    }

    private fun loading(state: Boolean){
        if (state){
            loading_add_product.visibility = View.VISIBLE
        }else{
            loading_add_product.visibility = View.INVISIBLE
        }
    }

    private fun errorMessage(){
        if (productViewModel.errorMessageAdd != null){
            view?.let {
                Snackbar.make(it, "${productViewModel.errorMessageAdd}", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok") {
                    }
                    .setBackgroundTint(resources.getColor(R.color.colorGreen))
                    .setActionTextColor(resources.getColor(R.color.colorWhite))
                    .show()
            }
        }
    }

}