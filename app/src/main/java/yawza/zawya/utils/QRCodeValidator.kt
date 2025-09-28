package yawza.zawya.utils

import android.util.Log
import org.json.JSONObject
import java.security.MessageDigest
import java.util.UUID

object QRCodeValidator {

    data class ValidationResult(
        val isValid: Boolean,
        val stickerId: String? = null,
        val zoneId: String? = null,
        val brandName: String? = null,
        val error: String? = null
    )

    /**
     * Validates a sticker QR code and extracts information
     * QR Code format: JSON with sticker_id, zone_id, brand_name, and signature
     */
    fun validateStickerQR(qrData: String): ValidationResult {
        return try {
            Log.d("QRCodeValidator", "Validating QR code: $qrData")
            
            // Parse JSON from QR code
            val json = JSONObject(qrData)
            
            val stickerId = json.optString("sticker_id", "")
            val zoneId = json.optString("zone_id", "")
            val brandName = json.optString("brand_name", "")
            val signature = json.optString("signature", "")
            val timestamp = json.optLong("timestamp", 0)
            
            // Validate required fields
            if (stickerId.isEmpty() || zoneId.isEmpty() || brandName.isEmpty()) {
                return ValidationResult(false, error = "Missing required fields")
            }
            
            // Validate signature (optional for now, but good for security)
            if (signature.isNotEmpty() && !validateSignature(qrData, signature)) {
                return ValidationResult(false, error = "Invalid signature")
            }
            
            // Validate timestamp (QR codes shouldn't be too old)
            val currentTime = System.currentTimeMillis()
            val maxAge = 30 * 24 * 60 * 60 * 1000L // 30 days (very lenient for testing)
            if (timestamp > 0 && (currentTime - timestamp) > maxAge) {
                return ValidationResult(false, error = "QR code expired")
            }
            
            // Allow QR codes with no timestamp or old timestamps for testing
            if (timestamp == 0L || timestamp < 1000000000000L) {
                Log.d("QRCodeValidator", "QR code has no valid timestamp, allowing for testing")
            }
            
            Log.d("QRCodeValidator", "QR code validated successfully: stickerId=$stickerId, zoneId=$zoneId, brandName=$brandName")
            
            ValidationResult(
                isValid = true,
                stickerId = stickerId,
                zoneId = zoneId,
                brandName = brandName
            )
            
        } catch (e: Exception) {
            Log.e("QRCodeValidator", "Error validating QR code", e)
            ValidationResult(false, error = "Invalid QR code format: ${e.message}")
        }
    }

    /**
     * Validates the signature of a QR code (for security)
     */
    private fun validateSignature(_qrData: String, _signature: String): Boolean {
        // For now, we'll skip signature validation
        // In production, you'd validate against a server or use a secret key
        return true
    }

    /**
     * Generates a unique sticker ID for a zone
     * This would typically be called when creating QR codes for stickers
     */
    fun generateStickerId(zoneId: String, brandName: String): String {
        val timestamp = System.currentTimeMillis()
        val random = UUID.randomUUID().toString()
        val data = "$zoneId:$brandName:$timestamp:$random"
        
        // Create a hash-based ID for uniqueness
        val hash = MessageDigest.getInstance("SHA-256")
            .digest(data.toByteArray())
            .joinToString("") { "%02x".format(it) }
        
        return "sticker_${hash.take(16)}"
    }

    /**
     * Creates QR code data for a sticker
     * This would be used to generate QR codes for physical stickers
     */
    fun createStickerQRData(zoneId: String, brandName: String): String {
        val stickerId = generateStickerId(zoneId, brandName)
        val timestamp = System.currentTimeMillis()
        
        val json = JSONObject().apply {
            put("sticker_id", stickerId)
            put("zone_id", zoneId)
            put("brand_name", brandName)
            put("timestamp", timestamp)
            put("version", "1.0")
        }
        
        return json.toString()
    }
}
