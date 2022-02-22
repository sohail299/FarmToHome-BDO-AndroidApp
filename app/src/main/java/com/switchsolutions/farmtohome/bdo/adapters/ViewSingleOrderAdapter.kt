package com.switchsolutions.farmtohome.bdo.adapters


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.switchsolutions.farmtohome.bdo.R
import com.switchsolutions.farmtohome.bdo.fragments.CartFragment
import com.switchsolutions.farmtohome.bdo.fragments.DashboardFragment
import com.switchsolutions.farmtohome.bdo.responsemodels.DashBoardOrdersData
import com.switchsolutions.farmtohome.bdo.responsemodels.OrderProductsData
import com.switchsolutions.farmtohome.bdo.viewmodels.DashboardFragmentViewModel
import kotlinx.coroutines.NonDisposableHandle.parent


class ViewSingleOrderAdapter(private var viewModel: DashboardFragmentViewModel,
                       private var listdata: ArrayList<OrderProductsData>,
                       private  var onClickListener: View.OnClickListener
) :
    RecyclerView.Adapter<ViewSingleOrderAdapter.ViewHolder>() {
    private lateinit var viewContext: Context
    private var quantity = 0
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        viewContext = parent.context
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem: View = layoutInflater.inflate(R.layout.view_single_order_item_list, parent, false)
        return ViewHolder(listItem, quantity)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listdata[position], position)
        //holder.textViewCustomerName.text = listdata[position].label

    }

    override fun getItemCount(): Int {
        return listdata.size
    }

    private fun openOrderDetail (){

    }
    private fun editOrder(){

    }

    class ViewHolder(itemView: View, var quantity: Int) : RecyclerView.ViewHolder(itemView) {

        var textViewCustomerName: TextView = itemView.findViewById<View>(R.id.product_name_edit) as TextView
        var textViewProductUnit: TextView = itemView.findViewById<View>(R.id.product_unit_and_quantity_cart2) as TextView
        var Quantity: TextView = itemView.findViewById<View>(R.id.total_qty_cart_edit) as TextView

        fun bind(item: OrderProductsData, position: Int){
            textViewCustomerName.text = item.label
            Quantity.text = item.quantity!!.toString()
            textViewProductUnit.text = item.unit
            DashboardFragment.productQuantity.add(item.quantity!!)
        }
    }

    // RecyclerView recyclerView;
    init {
        this.listdata = listdata
    }
}