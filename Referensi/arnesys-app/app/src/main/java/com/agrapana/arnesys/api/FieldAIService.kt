package com.agrapana.arnesys.api

import com.agrapana.arnesys.model.FieldResponse
import com.agrapana.arnesys.model.AIInput
import com.agrapana.arnesys.model.CropRecommendResponse
import com.agrapana.arnesys.model.PestPredictionResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FieldAIService {

    @POST("recommend")
    @Headers("Accept:application/json","Content-Type:application/json")
    fun getCropRecommend(@Body data: AIInput): Call<CropRecommendResponse>

    @POST("pest")
    @Headers("Accept:application/json","Content-Type:application/json")
    fun getPestPrediction(@Body data: AIInput): Call<PestPredictionResponse>


}