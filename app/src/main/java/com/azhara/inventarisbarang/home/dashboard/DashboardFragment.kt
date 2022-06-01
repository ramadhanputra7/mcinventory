package com.azhara.inventarisbarang.home.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.azhara.inventarisbarang.R
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : Fragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        card_product.setOnClickListener(this)
        card_logistic.setOnClickListener(this)
        card_report.setOnClickListener(this)
        card_account.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.card_product -> {
                view?.findNavController()?.navigate(R.id.action_navigation_dashboard_fragment_to_navigation_product_fragment)
            }
            R.id.card_logistic -> {
                view?.findNavController()?.navigate(R.id.action_navigation_dashboard_fragment_to_navigation_item_fragment)
            }
            R.id.card_report -> {
                view?.findNavController()?.navigate(R.id.action_navigation_dashboard_fragment_to_navigation_report_fragment)
            }
            R.id.card_account -> {
                view?.findNavController()?.navigate(R.id.action_navigation_dashboard_fragment_to_navigation_account_fragment)
            }
        }
    }

}