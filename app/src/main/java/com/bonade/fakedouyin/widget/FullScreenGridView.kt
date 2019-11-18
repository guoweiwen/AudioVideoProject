package com.bonade.fakedouyin.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.bonade.fakedouyin.R
import com.bonade.fakedouyin.utils.dp2px
import com.bonade.fakedouyin.utils.getScreenResolution
import kotlin.math.roundToInt

/**
 * Created by wyman
 * on 2019-07-07.
 * 将 ImageView的图片分成一个个网格
 */
class FullScreenGridView @JvmOverloads constructor(context : Context, attrs : AttributeSet?,
                                                   defStyleAttr:Int=0) : View(context,attrs,defStyleAttr){
    private var initFlag : Boolean = true
    private val screenWidth = getScreenResolution()[0]
    private val screenHeight = getScreenResolution()[1]
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paint.style = Paint.Style.FILL
        paint.color = resources.getColor(R.color.pinkColor)
        paint.strokeWidth = dp2px(1)
    }

    /**
     * 将整个屏幕分成40格;
     * 上下空48dp 每格都为正方形
     * */
    override fun onDraw(canvas: Canvas) {
        val width = measuredWidth
        //减 2 * 48dp为了屏幕上下都没有一个title栏
        val height = measuredHeight - dp2px(48) * 2
        val widthOffset = width / 5
        val heightOffset = (height / 8).roundToInt()

        val startYRow = dp2px(48)
        val startYCollum = dp2px(48)
        for(index in 1..4){//竖 循环 1,2,3,4
            canvas.drawLine(
                    widthOffset * index.toFloat(),
                    startYRow,
                    widthOffset * index.toFloat(),
                    height + startYRow,
                    paint)
        }
        for(index in 1..7){//横 循环 1,2,3,4,5,6,7
            canvas.drawLine(
                    0f,
                    heightOffset * index.toFloat() + startYCollum,
                    width * 1.0f,
                    heightOffset * index.toFloat() + startYCollum,
                    paint)
        }
    }
}