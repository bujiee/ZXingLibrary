package com.bu.zxinglibrary

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View

import com.bj.qrcodelibrary.CaptureActivity
import com.bj.qrcodelibrary.QRCodeIntent
import com.bu.zxinglibrary.util.ToastUtil

class KotlinMainActivity : AppCompatActivity() {
    private val REQUEST_CODE = 10
    private var jumpCode = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun qrCodeScan(v: View) {
        jumpCode = 0
        requestPermission(CaptureActivity::class.java)
    }

    fun qrCodeCreate(v: View) {
        jumpCode = 1
        requestPermission(CreateQRCodeActivity::class.java)
    }

    fun qrCodePicture(v: View) {
        jumpCode = 2
        requestPermission(PictureIdentificationActivity::class.java)
    }

    /*权限申请,相机，存储，蜂鸣*/
    private fun requestPermission(activity: Class<*>) {
        val permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.VIBRATE, Manifest.permission.CAMERA)
        var flag = true
        for (aPermission in permission) {
            if (ActivityCompat.checkSelfPermission(this, aPermission) != PackageManager.PERMISSION_GRANTED) {
                flag = false
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, aPermission)) {
                    ToastUtil.showToast(this, "您已禁止所需要权限，需要重新开启")
                } else {
                    ActivityCompat.requestPermissions(this, permission, REQUEST_CODE)
                }
            } else {
                flag = flag and flag
            }
        }
        if (flag) {
            val intent = Intent(this, activity)
            if (jumpCode == 0) {
                //预览框的宽高
                intent.putExtra(QRCodeIntent.FRAME_WIDTH, 200)
                intent.putExtra(QRCodeIntent.FRAME_HEIGHT, 180)
                intent.putExtra(QRCodeIntent.SET_RESULT, true)
                startActivityForResult(intent, 10)
            } else {
                startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            var flag = true
            for (grantResult in grantResults) {
                flag = if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    false
                } else {
                    flag && flag
                }
            }

            if (flag) {
                val intent = Intent()
                when (jumpCode) {
                    0 -> intent.setClass(this, CaptureActivity::class.java)
                    1 -> intent.setClass(this, CreateQRCodeActivity::class.java)
                    2 -> intent.setClass(this, PictureIdentificationActivity::class.java)
                    else -> {
                    }
                }
                if (jumpCode >= 0 && grantResults.isNotEmpty())
                    startActivity(intent)
            } else {
                ToastUtil.showToast(this, "您已禁止所需要权限，需要重新开启")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (REQUEST_CODE == requestCode) {
            //接收返回值
            if (data != null && !TextUtils.isEmpty(data.getStringExtra(QRCodeIntent.SCAN_RESULT))) {
                ToastUtil.showToast(this, data.getStringExtra(QRCodeIntent.SCAN_RESULT))
            }
        }
    }
}
