package com.agrapana.arnesys.helper

import androidx.lifecycle.LiveData
import com.agrapana.arnesys.model.AuthResponse

interface AuthListener {

    fun onSuccess(response: LiveData<AuthResponse?>)
    fun onFailure(message: String)

}