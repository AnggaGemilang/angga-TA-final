package com.agrapana.fertigation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agrapana.fertigation.model.FieldResponse

class FieldViewModel: ViewModel() {

    var loadFilterFieldData =  MutableLiveData<FieldResponse?>()

    fun getLoadFieldObservable(): MutableLiveData<FieldResponse?> {
        return loadFilterFieldData
    }

    fun getAllField(id: String) {

    }
}