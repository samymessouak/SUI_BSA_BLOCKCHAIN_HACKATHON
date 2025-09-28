package yawza.zawya.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import yawza.zawya.manager.AuthManager
import yawza.zawya.manager.AuthState

class ProfileViewModel : ViewModel() {
    
    private lateinit var authManager: AuthManager
    
    private val _userProfile = MutableLiveData<FirebaseUser?>()
    val userProfile: LiveData<FirebaseUser?> = _userProfile
    
    private val _isSignedIn = MutableLiveData<Boolean>()
    val isSignedIn: LiveData<Boolean> = _isSignedIn
    
    fun initializeAuthManager(context: android.content.Context) {
        authManager = AuthManager(context)
        observeAuthState()
    }
    
    private fun observeAuthState() {
        viewModelScope.launch {
            authManager.authState.collect { authState ->
                when (authState) {
                    is AuthState.Authenticated -> {
                        _userProfile.value = authState.user
                        _isSignedIn.value = true
                    }
                    is AuthState.Unauthenticated -> {
                        _userProfile.value = null
                        _isSignedIn.value = false
                    }
                    is AuthState.Loading -> {
                        // Handle loading state if needed
                    }
                }
            }
        }
    }
    
    fun signOut() {
        authManager.signOut()
    }
    
    fun getCurrentUser(): FirebaseUser? {
        return authManager.getCurrentUser()
    }
}