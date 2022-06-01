package com.azhara.inventarisbarang.home.product

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.azhara.inventarisbarang.R
import com.azhara.inventarisbarang.entity.Product
import com.azhara.inventarisbarang.home.product.adapter.ProductAdapter
import com.azhara.inventarisbarang.home.product.viewmodel.ProductViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_product.*

class ProductFragment : Fragment(), View.OnClickListener {

    private lateinit var productViewModel: ProductViewModel
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_add_product.setOnClickListener(this)
        back_button_product.setOnClickListener(this)
        productViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ProductViewModel::class.java]
        productAdapter = ProductAdapter()
        getDataProduct()
        statusMessage()
        onItemOptionClicked()
        errorMessage()
    }

    private fun getDataProduct(){
        loading(true)
        productViewModel.getDataProduct()

        productViewModel.productData().observe(viewLifecycleOwner, Observer { data ->
            if (data.isNotEmpty()){
                loading(false)
                emptyState(false)
                setDataItem(data)
            }else{
                loading(false)
                emptyState(true)
            }
        })
    }

    private fun setDataItem(data: List<Product>) {
        with(rv_product){
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = productAdapter
        }
        productAdapter.submitList(data)
    }

    private fun emptyState(state: Boolean){
        if(state){
            layout_empty_product.visibility = View.VISIBLE
        }else{
            layout_empty_product.visibility = View.INVISIBLE
        }
    }

    private fun loading(state: Boolean){
        if (state){
            loading_product.visibility = View.VISIBLE
        }else{
            loading_product.visibility = View.INVISIBLE
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_add_product -> {
                view?.findNavController()?.navigate(R.id.action_navigation_product_fragment_to_navigation_add_product_fragment)
            }
            R.id.back_button_product -> {
                activity?.onBackPressed()
            }
        }
    }

    private fun statusMessage(){
        val message = arguments?.getString(AddProductFragment.EXTRA_MESSAGE)
        Log.d("message product", "$message")
        if (message != null){
            view?.let {
                Snackbar.make(it, "$message", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok") {
                    }
                    .setBackgroundTint(resources.getColor(R.color.colorGreen))
                    .setActionTextColor(resources.getColor(R.color.colorWhite))
                    .show()
            }
        }
    }

    private fun errorMessage(){
        if (productViewModel.errorMessage != null){
            view?.let {
                Snackbar.make(it, "Terjadi kesalahan!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok") {
                    }
                    .setBackgroundTint(resources.getColor(R.color.colorGreen))
                    .setActionTextColor(resources.getColor(R.color.colorWhite))
                    .show()
            }
        }
    }

    private fun onItemOptionClicked(){
        productAdapter.setOnOptionClicked(object : ProductAdapter.OnOptionClicked{
            override fun onOptionClicked(product: Product) {
                dialogSetting(product)
            }

        })
    }

    private fun dialogSetting(product: Product) {
        val items = arrayOf(getString(R.string.edit), getString(R.string.delete))
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(resources.getString(R.string.product_setting))
                .setItems(items) { _, which ->
                    if (items[which] == getString(R.string.edit)){
                        val productData = ProductFragmentDirections
                            .actionNavigationProductFragmentToNavigationEditProductFragment()
                        productData.productId = product.productId
                        productData.productName = product.productName
                        productData.totalItem = product.totalItem.toString()
                        if (product.imgUrl != null){
                            productData.productImg = product.imgUrl
                        }
                        view?.findNavController()?.navigate(productData)
                    }else{
                        alertDialogDelete(product.productName, product.productId)
                    }

                }
                .show()
        }
    }

    private fun alertDialogDelete(productName: String?, productId: String?) {
        MaterialAlertDialogBuilder(context!!, R.style.AlertDialogTheme)
            .setTitle(resources.getString(R.string.delete_product))
            .setMessage("${resources.getString(R.string.delete_product_subtitle)} $productName?")
            .setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->
                // Respond to negative button press
            }
            .setPositiveButton(resources.getString(R.string.delete)) { _, _ ->
                productViewModel.deleteProduct(productId)
            }
            .show()
    }

}