package com.wd.kotlin_basic.migratetokotlin

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.annotation.SuppressLint
import android.app.Activity
import android.app.WallpaperManager
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.os.Bundle
import android.os.SystemClock
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.RelativeLayout
import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import androidx.core.view.MotionEventCompat
import com.wd.kotlin_basic.R
import java.io.File
import java.io.IOException
import java.util.Arrays
import java.util.Collections
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

//import com.eco.textonphoto.analystic.AnalyticsManager;
//import com.eco.textonphoto.analystic.Event;
//import com.eco.textonphoto.R;
//import com.eco.textonphoto.model.StickerEvent;
//import com.eco.textonphoto.util.HawkHelper;
//
//import org.greenrobot.eventbus.EventBus;
class StickerViewMigrateKotlin @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    private var showBorder = false
    private var bringToFrontCurrentSticker = false
    var isDrawSticker: Boolean = true
    var isDrawLine: Boolean = true
    var isShowBackground: Boolean = true
    var isShowIcons: Boolean = true
    private var mappingPoint = floatArrayOf(0f)
    private var allowShowLineHorizon = false
    private var allowShowLineVertical = false

    @IntDef([ActionMode.NONE, ActionMode.DRAG, ActionMode.ZOOM_WITH_TWO_FINGER, ActionMode.ICON, ActionMode.CLICK])
    @Retention(
        AnnotationRetention.SOURCE
    )
    protected annotation class ActionMode {
        companion object {
            const val NONE: Int = 0
            const val DRAG: Int = 1
            const val ZOOM_WITH_TWO_FINGER: Int = 2
            const val ICON: Int = 3
            const val CLICK: Int = 4
        }
    }

    @IntDef(flag = true, value = [FLIP_HORIZONTALLY, FLIP_VERTICALLY])
    @Retention(
        AnnotationRetention.SOURCE
    )
    protected annotation class Flip

    private val stickers: MutableList<Sticker?> = ArrayList<Sticker?>()
    private val icons: MutableList<BitmapStickerIcon> = ArrayList<BitmapStickerIcon>(4)

    private val borderPaint = Paint()
    private val borderPaint1 = Paint()
    private val lineUpPaint = Paint()
    val stickerRect: RectF = RectF()

    private val sizeMatrix = Matrix()
    private val downMatrix = Matrix()
    private val moveMatrix = Matrix()

    // region storing variables
    private val bitmapPoints = FloatArray(8)
    private val bitmapPointCurve = FloatArray(8)
    private val bounds = FloatArray(8)
    private val point = FloatArray(2)
    private val currentCenterPoint = PointF()
    private val tmp = FloatArray(2)
    private var midPoint = PointF()

    // endregion
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var x = 0f
    private var y = 0f
    private var currentIcon: BitmapStickerIcon? = null

    //the first point down position
    private var downX = 0f
    private var downY = 0f

    private var oldDistance = 0f
    private var oldRotation = 0f
    private var currentSticker = 0
    private var oldSticker = 0
    var rectCurve: RectF? = null
    private var marginSize = 40

    @ActionMode
    var currentMode: Int = ActionMode.NONE
        private set

    private var handlingSticker: Sticker? = null

    var isLocked: Boolean = false
        private set
    var isDisableClick: Boolean = false

    var isConstrained: Boolean = false
        private set

    var onStickerOperationListener: OnStickerOperationListener? = null
        private set

    private var lastClickTime: Long = 0
    var minClickDelayTime: Int = DEFAULT_MIN_CLICK_DELAY_TIME
        private set
    private var isShowLine = false

    private var originalLeft = 0f
    private var originalTop = 0f

    init {
        var a: TypedArray? = null
        try {
            a = context.obtainStyledAttributes(attrs, R.styleable.StickerView)
            isShowIcons = a.getBoolean(R.styleable.StickerView_showIcons, false)
            showBorder = a.getBoolean(R.styleable.StickerView_showBorder, false)
            bringToFrontCurrentSticker =
                a.getBoolean(R.styleable.StickerView_bringToFrontCurrentSticker, false)
            borderPaint1.style = Paint.Style.STROKE
            borderPaint1.strokeWidth = 6f
            borderPaint1.setPathEffect(DashPathEffect(floatArrayOf(15f, 20f), 0f))
            borderPaint1.isAntiAlias = true
            borderPaint1.color = Color.WHITE

            lineUpPaint.strokeWidth = 2f
            lineUpPaint.style = Paint.Style.STROKE
            lineUpPaint.color = Color.parseColor("#03A9F4")

            borderPaint.isAntiAlias = true
            borderPaint.color = a.getColor(
                R.styleable.StickerView_borderColor,
                context.resources.getColor(R.color.colorBGCustomview)
            )
            borderPaint.alpha = a.getInteger(R.styleable.StickerView_borderAlpha, 128)
            marginSize = resources.getDimensionPixelSize(R.dimen._16sdp)
            configDefaultIcons()
        } finally {
            a?.recycle()
        }
    }

    fun getStickers(): List<Sticker?> {
        return stickers
    }

    fun configDefaultIcons() {
        try {
            val deleteIcon: BitmapStickerIcon = BitmapStickerIcon(
                ContextCompat.getDrawable(context, R.drawable.ic_close_1),
                BitmapStickerIcon.LEFT_TOP
            )
            deleteIcon.setIconEvent(DeleteIconEvent())
            val zoomIcon: BitmapStickerIcon = BitmapStickerIcon(
                ContextCompat.getDrawable(context, R.drawable.ic_transform),
                BitmapStickerIcon.RIGHT_BOTOM
            )
            zoomIcon.setIconEvent(ZoomIconEvent())
            icons.clear()
            icons.add(zoomIcon)
            icons.add(deleteIcon)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun moveToTop(sticker: Sticker) {
        if (sticker is TextSticker) {
            originalLeft = getRectFBase(bitmapPoints).left
            originalTop = getRectFBase(bitmapPoints).top
            if (handlingSticker is TextSticker && (handlingSticker as TextSticker).getCurve() !== 0) {
                val rectF = getRectFBase(bitmapPointCurve)
                val stickerWidth = rectF.width()
                val leftOffset = (width - stickerWidth) / 2
                val targetX = leftOffset - rectF.left
                val targetY = height / 3f - rectF.top
                updateAnimation(sticker, rectF, targetX, targetY)
            } else {
                val rectF = getRectFBase(bitmapPoints)
                val stickerWidth = rectF.width()
                val leftOffset = (width - stickerWidth) / 2
                val targetX = leftOffset - rectF.left
                val targetY = height / 3f - rectF.top
                updateAnimation(sticker, rectF, targetX, targetY)
            }
        }
    }

    private fun updateAnimation(sticker: Sticker, rectF: RectF, targetX: Float, targetY: Float) {
        val animator = ValueAnimator.ofFloat(0f, 1f) // Tạo một ValueAnimator từ 0 đến 1
        animator.addUpdateListener(object : AnimatorUpdateListener {
            private var startX = 0f
            private var startY = 0f

            override fun onAnimationUpdate(animation: ValueAnimator) {
                val animatedFraction = animation.animatedFraction
                val newX = startX + animatedFraction * (targetX - startX)
                val newY = startY + animatedFraction * (targetY - startY)
                sticker.getMatrix().postTranslate(newX - startX, newY - startY)
                startX = newX
                startY = newY
                invalidate()
            }
        })

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
            }
        })
        animator.setDuration(500)
        animator.start()
    }


    fun moveToOriginalPosition(sticker: Sticker) {
        updateAnimation(
            sticker,
            getRectFBase(bitmapPoints),
            originalLeft - getRectFBase(bitmapPoints).left,
            originalTop - getRectFBase(bitmapPoints).top
        )
    }

    /**
     * Swaps sticker at layer [[oldPos]] with the one at layer [[newPos]].
     * Does nothing if either of the specified layers doesn't exist.
     */
    fun swapLayers(oldPos: Int, newPos: Int) {
        if (stickers.size >= oldPos && stickers.size >= newPos) {
            Collections.swap(stickers, oldPos, newPos)
            invalidate()
        }
    }

    /**
     * Sends sticker from layer [[oldPos]] to layer [[newPos]].
     * Does nothing if either of the specified layers doesn't exist.
     */
    fun sendToLayer(oldPos: Int, newPos: Int) {
        if (stickers.size >= oldPos && stickers.size >= newPos) {
            val s: Sticker? = stickers[oldPos]
            stickers.removeAt(oldPos)
            stickers.add(newPos, s)
            invalidate()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            stickerRect.left = left.toFloat()
            stickerRect.top = top.toFloat()
            stickerRect.right = right.toFloat()
            stickerRect.bottom = bottom.toFloat()
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (isDrawSticker) {
            try {
                if (!isShowBackground) {
                    val backgroundPaint = Paint()
                    backgroundPaint.color =
                        resources.getColor(R.color.color_0000000) // Màu nền đen với độ trong suốt
                    backgroundPaint.style = Paint.Style.FILL
                    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
                }
                drawStickers(canvas)
            } catch (e: Throwable) {
            }
        }
    }

    fun drawStickers(canvas: Canvas) {
        var isCurve = false

        if (handlingSticker != null && !isLocked && (showBorder || isShowIcons)) {
            getStickerPoints(handlingSticker, bitmapPoints)
        }


        for (i in stickers.indices) {
            val sticker: Sticker? = stickers[i]
            if (sticker != null) {
                sticker.draw(canvas)
            }
        }

        if (handlingSticker != null && !isLocked && (showBorder || isShowIcons)) {
            if (handlingSticker is TextSticker && (handlingSticker as TextSticker).getCurve() !== 0) {
                isCurve = true
            }
            val x1: Float
            val y1: Float
            val x2: Float
            val y2: Float
            val x3: Float
            val y3: Float
            val x4: Float
            val y4: Float
            if (isCurve) {
                getStickerPoints2(handlingSticker, bitmapPointCurve)
                x1 = bitmapPointCurve[0]
                y1 = bitmapPointCurve[1]
                x2 = bitmapPointCurve[2]
                y2 = bitmapPointCurve[3]
                x3 = bitmapPointCurve[4]
                y3 = bitmapPointCurve[5]
                x4 = bitmapPointCurve[6]
                y4 = bitmapPointCurve[7]
            } else {
                x1 = bitmapPoints[0]
                y1 = bitmapPoints[1]
                x2 = bitmapPoints[2]
                y2 = bitmapPoints[3]
                x3 = bitmapPoints[4]
                y3 = bitmapPoints[5]
                x4 = bitmapPoints[6]
                y4 = bitmapPoints[7]
            }
            val rotation = calculateRotation(x4, y4, x3, y3)

            if (showBorder) {
                canvas.save()
                canvas.rotate(rotation, (x4 - x1) / 2 + x1, (y4 - y1) / 2 + y1)
                if (handlingSticker is TextSticker && (handlingSticker as TextSticker).getCurve() !== 0) {
                    rectCurve = getRectFBase(bitmapPointCurve)
                    drawLineText(canvas, rectCurve!!)
                } else {
                    drawLineText(canvas, getRectFBase(bitmapPoints))
                    if (handlingSticker is TextSticker) {
                    }
                }
                canvas.restore()
            }


            //draw icon
            if (isShowIcons) {
                for (i in icons.indices) {
                    val icon: BitmapStickerIcon = icons[i]
                    var x = 0f
                    var y = 0f
                    when (icon.getPosition()) {
                        BitmapStickerIcon.LEFT_TOP -> {
                            x = x1
                            y = y1
                        }

                        BitmapStickerIcon.RIGHT_TOP -> {
                            x = x2
                            y = y2
                        }

                        BitmapStickerIcon.LEFT_BOTTOM -> {
                            x = x3
                            y = y3
                        }

                        BitmapStickerIcon.RIGHT_BOTOM -> {
                            x = x4
                            y = y4
                        }
                    }
                    configIconMatrix(icon, x, y, rotation)
                    icon.draw(canvas, borderPaint)
                }
            }
        }


        if (this.handlingSticker != null && !isLocked && this.allowShowLineHorizon && (currentMode == ActionMode.DRAG) && isShowLine) {
            canvas.drawLine(
                (width.toFloat()) / 2.0f,
                0f,
                (width.toFloat()) / 2.0f,
                height.toFloat(),
                lineUpPaint
            )
        }
        if (this.handlingSticker != null && !isLocked && this.allowShowLineVertical && (currentMode == ActionMode.DRAG) && isShowLine) {
            canvas.drawLine(
                0f,
                (height.toFloat()) / 2.0f,
                width.toFloat(),
                (height.toFloat()) / 2.0f,
                lineUpPaint
            )
        }
    }

    private fun drawCircle(canvas: Canvas, rectF: RectF) {
        val painCircle = Paint(Paint.ANTI_ALIAS_FLAG)
        painCircle.style = Paint.Style.FILL
        painCircle.color = Color.WHITE
        canvas.drawCircle(
            (rectF.left + rectF.right) / 2,
            rectF.top,
            resources.getDimension(R.dimen._2sdp),
            painCircle
        )
        canvas.drawCircle(
            (rectF.left + rectF.right) / 2,
            rectF.bottom,
            resources.getDimension(R.dimen._2sdp),
            painCircle
        )
        canvas.drawCircle(
            rectF.left,
            (rectF.top + rectF.bottom) / 2,
            resources.getDimension(R.dimen._2sdp),
            painCircle
        )
        canvas.drawCircle(
            rectF.right,
            (rectF.top + rectF.bottom) / 2,
            resources.getDimension(R.dimen._2sdp),
            painCircle
        )
    }

    // This method using to draw a tool rect (gray rect when add new sticker)
    private fun getRectFBase(coordinate: FloatArray): RectF {
        val x1 = coordinate[0]
        val y1 = coordinate[1]
        val x2 = coordinate[2]
        val y2 = coordinate[3]
        val x3 = coordinate[4]
        val y3 = coordinate[5]
        val x4 = coordinate[6]
        val y4 = coordinate[7]

        val topBound = calculateDistance(x1, y1, x2, y2) / 2
        val leftBound = calculateDistance(x1, y1, x3, y3) / 2
        val middlePointX = (x4 - x1) / 2 + x1
        val middlePointY = (y4 - y1) / 2 + y1

        stickerRect.top = middlePointY - leftBound
        stickerRect.left = middlePointX - topBound
        stickerRect.right = middlePointX + topBound
        stickerRect.bottom = middlePointY + leftBound

        //if (leftBound <= 30) {
        // stickerRect.top -= 15;
        // stickerRect.bottom += 15;
        // }
        return stickerRect
    }

    fun drawLineText(canvas: Canvas, rect: RectF) {
        if (isDrawLine) {
            val paint = Paint()
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2f
            paint.color = Color.WHITE
            canvas.drawRect(rect, paint)
            drawCircle(canvas, rect)
        }
    }

    protected fun configIconMatrix(icon: BitmapStickerIcon, x: Float, y: Float, rotation: Float) {
        icon.setX(x)
        icon.setY(y)
        icon.getMatrix().reset()
        icon.getMatrix().postRotate(rotation, icon.getWidth() / 2, icon.getHeight() / 2)
        icon.getMatrix().postTranslate(x - icon.getWidth() / 2, y - icon.getHeight() / 2)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.x
                downY = ev.y
            }
        }

        return super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = MotionEventCompat.getActionMasked(event)
        if (!isDisableClick) {
            when (action) {
                MotionEvent.ACTION_DOWN -> if (!onTouchDown(event)) {
                    return false
                }

                MotionEvent.ACTION_POINTER_DOWN -> {
                    oldDistance = calculateDistance(event)
                    oldRotation = calculateRotation(event)
                    midPoint = calculateMidPoint(event)

                    if (handlingSticker != null && isInStickerArea(
                            handlingSticker,
                            event.getX(1),
                            event.getY(1)
                        ) && findCurrentIconTouched() == null
                    ) {
                        currentMode = ActionMode.ZOOM_WITH_TWO_FINGER
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    handleCurrentMode(event)
                    invalidate()
                }

                MotionEvent.ACTION_UP -> {
                    this.allowShowLineVertical = false
                    this.allowShowLineHorizon = false
                    updatePositionSticker()
                    onTouchUp(event)
                }

                MotionEvent.ACTION_POINTER_UP -> {
                    if (currentMode == ActionMode.ZOOM_WITH_TWO_FINGER && handlingSticker != null) {
                        if (onStickerOperationListener != null) {
                            onStickerOperationListener!!.onStickerZoomFinished(handlingSticker)
                        }
                    }
                    currentMode = ActionMode.NONE
                }
            }
        }
        return true
    }

    /**
     * @param event MotionEvent received from [)][.onTouchEvent]
     */
    protected fun onTouchDown(event: MotionEvent): Boolean {
        currentMode = ActionMode.DRAG
        isShowLine = true
        downX = event.x
        downY = event.y
        midPoint = calculateMidPoint()

        oldDistance = calculateDistance(midPoint.x, midPoint.y, downX, downY)
        oldRotation = calculateRotation(midPoint.x, midPoint.y, downX, downY)

        currentIcon = findCurrentIconTouched()
        if (currentIcon != null) {
            currentMode = ActionMode.ICON
            currentIcon.onActionDown(this, event)
        } else {
            handlingSticker = findHandlingSticker()
        }
        if (handlingSticker != null) {
            onStickerOperationListener!!.onStickerTouchedDown(handlingSticker)
            downMatrix.set(handlingSticker.getMatrix())

            mappingPoint = floatArrayOf(handlingSticker.getMappedBound().centerX())
            // Ánh xạ tọa độ tâm x của sticker qua ma trận hiện tại. Ở đây là ma trận đã lưu trước đó
            downMatrix.mapPoints(mappingPoint)
            if (bringToFrontCurrentSticker) {
                stickers.remove(handlingSticker)
                stickers.add(handlingSticker)
            }
        } else {
            EventBus.getDefault().postSticky(StickerEvent(false))
        }

        stickerCheck()

        if (currentIcon == null && handlingSticker == null) {
            invalidate()
            return false
        }
        invalidate()
        return true
    }

    fun clearSelectedSticker() {
        Log.d("LAM", "clearSelectedSticker: ")
        handlingSticker = null
    }

    fun findLastSticker(): Sticker? {
        if (getStickers().size > 0) {
            handlingSticker = getStickers()[getStickers().size - 1]
            return handlingSticker
        }
        return null
    }

    protected fun onTouchUp(event: MotionEvent) {
        isShowLine = false
        val currentTime = SystemClock.uptimeMillis()

        if (currentMode == ActionMode.ICON && currentIcon != null && handlingSticker != null) {
            currentIcon.onActionUp(this, event)
        }

        if (currentMode == ActionMode.DRAG && abs((event.x - downX).toDouble()) < touchSlop && abs((event.y - downY).toDouble()) < touchSlop && handlingSticker != null) {
            currentMode = ActionMode.CLICK
            if (onStickerOperationListener != null) {
                onStickerOperationListener!!.onStickerClicked(handlingSticker)
            }
            if (currentTime - lastClickTime < minClickDelayTime) {
                if (onStickerOperationListener != null) {
                    onStickerOperationListener!!.onStickerDoubleTapped(handlingSticker)
                }
            }
            invalidate()
        }


        if (currentMode == ActionMode.DRAG && handlingSticker != null) {
            if (onStickerOperationListener != null) {
                onStickerOperationListener!!.onStickerDragFinished(handlingSticker)
                invalidate()
            }
        }

        currentMode = ActionMode.NONE
        lastClickTime = currentTime

        // Đặt lại tọa độ x và khoảng cách đã di chuyển được của ngón tay
    }

    protected fun handleCurrentMode(event: MotionEvent) {
        when (currentMode) {
            ActionMode.NONE, ActionMode.CLICK -> {}
            ActionMode.DRAG -> if (handlingSticker != null) {
                moveMatrix.set(downMatrix)
                moveMatrix.postTranslate(event.x - downX, event.y - downY)
                handlingSticker.setMatrix(moveMatrix)
                if (isConstrained) {
                    constrainSticker(handlingSticker)
                }
                caculateRangerLockSticker(handlingSticker)
            }

            ActionMode.ZOOM_WITH_TWO_FINGER -> if (handlingSticker != null) {
                var newDistance = calculateDistance(event)
                val newRotation = calculateRotation(event)
                if (newDistance <= width.toFloat() / 15f) {
                    newDistance = width.toFloat() / 15f
                    if (oldDistance <= width / 15f) {
                        oldDistance = width / 15f
                    }
                }
                moveMatrix.set(downMatrix)
                moveMatrix.postScale(
                    newDistance / oldDistance,
                    newDistance / oldDistance,
                    midPoint.x,
                    midPoint.y
                )
                moveMatrix.postRotate(newRotation - oldRotation, midPoint.x, midPoint.y)
                handlingSticker.setMatrix(moveMatrix)
            }

            ActionMode.ICON -> if (handlingSticker != null && currentIcon != null) {
                currentIcon.onActionMove(this, event)
            }
        }
    }

    fun zoomAndRotateCurrentSticker(event: MotionEvent) {
        zoomAndRotateSticker(handlingSticker, event)
    }

    fun zoomAndRotateSticker(sticker: Sticker?, event: MotionEvent) {
        if (sticker != null) {
            var newDistance = calculateDistance(midPoint.x, midPoint.y, event.x, event.y)
            val newRotation = calculateRotation(midPoint.x, midPoint.y, event.x, event.y)
            moveMatrix.set(downMatrix)

            if (newDistance <= width / 15f) {
                newDistance = width / 15f
                if (oldDistance < width / 15f) {
                    oldDistance = width / 15f
                }
            }
            moveMatrix.postScale(
                newDistance / oldDistance,
                newDistance / oldDistance,
                midPoint.x,
                midPoint.y
            )

            // caculateRangerCornerSticker(handlingSticker,newRotation);
            moveMatrix.postRotate(newRotation - oldRotation, midPoint.x, midPoint.y)
            handlingSticker.setMatrix(moveMatrix)
        }
    }

    fun setTextSizeUp(sticker: Sticker?) {
        if (sticker != null) {
            sticker.getMatrix().postScale(1f, 1.05f, x, y)
        }
    }

    fun setTextSizeDown(sticker: Sticker?) {
        if (sticker != null) {
            sticker.getMatrix().postScale(1f, (100f / 105f), x, y)
        }
    }


    // NKP : Sample for rotate sticker
    fun rotateSticker(sticker: Sticker?, rotate: Float, scale: Float, point: PointF) {
        if (sticker != null && sticker.rotate) {
            val matrix: Matrix = sticker.getMatrix()
            matrix.reset()
            matrix.postScale(scale, scale, sticker.getCenterPoint().x, sticker.getCenterPoint().y)
            matrix.postRotate(rotate, sticker.getCenterPoint().x, sticker.getCenterPoint().y)
            matrix.postTranslate(
                point.x - sticker.getWidth() / 2f,
                point.y - sticker.getHeight() / 2f
            )
            handlingSticker.setMatrix(matrix)
            invalidate()
        }
    }

    protected fun constrainSticker(sticker: Sticker) {
        var moveX = 0f
        var moveY = 0f
        val width = width
        val height = height

        if (sticker is TextSticker && (sticker as TextSticker).getCurve() !== 0) {
            sticker.getMappedCenterPointCurve(
                currentCenterPoint,
                point,
                tmp,
                (sticker as TextSticker).getTextRectCurve()
            )
        } else {
            sticker.getMappedCenterPoint(currentCenterPoint, point, tmp)
        }
        if (currentCenterPoint.x < 0) {
            moveX = -currentCenterPoint.x
        }

        if (currentCenterPoint.x > width) {
            moveX = width - currentCenterPoint.x
        }

        if (currentCenterPoint.y < 0) {
            moveY = -currentCenterPoint.y
        }

        if (currentCenterPoint.y > height) {
            moveY = height - currentCenterPoint.y
        }
        sticker.getMatrix().postTranslate(moveX, moveY)
    }

    protected fun findCurrentIconTouched(): BitmapStickerIcon? {
        for (icon in icons) {
            val x: Float = icon.getX() - downX
            val y: Float = icon.getY() - downY
            val distance_pow_2 = x * x + y * y

            // Thêm bán kính bổ sung để tăng vùng chọn
            val additionalRadius =
                resources.getDimension(R.dimen._12sdp) // Giá trị có thể điều chỉnh
            val touchRadius: Float = icon.getIconRadius() + additionalRadius

            if (distance_pow_2 <= touchRadius * touchRadius) {
                return icon
            }
        }
        return null
    }

    /**
     * find the touched Sticker
     */
    protected fun findHandlingSticker(): Sticker? {
        for (i in stickers.indices.reversed()) {
            if (isInStickerArea(stickers[i], downX, downY)) {
                oldSticker = currentSticker
                currentSticker = i
                return stickers[i]
            }
        }
        return null
    }

    protected fun isInStickerArea(sticker: Sticker, downX: Float, downY: Float): Boolean {
        tmp[0] = downX
        tmp[1] = downY
        return sticker.contains(tmp)
    }

    protected fun calculateMidPoint(event: MotionEvent?): PointF {
        if (event == null || event.pointerCount < 2) {
            midPoint[0f] = 0f
            return midPoint
        }
        val x = (event.getX(0) + event.getX(1)) / 2
        val y = (event.getY(0) + event.getY(1)) / 2
        midPoint[x] = y
        return midPoint
    }

    protected fun calculateMidPoint(): PointF {
        if (handlingSticker == null) {
            midPoint[0f] = 0f
            return midPoint
        }
        if (handlingSticker is TextSticker && (handlingSticker as TextSticker).getCurve() !== 0) {
            handlingSticker.getMappedCenterPointCurve(
                midPoint,
                point,
                tmp,
                (handlingSticker as TextSticker).getTextRectCurve()
            )
        } else {
            handlingSticker.getMappedCenterPoint(midPoint, point, tmp)
        }
        return midPoint
    }

    /**
     * calculate rotation in line with two fingers and x-axis
     */
    protected fun calculateRotation(event: MotionEvent?): Float {
        if (event == null || event.pointerCount < 2) {
            return 0f
        }
        return calculateRotation(event.getX(0), event.getY(0), event.getX(1), event.getY(1))
    }

    protected fun calculateRotation(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val x = (x1 - x2).toDouble()
        val y = (y1 - y2).toDouble()
        val radians = atan2(y, x)
        return Math.toDegrees(radians).toFloat()
    }

    /**
     * calculate Distance in two fingers
     */
    protected fun calculateDistance(event: MotionEvent?): Float {
        if (event == null || event.pointerCount < 2) {
            return 0f
        }
        return calculateDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1))
    }

    protected fun calculateDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val x = (x1 - x2).toDouble()
        val y = (y1 - y2).toDouble()

        return sqrt(x * x + y * y) as Float
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        if (onStickerOperationListener != null) {
            onStickerOperationListener!!.onSizeChange(w, h, oldW, oldH)
        }
    }

    fun setSize(w: Int, h: Int) {
        onSizeChanged(w, h, w, h)
    }

    fun gava(sticker: Sticker?) {
        if (sticker == null) {
            return
        }

        val width = width.toFloat()
        val height = height.toFloat()
        val stickerWidth: Float = sticker.getWidth()
        val stickerHeight: Float = sticker.getHeight()
        //step 1
        val offsetX = (width - stickerWidth) / 2
        val offsetY = (height - stickerHeight) / 2

        sizeMatrix.postTranslate(offsetX, offsetY)
        invalidate()
    }

    /**
     * Sticker's drawable will be too bigger or smaller
     * This method is to transform it to fit
     * step 1：let the center of the sticker image is coincident with the center of the View.
     * step 2：Calculate the zoom and zoom
     */
    protected fun transformSticker(sticker: Sticker?) {
        if (sticker == null) {
            return
        }

        sizeMatrix.reset()

        val width = width.toFloat()
        val height = height.toFloat()
        val stickerWidth: Float = sticker.getWidth()
        val stickerHeight: Float = sticker.getHeight()
        //step 1
        val offsetX = (width - stickerWidth) / 2
        val offsetY = (height - stickerHeight) / 2

        sizeMatrix.postTranslate(offsetX, offsetY)

        //step 2
        val scaleFactor = if (width < height) {
            width / stickerWidth
        } else {
            height / stickerHeight
        }

        sizeMatrix.postScale(scaleFactor / 2f, scaleFactor / 2f, width / 2f, height / 2f)

        sticker.getMatrix().reset()
        sticker.setMatrix(sizeMatrix)

        invalidate()
    }

    fun flipCurrentSticker(direction: Int) {
        flip(handlingSticker, direction)
    }

    fun flip(sticker: Sticker?, @Flip direction: Int) {
        if (sticker != null) {
            sticker.getCenterPoint(midPoint)
            if ((direction and FLIP_HORIZONTALLY) > 0) {
                sticker.getMatrix().preScale(-1, 1, midPoint.x, midPoint.y)
                sticker.setFlippedHorizontally(!sticker.isFlippedHorizontally())
            }
            if ((direction and FLIP_VERTICALLY) > 0) {
                sticker.getMatrix().preScale(1, -1, midPoint.x, midPoint.y)
                sticker.setFlippedVertically(!sticker.isFlippedVertically())
            }

            if (onStickerOperationListener != null) {
                onStickerOperationListener!!.onStickerFlipped(sticker)
            }

            invalidate()
        }
    }

    fun replace(sticker: Sticker?): Boolean {
        return replace(sticker, false)
    }

    fun replace(sticker: Sticker?, needStayState: Boolean): Boolean {
        if (handlingSticker != null && sticker != null) {
            val width = width.toFloat()
            val height = height.toFloat()
            if (needStayState) {
                sticker.setMatrix(handlingSticker.getMatrix())
                sticker.setFlippedVertically(handlingSticker.isFlippedVertically())
                sticker.setFlippedHorizontally(handlingSticker.isFlippedHorizontally())
            } else {
                handlingSticker.getMatrix().reset()
                // reset scale, angle, and put it in center
                val offsetX: Float = (width - handlingSticker.getWidth()) / 2f
                val offsetY: Float = (height - handlingSticker.getHeight()) / 2f
                sticker.getMatrix().postTranslate(offsetX, offsetY)

                val scaleFactor: Float
                if (width < height) {
                    scaleFactor = width / handlingSticker.getDrawable().getIntrinsicWidth()
                } else {
                    scaleFactor = height / handlingSticker.getDrawable().getIntrinsicHeight()
                }
                sticker.getMatrix()
                    .postScale(scaleFactor / 2f, scaleFactor / 2f, width / 2f, height / 2f)
            }
            val index = stickers.indexOf(handlingSticker)
            stickers[index] = sticker
            handlingSticker = sticker

            invalidate()
            return true
        } else {
            return false
        }
    }

    fun remove(sticker: Sticker?): Boolean {
        if (stickers.contains(sticker)) {
            stickers.remove(sticker)
            if (onStickerOperationListener != null) {
                onStickerOperationListener!!.onStickerDeleted(sticker)
            }
            if (handlingSticker === sticker) {
                handlingSticker = null
            }
            invalidate()

            return true
        } else {
            return false
        }
    }

    fun removeCurrentSticker(): Boolean {
        return remove(handlingSticker)
    }

    fun removeAllStickers() {
        stickers.clear()
        if (handlingSticker != null) {
            handlingSticker.release()
            handlingSticker = null
        }
        invalidate()
    }

    fun addSticker(sticker: Sticker): StickerViewMigrateKotlin {
        return addSticker(sticker, Sticker.Position.CENTER)
    }

    fun addSticker(sticker: Sticker, @Sticker.Position position: Int): StickerViewMigrateKotlin {
        Log.d("LAM", "addSticker: ")
        post { addStickerImmediately(sticker, position) }
        return this
    }

    protected fun addStickerImmediately(sticker: Sticker, @Sticker.Position position: Int) {
        setStickerPosition(sticker, position)

        val scaleFactor: Float
        val widthScaleFactor: Float
        val heightScaleFactor: Float

        widthScaleFactor = width.toFloat() / sticker.getDrawable().getIntrinsicWidth()
        heightScaleFactor = height.toFloat() / sticker.getDrawable().getIntrinsicHeight()
        scaleFactor =
            if (widthScaleFactor > heightScaleFactor) heightScaleFactor else widthScaleFactor
        if (sticker is DrawableSticker) {
            sticker.getMatrix().postScale(scaleFactor / 4, scaleFactor / 4, width / 2, height / 2)
        } else if (sticker is BitmapSticker) {
            val displayMetrics = DisplayMetrics()
            (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels
            val targetWidth = width / 3f
            val originalWidth: Float = sticker.getWidth()
            val scale = targetWidth / originalWidth
            sticker.getMatrix().postScale(scale, scale, getWidth() / 2, height / 2)
        } else {
            sticker.getMatrix().postScale(scaleFactor / 2, scaleFactor / 2, width / 2, height / 2)
        }

        handlingSticker = sticker
        stickers.add(sticker)
        if (currentSticker < stickers.size) {
            stickers[currentSticker].setSelected(false)
        } else {
            stickers[stickers.size - 1].setSelected(false)
        }
        if (onStickerOperationListener != null) {
            onStickerOperationListener!!.onStickerAdded(sticker)
        }
        updatePositionSticker()
        invalidate()
    }


    private fun updatePositionSticker() {
        try {
            if (handlingSticker is TextSticker && (handlingSticker as TextSticker).getCurve() !== 0) {
                handlingSticker.getMappedCenterPointCurve(
                    currentCenterPoint,
                    point,
                    tmp,
                    (handlingSticker as TextSticker).getTextRectCurve()
                )
                x =
                    handlingSticker.getMappedCenterPointCurve((handlingSticker as TextSticker).getTextRectCurve()).x
                y =
                    handlingSticker.getMappedCenterPointCurve((handlingSticker as TextSticker).getTextRectCurve()).y
            } else {
                x = handlingSticker.getMappedCenterPoint().x
                y = handlingSticker.getMappedCenterPoint().y
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setStickerPosition(sticker: Sticker, @Sticker.Position position: Int) {
        val width = width.toFloat()
        val height = height.toFloat()
        var offsetX: Float = width - sticker.getWidth()
        var offsetY: Float = height - sticker.getHeight()
        if ((position and Sticker.Position.TOP) > 0) {
            offsetY /= 4f
        } else if ((position and Sticker.Position.BOTTOM) > 0) {
            offsetY *= 3f / 4f
        } else {
            offsetY /= 2f
        }
        if ((position and Sticker.Position.LEFT) > 0) {
            offsetX /= 4f
        } else if ((position and Sticker.Position.RIGHT) > 0) {
            offsetX *= 3f / 4f
        } else {
            offsetX /= 2f
        }

        sticker.getMatrix().postTranslate(offsetX, offsetY)
    }

    fun getStickerPoints(sticker: Sticker?, dst: FloatArray) {
        if (sticker == null) {
            Arrays.fill(dst, 0f)
            return
        }
        sticker.getBoundPoints(bounds)
        sticker.getMappedPoints(dst, bounds)
    }

    fun getStickerPoints2(sticker: Sticker?, dst: FloatArray) {
        if (sticker == null) {
            Arrays.fill(dst, 0f)
            return
        }
        (sticker as TextSticker).getBoundPointCurve(bounds)
        sticker.getMappedPoints(dst, bounds)
    }

    fun save(
        context: Context,
        file: File,
        setWallpaper: Boolean,
        listener: StickerUtils.ProgressListener?
    ): String {
        try {
            val path: String =
                StickerUtils.saveImageToGallery(getContext(), file, createBitmap(), listener)
            if (setWallpaper && HawkHelper.isWallPaperAuto()) {
                val bmOptions = BitmapFactory.Options()
                val bitmap = BitmapFactory.decodeFile(file.path, bmOptions)
                val wallpaperManager = WallpaperManager.getInstance(context.applicationContext)
                try {
                    wallpaperManager.setBitmap(bitmap)
                } catch (e: IOException) {
                } catch (e: NullPointerException) {
                }
            }
            return path
        } catch (ignored: IllegalArgumentException) {
            return ""
        } catch (ignored: IllegalStateException) {
            return ""
        }
    }

    fun saveImage(context: Context, folderName: String?): String {
        return StickerUtils.saveImageToExternalFileDir(context, createBitmapForSave(), folderName)
    }

    fun createBitmap(): Bitmap {
        if (isDrawSticker) {
            handlingSticker = null
        }
        if (currentSticker < stickers.size) {
            stickers[currentSticker].setSelected(false)
        }
        var bitmap: Bitmap
        try {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            this.draw(canvas)
        } catch (error: OutOfMemoryError) {
            AnalyticsManager.getInstance().trackEvent(Event("OUTOFMEMORY_CREATEBITMAP", Bundle()))
            val height = 300 * width / height
            bitmap = Bitmap.createBitmap(300, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            this.draw(canvas)
        } catch (error: Exception) {
            bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val paint = Paint()
            paint.color = Color.BLACK
            canvas.drawRect(0f, 0f, 300f, 300f, paint)
        }
        return bitmap
    }

    fun createBitmapForSave(): Bitmap? {
        if (isDrawSticker) {
            handlingSticker = null
        }
        if (currentSticker < stickers.size) {
            stickers[currentSticker].setSelected(false)
        }
        val bitmap: Bitmap
        try {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            this.draw(canvas)
        } catch (error: OutOfMemoryError) {
            AnalyticsManager.getInstance().trackEvent(Event("OUTOFMEMORY_CREATEBITMAP", Bundle()))
            return null
        } catch (error: Exception) {
            return null
        }
        return bitmap
    }

    val stickerCount: Int
        get() = stickers.size

    val isNoneSticker: Boolean
        get() = stickerCount == 0

    fun setLocked(locked: Boolean): StickerViewMigrateKotlin {
        this.isLocked = locked
        invalidate()
        return this
    }

    fun setMinClickDelayTime(minClickDelayTime: Int): StickerViewMigrateKotlin {
        this.minClickDelayTime = minClickDelayTime
        return this
    }

    fun setConstrained(constrained: Boolean): StickerViewMigrateKotlin {
        this.isConstrained = constrained
        postInvalidate()
        return this
    }

    fun setOnStickerOperationListener(onStickerOperationListener: OnStickerOperationListener?): StickerViewMigrateKotlin {
        this.onStickerOperationListener = onStickerOperationListener
        return this
    }

    fun getCurrentSticker(): Sticker? {
        return handlingSticker
    }

    fun getIcons(): List<BitmapStickerIcon> {
        return icons
    }

    fun setIcons(icons: List<BitmapStickerIcon>) {
        this.icons.clear()
        Log.d("LAM", "setIcons: " + icons.size)
        this.icons.addAll(icons)
        invalidate()
    }

    private fun stickerCheck() {
        if (handlingSticker != null) {
            if (currentSticker < stickers.size) {
                stickers[currentSticker].setSelected(true)
            }
            if (oldSticker != currentSticker && oldSticker < stickers.size) {
                stickers[oldSticker].setSelected(false)
            }
        } else {
            if (currentSticker < stickers.size) {
                stickers[currentSticker].setSelected(false)
            }
        }
    }

    interface OnStickerOperationListener {
        fun onStickerAdded(sticker: Sticker)

        fun onStickerClicked(sticker: Sticker)

        fun onStickerDeleted(sticker: Sticker)

        fun onStickerDragFinished(sticker: Sticker)

        fun onStickerTouchedDown(sticker: Sticker)

        fun onStickerZoomFinished(sticker: Sticker)

        fun onStickerFlipped(sticker: Sticker)

        fun onStickerDoubleTapped(sticker: Sticker)

        fun onSizeChange(w: Int, h: Int, oldW: Int, oldH: Int)
    }

    fun caculateRangerLockSticker(sticker: Sticker) {
        val dx: Float
        if (sticker is TextSticker && (sticker as TextSticker).getCurve() !== 0) {
            sticker.getMappedCenterPointCurve(
                currentCenterPoint,
                point,
                tmp,
                (sticker as TextSticker).getTextRectCurve()
            )
        } else {
            sticker.getMappedCenterPoint(currentCenterPoint, point, tmp)
        }
        var dy = 0f
        if (currentCenterPoint.x <= ((width.toFloat()) / 2.0f) - 15.0f || currentCenterPoint.x >= ((width.toFloat()) / 2.0f) + 15.0f) {
            this.allowShowLineHorizon = false
            dx = 0f
        } else {
            dx = ((width.toFloat()) / 2.0f) - currentCenterPoint.x
            this.allowShowLineHorizon = true
        }
        if (currentCenterPoint.y <= ((height.toFloat()) / 2.0f) - 15.0f || currentCenterPoint.y >= ((height.toFloat()) / 2.0f) + 15.0f) {
            this.allowShowLineVertical = false
        } else {
            dy = ((height.toFloat()) / 2.0f) - currentCenterPoint.y
            this.allowShowLineVertical = true
        }
        sticker.getMatrix().postTranslate(dx, dy)
    }

    companion object {
        private const val DEFAULT_MIN_CLICK_DELAY_TIME = 250

        const val FLIP_HORIZONTALLY: Int = 1
        const val FLIP_VERTICALLY: Int = 1 shl 1
    }
}
