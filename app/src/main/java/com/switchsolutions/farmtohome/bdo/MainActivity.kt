package com.switchsolutions.farmtohome.bdo

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.switchsolutions.farmtohome.bdo.adapters.ViewSingleOrderAdapter
import com.switchsolutions.farmtohome.bdo.callbacks.HttpStatusCodes
import com.switchsolutions.farmtohome.bdo.databinding.ActivityMainBinding
import com.switchsolutions.farmtohome.bdo.enums.Type
import com.switchsolutions.farmtohome.bdo.fragments.*
import com.switchsolutions.farmtohome.bdo.interfaces.CartBadge
import com.switchsolutions.farmtohome.bdo.interfaces.PlayBeep
import com.switchsolutions.farmtohome.bdo.interfaces.ReplaceFragment
import com.switchsolutions.farmtohome.bdo.interfaces.ShowOrderDetail
import com.switchsolutions.farmtohome.bdo.responsemodels.*
import com.switchsolutions.farmtohome.bdo.room_db.branch.BranchDatabase
import com.switchsolutions.farmtohome.bdo.room_db.branch.BranchRespository
import com.switchsolutions.farmtohome.bdo.room_db.cart.CartDatabase
import com.switchsolutions.farmtohome.bdo.room_db.cart.CartEntityClass
import com.switchsolutions.farmtohome.bdo.room_db.cart.CartRepository
import com.switchsolutions.farmtohome.bdo.room_db.customer.CustomerDatabase
import com.switchsolutions.farmtohome.bdo.room_db.customer.CustomerEntityClass
import com.switchsolutions.farmtohome.bdo.room_db.customer.CustomerRepository
import com.switchsolutions.farmtohome.bdo.room_db.product.ProductDatabase
import com.switchsolutions.farmtohome.bdo.room_db.product.ProductEntityClass
import com.switchsolutions.farmtohome.bdo.room_db.product.ProductRepository
import com.switchsolutions.farmtohome.bdo.viewmodels.CustomersApiViewModel
import com.switchsolutions.farmtohome.bdo.viewmodels.DashboardFragmentViewModel
import com.switchsolutions.farmtohome.bdo.viewmodels.ProductsApiViewModel
import mehdi.sakout.fancybuttons.FancyButton

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, ReplaceFragment, CartBadge, PlayBeep,
    ShowOrderDetail {
    companion object {
        var customerName: String = ""
        var customerId: Int = 0
        var badgeCount: Int = 0
        var requestID: Int = 0
        var deliveryDate: String = ""
        var alreadyStored: Boolean = false
        var userToken: String = ""
        var waitDilogFlag = false
        var token: String = ""
        var usersData: ArrayList<CustomersData> = ArrayList()
        var productsData: ArrayList<ProductsData> = ArrayList()
        fun newInstance() = MainActivity()
        var customerNamesData: ArrayList<String> = ArrayList()
        var customerIdData: ArrayList<Int> = ArrayList()
        var productNamesData: ArrayList<String> = ArrayList()
        var productIdData: ArrayList<Int> = ArrayList()
        var productUnitData: ArrayList<String> = ArrayList()
        var branchNamesData: ArrayList<String> = ArrayList()
        var branchIdData: ArrayList<Int> = ArrayList()
        var productQuantity: ArrayList<String> = ArrayList()
    }
    private lateinit var binding: ActivityMainBinding
    private val ISLAMABAD_I9 = "Islamabad-I9"
    private val MY_PREFS_NAME = "FarmToHomeBDO"
    private var USER_SELECTED_BRANCH_INDEX = 0
    private var USER_STORED_CITY_ID = 4
    private var CITY_ID = 4
    private var USER_SELECTED_BRANCH_NAME = ""
    private lateinit var viewModel: CartViewModel
    private lateinit var productsApiViewModel: ProductsApiViewModel
    private lateinit var customersApiViewModel: CustomersApiViewModel
    private lateinit var customerVM: CustomerViewModel
    private lateinit var productVM: ProductViewModel
    private lateinit var cartVM: CartViewModel
    private lateinit var branchVM: BranchViewModel
    private lateinit var viewModelSingle: DashboardFragmentViewModel
    private lateinit var editOrdersResponseData: GetOrdersForEditResponseModel
    private lateinit var products: ArrayList<OrderProductsData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMain)
        supportActionBar?.apply {
            // toolbar button click listener
            binding.cvToolbarLocation.setOnClickListener {
                // change toolbar title
                showLocationSelectionBox()
            }

            binding.ivBtnSettings.setOnClickListener {
                // change toolbar title

                openSettingDialog()
            }
        }
        supportActionBar?.apply {
            // toolbar button click listener
        }

        redirectToMainFragment(savedInstanceState)
        viewModelSingle = ViewModelProvider(this).get(DashboardFragmentViewModel::class.java)
        val prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE)
        USER_SELECTED_BRANCH_NAME = prefs.getString("BranchName", ISLAMABAD_I9)
            .toString() //"No name defined" is the default value.
        USER_STORED_CITY_ID = prefs.getInt("cityId", 4) //0 is the default value.
        USER_SELECTED_BRANCH_INDEX = prefs.getInt("BranchIndex", 0) //0 is the default value.
        customerId = prefs.getInt("customerId", 0) //0 is the default value.
        badgeCount = prefs.getInt("badgeCount", 0) //0 is the default value.
        customerName = prefs.getString("customerName", "").toString() //"" is the default value.
        userToken = prefs.getString("accessToken", "").toString() //"" is the default value.
        deliveryDate =
            prefs.getString("customerDeliveryDate", "").toString() //" is the default value.
        token = prefs.getString("accessToken", "").toString()
        binding.tvLocation.text = USER_SELECTED_BRANCH_NAME
        productsApiViewModel = ViewModelProvider(this).get(ProductsApiViewModel::class.java)
        customersApiViewModel = ViewModelProvider(this).get(CustomersApiViewModel::class.java)
        startProductsApiObservers()
        startCustomersApiObserver()
        productsApiViewModel.startObserver(USER_STORED_CITY_ID)
        customersApiViewModel.startObserver(USER_STORED_CITY_ID)
       // GetCustomersService(this, USER_STORED_CITY_ID)
        val dao = CartDatabase.getInstance(this).cartDAO
        val repository = CartRepository(dao)
        val factory = CartViewModelFactory(repository)
        cartVM = ViewModelProvider(this, factory).get(CartViewModel::class.java)
        getBranches()
        //var badgeCount = cartVM.getCartItemCount()
        val badge = binding.bottomNavigation.getOrCreateBadge(R.id.item3)
        badge.isVisible = true
        badge.number = badgeCount
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item0 -> {
                    replaceFragment(DashboardFragment(), DashboardFragment.TAG)
                    title = "Processing"
                    true
                }
                R.id.item1 -> {
                    replaceFragment(DispatchFragment(), DispatchFragment.TAG)
                    title = "Dispatch"
                    // Respond to navigation item 2 click
                    true
                }
                R.id.item2 -> {
                    replaceFragment(CreateRequestFragment(), CreateRequestFragment.TAG)
                    title = getString(R.string.create_request)
                    // Respond to navigation item 2 click
                    true
                }
                R.id.item3 -> {
                    replaceFragment(CartFragment(), CartFragment.TAG)
                    title = "Cart"
                    // Respond to navigation item 2 click
                    true
                }
                R.id.item4 -> {
                    replaceFragment(DeliveredFragment(), DeliveredFragment.TAG)
                    title = "Delivered"
                    // Respond to navigation item 2 click
                    true
                }
                else -> false
            }
        }
