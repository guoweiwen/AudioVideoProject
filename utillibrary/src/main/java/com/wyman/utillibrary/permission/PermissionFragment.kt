package com.wyman.utillibrary.permission

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
import com.wyman.utillibrary.logE

/**
 * 第一：检查是否已授权
 * 第二：发起授权申请，
 * 第三：处理授权结果
 * 权限申请通过创建空的 fragment
 * */
class PermissionFragment : Fragment() {
    //强制拒绝权限提示信息
    var forceDeniedPermissionMsg: String = ""

    var mPermissions: Array<String>? = null

    var mContext: Context? = null

    //回调函数
    var permissionRequestSuccess: (() -> Unit)? = null
    //有参的回调函数
    var permissionRequestFail: ((grantedPermissions : Array<String>,
                                 normalDeniedPermission : Array<String>,
                                 forceDeniedPermission : Array<String>) -> Unit)? = null

    //强制拒绝权限的提示信息
    var forceDeniedPermissionTips: String? = null
    //是否一直要求所有权限获取 默认为false 不强制设置所有权限通过
    var forceAllPermissionsGranted: Boolean? = false

    companion object {
        //创建 PermissionFragment 对象方法 传入保存权限数组
        fun newInstance(permissions: Array<String>): PermissionFragment {
            val args = Bundle()
            args.putStringArray("permissions", permissions)
            val fragment = PermissionFragment()
            fragment.arguments = args
            return fragment
        }

        //权限申请常量
        const val PERMISSION_REQUEST_CODE = 1001
        //要求跳转setting界面的
        const val REQUEST_PERMISSION_SETTING = 1002
    }

    /**
     * fragment 创建后调用此方法
     * */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        forceDeniedPermissionMsg = "请前往设置->应用->[ ${PermissionUtil.getAppName(mContext!!)} ]->权限中打开相关权限，否则功能无法正常运行！"

        //获取 权限数组
        mPermissions = arguments!!.getStringArray("permissions")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //6.0以下无需提示获得权限
            requestPermissionSuccess()
        } else {
            requestPermission()
        }
    }

    /**
     * 动态申请权限
     * */
    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermission() {
        //记录没有授权的权限
        val deniedPermissions = ArrayList<String>()
        for (index in mPermissions!!.indices) {//遍历申请的权限
            //ContextCompat.checkSelfPermission 查明是否获得特殊的权限
            val check = ContextCompat.checkSelfPermission(activity!!.applicationContext, mPermissions!![index])
            if (check == PackageManager.PERMISSION_GRANTED) {
                // 授权通过 nothing to do
                "该授权已获得".logE()
            } else {
                //记录没有被获得的权限
                deniedPermissions.add(mPermissions!![index])
            }
        }
        if (deniedPermissions.size != 0) {//4
            //有权限未通过
            //ArrayList.toTypedArray() 是将ArrayList转为 数组
            //调用fragment的 requestPermissions 方法
            requestPermissions(deniedPermissions.toTypedArray(), PERMISSION_REQUEST_CODE)
            //ActivityCompat.requestPermissions(activity as Activity,deniedPermissions.toTypedArray(), PERMISSION_REQUEST_CODE)
        } else {
            //权限全通过
            requestPermissionSuccess()
        }
    }

    /**
     * 成功回调函数或6.0以下系统调用
     * */
    private fun requestPermissionSuccess() {
        //跳用回调函数
        permissionRequestSuccess?.invoke()
        //清除当前 fragment
        (mContext!! as FragmentActivity).supportFragmentManager.beginTransaction().remove(this).commit()
    }

    /**
     * @param forceDeniedPermissions 拒绝权限申请数组
     * @param normalDeniedPermissions 正常拒绝权限数组
     * @param grantedPermissions     获得的权限
     * */
    private fun requestPermissionFail(grantedPermissions:Array<String>,
                                      normalDeniedPermissions:Array<String>,
                                      forceDeniedPermissions:Array<String>) {
        //权限请求失败回调
        permissionRequestFail?.invoke(grantedPermissions,normalDeniedPermissions,forceDeniedPermissions)
        //清除当前 fragment
        (mContext!! as FragmentActivity).supportFragmentManager.beginTransaction().remove(this).commit()
    }

    /**
     * PermissionUtil 调用的
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
     * fragment requestPermissions
     * 申请权限回调函数
     * */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_REQUEST_CODE){//4，显示2个给用户选，返回数组4个中2个否定
            //记录点击了不再提醒的未授权权限  永久拒绝的权限
            val forceDeniedPermissions = ArrayList<String>()
            //记录点击了普通的未授权权限 临时拒绝的权限
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
                    //授权拒绝  ActivityCompat.shouldShowRequestPermissionRationale 显示拒绝的基本原因
                    if(!ActivityCompat.shouldShowRequestPermissionRationale(mContext!! as Activity,permission)){
                        //用户勾选了不再提示
                        forceDeniedPermissions.add(permission)//返回false不显示
                    } else {
                        //用户仅仅点击了拒绝 未勾选不再提示
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

