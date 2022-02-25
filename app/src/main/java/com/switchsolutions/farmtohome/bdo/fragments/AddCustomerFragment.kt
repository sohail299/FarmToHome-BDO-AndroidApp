package com.switchsolutions.farmtohome.bdo.fragments

import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.switchsolutions.farmtohome.bdo.databinding.AddCustomerFragmentBinding

class AddCustomerFragment : Fragment()  {
    lateinit var binding: AddCustomerFragmentBinding

    companion object {
        fun newInstance() = AddCustomerFragment()
    }

    private lateinit var viewModel: AddCustomerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AddCustomerFragmentBinding.inflate(getLayoutInflater())
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddCustomerViewModel::class.java)
        // TODO: Use the ViewModel
    }

}