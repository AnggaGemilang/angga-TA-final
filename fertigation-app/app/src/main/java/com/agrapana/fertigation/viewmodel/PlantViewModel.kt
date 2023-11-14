package com.agrapana.fertigation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agrapana.fertigation.model.Plant
import com.google.firebase.database.*

class PlantViewModel : ViewModel() {

    private val dbPlants = FirebaseDatabase.getInstance().getReference("plants")

    private val _plants = MutableLiveData<List<Plant>?>()
    val plants: MutableLiveData<List<Plant>?>
        get() = _plants

    private val _plant = MutableLiveData<Plant>()
    val plant: LiveData<Plant>
        get() = _plant

    private val _result = MutableLiveData<Exception?>()

    fun getDBReference(): DatabaseReference {
        return dbPlants
    }

    fun getAllDataPlants() {
        dbPlants.addListenerForSingleValueEvent(valueEventListener)
    }

    private val childEventListener = object : ChildEventListener {
        override fun onCancelled(error: DatabaseError) { }

        override fun onChildMoved(snapshot: DataSnapshot, p1: String?) { }

        override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {
            val plant = snapshot.getValue(Plant::class.java)
            plant?.id = snapshot.key.toString()
            _plant.value = plant!!
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            val plant = snapshot.getValue(Plant::class.java)
            plant?.id = snapshot.key.toString()
            _plant.value = plant!!
        }

        override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
            val plant = snapshot.getValue(Plant::class.java)
            plant?.id = snapshot.key.toString()
            _plant.value = plant!!
        }
    }

    fun getRealtimeUpdates(type: String) {
        dbPlants.orderByChild("category").equalTo(type).addChildEventListener(childEventListener)
    }

    private val valueEventListener = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) { }

        override fun onDataChange(snapshot: DataSnapshot) {
            val plants = mutableListOf<Plant>()
            if (snapshot.exists()) {
                for (dataSnapshot in snapshot.children) {
                    val plant = dataSnapshot.getValue(Plant::class.java)
                    plant?.id = dataSnapshot.key.toString()
                    plant?.let { plants.add(it) }
                }
                _plants.value = plants
            } else {
                _plants.value = plants
            }
        }
    }

    fun fetchPlants(type: String) {
        dbPlants.orderByChild("category").equalTo(type).addListenerForSingleValueEvent(valueEventListener)
    }

    fun updatePlant(plant: Plant) {
        dbPlants.child(plant.id).setValue(plant).addOnCompleteListener {
            if(it.isSuccessful) {
                _result.value = null
                fetchPlants(plant.category)
            } else {
                _result.value = it.exception
            }
        }
    }

    fun deletePlant(plant: Plant) {
        dbPlants.child(plant.id).setValue(null).addOnCompleteListener { it ->
            if(it.isSuccessful) {

            } else {
                _result.value = it.exception
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        dbPlants.removeEventListener(childEventListener)
    }

}