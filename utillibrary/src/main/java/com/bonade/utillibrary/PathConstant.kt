package com.bonade.utillibrary

import android.content.Context
import android.os.Environment
import java.io.File

/**
 * Created by wyman
 * on 2019-09-13.
 * 路径常量工具类
 */

/**
 * 获取图片缓存绝对路径
 * @param context
 * @return
 */
fun getImageCachePath(context : Context?) : String{
    val directoryPath =
            if(Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()){
                // 判断外部存储是否可用，如果不可用则使用内部存储路径
                context?.externalCacheDir?.absolutePath
            }else {
                // 使用内部存储缓存目录
                context?.cacheDir?.absolutePath
            }
    val path = "$directoryPath${File.separator}wyman_camera${TimeUtils.now()}.jpeg"
    return path
}

/**
 * 获取视频缓存绝对路径
 * @param context
 * @return
 */
fun getVideoCachePath(context: Context?) : String{
    val directoryPath = if(Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()){
        //判断外部存储是否可用，如果不可用则使用内部存储路径
        context?.externalCacheDir?.absolutePath
    } else {
        //使用内部存储缓存目录
        context?.cacheDir?.absolutePath
    }
    val path = "$directoryPath${File.separator}wyman_camera${TimeUtils.now()}.mp4"
    val file = File(path)
    if(!file.parentFile.exists()){
        file.parentFile.mkdirs()
    }
    return path
}























