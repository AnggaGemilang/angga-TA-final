package com.agrapana.arnesys.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agrapana.arnesys.api.FieldService
import com.agrapana.arnesys.api.MonitoringMainDeviceService
import com.agrapana.arnesys.api.RetroInstance
import com.agrapana.arnesys.model.ChartMainDeviceResponse
import com.agrapana.arnesys.model.FieldResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailMainDeviceViewModel: ViewModel() {

    var loadChartMainDevice =  MutableLiveData<ChartMainDeviceResponse?>()

    fun getLoadChartObservable(): MutableLiveData<ChartMainDeviceResponse?> {
        return loadChartMainDevice
    }

    fun getChartMainDevice(id: String, column: String, type: String) {
        val retroInstance = RetroInstance.getRetroInstance().create(MonitoringMainDeviceService::class.java)
        retroInstance.getChartOfMonitoringMainDevice(id, column, type)
            .enqueue(object : Callback<ChartMainDeviceResponse> {
                override fun onResponse(
                    call: Call<ChartMainDeviceResponse>,
                    response: Response<ChartMainDeviceResponse>)
                {
                    if(response.isSuccessful){
                        Log.d("dadang_nanang", response.body().toString())
                        loadChartMainDevice.postValue(response.body())
                    } else {
                        loadChartMainDevice.postValue(null)
                    }
                }
                override fun onFailure(call: Call<ChartMainDeviceResponse>, t: Throwable) {
                    loadChartMainDevice.postValue(null)
                }
            })
    }
}