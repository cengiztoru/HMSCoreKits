package com.cengiztoru.hmscorekits.ui.scan_kit

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.cengiztoru.hmscorekits.databinding.ActivityScanKitBinding
import com.cengiztoru.hmscorekits.utils.extensions.isAllPermissionsGranted
import com.cengiztoru.hmscorekits.utils.extensions.showToast
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan

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
                printLog("originalValue $originalValue\n")
                printLog("showResult $showResult\n")
                printLog("scanType $scanType\n")
                printLog("scanTypeForm $scanTypeForm\n")
                printLog("zoomValue $zoomValue\n")
            }
        }
    }

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
    }

    private fun printLog(log: String) {
        mBinding.tvLogger.append("$log\n\n")
        Log.i(TAG, log)
    }

}