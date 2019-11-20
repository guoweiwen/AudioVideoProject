package com.wyman.fakedouyin

import android.Manifest
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.wyman.utillibrary.permission.PermissionUtil
import io.fotoapparat.Fotoapparat
import kotlinx.android.synthetic.main.activity_test.*

/**
 * 测试类
 * */
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
            addPermission(Manifest.permission.ACCESS_WIFI_STATE)//正常权限
            addPermission(Manifest.permission.CAMERA)
            permissionRequestSuccess = {
                testTv.text = "授权成功"
            }
            forceAllPermissionsGranted = true
            permissionRequestFail = { _: Array<String>, _: Array<String>, _: Array<String> ->
                Toast.makeText(this@TestActivity,"授权失败",Toast.LENGTH_LONG).show()
            }
            forceDeniedPermissionTips =
                    "请前往设置->应用->【 + ${PermissionUtil.getAppName(this@TestActivity)} + 】->权限中打开相关权限，否则功能无法正常运行！"
        }.startApplyPermission()
        //---------------------------------测试权限类---------------------------------

        Fotoapparat
    }




}