//        binding.bottomBar.onItemSelected = {
//            binding.bottomBar
//            Log.i("Bottom Navigation", "$it selected")
//            if (it == 0) {
////                val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
////                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
//                replaceFragment(DashboardFragment(), DashboardFragment.TAG)
//                title = "Processing"
//            } else if (it == 1) {
//                replaceFragment(DispatchFragment(), DispatchFragment.TAG)
//                title = "Dispatch"
//            } else if (it == 2) {
//                Log.i("ProductIdList", customerNamesData.size.toString())
//                replaceFragment(CreateRequestFragment(), CreateRequestFragment.TAG)
//                title= getString(R.string.create_request)
//
//            } else if (it == 3) {
//                replaceFragment(CartFragment(), CartFragment.TAG)
//                title= "Cart"
//            } else if (it == 4) {
//                replaceFragment(DeliveredFragment(), DeliveredFragment.TAG)
//                title="Delivered"
//            }
//        }
//        binding.bottomBar.onItemReselected = {
//            Log.i("Bottom Navigation", "$it Re-selected")
//        }
        getCustomerNames()
        getProductsNames()
    }

    private fun openSettingDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogLayout: View = inflater.inflate(R.layout.layout_settings, null)
        val myCardView: CardView = dialogLayout.findViewById(R.id.dialog_settings)
        val btnAddCustomer: Button = dialogLayout.findViewById(R.id.btn_add_customer)
        val btnLogout: Button = dialogLayout.findViewById(R.id.btn_logout)

        // linearLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent))
        //  linearLayout.setBackgroundResource(Color.TRANSPARENT)
        myCardView.setCardBackgroundColor(Color.TRANSPARENT)
        myCardView.cardElevation = 0f
        builder.setView(dialogLayout)
        builder.setCancelable(true)
        val settingDialog: Dialog = builder.create()
        settingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        settingDialog.show()

        btnAddCustomer.setOnClickListener {
            val i =Intent(this@MainActivity, AddCustomerActivity::class.java) //TODO Replace with Logout function
            startActivity(i)
            settingDialog.dismiss()
            finish()
        }
        btnLogout.setOnClickListener {
            settingDialog.dismiss()
            userLogout()
        }
    }

    private fun userLogout() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(this.getString(R.string.confirm_logout))
            .setPositiveButton(this.getString(R.string.logout)) { dialog, _ ->
                val editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit()
                editor.putInt("User", 0)
                editor.putBoolean("isLoggedIn", false)
                editor.putString("accessToken", "")
                editor.putInt("badgeCount", 0)
                editor.apply()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                dialog.dismiss()
            }
            .setNegativeButton(this.getString(R.string.cancel)) { dialog, _ ->
                // cancel delete process
               dialog.dismiss()
            }
        builder.show()
    }

    fun redirectToMainFragment(bundle: Bundle?) {
        if (findViewById<View?>(R.id.fragment_container) != null) {
            if (bundle != null) return
            clearBackStack()
            title = "Processing"
            val fragment = DashboardFragment()
            replaceFragment(fragment, DashboardFragment::class.java.simpleName)
        }
    }

    fun refreshFragment() {
        val frg: Fragment = supportFragmentManager.findFragmentByTag(CartFragment.TAG)!!;
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction();
        ft.detach(frg)
        ft.attach(frg)
        ft.commit()
    }

    fun replaceFragment(fragment: Fragment, tag: String?) {
        clearBackStack()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment).commit()
    }

    private fun clearBackStack() {
        val fm = supportFragmentManager
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    private fun showLocationSelectionBox() {

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogLayout: View = inflater.inflate(R.layout.custom_location_selection_dialog, null)
        val myCardView: CardView = dialogLayout.findViewById(R.id.dialog_location)
        val spinner: Spinner = dialogLayout.findViewById(R.id.sp_select_location)
        val btnOk: FancyButton = dialogLayout.findViewById(R.id.btn_ok_alert_msg)
        // linearLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent))
        //  linearLayout.setBackgroundResource(Color.TRANSPARENT)
        myCardView.setCardBackgroundColor(Color.TRANSPARENT)
        myCardView.cardElevation = 0f
        builder.setView(dialogLayout)
        builder.setCancelable(false)
        val locationDialog: Dialog = builder.create()
        locationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        locationDialog.show()
        spinner.onItemSelectedListener = this

        // Create an ArrayAdapter using a simple spinner layout and languages array
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, branchNamesData)
        // Set layout to use when the list of choices appear
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        spinner.setSelection(2)
        Log.i("SelectedIndex", USER_SELECTED_BRANCH_INDEX.toString())
        spinner.adapter = aa
        btnOk.setOnClickListener {
            if (!USER_SELECTED_BRANCH_NAME.isEmpty()) {
                binding.tvLocation.text = USER_SELECTED_BRANCH_NAME
                locationDialog.hide()
                CITY_ID = branchIdData[USER_SELECTED_BRANCH_INDEX]
                Log.i("SelectedCity", USER_SELECTED_BRANCH_INDEX.toString())
                val prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE)
                USER_STORED_CITY_ID = prefs.getInt("cityId", 1)
                val editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit()
                editor.putInt("cityId", CITY_ID)
                editor.putInt("BranchIndex", USER_SELECTED_BRANCH_INDEX)
                editor.putString("BranchName", USER_SELECTED_BRANCH_NAME)
                editor.apply()
                if (USER_STORED_CITY_ID != CITY_ID) {
                    binding.bottomNavigation.selectedItemId = R.id.item0
                    //replaceFragment(DashboardFragment(), DashboardFragment.TAG)
                    //title = "Processing"
                }
                updateCartData()
            }
        }
    }

    private fun updateCartData() {
        if (USER_STORED_CITY_ID == CITY_ID)
        else {
            val dao = CartDatabase.getInstance(this).cartDAO
            val repository = CartRepository(dao)
            val factory = CartViewModelFactory(repository)
            viewModel = ViewModelProvider(this, factory).get(CartViewModel::class.java)
            binding.mainCartViewModel = viewModel
            viewModel.clearAll()
            val editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit()
            editor.putInt("cityId", CITY_ID)
            editor.putInt("customerId", 0)
            editor.putString("customerName", "")
            editor.putString("customerDeliveryDate", "")
            editor.apply()
            resetValues()
            startProductsApiObservers()
            startCustomersApiObserver()
            productsApiViewModel.startObserver(CITY_ID)
            customersApiViewModel.startObserver(CITY_ID)
        }
    }

    private fun getCustomerNames() {

        val daoCustomer = CustomerDatabase.getInstance(this).customerDAO
        val repositoryCustomer = CustomerRepository(daoCustomer)
        val factoryCustomer = CustomerViewModelFactory(repositoryCustomer)
        customerVM = ViewModelProvider(this, factoryCustomer).get(CustomerViewModel::class.java)
        binding.customerViewModel = customerVM
        customerVM.getSavedProducts().observe(this, Observer {
            if (it.isNotEmpty()) {
                customerNamesData.clear()
                customerIdData.clear()
                for ((index) in it.withIndex()) {
                    customerNamesData.add(it[index].customer_name)
                    customerIdData.add(it[index].customer_id)
                }
            } else {
                customerNamesData = ArrayList()
                customerIdData = ArrayList()
            }
        })
    }

    private fun getProductsNames() {
        val daoProduct = ProductDatabase.getInstance(this).productDao
        val repositoryProduct = ProductRepository(daoProduct)
        val factoryProduct = ProductViewModelFactory(repositoryProduct)
        productVM = ViewModelProvider(this, factoryProduct).get(ProductViewModel::class.java)
        binding.productViewModel = productVM
        productVM.getSavedProducts().observe(this, Observer {
            if (it.isNotEmpty()) {
                productNamesData.clear()
                productIdData.clear()
                productUnitData.clear()
                for ((index) in it.withIndex()) {
                    productNamesData.add(it[index].productName)
                    productIdData.add(it[index].product_id)
                    productUnitData.add(it[index].productUnit)
                }
            } else {
                productNamesData = ArrayList()
                productIdData = ArrayList()
                productUnitData = ArrayList()
            }
        })
    }

    fun updateAutofillProducts(){
        productNamesData.clear()
        productIdData.clear()
        productUnitData.clear()

        val daoProduct = ProductDatabase.getInstance(this).productDao
        val repositoryProduct = ProductRepository(daoProduct)
        val factoryProduct = ProductViewModelFactory(repositoryProduct)
        productVM = ViewModelProvider(this, factoryProduct).get(ProductViewModel::class.java)
        binding.productViewModel = productVM
        if (productsData.isNotEmpty()) {
            productVM.clearAll()
        }
        for (element in productsData) {
            productVM.insertProduct(
                ProductEntityClass(
                    0,
                    element.value!!,
                    element.label!!,
                    element.unit!!
                )
            )
        }
        for ((index) in productsData.withIndex()) {
            productNamesData.add(productsData[index].label!!)
        }
        for ((index) in productsData.withIndex()) {
            productIdData.add(productsData[index].value!!)
        }
        for ((index) in productsData.withIndex()) {
            productUnitData.add(productsData[index].unit!!)
        }

    }
    fun getBranches(){
        branchNamesData.clear()
        branchIdData.clear()
        if (branchNamesData.isEmpty() || branchIdData.isEmpty()) {
            val daoBranch = BranchDatabase.getInstance(this).branchDAO
            val repositoryBranch = BranchRespository(daoBranch)
            val factoryBranch = BranchViewModelFactory(repositoryBranch)
            branchVM = ViewModelProvider(this, factoryBranch).get(BranchViewModel::class.java)
            binding.branchViewModel = branchVM
            branchVM.getSavedBranches().observe(this, Observer {
                if (it.isNotEmpty()) {
                    for ((index) in it.withIndex()) {
                        branchNamesData.add(it[index].code)
                        branchIdData.add(it[index].value)
                    }
                } else {
                    branchNamesData = ArrayList()
                    branchIdData = ArrayList()
                }
            })
        }
    }

    fun updateAutofilCustomerDetails() {
        customerNamesData.clear()
        customerIdData.clear()
        val daoCustomer = CustomerDatabase.getInstance(this).customerDAO
        val repositoryCustomer = CustomerRepository(daoCustomer)
        val factoryCustomer = CustomerViewModelFactory(repositoryCustomer)
        customerVM = ViewModelProvider(this, factoryCustomer).get(CustomerViewModel::class.java)
        binding.customerViewModel = customerVM
        if (usersData.isNotEmpty()) {
            customerVM.clearAll()
        }
        for (element in usersData) {
            customerVM.insertProduct(CustomerEntityClass(0, element.value!!, element.label!!))
        }
        for ((index) in usersData.withIndex()) {
            customerNamesData.add(usersData[index].label!!)
        }
        for ((index) in usersData.withIndex()) {
            customerIdData.add(usersData[index].value!!)
        }
    }

    private fun resetValues() {
        customerName = ""
        customerId = 0
        deliveryDate = ""
    }


    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        USER_SELECTED_BRANCH_NAME = branchNamesData[p2]
        USER_SELECTED_BRANCH_INDEX = p2
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun replaceWithMainFragment(fragment: Fragment?) {
        clearBackStack()
        binding.bottomNavigation.selectedItemId = R.id.item0

    }

    fun startProductsApiObservers() {
        productsApiViewModel.callProductsInApi.observe(this, Observer {
        })
        productsApiViewModel.apiResponseSuccess.observe(this, Observer {
            productsData = it.data
//            val adapter = DashboardAdapter(orders, View.OnClickListener(){
//            })
            updateAutofillProducts()
            getProductsNames()
            //showEditOrderDialog()
        })
        productsApiViewModel.apiResponseFailure.observe(this, Observer {
            if ((it?.statusCode == HttpStatusCodes.SC_UNAUTHORIZED)) {
                NotificationUtil.showShortToast(this, "Please Login again", Type.DANGER)
                val editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit()
                editor.putInt("User", 0)
                editor.putBoolean("isLoggedIn", false)
                editor.putString("accessToken", "")
                editor.putInt("badgeCount", 0)
                editor.apply()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
//                val builder = AlertDialog.Builder(context!!)
//                builder.setMessage(context?.getString(R.string.invalid_credentials))
//                        .setPositiveButton(context?.getString(R.string.ok)) { dialog, _ ->
//                            dialog.dismiss()
//                        }
//                builder.create().show()
            } else if((it?.statusCode == HttpStatusCodes.SC_NO_CONTENT)){
//                Toast.makeText(
//                    requireContext(), "An Error Occurred",
//                    Toast.LENGTH_LONG
//                ).show()
//                NotificationUtil.showShortToast(
//                    this,
//                    this.getString(R.string.error_occurred)+it.message,
//                    Type.DANGER
//                )
            }
            else{

            }
        })

    }
    fun startCustomersApiObserver(){
        customersApiViewModel.callCustomersInApi.observe(this, Observer {
        })
        customersApiViewModel.apiResponseSuccessCustomers.observe(this, Observer {
            usersData = it.data
//            val adapter = DashboardAdapter(orders, View.OnClickListener(){
//            })
            updateAutofilCustomerDetails()
            getCustomerNames()
            //showEditOrderDialog()
        })
        customersApiViewModel.apiResponseFailureCustomers.observe(this, Observer {
            if ((it?.statusCode == HttpStatusCodes.SC_UNAUTHORIZED) || (it?.statusCode == HttpStatusCodes.SC_NO_CONTENT)) {
                NotificationUtil.showShortToast(this, "UnAuthorized", Type.DANGER)
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
//                NotificationUtil.showShortToast(
//                    this,
//                    this.getString(R.string.error_occurred)+it.message,
//                    Type.DANGER
//                )
            }
        })
    }

    override fun cartBadge(count: Int) {
        val badge = binding.bottomNavigation.getOrCreateBadge(R.id.item3)
        badge.isVisible = true
        badge.number = count
    }

    override fun playBeep() {
        val mp: MediaPlayer = MediaPlayer.create(this, R.raw.success_sound)
        mp.start()
    }

    override fun showOrderDetails(reqId: Int) {
        viewModelSingle.getSingleOrder(reqId)
    }

    override fun startObserverForSingleOrder() {
        viewModelSingle.callSingleApi.observe(this, Observer {
            if (it!!) {
//                if (waitDialog != null && !waitDialog.isShowing) {
//                    waitDialog = ProgressDialog.show(this, "", "Fetching Detail..")
//                    waitDialog.setCancelable(true)
//                }
            }
        })
        viewModelSingle.singleApiResponseSuccess.observe(this, Observer {
//            if (waitDialog.isShowing) waitDialog.dismiss()
            editOrdersResponseData = it
            products = it.products
            if (requestID == editOrdersResponseData.data.id) {
                if (!DashboardFragment.dialoClicked) {
                    DashboardFragment.dialoClicked = true
                    showSingleOrderDialog(products, editOrdersResponseData.data)
                }
            }

        })
        viewModelSingle.singleApiResponseFailure.observe(this, Observer {
            //  if (waitDialog.isShowing) waitDialog.dismiss()
            if ((it?.statusCode == HttpStatusCodes.SC_UNAUTHORIZED) || (it?.statusCode == HttpStatusCodes.SC_NO_CONTENT)) {
                Toast.makeText(
                    this, "Unauthorized",
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
//                NotificationUtil.showShortToast(
//                    this,
//                    this.getString(R.string.error_occurred)+it.message,
//                    Type.DANGER
//                )
            }
        })
    }

    private fun showSingleOrderDialog(products: ArrayList<OrderProductsData>, data: EditOrdersData) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            val inflater: LayoutInflater =
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val dialogLayout: View = inflater.inflate(R.layout.custom_single_product_dialog, null)
            val myCardView: CardView = dialogLayout.findViewById(R.id.cv_edit_order)
            val btnOk: FancyButton = dialogLayout.findViewById(R.id.btn_update_cart_edit)
            val btnCloseDialog: ImageButton = dialogLayout.findViewById(R.id.cancel_product_image_cart)
            val custName: TextView = dialogLayout.findViewById(R.id.tv_customer_name_edit_dialog)
            val delivDate: TextView = dialogLayout.findViewById(R.id.tv_delivery_date_edit_dialog)
            val recyclerView: RecyclerView = dialogLayout.findViewById(R.id.rv_edit_item_list)
            val adapter = ViewSingleOrderAdapter(viewModelSingle, products, View.OnClickListener {
                //showEditOrderDialog()
            })
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()

            // linearLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent))
            //  linearLayout.setBackgroundResource(Color.TRANSPARENT)
            myCardView.setCardBackgroundColor(Color.TRANSPARENT)
            myCardView.cardElevation = 0f
            builder.setView(dialogLayout)
            builder.setCancelable(false)
            val editOrderDialog: Dialog = builder.create()
            editOrderDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            editOrderDialog.show()
            custName.text = data.customer
            delivDate.text = data.delivery_date
            // Create an ArrayAdapter using a simple spinner layout and languages array
            // Set layout to use when the list of choices appear
            // Set Adapter to Spinner

            btnOk.setOnClickListener {
                editOrderDialog.dismiss()
                DashboardFragment.dialoClicked = false
            }
            btnCloseDialog.setOnClickListener {
                DashboardFragment.dialoClicked = false
                editOrderDialog.dismiss()
            }

    }
}