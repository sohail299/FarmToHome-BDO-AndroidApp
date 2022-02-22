package com.switchsolutions.farmtohome.bdo.adapters;

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.switchsolutions.farmtohome.bdo.R
import com.switchsolutions.farmtohome.bdo.databinding.AddProductAdapterBinding


open class AddProductsAdapter(private var context: Context, private var items: ArrayList<String> ) : RecyclerView.Adapter<AddProductsAdapter.FeedBackItemVH>() {
    var binding: AddProductAdapterBinding? = null
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): FeedBackItemVH {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.add_product_adapter, parent, false)
         binding = DataBindingUtil.bind(parent)!!

        return FeedBackItemVH(v)
    }

    override fun onBindViewHolder(holder: FeedBackItemVH, position: Int) {
        var item: ArrayList<String>? = null
        item!!.add( items[position])
        holder.onBind(item)
    }

    override fun getItemCount(): Int {
        if (items.isEmpty()) {
            return 0
        }
        return items.count()
    }

    private fun getItem(position: Int): ArrayList<String>? {
        if (position >= items.size) return null
        return items
    }

    fun addItems(moreItems: ArrayList<String>) {
        val itemOldCount = items.count()
        items.addAll(moreItems)
    }

    fun clearItems() {
        items = ArrayList()
        this.notifyDataSetChanged()
    }

    fun deleteFeedBack(position: Int) {
        val item = getItem(position)

    }


    inner class FeedBackItemVH(val view: View) : RecyclerView.ViewHolder(view){

        private val binding: AddProductAdapterBinding = DataBindingUtil.bind(view)!!

        fun onBind(item: ArrayList<String>) {

            binding.tvProductName.text = item[0]
            //  }

        }
    }
}
