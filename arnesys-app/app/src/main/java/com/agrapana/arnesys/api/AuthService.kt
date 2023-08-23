package com.agrapana.arnesys.api

import com.agrapana.arnesys.model.AuthResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface AuthService {

    @GET("login/post")
    @Headers("Accept:application/json","Content-Type:application/json")
    fun login(@Query("email") email: String, @Query("password") password: String): Call<AuthResponse>

}