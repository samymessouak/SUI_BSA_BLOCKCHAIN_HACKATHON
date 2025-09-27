Zo# 🔧 Yawza Setup Guide

## 📋 **Required Setup for Team Members**

### **1. Google Maps API Key**
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing
3. Enable "Maps SDK for Android"
4. Create credentials → API Key
5. Restrict the key to your package name: `yawza.zawya`

### **2. Firebase Setup**
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project
3. Add Android app with package name: `yawza.zawya`
4. Download `google-services.json`
5. Place it in `app/` directory

### **3. Local Configuration**
1. Copy `app/src/main/res/values/api_keys.xml.template` to `app/src/main/res/values/api_keys.xml`
2. Replace `YOUR_GOOGLE_MAPS_API_KEY_HERE` with your actual API key
3. Copy `app/google-services.json.template` to `app/google-services.json`
4. Replace all placeholder values with your actual Firebase config

### **4. Build the App**
```bash
./gradlew assembleDebug
```

## 🚨 **Security Notes**
- Never commit `api_keys.xml` or `google-services.json` to git
- These files are in `.gitignore` for security
- Use template files for team setup

## 🆘 **Troubleshooting**
- If maps don't show: Check API key restrictions
- If build fails: Ensure all template files are properly configured
- If Firebase errors: Verify `google-services.json` is correct
