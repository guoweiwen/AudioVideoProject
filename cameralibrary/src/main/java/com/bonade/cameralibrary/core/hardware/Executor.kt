package com.bonade.cameralibrary.core.hardware

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors

/**
 * Created by wyman
 * on 2019-09-13.
 */
//单线程
private val loggingExecutor = Executors.newSingleThreadExecutor()

//主线程 Handler
private val mainThreadHandler = Handler(Looper.getMainLooper())


internal val pendingResultExecuteor = Executors.newSingleThreadExecutor()

//线程执行 帧预览数据
internal val frameProcessingExecutors = Executors.newSingleThreadExecutor()

//在工作线程执行 一个方法
internal fun executeLoggingThread(function : ()->Unit) = loggingExecutor.execute{
    function()
}

/**
 * 参数类型是一个 无参无返回值的方法
 * 调用该方法的时候传递一个 方法类型为 ： void fun(){} 的方法
 * 在 Handler上执行
 * */
internal fun executeMainThread(function : ()->Unit) = mainThreadHandler.post{
    function()
}



