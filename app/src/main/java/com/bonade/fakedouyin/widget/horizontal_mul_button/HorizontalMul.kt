package com.bonade.fakedouyin.widget.horizontal_mul_button

import android.content.Context
import android.os.Build
import androidx.annotation.Px
import androidx.core.view.ViewCompat
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.Scroller
import com.bonade.fakedouyin.utils.dp2px
import com.bonade.fakedouyin.utils.getScreenResolution
import com.tencent.bugly.proguard.w
import kotlin.math.abs
import kotlin.math.round

/**
 * Created by wyman
 * on 2019-07-20.
 * 抖音录制视频下面的选项按钮
 *
 * ViewGroup 包含 子View  在move时候 setX重绘或onLayout会导致子View出现重影问题
 * 用 scrollBy 可以解决，因为 setX会调用onLayout 而 scrollBy只是滑动view的内容不会调用onLayout和重绘
 *
 * scrollTo(x,y) 是根据当前view重新定位后的x,y值非固定不变的;即移动后x,y值会变为0,0
 *
 * onMeasure() 会多次调用
 * 多次调用 onMeasure() 后 onSizeChanged() 调用一次
 * 最后调用 onLayout() 在 onLayout() 可以调用获取子View的宽高
 *
 * 该 ViewGroup 只能添加最多四个子View
 */
class HorizontalMul @JvmOverloads constructor(context: Context,attrs: AttributeSet?,
                           defStyleAttr:Int=0) : LinearLayout(context, attrs, defStyleAttr){
    //记录上一次坐标
    private var lastX : Float = -1f
    private var lastY : Float = -1f

    private var moveX : Float = -1f
    private var moveY : Float = -1f

    //记录手指抬起
    private var upX : Float = -1f
    private var upY : Float = -1f

    private var originX : Float = -1f
    private var originY : Float = -1f

    //横移阈值
    private val threshold = 5//5像素

    //最小的X轴坐标
    private var mMinPositionX : Int = -1
    private var mMaxPositionX : Int = -1

    //屏幕宽高
    private val screenWidth : Int = getScreenResolution()[0]
    private val screenHeight : Int = getScreenResolution()[1]
    private var halfScreenWidth : Int

    //记录当前 rawX
    private var currentRawX : Int = -1

    private var childViewWidth : Int = -1
    private var firstRange : Array<Int> = Array(2) {-1}
    private var secondRange : Array<Int> = Array(2) {-1}
    private var thirdRange : Array<Int> = Array(2) {-1}
    private var fourthRange : Array<Int> = Array(2) {-1}
    private var firstHalf : Int = -1
    private var secondHalf : Int = -1
    private var thirdHalf : Int = -1
    private var fourthHalf : Int = -1

    //封装子View对象
    private var childViewInfos : Array<ChildTextView?>? = null
    //当前坐标
    private var mCurrentScale : Int = -1
    //move的偏移值叠加
    private var scrollOffsetXToUp : Float = -1f

    init {
//        Log.e(TAG,"originX:$originX,originY:$originY")//0  0
        checkAPILevel()
        halfScreenWidth = screenWidth / 2
        //第一次进入，跳转到设定刻度
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(HorizontalMul@this)
                scrollToSpecify(TAKE_PHOTO)
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action){
            MotionEvent.ACTION_DOWN ->{
                //上一次未完成的滑动停止
                lastX = event.x
                lastY = event.y
                originX = event.rawX
                originY = event.rawY
                Log.e(TAG,"lastX:$lastX")//519
                Log.e(TAG,"lastY:$lastY")//48
            }
            MotionEvent.ACTION_MOVE ->{
                moveX = event.x
                moveY = event.y

//                Log.e(TAG,"moveX:$moveX")
//                Log.e(TAG,"moveY:$moveY")

                val offsetX = abs(moveX - lastX)
                val scrollOffsetX = lastX - moveX
                scrollOffsetXToUp += scrollOffsetX
                //向左滑正，向右滑负
//                Log.e(TAG,"scrollOffsetX:$scrollOffsetX")
                if(offsetX > threshold){
//                    x = moveX //setX() 会造成重绘 或 onLayout
//                    Log.e(TAG,"X:$x,Y:$y")//0.0 1967
                    scrollBy(scrollOffsetX.toInt(),0)
//                    Log.e(TAG,"X:$x")//0
                    lastX = moveX
                    lastY = moveY
                }
//                Log.e(TAG,"MoveRawX:${event.rawX}")
            }
            MotionEvent.ACTION_UP -> {
                upX = event.rawX
                upY = event.rawY
                currentRawX = (upX - originX).toInt()
                //向左滑 负 向右滑 正
                Log.e("currentRawX","currentRawX:$currentRawX")
                //当前坐标 最终为负为了适应 scrollBackCurrentScale() 方法的区间
                val currentOffsetX = -(currentScaleChangeX(mCurrentScale) + (-currentRawX))
                Log.e("currentOffsetX","currentOffsetX:$currentOffsetX")
                scrollBackCurrentScale(currentOffsetX.toFloat())
            }
            //该事件发生的情况是：子View在Down-->Move过程中，
            // 即Move事件过程中被父控件拦截了，
            // 就会出现ACTION_CANCEL 事件;通常当 ACTION_UP事件处理
            MotionEvent.ACTION_CANCEL -> {
            }
        }
        return true
    }

    /**
     * 把移动后光标对准距离最近的刻度，就是回弹到最近刻度
     * */
    private fun scrollBackCurrentScale(currentOffsetX : Float) {
        //第三第四个参数为距离值 所以要 - getScrollX()
        //下面两行代码是实现弹性滑动，invalidate()调用完就调用computeScroll()方法
        //这里其实距离也有正负号，正为向左滑动，负为向右滑动
        //第三，第四个参数也是有"+-"号区分的，+ 向左滚动 - 向右滚动 Y轴系 + 向上滚动
        val offsetXInt = currentOffsetX.toInt()
        val currentOneX = halfScreenWidth - childViewInfos!![0]!!.halfValue * (2 * 0 + 1)
        val currentTwoX = halfScreenWidth - childViewInfos!![0]!!.halfValue * (2 * 1 + 1)
        val currentThreeX = halfScreenWidth - childViewInfos!![0]!!.halfValue * (2 * 2 + 1)
        val currentFourX = halfScreenWidth - childViewInfos!![0]!!.halfValue * (2 * 3 + 1)
        var scrollToX : Int = -1
        when {
            offsetXInt > currentOneX || (offsetXInt in (currentTwoX) until currentOneX) -> {
                scrollToX = TAKE_PHOTO
            }
            offsetXInt in (currentThreeX) until currentTwoX -> {
                scrollToX = VIDEO_60S
            }
            offsetXInt in (currentFourX + 1) until currentThreeX -> {
                scrollToX = VIDEO_15S
            }
            offsetXInt < currentFourX -> {
                scrollToX = VIDEO_ALBUM
            }
        }
        scrollToSpecify(scrollToX)
    }

    /**
     * View 布局时会调用
     * */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
