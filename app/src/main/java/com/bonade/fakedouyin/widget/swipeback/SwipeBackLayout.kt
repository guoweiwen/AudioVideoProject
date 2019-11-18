package com.bonade.fakedouyin.widget.swipeback

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.app.Activity
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.customview.widget.ViewDragHelper


/**
 * Created by wyman
 * on 2019-07-19.
 * 滑动回退前个 Activity
 */
open class SwipeBackLayout @JvmOverloads constructor(context: Context,attrs : AttributeSet?,
                                                defStyleAttr:Int=0) : FrameLayout(context,attrs,defStyleAttr){
    private var mDragView : View? = null

    lateinit var mViewDragHelper : ViewDragHelper

    private val mAutoBackOrignalPoint = Point()

    private val mCurArrivePoint = Point()

    private var mCurEdgeFlag = ViewDragHelper.EDGE_LEFT

    private var mSwipeEdge = ViewDragHelper.EDGE_LEFT

    private var mActivity : Activity? = null

    /**
     * 设置滑动边缘:上下左右
     * */
    fun setSwipeEdge(swipeEdge : Int) {
        this.mSwipeEdge = swipeEdge
        mViewDragHelper.setEdgeTrackingEnabled(swipeEdge)
    }

    init {
        /**
         * 第一个 ViewGroup本身
         * 第二个参数 触摸的溢出值，值越小 touchSlop的值就越大即越难不当触摸
         * 第三个回调方法
         * */
        mViewDragHelper = ViewDragHelper.create(
                SwipeBackLayout@this,
                1.0f,
                /*kotlin 实现匿名内部类*/
                object : ViewDragHelper.Callback(){
                    /**
                     * 触摸就回调此方法
                     * 返回ture则表示可以捕获该view，你可以根据传入的第一个view参数决定哪些可以捕获
                     * */
                    override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                        Log.e("tryCaptureView","child:$child")
                        //有一只手就 pointerId为0 两只手就pointerId 1
                        Log.e("tryCaptureView","pointerId:$pointerId")
                        return false
                    }

                    /**
                     * 可以在该方法中对child移动的边界进行控制，left , top 分别为即将移动到的位置，
                     * 比如横向的情况下，我希望只在ViewGroup的内部移动，
                     * 即：最小 >= paddingleft，
                     *    最大 <= ViewGroup.getWidth()-paddingright-child.getWidth。
                     * */
                    override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
                        Log.e("ViewPositionHorizontal","child:$child")
                        Log.e("ViewPositionHorizontal","left:$left")
                        Log.e("ViewPositionHorizontal","dx:$dx")
                        mCurArrivePoint.x = left
                        return if (mCurEdgeFlag != ViewDragHelper.EDGE_BOTTOM &&
                                mCurEdgeFlag != ViewDragHelper.EDGE_TOP ) {
                            left
                        }  else 0
                    }

                    /**
                     * 这里 clampViewPositionHorizontal 和 clampViewPositionVertical
                     * 如果返回 0 子View就会消耗这事件
                     * 子View可以消耗事件
                     * 子View不消耗事件，那么整个手势（DOWN-MOVE*-UP）都是直接进入onTouchEvent，
                     * 在onTouchEvent的DOWN的时候就确定了captureView。如果消耗事件，
                     * 那么就会先走onInterceptTouchEvent方法，判断是否可以捕获，
                     * 而在判断的过程中会去判断另外两个回调的方法：getViewHorizontalDragRange和getViewVerticalDragRange，
                     * 只有这两个方法返回大于0的值才能正常的捕获。
                     * */
                    override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
                        Log.e("ViewPositionVertical","child:" +child);
                        Log.e("ViewPositionVertical","top:" +top);
                        Log.e("ViewPositionVertical","dy:" +dy);
                        mCurArrivePoint.y = top;
                        return if (mCurEdgeFlag == ViewDragHelper.EDGE_BOTTOM ||
                                mCurEdgeFlag == ViewDragHelper.EDGE_TOP) {
                             top
                        } else  0
                    }

                    /**
                     * 手指抬起
                     * */
                    override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                        super.onViewReleased(releasedChild,xvel,yvel)
                        Log.e("onViewReleased","releasedChild:$releasedChild")
                        Log.e("onViewReleased", "xvel:$xvel")
                        Log.e("onViewReleased", "yvel:$yvel")
                        when (mCurEdgeFlag){
                            ViewDragHelper.EDGE_LEFT ->{
                                if (mCurArrivePoint.x > width / 2) {
                                    //大于当前view的一半宽，移出当前view
                                    mViewDragHelper.settleCapturedViewAt(width, mAutoBackOrignalPoint.y)
                                } else {
                                    //当前view回到原来位置
                                    mViewDragHelper.settleCapturedViewAt(mAutoBackOrignalPoint.x, mAutoBackOrignalPoint.y)
                                }
                            }
                            ViewDragHelper.EDGE_RIGHT -> {
                                if (mCurArrivePoint.x < -width / 2) {
                                    mViewDragHelper.settleCapturedViewAt(-width, mAutoBackOrignalPoint.y)
                                } else {
                                    mViewDragHelper.settleCapturedViewAt(mAutoBackOrignalPoint.x, mAutoBackOrignalPoint.y)
                                }
                            }
                            ViewDragHelper.EDGE_BOTTOM ->{
                                if (mCurArrivePoint.y < -height / 2) {
                                    mViewDragHelper.settleCapturedViewAt(mAutoBackOrignalPoint.x, -height)
                                } else {
                                    mViewDragHelper.settleCapturedViewAt(mAutoBackOrignalPoint.x, mAutoBackOrignalPoint.y)
                                }
                            }
                            ViewDragHelper.EDGE_TOP -> {
                                if (mCurArrivePoint.y > height / 2) {
                                    mViewDragHelper.settleCapturedViewAt(mAutoBackOrignalPoint.x, height)
                                } else {
                                    mViewDragHelper.settleCapturedViewAt(mAutoBackOrignalPoint.x, mAutoBackOrignalPoint.y)
                                }
                            }
                        }
                        mCurArrivePoint.x = 0
                        mCurArrivePoint.y = 0
                        //其内部使用的是mScroller.startScroll
                        invalidate()
                    }

                    override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
                        super.onViewPositionChanged(changedView, left, top, dx, dy)
                        Log.e("onViewPositionChanged", "changedView:$changedView")
                        Log.e("onViewPositionChanged", "left:$left")//回滚时正数
                        Log.e("onViewPositionChanged", "top:$top")
                        Log.e("onViewPositionChanged", "dx:$dx")//回滚时负数
                        Log.e("onViewPositionChanged", "dy:$dy")
                        when (mCurEdgeFlag) {
                            ViewDragHelper.EDGE_LEFT -> if (left >= width) {
                                if (mFinishScroll != null) {
                                    mFinishScroll!!.complete()
                                }
                            }
                            ViewDragHelper.EDGE_RIGHT -> if (left <= -width) {
                                if (mFinishScroll != null) {
                                    mFinishScroll!!.complete()
                                }
                            }
                            ViewDragHelper.EDGE_BOTTOM -> if (top <= -height) {
                                if (mFinishScroll != null) {
                                    mFinishScroll!!.complete()
                                }
                            }
                            ViewDragHelper.EDGE_TOP -> if (top >= height) {
                                if (mFinishScroll != null) {
                                    mFinishScroll!!.complete()
                                }
                            }
                        }
                    }

                    /**
                     * 触摸边界时就回调此方法
                     * */
                    override fun onEdgeDragStarted(edgeFlags : Int,pointerId : Int) {
                        Log.e("onEdgeDragStarted","edgeFlags:$edgeFlags")
                        Log.e("onEdgeDragStarted","pointerId:$pointerId")
                        mCurEdgeFlag = edgeFlags
                        if (mDragView == null) mDragView = getChildAt(0)
                        mViewDragHelper.captureChildView(mDragView!!,pointerId)
                    }
                }
        )

        //默认为左边缘滑动退出当前activity
