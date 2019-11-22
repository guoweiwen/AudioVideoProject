package com.wyman.cameralibrary.result

import android.graphics.BitmapFactory

/**
 * 图片封装类
 * */
data class Photo (
        /**
         * 编码图片 通过 BitmapFactory.decodeByteArray 解码
         * */
        @JvmField
        val encodeedImage : ByteArray,

        /**
         *  相对于拍摄照片时屏幕方向的顺时针旋转。要以正确的方向显示照片，需要按此值逆时针旋转照片。
         * */
        @JvmField
        val rotationDegress : Int
){
    //图片的高
    val height by lazy {
        decodeBounds.height
    }

    //图片的宽
    val width by lazy {
        decodeBounds.width
    }

    private val decodeBounds by lazy {
        //只获取图片信息所有 inJustDecodeBounds 为 true
        BitmapFactory.decodeByteArray(
                encodeedImage,
                0,
                encodeedImage.size,
                BitmapFactory.Options().apply {//apply 是返回处理完的对象
                    inJustDecodeBounds = true
                }
        )
    }

    //重写 equals 都要重写 hashCode ， hashCode一般map用到比较两对象是否一样

    //系统生成的 equals 方法
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Photo

        if (!encodeedImage.contentEquals(other.encodeedImage)) return false
        if (rotationDegress != other.rotationDegress) return false

        return true
    }

    //系统生成的 hashCode 方法
    override fun hashCode(): Int {
        var result = encodeedImage.contentHashCode()
        result = 31 * result + rotationDegress
        return result
    }

    companion object{
        private val EMPTY by lazy {
            Photo(encodeedImage = ByteArray(0),rotationDegress = 0)
        }

        /**
         * 返回空的图片
         * */
        internal fun empty() : Photo = EMPTY
    }
}










