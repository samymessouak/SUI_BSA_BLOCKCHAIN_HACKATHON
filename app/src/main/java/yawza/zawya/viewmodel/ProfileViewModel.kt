package yawza.zawya.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import yawza.zawya.manager.AuthManager
import yawza.zawya.models.StickerToken
import yawza.zawya.service.BlockchainService

class ProfileViewModel : ViewModel() {

    companion object {
        @Volatile
        private var INSTANCE: ProfileViewModel? = null

        fun getInstance(): ProfileViewModel {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ProfileViewModel().also { INSTANCE = it }
            }
        }
    }

    private val blockchainService = BlockchainService()
    private var authManager: AuthManager? = null

    private val _ownedStickers = MutableLiveData<List<StickerToken>>()
    val ownedStickers: LiveData<List<StickerToken>> = _ownedStickers

    private val _walletAddress = MutableLiveData<String>()
    val walletAddress: LiveData<String> = _walletAddress

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _userProfile = MutableLiveData<FirebaseUser?>()
    val userProfile: LiveData<FirebaseUser?> = _userProfile

    init {
        loadUserProfile()
    }

    fun initializeAuthManager(context: Context) {
        authManager = AuthManager(context)
        // Observe auth state changes
        authManager?.authState?.let { authState ->
            // Update user profile when auth state changes
            // This would need to be done in a coroutine scope in a real implementation
        }
    }

    fun signOut() {
        authManager?.signOut()
        _userProfile.value = null
    }
    
    private fun loadUserProfile() {
        _isLoading.value = true
        
        // For now, use a mock wallet address
        // In real implementation, this would come from wallet connection
        _walletAddress.value = "0x1234567890abcdef1234567890abcdef12345678"
        
        // Load owned stickers from blockchain
        loadOwnedStickers()
    }
    
    private fun loadOwnedStickers() {
        // This will be implemented when blockchain is connected
        // For now, return empty list
        _ownedStickers.value = emptyList()
        _isLoading.value = false
    }
    
    fun collectSticker(stickerId: String, brandName: String, stickerCount: Int) {
        android.util.Log.d("ProfileViewModel", "Collecting sticker: $stickerId, brand: $brandName")
        _isLoading.value = true
        
        // Call blockchain service to mint sticker
        blockchainService.mintSticker(
            zoneId = stickerId, // Use stickerId as the identifier
            brandName = brandName,
            stickerCount = stickerCount,
            onSuccess = { stickerToken ->
                android.util.Log.d("ProfileViewModel", "Sticker minted successfully: ${stickerToken.id}")
                // Add to owned stickers
                val currentStickers = _ownedStickers.value ?: emptyList()
                _ownedStickers.value = currentStickers + stickerToken
                _isLoading.value = false
            },
            onError = { error ->
                android.util.Log.e("ProfileViewModel", "Error minting sticker: $error")
                // Handle error
                _isLoading.value = false
                // You could show a toast or error dialog here
            }
        )
    }
    
    fun getStickerCountForBrand(brandName: String): Int {
        val count = _ownedStickers.value?.count { it.brandName == brandName } ?: 0
        android.util.Log.d("ProfileViewModel", "getStickerCountForBrand($brandName): $count")
        return count
    }
    
    fun hasSticker(zoneId: String): Boolean {
        return _ownedStickers.value?.any { it.zoneId == zoneId } ?: false
    }
    
    fun getBrandProgress(brandName: String, totalStickers: Int): Pair<Int, Int> {
        val collected = getStickerCountForBrand(brandName)
        return Pair(collected, totalStickers)
    }
    
    fun getOwnedStickers(): List<StickerToken> {
        return _ownedStickers.value ?: emptyList()
    }
}
