# 🔗 Blockchain Integration Plan for Yawza

## 📋 **Files to Modify for Sui Blockchain Integration**

### **1. 🔧 BlockchainService.kt - Core Blockchain Logic**
**Current Status:** Mock implementation
**Changes Needed:**
- Replace mock `mintSticker()` with actual KSUI calls
- Implement real `getOwnedStickers()` from Sui blockchain
- Add `transferSticker()` functionality
- Add `checkCollectionComplete()` for promotion codes
- Add wallet connection/authentication

**Key Methods to Implement:**
```kotlin
// Real blockchain calls
fun mintSticker(zoneId: String, brandName: String, userAddress: String)
fun getOwnedStickers(userAddress: String)
fun transferSticker(stickerId: String, toAddress: String)
fun checkCollectionComplete(brandName: String, userAddress: String)
fun generatePromotionCode(brandName: String, userAddress: String)
```

### **2. 🎯 ZoneController.kt - Scan Logic**
**Current Status:** Simulates sticker collection
**Changes Needed:**
- Replace `simulateStickerCollection()` with real blockchain minting
- Add QR code scanning for unique sticker IDs
- Prevent duplicate scans (check blockchain ownership)
- Add collection completion verification

**Key Methods to Modify:**
```kotlin
// Replace simulation with real blockchain
private fun handleStickerScan(zone: StickerZone) {
    // 1. Scan QR code to get unique sticker ID
    // 2. Check if user already owns this sticker
    // 3. Mint sticker on blockchain
    // 4. Check if collection is complete
    // 5. Generate promotion code if complete
}
```

### **3. 👤 ProfileViewModel.kt - User State Management**
**Current Status:** Manages owned stickers
**Changes Needed:**
- Connect to real blockchain data
- Add wallet address management
- Add collection completion tracking
- Add promotion code storage

**Key Methods to Add:**
```kotlin
fun connectWallet(walletAddress: String)
fun getPromotionCodes(): List<PromotionCode>
fun isCollectionComplete(brandName: String): Boolean
fun generatePromotionCode(brandName: String)
```

### **4. 🏗️ New Files to Create**

#### **A. WalletManager.kt**
```kotlin
class WalletManager {
    fun connectWallet(): String
    fun getWalletAddress(): String?
    fun signTransaction(transaction: String): String
}
```

#### **B. QRCodeScanner.kt**
```kotlin
class QRCodeScanner {
    fun scanQRCode(): String
    fun validateStickerQR(qrData: String): StickerInfo
}
```

#### **C. PromotionManager.kt**
```kotlin
class PromotionManager {
    fun generatePromotionCode(brandName: String): String
    fun validatePromotionCode(code: String): Boolean
    fun redeemPromotion(code: String)
}
```

### **5. 📱 UI Changes Needed**

#### **A. Add Wallet Connection Screen**
- Connect to Sui wallet
- Display wallet address
- Show owned stickers

#### **B. Add QR Scanner**
- Camera integration for QR scanning
- Sticker validation
- Collection progress

#### **C. Add Promotion Screen**
- Show available promotion codes
- Redeem codes
- Track redemptions

## 🔄 **Integration Flow**

### **Scan Process:**
1. User scans QR code → Get unique sticker ID
2. Check if user already owns this sticker (blockchain query)
3. If not owned → Mint sticker on blockchain
4. Update local state with new sticker
5. Check if collection is complete
6. If complete → Generate promotion code

### **Collection Verification:**
1. Smart contract checks all stickers owned by user
2. If complete → Mint promotion token
3. User can redeem promotion code

### **Transfer Process:**
1. User selects sticker to transfer
2. Enter recipient address
3. Sign transaction with wallet
4. Transfer ownership on blockchain

## 🛠️ **Implementation Priority**

### **Phase 1: Basic Blockchain Integration**
1. Replace mock `BlockchainService` with real KSUI calls
2. Add wallet connection
3. Implement real sticker minting

### **Phase 2: QR Code Scanning**
1. Add camera integration
2. Implement QR code scanning
3. Add sticker validation

### **Phase 3: Collection & Promotions**
1. Add collection completion logic
2. Implement promotion code generation
3. Add transfer functionality

### **Phase 4: UI/UX**
1. Add wallet connection screen
2. Add QR scanner UI
3. Add promotion management screen

## 🔐 **Security Considerations**

- Validate QR codes before minting
- Prevent duplicate scans
- Secure wallet connection
- Validate promotion codes
- Rate limiting for scans

## 📊 **Data Models to Update**

### **StickerToken.kt**
```kotlin
data class StickerToken(
    val id: String,           // Unique blockchain ID
    val zoneId: String,      // Zone where minted
    val brandName: String,    // Brand
    val tokenId: String,      // Sui object ID
    val ownerAddress: String, // Current owner
    val mintedAt: Date,      // Mint timestamp
    val rarity: StickerRarity,
    val metadata: Map<String, String>
)
```

### **PromotionCode.kt** (New)
```kotlin
data class PromotionCode(
    val id: String,
    val brandName: String,
    val code: String,
    val generatedAt: Date,
    val redeemedAt: Date?,
    val isRedeemed: Boolean
)
```
