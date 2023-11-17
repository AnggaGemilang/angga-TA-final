package com.agrapana.fertigation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agrapana.fertigation.helper.WorkerListener
import com.agrapana.fertigation.model.AuthResponse
import com.agrapana.fertigation.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class WorkerViewModel : ViewModel() {

    private val dbWorkers = FirebaseDatabase.getInstance().getReference("workers")
    private val authResponse = MutableLiveData<AuthResponse?>()
    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    var workerListener: WorkerListener? = null

    private val _workers = MutableLiveData<List<User>?>()
    val presets: MutableLiveData<List<User>?>
        get() = _workers

    private val _worker = MutableLiveData<User>()
    val preset: LiveData<User>
        get() = _worker

    private val _result = MutableLiveData<Exception?>()

    fun getDBReference(): DatabaseReference {
        return dbWorkers
    }

    private val childEventListener = object : ChildEventListener {
        override fun onCancelled(error: DatabaseError) { }

        override fun onChildMoved(snapshot: DataSnapshot, p1: String?) { }

        override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {
            val preset = snapshot.getValue(User::class.java)
//            preset?.id = snapshot.key.toString()
            _worker.value = preset!!
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            val preset = snapshot.getValue(User::class.java)
//            preset?.id = snapshot.key.toString()
            _worker.value = preset!!
        }

        override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
            val preset = snapshot.getValue(User::class.java)
//            preset?.id = snapshot.key.toString()
            _worker.value = preset!!
        }
    }

    private val valueEventListener = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) { }

        override fun onDataChange(snapshot: DataSnapshot) {
            val presets = mutableListOf<User>()
            if (snapshot.exists()) {
                for (dataSnapshot in snapshot.children) {
                    val preset = dataSnapshot.getValue(User::class.java)
//                    preset?.id = dataSnapshot.key.toString()
                    preset?.let { presets.add(it) }
                }
                _workers.value = presets
            } else {
                _workers.value = presets
            }
        }
    }

    fun getRealtimeUpdates(type: String) {
        dbWorkers.orderByChild("category").equalTo(type).addChildEventListener(childEventListener)
    }

    fun onAddWorker(userId: String, name: String, email: String, password: String){
        if(email.isEmpty() || password.isEmpty()){
            workerListener?.onFailure("Email or Password Is Empty!")
        } else if(password.length < 3 && !password.contains("[a-z]".toRegex()) && !password.contains("[0-9]".toRegex())
            && !password.contains("[!\"#\$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}~]".toRegex())) {
            workerListener?.onFailure(password.length.toString() + "- Password Didn't Match Format")
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val user = User(name, email, "Worker")
                    dbWorkers.child(userId).push().setValue(user).addOnCompleteListener {
                        if(it.isSuccessful) {
                            authResponse.value = AuthResponse(firebaseAuth.currentUser!!.uid, name, "Owner")
                            workerListener?.onSuccess()
                        } else {
                            workerListener?.onFailure(it.exception.toString())
                            Log.d("Error", it.exception.toString())
                        }
                    }
                } else {
                    workerListener?.onFailure(task.exception.toString())
                    Log.d("Error", task.exception.toString())
                }
            }
        }
    }

    fun fetchOnePreset(name: String) {
        dbWorkers.orderByChild("plantName").equalTo(name).addListenerForSingleValueEvent(valueEventListener)
    }

    fun getAllDataPreset() {
        dbWorkers.addListenerForSingleValueEvent(valueEventListener)
    }

    fun fetchPresets(type: String) {
        dbWorkers.orderByChild("category").equalTo(type).addListenerForSingleValueEvent(valueEventListener)
    }

//    fun deletePreset(preset: User) {
//        dbWorkers.child(preset.id).setValue(null).addOnCompleteListener { it ->
//            if(it.isSuccessful) {
//                val storageReference = FirebaseStorage.getInstance()
//                    .getReferenceFromUrl(preset.imageUrl)
//                storageReference.delete().addOnSuccessListener {
//                    _result.value = null
//                }.addOnFailureListener {
//                    _result.value = it
//                }
//            } else {
//                _result.value = it.exception
//            }
//        }
//    }

    override fun onCleared() {
        super.onCleared()
        dbWorkers.removeEventListener(childEventListener)
    }

}