//        Log.e("onSizeChanged","w:$w;h:$h")//w 1080,h 132  边界：test1:64；test4:1050
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
//        Log.e("onLayout","onLayout")
        if(mMinPositionX == -1 || mMaxPositionX == -1){
            val w = width
            val childView_0 = getChildAt(0)
            childViewWidth = childView_0.width
//            Log.e("min","childViewWidth:$childViewWidth;")
            //最小位置为 第一个TextView 的左边
            mMinPositionX = (w * 1.0f / 2 - 2 * 1.0 * childViewWidth).toInt()
            //最大位置为 最后一个TextView 的右边
            mMaxPositionX = -mMinPositionX
            Log.e("min","mMinPositionX:$mMinPositionX;")//264
            Log.e("max","mMaxPositionX:$mMaxPositionX;")//-264

            val childCount = childCount
            childViewInfos = Array(childCount){null}
            //封装4个view的范围值和中间值和textview的宽
            for(index in 0 until childCount){
                val ranges = Array(2){-1}
                if(index == 0){
                    ranges[0] = mMinPositionX
                    ranges[1] = mMinPositionX + getChildAt(index).width
                } else {
                    ranges[0] = childViewInfos!![index-1]!!.range[1] + 1
                    ranges[1] = ranges[0] + getChildAt(index).width
                }
                val halfRange = round((ranges[1] - ranges[0]) *  1.0 / 2).toInt()
                val childViewWidth = getChildAt(index).width
                childViewInfos!![index] = ChildTextView(ranges,halfRange,childViewWidth)
            }

            //四个view的范围值和中间值
//            firstRange[0] = mMinPositionX
//            firstRange[1] = mMinPositionX + childViewWidth
//            firstHalf = round((firstRange[1] - firstRange[0]) *  1.0 / 2).toInt()
//
//            secondRange[0] = firstRange[1] + 1
//            secondRange[1] = secondRange[0] + childViewWidth
//            secondHalf = round((secondRange[1] - secondRange[0]) *  1.0 / 2).toInt()
//
//            thirdRange[0] = secondRange[1] + 1
//            thirdRange[1] = thirdRange[0] + childViewWidth
//            thirdHalf = round((thirdRange[1] - thirdRange[0]) *  1.0 / 2).toInt()
//
//            fourthRange[0] = thirdRange[1] + 1
//            fourthRange[1] = fourthRange[0] + childViewWidth
//            fourthHalf = round((fourthRange[1] - fourthRange[0]) *  1.0 / 2).toInt()

//            Log.e("onLayout","firstRange[0]:${childViewInfos!![0]!!.range[0]};" +
//                    "firstRange[1]:${childViewInfos!![0]!!.range[1]};"+
//                    "firstHalf:${childViewInfos!![0]!!.halfValue}")
//
//            Log.e("onLayout","secondRange[0]:${childViewInfos!![1]!!.range[0]};" +
//                    "secondRange[1]:${childViewInfos!![1]!!.range[1]};"+
//                    "secondHalf:${childViewInfos!![1]!!.halfValue}")
//
//            Log.e("onLayout","thirdRange[0]:${childViewInfos!![2]!!.range[0]};" +
//                    "thirdRange[1]:${childViewInfos!![2]!!.range[1]};"+
//                    "thirdHalf:${childViewInfos!![2]!!.halfValue}")
//
//            Log.e("onLayout","fourthRange[0]:${childViewInfos!![3]!!.range[0]};" +
//                    "fourthRange[1]:${childViewInfos!![3]!!.range[1]};"+
//                    "fourthHalf:${childViewInfos!![3]!!.halfValue}")
            /*
            *   firstRange[0]:264;firstRange[1]:402firstHalf:69
                secondRange[0]:403;secondRange[1]:541secondHalf:69
                thirdRange[0]:542;thirdRange[1]:680thirdHalf:69
                fourthRange[0]:681;fourthRange[1]:819fourthHalf:69
            * */
        }
    }

    /**
     * 设置一开始显示什么按钮
     * */
    fun scrollToSpecify(currentBtn : Int){
        //67 为childview的宽
//        val current = halfScreenWidth -  67*(2*0+1)  // TextView 1
//        val current = halfScreenWidth -  67*(2*1+1)  // TextView 2
//        val current = halfScreenWidth -  67*(2*2+1)  // TextView 3
        //halfScreenWidth -  67*(2*3+1) // TextView 4
        if(childViewInfos != null && currentBtn < childViewInfos!!.size){
            mCurrentScale = currentBtn
            scrollTo(currentScaleChangeX(currentBtn),0)
//            listener?.callBack(currentBtn)
            //接口回调
            callBackListener?.invoke(currentBtn)
        } else {
            //传入参数出错
        }
    }

    /**
     * 指示器 转为 坐标
     * */
    private fun currentScaleChangeX(currentBtn : Int) : Int{
        val current = halfScreenWidth - childViewInfos!![currentBtn]!!.halfValue * (2 * currentBtn + 1)
        var offset = -1
        var result = -1
        when(currentBtn){
            //设置偏差值
            TAKE_PHOTO -> {
                offset = 0
                result = -current + offset
            }
            VIDEO_60S, VIDEO_15S -> {
                offset = -dp2px(15).toInt()
                result = -current + offset
            }
            VIDEO_ALBUM -> {
                result = dp2px(15).toInt()
            }
        }
        Log.e("currentScaleChangeX","result:$result")
        return result
    }

    //API小于18则关闭硬件加速，否则setAntiAlias()方法不生效
    private fun checkAPILevel(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2){
            setLayerType(LAYER_TYPE_NONE,null)
        }
    }

//    interface CallBackListener{
//        fun callBack(currentBtn : Int)
//    }
//    private var listener : CallBackListener? = null
//    fun setCallBackListener(listener : CallBackListener?){
//        this.listener = listener
//    }
    //接口回调函数(等同于上面java的接口设置)
    var callBackListener : ((currentBtn : Int)->Unit)? = null

    companion object{
        const val TAG = "HorizontalMul"
        const val TAKE_PHOTO = 0
        const val VIDEO_60S = 1
        const val VIDEO_15S = 2
        const val VIDEO_ALBUM = 3
    }
}