package com.bonade.fakedouyin

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bonade.fakedouyin.common.callback.PermissionListener
import com.bonade.fakedouyin.photographalbum.ImageSplitter
import com.bonade.utillibrary.permission.PermissionUtil
import kotlinx.android.synthetic.main.activity_test.*
import java.io.File
import java.io.InputStream

class TestActivity : BaseActivity() {
    private lateinit var testTv : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        testTv = test_tv
        testTv.text = "测试权限"

        //测试权限类
        PermissionUtil.generate(this){
            addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            addPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            addPermission(Manifest.permission.CALL_PHONE)
            addPermission(Manifest.permission.ACCESS_WIFI_STATE)
            addPermission(Manifest.permission.CAMERA)
            permissionRequestSuccess = {
                testTv.text = "授权成功"
            }
            permissionRequestFail = { grantedPermissions: Array<String>, deniedPermissions: Array<String>, forceDeniedPermissions: Array<String> ->
                //TODO  有些权限不用申请但放在 强迫否认数组
                Toast.makeText(this@TestActivity,"授权失败",Toast.LENGTH_LONG).show()
            }
            forceDeniedPermissionTips = "请前往设置->应用->【 + ${PermissionUtil.getAppName(this@TestActivity)} + 】->权限中打开相关权限，否则功能无法正常运行！\""
            startApplyPermission()
        }
    }

    private fun refreshPermissionStatus() {
        handlePermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE
        ,Manifest.permission.WRITE_EXTERNAL_STORAGE),object : PermissionListener {

            override fun onGranted() {

            }

            override fun onDenied(deniedPermissions: List<String>) {
                //无法获取权限 退出当前activity
//                finish()
            }
        })
    }


}
