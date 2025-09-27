package yawza.zawya

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import yawza.zawya.controller.NavigationController
import yawza.zawya.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navigationController: NavigationController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    private fun setupNavigation() {
        navigationController = NavigationController(
            this,
            supportFragmentManager,
            binding.bottomNavigation
        )
        navigationController.initialize()
    }
}