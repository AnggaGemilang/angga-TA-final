package com.agrapana.fertigation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agrapana.fertigation.helper.OperationListener
import com.agrapana.fertigation.model.Field
import com.agrapana.fertigation.model.ParameterPreset
import com.agrapana.fertigation.model.ParameterPresetNow
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FieldViewModel: ViewModel() {

    private val dbFields = FirebaseDatabase.getInstance().getReference("fields")
    private val dbPresets = FirebaseDatabase.getInstance().getReference("presets")
    private val dbControlling = FirebaseDatabase.getInstance().getReference("controlling")
    var operationListener: OperationListener? = null

    private val _fields = MutableLiveData<List<Field>?>()
    val fields: MutableLiveData<List<Field>?>
        get() = _fields

    private val _field = MutableLiveData<Field>()
    val field: LiveData<Field>
        get() = _field

    private val _deletedField = MutableLiveData<Field>()
    val deletedField: LiveData<Field>
        get() = _deletedField

    private val childEventListener = object : ChildEventListener {
        override fun onCancelled(error: DatabaseError) { }

        override fun onChildMoved(snapshot: DataSnapshot, p1: String?) { }

        override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {
            val field = snapshot.getValue(Field::class.java)
            field?.hardwareCode = snapshot.key.toString()
            _field.value = field!!
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            val field = snapshot.getValue(Field::class.java)
            field?.hardwareCode = snapshot.key.toString()
            _deletedField.value = field!!
        }

        override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
            val field = snapshot.getValue(Field::class.java)
            field?.hardwareCode = snapshot.key.toString()
            _field.value = field!!
        }
    }

    private val valueEventListener = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) { }
        override fun onDataChange(snapshot: DataSnapshot) {
            val fields = mutableListOf<Field>()
            if (snapshot.exists()) {
                for (dataSnapshot in snapshot.children) {
                    val field = dataSnapshot.getValue(Field::class.java)
                    field?.hardwareCode = dataSnapshot.key.toString()
                    field?.let { fields.add(it) }
                }
                _fields.value = fields
            } else {
                _fields.value = fields
            }
        }
    }

    fun getRealtimeUpdates(id: String) {
        dbFields.child(id).addChildEventListener(childEventListener)
    }

    fun fetchFields(id: String) {
        dbFields.child(id).addListenerForSingleValueEvent(valueEventListener)
    }

    fun getRealtimeUpdatesByWorker(id: String, fieldId: String) {
        val value3EventListener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) { }
            override fun onDataChange(snapshot: DataSnapshot) {
                val field = snapshot.getValue(Field::class.java)
                field?.hardwareCode = snapshot.key.toString()
                _field.value = field!!
            }
        }
        dbFields.child(id).child(fieldId).addValueEventListener(value3EventListener)
    }

    fun fetchFieldsByWorker(ownerId: String, fieldId: String) {
        val value2EventListener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) { }
            override fun onDataChange(snapshot: DataSnapshot) {
                val fields = mutableListOf<Field>()
                if(snapshot.exists()){
                    val field = snapshot.getValue(Field::class.java)
                    field?.let { fields.add(it) }
                }
                _fields.value = fields
            }
        }
        dbFields.child(ownerId).child(fieldId).addListenerForSingleValueEvent(value2EventListener)
    }

    fun onAddField(clientId: String, field: Field){
        if(field.name.isEmpty() || field.landArea.isEmpty() || field.address.isEmpty()
            || field.hardwareCode.isEmpty() || field.numberOfMonitorDevice == 0) {
            operationListener?.onFailure("Fill all blanks input")
        } else {
            dbFields.child(clientId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for(snapshot in dataSnapshot.children){
                        if (snapshot.child("hardware_code").value.toString() == field.hardwareCode) {
                            operationListener?.onFailure("Hardware Has Been Linked To Another Account")
                            return
                        }
                    }
                    dbFields.child(clientId).child(field.hardwareCode).setValue(field).addOnCompleteListener {
                        if(it.isSuccessful) {
                            dbPresets.child(clientId).child("parameter").orderByChild("id").equalTo(field.presetId).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    for(snapshot in dataSnapshot.children){
                                        val presetNow = ParameterPresetNow()
                                        presetNow.idealMoisture = snapshot.child("idealMoisture").value.toString()
                                        presetNow.fertigationDays = snapshot.child("fertigationDays").value.toString()
                                        presetNow.fertigationTimes = snapshot.child("fertigationTimes").value.toString()
                                        presetNow.irrigationDays = snapshot.child("irrigationDays").value.toString()
                                        presetNow.irrigationTimes = snapshot.child("fertigationTimes").value.toString()
                                        presetNow.fertigationDose = snapshot.child("fertigationDose").value.toString()
                                        presetNow.irrigationDose = snapshot.child("irrigationDose").value.toString()
                                        dbControlling.child(clientId).child("parameter").child(field.hardwareCode).setValue(presetNow).addOnCompleteListener { saved ->
                                            if (saved.isSuccessful) {
                                                operationListener?.onSuccess()
                                            } else {
                                                operationListener?.onFailure(it.exception.toString())
                                            }
                                        }
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {}
                            })
                        } else {
                            operationListener?.onFailure(it.exception.toString())
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    operationListener?.onFailure(error.message)
                }
            })
        }
    }

    fun onUpdateField(clientId: String, field: Field){
        if(field.name.isEmpty() || field.landArea.isEmpty() || field.address.isEmpty()
            || field.hardwareCode.isEmpty() || field.numberOfMonitorDevice == 0) {
            operationListener?.onFailure("Fill all blanks input")
        } else {
            dbFields.child(clientId).child(field.hardwareCode).setValue(field).addOnCompleteListener {
                if(it.isSuccessful) {
                    dbPresets.child(clientId).child("parameter").orderByChild("id").equalTo(field.presetId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for(snapshot in dataSnapshot.children){
                                val presetNow = ParameterPresetNow()
                                presetNow.idealMoisture = snapshot.child("idealMoisture").value.toString()
                                presetNow.fertigationDays = snapshot.child("fertigationDays").value.toString()
                                presetNow.fertigationTimes = snapshot.child("fertigationTimes").value.toString()
                                presetNow.irrigationDays = snapshot.child("irrigationDays").value.toString()
                                presetNow.irrigationTimes = snapshot.child("fertigationTimes").value.toString()
                                presetNow.fertigationDose = snapshot.child("fertigationDose").value.toString()
                                presetNow.irrigationDose = snapshot.child("irrigationDose").value.toString()
                                dbControlling.child(clientId).child("parameter").child(field.hardwareCode).setValue(presetNow).addOnCompleteListener { saved ->
                                    if (saved.isSuccessful) {
                                        operationListener?.onSuccess()
                                    } else {
                                        operationListener?.onFailure(it.exception.toString())
                                    }
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
                } else {
                    operationListener?.onFailure(it.exception.toString())
                }
            }
        }
    }

    fun onDeleteField(clientId: String, field: Field) {
        val operation = dbFields.child(clientId).orderByChild("hardwareCode").equalTo(field.hardwareCode)
        operation.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (worker in snapshot.children) {
                    worker.ref.removeValue()
                    operationListener?.onSuccess()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                operationListener?.onFailure(error.message)
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        dbFields.removeEventListener(childEventListener)
    }
}