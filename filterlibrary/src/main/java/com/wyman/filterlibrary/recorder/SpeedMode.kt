package com.wyman.filterlibrary.recorder

/**
 * 速度模式
 * */
sealed class SpeedMode(type : Int,speed : Float) {
    var type : Int = -1

    var speed : Float = 0f


    object MODE_EXTRA_SLOW : SpeedMode(1,1/3f)//极慢

    object MODE_SLOW : SpeedMode(2,0.5f)//慢

    object MODE_NORMAL : SpeedMode(3,1.0f)//标准

    object MODE_FAST : SpeedMode(4,2.0f)//快

    object MODE_EXTRA_FAST : SpeedMode(5,3.0f)//极快

    companion object{
        fun valueOf(type : Int) : SpeedMode{
            when (type){
                1 -> return MODE_EXTRA_SLOW
                2 -> return MODE_SLOW
                3 -> return MODE_NORMAL
                4 -> return MODE_FAST
                5 -> return MODE_EXTRA_FAST
                else -> return MODE_NORMAL
            }
        }

        fun valueOf(speed : Float) : SpeedMode{
            when (speed){
                1/3f -> return MODE_EXTRA_SLOW
                1/2f -> return MODE_SLOW
                1.0f -> return MODE_NORMAL
                2.0f -> return MODE_FAST
                3.0f -> return MODE_EXTRA_FAST
                else -> return MODE_NORMAL
            }
        }
    }
}























