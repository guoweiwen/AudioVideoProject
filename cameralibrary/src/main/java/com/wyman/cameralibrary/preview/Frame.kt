package com.wyman.cameralibrary.preview

import java.util.*

/**
 * Created by wyman
 * on 2019-09-07.
 * 摄像头预览数据封装对象
 */
data class Frame (
        //该帧的分辨率
        val size : Int,//Resolution,
        //该帧的数据 NV21 格式
        val image : ByteArray,
        //旋转角度
        val rotation : Int
){
    override fun toString(): String {
        return "Frame(size=$size, " +
                "image=${image.size}, " +
                "rotation=$rotation)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Frame

        if (size != other.size) return false
        if (!image.contentEquals(other.image)) return false
        if (rotation != other.rotation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = size.hashCode()
        result = 31 * result + Arrays.hashCode(image)
        result = 31 * result + rotation
        return result
    }


}