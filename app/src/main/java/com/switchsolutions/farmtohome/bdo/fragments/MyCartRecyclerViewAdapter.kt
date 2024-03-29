package com.switchsolutions.farmtohome.bdo.fragments

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.switchsolutions.farmtohome.bdo.R
import com.switchsolutions.farmtohome.bdo.databinding.CartItemListAdapterBinding
import com.switchsolutions.farmtohome.bdo.room_db.cart.CartEntityClass

class MyCartRecyclerViewAdapter( private val clickListener: (CartEntityClass) -> Unit) :
        RecyclerView.Adapter<MyViewHolder>() {
    private val subscribersList = ArrayList<CartEntityClass>()
    private var quantity = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: CartItemListAdapterBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.cart_item_list_adapter, parent, false)
        return MyViewHolder(binding, quantity)
    }

    override fun getItemCount(): Int {
        return subscribersList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(subscribersList[position],position, clickListener)
    }

    fun setList(subscribers: List<CartEntityClass>) {
        subscribersList.clear()
        CartFragment.productQuantity.clear()
        subscribersList.addAll(subscribers)
    }
}
class MyViewHolder( val binding: CartItemListAdapterBinding, var quantity: Int) : RecyclerView.ViewHolder(binding.root) {

    fun bind(product: CartEntityClass, position: Int, clickListener: (CartEntityClass) -> Unit) {
        binding.productNameCart.text = product.productName
        CartFragment.productQuantity.add(product.quantity)
        binding.totalQtyCart.setText(product.quantity)
        binding.tvCustomerName.text = product.customerName
        binding.productUnitAndQuantityCart2.text = product.productUnit
        binding.deleteProductImageCart.setOnClickListener {
            clickListener(product)
        }
        binding.totalQtyCart.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.totalQtyCart.text.toString().isNotEmpty()) {
                    if (position < CartFragment.productQuantity.size)
                    CartFragment.productQuantity[position] = s.toString()
                    product.quantity = s.toString()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        binding.productQtyAddCart.setOnClickListener {
                quantity = product.quantity.toIntOrNull()?.plus(1)!!
                product.quantity = product.quantity.toIntOrNull()?.plus(1).toString()
                binding.totalQtyCart.setText(quantity.toString())
        }
        binding.productQtyMinusCart.setOnClickListener {
            if ((product.quantity.toIntOrNull())!! > 1){
                quantity = product.quantity.toIntOrNull()?.minus(1)!!
                product.quantity = product.quantity.toIntOrNull()?.minus(1).toString()
            binding.totalQtyCart.setText(quantity.toString())
        }
        }
    }
}