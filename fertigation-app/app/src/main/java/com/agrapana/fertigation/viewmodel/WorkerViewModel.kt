package com.agrapana.fertigation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agrapana.fertigation.helper.WorkerListener
import com.agrapana.fertigation.model.AuthResponse
import com.agrapana.fertigation.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue


class WorkerViewModel : ViewModel() {

    private val dbWorkers = FirebaseDatabase.getInstance().getReference("workers")
    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    var workerListener: WorkerListener? = null

    private val _workers = MutableLiveData<List<User>?>()

    val workers: MutableLiveData<List<User>?>
        get() = _workers

    private val _worker = MutableLiveData<User>()
    val worker: LiveData<User>
        get() = _worker

    private val _deletedworker = MutableLiveData<User>()
    val deletedworker: LiveData<User>
        get() = _deletedworker

    private val childEventListener = object : ChildEventListener {
        override fun onCancelled(error: DatabaseError) { }

        override fun onChildMoved(snapshot: DataSnapshot, p1: String?) { }

        override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {
            Log.d("warko 1", "sini")
            val worker = snapshot.getValue(User::class.java)
            _worker.value = worker!!
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            Log.d("warko 2", snapshot.toString())
            val worker = snapshot.getValue(User::class.java)
            _deletedworker.value = worker!!
        }

        override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
            Log.d("warko 3", "sini")
            val worker = snapshot.getValue(User::class.java)
            _worker.value = worker!!
        }
    }

    private val valueEventListener = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) { }

        override fun onDataChange(snapshot: DataSnapshot) {
            val workers = mutableListOf<User>()
            if (snapshot.exists()) {

                Log.d("sabihis 1", snapshot.value.toString())

                for (dataSnapshot in snapshot.children) {
                    val worker = dataSnapshot.getValue(User::class.java)
                    worker?.let { workers.add(it) }
                }
                _workers.value = workers
            } else {
                Log.d("sabihis 2", "data ke sini")

                _workers.value = workers
            }
        }
    }

    fun getRealtimeUpdates(id: String) {
        dbWorkers.child(id).addChildEventListener(childEventListener)
    }

    fun onUpdateWorker(userId: String, name: String, email: String, password: String){
        if(password.isNotEmpty()){
            if(password.length < 3 || !password.contains("[a-z]".toRegex()) || !password.contains("[0-9]".toRegex())
                || !password.contains("[!\"#\$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}~]".toRegex())) {
                workerListener?.onFailure("Password Didn't Match Format")
            } else {
                val getUser = dbWorkers.child(userId).orderByChild("email").equalTo(email)
                getUser.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (worker in snapshot.children) {
                            val workerId = worker.ref.key
                            val worker = worker.getValue(User::class.java)
                            val newUser = User(name, email, password, "Worker")
                            dbWorkers.child(userId).child(workerId!!).setValue(newUser).addOnCompleteListener {
                                val user = firebaseAuth.currentUser!!
                                val credential = EmailAuthProvider
                                    .getCredential(worker!!.email!!, worker.password!!)
                                user.reauthenticate(credential)
                                    .addOnCompleteListener {
                                        val user = firebaseAuth.currentUser!!
                                        user.updatePassword(password)
                                        workerListener?.onSuccess()
                                    }
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        workerListener?.onFailure(error.message)
                    }
                })
            }
        } else {
            val getUser = dbWorkers.child(userId).orderByChild("email").equalTo(email)
            getUser.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (worker in snapshot.children) {
                        val workerId = worker.ref.key
                        val worker = worker.getValue(User::class.java)
                        val newUser = User(name, email, worker!!.password, "Worker")
                        dbWorkers.child(userId).child(workerId!!).setValue(newUser).addOnCompleteListener {
                            workerListener?.onSuccess()
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    workerListener?.onFailure(error.message)
                }
            })
        }
    }

    fun onAddWorker(userId: String, name: String, email: String, password: String){
        if(email.isEmpty() || password.isEmpty()){
            workerListener?.onFailure("Email or Password Is Empty!")
        } else if(password.length < 3 || !password.contains("[a-z]".toRegex()) || !password.contains("[0-9]".toRegex())
            || !password.contains("[!\"#\$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}~]".toRegex())) {
            workerListener?.onFailure("Password Didn't Match Format")
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val user = User(name, email, password, "Worker")
                    dbWorkers.child(userId).push().setValue(user).addOnCompleteListener {
                        if(it.isSuccessful) {
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

    fun fetchWorkers(id: String) {
        dbWorkers.child(id).addListenerForSingleValueEvent(valueEventListener)
    }

    fun deleteWorker(clientId: String, worker: User) {
        val operation = dbWorkers.child(clientId).orderByChild("email").equalTo(worker.email)
        operation.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (worker in snapshot.children) {
                    worker.ref.removeValue()
                    val userNow = worker.getValue(User::class.java)
                    val user = firebaseAuth.currentUser!!
                    val credential = EmailAuthProvider
                        .getCredential(userNow!!.email!!, userNow.password!!)
                    user.reauthenticate(credential)
                        .addOnCompleteListener {
                            val user = firebaseAuth.currentUser!!
                            user.delete()
                            workerListener?.onSuccess()
                        }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                workerListener?.onFailure(error.message)
            }

        })
    }

    override fun onCleared() {
        super.onCleared()
        dbWorkers.removeEventListener(childEventListener)
    }

}