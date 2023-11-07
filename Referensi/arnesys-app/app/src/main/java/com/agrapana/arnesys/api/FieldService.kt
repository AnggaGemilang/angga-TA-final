package com.agrapana.arnesys.api

import com.agrapana.arnesys.model.FieldResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface FieldService {

    @GET("field/{id}")
    @Headers("Accept:application/json","Content-Type:application/json")
    fun getFieldsByClient(@Path("id") id: String): Call<FieldResponse>

}