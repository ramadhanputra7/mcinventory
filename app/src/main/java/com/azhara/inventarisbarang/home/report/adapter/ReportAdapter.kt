package com.azhara.inventarisbarang.home.report.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.azhara.inventarisbarang.R
import com.azhara.inventarisbarang.entity.ReportItem
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_item_report.view.*
import java.text.SimpleDateFormat
import java.util.*

class ReportAdapter : ListAdapter<ReportItem, ReportAdapter.ReportViewHolder>(DIFF_CALLBACK){

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ReportItem>(){
            override fun areItemsTheSame(oldItem: ReportItem, newItem: ReportItem): Boolean {
                return oldItem.productName == newItem.productName
            }
            override fun areContentsTheSame(oldItem: ReportItem, newItem: ReportItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReportAdapter.ReportViewHolder {
        return ReportViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_report, parent, false))
    }

    override fun onBindViewHolder(holder: ReportAdapter.ReportViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReportViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(report: ReportItem){
            val date = report.dateItemUpdate
            with(itemView){
                if (report.imgUrl != null){
                    Glide.with(context).load(report.imgUrl).into(img_item_report)
                }
                if (report.typeReport == 0){
                    tv_item_report_product_in_out.text = "Keluar: ${report.totalItemUpdate} Items"
                    tv_item_report_product_in_out.setTextColor(ContextCompat.getColor(context, R.color.colorRed))
                }else{
                    tv_item_report_product_in_out.text = "Masuk: ${report.totalItemUpdate} Items"
                    tv_item_report_product_in_out.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
                }
                tv_item_report_product_name.text = "${report.productName}"
                tv_item_report_product_remaining.text = "Sisa: ${report.remainingItems} Items"
                tv_item_report_date.text = date?.seconds?.let { convertToLocalDate(it) }
            }
        }
    }

    private fun convertToLocalDate(date: Long): String {
        // Convert timestamp to local time
        val calendar = Calendar.getInstance()
        val tz = calendar.timeZone
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        sdf.timeZone = tz
        val startSecondDate = Date(date * 1000)
        return sdf.format(startSecondDate)
    }

}