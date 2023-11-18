package com.agrapana.fertigation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agrapana.fertigation.helper.OperationListener
import com.agrapana.fertigation.model.Preset
import com.agrapana.fertigation.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class PresetViewModel : ViewModel() {

    private val dbPresets = FirebaseDatabase.getInstance().getReference("presets")
    var operationListener: OperationListener? = null

    private val _presets = MutableLiveData<List<Preset>?>()
    val presets: MutableLiveData<List<Preset>?>
        get() = _presets

    private val _preset = MutableLiveData<Preset>()
    val preset: LiveData<Preset>
        get() = _preset

    private val _deletedPreset = MutableLiveData<Preset>()
    val deletedPreset: LiveData<Preset>
        get() = _deletedPreset

    private val childEventListener = object : ChildEventListener {
        override fun onCancelled(error: DatabaseError) { }

        override fun onChildMoved(snapshot: DataSnapshot, p1: String?) { }

        override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {
            val preset = snapshot.getValue(Preset::class.java)
            preset?.id = snapshot.key.toString()
            _preset.value = preset!!
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            val preset = snapshot.getValue(Preset::class.java)
            preset?.id = snapshot.key.toString()
            _deletedPreset.value = preset!!
        }

        override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
            val preset = snapshot.getValue(Preset::class.java)
            preset?.id = snapshot.key.toString()
            _preset.value = preset!!
        }
    }

    private val valueEventListener = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) { }

        override fun onDataChange(snapshot: DataSnapshot) {
            val presets = mutableListOf<Preset>()
            if (snapshot.exists()) {
                for (dataSnapshot in snapshot.children) {
                    val preset = dataSnapshot.getValue(Preset::class.java)
                    preset?.id = dataSnapshot.key.toString()
                    preset?.let { presets.add(it) }
                }
                _presets.value = presets
            } else {
                _presets.value = presets
            }
        }
    }

    fun getRealtimeUpdates(id: String) {
        dbPresets.child(id).addChildEventListener(childEventListener)
    }

    fun fetchPresets(id: String) {
        dbPresets.child(id).addListenerForSingleValueEvent(valueEventListener)
    }

    fun onAddPreset(clientId: String, preset: Preset){
        if(preset.presetName.isEmpty() || preset.fertigationDays.isEmpty() ||
            preset.fertigationTimes.isEmpty() || preset.irrigationDays.isEmpty() || preset.irrigationTimes.isEmpty()) {
            operationListener?.onFailure("Fill all blanks input")
        } else {
            preset.id = dbPresets.push().key.toString()
            dbPresets.child(clientId).child(preset.id).setValue(preset).addOnCompleteListener {
                if(it.isSuccessful) {
                    operationListener?.onSuccess()
                } else {
                    operationListener?.onFailure(it.exception.toString())
                }
            }
        }
    }

    fun onUpdatePreset(clientId: String, preset: Preset){
        if(preset.presetName.isEmpty() || preset.fertigationDays.isEmpty() ||
            preset.fertigationTimes.isEmpty() || preset.irrigationDays.isEmpty() || preset.irrigationTimes.isEmpty()) {
            operationListener?.onFailure("Fill all blanks input")
        } else {
            dbPresets.child(clientId).child(preset.id).setValue(preset).addOnCompleteListener {
                if(it.isSuccessful) {
                    operationListener?.onSuccess()
                } else {
                    operationListener?.onFailure(it.exception.toString())
                }
            }
        }
    }

    fun onDeletePreset(clientId: String, preset: Preset) {
        val operation = dbPresets.child(clientId).orderByChild("id").equalTo(preset.id)
        operation.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (worker in snapshot.children) {
                    worker.ref.removeValue()
                    val storageReference = FirebaseStorage.getInstance()
                        .getReferenceFromUrl(preset.imageUrl)
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