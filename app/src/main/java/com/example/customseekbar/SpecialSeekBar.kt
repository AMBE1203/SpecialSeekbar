package com.example.customseekbar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.roundToInt


class SpecialSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    companion object {
        const val DEFAULT_MAX = 100
        const val DEFAULT_MIN = 0
        const val DEFAULT_TEXT_SIZE = 18
        const val DEFAULT_RADIUS = 12f
        const val DEFAULT_TEXT_COLOR = Color.BLACK
        const val DEFAULT_THUMB_COLOR = Color.BLACK
        const val DEFAULT_BAR_PADDING = 12f
        const val DEFAULT_BAR_HEIGHT = 12f
        const val DEFAULT_THUMB_HEIGHT = 3 * DEFAULT_BAR_HEIGHT

        const val DEFAULT_BAR_COLOR = Color.GRAY
        const val DEFAULT_PROGRESS_COLOR = Color.CYAN
        const val INVALID_VALUE = -1


    }

    private var paintBar: Paint? = null
    private var paintThumb: Paint? = null
    private var paintProgress: Paint? = null

    private var rectFBar: RectF? = null
    private var rectFProgress: RectF? = null

    private var textColor: Int? = null
    private var thumbColor: Int? = null
    private var barColor: Int? = null
    private var progressColor: Int? = null
    private val mEnabled = true


    private var progress: Float? = 0f
    private var max: Int? = DEFAULT_MAX
    private var min: Int? = DEFAULT_MIN
    private var thumbHeight: Float = DEFAULT_THUMB_HEIGHT
    private var barPadding: Float = DEFAULT_BAR_PADDING * resources.displayMetrics.density
    private var barHeight: Float = DEFAULT_BAR_HEIGHT
    private var cornerRadius: Float = DEFAULT_RADIUS
    private var mWidth = 200
    private var listener: OnCustomsSeekbarChangeListener? = null


    init {

        context.obtainStyledAttributes(attrs, R.styleable.SpecialSeekBar, 0, 0).apply {
            try {

                //todo custom attrs

            } finally {
                recycle()
            }
        }




        paintThumb = Paint().apply {
            color = Color.BLUE!!
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        paintThumb?.setShadowLayer(10f, 0f, 2.0f, Color.GRAY)

        setLayerType(LAYER_TYPE_SOFTWARE, paintThumb)


        paintProgress = Paint().apply {
            color = Color.RED!!
            style = Paint.Style.FILL
            isAntiAlias = true
        }




        paintBar = Paint().apply {
            color = Color.CYAN!!
            style = Paint.Style.FILL
            isAntiAlias = true
        }


        rectFBar = RectF()
        rectFProgress = RectF()
        barHeight = getBarHeight()

    }

    private fun getBarHeight(): Float {
        return if (barHeight > 0) barHeight else thumbHeight * 0.5f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val w = resolveAdjustedSize(mWidth, widthMeasureSpec)
        val h = resolveAdjustedSize(mWidth, heightMeasureSpec)
        val d = Math.min(w, h)
        setMeasuredDimension(d, d)

    }

    private fun resolveAdjustedSize(desSize: Int, measureSpec: Int): Int {
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        var result: Int? = null

        when (mode) {
            MeasureSpec.EXACTLY -> result = size
            MeasureSpec.AT_MOST -> result = Math.min(desSize, size)
            MeasureSpec.UNSPECIFIED -> result = desSize

        }

        return result!!

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBar(canvas, paintBar!!)
        drawProgress(canvas, paintProgress!!)
        drawThumb(canvas, paintThumb)


    }

    private fun drawProgress(canvas: Canvas, paint: Paint) {

        val path = Path()

        val w1 = barHeight
        val h1 = barHeight
        val left1 = (0.5f * (height - barHeight) - w1 * 0.5f)
        val top1 = (height - h1) / 2.0f
        path.arcTo(RectF(left1, top1, left1 + w1, top1 + h1), 90f, 180f)


        path.moveTo(100f - barHeight / 2, 0.5f * (height - barHeight))
        path.lineTo(mP + 100f, 0.5f * height - w1 * 2f)
        path.lineTo(mP + 100f, 0.5f * height + w1 * 2f)
        path.lineTo(100f - barHeight / 2, 0.5f * (height + barHeight))


        path.close()

        canvas.drawPath(path, paint)
    }


    private fun drawThumb(canvas: Canvas?, paintThumb: Paint?) {

        // draw thumb
        canvas?.drawCircle(
            mP + 0.5f * (height - barHeight),
            height * 0.5f,
            thumbHeight,
            paintThumb!!
        )


    }

    private fun drawBar(canvas: Canvas, paint: Paint) {

        val path = Path()

        val w = 4 * barHeight
        val h = 4 * barHeight
        val left = (width.toFloat() - 100f - w * 0.5f)
        val top = (height - h) * 0.5f
        path.arcTo(RectF(left, top, left + w, top + h), 270f, 180f)


        val w1 = barHeight
        val h1 = barHeight
        val left1 = (0.5f * (height - barHeight) - w1 * 0.5f)
        val top1 = (height - h1) / 2.0f
        path.arcTo(RectF(left1, top1, left1 + w1, top1 + h1), 90f, 180f)


        path.moveTo(100f, 0.5f * (height - barHeight))
        path.lineTo(width.toFloat() - 100f, 0.5f * height - w1 * 2f)
        path.lineTo(width.toFloat() - 100f, 0.5f * height + w1 * 2f)
        path.lineTo(100f, 0.5f * (height + barHeight))

        path.close()

        canvas.drawPath(path, paint)


    }


    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (mEnabled) {

            parent.requestDisallowInterceptTouchEvent(true)

            when (event.action) {
                MotionEvent.ACTION_DOWN ->
                    listener?.onStartTrackingTouch(this)


                MotionEvent.ACTION_MOVE -> updateOnTouchEvent(event)

                MotionEvent.ACTION_UP -> {
                    listener?.onStopTrackingTouch(this)
                    isPressed = false
                    parent.requestDisallowInterceptTouchEvent(false)
                }
                MotionEvent.ACTION_CANCEL -> {
                    listener?.onStopTrackingTouch(this)
                    isPressed = false
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }

            return true
        }
        return false
    }

    private fun updateOnTouchEvent(event: MotionEvent) {
        isPressed = true

        val x = event.x.roundToInt()
        val y = event.y.roundToInt()

        val availableWidth = width - paddingRight - paddingLeft - 200f

        var scale = -1f
        var progress = 0.0f

        if (x > width - paddingRight - 100f) {
            scale = 1.0f
        } else if (x < paddingLeft + 100f) {
            scale = 0.0f
        } else {
            scale = (x - paddingLeft - 100f) / availableWidth
        }

        val range = max!! - min!!
        progress += scale * range + min!!
        mP = scale * availableWidth + 0.5f

        listener?.onChanged(this, progress.roundToInt(), true)

        invalidate()


    }


    private var mP = 0f

    fun setListener(mListener: OnCustomsSeekbarChangeListener) {
        listener = mListener
    }

    interface OnCustomsSeekbarChangeListener {
        fun onChanged(seekbarVertical: SpecialSeekBar, progress: Int, frommUser: Boolean)

        fun onStartTrackingTouch(seekbarVertical: SpecialSeekBar)

        fun onStopTrackingTouch(seekbarVertical: SpecialSeekBar)

    }


}