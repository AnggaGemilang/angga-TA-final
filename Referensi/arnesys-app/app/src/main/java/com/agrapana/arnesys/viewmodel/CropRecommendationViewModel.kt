package com.agrapana.arnesys.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agrapana.arnesys.api.FieldAIService
import com.agrapana.arnesys.api.FieldService
import com.agrapana.arnesys.api.RetroInstance
import com.agrapana.arnesys.api.RetroInstance2
import com.agrapana.arnesys.model.AIInput
import com.agrapana.arnesys.model.CropRecommendResponse
import com.agrapana.arnesys.model.FieldResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CropRecommendationViewModel: ViewModel() {

    var loadCropRecommend =  MutableLiveData<CropRecommendResponse?>()

    fun getLoadCropRecommendObservable(): MutableLiveData<CropRecommendResponse?> {
        return loadCropRecommend
    }

    fun getCropRecommend(id: AIInput) {
        val retroInstance = RetroInstance2.getRetroInstance().create(FieldAIService::class.java)
        retroInstance.getCropRecommend(id)
            .enqueue(object : Callback<CropRecommendResponse> {
                override fun onResponse(
                    call: Call<CropRecommendResponse>,
                    response: Response<CropRecommendResponse>)
                {
                    if(response.isSuccessful){
                        loadCropRecommend.postValue(response.body())
                    } else {
                        loadCropRecommend.postValue(null)
                    }
                }
                override fun onFailure(call: Call<CropRecommendResponse>, t: Throwable) {
                    loadCropRecommend.postValue(null)
                }
            })
    }
}