package com.switchsolutions.farmtohome.bdo.fragments

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.switchsolutions.farmtohome.bdo.NotificationUtil
import com.switchsolutions.farmtohome.bdo.R
import com.switchsolutions.farmtohome.bdo.adapters.DashboardAdapter
import com.switchsolutions.farmtohome.bdo.adapters.DeliveredItemsAdapter
import com.switchsolutions.farmtohome.bdo.adapters.DispatchedItemAdapter
import com.switchsolutions.farmtohome.bdo.callbacks.HttpStatusCodes
import com.switchsolutions.farmtohome.bdo.databinding.CustomEditProductDialogBinding
import com.switchsolutions.farmtohome.bdo.databinding.DashboardFragmenttFragmentBinding
import com.switchsolutions.farmtohome.bdo.databinding.DeliveredFragmentBinding
import com.switchsolutions.farmtohome.bdo.databinding.DispatchedFragmentBinding
import com.switchsolutions.farmtohome.bdo.enums.Type
import com.switchsolutions.farmtohome.bdo.interfaces.ShowOrderDetail
import com.switchsolutions.farmtohome.bdo.responsemodels.*
import com.switchsolutions.farmtohome.bdo.viewmodels.DashboardFragmentViewModel
import com.switchsolutions.farmtohome.bdo.viewmodels.DeliveredViewModel
import com.switchsolutions.farmtohome.bdo.viewmodels.DispatchViewModel
import mehdi.sakout.fancybuttons.FancyButton


class DeliveredFragment : Fragment() {

    companion object {
        const val TAG: String = "deliveredFragment"
        var USER_STORED_CITY_ID = 1
        var requisitionId = 0
        val deliveredOrdersJson = JsonObject()
        var USER_ID = 1
        fun newInstance() = DeliveredFragment()
        lateinit var singleOrder : ShowOrderDetail
        lateinit var binding: DeliveredFragmentBinding
    }

    private lateinit var viewModel: DeliveredViewModel

    private lateinit var dashboardResponseData: DeliveredResponseModel
    private lateinit var editData: DeliveredResponseData
    private lateinit var orders: ArrayList<DeliveredResponseData>
    var orderDataCopy: ArrayList<DeliveredResponseData> = java.util.ArrayList()
    private lateinit var waitDialog: ProgressDialog
    private val MY_PREFS_NAME = "FarmToHomeBDO"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val preferences = requireContext().getSharedPreferences(MY_PREFS_NAME, AppCompatActivity.MODE_PRIVATE)
        USER_STORED_CITY_ID = preferences.getInt("cityId", 1)
        USER_ID = preferences.getInt("User", 1)
        binding = DeliveredFragmentBinding.inflate(getLayoutInflater())
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DeliveredViewModel::class.java)
        waitDialog = ProgressDialog(requireContext())
        binding.searchView.queryHint = "Search by customer name"
        singleOrder = activity as ShowOrderDetail
        deliveredOrdersJson.addProperty("city_id", USER_STORED_CITY_ID)
        deliveredOrdersJson.addProperty("status", 4)
        startObservers()
        viewModel.startObserver(deliveredOrdersJson)


        // TODO: Use the ViewModel
    }

    fun startObservers() {
        viewModel.callSignInApi.observe(viewLifecycleOwner, Observer {
            if (it!!) {
                waitDialog = ProgressDialog.show(requireContext(), "", "Fetching Orders")
            }
        })
        viewModel.apiResponseSuccess.observe(viewLifecycleOwner, Observer {
            if (waitDialog.isShowing) waitDialog.dismiss()
            dashboardResponseData = it
            orders = dashboardResponseData.data
//            val adapter = DashboardAdapter(orders, View.OnClickListener(){
//            })
            orderDataCopy.addAll(dashboardResponseData.data)
            val adapter = DeliveredItemsAdapter(viewModel, orders,orderDataCopy,  View.OnClickListener {
                // showEditOrderDialog()
            })
            binding.recyclerViewDelivered.setHasFixedSize(true)
            binding.recyclerViewDelivered.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerViewDelivered.adapter = adapter

            binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                android.widget.SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String): Boolean {
                    adapter.filter(query)
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    adapter.filter(newText)
                    return true
                }
            })
            //showEditOrderDialog()
        })
        viewModel.apiResponseFailure.observe(viewLifecycleOwner, Observer {
            if (waitDialog.isShowing) waitDialog.dismiss()
            if ((it?.statusCode == HttpStatusCodes.SC_UNAUTHORIZED) || (it?.statusCode == HttpStatusCodes.SC_NO_CONTENT)) {
                Toast.makeText(
                    requireContext(), "Unauthorized",
                    Toast.LENGTH_LONG
                ).show()
//                val builder = AlertDialog.Builder(context!!)
//                builder.setMessage(context?.getString(R.string.invalid_credentials))
//                        .setPositiveButton(context?.getString(R.string.ok)) { dialog, _ ->
//                            dialog.dismiss()
//                        }
//                builder.create().show()
            } else {
//                Toast.makeText(
//                    requireContext(), "An Error Occurred",
//                    Toast.LENGTH_LONG
//                ).show()
                //NotificationUtil.showShortToast(requireContext(), requireContext().getString(R.string.error_occurred), Type.DANGER)
            }
        })

    }



}