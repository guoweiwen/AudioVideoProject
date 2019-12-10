package com.wyman.filterlibrary.recorder

import android.media.AudioFormat

class AudioParams {
    companion object{
        const val MIME_TYPE = "audio/mp4a-latm"

        const val SAMPLE_RATE = 44100        // 44.1[KHz] is only setting guaranteed to be available on all devices.

        const val SAMPLE_PER_FRAME = 1024    // AAC, bytes/frame/channel

        const val FRAMES_PER_BUFFER = 25    // AAC, frame/buffer/sec

        const val BIT_RATE = 64000

        const val CHANNEL = AudioFormat.CHANNEL_IN_MONO

        const val CHANNEL_COUNT = 1

        const val BITS_PER_SAMPLE = AudioFormat.ENCODING_PCM_16BIT
    }

    private var mSampleRate: Int = 0    // 采样率
    private var mNbSamples: Int = 0     // 一帧的采样点数
    private var mSpeedMode: SpeedMode       // 速度模式
    private var mAudioPath: String? = null   // 文件名
    private var mMaxDuration: Long = 0  // 最大时长

    init {
        mSampleRate = SAMPLE_RATE
        mNbSamples = SAMPLE_PER_FRAME
        mSpeedMode = SpeedMode.MODE_NORMAL
    }

    fun setSampleRate(sampleRate: Int) {
        this.mSampleRate = sampleRate
    }

    fun getSampleRate(): Int {
        return mSampleRate
    }

    fun setNbSamples(nbSamples: Int) {
        this.mNbSamples = nbSamples
    }

    fun getNbSamples(): Int {
        return mNbSamples
    }

    fun setSpeedMode(mode: SpeedMode) {
        this.mSpeedMode = mode
    }

    fun getSpeedMode(): SpeedMode {
        return mSpeedMode
    }

    fun setAudioPath(audioPath: String) {
        this.mAudioPath = audioPath
    }

    fun getAudioPath(): String? {
        return mAudioPath
    }

    fun setMaxDuration(maxDuration: Long) {
        this.mMaxDuration = maxDuration
    }

    fun getMaxDuration(): Long {
        return mMaxDuration
    }
}