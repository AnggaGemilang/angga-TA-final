package com.agrapana.arnesys.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agrapana.arnesys.api.FieldAIService
import com.agrapana.arnesys.api.RetroInstance2
import com.agrapana.arnesys.model.AIInput
import com.agrapana.arnesys.model.PestPredictionResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PestPredictionViewModel: ViewModel() {

    var loadPestPrediction =  MutableLiveData<PestPredictionResponse?>()

    fun getLoadPestPredictionObservable(): MutableLiveData<PestPredictionResponse?> {
        return loadPestPrediction
    }

    fun getPestPrediction(datas: AIInput) {
        val retroInstance = RetroInstance2.getRetroInstance().create(FieldAIService::class.java)
        retroInstance.getPestPrediction(datas)
            .enqueue(object : Callback<PestPredictionResponse> {
                override fun onResponse(
                    call: Call<PestPredictionResponse>,
                    response: Response<PestPredictionResponse>
                ) {
                    if(response.isSuccessful){
                        loadPestPrediction.postValue(response.body())
                    } else {
                        loadPestPrediction.postValue(null)
                    }
                }

                override fun onFailure(call: Call<PestPredictionResponse>, t: Throwable) {
                    loadPestPrediction.postValue(null)
                }
            })
    }
}