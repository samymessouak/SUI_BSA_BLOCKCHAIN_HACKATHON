package yawza.zawya.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import yawza.zawya.MainActivity
import yawza.zawya.R
import yawza.zawya.databinding.ActivitySimpleAuthBinding
import yawza.zawya.manager.AuthManager
import yawza.zawya.manager.AuthResult

class SimpleAuthActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySimpleAuthBinding
    private lateinit var authManager: AuthManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySimpleAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        authManager = AuthManager(this)
        setupUI()
    }
    
    private fun setupUI() {
        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            signInWithEmail(email, password)
        }
        
        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            createAccountWithEmail(email, password)
        }
    }
    
    private fun signInWithEmail(email: String, password: String) {
        lifecycleScope.launch {
            val result = authManager.signInWithEmail(email, password)
            handleAuthResult(result)
        }
    }
    
    private fun createAccountWithEmail(email: String, password: String) {
        lifecycleScope.launch {
            val result = authManager.createUserWithEmail(email, password)
            handleAuthResult(result)
        }
    }
    
    private fun handleAuthResult(authResult: AuthResult) {
        when (authResult) {
            is AuthResult.Success -> {
                Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show()
                navigateToMain()
            }
            is AuthResult.Error -> {
                Toast.makeText(this, "Authentication failed: ${authResult.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun navigateToMain() {
        // Wait a moment for the auth state to propagate
        lifecycleScope.launch {
            kotlinx.coroutines.delay(500) // Small delay to ensure auth state is updated
            val intent = Intent(this@SimpleAuthActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
