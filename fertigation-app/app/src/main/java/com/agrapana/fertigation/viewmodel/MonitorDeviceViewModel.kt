package com.agrapana.fertigation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agrapana.fertigation.model.Field
import com.agrapana.fertigation.model.MonitoringMonitorDevice
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MonitorDeviceViewModel: ViewModel() {

    private val dbMonitoring = FirebaseDatabase.getInstance().getReference("monitoring")
    private val _monitorDevices = MutableLiveData<List<MonitoringMonitorDevice>?>()
    val monitorDevices: MutableLiveData<List<MonitoringMonitorDevice>?>
        get() = _monitorDevices

    private val valueEventListener = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) { }
        override fun onDataChange(snapshot: DataSnapshot) {
            val monitoringMonitorDevices = mutableListOf<MonitoringMonitorDevice>()
            if (snapshot.exists()) {
                for (dataSnapshot in snapshot.children) {
                    val monitoringMonitorDevice = dataSnapshot.getValue(MonitoringMonitorDevice::class.java)
                    monitoringMonitorDevice?.let { monitoringMonitorDevices.add(it) }
                }
                _monitorDevices.value = monitoringMonitorDevices
            } else {
                _monitorDevices.value = monitoringMonitorDevices
            }
        }
    }

    fun fetchMonitoringData(ownerId: String, fieldId: String) {
        dbMonitoring.child(ownerId).child(fieldId).child("monitorDevice").addListenerForSingleValueEvent(valueEventListener)
    }

}