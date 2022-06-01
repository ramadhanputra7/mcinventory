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
import kotlinx.android.synthetic.main.fragment_item_out.*

class ItemOutFragment : Fragment(), View.OnClickListener {

    private lateinit var itemViewModel: ItemViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_out, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_item_out.setOnClickListener(this)
        back_button_item_out.setOnClickListener(this)
        itemViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ItemViewModel::class.java]
        setDataNameProduct()
        reportState()
        errorMessage()
    }

    private fun setDataNameProduct(){
        val productName = ItemOutFragmentArgs.fromBundle(arguments as Bundle).productName
        tv_name_item_out.text = productName
    }


    private fun itemInCheck(){
        input_layout_item_out.error = null
        loading(true)
        val itemIn = edt_total_item_out.text.toString().trim()

        if (itemIn.isEmpty()){
            loading(false)
            input_layout_item_out.error = getString(R.string.item_out_empty)
            return
        }

        if (itemIn.startsWith("0")){
            loading(false)
            input_layout_item_out.error = getString(R.string.item_number_error)
            return
        }

        if (itemIn.isNotEmpty() && !itemIn.startsWith("0")){
            updateTotalItemProduct(itemIn)
        }

    }

    private fun updateTotalItemProduct(itemIn: String?) {
        val productTotalItem = ItemOutFragmentArgs.fromBundle(arguments as Bundle).totalItem
        val productId = ItemOutFragmentArgs.fromBundle(arguments as Bundle).productId
        val imgUrl = ItemOutFragmentArgs.fromBundle(arguments as Bundle).imgUrl
        val productName = ItemOutFragmentArgs.fromBundle(arguments as Bundle).productName
        val totalItemNow = productTotalItem?.toInt()
        val totalItemOut = itemIn?.toInt()

        val totalItem = totalItemNow?.minus(totalItemOut!!)
        itemViewModel.itemOut(productId, totalItem, totalItemOut, imgUrl, productName)
    }

    private fun reportState(){
        itemViewModel.reportState().observe(viewLifecycleOwner, Observer { state ->
            if (state == true){
                loading(false)
                val itemOutSuccess = ItemOutFragmentDirections
                    .actionNavigationItemOutFragmentToNavigationItemFragment()
                itemOutSuccess.message = getString(R.string.product_item_out_report_message)
                view?.findNavController()?.navigate(itemOutSuccess)
            }else{
                loading(false)
                view?.let {
                    Snackbar.make(it, "Gagal memasukan data item keluar!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Coba lagi"){}
                        .setBackgroundTint(resources.getColor(R.color.colorRed))
                        .show()
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_item_out -> {
                itemInCheck()
            }
            R.id.back_button_item_out -> {
                activity?.onBackPressed()
            }
        }
    }

    private fun loading(state: Boolean){
        if (state){
            loading_item_out.visibility = View.VISIBLE
        }else{
            loading_item_out.visibility = View.INVISIBLE
        }
    }

    private fun errorMessage(){
        if (itemViewModel.errorMessageItemOut != null){
            view?.let {
                context?.let { it1 -> ContextCompat.getColor(it1, R.color.colorGreen) }?.let { it2 ->
                    Snackbar.make(it, "${itemViewModel.errorMessageItemOut}", Snackbar.LENGTH_INDEFINITE)
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