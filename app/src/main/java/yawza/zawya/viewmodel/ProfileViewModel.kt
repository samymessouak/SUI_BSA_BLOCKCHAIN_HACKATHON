package yawza.zawya.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
    
    private val _ownedStickers = MutableLiveData<List<StickerToken>>()
    val ownedStickers: LiveData<List<StickerToken>> = _ownedStickers
    
    private val _walletAddress = MutableLiveData<String>()
    val walletAddress: LiveData<String> = _walletAddress
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    init {
        loadUserProfile()
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
    
    fun collectSticker(zoneId: String, brandName: String, stickerCount: Int) {
        _isLoading.value = true
        
        // Call blockchain service to mint sticker
        blockchainService.mintSticker(
            zoneId = zoneId,
            brandName = brandName,
            stickerCount = stickerCount,
            onSuccess = { stickerToken ->
                // Add to owned stickers
                val currentStickers = _ownedStickers.value ?: emptyList()
                _ownedStickers.value = currentStickers + stickerToken
                _isLoading.value = false
            },
            onError = { error ->
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
}
