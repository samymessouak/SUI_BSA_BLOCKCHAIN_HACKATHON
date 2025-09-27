# 🏷️ Physical QR Code Generation Guide

## 📋 **How to Create Unique QR Codes for Physical Stickers**

### **🎯 Goal:**
Create physical QR codes that will be placed on walls/stickers around Lausanne. Each QR code contains a unique sticker ID that can only be scanned once per user.

## 🔧 **QR Code Generation Process**

### **Step 1: Create QR Code Data Structure**
Each QR code contains JSON data with:
```json
{
  "sticker_id": "unique_sticker_id",
  "zone_id": "zone_identifier", 
  "brand_name": "Brand Name",
  "timestamp": 1234567890,
  "version": "1.0"
}
```

### **Step 2: Generate Unique Sticker IDs**

#### **For McDonald's (2 stickers):**
```bash
# Zone 1: Ouchy McDonald's
sticker_id: "mcdo_ouchy_001"
zone_id: "mcdo_1"
brand_name: "McDonald's"

# Zone 2: Flon McDonald's  
sticker_id: "mcdo_flon_002"
zone_id: "mcdo_2"
brand_name: "McDonald's"
```

#### **For Nike (3 stickers):**
```bash
# Zone 1: Near cathedral
sticker_id: "nike_cathedral_001"
zone_id: "nike_1"
brand_name: "Nike"

# Zone 2: South of cathedral
sticker_id: "nike_south_002"
zone_id: "nike_2"
brand_name: "Nike"

# Zone 3: North of cathedral
sticker_id: "nike_north_003"
zone_id: "nike_3"
brand_name: "Nike"
```

#### **For Sephora (4 stickers):**
```bash
# Zone 1: Cathedral area
sticker_id: "sephora_cathedral_001"
zone_id: "sephora_1"
brand_name: "Sephora"

# Zone 2: Ouchy area
sticker_id: "sephora_ouchy_002"
zone_id: "sephora_2"
brand_name: "Sephora"

# Zone 3: Flon area
sticker_id: "sephora_flon_003"
zone_id: "sephora_3"
brand_name: "Sephora"

# Zone 4: North area
sticker_id: "sephora_north_004"
zone_id: "sephora_4"
brand_name: "Sephora"
```

## 🛠️ **QR Code Generation Tools**

