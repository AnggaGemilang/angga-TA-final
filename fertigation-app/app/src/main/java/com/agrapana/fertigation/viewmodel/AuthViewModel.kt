package com.agrapana.fertigation.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agrapana.fertigation.helper.AuthListener
import com.agrapana.fertigation.model.AuthResponse
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
    private val authResponse = MutableLiveData<AuthResponse?>()
    var authListener: AuthListener? = null

    fun onLogin(email: String, password: String){
        if(email.isEmpty() || password.isEmpty()){
            authListener?.onFailure("Email or Password Is Empty!")
        } else {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful){
                    dbUsers.child(firebaseAuth.currentUser!!.uid).addListenerForSingleValueEvent(
                        object: ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                authResponse.value = AuthResponse(
                                    snapshot.key,
                                    snapshot.child("name").value.toString(),
                                    snapshot.child("role").value.toString()
                                )
                                authListener?.onSuccess(authResponse)
                            }
                            override fun onCancelled(error: DatabaseError) {
                            }
                        })
                } else {
                    authListener?.onFailure("Account Not Found")
                    Log.d("hasil", it.exception.toString())
                }
            }
        }
    }

    fun onRegister(name: String, email: String, password: String, confirmPassword: String){
        if(email.isEmpty() || password.isEmpty()){
            authListener?.onFailure("Email or Password Is Empty!")
        } else if(password.length < 3 && !password.contains("[a-z]".toRegex()) && !password.contains("[0-9]".toRegex())
                && !password.contains("[!\"#\$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}~]".toRegex())) {
            authListener?.onFailure(password.length.toString() + "- Password Didn't Match Format")
        } else if(password != confirmPassword) {
            authListener?.onFailure("Confirm Password Didn't Match")
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val user = User(name, email, "Owner")
                    dbUsers.child(firebaseAuth.currentUser!!.uid).setValue(user).addOnCompleteListener {
                        if(it.isSuccessful) {
                            authResponse.value = AuthResponse(firebaseAuth.currentUser!!.uid, name, "Owner")
                            authListener?.onSuccess(authResponse)
                        } else {
                            authListener?.onFailure(it.exception.toString())
                            Log.d("Error", it.exception.toString())
                        }
                    }
                } else {
                    authListener?.onFailure(task.exception.toString())
                    Log.d("Error", task.exception.toString())
                }
            }
        }
    }

    fun logout() = firebaseAuth.signOut()

    fun currentUser() = firebaseAuth.currentUser

}
