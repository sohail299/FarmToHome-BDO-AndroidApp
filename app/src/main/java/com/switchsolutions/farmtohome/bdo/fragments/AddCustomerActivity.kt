package com.switchsolutions.farmtohome.bdo.fragments

import android.R
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.switchsolutions.farmtohome.bdo.AddCustomerRequestModel
import com.switchsolutions.farmtohome.bdo.MainActivity
import com.switchsolutions.farmtohome.bdo.ValidationUtil
import com.switchsolutions.farmtohome.bdo.databinding.AddCustomerFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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

    companion object {
        fun newInstance() = AddCustomerActivity()
    }

    private lateinit var viewModel: AddCustomerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddCustomerFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE)
        USER_SELECTED_BRANCH_NAME = prefs.getString("BranchName", ISLAMABAD_I9)
            .toString() //"No name defined" is the default value.
        USER_STORED_CITY_ID = prefs.getInt("cityId", 4) //0 is the default value.
        USER_SELECTED_BRANCH_INDEX = prefs.getInt("BranchIndex", 0)
        viewModel = ViewModelProvider(this)[AddCustomerViewModel::class.java]
        viewModel.statusSectorsSuccess.observe(this) { it ->
            it.getContentIfNotHandled()?.let {
                if (it.statusCode == 200 && it.data.isNotEmpty()) {
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

        viewModel.statusAddCustomerSuccess.observe(this) { it ->
            it.getContentIfNotHandled()?.let {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                finish()
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
                etCustomerPassword.error = "Password should be at least 5 characters"
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
            addCustomerJson.addProperty("email", etCustomerEmail.text.toString())
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
}