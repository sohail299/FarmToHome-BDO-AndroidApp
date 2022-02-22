package com.switchsolutions.farmtohome.bdo.adapters


import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.gson.JsonObject
import com.switchsolutions.farmtohome.bdo.R
import com.switchsolutions.farmtohome.bdo.interfaces.PlayBeep
import com.switchsolutions.farmtohome.bdo.responsemodels.DispatchedOrdersData
import com.switchsolutions.farmtohome.bdo.viewmodels.DispatchViewModel
import mehdi.sakout.fancybuttons.FancyButton


class DispatchedItemAdapter(private var viewModel: DispatchViewModel,
                       private var listdata: ArrayList<DispatchedOrdersData>,
                       private  var onClickListener: View.OnClickListener
) :
    RecyclerView.Adapter<DispatchedItemAdapter.ViewHolder>() {
    private lateinit var viewContext: Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        viewContext = parent.context
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem: View = layoutInflater.inflate(R.layout.dispatched_list_items, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myListData: DispatchedOrdersData = listdata[position]
        holder.textViewCustomerName.text = listdata[position].customer
        holder.tvDeliveryDate.text = listdata[position].delivery_date
        holder.tvRequestId.text = listdata[position].id.toString()
        holder.relativeLayout.setOnClickListener { view ->
//            Toast.makeText(
//                view.context,
//                "View Order " + myListData.description +"  "+position,
//                Toast.LENGTH_LONG
//            ).show()
        }



        //    holder.ivEditOrder.setOnClickListener {
        //   DashboardFragment.newInstance().triggerMainFragmentFunction(listdata[position])
        holder.cvDeleteOrder.setOnClickListener { view ->
            confirmDelivery(position, listdata[position].id!!)
           // confirmFeedBackDelete(position)
        }
    }

    private fun confirmDelivery(position: Int, requestId : Int) {
        val reqObject = JsonObject()
            val builder = AlertDialog.Builder(viewContext)
            Log.i("requisitionID", listdata[position].id!!.toString())
            builder.setMessage(viewContext.getString(R.string.deliver_confirm))
                .setPositiveButton(viewContext.getString(R.string.delivered)) { dialog, _ ->
                    reqObject.addProperty("request_id", requestId)
                    reqObject.addProperty("status", 4)
                    viewModel.markAsDelivered(reqObject)
                    dialog.dismiss()
                    //delete order
                    listdata.removeAt(position)
                    notifyDataSetChanged()
                    showDispatchSuccessMessage()
                }
                .setNegativeButton(viewContext.getString(R.string.cancel)) { dialog, _ ->
                    // cancel delete process
                    dialog.dismiss()
                }
            builder.show()

    }
    private fun showDispatchSuccessMessage() {
            val pb : PlayBeep = viewContext as PlayBeep
                pb.playBeep()
        val builder: AlertDialog.Builder = AlertDialog.Builder(viewContext)
        val inflater: LayoutInflater =
            viewContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogLayout: View = inflater.inflate(R.layout.dialog_custome_dispatched, null)
        val myCardView: CardView = dialogLayout.findViewById(R.id.dialog_sign_in_box)
        val buttonOk: FancyButton = dialogLayout.findViewById(R.id.btn_ok_submission)
        // linearLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent))
        //  linearLayout.setBackgroundResource(Color.TRANSPARENT)
        myCardView.setCardBackgroundColor(Color.TRANSPARENT)
        myCardView.cardElevation = 0f

        ///
        val anim: LottieAnimationView = dialogLayout.findViewById(R.id.animation_view)
        builder.setView(dialogLayout)
        builder.setCancelable(false)
        val loginDialog: Dialog = builder.create()
        loginDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loginDialog.show()
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 2000
        animator.addUpdateListener(ValueAnimator.AnimatorUpdateListener { valueAnimator ->
            anim.setProgress(
                valueAnimator.animatedValue as Float
            )
        })
        animator.start()
        val handler = Handler()
        handler.postDelayed({
            loginDialog.dismiss()
        }, 3000)

        buttonOk.setOnClickListener {
            loginDialog.dismiss()

            ///

        }
    }


    override fun getItemCount(): Int {
        return listdata.size
    }

    private fun openOrderDetail (){

    }
    private fun editOrder(){

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewCustomerName: TextView
        var relativeLayout: ConstraintLayout
        var tvDeliveryDate: TextView
        var tvRequestId: TextView
        var cvDeleteOrder: CardView
        init {
            textViewCustomerName = itemView.findViewById<View>(R.id.tv_customer_name) as TextView
            tvDeliveryDate = itemView.findViewById<View>(R.id.tv_delivery_date) as TextView
            tvRequestId = itemView.findViewById<View>(R.id.tv_request_id) as TextView
            relativeLayout = itemView.findViewById<View>(R.id.constraint_layout) as ConstraintLayout
            cvDeleteOrder = itemView.findViewById(R.id.cv_remove_order) as CardView
        }
    }

    // RecyclerView recyclerView;
    init {
        this.listdata = listdata
    }
}