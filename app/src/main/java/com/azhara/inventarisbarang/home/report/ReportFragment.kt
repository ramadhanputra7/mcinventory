package com.azhara.inventarisbarang.home.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.azhara.inventarisbarang.R
import com.azhara.inventarisbarang.entity.ReportItem
import com.azhara.inventarisbarang.home.report.adapter.ReportAdapter
import com.azhara.inventarisbarang.home.report.viewmodel.ReportViewModel
import kotlinx.android.synthetic.main.fragment_report.*

class ReportFragment : Fragment() {

    private lateinit var reportViewModel: ReportViewModel
    private lateinit var reportAdapter: ReportAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reportViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ReportViewModel::class.java]
        reportAdapter = ReportAdapter()
        getDataReport()
        onBackPressed()
    }

    private fun getDataReport(){
        loading(true)
        reportViewModel.getDataReport()

        reportViewModel.reportData().observe(viewLifecycleOwner, Observer { data ->
            if (data != null){
                emptyState(false)
                loading(false)
                setDataReport(data)
            }else{
                loading(false)
                emptyState(true)
            }
        })
    }

    private fun setDataReport(data: List<ReportItem>) {
        with(rv_report){
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = reportAdapter
        }
        reportAdapter.submitList(data)
    }

    private fun emptyState(state: Boolean){
        if(state){
            layout_empty_report.visibility = View.VISIBLE
        }else{
            layout_empty_report.visibility = View.INVISIBLE
        }
    }

    private fun loading(state: Boolean){
        if (state){
            loading_report.visibility = View.VISIBLE
        }else{
            loading_report.visibility = View.INVISIBLE
        }
    }

    private fun onBackPressed(){
        back_button_report.setOnClickListener {
            activity?.onBackPressed()
        }
    }
}