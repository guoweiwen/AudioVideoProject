package com.bonade.utillibrary.permission

import android.annotation.SuppressLint
import android.content.Context
import androidx.collection.ArrayMap
import androidx.fragment.app.FragmentActivity
import androidx.versionedparcelable.VersionedParcelize
import java.util.ArrayList

/**
 * example:
 *
 * */
/**
 * 第一：检查是否已授权
 * 第二：发起授权申请，
 * 第三：处理授权结果
 *
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
        /*
         *   为了让java使用和原本一样
         *   PermissionUtil.generate();
         *   非：
         *   PermissionUtil.Companion.generate();
         */
        @JvmStatic
        fun generate(activity: Context,
                /*body为PermissionUtil对象*/body : PermissionUtil.()->Unit) {
            return with(PermissionUtil(activity)){
                body()//返回 PermissionUtil
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
    var configMap : ArrayMap<Int,String> = ArrayMap()

    init{
       mContext = activity as FragmentActivity
       configMap[FORCE_DENIED_PERMISSIONTIPS] = ""
       configMap[FORCE_ALL_PERMISSIONS_GRANTED] = "false"
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
        //also 实现builder模式
        PermissionFragment.newInstance(permissions.toTypedArray()).also { //also 返回PermissionFragment
            it.permissionRequestSuccess = permissionRequestSuccess
            it.permissionRequestFail = permissionRequestFail
            it.forceDeniedPermissionTips = configMap[FORCE_DENIED_PERMISSIONTIPS]
            it.forceAllPermissionsGranted = configMap[FORCE_ALL_PERMISSIONS_GRANTED]?.toBoolean()
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















