package com.agrapana.fertigation.ui.activity

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.agrapana.fertigation.databinding.ActivityRegisterBinding
import com.agrapana.fertigation.helper.AuthListener
import com.agrapana.fertigation.model.AuthResponse
import com.agrapana.fertigation.viewmodel.AuthViewModel
import org.imaginativeworld.oopsnointernet.NoInternetDialog

class RegisterActivity : AppCompatActivity(), AuthListener {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: AuthViewModel
    private var progressDialog: ProgressDialog? = null
    private var noInternetDialog: NoInternetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        viewModel.authListener = this

        binding.btnRegister.setOnClickListener {
            progressDialog = ProgressDialog(this)
            progressDialog!!.setTitle("Please Wait")
            progressDialog!!.setMessage("System is working . . .")
            progressDialog!!.show()
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etPasswordConfirm.text.toString()
            viewModel.onRegister(name, email, password, confirmPassword)
        }

        binding.etPassword.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0!!.toString().length > 3 && p0.toString().contains("[a-z]".toRegex()) && p0.toString().contains("[0-9]".toRegex())
                        && p0.toString().contains("[!\"#\$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}~]".toRegex())){
                    binding.txtPasswordError.visibility = View.GONE
                } else {
                    binding.txtPasswordError.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.etPasswordConfirm.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val stringBuilder = StringBuilder()
                stringBuilder.append(binding.etPassword.text)
                if(stringBuilder.contentEquals(p0, false)){
                    binding.txtPasswordConfirmError.visibility = View.GONE
                } else {
                    binding.txtPasswordConfirmError.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }

    override fun onSuccess(response: LiveData<AuthResponse?>) {
        progressDialog!!.dismiss()
        response.observe(this) {
            val prefs: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
            val editor: SharedPreferences.Editor? = prefs.edit()
            editor?.putBoolean("login_status", false)
            editor?.putString("client_id", it?.ownerId)
            editor?.putString("client_name", it?.name!!.split(" ")[0])
            editor?.putString("client_role", it?.role)
            editor?.apply()
            startActivity(Intent(this, MainActivity::class.java))
            Toast.makeText(this, "Register Succeed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onFailure(message: String) {
        progressDialog!!.dismiss()
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        val builder1 = NoInternetDialog.Builder(this)
        builder1.cancelable = false // Optional
        builder1.noInternetConnectionTitle = "No Internet" // Optional
        builder1.noInternetConnectionMessage = "Check your Internet connection and try again" // Optional
        builder1.showInternetOnButtons = true // Optional
        builder1.pleaseTurnOnText = "Please turn on" // Optional
        builder1.wifiOnButtonText = "Wifi" // Optional
        builder1.mobileDataOnButtonText = "Mobile data" // Optional
        builder1.onAirplaneModeTitle = "No Internet" // Optional
        builder1.onAirplaneModeMessage = "You have turned on the airplane mode." // Optional
        builder1.pleaseTurnOffText = "Please turn off" // Optional
        builder1.airplaneModeOffButtonText = "Airplane mode" // Optional
        builder1.showAirplaneModeOffButtons = true // Optional
        noInternetDialog = builder1.build()
    }

    override fun onPause() {
        super.onPause()
        if (noInternetDialog != null) {
            noInternetDialog!!.destroy();
        }
    }

}