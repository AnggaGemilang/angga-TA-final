package com.agrapana.arnesys.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agrapana.arnesys.api.AuthService
import com.agrapana.arnesys.api.RetroInstance
import com.agrapana.arnesys.helper.AuthListener
import com.agrapana.arnesys.model.AuthResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {

    val loginResponse = MutableLiveData<AuthResponse?>()
    var authListener: AuthListener? = null

    fun onLoginButtonClick(email: String, password: String){
        if(email.isEmpty() || password.isEmpty()){
            authListener?.onFailure("Email or Password Is Empty!")
        } else {
            val retroInstance = RetroInstance.getRetroInstance().create(AuthService::class.java)
            retroInstance.login(email, password)
                .enqueue(object: Callback<AuthResponse> {
                    override fun onResponse(
                        call: Call<AuthResponse>,
                        response: Response<AuthResponse>
                    ) {
                        if(response.isSuccessful){
                            loginResponse.value = response.body()
                            authListener?.onSuccess(loginResponse)
                        } else {
                            authListener?.onFailure("Email or Password Is Incorrect!")
                        }
                    }

                    override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                        loginResponse.value = null
                    }

                })
        }
    }
}
