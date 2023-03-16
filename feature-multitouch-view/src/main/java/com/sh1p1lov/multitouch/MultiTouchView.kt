package com.sh1p1lov.multitouch

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import com.sh1p1lov.multitouch.view.R
import kotlin.math.max
import kotlin.random.Random

class MultiTouchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    companion object {
        private const val TAG = "MultiTouchViewLog"
        private const val TOUCH_COLOR_DEFAULT = "#00ffb7"
        private const val TOUCH_STROKE_WIDTH_DEFAULT_DP = 4f
        private const val TOUCH_STROKE_WIDTH_MIN_DP = 1f
        private const val TOUCH_RADIUS_DEFAULT_DP = 60f
        private const val TOUCH_RADIUS_MIN_DP = 10f
        private const val TOUCH_LIGHTING_WIDTH_DEFAULT_DP = 16f
        private const val TOUCH_LIGHTING_WIDTH_MIN_DP = 0f
    }

    var isRandomTouchColor: Boolean
    @ColorInt var touchColor: Int

    var touchStrokeWidth: Float = 0f
        set(value) {
            field = max(value, TOUCH_STROKE_WIDTH_MIN_DP.toPx())
        }

    var touchRadius: Float = 0f
        set(value) {
            field = max(value, TOUCH_RADIUS_MIN_DP.toPx())
        }

    var touchLightingWidth: Float = 0f
        set(value) {
            field = max(value, TOUCH_LIGHTING_WIDTH_MIN_DP.toPx())
        }

    init {
        val attributes = context.obtainStyledAttributes(attrs,
            R.styleable.MultiTouchView, defStyleAttr, defStyleRes)
        isRandomTouchColor = attributes.getBoolean(R.styleable.MultiTouchView_isRandomTouchColor, false)
        touchColor = attributes.getColor(
            R.styleable.MultiTouchView_touchColor, Color.parseColor(
                TOUCH_COLOR_DEFAULT
            ))
        touchStrokeWidth = attributes.getDimension(R.styleable.MultiTouchView_touchStrokeWidth, TOUCH_STROKE_WIDTH_DEFAULT_DP.toPx())
        touchRadius = attributes.getDimension(R.styleable.MultiTouchView_touchRadius, TOUCH_RADIUS_DEFAULT_DP.toPx())
        touchLightingWidth = attributes.getDimension(R.styleable.MultiTouchView_touchLightingWidth, TOUCH_LIGHTING_WIDTH_DEFAULT_DP.toPx())
        attributes.recycle()
    }

    private val touchPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private val touches = mutableMapOf<Int, PointF>()
    private val touchesColors = mutableMapOf<Int, Int>()

    private fun addTouch(touchId: Int, point: PointF) {
        touchesColors[touchId] = if (isRandomTouchColor) randomColor() else touchColor
        touches[touchId] = point
        invalidate()
    }

    private fun removeTouch(touchId: Int) {
        touches.remove(touchId)
        touchesColors.remove(touchId)
        invalidate()
    }

    private fun updateTouchPosition(touchId: Int, point: PointF) {
        touches[touchId] = point
        invalidate()
    }

    private fun clearTouches() {
        touches.clear()
        touchesColors.clear()
        invalidate()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        val touchId = event.getPointerId(event.actionIndex)
        val x = event.getX(event.actionIndex)
        val y = event.getY(event.actionIndex)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(TAG, "ACTION_DOWN $touchId: ($x, $y")
                addTouch(touchId, PointF(x, y))
                return true
            }
            MotionEvent.ACTION_UP -> {
                Log.d(TAG, "ACTION_UP $touchId: ($x, $y)")
                removeTouch(touchId)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d(TAG, "ACTION_MOVE $touchId: ($x, $y) ${event.pointerCount}")
                for (i in 0 until event.pointerCount) {
                    val id = event.getPointerId(i)
                    val newX = event.getX(i)
                    val newY = event.getY(i)
                    updateTouchPosition(id, PointF(newX, newY))
                }
                return true
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                Log.d(TAG, "ACTION_POINTER_DOWN $touchId: ($x, $y)")
                addTouch(touchId, PointF(x, y))
                return true
            }
            MotionEvent.ACTION_POINTER_UP -> {
                Log.d(TAG, "ACTION_POINTER_UP $touchId: ($x, $y)")
                removeTouch(touchId)
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                Log.d(TAG, "ACTION_CANCEL")
                clearTouches()
            }
        }

        return false
    }

    override fun onDraw(canvas: Canvas) {
        drawTouches(canvas)
    }

    private fun drawTouches(canvas: Canvas) {
        for (t in touches) {
            val p = t.value
            val color = touchesColors[t.key] ?: touchColor
            canvas.drawTouch(point = p, touchColor = color)
        }
    }

    private fun Canvas.drawTouch(point: PointF, @ColorInt touchColor: Int) {
        touchPaint.apply {
            color = touchColor
            strokeWidth = touchStrokeWidth
            setShadowLayer(
                touchLightingWidth,
                0f,
                0f,
                touchColor
            )
        }

        this.drawCircle(point.x, point.y, touchRadius, touchPaint)
    }

    private fun Float.toPx(): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, resources.displayMetrics)
    }

    private fun randomColor(): Int {
        val from = 100
        val until = 256
        val hsv = floatArrayOf(0f, 0f, 0f)
        Color.RGBToHSV(
            Random.nextInt(from, until),
            Random.nextInt(from, until),
            Random.nextInt(from, until),
            hsv
        )
        // HSV - Hue (0.0 - 360.0), Saturation (0.0 - 1.0), Value(0.0 - 1.0)
        // Value -> Brightness
        hsv[2] = 1f
        return Color.HSVToColor(hsv)
    }
}