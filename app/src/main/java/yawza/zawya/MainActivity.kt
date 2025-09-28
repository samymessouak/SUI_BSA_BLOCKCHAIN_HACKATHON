package yawza.zawya

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import yawza.zawya.activity.SimpleAuthActivity
import yawza.zawya.controller.NavigationController
import yawza.zawya.databinding.ActivityMainBinding
import yawza.zawya.manager.AuthManager
import yawza.zawya.manager.AuthState

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navigationController: NavigationController
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        authManager = AuthManager(this)
        checkAuthentication()
    }

    private fun checkAuthentication() {
        lifecycleScope.launch {
            authManager.authState.collect { authState ->
                when (authState) {
                    is AuthState.Authenticated -> {
                        // User is authenticated, show main app
                        setupNavigation()
                    }
                    is AuthState.Unauthenticated -> {
                        // User is not authenticated, redirect to auth
                        redirectToAuth()
                    }
                    is AuthState.Loading -> {
                        // Show loading state if needed
                        android.util.Log.d("MainActivity", "Checking authentication...")
                    }
                }
            }
        }
    }

    private fun setupNavigation() {
        navigationController = NavigationController(
            this,
            supportFragmentManager,
            binding.bottomNavigation
        )
        navigationController.initialize()
    }

    private fun redirectToAuth() {
        val intent = Intent(this, SimpleAuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}