package yawza.zawya.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import yawza.zawya.models.CollectionItem

class CollectionViewModel : ViewModel() {
    
    private val profileViewModel = ProfileViewModel.getInstance()
    
    private val _collections = MutableLiveData<List<CollectionItem>>()
    val collections: LiveData<List<CollectionItem>> = _collections
    
    private val _totalStickers = MutableLiveData<Int>()
    val totalStickers: LiveData<Int> = _totalStickers
    
    init {
        loadCollections()
        observeProfileChanges()
    }
    
    private fun loadCollections() {
        val collections = listOf(
            CollectionItem(
                brandName = "McDonald's",
                brandIcon = "🍔",
                collectedStickers = profileViewModel.getStickerCountForBrand("McDonald's"),
                totalStickers = 2,
                brandColor = android.graphics.Color.RED
            ),
            CollectionItem(
                brandName = "Nike",
                brandIcon = "👟",
                collectedStickers = profileViewModel.getStickerCountForBrand("Nike"),
                totalStickers = 3,
                brandColor = android.graphics.Color.BLACK
            ),
            CollectionItem(
                brandName = "Sephora",
                brandIcon = "💄",
                collectedStickers = profileViewModel.getStickerCountForBrand("Sephora"),
                totalStickers = 4,
                brandColor = android.graphics.Color.MAGENTA
            )
        )
        
        _collections.value = collections
        _totalStickers.value = collections.sumOf { it.collectedStickers }
    }
    
    private fun observeProfileChanges() {
        // Use a direct approach - update collections whenever we need to
        // This will be called from the Fragment when needed
    }
    
    private fun updateCollectionsFromProfile() {
        val mcdonaldsCount = profileViewModel.getStickerCountForBrand("McDonald's")
        val nikeCount = profileViewModel.getStickerCountForBrand("Nike")
        val sephoraCount = profileViewModel.getStickerCountForBrand("Sephora")
        
        android.util.Log.d("CollectionViewModel", "Updating collections: McDonald's=$mcdonaldsCount, Nike=$nikeCount, Sephora=$sephoraCount")
        
        val collections = listOf(
            CollectionItem(
                brandName = "McDonald's",
                brandIcon = "🍔",
                collectedStickers = mcdonaldsCount,
                totalStickers = 2,
                brandColor = android.graphics.Color.RED
            ),
            CollectionItem(
                brandName = "Nike",
                brandIcon = "👟",
                collectedStickers = nikeCount,
                totalStickers = 3,
                brandColor = android.graphics.Color.BLACK
            ),
            CollectionItem(
                brandName = "Sephora",
                brandIcon = "💄",
                collectedStickers = sephoraCount,
                totalStickers = 4,
                brandColor = android.graphics.Color.MAGENTA
            )
        )
        
        _collections.value = collections
        _totalStickers.value = collections.sumOf { it.collectedStickers }
        
        android.util.Log.d("CollectionViewModel", "Collections updated: ${collections.map { "${it.brandName}=${it.collectedStickers}" }}")
    }
    
    fun updateCollection(brandName: String, newCount: Int) {
        val currentCollections = _collections.value ?: return
        val updatedCollections = currentCollections.map { collection ->
            if (collection.brandName == brandName) {
                collection.copy(collectedStickers = newCount)
            } else {
                collection
            }
        }
        
        _collections.value = updatedCollections
        _totalStickers.value = updatedCollections.sumOf { it.collectedStickers }
    }
    
    fun getProgressPercentage(collection: CollectionItem): Int {
        return if (collection.totalStickers > 0) {
            ((collection.collectedStickers.toFloat() / collection.totalStickers.toFloat()) * 100).toInt()
        } else {
            0
        }
    }
    
    fun refreshCollections() {
        updateCollectionsFromProfile()
    }
}
