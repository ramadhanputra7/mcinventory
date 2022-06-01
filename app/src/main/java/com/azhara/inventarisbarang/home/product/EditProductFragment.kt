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
import kotlinx.android.synthetic.main.fragment_edit_product.*
import java.io.ByteArrayOutputStream

class EditProductFragment : Fragment(), View.OnClickListener {

    private lateinit var productViewModel: ProductViewModel
    private var imgUri: Uri? = null
    private var bitmapImage: Bitmap? = null
    private var requestImg = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        back_button_edit_product.setOnClickListener(this)
        fab_choose_pic_edit_product_item.setOnClickListener(this)
        btn_edit_product.setOnClickListener(this)
        productViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ProductViewModel::class.java]
        setData()
        checkEditProductState()
        errorMessage()
    }

    private fun setData(){
        val name = EditProductFragmentArgs.fromBundle(arguments as Bundle).productName
        val totalItem = EditProductFragmentArgs.fromBundle(arguments as Bundle).totalItem
        val img = EditProductFragmentArgs.fromBundle(arguments as Bundle).productImg

        if (img != "productImg"){
            context?.let { Glide.with(it).load(img).into(img_edit_product_item) }
        }
        edt_edit_product_name.setText(name)
        edt_edit_total_item_product.setText(totalItem)
    }

    private fun editProduct(){
        loading(true)
        val productId = EditProductFragmentArgs.fromBundle(arguments as Bundle).productId
        val productName = edt_edit_product_name.text.toString().trim()
        val totalItem = edt_edit_total_item_product.text.toString().trim()

        if (productName.isEmpty()){
            loading(false)
            input_layout_edit_name_product_item.error = getString(R.string.product_name_empty)
            return
        }

        if (totalItem.isEmpty()){
            loading(false)
            input_layout_edit_number_product_item.error = getString(R.string.total_item_product_empty)
            return
        }

        if (productName.isNotEmpty() && totalItem.isNotEmpty()){
            if (imgUri != null){
                productViewModel.editProductWithImage(productName, totalItem.toInt(), productId, imageByteArray(bitmapImage))
            }else{
                productViewModel.editProductWithoutImage(productName, totalItem.toInt(), productId)
            }
        }
    }

    private fun checkEditProductState(){
        productViewModel.editProductState().observe(viewLifecycleOwner, Observer { state ->
            if (state == true){
                val bundle = Bundle()
                bundle.putString(AddProductFragment.EXTRA_MESSAGE, getString(R.string.edit_product_message))
                view?.findNavController()?.navigate(R.id.action_navigation_edit_product_fragment_to_navigation_product_fragment, bundle)
                loading(false)
            }else{
                loading(false)
                view?.let {
                    Snackbar.make(it, "Edit produk gagal!", Snackbar.LENGTH_INDEFINITE)
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
            activity?.let { Glide.with(it).load(imgUri).into(img_edit_product_item) }

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


    private fun loading(state: Boolean){
        if (state){
            loading_edit_product.visibility = View.VISIBLE
        }else{
            loading_edit_product.visibility = View.INVISIBLE
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.back_button_edit_product -> {
                activity?.onBackPressed()
            }
            R.id.fab_choose_pic_edit_product_item -> {
                openGallery()
            }
            R.id.btn_edit_product -> {
                editProduct()
            }
        }
    }

    private fun errorMessage(){
        if (productViewModel.errorMessageEdit != null){
            view?.let {
                Snackbar.make(it, "${productViewModel.errorMessageEdit}", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok") {
                    }
                    .setBackgroundTint(resources.getColor(R.color.colorGreen))
                    .setActionTextColor(resources.getColor(R.color.colorWhite))
                    .show()
            }
        }
    }
}