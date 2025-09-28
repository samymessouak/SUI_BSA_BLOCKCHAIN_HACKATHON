package yawza.zawya.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import yawza.zawya.controller.LocationController
import yawza.zawya.controller.MapController
import yawza.zawya.controller.MapUIController
import yawza.zawya.controller.ZoneController
import yawza.zawya.databinding.FragmentMapBinding
import yawza.zawya.manager.LocationManager
import yawza.zawya.manager.MapManager
import yawza.zawya.manager.NavigationManager
import yawza.zawya.repository.ZoneRepository
import yawza.zawya.viewmodel.MapViewModel
import yawza.zawya.viewmodel.ProfileViewModel

class MapFragment : Fragment() {
    
    private lateinit var binding: FragmentMapBinding
    private lateinit var viewModel: MapViewModel
    private lateinit var profileViewModel: ProfileViewModel
    
    // Controllers
    private lateinit var uiController: MapUIController
    private lateinit var mapController: MapController
    private lateinit var zoneController: ZoneController
    private lateinit var locationController: LocationController
    
    // Managers
    private lateinit var mapManager: MapManager
    private lateinit var locationManager: LocationManager
    private lateinit var navigationManager: NavigationManager
    private lateinit var zoneRepository: ZoneRepository
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeComponents()
        setupUI()
        setupMap()
        requestLocationPermissions()
    }
    
    private fun initializeComponents() {
        // Initialize ViewModels
        viewModel = ViewModelProvider(this)[MapViewModel::class.java]
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        profileViewModel.initializeAuthManager(requireContext())
        
        // Initialize managers
        mapManager = MapManager(requireContext())
        locationManager = LocationManager(requireContext())
        navigationManager = NavigationManager(requireContext())
        zoneRepository = ZoneRepository()
        
        // Initialize controllers
        uiController = MapUIController(requireContext(), binding, viewModel)
        locationController = LocationController(requireActivity(), viewModel, mapManager, locationManager, zoneRepository)
        mapController = MapController(requireContext(), viewModel, mapManager, zoneRepository) { latLng ->
            handleMapClick(latLng)
        }
        zoneController = ZoneController(requireContext(), viewModel, mapManager, locationManager, navigationManager, zoneRepository, profileViewModel)
        
        // Observe ViewModel changes
        observeViewModel()
    }
    
    private fun observeViewModel() {
        viewModel.userLocation.observe(viewLifecycleOwner) { location ->
            location?.let {
                mapManager.updateUserLocationMarker(it)
                mapManager.updateZoneOverlays(zoneRepository.getLausanneZones(), it)
                updateLegend()
            }
        }
        
        // Removed progress tracking from map screen
    }
    
    private fun setupUI() {
        // Brand navigation buttons
        uiController.setupBrandButtons(
            onMcDonaldsClick = { zoneController.navigateToBrandZones("McDonald's") },
            onNikeClick = { zoneController.navigateToBrandZones("Nike") },
            onSephoraClick = { zoneController.navigateToBrandZones("Sephora") }
        )
        
        // Zoom controls
        uiController.setupZoomControls(
            onZoomInClick = { mapController.handleZoomIn() },
            onZoomOutClick = { mapController.handleZoomOut() }
        )
        
        // Rotation controls
        uiController.setupRotationControls(
            onRotateLeftClick = { mapController.handleRotateLeft() },
            onRotateRightClick = { mapController.handleRotateRight() }
        )
    }
    
    private fun setupMap() {
        mapController.setupMap(childFragmentManager)
        // Get current location immediately to avoid 0,0 position
        locationController.getCurrentLocation()
    }
    
    private fun handleMapClick(latLng: com.google.android.gms.maps.model.LatLng) {
        val clickedZone = zoneController.handleMapClick(latLng)
        clickedZone?.let { zone ->
            zoneController.showZoneInfoDialog(zone)
        }
    }
    
    private fun updateLegend() {
        // Show static legend with total stickers available
        uiController.updateLegend(emptyMap())
    }
    
    private fun requestLocationPermissions() {
        locationController.requestLocationPermissions()
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        zoneController.handleQRScanResult(requestCode, resultCode, data)
    }
}
