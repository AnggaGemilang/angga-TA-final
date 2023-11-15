package com.agrapana.fertigation.helper

import androidx.lifecycle.LiveData
import com.agrapana.fertigation.model.AuthResponse

interface AuthListener {

    fun onSuccess(response: LiveData<AuthResponse?>)
    fun onFailure(message: String)

}