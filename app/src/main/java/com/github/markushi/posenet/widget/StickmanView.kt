package com.github.markushi.posenet.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.github.markushi.posenet.domain.Stickman

class StickmanView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        strokeCap = Paint.Cap.ROUND
        strokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            24f,
            context.resources.displayMetrics
        )
        style = Paint.Style.STROKE
    }
    private val padding = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        8f + 16f,
        context.resources.displayMetrics
    )

    var stickman: Stickman? = null
        set(value) {
            field = value
            if (field != null) {
                postInvalidateOnAnimation()
            }
        }

    override fun onDraw(c: Canvas?) {
        super.onDraw(c)
        val canvas = c!!
        stickman?.let { s ->
            canvas.apply {
                translate(padding, padding)
                val scaleX = width - 2 * padding
                val scaleY = height - 2 * padding
                drawCircle(s.headX * scaleX, s.headY * scaleY, s.headRadius * scaleY, paint)
                drawLine(
                    s.shoulderX * scaleX,
                    s.shoulderY * scaleY,
                    s.hipX * scaleX,
                    s.hipY * scaleY,
                    paint
                )
                drawLine(
                    s.shoulderX * scaleX,
                    s.shoulderY * scaleY,
                    s.leftElbowX * scaleX,
                    s.leftElbowY * scaleY,
                    paint
                )
                drawLine(
                    s.shoulderX * scaleX,
                    s.shoulderY * scaleY,
                    s.rightElbowX * scaleX,
                    s.rightElbowY * scaleY,
                    paint
                )
                drawLine(
                    s.leftElbowX * scaleX,
                    s.leftElbowY * scaleY,
                    s.leftWristX * scaleX,
                    s.leftWristY * scaleY,
                    paint
                )
                drawLine(
                    s.rightElbowX * scaleX,
                    s.rightElbowY * scaleY,
                    s.rightWristX * scaleX,
                    s.rightWristY * scaleY,
                    paint
                )
                drawLine(
                    s.hipX * scaleX,
                    s.hipY * scaleY,
                    s.leftKneeX * scaleX,
                    s.leftKeeY * scaleY,
                    paint
                )
                drawLine(
                    s.leftKneeX * scaleX,
                    s.leftKeeY * scaleY,
                    s.leftAnkleX * scaleX,
                    s.leftAnkleY * scaleY,
                    paint
                )
                drawLine(
                    s.hipX * scaleX,
                    s.hipY * scaleY,
                    s.rightKneeX * scaleX,
                    s.rightKeeY * scaleY,
                    paint
                )
                drawLine(
                    s.rightKneeX * scaleX,
                    s.rightKeeY * scaleY,
                    s.rightAnkleX * scaleX,
                    s.rightAnkleY * scaleY,
                    paint
                )
            }
        }
    }
}