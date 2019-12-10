package com.wyman.filterlibrary.recorder

/**
 * 录制监听器, MediaRecorder内部使用
 */
interface OnRecordListener {
    // 录制开始
    fun onRecordStart(type : MediaType)

    //录制进度
    fun onRecording(type : MediaType,duration : Long)

    //录制完成
    fun onRecordFinish(info : RecordInfo)
}