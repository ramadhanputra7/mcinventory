package com.azhara.inventarisbarang.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.azhara.inventarisbarang.R
import kotlinx.android.synthetic.main.fragment_register_success.*

class RegisterSuccessFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_success, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_to_login.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_register_success_fragment_to_navigation_login_fragment)
        }
    }
}