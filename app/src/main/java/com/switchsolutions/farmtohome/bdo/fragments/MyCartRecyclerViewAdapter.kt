package com.switchsolutions.farmtohome.bdo.fragments

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.switchsolutions.farmtohome.bdo.CartViewModel
import com.switchsolutions.farmtohome.bdo.MainActivity
import com.switchsolutions.farmtohome.bdo.R
import com.switchsolutions.farmtohome.bdo.databinding.CartItemListAdapterBinding
import com.switchsolutions.farmtohome.bdo.databinding.CartListItemBinding
import com.switchsolutions.farmtohome.bdo.room_db.CartEntityClass

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
        subscribersList.addAll(subscribers)
    }
}

class MyViewHolder( val binding: CartItemListAdapterBinding, var quantity: Int) : RecyclerView.ViewHolder(binding.root) {

    fun bind(product: CartEntityClass,position: Int, clickListener: (CartEntityClass) -> Unit) {
        binding.productNameCart.text = product.productName
        binding.totalQtyCart.setText(product.quantity)
        binding.tvCustomerName.text = product.customerName
        binding.productUnitAndQuantityCart2.text = product.productUnit
        CartFragment.productQuantity.add(product.quantity)
        binding.deleteProductImageCart.setOnClickListener {
            clickListener(product)
        }
        binding.productQtyAddCart.setOnClickListener {
                quantity = product.quantity.toIntOrNull()?.plus(1)!!
                CartFragment.productQuantity[position] = quantity.toString()
                binding.totalQtyCart.setText(quantity.toString())
                product.quantity = product.quantity.toIntOrNull()?.plus(1).toString()


        }
        binding.productQtyMinusCart.setOnClickListener {
            if ((product.quantity.toIntOrNull())!! > 1){
                quantity = product.quantity.toIntOrNull()?.minus(1)!!
            CartFragment.productQuantity[position] = quantity.toString()
            binding.totalQtyCart.setText(quantity.toString())
            product.quantity = product.quantity.toIntOrNull()?.minus(1).toString()
        }
        }
    }
}