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
import com.switchsolutions.farmtohome.bdo.MainActivity
import com.switchsolutions.farmtohome.bdo.R
import com.switchsolutions.farmtohome.bdo.fragments.CartFragment
import com.switchsolutions.farmtohome.bdo.fragments.DashboardFragment
import com.switchsolutions.farmtohome.bdo.responsemodels.DashBoardOrdersData
import com.switchsolutions.farmtohome.bdo.responsemodels.OrderProductsData
import com.switchsolutions.farmtohome.bdo.viewmodels.DashboardFragmentViewModel
import kotlinx.coroutines.NonDisposableHandle.parent
import okhttp3.internal.notify


class OrderEditAdapter(private var viewModel: DashboardFragmentViewModel,
                       private var listdata: ArrayList<OrderProductsData>,
                       private  var onClickListener: View.OnClickListener
) :
    RecyclerView.Adapter<OrderEditAdapter.ViewHolder>() {
    private lateinit var viewContext: Context
    private var quantity = 0
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        viewContext = parent.context
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem: View = layoutInflater.inflate(R.layout.edit_order_item_adapter, parent, false)
        return ViewHolder(listItem, quantity, listdata )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listdata[position], position, listdata)
        //holder.textViewCustomerName.text = listdata[position].label

    }

    override fun getItemCount(): Int {
        return listdata.size
    }
    private fun openOrderDetail (){
    }
    private fun editOrder(){
    }

    class ViewHolder(itemView: View, var quantity: Int, listdata: ArrayList<OrderProductsData>) : RecyclerView.ViewHolder(itemView) {

        var textViewCustomerName: TextView = itemView.findViewById<View>(R.id.product_name_edit) as TextView
        var textViewProductUnit: TextView = itemView.findViewById<View>(R.id.product_unit_and_quantity_cart2) as TextView
        var addBtn: ImageButton = itemView.findViewById<View>(R.id.product_qty_add_cart_edit) as ImageButton
        var minusBtn: ImageButton = itemView.findViewById<View>(R.id.product_qty_minus_cart_edit) as ImageButton
        var removeItemBtn: ImageButton = itemView.findViewById<View>(R.id.ib_remove_item_edit_cart) as ImageButton
        var editQuantity: EditText = itemView.findViewById<View>(R.id.total_qty_cart_edit) as EditText

        fun bind(item: OrderProductsData, position: Int, listdata: ArrayList<OrderProductsData>){
            textViewCustomerName.text = item.label
            editQuantity.setText(item.quantity!!.toString())
            textViewProductUnit.text = item.unit
            DashboardFragment.productQuantity.add(item.quantity!!)
            addBtn.setOnClickListener {
                quantity = item.quantity!!.plus(1)
                DashboardFragment.productQuantity[position] = quantity
                editQuantity.setText(quantity.toString())
                item.quantity = item.quantity!!.plus(1)
            }
            minusBtn.setOnClickListener {
                if (item.quantity!! > 1) {
                    quantity = item.quantity!!.minus(1)
                    DashboardFragment.productQuantity[position] = quantity
                    editQuantity.setText(quantity.toString())
                    item.quantity = item.quantity!!.minus(1)
                }
            }

            removeItemBtn.setOnClickListener {
                DashboardFragment.editOrders.removeAt(position)
                DashboardFragment.adapter.notifyItemRemoved(position)


            }
        }


    }

    // RecyclerView recyclerView;
    init {
        this.listdata = listdata
    }
}