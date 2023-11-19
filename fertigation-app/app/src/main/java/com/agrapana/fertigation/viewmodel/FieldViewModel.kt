package com.agrapana.fertigation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agrapana.fertigation.helper.OperationListener
import com.agrapana.fertigation.model.Field
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FieldViewModel: ViewModel() {

    private val dbFields = FirebaseDatabase.getInstance().getReference("fields")
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
            field?.id = snapshot.key.toString()
            _field.value = field!!
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            val field = snapshot.getValue(Field::class.java)
            field?.id = snapshot.key.toString()
            _deletedField.value = field!!
        }

        override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
            val field = snapshot.getValue(Field::class.java)
            field?.id = snapshot.key.toString()
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
                    field?.id = dataSnapshot.key.toString()
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

    fun fetchPresets(id: String) {
        dbFields.child(id).addListenerForSingleValueEvent(valueEventListener)
    }

    fun onAddPreset(clientId: String, field: Field){
        if(field.name.isEmpty() || field.land_area.isEmpty() || field.address.isEmpty()
            || field.hardware_code.isEmpty() || field.number_of_monitor_device == 0) {
            operationListener?.onFailure("Fill all blanks input")
        } else {
            dbFields.child(clientId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for(snapshot in dataSnapshot.children){
                        if (snapshot.child("hardware_code").value.toString() == field.hardware_code) {
                            operationListener?.onFailure("Hardware Has Been Linked To Another Account")
                            return
                        }
                    }
                    field.id = dbFields.push().key.toString()
                    dbFields.child(clientId).child(field.id).setValue(field).addOnCompleteListener {
                        if(it.isSuccessful) {
                            operationListener?.onSuccess()
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

    fun onUpdatePreset(clientId: String, field: Field){
        if(field.name.isEmpty() || field.land_area.isEmpty() || field.address.isEmpty()
            || field.hardware_code.isEmpty() || field.number_of_monitor_device == 0) {
            operationListener?.onFailure("Fill all blanks input")
        } else {
            dbFields.child(clientId).child(field.id).setValue(field).addOnCompleteListener {
                if(it.isSuccessful) {
                    operationListener?.onSuccess()
                } else {
                    operationListener?.onFailure(it.exception.toString())
                }
            }
        }
    }

    fun onDeleteField(clientId: String, field: Field) {
        val operation = dbFields.child(clientId).orderByChild("id").equalTo(field.id)
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