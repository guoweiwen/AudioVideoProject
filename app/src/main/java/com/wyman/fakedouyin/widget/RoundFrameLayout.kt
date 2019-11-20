package com.wyman.fakedouyin.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Region
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import com.wyman.fakedouyin.utils.dp2px

/**
 * Created by wyman
 * on 2019-07-03.
 * 圆角 FrameLayout
 */
class RoundFrameLayout @JvmOverloads constructor(context : Context, attrs : AttributeSet?,
                                                 defStyleAttr:Int=0):FrameLayout(context,attrs,defStyleAttr){

    override fun dispatchDraw(canvas: Canvas?) {
        val path = Path()
        path.addRoundRect(RectF(0f,0f,measuredWidth.toFloat(),measuredHeight.toFloat())
            , dp2px(8),dp2px(8),Path.Direction.CW)
        //版本兼容不然报错
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            canvas!!.clipPath(path)
        }else {
            canvas!!.clipPath(path, Region.Op.REPLACE)
        }
        super.dispatchDraw(canvas)
    }

    //记录当前点击的坐标
    private var nowX : Float = 0f
    private var nowY : Float = 0f
    //记录下压的坐标
    private var downX : Float = 0f
    private var downY : Float = 0f
    //上一次坐标
    private var lastX : Float = 0f
    private var lastY : Float = 0f
    //记录移动的坐标
    private var moveX : Float = 0f
    private var moveY : Float = 0f
    //横向滑动距离
    var offsetX : Float = 0f
    var offsetY : Float = 0f

//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        //当次点击的坐标
//        nowX = event!!.rawX
//        nowY = event!!.rawY
//        when (event!!.action){
//            MotionEvent.ACTION_DOWN -> {
//                downX = nowX
//                downY = nowY
//                lastX = nowX
//                lastY = nowY
//            }
//            MotionEvent.ACTION_MOVE -> {
//                moveX = nowX
//                moveY = nowY
//                //向左滑动负数；向右滑动正数
//                val vecDistanceX = moveX - downX
//                val vecDistanceY = moveY - downY
//                if(vecDistanceX > vecDistanceY){//横滑
//                    if(vecDistanceX > 0){
//                        //向右滑动看之前有没有滤镜:没有就不移动
//                    } else {
//                        offsetX = moveX - lastX
//                        offsetY = moveY - lastY
//                        if(onTouchScroller != null){
//                            onTouchScroller!!.moveLeft(offsetX,offsetY)
//                        }
//                    }
//                }
//                lastX = nowX
//                lastY = nowY
//            }
//        }
//        return super.onTouchEvent(event)
//    }

    private var onTouchScroller : OnTouchScroller? = null
    interface OnTouchScroller{
        fun moveLeft(moveX:Float,moveY:Float)
        fun moveRight(moveX:Float,moveY:Float)
    }
    fun setOnTouchScroller(onTouchScroller : OnTouchScroller){
        this.onTouchScroller = onTouchScroller
    }

    companion object{
        const val TAG : String = "RoundFrameLayout"
    }
}