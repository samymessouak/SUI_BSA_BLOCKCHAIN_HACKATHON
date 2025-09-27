package yawza.zawya.models

import com.google.android.gms.maps.model.LatLng

data class StickerZone(
    val id: String,
    val center: LatLng,
    val radius: Double, // in meters
    val brandName: String,
    val stickerCount: Int,
    val color: Int,
    val description: String = "",
    val isActive: Boolean = true,
    val sponsorId: String = "",
    val collectionId: String = ""
)

data class Sticker(
    val id: String,
    val zoneId: String,
    val brandName: String,
    val rarity: StickerRarity,
    val imageUrl: String = "",
    val tokenId: String = "", // Blockchain token ID
    val isCollected: Boolean = false
)

enum class StickerRarity {
    COMMON,
    RARE,
    EPIC,
    LEGENDARY
}

data class ZoneInfo(
    val zone: StickerZone,
    val nearbyStickers: List<Sticker>,
    val userDistance: Double = 0.0 // in meters
)
