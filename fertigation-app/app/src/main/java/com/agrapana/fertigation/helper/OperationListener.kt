package com.agrapana.fertigation.helper

interface OperationListener {

    fun onSuccess()
    fun onFailure(message: String)

}