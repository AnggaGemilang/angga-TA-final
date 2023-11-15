package com.agrapana.fertigation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agrapana.fertigation.helper.AuthListener
import com.agrapana.fertigation.model.AuthResponse

class LoginViewModel : ViewModel() {

    val loginResponse = MutableLiveData<AuthResponse?>()
    var authListener: AuthListener? = null

    fun onLoginButtonClick(email: String, password: String){
        if(email.isEmpty() || password.isEmpty()){
            authListener?.onFailure("Email or Password Is Empty!")
        } else {

        }
    }
}
