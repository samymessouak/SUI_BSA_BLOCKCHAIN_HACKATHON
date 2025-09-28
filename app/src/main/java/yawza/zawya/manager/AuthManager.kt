package yawza.zawya.manager

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.EmailAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class AuthManager(private val context: Context) {
    
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    init {
        setupGoogleSignIn()
        checkCurrentUser()
        setupAuthStateListener()
    }
    
    private fun setupAuthStateListener() {
        firebaseAuth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            android.util.Log.d("AuthManager", "Auth state changed: ${user?.email}")
            _authState.value = if (user != null) {
                AuthState.Authenticated(user)
            } else {
                AuthState.Unauthenticated
            }
        }
    }
    
    private fun setupGoogleSignIn() {
        val webClientId = context.getString(context.resources.getIdentifier("default_web_client_id", "string", context.packageName))
        android.util.Log.d("AuthManager", "Using Web Client ID: $webClientId")
        
        // Try with the Web Client ID from google-services.json
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .requestProfile()
            .build()
        
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }
    
    private fun checkCurrentUser() {
        val currentUser = firebaseAuth.currentUser
        android.util.Log.d("AuthManager", "Checking current user: ${currentUser?.email}")
        _authState.value = if (currentUser != null) {
            AuthState.Authenticated(currentUser)
        } else {
            AuthState.Unauthenticated
        }
    }
    
    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }
    
    suspend fun handleSignInResult(data: Intent?): AuthResult {
        return try {
            android.util.Log.d("AuthManager", "Handling sign in result")
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            android.util.Log.d("AuthManager", "Google account obtained: ${account.email}")
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            android.util.Log.e("AuthManager", "Google Sign-In failed: ${e.message}", e)
            AuthResult.Error(e.message ?: "Sign in failed")
        }
    }
    
    private suspend fun firebaseAuthWithGoogle(account: GoogleSignInAccount): AuthResult {
        return try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user
            
            if (user != null) {
                _authState.value = AuthState.Authenticated(user)
                AuthResult.Success(user)
            } else {
                AuthResult.Error("Authentication failed")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Authentication failed")
        }
    }
    
    fun signOut() {
        firebaseAuth.signOut()
        googleSignInClient.signOut()
        _authState.value = AuthState.Unauthenticated
    }
    
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
    
    // Simple email/password authentication as fallback
    suspend fun signInWithEmail(email: String, password: String): AuthResult {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                _authState.value = AuthState.Authenticated(user)
                AuthResult.Success(user)
            } else {
                AuthResult.Error("Authentication failed")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Sign in failed")
        }
    }
    
    suspend fun createUserWithEmail(email: String, password: String): AuthResult {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                _authState.value = AuthState.Authenticated(user)
                AuthResult.Success(user)
            } else {
                AuthResult.Error("Account creation failed")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Account creation failed")
        }
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()
}

sealed class AuthResult {
    data class Success(val user: FirebaseUser) : AuthResult()
    data class Error(val message: String) : AuthResult()
}
