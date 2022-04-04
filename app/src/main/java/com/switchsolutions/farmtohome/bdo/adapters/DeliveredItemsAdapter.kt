package com.switchsolutions.farmtohome.bdo.adapters


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.switchsolutions.farmtohome.bdo.MainActivity
import com.switchsolutions.farmtohome.bdo.R
import com.switchsolutions.farmtohome.bdo.fragments.DashboardFragment
import com.switchsolutions.farmtohome.bdo.fragments.DeliveredFragment
import com.switchsolutions.farmtohome.bdo.fragments.DeliveredFragment.Companion.singleOrder
import com.switchsolutions.farmtohome.bdo.fragments.DispatchFragment
import com.switchsolutions.farmtohome.bdo.responsemodels.DashBoardOrdersData
import com.switchsolutions.farmtohome.bdo.responsemodels.DeliveredResponseData
import com.switchsolutions.farmtohome.bdo.responsemodels.DispatchedOrdersData
import com.switchsolutions.farmtohome.bdo.responsemodels.DispatchedOrdersResponseModel
import com.switchsolutions.farmtohome.bdo.viewmodels.DashboardFragmentViewModel
import com.switchsolutions.farmtohome.bdo.viewmodels.DeliveredViewModel
import com.switchsolutions.farmtohome.bdo.viewmodels.DispatchViewModel
import kotlinx.coroutines.NonDisposableHandle.parent
import java.util.*
import kotlin.collections.ArrayList

class DeliveredItemsAdapter(private var viewModel: DeliveredViewModel,
                            private var listdata: ArrayList<DeliveredResponseData>,
                            private var listdataCopy: java.util.ArrayList<DeliveredResponseData>,
                            private  var onClickListener: View.OnClickListener
) :
    RecyclerView.Adapter<DeliveredItemsAdapter.ViewHolder>() {
    private lateinit var viewContext: Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        viewContext = parent.context
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem: View = layoutInflater.inflate(R.layout.delivered_list_items, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myListData: DeliveredResponseData = listdata[position]
        holder.textViewCustomerName.text = listdata[position].customer
        holder.tvDeliveryDate.text = listdata[position].delivery_date
        holder.tvRequestId.text = listdata[position].id.toString()
        holder.relativeLayout.setOnClickListener { view ->
            MainActivity.requestID = listdata[position].id!!
            singleOrder.showOrderDetails(listdata[position].id!!)
            singleOrder.startObserverForSingleOrder()
//            Toast.makeText(
//                view.context,
//                "View Order " + myListData.description +"  "+position,
//                Toast.LENGTH_LONG
//            ).show()
        }
        //    holder.ivEditOrder.setOnClickListener {
        //   DashboardFragment.newInstance().triggerMainFragmentFunction(listdata[position])
        holder.ivDeleteOrder.setOnClickListener { view ->
            //confirmDelivery(position, listdata[position].id!!)
            // confirmFeedBackDelete(position)
        }
    }

    override fun getItemCount(): Int {
        return listdata.size
    }

    private fun openOrderDetail (){

    }
    private fun editOrder(){

    }
    @SuppressLint("NotifyDataSetChanged")
    fun filter(textString: String) {
        var flag  = false
        var text = textString
        listdata.clear()
        if (text.isEmpty()) {
            DeliveredFragment.binding.serchviewTextNoProducts.visibility = View.GONE
            listdata.addAll(listdataCopy)
        } else {
            text = text.lowercase(Locale.getDefault())
            for (item in listdataCopy) {
                if (item.customer!!.lowercase(Locale.getDefault()).contains(text)) {
                    DeliveredFragment.binding.serchviewTextNoProducts.visibility = View.GONE
                    listdata.add(item)
                    flag = true
                }
            }
            if (listdata.isEmpty()){
                DeliveredFragment.binding.serchviewTextNoProducts.visibility = View.VISIBLE
            }
        }
        notifyDataSetChanged()
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewCustomerName: TextView
        var relativeLayout: ConstraintLayout
        var tvDeliveryDate: TextView
        var tvRequestId: TextView
        var ivDeleteOrder: ImageView
        init {
            textViewCustomerName = itemView.findViewById<View>(R.id.tv_customer_name) as TextView
            tvDeliveryDate = itemView.findViewById<View>(R.id.tv_delivery_date) as TextView
            tvRequestId = itemView.findViewById<View>(R.id.tv_request_id) as TextView
            relativeLayout = itemView.findViewById<View>(R.id.constraint_layout_delivered) as ConstraintLayout
            ivDeleteOrder = itemView.findViewById(R.id.iv_remove_order) as ImageView
        }
    }

    // RecyclerView recyclerView;
    init {
        this.listdata = listdata
    }
}