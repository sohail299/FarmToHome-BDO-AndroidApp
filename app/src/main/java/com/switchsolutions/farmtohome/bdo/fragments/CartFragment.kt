package com.switchsolutions.farmtohome.bdo.fragments

import android.animation.ValueAnimator
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.switchsolutions.farmtohome.bdo.*
import com.switchsolutions.farmtohome.bdo.callbacks.HttpStatusCodes
import com.switchsolutions.farmtohome.bdo.databinding.CreateCartFragmentBinding
import com.switchsolutions.farmtohome.bdo.dbhelper.DBHelper
import com.switchsolutions.farmtohome.bdo.enums.Type
import com.switchsolutions.farmtohome.bdo.interfaces.CartBadge
import com.switchsolutions.farmtohome.bdo.interfaces.ReplaceFragment
import com.switchsolutions.farmtohome.bdo.responsemodels.BDORequestResponsemodel
import com.switchsolutions.farmtohome.bdo.responsemodels.CartItems
import com.switchsolutions.farmtohome.bdo.room_db.CartDatabase
import com.switchsolutions.farmtohome.bdo.room_db.CartEntityClass
import com.switchsolutions.farmtohome.bdo.room_db.CartRepository
import com.switchsolutions.farmtohome.bdo.viewmodels.SubmitCartViewModel
import mehdi.sakout.fancybuttons.FancyButton

class CartFragment : Fragment() {
    companion object {
        const val TAG: String = "cartFragment"
        var item: ArrayList<String>? = ArrayList()
        val placeOrderJson = JsonObject()
        fun newInstance() = CartFragment()
         var productQuantity: ArrayList<String> = ArrayList()
    }

    private lateinit var cartViewModel: CartViewModel
    private lateinit var adapter: MyCartRecyclerViewAdapter
    lateinit var binding: CreateCartFragmentBinding
    lateinit var helper: DBHelper
    lateinit var cartDataList: List<CartEntityClass>
    lateinit var product: CartEntityClass

    private lateinit var submitViewModel: SubmitCartViewModel
    var cartItems: ArrayList<CartItems> = ArrayList()
    private lateinit var waitDialog: ProgressDialog
    private lateinit var submissionDataResponse: BDORequestResponsemodel
    var customerId: Int = 0
    var cityId: Int = 0
    private val MY_PREFS_NAME = "FarmToHomeBDO"
    var customerName: String = ""
    var deliveryDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val prefs = requireContext().getSharedPreferences(MY_PREFS_NAME, AppCompatActivity.MODE_PRIVATE)
        customerId = prefs.getInt("customerId", 0)
        cityId = prefs.getInt("cityId", 1)
        customerId = prefs.getInt("customerId", 1) //0 is the default value.
        customerName = prefs.getString("customerName", "").toString() //0 is the default value.
        deliveryDate =
            prefs.getString("customerDeliveryDate", "").toString() //0 is the default value.
        binding = CreateCartFragmentBinding.inflate(layoutInflater)

        helper = DBHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        submitViewModel = ViewModelProvider(this).get(SubmitCartViewModel::class.java)

