package com.switchsolutions.farmtohome.bdo.adapters


import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.switchsolutions.farmtohome.bdo.R
import com.switchsolutions.farmtohome.bdo.fragments.DashboardFragment
import com.switchsolutions.farmtohome.bdo.responsemodels.OrderProductsData
import com.switchsolutions.farmtohome.bdo.viewmodels.DashboardFragmentViewModel


class OrderEditAdapter(private var viewModel: DashboardFragmentViewModel,
                       private var listdata: ArrayList<OrderProductsData>,
                       private var srNoCount: ArrayList<Int>,
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
        return ViewHolder(listItem, quantity, listdata, srNoCount )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listdata[position], position, listdata, srNoCount)
        //holder.textViewCustomerName.text = listdata[position].label

    }

    override fun getItemCount(): Int {
        return listdata.size
    }
    private fun openOrderDetail (){
    }
    private fun editOrder(){
    }

    class ViewHolder(itemView: View, var quantity: Int, listdata: ArrayList<OrderProductsData>, srNoCount: ArrayList<Int>) : RecyclerView.ViewHolder(itemView) {

        var textViewCustomerName: TextView = itemView.findViewById<View>(R.id.product_name_edit) as TextView
        var tvProductSrNo: TextView = itemView.findViewById<View>(R.id.product_id_count) as TextView
        var textViewProductUnit: TextView = itemView.findViewById<View>(R.id.product_unit_and_quantity_cart2) as TextView
        var addBtn: ImageButton = itemView.findViewById<View>(R.id.product_qty_add_cart_edit) as ImageButton
        var minusBtn: ImageButton = itemView.findViewById<View>(R.id.product_qty_minus_cart_edit) as ImageButton
        var removeItemBtn: ImageButton = itemView.findViewById<View>(R.id.ib_remove_item_edit_cart) as ImageButton
        var editQuantity: EditText = itemView.findViewById<View>(R.id.total_qty_cart_edit) as EditText

        fun bind(item: OrderProductsData, position: Int, listdata: ArrayList<OrderProductsData>, srNoCount: ArrayList<Int>){
            textViewCustomerName.text = item.label
            tvProductSrNo.text = srNoCount[position].toString()+". "
            editQuantity.setText(item.quantity!!.toString())
            textViewProductUnit.text = item.unit
            DashboardFragment.productQuantity.add(item.quantity!!)

            editQuantity.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (editQuantity.text.toString().isNotEmpty()) {
                        if (DashboardFragment.productQuantity.size > position) {
                            DashboardFragment.productQuantity[position] = s.toString().toInt()
                            item.quantity = s.toString().toIntOrNull()
                        }
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (editQuantity.text.toString().isNotEmpty()) {
                        if (DashboardFragment.productQuantity.size > position) {
                            DashboardFragment.productQuantity[position] = s.toString().toInt()
                            item.quantity = s.toString().toIntOrNull()
                        }
                    }
                }
            }
            )
            addBtn.setOnClickListener {
                quantity = item.quantity!!.plus(1)
                item.quantity = item.quantity!!.plus(1)
                if (DashboardFragment.productQuantity.size > position)
                DashboardFragment.productQuantity[position] = quantity
                editQuantity.setText(quantity.toString())

            }
            minusBtn.setOnClickListener {
                if (item.quantity!! > 1) {
                    quantity = item.quantity!!.minus(1)
                    item.quantity = item.quantity!!.minus(1)
                    if (DashboardFragment.productQuantity.size > position)
                    DashboardFragment.productQuantity[position] = quantity
                    editQuantity.setText(quantity.toString())

                }
            }
            removeItemBtn.setOnClickListener {
                if (DashboardFragment.editOrders.size > position) {
                    DashboardFragment.editOrders.removeAt(position)
                    DashboardFragment.adapter.notifyItemRemoved(position)
                    DashboardFragment.adapter.notifyDataSetChanged()
                }


            }
        }


    }

    // RecyclerView recyclerView;
    init {
        this.listdata = listdata
    }
}