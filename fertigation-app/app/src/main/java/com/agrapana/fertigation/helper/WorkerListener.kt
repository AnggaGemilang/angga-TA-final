package com.agrapana.fertigation.helper

import androidx.lifecycle.LiveData
import com.agrapana.fertigation.model.AuthResponse

interface WorkerListener {

    fun onSuccess()
    fun onFailure(message: String)

}