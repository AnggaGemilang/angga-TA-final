package com.agrapana.arnesys.api

import com.agrapana.arnesys.model.ChartMainDeviceResponse
import com.agrapana.arnesys.model.AIInput
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface MonitoringMainDeviceService {

    @GET("monitoring-main-devices/get-chart/{id}/{column}/{type}")
    @Headers("Accept:application/json","Content-Type:application/json")
    fun getChartOfMonitoringMainDevice(
        @Path("id") id: String,
        @Path("column") column: String,
        @Path("type") type: String
    ): Call<ChartMainDeviceResponse>

    @GET("monitoring-main-devices/get-pest-prediction/{id}")
    @Headers("Accept:application/json","Content-Type:application/json")
    fun getPestDataInput(
        @Path("id") id: String
    ): Call<AIInput>

}