        val dao = CartDatabase.getInstance(requireContext()).cartDAO
        val repository = CartRepository(dao)
        val factory = CartViewModelFactory(repository)
        cartViewModel = ViewModelProvider(this, factory).get(CartViewModel::class.java)
        binding.myCartViewModel = cartViewModel
        binding.lifecycleOwner = this
        cartViewModel.message.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
               // Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        })
        binding.btnSubmitCart.setOnClickListener {
            val productsArray = JsonArray()

//                placeOrderJson.addProperty("customer_id", 9241)
//                placeOrderJson.addProperty("delivery_date", "2022-03-12")
//                placeOrderJson.addProperty("city_id", 1)
//                productObject.addProperty("name", "Potato")
//                productObject.addProperty("value", 40)
//                productObject.addProperty("is_removed", 0)
//                productObject.addProperty("name", "Tomato")
//                productObject.addProperty("value", 50)
//                productObject.addProperty("is_removed", 0)
//                productsArray.add(productObject)
//                placeOrderJson.add("products", productsArray)
//                CreateBdoRequestService(requireContext(), placeOrderJson )
            if (cartDataList.isNotEmpty()) {
                placeOrderJson.addProperty("customer_id", customerId)
                placeOrderJson.addProperty("delivery_date", deliveryDate)
                placeOrderJson.addProperty("city_id", cityId)
                for ((index) in cartDataList.withIndex()) {
                    product = cartDataList[index]
                    val productObject = JsonObject()
                    productObject.addProperty("name", cartDataList[index].productId)
                    productObject.addProperty("value", productQuantity[index])
                    Log.i("ProductQuantity", product.quantity)
                    productObject.addProperty("is_removed", 0)
                    productsArray.add(productObject)
                }
                placeOrderJson.add("products", productsArray)
                //CreateBdoRequestService(cartViewModel, this, requireContext(), placeOrderJson).callApi()
                startObservers()
                submitViewModel.startObserver()
            }
        }
        initRecyclerView()
    }
    fun startObservers() {
        submitViewModel.callCartSubmitApi.observe(viewLifecycleOwner, Observer {
            if (it!!) {
                waitDialog = ProgressDialog.show(requireContext(), "", "Submitting")
            }
        })
        submitViewModel.apiResponseSuccess.observe(viewLifecycleOwner, Observer {
            if (waitDialog.isShowing) waitDialog.dismiss()
            submissionDataResponse = it
//            val adapter = DashboardAdapter(orders, View.OnClickListener(){
//            })
            //showEditOrderDialog()
            cartViewModel.clearAll()
            clearCart()

            showSubmissionSuccessMessage()
            initRecyclerView()
        })
        submitViewModel.apiResponseFailure.observe(viewLifecycleOwner, Observer {
            if ((it?.statusCode == HttpStatusCodes.SC_UNAUTHORIZED) || (it?.statusCode == HttpStatusCodes.SC_NO_CONTENT)) {
                Toast.makeText(
                    requireContext(), "Failed to Submit",
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
                NotificationUtil.showShortToast(context!!, context!!.getString(R.string.error_occurred), Type.DANGER)
            }
        })
    }
    private fun clearCart() {

        val editor = requireContext().getSharedPreferences(MY_PREFS_NAME, AppCompatActivity.MODE_PRIVATE).edit()
        editor.putInt("badgeCount", 0)
        editor.putInt("customerId", 0) //0 is the default value.
        editor.putString("customerName", "")//"" is the default value.
        editor.putString("customerDeliveryDate", "" )
        editor.apply()
        val cb : CartBadge = activity as CartBadge
        cb.cartBadge(0)

    }

    fun initRecyclerView() {
        binding.tvCustomerNameCart.text = customerName
        binding.tvDeliveryDateCart.text = deliveryDate
        //binding = CreateCartFragmentBinding.inflate(layoutInflater)
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MyCartRecyclerViewAdapter { selectedItem: CartEntityClass ->
            listItemClicked(
                selectedItem
            )
        }
        binding.cartRecyclerView.adapter = adapter
        displaySubscribersList()
    }

    fun showSubmissionSuccessMessage() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val inflater: LayoutInflater =
            requireContext().getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogLayout: View = inflater.inflate(R.layout.dialog_custom_cart_submission, null)
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
//        val handler = Handler()
//        handler.postDelayed({
//            loginDialog.dismiss()
//            jumpToMainFragment()
//           // MainActivity.newInstance().redirectToMainFragment(savedStateRegistry)
//
//        }, 2000)
        buttonOk.setOnClickListener {
            loginDialog.dismiss()
            jumpToMainFragment()
            //jumpToMainFragment()

            ///

        }
    }

    private fun jumpToMainFragment() {
        val fr: Fragment = DashboardFragment()
        val rf: ReplaceFragment? = activity as ReplaceFragment?
        rf!!.replaceWithMainFragment(fr)
    }

    private fun displaySubscribersList() {
        cartViewModel.getSavedProducts().observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                cartDataList = it
                binding.btnSubmitCart.visibility = View.VISIBLE
                binding.cvCartCustomerDetails.visibility = View.VISIBLE
                adapter.setList(it)
                adapter.notifyDataSetChanged()
            } else {
               clearCart()
                binding.btnSubmitCart.visibility = View.GONE
                binding.cvCartCustomerDetails.visibility = View.GONE
                adapter.setList(it)
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun listItemClicked(product: CartEntityClass) {
        cartViewModel.deleteProduct(product)
    }

}
