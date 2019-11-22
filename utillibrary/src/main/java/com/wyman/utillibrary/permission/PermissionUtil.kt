package com.wyman.utillibrary.permission

import android.annotation.SuppressLint
import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.versionedparcelable.VersionedParcelize
import java.util.ArrayList

/**
 * example:
 *  PermissionUtil.generate(this){//另外四个都是需要申请
        addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        addPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        addPermission(Manifest.permission.CALL_PHONE)
        addPermission(Manifest.permission.ACCESS_WIFI_STATE)//正常权限
        addPermission(Manifest.permission.CAMERA)
        permissionRequestSuccess = {
            testTv.text = "授权成功"
        }
        permissionRequestFail = { grantedPermissions: Array<String>, deniedPermissions: Array<String>, forceDeniedPermissions: Array<String> ->
            //TODO  有些权限不用申请但放在 强迫否认数组
            Toast.makeText(this@TestActivity,"授权失败",Toast.LENGTH_LONG).show()
        }
        forceDeniedPermissionTips = "请前往设置->应用->【 + ${PermissionUtil.getAppName(this@TestActivity)} + 】->权限中打开相关权限，否则功能无法正常运行！\""
    }.startApplyPermission()
 * */
/**
 * 注意！！！
 * 动态申请权限时，申请的权限在 Manifest 要存在否则动态申请时会不出现的
 * 下面的方法可以通过注解实现 Parcel 接口
 * @SuppressLint("ParcelCreator")
 * @VersionedParcelize
 * 主项目添加如下：
 * androidExtensions {
 *  experimental = true
 * }
 * */
@SuppressLint("ParcelCreator")
@VersionedParcelize
open class PermissionUtil internal constructor(private var activity: Context)/*(private val activity: FragmentActivity)*/{
    companion object{
        /**
         * 饿汉单例
         * */
        /**
         *   为了让java使用和原本一样
         *   PermissionUtil.generate();
         *   非：
         *   PermissionUtil.Companion.generate();
         */
        @JvmStatic
        fun generate(activity: Context,
                /*body为PermissionUtil对象*/body : PermissionUtil.()->Unit) : PermissionUtil{
            with(PermissionUtil(activity)){
                body()//返回 PermissionUtil
                return this
            }
        }

        /**
         * 获取app应用名称
         * @param context
         * @return app名称
         * */
        fun getAppName(context: Context) : String{
            //获取包管理器
            val packageManager = context.packageManager
            //通过包管理器获取包信息
            val packageInfo = packageManager.getPackageInfo(context.packageName,0)
            //通过包信息获取应用信息
            val applicationInfo = packageInfo.applicationInfo
            //通过应用信息获取app logo的资源id
            val labelRes = applicationInfo.labelRes
            return context.resources.getString(labelRes)
        }

        const val FORCE_ALL_PERMISSIONS_GRANTED = 100000
        const val FORCE_DENIED_PERMISSIONTIPS = 100001
    }

    private var mContext : FragmentActivity? = null

    init{
       mContext = activity as FragmentActivity
    }

    //回调函数
    var permissionRequestSuccess : (()->Unit)? = null
    var permissionRequestFail : ((grantedPermissions : Array<String>,
                                  normalDeniedPermission : Array<String>,
                                  forceDeniedPermission : Array<String>)->Unit)? = null

    var permissions : MutableList<String> = ArrayList()

    var forceAllPermissionsGranted: Boolean? = null

    var forceDeniedPermissionTips: String? = null


    /**
     * 开始申请权限
     * */
    fun startApplyPermission(){
        //also 与 apply 不同在于 also后再链式调用对象不变；apply：对象发生变化；
        //相同就是都返回 该调用对象
        PermissionFragment.newInstance(permissions.toTypedArray()).also { //also 返回PermissionFragment
            it.permissionRequestSuccess = permissionRequestSuccess
            it.permissionRequestFail = permissionRequestFail
            it.forceDeniedPermissionTips = forceDeniedPermissionTips
            it.forceAllPermissionsGranted = forceAllPermissionsGranted
        }.start(mContext!!)
    }

    /**
     * 添加权限
     * */
    fun addPermission(permission : String){
        if(!permissions.contains(permission)){
            permissions.add(permission)
        }
    }
}















