package yawza.zawya.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import yawza.zawya.models.StickerToken
import yawza.zawya.models.StickerRarity
import yawza.zawya.service.BlockchainService

class TransactionsViewModel : ViewModel() {

    private val blockchainService = BlockchainService()
    private val profileViewModel = ProfileViewModel.getInstance()

    private val _stickers = MutableLiveData<List<StickerToken>>()
    val stickers: LiveData<List<StickerToken>> = _stickers

    private val _transferResult = MutableLiveData<Result<Unit>>()
    val transferResult: LiveData<Result<Unit>> = _transferResult

    private var friendId: String = ""
    private var currentFilter: String = "All"

    init {
        loadStickers()
        observeProfileChanges()
    }
    
    private fun observeProfileChanges() {
        // Observe ProfileViewModel changes
        profileViewModel.ownedStickers.observeForever { _ ->
            loadStickers() // Reload stickers when collection changes
        }
    }

    private fun loadStickers() {
        // Get all possible stickers (owned + unowned) based on real collection data
        val allStickers = getAllPossibleStickers()
        _stickers.value = allStickers
    }

    private fun getAllPossibleStickers(): List<StickerToken> {
        // Get real collection data from ProfileViewModel
        val ownedStickers = profileViewModel.getOwnedStickers()
        
        // Create all possible stickers (2 McDonald's, 3 Nike, 4 Sephora)
        val allPossibleStickers = mutableListOf<StickerToken>()
        
        // McDonald's stickers (2 total)
        for (i in 1..2) {
            val stickerId = "mcdo_$i"
            val isOwned = ownedStickers.any { it.id == stickerId }
            allPossibleStickers.add(
                StickerToken(
                    id = stickerId,
                    zoneId = stickerId,
                    brandName = "McDonald's",
                    tokenId = "token_mcdo_$i",
                    mintedAt = java.util.Date(),
                    rarity = StickerRarity.COMMON,
                    metadata = mapOf("owner" to if (isOwned) "user_address" else "")
                )
            )
        }
        
        // Nike stickers (3 total)
        for (i in 1..3) {
            val stickerId = "nike_$i"
            val isOwned = ownedStickers.any { it.id == stickerId }
            allPossibleStickers.add(
                StickerToken(
                    id = stickerId,
                    zoneId = stickerId,
                    brandName = "Nike",
                    tokenId = "token_nike_$i",
                    mintedAt = java.util.Date(),
                    rarity = StickerRarity.RARE,
                    metadata = mapOf("owner" to if (isOwned) "user_address" else "")
                )
            )
        }
        
        // Sephora stickers (4 total)
        for (i in 1..4) {
            val stickerId = "sephora_$i"
            val isOwned = ownedStickers.any { it.id == stickerId }
            allPossibleStickers.add(
                StickerToken(
                    id = stickerId,
                    zoneId = stickerId,
                    brandName = "Sephora",
                    tokenId = "token_sephora_$i",
                    mintedAt = java.util.Date(),
                    rarity = StickerRarity.EPIC,
                    metadata = mapOf("owner" to if (isOwned) "user_address" else "")
                )
            )
        }
        
        return allPossibleStickers
    }

    fun setFriendId(friendId: String) {
        this.friendId = friendId
    }
    
    fun refreshStickers() {
        // Reload stickers to reflect current collection state
        loadStickers()
    }

    fun filterByBrand(brand: String) {
        currentFilter = brand
        val allStickers = getAllPossibleStickers()
        val filteredStickers = if (brand == "All") {
            allStickers
        } else {
            allStickers.filter { it.brandName == brand }
        }
        _stickers.value = filteredStickers
    }

    fun sendRequest(stickerIds: List<String>) {
        if (friendId.isEmpty()) {
            _transferResult.value = Result.failure(Exception("No friend ID set"))
            return
        }

        if (stickerIds.isEmpty()) {
            _transferResult.value = Result.failure(Exception("No stickers selected"))
            return
        }

        // Check if user is trying to send multiple stickers from same brand
        val selectedStickers = _stickers.value?.filter { stickerIds.contains(it.id) } ?: emptyList()
        val brands = selectedStickers.map { it.brandName }.distinct()
        
        if (brands.size > 1) {
            _transferResult.value = Result.failure(Exception("Cannot send stickers from different brands"))
            return
        }

        // Send request to friend
        sendRequestToFriend(stickerIds, friendId)
    }

    fun transferStickers(stickerIds: List<String>) {
        if (friendId.isEmpty()) {
            _transferResult.value = Result.failure(Exception("No friend ID set"))
            return
        }

        if (stickerIds.isEmpty()) {
            _transferResult.value = Result.failure(Exception("No stickers selected"))
            return
        }

        // Check if user is trying to transfer multiple stickers from same brand
        val selectedStickers = _stickers.value?.filter { stickerIds.contains(it.id) } ?: emptyList()
        val brands = selectedStickers.map { it.brandName }.distinct()
        
        if (brands.size > 1) {
            _transferResult.value = Result.failure(Exception("Cannot transfer stickers from different brands"))
            return
        }

        // Transfer stickers via blockchain
        transferStickersToFriend(stickerIds, friendId)
    }

    private fun sendRequestToFriend(stickerIds: List<String>, friendId: String) {
        // Simulate sending request to friend
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            try {
                // TODO: Implement actual blockchain request
                // For now, just simulate success
                _transferResult.value = Result.success(Unit)
            } catch (e: Exception) {
                _transferResult.value = Result.failure(e)
            }
        }, 1000) // Simulate network delay
    }

    private fun transferStickersToFriend(stickerIds: List<String>, friendId: String) {
        // Simulate blockchain transfer
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            try {
                // TODO: Implement actual blockchain transfer
                // For now, just simulate success
                _transferResult.value = Result.success(Unit)
            } catch (e: Exception) {
                _transferResult.value = Result.failure(e)
            }
        }, 2000) // Simulate network delay
    }
}
