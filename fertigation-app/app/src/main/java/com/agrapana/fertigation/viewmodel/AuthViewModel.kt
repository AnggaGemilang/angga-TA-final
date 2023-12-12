package com.agrapana.fertigation.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agrapana.fertigation.helper.AuthListener
import com.agrapana.fertigation.helper.OperationListener
import com.agrapana.fertigation.model.AuthResponse
import com.agrapana.fertigation.model.IntervalPreset
import com.agrapana.fertigation.model.ParameterPresetNow
import com.agrapana.fertigation.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AuthViewModel : ViewModel() {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val dbUsers = FirebaseDatabase.getInstance().getReference("users")
    private val dbWorkers = FirebaseDatabase.getInstance().getReference("workers")
    private val dbControlling = FirebaseDatabase.getInstance().getReference("controlling")
    private val authResponse = MutableLiveData<AuthResponse?>()
    var authListener: AuthListener? = null
    var operationListener: OperationListener? = null

    fun onLogin(email: String, password: String){
        if(email.isEmpty() || password.isEmpty()){
            authListener?.onFailure("Email or Password Is Empty!")
        } else {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful){
                    dbUsers.child(firebaseAuth.currentUser!!.uid).addListenerForSingleValueEvent(
                        object: ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(snapshot.exists()){
                                    if(snapshot.child("loggedIn").value.toString() == "false"){
                                        val userNow = User()
                                        userNow.id = snapshot.child("id").value.toString()
                                        userNow.loggedIn = true
                                        userNow.email = snapshot.child("email").value.toString()
                                        userNow.password = snapshot.child("password").value.toString()
                                        userNow.role = snapshot.child("role").value.toString()
                                        userNow.name = snapshot.child("name").value.toString()
                                        Log.d("sabihis", "nanang123")
                                        dbUsers.child(firebaseAuth.currentUser!!.uid).setValue(userNow).addOnCompleteListener { saved ->
                                            if (saved.isSuccessful) {
                                                Log.d("sabihis2", "nanang123")
                                                authResponse.value = AuthResponse(
                                                    snapshot.key,
                                                    snapshot.child("name").value.toString(),
                                                    snapshot.child("role").value.toString(),
                                                )
                                            } else {
                                                authListener?.onFailure(saved.exception!!.message.toString())
                                            }
                                        }
                                    } else {
                                        authListener?.onFailure("Account Is Already In Use On Another Device")
                                    }
                                } else {
                                    dbWorkers.addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            for(owner in snapshot.children){
                                                for(worker in owner.children){
                                                    if(firebaseAuth.currentUser!!.email == worker.child("email").value.toString()){
                                                        authResponse.value = AuthResponse(
                                                            owner.key.toString(),
                                                            worker.child("name").value.toString(),
                                                            worker.child("role").value.toString(),
                                                            worker.child("id").value.toString(),
                                                            worker.child("fieldId").value.toString(),
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        override fun onCancelled(error: DatabaseError) {}
                                    })
                                }
                                authListener?.onSuccess(authResponse)
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                } else {
                    authListener?.onFailure("Account Not Found")
                    Log.d("hasil", it.exception!!.message.toString())
                }
            }
        }
    }

    fun onRegister(name: String, email: String, password: String, confirmPassword: String){
        if(email.isEmpty() || password.isEmpty()){
            authListener?.onFailure("Email or Password Is Empty!")
        } else if(password.length < 5 || !password.contains("[a-z]".toRegex()) || !password.contains("[0-9]".toRegex())
            || !password.contains("[!\"#\$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}~]".toRegex())) {
            authListener?.onFailure("Password Didn't Match Format")
        } else if(password != confirmPassword) {
            authListener?.onFailure("Confirm Password Didn't Match")
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { saveAccount ->
                if(saveAccount.isSuccessful){
                    val user = User(firebaseAuth.currentUser!!.uid, name, email, password, "Owner", true)
                    dbUsers.child(firebaseAuth.currentUser!!.uid).setValue(user).addOnCompleteListener { saveUser ->
                        if(saveUser.isSuccessful) {
                            val intervalPreset = IntervalPreset(5)
                            dbControlling.child(firebaseAuth.currentUser!!.uid).child("interval").setValue(intervalPreset).addOnCompleteListener { savePreset ->
                                if(savePreset.isSuccessful) {
                                    authResponse.value = AuthResponse(firebaseAuth.currentUser!!.uid, name, "Owner")
                                    authListener?.onSuccess(authResponse)
                                } else {
                                    authListener?.onFailure(savePreset.exception!!.message.toString())
                                    Log.d("Error", savePreset.exception!!.message.toString())
                                }
                            }
                        } else {
                            authListener?.onFailure(saveUser.exception!!.message.toString())
                            Log.d("Error", saveUser.exception!!.message.toString())
                        }
                    }
                } else {
                    authListener?.onFailure(saveAccount.exception!!.message.toString())
                    Log.d("Error", saveAccount.exception!!.message.toString())
                }
            }
        }
    }

    fun logout(id: String){
        dbUsers.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userNow = User()
                userNow.id = snapshot.child("id").value.toString()
                userNow.loggedIn = false
                userNow.email = snapshot.child("email").value.toString()
                userNow.password = snapshot.child("password").value.toString()
                userNow.role = snapshot.child("role").value.toString()
                userNow.name = snapshot.child("name").value.toString()
                dbUsers.child(id).setValue(userNow).addOnCompleteListener { saved ->
                    if (saved.isSuccessful) {
                        firebaseAuth.signOut()
                        operationListener?.onSuccess()
                    } else {
                        operationListener?.onFailure(saved.exception!!.message.toString())
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

}
