package com.github.markushi.posenet.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import com.github.markushi.posenet.domain.Pose

class PoseView @JvmOverloads constructor(
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

    @ColorInt
    var color: Int = Color.GRAY
        set(value) {
            field = value
            paint.color = field
            postInvalidateOnAnimation()
        }

    var strokeWidth: Float = 1f
        set(value) {
            field = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                context.resources.displayMetrics
            )
            paint.strokeWidth = field
            postInvalidateOnAnimation()
        }

    var pose: Pose? = null
        set(value) {
            field = value
            postInvalidateOnAnimation()
        }

    override fun onDraw(c: Canvas?) {
        super.onDraw(c)
        val canvas = c!!
        pose?.let { s ->
            canvas.apply {
                translate(padding, padding)
                val scaleX = width - 2 * padding
                val scaleY = height - 2 * padding
                drawCircle(s.head.x * scaleX, s.head.y * scaleY, s.headRadius * scaleY, paint)
                drawLine(
                    s.shoulder.x * scaleX,
                    s.shoulder.y * scaleY,
                    s.hip.x * scaleX,
                    s.hip.y * scaleY,
                    paint
                )
                drawLine(
                    s.shoulder.x * scaleX,
                    s.shoulder.y * scaleY,
                    s.leftElbow.x * scaleX,
                    s.leftElbow.y * scaleY,
                    paint
                )
                drawLine(
                    s.shoulder.x * scaleX,
                    s.shoulder.y * scaleY,
                    s.rightElbow.x * scaleX,
                    s.rightElbow.y * scaleY,
                    paint
                )
                drawLine(
                    s.leftElbow.x * scaleX,
                    s.leftElbow.y * scaleY,
                    s.leftWrist.x * scaleX,
                    s.leftWrist.y * scaleY,
                    paint
                )
                drawLine(
                    s.rightElbow.x * scaleX,
                    s.rightElbow.y * scaleY,
                    s.rightWrist.x * scaleX,
                    s.rightWrist.y * scaleY,
                    paint
                )
                drawLine(
                    s.hip.x * scaleX,
                    s.hip.y * scaleY,
                    s.leftKnee.x * scaleX,
                    s.leftKnee.y * scaleY,
                    paint
                )
                drawLine(
                    s.leftKnee.x * scaleX,
                    s.leftKnee.y * scaleY,
                    s.leftAnkle.x * scaleX,
                    s.leftAnkle.y * scaleY,
                    paint
                )
                drawLine(
                    s.hip.x * scaleX,
                    s.hip.y * scaleY,
                    s.rightKnee.x * scaleX,
                    s.rightKnee.y * scaleY,
                    paint
                )
                drawLine(
                    s.rightKnee.x * scaleX,
                    s.rightKnee.y * scaleY,
                    s.rightAnkle.x * scaleX,
                    s.rightAnkle.y * scaleY,
                    paint
                )
            }
        }
    }
}