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
import com.azhara.inventarisbarang.R
import com.azhara.inventarisbarang.home.Item.viewmodel.ItemViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_item_in.*

class ItemInFragment : Fragment(), View.OnClickListener {

    private lateinit var itemViewModel: ItemViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_item_in.setOnClickListener(this)
        back_button_item_in.setOnClickListener(this)
        itemViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ItemViewModel::class.java]
        setDataNameProduct()
        reportState()
        errorMessage()
    }

    private fun setDataNameProduct(){
        val productName = ItemInFragmentArgs.fromBundle(arguments as Bundle).productName
        tv_name_item_in.text = productName
    }

    private fun itemInCheck(){
        input_layout_item_in.error = null
        loading(true)
        val itemIn = edt_total_item_in.text.toString().trim()

        if (itemIn.isEmpty()){
            loading(false)
            input_layout_item_in.error = getString(R.string.item_in_empty)
            return
        }

        if (itemIn.startsWith("0")){
            loading(false)
            input_layout_item_in.error = getString(R.string.item_number_error)
            return
        }

        if (itemIn.isNotEmpty() && !itemIn.startsWith("0")){
            updateTotalItemProduct(itemIn)
        }

    }

    private fun updateTotalItemProduct(itemIn: String?) {
        val productTotalItem = ItemInFragmentArgs.fromBundle(arguments as Bundle).totalItem
        val productId = ItemInFragmentArgs.fromBundle(arguments as Bundle).productId
        val imgUrl = ItemInFragmentArgs.fromBundle(arguments as Bundle).imgUrl
        val productName = ItemInFragmentArgs.fromBundle(arguments as Bundle).productName
        val totalItemNow = productTotalItem?.toInt()
        val totalItemIn = itemIn?.toInt()

        val totalItem = totalItemNow?.plus(totalItemIn!!)
        itemViewModel.itemIn(productId, totalItem, totalItemIn, imgUrl, productName)
    }

    private fun reportState(){
        itemViewModel.reportState().observe(viewLifecycleOwner, Observer { state ->
            if (state == true){
                loading(false)
                val itemInSuccess = ItemInFragmentDirections
                    .actionNavigationItemInFragmentToNavigationItemFragment()
                itemInSuccess.message = getString(R.string.product_item_in_report_message)
                view?.findNavController()?.navigate(itemInSuccess)
            }else{
                loading(false)
                view?.let {
                    Snackbar.make(it, "Gagal memasukan data item masuk!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Coba lagi"){}
                        .setBackgroundTint(resources.getColor(R.color.colorRed))
                        .show()
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_item_in -> {
                itemInCheck()
            }
            R.id.back_button_item_in -> {
                activity?.onBackPressed()
            }
        }
    }

    private fun loading(state: Boolean){
        if (state){
            loading_item_in.visibility = View.VISIBLE
        }else{
            loading_item_in.visibility = View.INVISIBLE
        }
    }

    private fun errorMessage(){
        if (itemViewModel.errorMessageItemIn != null){
            view?.let {
                context?.let { it1 -> ContextCompat.getColor(it1, R.color.colorGreen) }?.let { it2 ->
                    Snackbar.make(it, "${itemViewModel.errorMessageItemIn}", Snackbar.LENGTH_INDEFINITE)
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