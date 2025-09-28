# Yawza – SUI BSA Hackathon Project

## 🚀 Overview
Yawza is our submission for the **SUI BSA Hackathon**.  
It's a mobile application (prototype) that combines **blockchain technology** with a fun city exploration game.  

The concept:
- Sponsors place **digital sticker collections** in different locations across the city (like Easter eggs 🥚).
- Users move around, scan stickers with their phone camera, and collect them into their digital wallet.
- Once a user completes a sponsor's collection, they unlock **exclusive promotions** from that sponsor.
- Users can **trade stickers (tokens)** with each other, but only between different collections to keep the system balanced.

All sticker ownership and trades are stored **on-chain**, using the **Sui blockchain** and the **Move language**.

---

## 🏗️ Features
-  **Mobile App Prototype** (basic UI with camera scanning).
-  **Geolocated Sticker Collections** sponsored by companies.
-  **Blockchain-backed ownership** of stickers (NFTs/tokens).
-  **Token trading** between users across different collections.
-  **Rewards** when a collection is completed.

---

## ⚙️ Tech Stack

### Mobile App (Android)
- **Language**: Kotlin
- **UI Framework**: Android Views with Material Design
- **Maps**: Google Maps SDK
- **Architecture**: MVVM with ViewBinding
- **Location**: Google Play Services Location API

### Blockchain
- **Platform**: Sui blockchain
- **Language**: Move smart contracts
- **SDK**: KSUI (Kotlin SDK for Sui)
- **Token Standard**: SFTs (Semi-Fungible Tokens)

### Backend/Storage
- **On-chain**: Sticker ownership and trading
- **Off-chain**: User profiles, promotion metadata (Firebase)

---

## 📱 Interactive Map Features

### 🗺️ Map Functionality
- **Multi-level Zoom**: Seamless zoom from city-wide view to street level
- **Zone Heatmaps**: Color-coded circles showing sticker density
- **Real-time Location**: GPS tracking with user position indicator
- **Zone Interaction**: Tap zones to view detailed information

### 🎯 Zone System
- **Visual Indicators**: Colored circles showing sticker locations
- **Dynamic Circle Behavior**: When you enter a zone, the circle gets smaller and more precise to guide you to the exact sticker location
- **Brand Information**: Display sponsor name and available stickers
- **Action Buttons**: 
  - "Scan Here" - Opens camera for sticker scanning
  - "Navigate" - Centers map on zone location
- **Navigation**: Users must physically walk to zones to scan stickers

### 📍 Location Services
- **Permission Handling**: Automatic location permission requests
- **GPS Integration**: Real-time user position tracking
- **Zone Detection**: Automatic highlighting of nearby zones

---

## 🚀 Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24+ (Android 7.0)
- Google Maps API Key
- Sui blockchain access (for testing)

### 1. Clone the Repository
```bash
git clone <repository-url>
cd SUI_BSA_BLOCKCHAIN_HACKATHON
```

