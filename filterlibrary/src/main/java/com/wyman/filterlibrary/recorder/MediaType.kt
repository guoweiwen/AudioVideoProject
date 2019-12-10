package com.wyman.filterlibrary.recorder

sealed class MediaType {
    object AUDIO : MediaType()

    object VIDEO : MediaType()
}