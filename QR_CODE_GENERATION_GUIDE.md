# QR Code Generation Guide for StickerHunt App

## 📱 **QR Code Data Format**

Your app expects QR codes with this JSON structure:
```json
{
  "sticker_id": "mcdo_1",
  "zone_id": "mcdo_1", 
  "brand_name": "McDonald's",
  "signature": "optional_signature",
  "timestamp": 1695892800000
}
```

## 🎯 **Valid Sticker IDs**

### McDonald's Stickers (2 total)
- `mcdo_1` - McDonald's Sticker 1
- `mcdo_2` - McDonald's Sticker 2

### Nike Stickers (3 total)  
- `nike_1` - Nike Sticker 1
- `nike_2` - Nike Sticker 2
- `nike_3` - Nike Sticker 3

### Sephora Stickers (4 total)
- `sephora_1` - Sephora Sticker 1
- `sephora_2` - Sephora Sticker 2
- `sephora_3` - Sephora Sticker 3
- `sephora_4` - Sephora Sticker 4

## 🛠️ **Method 1: Online QR Code Generator**

1. **Go to**: https://www.qr-code-generator.com/
2. **Select**: "Text" option
3. **Paste this JSON** (replace with your desired sticker):
```json
{"sticker_id":"mcdo_1","zone_id":"mcdo_1","brand_name":"McDonald's","signature":"","timestamp":1695892800000}
```
4. **Click**: "Generate QR Code"
5. **Download**: High resolution PNG

## 🛠️ **Method 2: Python Script (Recommended)**

Create a file called `generate_qr_codes.py`:

```python
import qrcode
import json
import time
from PIL import Image, ImageDraw, ImageFont

def generate_sticker_qr(sticker_id, zone_id, brand_name, output_file):
    # Create QR data
    qr_data = {
        "sticker_id": sticker_id,
        "zone_id": zone_id,
        "brand_name": brand_name,
        "signature": "",
        "timestamp": int(time.time() * 1000)
    }
    
    # Convert to JSON string
    json_string = json.dumps(qr_data)
    
    # Generate QR code
    qr = qrcode.QRCode(
        version=1,
        error_correction=qrcode.constants.ERROR_CORRECT_L,
        box_size=10,
        border=4,
    )
    qr.add_data(json_string)
    qr.make(fit=True)
    
    # Create QR code image
    img = qr.make_image(fill_color="black", back_color="white")
    
    # Add brand-specific styling
    if brand_name == "McDonald's":
        # Add red border
        img = add_brand_border(img, "red")
    elif brand_name == "Nike":
        # Add black border
        img = add_brand_border(img, "black")
    elif brand_name == "Sephora":
        # Add pink border
        img = add_brand_border(img, "magenta")
    
    # Save image
    img.save(output_file)
    print(f"Generated QR code for {brand_name} {sticker_id}: {output_file}")

def add_brand_border(img, color):
    # Add colored border around QR code
    width, height = img.size
    new_img = Image.new('RGB', (width + 40, height + 40), color)
    new_img.paste(img, (20, 20))
    return new_img

# Generate all stickers
stickers = [
    # McDonald's
    ("mcdo_1", "mcdo_1", "McDonald's", "qr_mcdo_1.png"),
    ("mcdo_2", "mcdo_2", "McDonald's", "qr_mcdo_2.png"),
    
    # Nike
    ("nike_1", "nike_1", "Nike", "qr_nike_1.png"),
    ("nike_2", "nike_2", "Nike", "qr_nike_2.png"),
    ("nike_3", "nike_3", "Nike", "qr_nike_3.png"),
    
    # Sephora
    ("sephora_1", "sephora_1", "Sephora", "qr_sephora_1.png"),
    ("sephora_2", "sephora_2", "Sephora", "qr_sephora_2.png"),
    ("sephora_3", "sephora_3", "Sephora", "qr_sephora_3.png"),
    ("sephora_4", "sephora_4", "Sephora", "qr_sephora_4.png"),
]

for sticker_id, zone_id, brand_name, output_file in stickers:
    generate_sticker_qr(sticker_id, zone_id, brand_name, output_file)

print("All QR codes generated successfully!")
```

**Install dependencies:**
```bash
pip install qrcode[pil] pillow
```

**Run the script:**
```bash
python generate_qr_codes.py
```

## 🛠️ **Method 3: JavaScript (Web-based)**

Create an HTML file `qr_generator.html`:

```html
<!DOCTYPE html>
<html>
<head>
    <title>StickerHunt QR Generator</title>
    <script src="https://cdn.jsdelivr.net/npm/qrcode@1.5.3/build/qrcode.min.js"></script>
</head>
<body>
    <h1>StickerHunt QR Code Generator</h1>
    
    <div>
        <label>Sticker ID:</label>
        <select id="stickerSelect">
            <option value="mcdo_1">McDonald's 1</option>
            <option value="mcdo_2">McDonald's 2</option>
            <option value="nike_1">Nike 1</option>
            <option value="nike_2">Nike 2</option>
            <option value="nike_3">Nike 3</option>
            <option value="sephora_1">Sephora 1</option>
            <option value="sephora_2">Sephora 2</option>
            <option value="sephora_3">Sephora 3</option>
            <option value="sephora_4">Sephora 4</option>
        </select>
    </div>
    
    <div>
        <button onclick="generateQR()">Generate QR Code</button>
    </div>
    
    <div id="qrcode"></div>
    
    <script>
        function generateQR() {
            const stickerId = document.getElementById('stickerSelect').value;
            const brandName = stickerId.startsWith('mcdo') ? 'McDonald\'s' : 
                             stickerId.startsWith('nike') ? 'Nike' : 'Sephora';
            
            const qrData = {
                sticker_id: stickerId,
                zone_id: stickerId,
                brand_name: brandName,
                signature: "",
                timestamp: Date.now()
            };
            
            const jsonString = JSON.stringify(qrData);
            
            // Clear previous QR code
            document.getElementById('qrcode').innerHTML = '';
            
            // Generate new QR code
            QRCode.toCanvas(document.getElementById('qrcode'), jsonString, {
                width: 300,
                margin: 2,
                color: {
                    dark: '#000000',
                    light: '#FFFFFF'
                }
            }, function (error) {
                if (error) console.error(error);
                console.log('QR Code generated successfully!');
            });
        }
    </script>
</body>
</html>
```

## 🎯 **Testing Your QR Codes**

1. **Generate a QR code** using any method above
2. **Open your app** on the emulator
3. **Go to Map** → **Navigate to a zone** → **Tap "Scan here"**
4. **Point camera at QR code** (you can display it on another screen)
5. **App should detect** the sticker and add it to your collection

## 📱 **Physical QR Code Setup**

1. **Print QR codes** on paper or stickers
2. **Place them** in the corresponding zones:
   - McDonald's zones: `mcdo_1`, `mcdo_2`
   - Nike zones: `nike_1`, `nike_2`, `nike_3`
   - Sephora zones: `sephora_1`, `sephora_2`, `sephora_3`, `sephora_4`
3. **Test scanning** with your app

## 🔧 **Troubleshooting**

- **QR not detected**: Make sure JSON format is exact
- **Invalid QR code**: Check timestamp is recent
- **Wrong sticker**: Verify sticker_id matches zone
- **Camera issues**: Ensure camera permissions are granted

## 🎨 **Design Tips**

- **High contrast**: Black QR on white background
- **Size**: At least 2x2 inches for easy scanning
- **Brand colors**: Use brand-specific borders
- **Durability**: Laminate for outdoor use
