package yawza.zawya.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import yawza.zawya.MainActivity
import yawza.zawya.R
import yawza.zawya.databinding.ActivityAuthBinding
import yawza.zawya.manager.AuthManager
import yawza.zawya.manager.AuthResult

class AuthActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAuthBinding
    private lateinit var authManager: AuthManager
    
    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        lifecycleScope.launch {
            val authResult = authManager.handleSignInResult(result.data)
            handleAuthResult(authResult)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        authManager = AuthManager(this)
        setupUI()
        observeAuthState()
    }
    
    private fun setupUI() {
        binding.btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }
    }
    
    private fun observeAuthState() {
        lifecycleScope.launch {
            authManager.authState.collect { authState ->
                when (authState) {
                    is yawza.zawya.manager.AuthState.Authenticated -> {
                        navigateToMain()
                    }
                    is yawza.zawya.manager.AuthState.Unauthenticated -> {
                        // User is not authenticated, show sign-in UI
                    }
                    is yawza.zawya.manager.AuthState.Loading -> {
                        // Show loading state if needed
                    }
                }
            }
        }
    }
    
    private fun signInWithGoogle() {
        val signInIntent = authManager.getSignInIntent()
        signInLauncher.launch(signInIntent)
    }
    
    private fun handleAuthResult(authResult: AuthResult) {
        when (authResult) {
            is AuthResult.Success -> {
                android.util.Log.d("AuthActivity", "Sign in successful: ${authResult.user.email}")
                Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show()
                navigateToMain()
            }
            is AuthResult.Error -> {
                android.util.Log.e("AuthActivity", "Sign in failed: ${authResult.message}")
                Toast.makeText(this, "Sign in failed: ${authResult.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
