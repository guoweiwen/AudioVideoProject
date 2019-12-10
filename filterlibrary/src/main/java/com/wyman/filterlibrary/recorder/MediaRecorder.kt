package com.wyman.filterlibrary.recorder

import com.wyman.utillibrary.logE
import java.io.IOException

/**
 * 媒体录制器，支持倍速录制
 * @author CainHuang
 * @date 2019/6/30
 */
class MediaRecorder(// 录制状态回调
        private val mRecordStateListener: OnRecordStateListener?) : OnRecordListener {

    // 音频录制器
    private val mAudioRecorder: AudioRecorder?
    // 视频录制器
    private val mVideoRecorder: VideoRecorder?
    // 是否支持音频录制
    private var mAudioEnable = true
    // 打开的录制器个数
    private var mRecorderCount: Int = 0

    // 处理时长
    private var mProcessTime: Long = 0

    /**
     * 判断是否正在录制阶段
     * @return
     */
    val isRecording: Boolean
        get() = mVideoRecorder?.isRecording ?: false

    init {
        mVideoRecorder = VideoRecorder()
        mVideoRecorder!!.setOnRecordListner(this)

        mAudioRecorder = AudioRecorder()
        mAudioRecorder!!.setOnRecordListner(this)
        mRecorderCount = 0
    }

    /**
     * 释放资源
     */
    fun release() {
        mVideoRecorder!!.release()
        mAudioRecorder!!.release()
    }

    /**
     * 设置是否允许音频录制
     * @param enable
     */
    fun setEnableAudio(enable: Boolean) {
        mAudioEnable = enable
    }

    /**
     * 是否允许录制音频
     * @return
     */
    fun enableAudio(): Boolean {
        return mAudioEnable
    }

    /**
     * 开始录制
     *
     */
    fun startRecord(videoParams: VideoParams, audioParams: AudioParams) {
        " start record".logE()

        mVideoRecorder!!.startRecord(videoParams)

        if (mAudioEnable) {
            try {
                mAudioRecorder!!.prepare(audioParams)
                mAudioRecorder!!.startRecord()
            } catch (e: IOException) {
                "startRecord: ${e.message}".logE()
            }

        }
    }

    /**
     * 停止录制
     */
    fun stopRecord() {
        "stop recording".logE()
        val time = System.currentTimeMillis()
        mVideoRecorder?.stopRecord()
        if (mAudioEnable) {
            if (mAudioRecorder != null) {
                mAudioRecorder!!.stopRecord()
            }
        }
            mProcessTime += System.currentTimeMillis() - time
            "sum of init and release time: $mProcessTime ms".logE()
            mProcessTime = 0
    }

    /**
     * 录制帧可用
     */
    fun frameAvailable(texture: Int, timestamp: Long) {
        mVideoRecorder?.frameAvailable(texture, timestamp)
    }

    /**
     * 录制开始
     * @param type
     */
    override fun onRecordStart(type: MediaType) {
        mRecorderCount++
        // 允许音频录制，则判断录制器打开的个数大于等于两个，则表示全部都打开了
        if (!mAudioEnable || mRecorderCount >= 2) {
            if (mRecordStateListener != null) {
                mRecordStateListener!!.onRecordStart()
                mRecorderCount = 0
            }
        }
    }

    /**
     * 正在录制
     * @param type
     * @param duration
     */
    override fun onRecording(type: MediaType, duration: Long) {
        if (type === MediaType.VIDEO) {
            if (mRecordStateListener != null) {
                mRecordStateListener!!.onRecording(duration)
            }
        }
    }

    /**
     * 录制完成
     * @param info
     */
    override fun onRecordFinish(info: RecordInfo) {
        if (mRecordStateListener != null) {
            mRecordStateListener!!.onRecordFinish(info)
        }
    }

    companion object {

        private val TAG = "MediaRecorder"

        val SECOND_IN_US = 1000000
    }
}
