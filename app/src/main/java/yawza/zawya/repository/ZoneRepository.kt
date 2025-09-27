package yawza.zawya.repository

import com.google.android.gms.maps.model.LatLng
import yawza.zawya.models.StickerZone

class ZoneRepository {
    
    // Lausanne zones data
    fun getLausanneZones(): List<StickerZone> {
        return listOf(
            // McDonald's zones (5 stickers total in 2 zones)
            StickerZone(
                id = "mcdo_1",
                center = LatLng(46.5197, 6.6323), // Ouchy area
                radius = 150.0,
                brandName = "McDonald's",
                stickerCount = 3,
                color = android.graphics.Color.RED
            ),
            StickerZone(
                id = "mcdo_2",
                center = LatLng(46.5250, 6.6280), // Flon area
                radius = 120.0,
                brandName = "McDonald's",
                stickerCount = 2,
                color = android.graphics.Color.RED
            ),
            
            // Nike zones (10 stickers total in 3 zones)
            StickerZone(
                id = "nike_1",
                center = LatLng(46.5220, 6.6350), // Near cathedral
                radius = 180.0,
                brandName = "Nike",
                stickerCount = 4,
                color = android.graphics.Color.BLACK
            ),
            StickerZone(
                id = "nike_2",
                center = LatLng(46.5180, 6.6250), // South of cathedral
                radius = 160.0,
                brandName = "Nike",
                stickerCount = 3,
                color = android.graphics.Color.BLACK
            ),
            StickerZone(
                id = "nike_3",
                center = LatLng(46.5280, 6.6400), // North of cathedral
                radius = 140.0,
                brandName = "Nike",
                stickerCount = 3,
                color = android.graphics.Color.BLACK
            ),
            
            // Sephora zones (15 stickers total in 4 zones)
            StickerZone(
                id = "sephora_1",
                center = LatLng(46.5200, 6.6300), // Cathedral area
                radius = 200.0,
                brandName = "Sephora",
                stickerCount = 4,
                color = android.graphics.Color.MAGENTA
            ),
            StickerZone(
                id = "sephora_2",
                center = LatLng(46.5150, 6.6200), // Ouchy area
                radius = 170.0,
                brandName = "Sephora",
                stickerCount = 4,
                color = android.graphics.Color.MAGENTA
            ),
            StickerZone(
                id = "sephora_3",
                center = LatLng(46.5250, 6.6450), // Flon area
                radius = 160.0,
                brandName = "Sephora",
                stickerCount = 4,
                color = android.graphics.Color.MAGENTA
            ),
            StickerZone(
                id = "sephora_4",
                center = LatLng(46.5300, 6.6350), // North area
                radius = 150.0,
                brandName = "Sephora",
                stickerCount = 3,
                color = android.graphics.Color.MAGENTA
            )
        )
    }
    
    fun getLausanneCenter(): LatLng {
        return LatLng(46.5197, 6.6323)
    }
    
    fun getLausanneBounds(): Pair<LatLng, LatLng> {
        return Pair(
            LatLng(46.5100, 6.6150), // Southwest corner (Ouchy)
            LatLng(46.5350, 6.6500)  // Northeast corner (North of cathedral)
        )
    }
}
