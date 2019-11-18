package com.bonade.fakedouyin.widget

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.bonade.fakedouyin.R
import com.bonade.fakedouyin.utils.dp2px
import kotlin.math.min

/**
 * Created by wyman
 * on 2019-07-03.
 * 仿抖音录制按钮
 * 当初思路通过属性动画更改 strokeWidth 的值令到空心圆粗度变化 整个按钮用两个圆实现，内圆变正方形
 * 网上用 Xfermode 的 PorterDuffXfermode(PorterDuff.Mode.DST_OUT) 效果：重叠部分去除
 * 内圆变正方形通过 drawRoundRect 画圆矩形实现，corner 是半径大小就是圆，到0就是正方形
 */
class RecordedButton @JvmOverloads constructor(context: Context,attrs:AttributeSet? = null,
                              defStyleAttr: Int = 0) : View(context,attrs,defStyleAttr){


    private val mContext : Context = context
    private var dp5 : Float = -1f
    private var pinkColor : Int = -1
    private var pinkTransparentColor : Int = -1
    //外圆
    private var circlePaint : Paint
    //实现内圆变化正方形按钮
    private var rectPaint : Paint
    //通过 PorterDuff.Mode.DST_OUT 实现两圆重叠只保留没有重叠部分
    private var mXfermode : Xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)

    //外圆属性动画
    private var outerAnimator : ValueAnimator? = null
    //onDraw初始化标记
    private var initDraw : Boolean = true
    //控制画正方形标记
    private var drawRectFlag : Boolean = false

    //该View的宽高比小那个长度
    private var length: Int = 0
    //外圆半径
    private var radius : Float = 0f
    //内圆半径
    private var innerRadius : Float = 0f
    //用于做变化外圆半径
    private var varRadius : Float = 0f
    //用于做变化内圆圆半径
    private var varInnerRadius : Float = 0f
    private var rectF : RectF = RectF()
    //最大矩形宽
    private var mMaxRectWidth : Float = 0f
    //最小矩形长
    private var mMinRectWidth : Float = 0f
    //最小圆半径
    private var mMinCircleRadius: Float = 0f
    //圆半径
    private var circleRadius: Float = 0f
    //最大圆半径
    private var mMaxCircleRadius : Float = 0f
    //最小画圆画笔粗度
    private var mMinCircleStrokeWidth: Float = 0f
    //最大画圆画笔粗度
    private var mMaxCircleStrokeWidth: Float = 0f
    //画笔粗度
    private var circleStrokeWidth: Float = 0f
    //矩形宽度
    private var rectWidth : Float = 0f
    //矩形角度
    private var corner : Float = 0f
    //矩形的最大最小角度
    private var mMinCorner : Float = 0f
    private var mMaxCorner : Float = 0f
    //动画集合
    private val mBeginAnimatorSet = AnimatorSet()
    private val mEndAnimatorSet = AnimatorSet()

    //记录当前点击的坐标
    private var nowX : Float = 0f
    private var nowY : Float = 0f
    //记录按下按钮的坐标
    private var downX : Float = 0f
    private var downY : Float = 0f
    //手指按下的坐标
    private var firstX : Float = 0f
    private var firstY : Float = 0f

    //记录当前View的 x和y坐标
    private var rawX : Float = -1f
    private var rawY : Float = -1f
    //点击模式
    var mRecordMode = ORIGIN//拍照的话更改该模式
    private var mScrollDirection : Int = -999//没有确定滑动方向
    private val mHandler = Handler()
    //用于判断是否长按
    private val mClickRunnable = ClickRunnable()
    //
    private var mInitX : Float = 0f
    private var mInitY : Float = 0f
    private var mInfectionPoint: Float = 0f
    private var mHasCancel = false

    //初始化 构造函数会调用这里面的代码再调用构造函数的代码
    init {
        //硬件层渲染
        setLayerType(LAYER_TYPE_HARDWARE,null)
        dp5 = dp2px(5)
        pinkColor = resources.getColor(R.color.pinkColor)
        pinkTransparentColor = resources.getColor(R.color.pinkColorTransparent)

        rectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        //实心图形
        rectPaint.style = Paint.Style.FILL
        rectPaint.color = pinkColor

        circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)

        mMinCircleStrokeWidth = dp2px(3)
        mMaxCircleStrokeWidth = dp2px(12)
        circleStrokeWidth = mMinCircleStrokeWidth
        //画笔宽度
        circlePaint.strokeWidth = circleStrokeWidth
    }

    override fun onDraw(canvas: Canvas) {
        val width = width - paddingLeft - paddingRight
        val height = height - paddingTop - paddingBottom
        val center = min(width, height) / 2

        if(initDraw){
//            layoutLeft = left
//            layoutRight = right
//            layoutTop = top
//            layoutBottom = bottom

            //控制矩形大小
            mMaxRectWidth = width * 1.0f / 2
            mMinRectWidth = mMaxRectWidth * 0.4f
            rectWidth = mMaxRectWidth
            //控制圆或正方形
            corner = rectWidth / 2
            //正方形
            mMinCorner = dp2px(5)
            //圆形
            mMaxCorner = mMaxRectWidth / 2

            //控制外圆大小
            mMinCircleRadius = mMaxRectWidth / 2 + mMinCircleStrokeWidth + dp2px(5)
            mMaxCircleRadius = width / 2 - mMaxCircleStrokeWidth
            circleRadius = mMinCircleRadius

            initDraw = false
        }

        //画圆 通过 Xfermode 实现空心圆效果 需要不同颜色才能实现重叠只选取非重叠部分显示
        circlePaint.color = resources.getColor(R.color.pinkColorTransparent)
        canvas!!.drawCircle(center.toFloat(),center.toFloat(),circleRadius,circlePaint)

        circlePaint.xfermode = mXfermode
        circlePaint.color = resources.getColor(R.color.pinkColor)
        canvas!!.drawCircle(center.toFloat(),center.toFloat(),circleRadius - circleStrokeWidth,circlePaint)
        circlePaint.xfermode = null

        //画内部圆或转为正方形
        rectF.left = center - rectWidth / 2
        rectF.right = center + rectWidth / 2
        rectF.top = center - rectWidth / 2
        rectF.bottom = center + rectWidth / 2
        //绘制圆角矩形，可以绘制到圆
        if(mRecordMode != LONG_CLICK){
            canvas!!.drawRoundRect(rectF,corner,corner,rectPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //当次点击的坐标
        nowX = event.rawX
        nowY = event.rawY

        when(event.action){
            MotionEvent.ACTION_DOWN -> {
                if(mRecordMode == ORIGIN && inBeginRange(event)){
                    firstX = nowX
                    firstY = nowY
                    downX = nowX
                    downY = nowY

                    beginAnimation()
                    //200毫秒后发送消息 定义为长点击
                    mHandler.postDelayed(mClickRunnable,200)
                    if(listener != null){
                        //录制开始
                        listener!!.onRecordStart()
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if(!mHasCancel){
                    if(mRecordMode == LONG_CLICK){
                        //偏移值
                        val offsetX = nowX - downX
                        val offsetY = nowY - downY
                        //滑动方向
                        val mOldDirection = mScrollDirection
                        val oldY = y
                        //通过 setX() 控制随手指移动
                        x += offsetX
                        y += offsetY
                        val newY = y
                        mScrollDirection = if(oldY >= newY){
                            //滑动向上
                            UP
                        } else {
                            //滑动向下
                            DOWN
                        }
                        if(mOldDirection != mScrollDirection){
                            mInfectionPoint = oldY
                        }
                        //记录缩放比例
                        val zoomPercentage = (mInfectionPoint - y) / mInitY
                        if (listener != null) {
                            listener!!.onZoom(zoomPercentage)
                        }
                        downX = nowX
                        downY = nowY
                    }
                }
                //随手指移动而移动
//                layout((left + offsetX).toInt(),
//                        (top + offsetY).toInt(),
//                        (right + offsetX).toInt(),
//                        (bottom + offsetY).toInt())
            }
            MotionEvent.ACTION_UP , MotionEvent.ACTION_CANCEL -> {
                if(!mHasCancel){
                    if (mRecordMode == LONG_CLICK){
                        if (listener != null) {
                            listener!!.onRecordStop()
                        }
                        resetLongClick()
                    } else if(mRecordMode == ORIGIN && inBeginRange(event)){
                        //记录第一次点击录制的情况
                        mHandler.removeCallbacks(mClickRunnable)
                        mRecordMode = SINGLE_CLICK
                    } else if (mRecordMode == SINGLE_CLICK && inEndRange(event)) {
                        //记录下次点击继而进行停止录制操作
                        if (listener != null) {
                            listener!!.onRecordStop()
                        }
                        resetSingleClick()
                    } else if(mRecordMode == PHOTO && inEndRange(event)){
                        //拍照模式
                        if(listener != null){
                            listener!!.takePricture()
                        }
                        resetSingleClick()
                    }
                } else {
                    mHasCancel = false
                }
                /*
                    该效果不好
                    layout(layoutLeft!!,layoutTop!!,layoutRight!!,layoutBottom!!)
                */
            }
        }
        return true
    }

    /**
     * 按下的范围
     * */
    private fun inBeginRange(event: MotionEvent) : Boolean{
        val centerX = measuredWidth / 2
        val centerY = measuredHeight / 2
        //最小域
        val minX = (centerX - mMinCircleRadius).toInt()
        val maxX = (centerX + mMinCircleRadius).toInt()
        val minY = (centerY - mMinCircleRadius).toInt()
        val maxY = (centerY + mMinCircleRadius).toInt()
        val isInXrange = event.x >= minX && event.x <= maxX
        val isInYrange = event.y >= minY && event.y <= maxY
        return isInXrange && isInYrange
    }

    private fun inEndRange(event: MotionEvent): Boolean {
        val minX = 0
        val maxX = measuredWidth
        val minY = 0
        val maxY = measuredHeight
        val isXInRange = event.x >= minX && event.x <= maxX
        val isYInRange = event.y >= minY && event.y <= maxY
        return isXInRange && isYInRange
    }

    private fun resetLongClick() {
        mRecordMode = ORIGIN
        mBeginAnimatorSet.cancel()
        startEndAnimation()
        //手指离开要回到原处
        startMoveOriginal()
    }

    private fun resetSingleClick() {
        mRecordMode = ORIGIN
        mBeginAnimatorSet.cancel()
        startEndAnimation()
    }

    /**
     * 按下时动画
     * */
    open fun beginAnimation(){
        //动画集合
        val beginAnimatorSet = AnimatorSet()
        val cornerAnimator = ObjectAnimator.ofFloat(
                this,"corner",mMaxCorner,mMinCorner)//由圆形变正方形
                .setDuration(500)
        val rectSizeAnimator = ObjectAnimator.ofFloat(
                this,"rectWidth",mMaxRectWidth,mMinRectWidth)
                .setDuration(500)
        val radiusAnimator = ObjectAnimator.ofFloat(
                this,"circleRadius",mMinCircleRadius,mMaxCircleRadius)
                .setDuration(500)
        beginAnimatorSet.playTogether(cornerAnimator,rectSizeAnimator,radiusAnimator)

        //外圆粗度变化效果
        val circleWidthAnimator = ObjectAnimator.ofFloat(
                this,"circleStrokeWidth",mMinCircleStrokeWidth,mMaxCircleStrokeWidth,mMinCircleStrokeWidth)
                .setDuration(1500)
        circleWidthAnimator.repeatCount = ObjectAnimator.INFINITE

        //动画的顺序
        mBeginAnimatorSet.playSequentially(beginAnimatorSet,circleWidthAnimator)
        mBeginAnimatorSet.start()
    }

    private fun startEndAnimation() {
        val cornerAnimator = ObjectAnimator.ofFloat(this, "corner",
                mMinCorner, mMaxCorner)
                .setDuration(500)
        val rectSizeAnimator = ObjectAnimator.ofFloat(this, "rectWidth",
                mMinRectWidth, mMaxRectWidth)
                .setDuration(500)
        val radiusAnimator = ObjectAnimator.ofFloat(this, "circleRadius",
                mMaxCircleRadius, mMinCircleRadius)
                .setDuration(500)
        val circleWidthAnimator = ObjectAnimator.ofFloat(this, "circleStrokeWidth",
                mMaxCircleStrokeWidth, mMinCircleStrokeWidth)
                .setDuration(500)

        mEndAnimatorSet.playTogether(cornerAnimator, rectSizeAnimator, radiusAnimator, circleWidthAnimator)
        mEndAnimatorSet.start()
    }

    internal inner class ClickRunnable : Runnable{
        override fun run() {
            if(!mHasCancel){
                mRecordMode = LONG_CLICK
                mInitX = x//当前X轴坐标
                mInitY = y//
                mInfectionPoint = mInitY
                mScrollDirection = UP
            }
        }
    }

    fun setCorner(corner : Float){
        this.corner = corner
        invalidate()
    }

    fun setCircleRadius(circleRadius : Float){
        this.circleRadius = circleRadius
    }

    fun setRectWidth(rectWidth : Float){
        this.rectWidth = rectWidth
    }

    fun setCircleStrokeWidth(circleStrokeWidth : Float){
        this.circleStrokeWidth = circleStrokeWidth
        invalidate()
    }

    fun reset() {
        if (mRecordMode == LONG_CLICK) {
            resetLongClick()
        } else if (mRecordMode == SINGLE_CLICK) {
            resetSingleClick()
        } else if (mRecordMode == ORIGIN) {
            if (mBeginAnimatorSet.isRunning) {
                mHasCancel = true
                mBeginAnimatorSet.cancel()
                startEndAnimation()
                mHandler.removeCallbacks(mClickRunnable)
                mRecordMode = ORIGIN
            }
        }
    }

    /**
     * 回到原来位置动画 配合 onLayout 方法使用
     * */
    private fun startMoveOriginal(){
        val offsetX = rawX - x
        val offsetY = rawY - y

        val rX = x
        val rY = y

        val va = ValueAnimator.ofFloat(0f,1f)
                .setDuration(200)
        va.addUpdateListener {
            val value = it.animatedValue as Float
            x = rX + offsetX * value
            y = rY + offsetY * value
        }
        va.start()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if(rawX == -1f || rawY == -1f){
            //记录当前view 的x，y坐标
            rawX = x
            rawY = y
        }
    }

    private var listener : OnGestureListener? = null

    interface OnGestureListener{
        fun takePricture()//拍照
        //开始录制
        fun onRecordStart()
        //结束录制
        fun onRecordStop()
        //缩放百分比
        fun onZoom(zoomPercentage : Float)
    }

    open fun setOnGestureListener(listener : OnGestureListener){
        this.listener = listener
    }

    companion object{
        const val TAG = "RecordedButton"
        //单击
        const val SINGLE_CLICK = 0
        //长按
        const val LONG_CLICK = 1
        //初始化
        const val ORIGIN = 2
        //拍照模式 (需要用户选择该模式)
        const val PHOTO = 3
        //滑动方向：上
        const val UP = -1
        //滑动方向：下
        const val DOWN = 0
    }
}
























