package com.switchsolutions.farmtohome.bdo.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.switchsolutions.farmtohome.bdo.NotificationUtil
import com.switchsolutions.farmtohome.bdo.R
import com.switchsolutions.farmtohome.bdo.adapters.DashboardAdapter
import com.switchsolutions.farmtohome.bdo.adapters.DispatchedItemAdapter
import com.switchsolutions.farmtohome.bdo.callbacks.HttpStatusCodes
import com.switchsolutions.farmtohome.bdo.databinding.CustomEditProductDialogBinding
import com.switchsolutions.farmtohome.bdo.databinding.DashboardFragmenttFragmentBinding
import com.switchsolutions.farmtohome.bdo.databinding.DispatchedFragmentBinding
import com.switchsolutions.farmtohome.bdo.enums.Type
import com.switchsolutions.farmtohome.bdo.responsemodels.DashBoardOrdersData
import com.switchsolutions.farmtohome.bdo.responsemodels.DashboardResponseData
import com.switchsolutions.farmtohome.bdo.responsemodels.DispatchedOrdersData
import com.switchsolutions.farmtohome.bdo.responsemodels.DispatchedOrdersResponseModel
import com.switchsolutions.farmtohome.bdo.viewmodels.DashboardFragmentViewModel
import com.switchsolutions.farmtohome.bdo.viewmodels.DispatchViewModel
import mehdi.sakout.fancybuttons.FancyButton


class DispatchFragment : Fragment() {

    companion object {
        const val TAG: String = "dispatchedFragment"
        var USER_STORED_CITY_ID = 1
        var requisitionId = 0
        val dispatchedOrdersJson = JsonObject()
        var USER_ID = 1
        fun newInstance() = DispatchFragment()
    }

    private lateinit var viewModel: DispatchViewModel
    lateinit var binding: DispatchedFragmentBinding
    private lateinit var dashboardResponseData: DispatchedOrdersResponseModel
    private lateinit var editData: DashBoardOrdersData
    private lateinit var orders: ArrayList<DispatchedOrdersData>
    private lateinit var waitDialog: ProgressDialog
    private val MY_PREFS_NAME = "FarmToHomeBDO"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val preferences = requireContext().getSharedPreferences(MY_PREFS_NAME, AppCompatActivity.MODE_PRIVATE)
        USER_STORED_CITY_ID = preferences.getInt("cityId", 1)
        USER_ID = preferences.getInt("User", 1)
        binding = DispatchedFragmentBinding.inflate(getLayoutInflater())
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DispatchViewModel::class.java)
        waitDialog = ProgressDialog(requireContext())
        dispatchedOrdersJson.addProperty("city_id", USER_STORED_CITY_ID)
        dispatchedOrdersJson.addProperty("status", 3)
        dispatchedOrdersJson.addProperty("created_by", USER_ID)
        startObservers()
        viewModel.startObserver()
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
            val adapter = DispatchedItemAdapter(viewModel, orders, View.OnClickListener {
               // showEditOrderDialog()
            })
            binding.recyclerViewDispatched.setHasFixedSize(true)
            binding.recyclerViewDispatched.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerViewDispatched.adapter = adapter
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
                NotificationUtil.showShortToast(requireContext(), requireContext().getString(R.string.error_occurred), Type.DANGER)
            }
        })

    }



}