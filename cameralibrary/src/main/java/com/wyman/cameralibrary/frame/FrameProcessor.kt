package com.wyman.cameralibrary.frame

import com.wyman.cameralibrary.preview.Frame

interface FrameProcessor {
    fun process(frame : Frame)
}