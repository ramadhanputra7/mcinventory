package com.azhara.inventarisbarang.entity

import com.google.firebase.Timestamp

data class ReportItem(
    val productName: String? = null,
    val imgUrl: String? = null,
    val dateItemUpdate: Timestamp? = null,
    val typeReport: Int? = null, //type 0 item out and 1 item in
    val totalItemUpdate: Int? = null,
    val remainingItems: Int? = null
)