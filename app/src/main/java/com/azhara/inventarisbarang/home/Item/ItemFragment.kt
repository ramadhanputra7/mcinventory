package com.azhara.inventarisbarang.home.Item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.azhara.inventarisbarang.R
import com.azhara.inventarisbarang.entity.Product
import com.azhara.inventarisbarang.home.Item.viewmodel.ItemViewModel
import com.azhara.inventarisbarang.home.product.adapter.ProductAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_item.*

class ItemFragment : Fragment() {

    private lateinit var itemViewModel: ItemViewModel
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ItemViewModel::class.java]
        productAdapter = ProductAdapter()
        loadData()
        onItemOptionClicked()
        statusMessage()
        backButton()
        errorMessage()
    }

    private fun backButton(){
        back_button_item.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun loadData(){
        loading(true)
        itemViewModel.getDataProduct()

        itemViewModel.productData().observe(viewLifecycleOwner, Observer { data ->
            if (data.isNotEmpty()){
                loading(false)
                emptyState(false)
                setData(data)
            }else{
                loading(false)
                emptyState(true)
            }
        })

    }

    private fun setData(data: List<Product>) {
        with(rv_item){
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = productAdapter
        }
        productAdapter.submitList(data)
    }

    private fun onItemOptionClicked(){
        productAdapter.setOnOptionClicked(object : ProductAdapter.OnOptionClicked{
            override fun onOptionClicked(product: Product) {
                dialogSetting(product)
            }
        })
    }

    private fun dialogSetting(product: Product) {
        val items = arrayOf(getString(R.string.barang_masuk), getString(R.string.barang_keluar))
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(resources.getString(R.string.logistic_setting))
                .setItems(items) { dialog, which ->
                    if (items[which] == getString(R.string.barang_masuk)){
                        val productDataIn = ItemFragmentDirections
                            .actionNavigationItemFragmentToNavigationItemInFragment()
                        productDataIn.productId = product.productId
                        productDataIn.productName = product.productName
                        productDataIn.totalItem = product.totalItem.toString()
                        productDataIn.imgUrl = product.imgUrl
                        view?.findNavController()?.navigate(productDataIn)
                    }else{
                        val productOut = ItemFragmentDirections
                            .actionNavigationItemFragmentToNavigationItemOutFragment()
                        productOut.productId = product.productId
                        productOut.productName = product.productName
                        productOut.totalItem = product.totalItem.toString()
                        productOut.imgUrl = product.imgUrl
                        view?.findNavController()?.navigate(productOut)
                    }
                }
                .show()
        }
    }

    private fun statusMessage(){
        val message = ItemFragmentArgs.fromBundle(arguments as Bundle).message
        if (message != "message"){
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

    private fun loading(state: Boolean){
        if (state){
            loading_product_item.visibility = View.VISIBLE
        }else{
            loading_product_item.visibility = View.INVISIBLE
        }
    }

    private fun emptyState(state: Boolean){
        if(state){
            layout_empty_product_item.visibility = View.VISIBLE
        }else{
            layout_empty_product_item.visibility = View.INVISIBLE
        }
    }

    private fun errorMessage(){
        if (itemViewModel.errorMessage != null){
            view?.let {
                context?.let { it1 -> ContextCompat.getColor(it1, R.color.colorGreen) }?.let { it2 ->
                    Snackbar.make(it, "${itemViewModel.errorMessage}", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Ok") {
                        }
                        .setBackgroundTint(it2)
                        .setActionTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
                        .show()
                }
            }
        }
    }

}