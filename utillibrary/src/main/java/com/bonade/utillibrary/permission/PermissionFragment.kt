package com.bonade.utillibrary.permission

import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

class PermissionFragment : Fragment() {
    var forceDeniedPermissionMsg: String = ""

    var mPermissions: Array<String>? = null

    var mContext: Context? = null

    var permissionRequestSuccess: (() -> Unit)? = null
    var permissionRequestFail: ((grantedPermissions : Array<String>,
                                 normalDeniedPermission : Array<String>,
                                 forceDeniedPermission : Array<String>) -> Unit)? = null

    var forceDeniedPermissionTips: String? = null
    var forceAllPermissionsGranted: Boolean? = null

    companion object {
        fun newInstance(permissions: Array<String>): PermissionFragment {
            val args = Bundle()
            args.putStringArray("permissions", permissions)
            val fragment = PermissionFragment()
            fragment.arguments = args
            return fragment
        }

        const val PERMISSION_REQUEST_CODE = 1001
        const val REQUEST_PERMISSION_SETTING = 1002
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        forceDeniedPermissionMsg = "请前往设置->应用->[ ${PermissionUtil.getAppName(mContext!!)} ]->权限中打开相关权限，否则功能无法正常运行！"

        mPermissions = arguments!!.getStringArray("permissions")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //5.0
            requestPermissionSuccess()
        } else {
            requestPermission()
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermission() {
        //记录没有授权的权限
        val deniedPermissions = ArrayList<String>()
        for (index in mPermissions!!.indices) {
            val check = ContextCompat.checkSelfPermission(activity!!.applicationContext, mPermissions!![index])
            if (check == PackageManager.PERMISSION_GRANTED) {
                // 授权通过 nothing to do
            } else {
                deniedPermissions.add(mPermissions!![index])
            }
        }
        if (deniedPermissions.size != 0) {
            //有权限未通过
            //val arr = arrayOf(deniedPermissions) as Array<String>
            requestPermissions(deniedPermissions.toTypedArray(), PERMISSION_REQUEST_CODE)
        } else {
            //权限全通过
            requestPermissionSuccess()
        }
    }

    /**
     * 回调函数
     * */
    private fun requestPermissionSuccess() {
        permissionRequestSuccess?.invoke()
        (mContext!! as FragmentActivity).supportFragmentManager.beginTransaction().remove(this).commit()
    }

    /**
     * @param forceDeniedPermissions
     * @param normalDeniedPermissions
     * @param grantedPermissions
     * */
    private fun requestPermissionFail(grantedPermissions:Array<String>,
                                      normalDeniedPermissions:Array<String>,
                                      forceDeniedPermissions:Array<String>) {
        permissionRequestFail?.invoke(grantedPermissions,normalDeniedPermissions,forceDeniedPermissions)
        (mContext!! as FragmentActivity).supportFragmentManager.beginTransaction().remove(this).commit()
    }

    /**
     * 开始请求权限
     * */
    fun start(activity: FragmentActivity) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            return
        }
        mContext = activity
        activity.supportFragmentManager.beginTransaction().add(this, activity.javaClass.name).commit()
    }

    /**
     * 打开Android系统设置界面
     * */
    fun openSettingPage(){
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package",mContext!!.packageName,null)
        intent.data = uri
        startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
    }

    /**
     * 设置界面回调函数
     * */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_PERMISSION_SETTING){
            //设置页面回调
            requestPermission()
        }
    }

    /**
     * 权限申请回调函数
     * */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_REQUEST_CODE){
            //记录点击了不再提醒的未授权权限
            val forceDeniedPermissions = ArrayList<String>()
            //记录点击了普通的未授权权限
            val normalDeniedPermissions = ArrayList<String>()
            //记录点击了获得权限
            val grantedPermissions = ArrayList<String>()
            for (index in grantResults.indices){//遍历
                //grantResults 为需要获取权限列表的结果
                val grantResult = grantResults[index]
                //获取的权限
                val permission = permissions[index]
                if(grantResult == PackageManager.PERMISSION_GRANTED){
                    //授权通过
                    grantedPermissions.add(permission)
                } else {
                    //授权拒绝
                    if(!ActivityCompat.shouldShowRequestPermissionRationale(mContext!! as Activity,permission)){
                        forceDeniedPermissions.add(permission)
                    } else {
                        normalDeniedPermissions.add(permission)
                    }
                }
            }
            if(forceDeniedPermissions.size == 0 && normalDeniedPermissions.size == 0){
                //全部通过
                requestPermissionSuccess()
            } else {
                //部分授权通过 如果用户希望一直提示授权直到给权限位置 那么就一直去请求权限
                if(forceAllPermissionsGranted!!){
                    if(normalDeniedPermissions.size != 0){
                        requestPermission()
                    } else {
                        //所有没有通过的权限都是用户点击了不再提示的
                        AlertDialog.Builder(mContext)
                                .setTitle("警告")
                                .setMessage(forceDeniedPermissionMsg)
                                .setCancelable(false)
                                //kotlin 简写由于接口有两个参数且两个参数没有用所以用 "_"替代
                                .setPositiveButton("确定") { _: DialogInterface, _: Int ->
                                    openSettingPage()
                                }.show()
                    }
                } else {
                    for(index in mPermissions!!.indices ){
                        if(grantedPermissions.contains(mPermissions!![index])
                                || normalDeniedPermissions.contains(mPermissions!![index])
                                || forceDeniedPermissions.contains(mPermissions!![index])){

                        } else {
                            //如果三者都不包含他 包名这个权限不是隐私权限 直接给就完事了 所以要放到已授权的权限列表里面去
                            grantedPermissions.add(mPermissions!![index])
                        }
                    }
                    requestPermissionFail(grantedPermissions.toTypedArray(),
                            normalDeniedPermissions.toTypedArray(),
                            forceDeniedPermissions.toTypedArray())
                }
            }
        }
    }
}