### **Option 1: Online QR Generator**
1. Go to [QR Code Generator](https://www.qr-code-generator.com/)
2. Select "Text" type
3. Paste the JSON data
4. Download as PNG/SVG

### **Option 2: Python Script (Recommended)**
```python
import qrcode
import json
from datetime import datetime

def generate_sticker_qr(sticker_id, zone_id, brand_name):
    data = {
        "sticker_id": sticker_id,
        "zone_id": zone_id,
        "brand_name": brand_name,
        "timestamp": int(datetime.now().timestamp()),
        "version": "1.0"
    }
    
    qr = qrcode.QRCode(
        version=1,
        error_correction=qrcode.constants.ERROR_CORRECT_L,
        box_size=10,
        border=4,
    )
    qr.add_data(json.dumps(data))
    qr.make(fit=True)
    
    img = qr.make_image(fill_color="black", back_color="white")
    return img

# Generate all QR codes
stickers = [
    ("mcdo_ouchy_001", "mcdo_1", "McDonald's"),
    ("mcdo_flon_002", "mcdo_2", "McDonald's"),
    ("nike_cathedral_001", "nike_1", "Nike"),
    ("nike_south_002", "nike_2", "Nike"),
    ("nike_north_003", "nike_3", "Nike"),
    ("sephora_cathedral_001", "sephora_1", "Sephora"),
    ("sephora_ouchy_002", "sephora_2", "Sephora"),
    ("sephora_flon_003", "sephora_3", "Sephora"),
    ("sephora_north_004", "sephora_4", "Sephora"),
]

for sticker_id, zone_id, brand_name in stickers:
    img = generate_sticker_qr(sticker_id, zone_id, brand_name)
    img.save(f"qr_{sticker_id}.png")
    print(f"Generated QR code for {sticker_id}")
```

### **Option 3: Node.js Script**
```javascript
const QRCode = require('qrcode');
const fs = require('fs');

async function generateQR(stickerId, zoneId, brandName) {
    const data = {
        sticker_id: stickerId,
        zone_id: zoneId,
        brand_name: brandName,
        timestamp: Date.now(),
        version: "1.0"
    };
    
    const qrData = JSON.stringify(data);
    const qrCode = await QRCode.toDataURL(qrData);
    
    // Save as file
    const base64Data = qrCode.replace(/^data:image\/png;base64,/, "");
    fs.writeFileSync(`qr_${stickerId}.png`, base64Data, 'base64');
    
    console.log(`Generated QR code for ${stickerId}`);
}

// Generate all QR codes
const stickers = [
    ["mcdo_ouchy_001", "mcdo_1", "McDonald's"],
    ["mcdo_flon_002", "mcdo_2", "McDonald's"],
    ["nike_cathedral_001", "nike_1", "Nike"],
    ["nike_south_002", "nike_2", "Nike"],
    ["nike_north_003", "nike_3", "Nike"],
    ["sephora_cathedral_001", "sephora_1", "Sephora"],
    ["sephora_ouchy_002", "sephora_2", "Sephora"],
    ["sephora_flon_003", "sephora_3", "Sephora"],
    ["sephora_north_004", "sephora_4", "Sephora"],
];

stickers.forEach(([stickerId, zoneId, brandName]) => {
    generateQR(stickerId, zoneId, brandName);
});
```

## 🎨 **QR Code Design for Physical Stickers**

### **Brand-Specific Colors:**
- **McDonald's**: Red QR codes on white background
- **Nike**: Black QR codes on white background  
- **Sephora**: Pink/Magenta QR codes on white background

### **Size Recommendations:**
- **Minimum**: 5cm x 5cm (2" x 2")
- **Recommended**: 8cm x 8cm (3" x 3")
- **Large**: 12cm x 12cm (5" x 5") for high visibility

### **Print Specifications:**
- **Resolution**: 300 DPI minimum
- **Format**: PNG or SVG for vector graphics
- **Background**: White or light color
- **Contrast**: High contrast for easy scanning

## 📍 **Physical Placement Strategy**

### **McDonald's Zones:**
1. **Ouchy McDonald's** - Place QR code near entrance
2. **Flon McDonald's** - Place QR code on window or wall

### **Nike Zones:**
1. **Cathedral area** - Place on building facade
2. **South of cathedral** - Place on street furniture
3. **North of cathedral** - Place on wall or post

### **Sephora Zones:**
1. **Cathedral area** - Place near store entrance
2. **Ouchy area** - Place on building wall
3. **Flon area** - Place on window or display
4. **North area** - Place on street furniture

## 🔒 **Security Considerations**

### **Unique Sticker IDs:**
- Each QR code has a unique `sticker_id`
- Users can only scan each sticker once
- Blockchain prevents duplicate ownership

### **Timestamp Validation:**
- QR codes have creation timestamps
- App can reject very old QR codes
- Prevents replay attacks

### **Zone Validation:**
- QR codes are tied to specific zones
- App validates user is in correct location
- Prevents remote scanning

## 🧪 **Testing QR Codes**

### **Test Data for Development:**
```json
{
  "sticker_id": "test_mcdo_001",
  "zone_id": "mcdo_1", 
  "brand_name": "McDonald's",
  "timestamp": 1703123456789,
  "version": "1.0",
  "test": true
}
```

### **Validation Process:**
1. Scan QR code with app
2. App validates JSON format
3. App checks if user already has sticker
4. App mints sticker on blockchain
5. App updates collection progress

## 📱 **App Integration**

The app is already set up to:
- ✅ Scan QR codes with camera
- ✅ Validate QR code format
- ✅ Check for duplicate scans
- ✅ Mint stickers on blockchain
- ✅ Update collection progress
- ✅ Show brand-specific feedback

## 🎯 **Next Steps**

1. **Generate QR codes** using one of the methods above
2. **Print QR codes** on weather-resistant material
3. **Place QR codes** at designated locations in Lausanne
4. **Test scanning** with the app
5. **Monitor collection** progress through the app

## 📊 **Expected Results**

- **9 unique QR codes** total (2 McDonald's + 3 Nike + 4 Sephora)
- **Each user can scan each QR code once**
- **Collection completion** triggers promotion codes
- **Blockchain tracking** of all sticker ownership
