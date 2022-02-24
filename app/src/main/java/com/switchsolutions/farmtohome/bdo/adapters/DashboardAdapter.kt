package com.switchsolutions.farmtohome.bdo.adapters


import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.switchsolutions.farmtohome.bdo.MainActivity
import com.switchsolutions.farmtohome.bdo.R
import com.switchsolutions.farmtohome.bdo.fragments.CreateRequestFragment
import com.switchsolutions.farmtohome.bdo.fragments.DashboardFragment
import com.switchsolutions.farmtohome.bdo.fragments.DashboardFragment.Companion.singleOrder
import com.switchsolutions.farmtohome.bdo.interfaces.PlayBeep
import com.switchsolutions.farmtohome.bdo.interfaces.ShowOrderDetail
import com.switchsolutions.farmtohome.bdo.responsemodels.DashBoardOrdersData
import com.switchsolutions.farmtohome.bdo.responsemodels.EditOrdersData
import com.switchsolutions.farmtohome.bdo.responsemodels.OrderProductsData
import com.switchsolutions.farmtohome.bdo.viewmodels.DashboardFragmentViewModel
import com.switchsolutions.farmtohome.bdo.viewmodels.GetOrdersToEditViewModel
import kotlinx.coroutines.NonDisposableHandle.parent
import mehdi.sakout.fancybuttons.FancyButton
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DashboardAdapter(private val context: Context, private var viewModel: DashboardFragmentViewModel,
                       private var ViewModelEdit: GetOrdersToEditViewModel,
    private var listdata: ArrayList<DashBoardOrdersData>,
                       private val listener: (DashBoardOrdersData) -> Unit
) :
    RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {
    companion object{
    }
    private lateinit var viewContext: Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        viewContext = parent.context
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem: View = layoutInflater.inflate(R.layout.dashboard_list_items, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listdata[position]
        holder.ivEditOrder.setOnClickListener { listener(item) }
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

//        holder.ivEditOrder.setOnClickListener {
//            waitDialog = ProgressDialog.show(context, "", "Please wait")
//            ViewModelEdit.startObserver()
//            showEditOrderDialog(position)
//        }

    //    holder.ivEditOrder.setOnClickListener {
         //   DashboardFragment.newInstance().triggerMainFragmentFunction(listdata[position])
           // holder.ivEditOrder.setOnClickListener (onClickListener)
       // }
        holder.ivDeleteOrder.setOnClickListener { view ->
            confirmFeedBackDelete(position)
        }
    }


    override fun getItemCount(): Int {
        return listdata.size
    }
    private fun confirmFeedBackDelete( position: Int) {
        val builder = AlertDialog.Builder(viewContext)
        Log.i("requisitionID", listdata[position].id!!.toString())
        builder.setMessage(viewContext.getString(R.string.delete_confirm))
            .setPositiveButton(viewContext.getString(R.string.delete)) { dialog, _ ->
                DashboardFragment.requisitionId = listdata[position].id!!
                viewModel.deleteOrder()
                dialog.dismiss()
                //delete order
                listdata.removeAt(position)
                notifyDataSetChanged()
            }
            .setNegativeButton(viewContext.getString(R.string.cancel)) { dialog, _ ->
                // cancel delete process
                dialog.dismiss()
            }
        builder.show()
    }

    private fun openOrderDetail (){

    }




    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewCustomerName: TextView
        var relativeLayout: ConstraintLayout
        var tvDeliveryDate: TextView
        var tvRequestId: TextView
        var ivEditOrder : ImageView
        var ivDeleteOrder: ImageView
        var cardView: CardView
        init {
            textViewCustomerName = itemView.findViewById<View>(R.id.tv_customer_name) as TextView
            tvDeliveryDate = itemView.findViewById<View>(R.id.tv_delivery_date) as TextView
            tvRequestId = itemView.findViewById<View>(R.id.tv_request_id) as TextView
            relativeLayout = itemView.findViewById<View>(R.id.constraint_layout) as ConstraintLayout
            ivEditOrder = itemView.findViewById(R.id.iv_edit_order) as ImageView
            ivDeleteOrder = itemView.findViewById(R.id.iv_remove_order) as ImageView
            cardView = itemView.findViewById(R.id.cardview_items) as CardView
        }
    }
    // RecyclerView recyclerView;
    init {
        this.listdata = listdata
    }
}