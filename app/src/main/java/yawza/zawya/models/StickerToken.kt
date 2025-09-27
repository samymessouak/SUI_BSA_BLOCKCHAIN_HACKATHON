package yawza.zawya.models

import java.util.Date

data class StickerToken(
    val id: String,
    val zoneId: String,
    val brandName: String,
    val tokenId: String, // Blockchain token ID
    val mintedAt: Date,
    val rarity: StickerRarity = StickerRarity.COMMON,
    val metadata: Map<String, String> = emptyMap()
)
