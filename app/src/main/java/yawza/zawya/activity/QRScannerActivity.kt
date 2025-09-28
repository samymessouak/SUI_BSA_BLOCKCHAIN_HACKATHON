package yawza.zawya.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import yawza.zawya.R
import yawza.zawya.databinding.ActivityQrScannerBinding
import yawza.zawya.utils.QRCodeValidator

class QRScannerActivity : AppCompatActivity(), PermissionListener {

    private lateinit var binding: ActivityQrScannerBinding
    private lateinit var barcodeView: DecoratedBarcodeView
    private var isScanning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupScanner()
        setupUI()
        requestCameraPermission()
    }

    private fun setupScanner() {
        barcodeView = binding.barcodeScanner
        barcodeView.decodeContinuous(callback)
    }

    private fun setupUI() {
        // Back button
        binding.fabBack.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private val callback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (!isScanning) return
            
            isScanning = false
            barcodeView.pause()
            
            // Validate QR code
            val qrData = result.text
            val validationResult = QRCodeValidator.validateStickerQR(qrData)
            
            if (validationResult.isValid) {
                // Return result to calling activity
                val intent = Intent().apply {
                    putExtra("sticker_id", validationResult.stickerId)
                    putExtra("zone_id", validationResult.zoneId)
                    putExtra("brand_name", validationResult.brandName)
                    putExtra("qr_data", qrData)
                }
                setResult(RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this@QRScannerActivity, "Invalid QR code: ${validationResult.error}", Toast.LENGTH_LONG).show()
                // Resume scanning after showing error
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    resumeScanning()
                }, 2000)
            }
        }

        override fun possibleResultPoints(resultPoints: MutableList<com.google.zxing.ResultPoint>?) {
            // Optional: Handle possible result points
        }
    }

    private fun requestCameraPermission() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(this)
            .check()
    }

    override fun onPermissionGranted(response: PermissionGrantedResponse) {
        startScanning()
    }

    override fun onPermissionDenied(response: PermissionDeniedResponse) {
        Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onPermissionRationaleShouldBeShown(
        permission: PermissionRequest,
        token: PermissionToken
    ) {
        token.continuePermissionRequest()
    }

    private fun startScanning() {
        isScanning = true
        barcodeView.resume()
    }

    private fun resumeScanning() {
        isScanning = true
        barcodeView.resume()
    }

    override fun onResume() {
        super.onResume()
        if (isScanning) {
            barcodeView.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        super.onBackPressed()
    }
}
