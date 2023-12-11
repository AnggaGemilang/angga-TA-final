package com.agrapana.fertigation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agrapana.fertigation.model.Field
import com.agrapana.fertigation.model.MonitoringMonitorDevice
import com.agrapana.fertigation.model.MonitoringPrimaryDevice
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PrimaryDeviceViewModel: ViewModel() {

    private val dbMonitoring = FirebaseDatabase.getInstance().getReference("monitoring")
    private val _primaryDevices = MutableLiveData<List<MonitoringPrimaryDevice>?>()
    val primaryDevices: MutableLiveData<List<MonitoringPrimaryDevice>?>
        get() = _primaryDevices

    private val valueEventListener = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) { }
        override fun onDataChange(snapshot: DataSnapshot) {
            val monitoringPrimaryDevices = mutableListOf<MonitoringPrimaryDevice>()
            if (snapshot.exists()) {
                for (dataSnapshot in snapshot.children) {
                    val monitoringPrimaryDevice = dataSnapshot.getValue(MonitoringPrimaryDevice::class.java)
                    monitoringPrimaryDevice?.let { monitoringPrimaryDevices.add(it) }
                }
                _primaryDevices.value = monitoringPrimaryDevices
            } else {
                _primaryDevices.value = monitoringPrimaryDevices
            }
        }
    }

    fun fetchMonitoringData(ownerId: String, fieldId: String) {
        dbMonitoring.child(ownerId).child(fieldId).child("primaryDevice").addListenerForSingleValueEvent(valueEventListener)
    }

}