package com.example.lingvofriend.pages

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

sealed class AuthState {
    object Authenticated : AuthState()

    object Unauthenticated : AuthState()

    object Loading : AuthState()

    data class Error(
        val message: String,
    ) : AuthState()
}

class AuthViewModel : ViewModel() {
    // object to interact with firebase api
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // LiveData to store AuthState, public authState is used outside of the class
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    // when first creating an object (launching an app)
    // we'll check if user is already Authenticated and let him go through
    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        if (auth.currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated
        }
    }

    fun signIn(
        email: String,
        password: String,
    ) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }

        // we can use this to provide an animation of loading going forward
        _authState.value = AuthState.Loading

        // sending an async request
        auth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                    Log.d("FirebaseAuth", "SignUp successful")
                } else {
                    val errorMessage = task.exception?.message ?: "Something went wrong"
                    _authState.value = AuthState.Error(errorMessage)
                    Log.e("FirebaseAuth", "SignUp failed: $errorMessage")
                }
            }
    }

    fun signUp(
        email: String,
        password: String,
    ) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }

        _authState.value = AuthState.Loading

        auth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                    Log.d("FirebaseAuth", "SignUp successful")
                } else {
                    val errorMessage = task.exception?.message ?: "Something went wrong"
                    _authState.value = AuthState.Error(errorMessage)
                    Log.e("FirebaseAuth", "SignUp failed: $errorMessage")
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}
