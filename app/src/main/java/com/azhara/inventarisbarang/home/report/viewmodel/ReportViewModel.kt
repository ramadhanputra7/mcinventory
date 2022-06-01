package com.azhara.inventarisbarang.home.report.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.azhara.inventarisbarang.entity.ReportItem
import com.google.firebase.firestore.FirebaseFirestore

class ReportViewModel : ViewModel(){

    private val db = FirebaseFirestore.getInstance()
    private val tag = ReportViewModel::class.java.simpleName

    private val reportData = MutableLiveData<List<ReportItem>>()

    fun getDataReport(){
        val reportDb = db.collection("report")

        reportDb.addSnapshotListener { value, error ->
            if (error != null){
                Log.e(tag, "Error get data report: ${error.message}")
            }
            if (value != null){
                val data = value.toObjects(ReportItem::class.java)
                reportData.postValue(data)
            }
        }
    }

    fun reportData(): LiveData<List<ReportItem>> = reportData

}