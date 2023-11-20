package com.agrapana.fertigation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agrapana.fertigation.helper.OperationListener
import com.agrapana.fertigation.model.IntervalPreset
import com.agrapana.fertigation.model.ParameterPreset
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class PresetViewModel : ViewModel() {

    private val dbPresets = FirebaseDatabase.getInstance().getReference("presets")
    var operationListener: OperationListener? = null

    private val _presets = MutableLiveData<List<ParameterPreset>?>()
    val presets: MutableLiveData<List<ParameterPreset>?>
        get() = _presets

    private val _Parameter_preset = MutableLiveData<ParameterPreset>()
    val parameterPreset: LiveData<ParameterPreset>
        get() = _Parameter_preset

    private val _deletedParameterPreset = MutableLiveData<ParameterPreset>()
    val deletedParameterPreset: LiveData<ParameterPreset>
        get() = _deletedParameterPreset

    private val childEventListener = object : ChildEventListener {
        override fun onCancelled(error: DatabaseError) { }

        override fun onChildMoved(snapshot: DataSnapshot, p1: String?) { }

        override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {
            Log.d("nanang4", snapshot.toString())
            val parameterPreset = snapshot.getValue(ParameterPreset::class.java)
            parameterPreset?.id = snapshot.key.toString()
            _Parameter_preset.value = parameterPreset!!
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            val parameterPreset = snapshot.getValue(ParameterPreset::class.java)
            parameterPreset?.id = snapshot.key.toString()
            _deletedParameterPreset.value = parameterPreset!!
        }

        override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
            val parameterPreset = snapshot.getValue(ParameterPreset::class.java)
            parameterPreset?.id = snapshot.key.toString()
            _Parameter_preset.value = parameterPreset!!
        }
    }

    private val valueEventListener = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) { }

        override fun onDataChange(snapshot: DataSnapshot) {
            val parameterPresets = mutableListOf<ParameterPreset>()
            if (snapshot.exists()) {
                for (dataSnapshot in snapshot.children) {
                    val parameterPreset = dataSnapshot.getValue(ParameterPreset::class.java)
                    parameterPreset?.id = dataSnapshot.key.toString()
                    parameterPreset?.let { parameterPresets.add(it) }
                }
                _presets.value = parameterPresets
            } else {
                _presets.value = parameterPresets
            }
        }
    }

    fun getRealtimeUpdates(id: String) {
        dbPresets.child(id).child("parameter").addChildEventListener(childEventListener)
    }

    fun fetchPresets(id: String) {
        dbPresets.child(id).child("parameter").addListenerForSingleValueEvent(valueEventListener)
    }

    fun onAddPreset(clientId: String, parameterPreset: ParameterPreset){
        if(parameterPreset.presetName.isEmpty() || parameterPreset.fertigationDays.isEmpty() ||
            parameterPreset.fertigationTimes.isEmpty() || parameterPreset.irrigationDays.isEmpty() || parameterPreset.irrigationTimes.isEmpty()) {
            operationListener?.onFailure("Fill all blanks input")
        } else {
            parameterPreset.id = dbPresets.push().key.toString()
            dbPresets.child(clientId).child("parameter").child(parameterPreset.id).setValue(parameterPreset).addOnCompleteListener {
                if(it.isSuccessful) {
                    operationListener?.onSuccess()
                } else {
                    operationListener?.onFailure(it.exception.toString())
                }
            }
        }
    }

    fun onUpdateIntervalPreset(clientId: String, newPreset: IntervalPreset) {
        dbPresets.child(clientId).child("interval").setValue(newPreset).addOnCompleteListener {
            if(it.isSuccessful) {
                operationListener?.onSuccess()
            } else {
                operationListener?.onFailure(it.exception.toString())
            }
        }
    }

    fun onUpdatePreset(clientId: String, parameterPreset: ParameterPreset){
        if(parameterPreset.presetName.isEmpty() || parameterPreset.fertigationDays.isEmpty() ||
            parameterPreset.fertigationTimes.isEmpty() || parameterPreset.irrigationDays.isEmpty() || parameterPreset.irrigationTimes.isEmpty()) {
            operationListener?.onFailure("Fill all blanks input")
        } else {
            dbPresets.child(clientId).child("parameter").child(parameterPreset.id).setValue(parameterPreset).addOnCompleteListener {
                if(it.isSuccessful) {
                    operationListener?.onSuccess()
                } else {
                    operationListener?.onFailure(it.exception.toString())
                }
            }
        }
    }

    fun onDeletePreset(clientId: String, parameterPreset: ParameterPreset) {
        val operation = dbPresets.child(clientId).orderByChild("id").equalTo(parameterPreset.id)
        operation.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (worker in snapshot.children) {
                    worker.ref.removeValue()
                    val storageReference = FirebaseStorage.getInstance()
                        .getReferenceFromUrl(parameterPreset.imageUrl)
                    storageReference.delete().addOnSuccessListener {
                        operationListener?.onSuccess()
                    }.addOnFailureListener {
                        operationListener?.onFailure(it.message.toString())
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                operationListener?.onFailure(error.message)
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        dbPresets.removeEventListener(childEventListener)
    }

}