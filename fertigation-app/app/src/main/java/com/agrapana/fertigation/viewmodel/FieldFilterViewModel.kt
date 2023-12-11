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

class FieldFilterViewModel: ViewModel() {

    private val dbFields = FirebaseDatabase.getInstance().getReference("fields")
    var operationListener: OperationListener? = null

    private val _fields = MutableLiveData<List<Field>?>()
    val fields: MutableLiveData<List<Field>?>
        get() = _fields

    private val _field = MutableLiveData<Field>()
    val field: LiveData<Field>
        get() = _field

    private val childEventListener = object : ChildEventListener {
        override fun onCancelled(error: DatabaseError) { }

        override fun onChildMoved(snapshot: DataSnapshot, p1: String?) { }

        override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {
            val field = snapshot.getValue(Field::class.java)
            field?.hardwareCode = snapshot.key.toString()
            _field.value = field!!
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {}

        override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
            val field = snapshot.getValue(Field::class.java)
            field?.hardwareCode = snapshot.key.toString()
            _field.value = field!!
        }
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

    override fun onCleared() {
        super.onCleared()
        dbFields.removeEventListener(childEventListener)
    }
}