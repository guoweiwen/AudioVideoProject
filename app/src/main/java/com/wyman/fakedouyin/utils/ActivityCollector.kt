package com.wyman.fakedouyin.utils

import android.app.Activity
import java.lang.ref.WeakReference

/**
 * 管理应用中所有的 Activity，可用于一键杀死所有Activity
 * Created by wyman
 * on 2019-07-03.
 */
object ActivityCollector {
    private const val TAG = "ActivityCollector"

    private val activityList = ArrayList<WeakReference<Activity>?>()

    fun size() : Int{
        return activityList.size
    }

    fun add(weakRefActivity : WeakReference<Activity>?){
        activityList.add(weakRefActivity)
    }

    fun remove(weakRefActivity: WeakReference<Activity>?){
        val result = activityList.remove(weakRefActivity)

    }

    fun finishAll(){
        if(activityList.isNotEmpty()){
            for(activityReference in activityList){
                val activity = activityReference?.get()
                if(activity != null && !activity.isFinishing){
                    activity.finish()
                }
            }
            activityList.clear()
        }
    }
}























