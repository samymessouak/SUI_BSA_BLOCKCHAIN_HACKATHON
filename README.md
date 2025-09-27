# StickerHunt – SUI BSA Hackathon Project

## 🚀 Overview
StickerHunt is our submission for the **SUI BSA Hackathon**.  
It’s a mobile application (prototype) that combines **blockchain technology** with a fun city exploration game.  

The concept:
- Sponsors place **digital sticker collections** in different locations across the city (like Easter eggs 🥚).
- Users move around, scan stickers with their phone camera, and collect them into their digital wallet.
- Once a user completes a sponsor’s collection, they unlock **exclusive promotions** from that sponsor.
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
- **Frontend (Mobile App)**: React Native (expo) or Flutter (prototype phase).
- **Blockchain**: [Sui](https://sui.io/) smart contracts written in **Move**.
- **Token System**:
  - Stickers are represented as **NFTs** or **SFTs (semi-fungible tokens)** on Sui.
  - Collections are grouped under sponsor IDs.
  - Trades are restricted by rules (only cross-collection).
- **Backend/Storage**:
  - Promotions metadata stored off-chain (e.g. Firebase / Supabase).
  - On-chain proof of collection completion.

---

## 📂 Repository Structure

TODO