//        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_TOP);
        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_ALL)
    }

    override fun computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            invalidate()
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent) : Boolean{
        //  mViewDragHelper.shouldInterceptTouchEvent(ev) 决定我们是否应该拦截当前的事件
        return mViewDragHelper.shouldInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean{
        // 通过mDragger.processTouchEvent(event)处理事件
        mViewDragHelper.processTouchEvent(event)
        return true
    }

    override fun onLayout(changed : Boolean,left : Int,top : Int,right : Int,bottom : Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 0) {
            mDragView = getChildAt(0)
            Log.e("onFinishInflate","mDragView:$mDragView::class.java.simpleName")
        }
    }

    private var  mFinishScroll : OnFinishScroll? = null

    fun setOnFinishScroll(finishScroll : OnFinishScroll) {
        this.mFinishScroll = finishScroll
    }

    //滑动结束的回调接口
    interface OnFinishScroll {
        fun complete()
    }

    fun attachToActivity(activity : Activity){
        this.mActivity = activity
        //在 decorview view添加 BaseSwipeLayout实现不了上下拉退出当前activity
//        TypedArray a = activity.getTheme().obtainStyledAttributes(new int[]{
//                android.R.attr.windowBackground
//        });
//        int background = a.getResourceId(0, 0);
//        a.recycle();
//        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
//        //获取 DecorView的 LinearLayout
//        ViewGroup decorChild = (ViewGroup) decorView.getChildAt(0);
//        decorChild.setBackgroundResource(background);
//        decorView.removeView(decorChild);
//        //当前SwipeBackLayout 添加 LinearLayout
//        addView(decorChild);
//        //decorview 再添加 SwipeBackLayout 即 DecorView的子View为SwipeBackLayout
//        decorView.addView(this);

        //通过 contentView 添加 BaseSwipeLayout 而 BaseSwipeLayout 将xml布局文件的根布局添加上去
        val contentView = activity.findViewById(android.R.id.content) as ViewGroup
        val contentViewChiild = contentView.getChildAt(0) as ViewGroup
        //ContentView 移除 当前的ViewGroup
        contentView.removeView(contentViewChiild)
        //将 BaseSwipeLayout 添加被移除的 ViewGroup
        this.addView(contentViewChiild)
        //contentView 添加 BaseSwipeLayout
        contentView.addView(this)
    }
}



















