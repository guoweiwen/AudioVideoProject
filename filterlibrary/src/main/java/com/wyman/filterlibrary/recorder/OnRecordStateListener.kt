package com.wyman.filterlibrary.recorder

/**
 * 录制状态监听
 * @author CainHuang
 * @date 2019/6/30
 */
interface OnRecordStateListener {

    // 录制开始
    fun onRecordStart()

    // 录制进度
    fun onRecording(duration: Long)

    // 录制结束
    fun onRecordFinish(info: RecordInfo)
}
