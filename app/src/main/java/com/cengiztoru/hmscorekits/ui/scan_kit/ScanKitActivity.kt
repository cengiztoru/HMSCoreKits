package com.cengiztoru.hmscorekits.ui.scan_kit

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.cengiztoru.hmscorekits.databinding.ActivityScanKitBinding
import com.cengiztoru.hmscorekits.utils.extensions.isAllPermissionsGranted
import com.cengiztoru.hmscorekits.utils.extensions.showToast
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.hmsscankit.WriterException
import com.huawei.hms.ml.scan.HmsBuildBitmapOption
import com.huawei.hms.ml.scan.HmsScan
import kotlin.random.Random

class ScanKitActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ScanKitActivity"
        private const val REQUEST_CODE_SCAN_ONE = 4112021
        private const val PERMISSION_REQUEST_CODE = 20211104
        private val neededPermissions =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private lateinit var mBinding: ActivityScanKitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityScanKitBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        permissionOperations()
        setListeners()
    }

    //region BARCODE SCANNING
    private fun startDefaultScanView() {
        // QRCODE_SCAN_TYPE and DATAMATRIX_SCAN_TYPE are set for the barcode format, indicating that Scan Kit will support only QR code and Data Matrix.
//        val options = HmsScanAnalyzerOptions.Creator()
//            .setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE).create()
        ScanUtil.startScan(this, REQUEST_CODE_SCAN_ONE, null)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || data == null) {
            return
        }
        if (requestCode == REQUEST_CODE_SCAN_ONE) {
            // Input an image for scanning and return the result.
            val obj = data.getParcelableExtra(ScanUtil.RESULT) as HmsScan?
            obj?.apply {
                parseScanningResult(this)
            }
        }
    }

    private fun parseScanningResult(result: HmsScan) {
        when (result.scanTypeForm) {
            HmsScan.SMS_FORM -> {
                // Parse the data into structured SMS data.
                val smsContent = result.smsContent
                val content = smsContent.msgContent
                val phoneNumber = smsContent.destPhoneNumber
                printLog("smsContent : $content, phoneNumber $phoneNumber")
            }

            HmsScan.WIFI_CONNECT_INFO_FORM -> {
                // Parse the data into structured Wi-Fi data.
                val wifiConnectionInfo = result.wiFiConnectionInfo
                val password = wifiConnectionInfo.password
                val ssidNumber = wifiConnectionInfo.ssidNumber
                val cipherMode = wifiConnectionInfo.cipherMode
                printLog("WIFI_CONNECT_INFO_FORM. password: $password, ssidNumber: $ssidNumber, cipherMode: $cipherMode")
            }

            HmsScan.BOOK_MARK_FORM -> {
                val bookMarkForm = result.bookMarkInfo
            }

            HmsScan.EVENT_INFO_FORM -> {
                val eventInfo = result.eventInfo

            }

            HmsScan.CONTACT_DETAIL_FORM -> {
                val contactDetailForm = result.contactDetail

            }

            HmsScan.DRIVER_INFO_FORM -> {
                val driverInfo = result.driverInfo

            }

            HmsScan.EMAIL_CONTENT_FORM -> {
                val emailContent = result.emailContent

            }
            HmsScan.LOCATION_COORDINATE_FORM -> {
                val locationCoordinate = result.locationCoordinate

            }

            HmsScan.TEL_PHONE_NUMBER_FORM -> {
                val telPhoneNumber = result.telPhoneNumber

            }

            HmsScan.VEHICLEINFO_FORM -> {
                val telPhoneNumber = result.vehicleInfo

            }

//            HmsScan.ARTICLE_NUMBER_FORM -> {
//
//            }
//
//            HmsScan.PURE_TEXT_FORM -> {
//
//            }
//
//            HmsScan.ISBN_NUMBER_FORM -> {
//
//            }
            else -> {
                printLog("originalValue ${result.originalValue}")
                printLog("scanType ${result.scanType}")
                printLog("scanTypeForm ${result.scanTypeForm}")
                printLog("zoomValue ${result.zoomValue}")
            }
        }
    }
//endregion

    //region BARCODE CREATING
    private fun createQrCode(content: String) {
        val type = HmsScan.QRCODE_SCAN_TYPE
        val width = 400
        val height = 400
        val options = HmsBuildBitmapOption.Creator()
            .setBitmapBackgroundColor(Color.WHITE)
            .setBitmapColor(Color.GREEN)
            .setBitmapMargin(Random.nextInt(1, 5))
            .create()
        try {
            // If the HmsBuildBitmapOption object is not constructed, set options to null.
            val qrBitmap = ScanUtil.buildBitmap(content, type, width, height, options)
            mBinding.imgCreatedBarcode.setImageBitmap(qrBitmap)
            printLog("BARCODE CREATED")
        } catch (e: WriterException) {
            printLog("BARCODE CREATION FAILED. Message ${e.localizedMessage}")
        }
    }

//endregion

//region HANDLING RUNTIME PERMISSIONS

    private fun permissionOperations() {
        if (isAllPermissionsGranted(neededPermissions).not()) {
            ActivityCompat.requestPermissions(
                this, neededPermissions,
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE
            && grantResults.size == neededPermissions.size
            && grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        ) {
            printLog("onRequestPermissionsResult: PERMISSIONS GRANTED")
        } else {
            printLog("onRequestPermissionsResult:  USER NOT APPROVED ALL PERMISSIONS")
            showToast("Please grant to all permissions for using services")
        }
    }

//endregion

    private fun setListeners() {
        mBinding.btnDefaultView.setOnClickListener {
            startDefaultScanView()
        }

        mBinding.btnCreateQr.setOnClickListener {
            if (mBinding.etContent.text.isNullOrBlank()) {
                mBinding.tilContent.error = "Please fill QR Code Content"
                return@setOnClickListener
            }
            mBinding.tilContent.error = null
            createQrCode(mBinding.etContent.text.toString())
        }
    }

    private fun printLog(log: String) {
        mBinding.tvLogger.append("$log\n\n")
        Log.i(TAG, log)
    }

}