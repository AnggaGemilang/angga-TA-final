package com.agrapana.arnesys.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agrapana.arnesys.api.FieldService
import com.agrapana.arnesys.api.RetroInstance
import com.agrapana.arnesys.model.FieldResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FieldViewModel: ViewModel() {

    var loadFilterFieldData =  MutableLiveData<FieldResponse?>()

    fun getLoadFieldObservable(): MutableLiveData<FieldResponse?> {
        return loadFilterFieldData
    }

    fun getAllField(id: String) {
        val retroInstance = RetroInstance.getRetroInstance().create(FieldService::class.java)
        retroInstance.getFieldsByClient(id)
            .enqueue(object : Callback<FieldResponse> {
                override fun onResponse(
                    call: Call<FieldResponse>,
                    response: Response<FieldResponse>)
                {
                    if(response.isSuccessful){
                        loadFilterFieldData.postValue(response.body())
                    } else {
                        loadFilterFieldData.postValue(null)
                    }
                }
                override fun onFailure(call: Call<FieldResponse >, t: Throwable) {
                    loadFilterFieldData.postValue(null)
                }
            })
    }
}