package com.wyman.utillibrary

import android.util.Log

/**
 * kt log日志工具类
 * example：
 *     "erroe message".logE()
 * */

const val NONE = 6
const val ERROR  = 5
const val WARN = 4
const val INFO = 3
const val DEBUG = 2
const val VERBOSE = 1

var printLevel = VERBOSE

private val logTag : String
    get(){
        //获取的是当前线程堆栈的信息，数组坐标4可能就是角标4就是当前调用方法信息
        val element = Thread.currentThread().stackTrace[4]
        //logTag 打印出文件名行数和方法名
        return "(${element.fileName}:${element.lineNumber})${element.methodName}"
    }


fun Any?.logV(){
    if(printLevel <= VERBOSE){
        //toString 为Any对象
        Log.v(logTag,toString())
    }
}

fun Any?.logD(){
    if(printLevel <= DEBUG){
        Log.d(logTag,toString())
    }
}

fun Any?.logI(){
    if(printLevel <= INFO){
        Log.i(logTag,toString())
    }
}

fun Any?.logW(){
    if(printLevel <= WARN){
        Log.w(logTag,toString())
    }
}

fun Any?.logE(){
    if(printLevel <= ERROR){
        Log.e(logTag,toString())
    }
}


fun main(args : Array<String>){
    val elements = Thread.currentThread().stackTrace
    for(element in elements){
        val methodName = element.methodName
        println("MethodName:$methodName")
    }
}























