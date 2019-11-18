package com.bonade.fakedouyin

import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bonade.fakedouyin.common.callback.PermissionListener
import com.bonade.fakedouyin.utils.ActivityCollector
import com.bonade.fakedouyin.utils.AndroidVersion
import java.lang.ref.WeakReference

/**
 * Created by wyman
 * on 2019-07-03.
 * Activiyt 的基类
 */
open class BaseActivity : AppCompatActivity(){
    //当前Activity
    protected var activity : Activity? = null
    //权限接口
    private var mListener : PermissionListener? = null

    private var weakRefActivity : WeakReference<Activity>? = null
    private var isActive : Boolean? = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        //弱引用
        weakRefActivity = WeakReference(this)
        ActivityCollector.add(weakRefActivity)
    }

    override fun onResume() {
        super.onResume()
        isActive = true
    }

    override fun onPause() {
        super.onPause()
        isActive = false
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.remove(weakRefActivity)
    }

    /**
     * 将状态栏设置成透明。只适配Android 5.0以上系统的手机。
     */
    protected fun transparentStatusBar(){
//        if(AndroidVersion.hasLollipop()){
//            val decorView = window.decorView
//            decorView.systemUiVisibility =
//                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//            //如果系统是上面两个标签就设置透明状态栏
//            window.statusBarColor = Color.TRANSPARENT
//        }
    }

    /**
     * 检查和处理运行时权限，并将用户授权的结果通过PermissionListener进行回调。
     *
     * @param permissions
     * 要检查和处理的运行时权限数组
     * @param listener
     * 用于接收授权结果的监听器
     */
    protected fun handlePermission(permissions: Array<String>?,listener : PermissionListener){
        if(permissions == null || activity == null){
            return
        }
        mListener = listener
        val requestPermissionList = ArrayList<String>()
        for(permission in permissions){
            if(ContextCompat.checkSelfPermission(activity!!,permission) != PackageManager.PERMISSION_GRANTED){
                //用户不同意的通过集合收集
                requestPermissionList.add(permission)
            }
        }
        if(!requestPermissionList.isEmpty()){
            ActivityCompat.requestPermissions(activity!!,requestPermissionList.toTypedArray(),1)
        } else {
            //动态权限申请成功
            listener.onGranted()
        }
    }

    open fun permissionGranted(){
        //子类具体实现
    }

    /**
     * 获取权限返回的结果
     * */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1 -> if(grantResults.isNotEmpty()){
                val deniedPermissions = ArrayList<String>()
                for(i in grantResults.indices){
                    val grantResult = grantResults[i]
                    val permission = permissions[i]
                    if(grantResult != PackageManager.PERMISSION_GRANTED){
                        deniedPermissions.add(permission)
                    }
                }
                if(deniedPermissions.isEmpty()){
                    mListener!!.onGranted()
                } else {
                    mListener!!.onDenied(deniedPermissions)
                }
            }
        }
    }

    protected fun setupToolbar(){

    }

    companion object{
        private const val TAG = "BaseActivity"
    }
}






















