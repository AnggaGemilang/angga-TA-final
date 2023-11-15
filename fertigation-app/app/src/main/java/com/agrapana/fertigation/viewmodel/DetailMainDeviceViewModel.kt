package com.agrapana.fertigation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agrapana.fertigation.model.ChartMainDeviceResponse

class DetailMainDeviceViewModel: ViewModel() {

    var loadChartMainDevice =  MutableLiveData<ChartMainDeviceResponse?>()

    fun getLoadChartObservable(): MutableLiveData<ChartMainDeviceResponse?> {
        return loadChartMainDevice
    }

    fun getChartMainDevice(id: String, column: String, type: String) {

    }
}