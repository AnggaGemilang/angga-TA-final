package com.agrapana.fertigation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agrapana.fertigation.model.Preset
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class PresetViewModel : ViewModel() {

    private val dbPresets = FirebaseDatabase.getInstance().getReference("presets")

    private val _presets = MutableLiveData<List<Preset>?>()
    val presets: MutableLiveData<List<Preset>?>
        get() = _presets

    private val _preset = MutableLiveData<Preset>()
    val preset: LiveData<Preset>
        get() = _preset

    private val _result = MutableLiveData<Exception?>()

    fun getDBReference(): DatabaseReference {
        return dbPresets
    }

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
            _preset.value = preset!!
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

    fun getRealtimeUpdates(type: String) {
        dbPresets.orderByChild("category").equalTo(type).addChildEventListener(childEventListener)
    }

    fun fetchOnePreset(name: String) {
        dbPresets.orderByChild("plantName").equalTo(name).addListenerForSingleValueEvent(valueEventListener)
    }

    fun getAllDataPreset() {
        dbPresets.addListenerForSingleValueEvent(valueEventListener)
    }

    fun fetchPresets(type: String) {
        dbPresets.orderByChild("category").equalTo(type).addListenerForSingleValueEvent(valueEventListener)
    }

    fun deletePreset(preset: Preset) {
        dbPresets.child(preset.id).setValue(null).addOnCompleteListener { it ->
            if(it.isSuccessful) {
                val storageReference = FirebaseStorage.getInstance()
                    .getReferenceFromUrl(preset.imageUrl)
                storageReference.delete().addOnSuccessListener {
                    _result.value = null
                }.addOnFailureListener {
                    _result.value = it
                }
            } else {
                _result.value = it.exception
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        dbPresets.removeEventListener(childEventListener)
    }

}