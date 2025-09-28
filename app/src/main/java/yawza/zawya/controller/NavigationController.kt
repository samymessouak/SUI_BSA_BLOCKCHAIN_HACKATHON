package yawza.zawya.controller

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import yawza.zawya.R
import yawza.zawya.fragment.CollectionFragment
import yawza.zawya.fragment.MapFragment
import yawza.zawya.fragment.TransactionsFragment

class NavigationController(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val bottomNavigation: BottomNavigationView
) {
    
    private var currentFragment: Fragment? = null
    private lateinit var mapFragment: MapFragment
    private lateinit var collectionFragment: CollectionFragment
    private lateinit var transactionsFragment: TransactionsFragment
    
    fun initialize() {
        setupFragments()
        setupBottomNavigation()
        showMapFragment() // Default to map
    }
    
    private fun setupFragments() {
        mapFragment = MapFragment()
        collectionFragment = CollectionFragment()
        transactionsFragment = TransactionsFragment()
    }
    
    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_map -> {
                    showMapFragment()
                    true
                }
                R.id.nav_collection -> {
                    showCollectionFragment()
                    true
                }
                R.id.nav_transactions -> {
                    showTransactionsFragment()
                    true
                }
                else -> false
            }
        }
    }
    
    private fun showMapFragment() {
        if (currentFragment != mapFragment) {
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mapFragment)
                .commit()
            currentFragment = mapFragment
        }
    }
    
    private fun showCollectionFragment() {
        if (currentFragment != collectionFragment) {
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, collectionFragment)
                .commit()
            currentFragment = collectionFragment
        }
    }
    
    private fun showTransactionsFragment() {
        if (currentFragment != transactionsFragment) {
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, transactionsFragment)
                .commit()
            currentFragment = transactionsFragment
        }
    }
    
    fun updateCollection(brandName: String, newCount: Int) {
        collectionFragment.updateCollection(brandName, newCount)
    }
}
