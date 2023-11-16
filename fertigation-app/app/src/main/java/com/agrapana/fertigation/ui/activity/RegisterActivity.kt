package com.agrapana.fertigation.ui.activity

import android.R
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.agrapana.fertigation.databinding.ActivityRegisterBinding
import com.agrapana.fertigation.helper.AuthListener
import com.agrapana.fertigation.model.AuthResponse
import com.agrapana.fertigation.viewmodel.AuthViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity(), AuthListener {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        viewModel.authListener = this

        binding.btnRegister.setOnClickListener {
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
        response.observe(this) {
            val prefs: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
            val editor: SharedPreferences.Editor? = prefs.edit()
            editor?.putBoolean("login_status", true)
            editor?.putString("client_id", it?.id)
            editor?.putString("client_name", it?.name!!.split(" ")[0])
            editor?.putString("client_role", it?.role)
            editor?.apply()
            startActivity(Intent(this, MainActivity::class.java))
            Toast.makeText(this, "Register Succeed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onFailure(message: String) {
        Toast.makeText(this, "Register Failed", Toast.LENGTH_SHORT).show()
    }
}