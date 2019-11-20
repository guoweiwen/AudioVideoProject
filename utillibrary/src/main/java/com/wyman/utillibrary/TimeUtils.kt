package com.wyman.utillibrary

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit

/**
 * Created by wyman
 * on 2019-09-13.
 */
val DEFAULT_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
val DATE_FORMAT_DATE = SimpleDateFormat("yyyy-MM-dd")

/**
 * @功能: 获取当前系统时间
 * @描述:
 */
fun getCurrentSystemTime() : String{
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    //获取当前时间
    val date = Date(TimeUtils.now())
    return simpleDateFormat.format(date)
}

/**
 * 获取当前时间方法
 * */
object TimeUtils{
    init {
        scheduleTick()
    }

    private var now : Long = System.currentTimeMillis()

    private fun scheduleTick(){
        ScheduledThreadPoolExecutor(1, ThreadFactory {
            //这里是 Runnable 代码
            val thread = Thread()
            thread.isDaemon = true
            return@ThreadFactory thread
        }).scheduleAtFixedRate({
            now = System.currentTimeMillis()
        },1,1,TimeUnit.MILLISECONDS)
    }

    /**
     * 获取当前时间
     * */
    fun now() : Long{
        return now
    }
}























