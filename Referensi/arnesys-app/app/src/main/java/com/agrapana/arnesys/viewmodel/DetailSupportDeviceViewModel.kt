package com.agrapana.arnesys.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agrapana.arnesys.api.MonitoringSupportDeviceService
import com.agrapana.arnesys.api.RetroInstance
import com.agrapana.arnesys.model.ChartSupportDeviceResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailSupportDeviceViewModel: ViewModel() {

    var loadChartSupportDevice =  MutableLiveData<ChartSupportDeviceResponse?>()

    fun getLoadChartObservable(): MutableLiveData<ChartSupportDeviceResponse?> {
        return loadChartSupportDevice
    }

    fun getChartSupportDevice(id: String, number: Int, column: String, type: String) {
        val retroInstance = RetroInstance.getRetroInstance().create(MonitoringSupportDeviceService::class.java)
        retroInstance.getChartOfMonitoringSupportDevice(id, number, column, type)
            .enqueue(object : Callback<ChartSupportDeviceResponse> {
                override fun onResponse(
                    call: Call<ChartSupportDeviceResponse>,
                    response: Response<ChartSupportDeviceResponse>)
                {
                    if(response.isSuccessful){
                        loadChartSupportDevice.postValue(response.body())
                    } else {
                        loadChartSupportDevice.postValue(null)
                    }
                }
                override fun onFailure(call: Call<ChartSupportDeviceResponse>, t: Throwable) {
                    loadChartSupportDevice.postValue(null)
                }
            })
    }
}