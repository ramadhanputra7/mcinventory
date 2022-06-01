package com.azhara.inventarisbarang.home.Item.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.azhara.inventarisbarang.entity.Product
import com.azhara.inventarisbarang.entity.ReportItem
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class ItemViewModel : ViewModel(){

    private val db = FirebaseFirestore.getInstance()
    private val tag = ItemViewModel::class.java.simpleName
    private val productData = MutableLiveData<List<Product>>()
    private val reportState = MutableLiveData<Boolean>()
    var errorMessage: String? = null
    var errorMessageItemIn: String? = null
    var errorMessageItemOut: String? = null

    fun getDataProduct(){
        val productDb = db.collection("product")

        productDb.addSnapshotListener { value, error ->
            if (error != null){
                Log.e(tag, "Error load data product: ${error.message}")
                errorMessage = "Error mengambil data product"
            }

            if (value != null){
                val listProduct = ArrayList<Product>()
                val data = value.documents
                data.forEach {
                    val product = it.toObject(Product::class.java)
                    if (product != null){
                        product.productId = it.id
                        listProduct.add(product)
                    }
                }
                productData.postValue(listProduct)
                Log.d("product", "$listProduct")
            }
        }
    }

    fun productData() : LiveData<List<Product>> = productData

    fun itemIn(
        productId: String?,
        totalItem: Int?,
        totalItemIn: Int?,
        imgUrl: String?,
        productName: String?
    ){
        val productDb = db.collection("product").document("$productId")

        productDb.update("totalItem", totalItem).addOnCompleteListener { task ->
            if (task.isSuccessful){
                setReport(totalItemIn, 1, imgUrl, productName, totalItem)
            }else{
                reportState.postValue(false)
                Log.e(tag, "Error update item in: ${task.exception?.message}")
                errorMessageItemIn = "Error update produk masuk"
            }
        }
    }

    fun itemOut(
        productId: String?,
        totalItem: Int?,
        totalItemOut: Int?,
        imgUrl: String?,
        productName: String?
    ){
        val productDb = db.collection("product").document("$productId")

        productDb.update("totalItem", totalItem).addOnCompleteListener { task ->
            if (task.isSuccessful){
                setReport(totalItemOut, 0, imgUrl, productName, totalItem)
            }else{
                reportState.postValue(false)
                Log.e(tag, "Error update item out: ${task.exception?.message}")
                errorMessageItemOut = "Error update produk keluar."
            }
        }
    }

    private fun setReport(
        totalItemUpdate: Int?,
        typeReport: Int?,
        imgUrl: String?,
        productName: String?,
        totalItem: Int?
    ){
        val reportDb = db.collection("report")
        val timeNow = System.currentTimeMillis()
        val report = ReportItem(productName, imgUrl, Timestamp(Date(timeNow)), typeReport, totalItemUpdate, totalItem)
        reportDb.add(report).addOnCompleteListener { task ->
            if (task.isSuccessful){
                reportState.postValue(true)
            }else{
                reportState.postValue(false)
                Log.e(tag, "Error set report: ${task.exception?.message}")
            }
        }
    }

    fun reportState(): LiveData<Boolean> = reportState
}