### 2. Configure Google Maps
1. Get a Google Maps API key from [Google Cloud Console](https://console.cloud.google.com/)
2. Enable the following APIs:
   - Maps SDK for Android
   - Places API
   - Geocoding API
3. Replace `YOUR_GOOGLE_MAPS_API_KEY` in `app/src/main/AndroidManifest.xml`

### 3. Build and Run
```bash
# Open in Android Studio
# Or use command line:
./gradlew assembleDebug
./gradlew installDebug
```

### 4. Test the App
- Grant location permissions when prompted
- Navigate around the map to see sample zones
- Tap on zones to view brand information
- Test zoom functionality (city-wide to street level)

---

## 📂 Project Structure

```
SUI_BSA_BLOCKCHAIN_HACKATHON/
├── app/                                    # Android App
│   ├── src/main/
│   │   ├── java/yawza/zawya/
│   │   │   ├── MainActivity.kt             # Main activity with map
│   │   │   ├── models/
│   │   │   │   └── StickerZone.kt          # Data models
│   │   │   └── utils/
│   │   │       ├── ZoneOverlayManager.kt   # Map overlay management
│   │   │       └── ZoneInfoDialog.kt       # Zone info dialog
│   │   ├── res/                            # Resources
│   │   │   ├── layout/                     # UI layouts
│   │   │   ├── values/                     # Strings, colors, themes
│   │   │   └── drawable/                   # Button styles
│   │   └── AndroidManifest.xml             # App configuration
│   └── build.gradle.kts                    # App dependencies
├── stickers/                               # Sui Move Contracts
│   ├── sources/
│   │   └── stickers.move                   # Smart contracts
│   └── Move.toml                           # Move configuration
└── README.md                               # This file
```

---

## 🔗 Blockchain Integration

### QR Code Scanning & Blockchain Updates
When you scan a QR code for the **first time**, the sticker is automatically added to your **blockchain inventory**. This creates a permanent, verifiable record on the Sui blockchain.

### How to Verify Blockchain Updates
After scanning a QR code, you can verify that the blockchain has been updated:

1. **Check Your Objects**: Use the Sui CLI to see all objects owned by your address:
   ```bash
   sui client objects <YOUR_ADDRESS>
   ```

2. **Inspect Individual Objects**: Check each object ID on [Suiscan](https://suiscan.xyz/) to see the detailed metadata and ownership information.

### Blockchain Workflow
1. **First Scan**: QR code → Sticker added to blockchain inventory
2. **Verification**: Check `sui client objects` to see new inventory objects
3. **Trading**: Transfer stickers between users using blockchain transactions
4. **Ownership**: All sticker ownership is permanently recorded on-chain

---

## 🎮 Complete User Workflow

### 1. **Authentication & Setup**
- Launch the app and sign in/sign up
- Create your blockchain inventory (one-time setup)
- Grant location and camera permissions

### 2. **Map Exploration**
- Navigate around the interactive map of Lausanne
- Look for colored zones indicating sticker locations
- Tap on zones to see brand information and available stickers

### 3. **QR Code Scanning**
- When near a sticker zone, tap the purple scan button
- Point your camera at the QR code
- **First scan**: Sticker is automatically added to your blockchain inventory
- **Subsequent scans**: No duplicate stickers (blockchain prevents this)

### 4. **Collection Management**
- View your collected stickers in the "Collection" tab
- See progress for each brand (McDonald's, Nike, Sephora)
- Track completion percentages and remaining stickers

### 5. **Trading & Transactions**
- Use the "Transaction" tab to transfer stickers to friends
- Enter friend's wallet address
- Select owned stickers to send
- All transfers are recorded on the blockchain

### 6. **Blockchain Verification**
- Verify your inventory: `sui client objects <YOUR_ADDRESS>`
- Check individual stickers on [Suiscan](https://suiscan.xyz/)
- All ownership is permanently recorded on-chain

---

## 📱 App Screens & Workflow

### **Map Screen** 🗺️
- Interactive map of Lausanne with colored zones
- Purple hexagonal scan button (center-bottom)
- Zone indicators showing sticker locations
- Real-time GPS tracking

### **Collection Screen** 📚
- View all your collected stickers by brand
- Progress tracking (e.g., "1/2 McDonald's stickers")
- Completion percentages
- Detailed sticker views with lock/unlock status

### **Transaction Screen** 💱
- Transfer stickers to friends
- Enter friend's wallet address
- Select owned stickers to send
- Visual distinction: owned (dark purple) vs unowned (light purple with lock)

### **QR Scanner** 📷
- Camera-based QR code scanning
- Automatic blockchain inventory updates
- Duplicate prevention (first scan only)

---

## 📸 Workflow Screenshots

The app workflow is demonstrated through these key screens:

### **Map Screen** 🗺️
*Interactive map of Lausanne showing colored zones and the purple hexagonal scan button*

### **Collection Screen** 📚
*"My Sticker Collection" showing brand progress (McDonald's 1/2, Nike 1/3, Sephora 0/4)*

### **Collection Detail Modal** 🔍
*McDonald's Collection modal showing individual stickers with lock/unlock status*

### **Transaction Screen** 💱
*Sticker transfer interface with friend's wallet ID input and 3x3 sticker grid*

---

### For Developers
1. **Map Integration**: The app uses Google Maps SDK for interactive maps
2. **Zone Management**: `ZoneOverlayManager` handles zone visualization
3. **Location Services**: GPS tracking with permission handling
4. **UI Components**: Material Design with custom dialogs

---

## 🔧 Configuration

### Google Maps Setup
```xml
<!-- In AndroidManifest.xml -->
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_GOOGLE_MAPS_API_KEY" />
```

### Sample Zone Data
The app includes sample zones in Lausanne:
- **Lausanne Flon Zone**: 
  - **Sephora**: 4 stickers
  - **Nike**: 3 stickers  
  - **McDonald's**: 2 stickers

---

## 🚧 Next Steps

### Immediate Development
1. **Camera Integration**: Implement sticker scanning functionality
2. **Blockchain Connection**: Integrate with Sui Move contracts
3. **User Authentication**: Add wallet connection
4. **Data Persistence**: Store user progress and collected stickers

### Future Enhancements
1. **AR Features**: Augmented reality sticker scanning
2. **Social Features**: Share achievements and trade stickers
3. **Gamification**: Leaderboards and achievements
4. **Analytics**: Track user engagement and zone popularity

---

## 🤝 Contributing

This is a hackathon project for the SUI BSA Blockchain Hackathon. Contributions are welcome!

### Development Workflow
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

---

## 📄 License

This project is part of the SUI BSA Blockchain Hackathon. See the hackathon terms and conditions for usage rights.

---

## 🏆 Hackathon Submission

**Project**: Yawza  
**Track**: Mobile + Blockchain Integration  
**Focus**: Interactive Map with Zone-based Sticker Collection  
**Technology**: Android (Kotlin) + Sui (Move) + Google Maps  

This implementation demonstrates a working MVP of the interactive map feature, ready for blockchain integration and camera scanning functionality.
