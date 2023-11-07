package com.agrapana.arnesys.api

import com.agrapana.arnesys.model.ChartSupportDeviceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface MonitoringSupportDeviceService {

    @GET("monitoring-support-devices/get-chart/{id}/{number}/{column}/{type}")
    @Headers("Accept:application/json","Content-Type:application/json")
    fun getChartOfMonitoringSupportDevice(
        @Path("id") id: String,
        @Path("number") number: Int,
        @Path("column") column: String,
        @Path("type") type: String,
    ): Call<ChartSupportDeviceResponse>

}