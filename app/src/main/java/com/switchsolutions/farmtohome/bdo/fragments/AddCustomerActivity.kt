package com.switchsolutions.farmtohome.bdo.fragments

import android.R
import android.animation.ValueAnimator
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.switchsolutions.farmtohome.bdo.AddCustomerRequestModel
import com.switchsolutions.farmtohome.bdo.MainActivity
import com.switchsolutions.farmtohome.bdo.NotificationUtil
import com.switchsolutions.farmtohome.bdo.ValidationUtil
import com.switchsolutions.farmtohome.bdo.databinding.AddCustomerFragmentBinding
import com.switchsolutions.farmtohome.bdo.enums.Type
import com.switchsolutions.farmtohome.bdo.interfaces.PlayBeep
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mehdi.sakout.fancybuttons.FancyButton

class AddCustomerActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    lateinit var binding: AddCustomerFragmentBinding
    lateinit var addCustomerRequestModel: AddCustomerRequestModel
    var sector = ""
    private val ISLAMABAD_I9 = "Islamabad-I9"
    private val MY_PREFS_NAME = "FarmToHomeBDO"
    private var USER_SELECTED_BRANCH_INDEX = 0
    private var USER_STORED_CITY_ID = 4
    val addCustomerJson = JsonObject()
    private var USER_SELECTED_BRANCH_NAME = ""
    private  var sectors: ArrayList<String> = ArrayList()
    private var products: ArrayList<String> = ArrayList()
    private lateinit var waitDialog: ProgressDialog

    companion object {
        fun newInstance() = AddCustomerActivity()
    }
    private lateinit var viewModel: AddCustomerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddCustomerFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarAddCustomer)
        supportActionBar.apply {
            title = "Add Customer"
        }
        waitDialog = ProgressDialog(this)
        val prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE)
        USER_SELECTED_BRANCH_NAME = prefs.getString("BranchName", ISLAMABAD_I9)
            .toString() //"No name defined" is the default value.
        USER_STORED_CITY_ID = prefs.getInt("cityId", 4) //0 is the default value.
        USER_SELECTED_BRANCH_INDEX = prefs.getInt("BranchIndex", 0)
        viewModel = ViewModelProvider(this)[AddCustomerViewModel::class.java]

        viewModel.statusAddUserCall.observe(this, Observer { it ->
            it.getContentIfNotHandled()?.let {
                if (it)
                {
                    waitDialog = ProgressDialog.show(this, "", "Please wait")
                }
                else
                {
                    if (waitDialog.isShowing)
                        waitDialog.dismiss()
                }
            }
        })
        viewModel.statusSectorsSuccess.observe(this) { it ->
            it.getContentIfNotHandled()?.let {
                if (it.statusCode == 200 && it.data.isNotEmpty()) {
                    sectors.clear()
                    for ((index) in it.data.withIndex()) {
                        sectors.add(it.data[index].label)
                    }
                    binding.spSectors.onItemSelectedListener = this
                    val adapterSectors = ArrayAdapter(this, R.layout.simple_spinner_item, sectors)
                    // Set layout to use when the list of choices appear
                    adapterSectors.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    // Set Adapter to Spinner
                    // Log.i("SelectedIndex", USER_SELECTED_BRANCH_INDEX.toString())
                    binding.spSectors.adapter = adapterSectors
                } else
                    sectors = ArrayList()
            }
        }
        viewModel.statusSectorsFailure.observe(this) { it ->
            it.getContentIfNotHandled()?.let {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()

            }
        }
        viewModel.statusAddCustomerFailure.observe(this) { it ->
            it.getContentIfNotHandled()?.let {
                if (it.statusCode ==400)
                    NotificationUtil.showShortToast(this, "The phone has already been taken", Type.DANGER)
                //Toast.makeText(this, "The phone has already been taken", Toast.LENGTH_LONG).show()

            }
        }

        viewModel.statusAddCustomerSuccess.observe(this) { it ->
            it.getContentIfNotHandled()?.let {
                showSubmissionSuccessMessage()
            }
        }
        // getSectorsList()
        viewModel.getSectors(USER_STORED_CITY_ID)

        binding.spBranch.onItemSelectedListener = this
        val adapterBranches =
            ArrayAdapter(this, R.layout.simple_spinner_item, MainActivity.branchNamesData)
        // Set layout to use when the list of choices appear
        adapterBranches.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        // Log.i("SelectedIndex", USER_SELECTED_BRANCH_INDEX.toString())
        binding.spBranch.adapter = adapterBranches


        val adapter =
            ArrayAdapter(this, R.layout.simple_dropdown_item_1line, MainActivity.productNamesData)
        binding.nachoTextView.setAdapter(adapter)
        binding.btnAddCustomer.setOnClickListener {
            addCustomerBtnClicked()
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(this@AddCustomerActivity, MainActivity::class.java)
        startActivity(i)
        finish()
    }

    private fun addCustomerBtnClicked() {
        binding.apply {
            if (etSelectCustomer.text.isEmpty()) {
                etSelectCustomer.error = "Enter Customer Name"
                return
            }
            if (etEnterPhone.text.isEmpty()) {
                etEnterPhone.error = "Enter Phone Number"
                return
            }
            if (!ValidationUtil.isPhoneNumberValid(etEnterPhone.text.toString())) {
                etEnterPhone.error = "Enter a Valid Phone Number"
                return
            }
            if (etCustomerAddress.text.isEmpty()) {
                etCustomerAddress.error = "Enter Address"
                return
            }
            if (etCustomerPassword.text.isEmpty()) {
                etCustomerPassword.error = "Enter Password"
                return
            }
            if (!ValidationUtil.isPasswordValid(etCustomerPassword.text.toString())) {
                etCustomerPassword.error = "Password should be at least 8 characters"
                return
            }
            if (sector.isEmpty()) {
                Toast.makeText(
                    this@AddCustomerActivity,
                    "Please select a sector",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            if (binding.nachoTextView.allChips.isEmpty()){
                Toast.makeText(
                    this@AddCustomerActivity,
                    "Please add some products",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            val productsArray = JsonArray()
            addCustomerJson.addProperty("name", etSelectCustomer.text.toString())
            addCustomerJson.addProperty("msisdn", etEnterPhone.text.toString())
            addCustomerJson.addProperty("address", etCustomerAddress.text.toString())
            addCustomerJson.addProperty("sector", sector)
            addCustomerJson.addProperty("city_id", USER_STORED_CITY_ID)
            addCustomerJson.addProperty("email","")
            addCustomerJson.addProperty("password", etCustomerPassword.text.toString())
                    for (chip in binding.nachoTextView.allChips) {
                        val productObject = JsonObject()
                        for ((index) in MainActivity.productNamesData.withIndex()){
                            if (chip.text.toString() == MainActivity.productNamesData[index])
                                productObject.addProperty("site_product_id", MainActivity.productIdData[index])
                        }
                        productsArray.add(productObject)
                }
                    addCustomerJson.add("products", productsArray)
                    viewModel.addCustomerBtnClicked(addCustomerJson)
            //CreateBdoRequestService(cartViewModel, this, requireContext(), placeOrderJson).callApi()
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        when (p0!!.id) {
            binding.spBranch.id -> {
                USER_SELECTED_BRANCH_NAME = MainActivity.branchNamesData[p2]
                USER_STORED_CITY_ID = MainActivity.branchIdData[p2]
                viewModel.getSectors(USER_STORED_CITY_ID)
            }
            binding.spSectors.id -> {
                sector = sectors[p2]
            }
            else -> {}
        }
        //sector = MainActivity.branchNamesData[p2]
    }
    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
    fun showSubmissionSuccessMessage() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater: LayoutInflater =
            this.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogLayout: View = inflater.inflate(com.switchsolutions.farmtohome.bdo.R.layout.dialog_add_user_message, null)
        val myCardView: CardView = dialogLayout.findViewById(com.switchsolutions.farmtohome.bdo.R.id.dialog_sign_in_box)
        val buttonOk: FancyButton = dialogLayout.findViewById(com.switchsolutions.farmtohome.bdo.R.id.btn_ok_submission)
        // linearLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent))
        //  linearLayout.setBackgroundResource(Color.TRANSPARENT)
        myCardView.setCardBackgroundColor(Color.TRANSPARENT)
        myCardView.cardElevation = 0f
        ///
        val anim: LottieAnimationView = dialogLayout.findViewById(com.switchsolutions.farmtohome.bdo.R.id.animation_view)
        builder.setView(dialogLayout)
        builder.setCancelable(false)
        val loginDialog: Dialog = builder.create()
        loginDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loginDialog.show()
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 2000
        animator.addUpdateListener(ValueAnimator.AnimatorUpdateListener { valueAnimator ->
            anim.progress = valueAnimator.animatedValue as Float
        })
        animator.start()
        val handler = Handler()
        handler.postDelayed({
            loginDialog.dismiss()
           // finish()
           // MainActivity.newInstance().redirectToMainFragment(savedStateRegistry)

        }, 2000)
        buttonOk.setOnClickListener {
            loginDialog.dismiss()
           //finish()
        }
        loginDialog.setOnDismissListener {
            val i = Intent(this@AddCustomerActivity, MainActivity::class.java) //TODO Replace with Logout function
            startActivity(i)
            finish()
        }
    }
}