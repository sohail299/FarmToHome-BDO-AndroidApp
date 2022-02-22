package com.switchsolutions.farmtohome.bdo.fragments

import android.animation.ValueAnimator
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.switchsolutions.farmtohome.bdo.MainActivity
import com.switchsolutions.farmtohome.bdo.MainActivity.Companion.waitDilogFlag
import com.switchsolutions.farmtohome.bdo.NotificationUtil
import com.switchsolutions.farmtohome.bdo.R
import com.switchsolutions.farmtohome.bdo.adapters.DashboardAdapter
import com.switchsolutions.farmtohome.bdo.adapters.OrderEditAdapter
import com.switchsolutions.farmtohome.bdo.callbacks.HttpStatusCodes
import com.switchsolutions.farmtohome.bdo.databinding.DashboardFragmenttFragmentBinding
import com.switchsolutions.farmtohome.bdo.enums.Type
import com.switchsolutions.farmtohome.bdo.interfaces.PlayBeep
import com.switchsolutions.farmtohome.bdo.interfaces.ShowOrderDetail
import com.switchsolutions.farmtohome.bdo.responsemodels.*
import com.switchsolutions.farmtohome.bdo.viewmodels.DashboardFragmentViewModel
import com.switchsolutions.farmtohome.bdo.viewmodels.EditOrderViewModel
import com.switchsolutions.farmtohome.bdo.viewmodels.GetOrdersToEditViewModel
import mehdi.sakout.fancybuttons.FancyButton
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DashboardFragment : Fragment(), AdapterView.OnItemClickListener {

    companion object {
        const val TAG: String = "dashboardFragment"
        var USER_STORED_CITY_ID = 1
        var requisitionId = 0
        var requisitionIdEdit = 0
        private lateinit var editData: DashBoardOrdersData
        var USER_ID = 1
        var dialoClicked = false
        fun newInstance() = DashboardFragment()
        var productQuantity: ArrayList<Int> = ArrayList()
        lateinit var singleOrder : ShowOrderDetail
        lateinit var editOrders: ArrayList<OrderProductsData>
        lateinit var adapter: OrderEditAdapter
    }

    private lateinit var viewModel: DashboardFragmentViewModel
    private lateinit var viewModelEdit: EditOrderViewModel
    private lateinit var viewModelEditOrders: GetOrdersToEditViewModel
    lateinit var binding: DashboardFragmenttFragmentBinding
    private lateinit var dashboardResponseData: DashboardResponseData
    private lateinit var editOrderResponseData: EditResponseModel
    private lateinit var editOrdersResponseData: GetOrdersForEditResponseModel
    private lateinit var editOrderDialog: Dialog

    private lateinit var orders: ArrayList<DashBoardOrdersData>

    private lateinit var waitDialog: ProgressDialog

    private val MY_PREFS_NAME = "FarmToHomeBDO"
    var productId: Int = 0
    var productName: String = ""
    var productUnit: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val preferences =
            requireContext().getSharedPreferences(MY_PREFS_NAME, AppCompatActivity.MODE_PRIVATE)
        USER_STORED_CITY_ID = preferences.getInt("cityId", 1)
        USER_ID = preferences.getInt("User", 1)
        binding = DashboardFragmenttFragmentBinding.inflate(getLayoutInflater())
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DashboardFragmentViewModel::class.java)
        viewModelEdit = ViewModelProvider(this).get(EditOrderViewModel::class.java)
        viewModelEditOrders = ViewModelProvider(this).get(GetOrdersToEditViewModel::class.java)
        waitDialog = ProgressDialog(requireContext())
        singleOrder = activity as ShowOrderDetail
        startObservers()
        startDeletionObservers()
        viewModel.startObserver()
        // TODO: Use the ViewModel
    }

    fun startObservers() {
        viewModel.callSignInApi.observe(viewLifecycleOwner, Observer {
            if (it!!) {
                if (waitDialog != null && !waitDialog.isShowing) {
                    waitDialog = ProgressDialog.show(requireContext(), "", "Fetching Orders")
                    waitDialog.setCanceledOnTouchOutside(true)
                }
            }
        })
        viewModel.apiResponseSuccess.observe(viewLifecycleOwner, Observer {
            if (waitDialog.isShowing) {
                waitDilogFlag = false
                waitDialog.dismiss()
            }

            dashboardResponseData = it
            orders = dashboardResponseData.data
//            val adapter = DashboardAdapter(orders, View.OnClickListener(){
//            })
            val adapter =
                DashboardAdapter(requireContext(), viewModel, viewModelEditOrders, orders) { item ->
                    //Log.i("DialogClicked", dialoClicked.toString())
                    requisitionIdEdit = item.id!!
                    viewModelEditOrders.startObserver()
                    startObserversForEditOrders()
                }
            binding.recyclerView.setHasFixedSize(true)
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerView.adapter = adapter
            //showEditOrderDialog()
        })
        viewModel.apiResponseFailure.observe(viewLifecycleOwner, Observer {
            if (waitDialog.isShowing) {
                waitDialog.dismiss()
            }
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
                NotificationUtil.showShortToast(
                    requireContext(),
                    requireContext().getString(R.string.error_occurred),
                    Type.DANGER
                )
            }
        })
    }

    fun startDeletionObservers() {
        viewModel.callDeleteApi.observe(viewLifecycleOwner, Observer {
            if (it!!) {
                if (waitDialog != null && !waitDialog.isShowing) {
                    waitDialog = ProgressDialog.show(requireContext(), "", "Deleting Order")
                    waitDialog.setCanceledOnTouchOutside(true)
                }
            }
        })
        viewModel.apiRequisitionSuccess.observe(viewLifecycleOwner, Observer {
            if (waitDialog.isShowing)
                waitDialog.dismiss()
            showDeletionSuccessMessage()
//            val adapter = DashboardAdapter(orders, View.OnClickListener(){
//            })
            //showEditOrderDialog()
        })
        viewModel.apiRequisitionFailure.observe(viewLifecycleOwner, Observer {
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
                NotificationUtil.showShortToast(
                    requireContext(),
                    requireContext().getString(R.string.error_occurred),
                    Type.DANGER
                )
            }
        })

    }

    private fun showDeletionSuccessMessage() {

        val pb: PlayBeep = activity as PlayBeep
        pb.playBeep()

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val inflater: LayoutInflater =
            requireContext().getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogLayout: View = inflater.inflate(R.layout.dialog_custom_cart_deletion, null)
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
            // MainActivity.newInstance().redirectToMainFragment(savedStateRegistry)

        }, 3000)
        buttonOk.setOnClickListener {
            loginDialog.dismiss()

            ///

        }
    }

    fun startObserversForEditOrders() {
        viewModelEditOrders.callSignInApi.observe(viewLifecycleOwner, Observer {
            if (it!!) {
                if (waitDialog != null && !waitDialog.isShowing) {
                    waitDialog = ProgressDialog.show(requireContext(), "", "Fetching Detail..")
                    waitDialog.setCancelable(true)
                }
            }
        })
        viewModelEditOrders.apiResponseSuccess.observe(viewLifecycleOwner, Observer {
            if (waitDialog.isShowing) waitDialog.dismiss()
            editOrdersResponseData = it
            editOrders = editOrdersResponseData.products
//            val adapter = DashboardAdapter(orders, View.OnClickListener(){
//            })
            if (requisitionIdEdit == editOrdersResponseData.data.id)
            {
                if (!dialoClicked) {
                    dialoClicked = true
                    showEditOrderDialog(editOrders, editOrdersResponseData.data)
                }
        }
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
                //NotificationUtil.showShortToast(context!!, context!!.getString(R.string.error_occurred), Type.DANGER)
            }
        })

    }

    fun triggerMainFragmentFunction(dashBoardOrdersData: DashBoardOrdersData) {
        editData = dashBoardOrdersData
    }

    fun showEditOrderDialog(products: ArrayList<OrderProductsData>, data: EditOrdersData) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val inflater: LayoutInflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogLayout: View = inflater.inflate(R.layout.custom_edit_product_dialog, null)
        val myCardView: CardView = dialogLayout.findViewById(R.id.cv_edit_order)
        val btnOk: FancyButton = dialogLayout.findViewById(R.id.btn_update_cart_edit)
        val btnCloseDialog: ImageButton = dialogLayout.findViewById(R.id.cancel_product_image_cart)
        val custName: TextView = dialogLayout.findViewById(R.id.tv_customer_name_edit_dialog)
        val deliveryDateLayout: LinearLayout = dialogLayout.findViewById(R.id.ll_delivery_date)
        val delivDate: TextView = dialogLayout.findViewById(R.id.tv_delivery_date_edit_dialog)
        val ivCalender: ImageView = dialogLayout.findViewById(R.id.iv_date_picker_edit)
        val recyclerView: RecyclerView = dialogLayout.findViewById(R.id.rv_edit_item_list)
        val etProducts: AutoCompleteTextView =
            dialogLayout.findViewById(R.id.et_select_product_edit)
        val textProductsQuantity: TextView =
            dialogLayout.findViewById(R.id.tv_product_quantity_edit)
        val etProductsQuantit: TextView =
            dialogLayout.findViewById(R.id.et_select_product_quantity_edit)
        adapter = OrderEditAdapter(viewModel, products, View.OnClickListener { item ->
            refreshAdapter()
        })
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        val adapterTvProducts = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            MainActivity.productNamesData
        )
        etProducts.setAdapter(adapterTvProducts)
        etProducts.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (etProducts.isPerformingCompletion) {
                    ///    Log.i("CustomerIndex", "Change Text  " + MainActivity.productIdData[count] +  " Item  " + MainActivity.productNamesData[count] )
                    etProducts.error = null
                    for ((index) in MainActivity.productUnitData.withIndex()) {
                        if (etProducts.text.toString().equals(
                                MainActivity.productNamesData[index]
                            )
                        ) {
                            productId = MainActivity.productIdData[index]
                            productUnit = MainActivity.productUnitData[index]
                            textProductsQuantity.text = "Quantity $productUnit"
                            Log.i("CustomerIndex", "ID:   $productId Customer:   $productUnit")
                            break
                        }
                    }
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (etProducts.isPerformingCompletion) {
                    etProducts.error = null
                    return
                }
                if (etProducts.text.toString().isNotEmpty())
                    etProducts.error = "Choose Product"
                else
                    etProducts.error = null
            }
        })
        // linearLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent))
        //  linearLayout.setBackgroundResource(Color.TRANSPARENT)
        myCardView.setCardBackgroundColor(Color.TRANSPARENT)
        myCardView.cardElevation = 0f
        builder.setView(dialogLayout)
        builder.setCancelable(false)
        editOrderDialog = builder.create()
        editOrderDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        editOrderDialog.show()
        if (waitDialog.isShowing)
            waitDialog.dismiss()
        custName.text = data.customer
        delivDate.text = data.delivery_date
        // Create an ArrayAdapter using a simple spinner layout and languages array
        // Set layout to use when the list of choices appear
        // Set Adapter to Spinner
        deliveryDateLayout.setOnClickListener {
            datePick(delivDate, ivCalender)  //TODO change it
        }
        btnOk.setOnClickListener {
            val productsArray = JsonArray()
            dialoClicked = false
            val editOrderObject = JsonObject()
            editOrderObject.addProperty("customer_id", data.customer_id)
            editOrderObject.addProperty("delivery_date", delivDate.text.toString())
            editOrderObject.addProperty("request_id", data.id)
            editOrderObject.addProperty("city_id", USER_STORED_CITY_ID)
            for ((index) in products.withIndex()) {
                val editOrderProducts = JsonObject()
                editOrderProducts.addProperty("value", products[index].value)
                editOrderProducts.addProperty("quantity", productQuantity[index])
                editOrderProducts.addProperty("is_removed", 0)
                productsArray.add(editOrderProducts)
            }
            if (etProducts.text.isNotEmpty() && etProductsQuantit.text.isNotEmpty()) {
                val editOrderProducts = JsonObject()
                editOrderProducts.addProperty("value", productId)
                editOrderProducts.addProperty("quantity", etProductsQuantit.text.toString())
                editOrderProducts.addProperty("is_removed", 0)
                etProducts.setText("")
                etProductsQuantit.text = ""
                productsArray.add(editOrderProducts)
            }

            editOrderObject.add("products", productsArray)
            startEditObserver()
            viewModelEdit.startObserver(editOrderObject)
        }
        btnCloseDialog.setOnClickListener {
            dialoClicked = false
            if (waitDialog.isShowing)
                waitDialog.dismiss()
            editOrderDialog.dismiss()
        }
    }

     fun refreshAdapter() {
        adapter.notifyDataSetChanged()
    }

    private fun startEditObserver() {
        viewModelEdit.callEditApi.observe(viewLifecycleOwner, Observer {
            if (it!!) {
                if (waitDialog != null && !waitDialog.isShowing) {
                    waitDialog = ProgressDialog.show(requireContext(), "", "Updating")
                    waitDialog.setCanceledOnTouchOutside(true)
                }
            }
        })
        viewModelEdit.apiResponseSuccess.observe(viewLifecycleOwner, Observer {
            if (waitDialog.isShowing) waitDialog.dismiss()
            editOrderResponseData = it
            dialoClicked = false
            if (waitDialog.isShowing)
                waitDialog.dismiss()
            editOrderDialog.hide()
            NotificationUtil.showShortToast(
                context!!,
                context!!.getString(R.string.order_updated),
                Type.SUCCESS
            )
            //orders = dashboardResponseData.data
//            val adapter = DashboardAdapter(orders, View.OnClickListener(){
//            })
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
                NotificationUtil.showShortToast(
                    context!!,
                    context!!.getString(R.string.error_occurred),
                    Type.DANGER
                )
            }
        })
    }

    private fun handleOrderUpdate() {
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        Log.i("ClickedPosition", p2.toString())
    }

    fun datePick(tvDate: TextView, ivCalender: ImageView) {
        //function to show the date picker
        /**
         * This Gets a calendar using the default time zone and locale.
         * The calender returned is based on the current time
         * in the default time zone with the default.
         */
        val myCalendar = Calendar.getInstance()
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)

        //get the id of the textviews from the layout

        /**
         * Creates a new date picker dialog for the specified date using the parent
         * context's default date picker dialog theme.
         */
        val dpd = DatePickerDialog(
            requireContext(), { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                /**
                 * The listener used to indicate the user has finished selecting a date.
                 */

                /**
                 * The listener used to indicate the user has finished selecting a date.
                 */
                /**
                 * The listener used to indicate the user has finished selecting a date.
                 */


                /**
                 * The listener used to indicate the user has finished selecting a date.
                 */

                /**
                 *Here the selected date is set into format i.e : Year-Month-Day
                 * And the month is counted in java is 0 to 11 so we need to add +1 so it can be as selected.
                 * */

                /**
                 *Here the selected date is set into format i.e : Year-Month-Day
                 * And the month is counted in java is 0 to 11 so we need to add +1 so it can be as selected.
                 * */
                /**
                 *Here the selected date is set into format i.e : Year-Month-Day
                 * And the month is counted in java is 0 to 11 so we need to add +1 so it can be as selected.
                 * */
                /**
                 *Here the selected date is set into format i.e : Year-Month-Day
                 * And the month is counted in java is 0 to 11 so we need to add +1 so it can be as selected.
                 * */
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDayOfMonth"
                // Selected date it set to the TextView to make it visible to user.
                // tvSelectedDate.text = selectedDate

                /**
                 * Here we have taken an instance of Date Formatter as it will format our
                 * selected date in the format which we pass it as an parameter and Locale.
                 * Here I have passed the format as dd/MM/yyyy.
                 */

                /**
                 * Here we have taken an instance of Date Formatter as it will format our
                 * selected date in the format which we pass it as an parameter and Locale.
                 * Here I have passed the format as dd/MM/yyyy.
                 */
                /**
                 * Here we have taken an instance of Date Formatter as it will format our
                 * selected date in the format which we pass it as an parameter and Locale.
                 * Here I have passed the format as dd/MM/yyyy.
                 */
                /**
                 * Here we have taken an instance of Date Formatter as it will format our
                 * selected date in the format which we pass it as an parameter and Locale.
                 * Here I have passed the format as dd/MM/yyyy.
                 */
                val sdf = SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH)
                // The formatter will parse the selected date in to Date object
                // so we can simply get date in to milliseconds.
                val theDate = sdf.parse(selectedDate)
                //use the safe call operator with .let to avoid app crashing it theDate is null
                theDate?.let {

                    tvDate.text = sdf.format(theDate)
                    ivCalender.setImageResource(R.drawable.calendar)
                    // Here we have parsed the current date with the Date Formatter which is used above.
                    val currentDate = sdf.parse(sdf.format(System.currentTimeMillis()))
                    //use the safe call operator with .let to avoid app crashing it currentDate is null
                }
            },
            year, month, day
        )

        /**
         * Sets the maximal date supported by this in
         * milliseconds since January 1, 1970 00:00:00 in time zone.
         *
         * @param maxDate The maximal supported date.
         */
        // 86400000 is milliseconds of 24 Hours. Which is used to restrict the user from selecting today and future day.
        dpd.datePicker.minDate = System.currentTimeMillis() - 1000
        dpd.show()

    }




}