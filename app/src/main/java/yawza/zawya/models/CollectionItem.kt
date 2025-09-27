package yawza.zawya.models

data class CollectionItem(
    val brandName: String,
    val brandIcon: String,
    val collectedStickers: Int,
    val totalStickers: Int,
    val brandColor: Int
) {
    val progressPercentage: Int
        get() = if (totalStickers > 0) {
            ((collectedStickers.toFloat() / totalStickers.toFloat()) * 100).toInt()
        } else {
            0
        }
    
    val isComplete: Boolean
        get() = collectedStickers >= totalStickers
}
