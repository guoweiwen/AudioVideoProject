package com.wyman.filterlibrary.recorder

import android.opengl.EGLContext

/**
* 视频参数
*/
class VideoParams{
    var mSpeedMode : SpeedMode
    var mBitRate : Int = 0

    var mVideoWidth : Int = -1
    var mVideoHeight : Int = -1

    var mVideoPath : String? = null
    var mMaxDuration : Long = -1
    var mEglContext : EGLContext? = null

    init {
        mBitRate = BIT_RATE
        mSpeedMode = SpeedMode.MODE_NORMAL
    }

    fun setVideoPath(fileName: String): VideoParams {
        this.mVideoPath = fileName
        return this
    }

    fun getVideoPath(): String? {
        return mVideoPath
    }

    fun setVideoWidth(width: Int): VideoParams {
        this.mVideoWidth = width
        return this
    }

    fun getVideoWidth(): Int {
        return mVideoWidth
    }

    fun setVideoHeight(height: Int): VideoParams {
        this.mVideoHeight = height
        return this
    }

    fun getVideoHeight(): Int {
        return mVideoHeight
    }

    fun setBitRate(bitRate: Int): VideoParams {
        this.mBitRate = bitRate
        return this
    }

    fun getBitRate(): Int {
        return mBitRate
    }

    fun setSpeedMode(mode: SpeedMode): VideoParams {
        this.mSpeedMode = mode
        return this
    }

    fun getSpeedMode(): SpeedMode {
        return mSpeedMode
    }

    fun setMaxDuration(maxDuration: Long): VideoParams {
        this.mMaxDuration = maxDuration
        return this
    }

    fun getMaxDuration(): Long {
        return mMaxDuration
    }

    fun setEglContext(context: EGLContext): VideoParams {
        this.mEglContext = context
        return this
    }

    fun getEglContext(): EGLContext? {
        return mEglContext
    }

    override fun toString(): String {
        return "VideoParams:  + $mVideoWidth x $mVideoHeight @ $mBitRate to $mVideoPath"
    }


    companion object{
        // 录制视频的类型
        const val MIME_TYPE = "video/avc"

        //帧率
        const val FRAME_RATE = 25

        //I帧时长
        const val I_FRAME_INTERVAL = 1

        /**
         * 16*1000 bps：可视电话质量
         * 128-384 * 1000 bps：视频会议系统质量
         * 1.25 * 1000000 bps：VCD质量（使用MPEG1压缩）
         * 5 * 1000000 bps：DVD质量（使用MPEG2压缩）
         * 8-15 * 1000000 bps：高清晰度电视（HDTV） 质量（使用H.264压缩）
         * 29.4  * 1000000 bps：HD DVD质量
         * 40 * 1000000 bps：蓝光光碟质量（使用MPEG2、H.264或VC-1压缩）
         */
        const val BIT_RATE = 15 * 1000000
    }
}