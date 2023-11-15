package com.agrapana.fertigation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agrapana.fertigation.model.ChartSupportDeviceResponse

class DetailSupportDeviceViewModel: ViewModel() {

    var loadChartSupportDevice =  MutableLiveData<ChartSupportDeviceResponse?>()

    fun getLoadChartObservable(): MutableLiveData<ChartSupportDeviceResponse?> {
        return loadChartSupportDevice
    }

    fun getChartSupportDevice(id: String, number: Int, column: String, type: String) {

    }
}