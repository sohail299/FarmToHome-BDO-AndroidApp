package com.switchsolutions.farmtohome.bdo


import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.switchsolutions.farmtohome.bdo.callbacks.HttpStatusCodes
import com.switchsolutions.farmtohome.bdo.databinding.ActivitySigninBinding
import com.switchsolutions.farmtohome.bdo.enums.Type
import com.switchsolutions.farmtohome.bdo.responsemodels.pojo.LoginResponseModel
import com.switchsolutions.farmtohome.bdo.responsemodels.pojo.Roles
import com.switchsolutions.farmtohome.bdo.room_db.branch.BranchDatabase
import com.switchsolutions.farmtohome.bdo.room_db.branch.BranchEntityClass
import com.switchsolutions.farmtohome.bdo.room_db.branch.BranchRespository
import com.switchsolutions.farmtohome.bdo.viewmodels.SignInViewModel


class LoginActivity : AppCompatActivity(),
    View.OnClickListener/*, ICallBackListener<LoginResponseModel>*/ {
    lateinit var binding: ActivitySigninBinding
    private lateinit var loginRequestModel: LoginRequestModel
    private lateinit var loginResponseModel: LoginResponseModel
    private lateinit var waitDialog: ProgressDialog
    private lateinit var viewModel: SignInViewModel
    private lateinit var roles: ArrayList<Roles>
    private val MY_PREFS_NAME = "FarmToHomeBDO"
    private lateinit var branchViewmodel: BranchViewModel

    companion object {
        const val SIGNIN_FRAGMENT_TAG: String = "signInFrag"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout for this fragment
        binding = ActivitySigninBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[SignInViewModel::class.java]
        setContentView(binding.root)
        val preferences = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE)
        var name = preferences.getInt("User", 0)
        var isLoggedIn = preferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        startObservers()
        binding.btnSignIn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val id: Int = v?.id!!
        when (id) {
            binding.btnSignIn.id -> viewModel.signInClicked()
        }
    }

    private fun startObservers() {
        viewModel.getLoginRequestModel().observe(this, Observer {
            loginRequestModel = it!!
            binding.loginRequestModel = loginRequestModel
        })
        viewModel.signInEmailErrorStatus.observe(this, Observer {
            if (it!!) setEmailError()
        })
        viewModel.signInPasswordErrorStatus.observe(this, Observer {
            if (it!!) setPasswordError()
        })
        viewModel.clearAllInputErrors.observe(this, Observer {
            if (it!!) clearAllErrors()
        })
        viewModel.callSignInApi.observe(this, Observer {
            if (it!!) {
                waitDialog = ProgressDialog.show(this, "", "Sign in")
            }
        })
        viewModel.apiResponseSuccess.observe(this, Observer {
            if (waitDialog.isShowing) waitDialog.dismiss()
            loginResponseModel = it
            //redirect to main landing screen
            roles = loginResponseModel.data?.roles!!
            if (roles.size >= 0) {
                for (element in roles) {
                    if (!element.guard.equals("bdo")) {
                        Toast.makeText(
                            this, "Unauthorized",
                            Toast.LENGTH_LONG
                        ).show()
                        break
                    } else {
                        storeBranches()
                        val editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit()
                        editor.putInt("User", loginResponseModel.data!!.id!!)
                        editor.putBoolean("isLoggedIn", true)
                        editor.putString("accessToken", loginResponseModel.accessToken)
                        if (loginResponseModel.data!!.siteBranches.isNotEmpty())
                        {
                            editor.putInt("cityId", loginResponseModel.data!!.siteBranches[0].value!!)
                            editor.putString("BranchName", loginResponseModel.data!!.siteBranches[0].code!!)
                        }
                        editor.apply()
                        Toast.makeText(
                            this, "SUCCESS",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        break
                    }

                }
            }
//            if (loginResponseModel.data?.roles!!["gaurd"].equals("BDO") == true) {
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
//                finish()
//            }
        })
        viewModel.apiResponseFailure.observe(this, Observer {
            if (waitDialog.isShowing) waitDialog.dismiss()
            if ((it?.statusCode == HttpStatusCodes.SC_UNAUTHORIZED) || (it?.statusCode == HttpStatusCodes.SC_NO_CONTENT)) {
                Toast.makeText(
                    this, it.message,
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
//                    this, "An Error Occurred",
//                    Toast.LENGTH_LONG
//                ).show()
                //NotificationUtil.showShortToast(this, this.getString(R.string.error_occurred) + it.message, Type.DANGER)
            }
        })
    }

    private fun storeBranches() {
        val daoBranch = BranchDatabase.getInstance(this).branchDAO
        val repositoryBranch = BranchRespository(daoBranch)
        val factoryBranch = BranchViewModelFactory(repositoryBranch)
        branchViewmodel = ViewModelProvider(this, factoryBranch).get(BranchViewModel::class.java)
        binding.branchViewModel = branchViewmodel
        if (loginResponseModel.data!!.siteBranches.isNotEmpty()) {
            branchViewmodel.clearAll()
        }
        for (element in loginResponseModel.data!!.siteBranches) {
            branchViewmodel.insertBranch(
                BranchEntityClass(
                    0,
                    element.value!!,
                    element.label!!,
                    element.code!!
                )
            )
        }
    }

    private fun setEmailError() {
        // NotificationUtil.showShortToast(context!!, getString(R.string.email_is_invalid), Type.DANGER)
        binding.llUserCode.error = getString(R.string.phone_number_is_invalid)
    }

    private fun setPasswordError() {
        // NotificationUtil.showShortToast(context!!, getString(R.string.password_error), Type.DANGER)
        binding.etPassword.error = "invalid Password"
//        binding.tilUserPassword.isErrorEnabled = true
    }

    private fun clearAllErrors() {
//        binding.tilUserEmail.isErrorEnabled = false
//        binding.tilUserPassword.isErrorEnabled = false
    }

}
