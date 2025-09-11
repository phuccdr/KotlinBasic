package com.wd.kotlin_basic.migratetokotlin

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
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
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.RelativeLayout
import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import com.wd.kotlin_basic.R
import java.io.File
import java.io.IOException
import java.util.Arrays
import java.util.Collections
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt


private const val TAG = "StickerViewKotlin"

/**
 * vi sao dung @JvmOverload?
 * Vi @JvmOverload tao ra nh constructor trong bytecode ( bytecode la ma trung gian giup JVM hieu duoc ) mac du kotlin co default parameter nhung bytecode thi khong
 * Trong custom xml can co constructor (Context, AttributeSet) nen ta sd @JvmOverload.
 * **/
class StickerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    /**
     * constants trong kotlin chi duoc khai bao trong object, companion object hoac top level
     */
    companion object {
        private const val DEFAULT_MIN_CLICK_DELAY_TIME = 250
        const val FLIP_HORIZONTALLY = 1
        const val FLIP_VERTICALLY = 1 shl 1
    }


    private var showBorder = false

    private var bringToFrontCurrentSticker = false

    private var drawSticker: Boolean = true

    private var drawLine: Boolean = true

    var showBackground: Boolean = true

    private var showIcons: Boolean = true

    private var mappingPoint = floatArrayOf(0f)

    private var allowShowLineHorizon = false

    private var allowShowLineVertical = false

    /**
     * vi sao dung object: vi chi tao 1 instance (singleton ) duy nhat có th truy cap truc tiep ma khong can khoi tao
     * IntDef - android support library kiem tra type safety tại compile-time tao cac const cho enum tranh sau gan cac gia tri khac ngoai gia tri đã duoc dinh nghia truoc
     * annotion class = @interface
     */
    @Target(
        AnnotationTarget.TYPE,
        AnnotationTarget.VALUE_PARAMETER,
        AnnotationTarget.PROPERTY,
        AnnotationTarget.FIELD
    )
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(
        ActionMode.NONE,
        ActionMode.DRAG,
        ActionMode.ZOOM_WITH_TWO_FINGER,
        ActionMode.ICON,
        ActionMode.CLICK
    )
    protected annotation class ActionModeType

    object ActionMode {
        const val NONE = 0
        const val DRAG = 1
        const val ZOOM_WITH_TWO_FINGER = 2
        const val ICON = 3
        const val CLICK = 4
    }

    @Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(flag = true, value = [FLIP_HORIZONTALLY, FLIP_VERTICALLY])
    protected annotation class Flip

    private val stickers: MutableList<Sticker> = ArrayList<Sticker>()
    private val icons: MutableList<BitmapStickerIcon> = ArrayList(4)

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
    private var touchSlop = ViewConfiguration.get(context).scaledTouchSlop
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

    @ActionModeType
    private var currentMode = ActionMode.NONE

    private var handlingSticker: Sticker? = null

    var locked: Boolean = false
        private set
    var disableClick: Boolean = false

    var constrained: Boolean = false
        private set

    var onStickerOperationListener: OnStickerOperationListener? = null
        private set

    private var lastClickTime: Long = 0
    var minClickDelayTime: Int = DEFAULT_MIN_CLICK_DELAY_TIME
        private set
    private var isShowLine = false

    private var originalLeft = 0f
    private var originalTop = 0f

    fun getCurrentMode(): Int = currentMode

    init {
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        var a: TypedArray? = null
        try {
            a = context.obtainStyledAttributes(attrs, R.styleable.StickerView)
            showIcons = a.getBoolean(R.styleable.StickerView_showIcons, false)
            showBorder = a.getBoolean(R.styleable.StickerView_showBorder, false)
            bringToFrontCurrentSticker =
                a.getBoolean(R.styleable.StickerView_bringToFrontCurrentSticker, false)

            borderPaint1.apply {
                style = Paint.Style.STROKE
                strokeWidth = 6f
                pathEffect = DashPathEffect(floatArrayOf(15f, 20f), 0f)
                isAntiAlias = true
                color = Color.WHITE
            }

            lineUpPaint.apply {
                strokeWidth = 2f
                style = Paint.Style.STROKE
                color = Color.parseColor("#03A9F4")
            }

            borderPaint.apply {
                isAntiAlias = true
                color = a.getColor(
                    R.styleable.StickerView_borderColor,
                    //context.resources.getColor(R.color.colorBGCustomview)
                    ContextCompat.getColor(context, R.color.colorBGCustomview)
                )
                alpha = a.getInteger(R.styleable.StickerView_borderAlpha, 128)
            }
            marginSize = resources.getDimensionPixelSize(R.dimen._16sdp)
            configDefaultIcons()
        } finally {
            a?.recycle()
        }
    }

    // su dung get set cho property

    fun getStickers(): List<Sticker> = stickers

    var isDrawSticker: Boolean
        get() = drawSticker
        set(value) {
            drawSticker = value
        }

    var isShowIcons: Boolean
        get() = showIcons
        set(value) {
            showIcons = value
        }

    var isDrawLine: Boolean
        get() = drawLine
        set(value) {
            drawLine = value
        }

    var isShowBackground: Boolean
        get() = showBackground
        set(value) {
            showBackground = value
        }

    fun configDefaultIcons() {
        try {
            val deleteIcon = BitmapStickerIcon(
                ContextCompat.getDrawable(context, R.drawable.ic_close_1),
                BitmapStickerIcon.LEFT_TOP
            )
            deleteIcon.setIconEvent(DeleteIconEvent())
            val zoomIcon = BitmapStickerIcon(
                ContextCompat.getDrawable(context, R.drawable.ic_transform),
                BitmapStickerIcon.RIGHT_BOTOM
            )
            zoomIcon.setIconEvent(ZoomIconEvent())
            icons.clear()
            icons.add(zoomIcon)
            icons.add(deleteIcon)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "configDefaultIcons: " + e.message)
        }
    }


    fun moveToTop(sticker: Sticker) {
        originalLeft = getRectFBase(bitmapPoints).left
        originalTop = getRectFBase(bitmapPoints).top


        val rectF: RectF =
            if (handlingSticker is TextSticker && (handlingSticker as TextSticker).getCurve() != 0) {
                getRectFBase(bitmapPointCurve)
            } else {
                getRectFBase(bitmapPoints)
            }

        val stickerWidth = rectF.width()
        val leftOffset = (width - stickerWidth) / 2
        val targetX = leftOffset - rectF.left
        val targetY = height / 3f - rectF.top
        updateAnimation(sticker, rectF, targetX, targetY)
    }


    private fun updateAnimation(sticker: Sticker, rectF: RectF, target: Float, target1: Float) {
        val animator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)
        animator.addUpdateListener { animation ->
            var startX = 0f
            var startY = 0f
            val animatedFraction = animation.animatedFraction
            val newX = startX + animatedFraction * (target - startX)
            val newY = startY + animatedFraction * (target1 - startY)
            sticker.getMatrix().postTranslate(newX - startX, newY - startY)
            //ưa
            startX = newX
            startY = newY
            invalidate()
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
            }
        })

        animator.duration = 500
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
    fun senToLayer(oldPos: Int, newPos: Int) {
        if (stickers.size >= oldPos && stickers.size >= newPos) {
            val sticker = stickers[oldPos]
            stickers.removeAt(oldPos)
            stickers.add(newPos, sticker)
            invalidate()
        }
    }


    /**
     * overridelaiji method cua ViewGroup
     * View
     *   ↓
     * ViewGroup (chứa onLayout() và dispatchDraw())
     *   ↓
     * RelativeLayout
     *   ↓
     * StickerView
     */
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            stickerRect.apply {
                this.left = left.toFloat()
                this.top = top.toFloat()
                this.right = right.toFloat()
                this.bottom = bottom.toFloat()
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (drawSticker) {
            try {
                if (!showBackground) {
                    val backgroundPaint = Paint().apply {
                        //color = resources.getColor(R.color.color_0000000)
                        // getColor(id:Int) da bi deprecation nen thay the bang ContextCompat.getColor(context,id) :)))
                        color = ContextCompat.getColor(context, R.color.color_0000000)
                        style = Paint.Style.FILL
                    }
                    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
                }
                drawStickers(canvas)
            } catch (e: Throwable) {
                // Handle exception
            }
        }
    }

    fun drawStickers(canvas: Canvas) {
        var isCurve = false

        if (handlingSticker != null && !locked && (showBorder || showIcons)) {
            getStickerPoints(handlingSticker, bitmapPoints)
        }

        // Draw all stickers
        for (i in stickers.indices) {
            val sticker = stickers[i]
            sticker.draw(canvas)
        }

        if (handlingSticker != null && !locked && (showBorder || showIcons)) {
            if (handlingSticker is TextSticker && (handlingSticker as TextSticker).curve != 0) {
                isCurve = true
            }

            val arr = if (isCurve) {
                getStickerPoints2(handlingSticker, bitmapPointCurve)
                arrayOf(
                    bitmapPointCurve[0],
                    bitmapPointCurve[1],
                    bitmapPointCurve[2],
                    bitmapPointCurve[3],
                    bitmapPointCurve[4],
                    bitmapPointCurve[5],
                    bitmapPointCurve[6],
                    bitmapPointCurve[7]
                )
            } else {
                arrayOf(
                    bitmapPoints[0],
                    bitmapPoints[1],
                    bitmapPoints[2],
                    bitmapPoints[3],
                    bitmapPoints[4],
                    bitmapPoints[5],
                    bitmapPoints[6],
                    bitmapPoints[7]
                )
            }
            val x1 = arr[0]
            val y1 = arr[1]
            val x2 = arr[2]
            val y2 = arr[3]
            val x3 = arr[4]
            val y3 = arr[5]
            val x4 = arr[6]
            val y4 = arr[7]
            val rotation = calculateRotation(x4, y4, x3, y3)
            if (showBorder) {
                canvas.save()
                canvas.rotate(rotation, (x4 - x1) / 2 + x1, (y4 - y1) / 2 + y1)
                if (handlingSticker is TextSticker && (handlingSticker as TextSticker).curve != 0) {
                    rectCurve = getRectFBase(bitmapPointCurve)
                    drawLineText(canvas, rectCurve!!)
                } else {
                    drawLineText(canvas, getRectFBase(bitmapPoints))
                }
                canvas.restore()
            }
            if (showIcons) {
                for (i in icons.indices) {
                    val icon = icons[i]
                    val (iconX, iconY) = when (icon.position) {
                        BitmapStickerIcon.LEFT_TOP -> {
                            Pair(x1, y1)
                        }

                        BitmapStickerIcon.RIGHT_TOP -> {
                            Pair(x2, y2)
                        }

                        BitmapStickerIcon.LEFT_BOTTOM -> {
                            Pair(x3, y3)
                        }

                        BitmapStickerIcon.RIGHT_BOTOM -> {
                            Pair(x4, y4)
                        }

                        else -> {
                            Pair(0f, 0f)
                        }
                    }
                    configIconMatrix(icon, iconX, iconY, rotation)
                    icon.draw(canvas, borderPaint)
                }
            }
        }

        if (handlingSticker != null && !locked && allowShowLineHorizon && currentMode == ActionMode.DRAG && isShowLine) {
            canvas.drawLine(width / 2f, 0f, width / 2f, height.toFloat(), lineUpPaint)
        }
        if (handlingSticker != null && !locked && allowShowLineVertical && currentMode == ActionMode.DRAG && isShowLine) {
            canvas.drawLine(0f, height / 2f, width.toFloat(), height / 2f, lineUpPaint)
        }
    }

    private fun drawCircle(canvas: Canvas, rectF: RectF) {
        val paintCircle = Paint(Paint.ANTI_ALIAS_FLAG)
        paintCircle.apply {
            style = Paint.Style.FILL
            color = Color.WHITE
        }
        val radius = resources.getDimension(R.dimen._2sdp)
        canvas.apply {
            drawCircle((rectF.left + rectF.right) / 2, rectF.top, radius, paintCircle)
            drawCircle((rectF.left + rectF.right) / 2, rectF.bottom, radius, paintCircle)
            drawCircle(rectF.left, (rectF.top + rectF.bottom) / 2, radius, paintCircle)
            drawCircle(rectF.right, (rectF.top + rectF.bottom) / 2, radius, paintCircle)
        }
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

        stickerRect.apply {
            top = middlePointY - leftBound
            left = middlePointX - topBound
            right = middlePointX + topBound
            bottom = middlePointY + leftBound
        }
        return stickerRect
    }

    fun getStickerRect(): RectF {
        return stickerRect
    }

    fun drawLineText(canvas: Canvas, rect: RectF) {
        if (drawLine) {
            val paint = Paint().apply {
                style = Paint.Style.STROKE
                strokeWidth = 2f
                color = Color.WHITE
            }
            canvas.drawRect(rect, paint)
            drawCircle(canvas, rect)
        }
    }

    protected fun configIconMatrix(icon: BitmapStickerIcon, x: Float, y: Float, rotation: Float) {
        icon.apply {
            this.x = x
            this.y = y
            matrix.reset()
            matrix.postRotate(rotation, width / 2f, height / 2f)
            matrix.postTranslate(x - width / 2f, y - height / 2f)
        }
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
        val action = event.actionMasked
        if (!disableClick) {
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!onTouchDown(event)) {
                        return false
                    }
                }

                MotionEvent.ACTION_POINTER_DOWN -> {
                    oldDistance = calculateDistance(event)
                    oldRotation = calculateRotation(event)
                    midPoint = calculateMidPoint(event)

                    if (handlingSticker != null && isInStickerArea(
                            handlingSticker!!, event.getX(1), event.getY(1)
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
                    allowShowLineVertical = false
                    allowShowLineHorizon = false
                    updatePositionSticker()
                    onTouchUp(event)
                }

                MotionEvent.ACTION_POINTER_UP -> {
                    if (currentMode == ActionMode.ZOOM_WITH_TWO_FINGER && handlingSticker != null) {
                        onStickerOperationListener?.onStickerZoomFinished(handlingSticker!!)
                    }
                    currentMode = ActionMode.NONE
                }
            }
        }
        return true
    }

    /**
     * @param event MotionEvent received from [onTouchEvent]
     * @return true if has touch something
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
            currentIcon?.onActionDown(this, event)
        } else {
            handlingSticker = findHandlingSticker()
        }

        if (handlingSticker != null) {
            onStickerOperationListener?.onStickerTouchedDown(handlingSticker!!)
            downMatrix.set(handlingSticker!!.matrix)

            mappingPoint = floatArrayOf(handlingSticker!!.mappedBound.centerX())
            downMatrix.mapPoints(mappingPoint)

            if (bringToFrontCurrentSticker) {
                stickers.remove(handlingSticker)
                stickers.add(handlingSticker!!)
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
        return if (stickers.size > 0) {
            handlingSticker = stickers[stickers.size - 1]
            handlingSticker
        } else {
            null
        }
    }

    protected fun onTouchUp(event: MotionEvent) {
        isShowLine = false
        val currentTime = SystemClock.uptimeMillis()

        if (currentMode == ActionMode.ICON && currentIcon != null && handlingSticker != null) {
            currentIcon?.onActionUp(this, event)
        }

        if (currentMode == ActionMode.DRAG && abs(event.x - downX) < touchSlop && abs(event.y - downY) < touchSlop && handlingSticker != null) {

            currentMode = ActionMode.CLICK
            onStickerOperationListener?.onStickerClicked(handlingSticker!!)

            if (currentTime - lastClickTime < minClickDelayTime) {
                onStickerOperationListener?.onStickerDoubleTapped(handlingSticker!!)
            }
            invalidate()
        }

        if (currentMode == ActionMode.DRAG && handlingSticker != null) {
            onStickerOperationListener?.onStickerDragFinished(handlingSticker!!)
            invalidate()
        }

        currentMode = ActionMode.NONE
        lastClickTime = currentTime
    }


    protected fun handleCurrentMode(event: MotionEvent) {
        when (currentMode) {
            ActionMode.NONE, ActionMode.CLICK -> {
                // Do nothing
            }

            ActionMode.DRAG -> {
                handlingSticker?.let {
                    moveMatrix.set(downMatrix)
                    moveMatrix.postTranslate(event.x - downX, event.y - downY)
                    handlingSticker.setMatrix(moveMatrix)
                    if (constrained) {
                        constrainSticker(handlingSticker)
                    }
                    calculateRangerLockSticker(handlingSticker)
                }
            }

            ActionMode.ZOOM_WITH_TWO_FINGER -> {
                handlingSticker?.let {
                    var newDistance = calculateDistance(event)
                    val newRotation = calculateRotation(event)

                    if (newDistance <= width / 15f) {
                        newDistance = width / 15f
                        if (oldDistance <= width / 15f) {
                            oldDistance = width / 15f
                        }
                    }

                    moveMatrix.set(downMatrix)
                    moveMatrix.postScale(
                        newDistance / oldDistance, newDistance / oldDistance, midPoint.x, midPoint.y
                    )
                    moveMatrix.postRotate(newRotation - oldRotation, midPoint.x, midPoint.y)
                    handlingSticker.setMatrix(moveMatrix)
                }
            }

            ActionMode.ICON -> {
                if (handlingSticker != null && currentIcon != null) {
                    currentIcon?.onActionMove(this, event)
                }
            }
        }
    }

    fun zoomAndRotateCurrentSticker(event: MotionEvent) {
        zoomAndRotateSticker(handlingSticker, event)
    }

    fun zoomAndRotateSticker(sticker: Sticker?, event: MotionEvent) {
        sticker?.let {
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
                newDistance / oldDistance, newDistance / oldDistance, midPoint.x, midPoint.y
            )
            moveMatrix.postRotate(newRotation - oldRotation, midPoint.x, midPoint.y)
            handlingSticker.setMatrix(moveMatrix)
        }
    }

    fun setTextSizeUp(sticker: Sticker) {
        sticker.getMatrix().postScale(1f, 1 / 05f, x, y)
    }

    fun setTextSizeDown(sticker: Sticker?) {
        sticker?.getMatrix()?.postScale(1f, 100f / 105f, x, y)
    }

    // NKP : Sample for rotate sticker
    fun rotateSticker(sticker: Sticker?, rotate: Float, scale: Float, point: PointF) {
        if (sticker != null && sticker.rotate) {
            val matrix: Matrix = sticker.getMatrix()
            matrix.reset()
            matrix.postScale(scale, scale, sticker.getCenterPoint().x, sticker.getCenterPoint().y)
            matrix.postRotate(rotate, sticker.getCenterPoint().x, sticker.getCenterPoint().y)
            matrix.postTranslate(
                point.x - sticker.getWidth() / 2f, point.y - sticker.getHeight() / 2f
            )
            handlingSticker.setMatrix(matrix)
            invalidate()
        }
    }

    protected fun constrainSticker(sticker: Sticker) {
        var moveX = 0f
        var moveY = 0f
        val width: Int = getWidth()
        val height: Int = getHeight()
        if (sticker is TextSticker && (sticker as TextSticker).getCurve() != 0) {
            sticker.getMappedCenterPointCurve(
                currentCenterPoint, point, tmp, (sticker as TextSticker).getTextRectCurve()
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
            val x = icon.x - downX
            val y = icon.y - downY
            val distancePow2 = x * x + y * y

            val additionalRadius = resources.getDimension(R.dimen._12sdp)
            val touchRadius = icon.iconRadius + additionalRadius

            if (distancePow2 <= touchRadius * touchRadius) {
                return icon
            }
        }
        return null
    }

    /**
     * find the touched Sticker
     **/
    protected fun findHandlingSticker(): Sticker? {
        for (index in stickers.indices) {

            if (isInStickerArea(stickers[index], downX, downY)) {
                oldSticker = currentSticker
                currentSticker = index
                return stickers[index]
            }
        }
        return null
    }

    protected fun isInStickerArea(sticker: Sticker, downX: Float, downY: Float): Boolean {
        tmp[0] = downX
        tmp[1] = downY
        return sticker.contains(tmp)
    }

    protected fun calculateMidPoint(event: MotionEvent? = null): PointF {
        if (event == null || event.pointerCount < 2) {
            midPoint.set(0f, 0f)
            return midPoint
        } else {
            val x = (event.getX(0) + event.getX(1)) / 2
            val y = (event.getY(0) + event.getY(1)) / 2
            midPoint.set(x, y)
            return midPoint
        }
    }

    protected fun calculateMidPoint(): PointF {
        return if (handlingSticker == null) {
            midPoint.set(0f, 0f)
            midPoint
        } else {
            if (handlingSticker is TextSticker && (handlingSticker as TextSticker).curve != 0) {
                handlingSticker!!.getMappedCenterPointCurve(
                    midPoint, point, tmp, (handlingSticker as TextSticker).textRectCurve
                )
            } else {
                handlingSticker!!.getMappedCenterPoint(midPoint, point, tmp)
            }
            midPoint
        }
    }

    /**
     * calculate rotation in line with two fingers and x-axis
     **/

    protected fun calculateRotation(event: MotionEvent?): Float {
        return if (event == null || event.pointerCount < 2) {
            0f
        } else {
            calculateRotation(event.getX(0), event.getY(0), event.getX(1), event.getY(1))
        }
    }

    protected fun calculateRotation(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val x = (x1 - x2).toDouble()
        val y = (y1 - y2).toDouble()
        val radians = atan2(y, x)
        return Math.toDegrees(radians).toFloat()
    }

    /**
     * calculate Distance in two fingers
     **/

    protected fun calculateDistance(event: MotionEvent?): Float {
        return if (event == null || event.pointerCount < 2) {
            0f
        } else {
            calculateDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1))
        }
    }

    protected fun calculateDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val x = (x1 - x2).toDouble()
        val y = (y1 - y2).toDouble()
        return sqrt(x * x + y * y).toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        onStickerOperationListener?.onSizeChange(w, h, oldW, oldH)
    }

    fun setSize(w: Int, h: Int) {
        onSizeChanged(w, h, w, h)
    }

    fun gava(sticker: Sticker?) {
        sticker?.let {
            val width: Float = width.toFloat()
            val height: Float = height.toFloat()
            val stickerWidth: Float = it.getWith()
            val stickerHeight: Float = it.getHeight()

            val offsetX = (width.minus(stickerWidth)) / 2
            val offsetY = (height - stickerHeight) / 2

            sizeMatrix.postTranslate(offsetX, offsetY)
            invalidate()
        }
    }

    /**
     * Sticker's drawable will be too bigger or smaller
     * This method is to transform it to fit
     * step 1：let the center of the sticker image is coincident with the center of the View.
     * step 2：Calculate the zoom and zoom
     **/
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
            if ((direction and StickerViewMigrateKotlin.FLIP_HORIZONTALLY) > 0) {
                sticker.getMatrix().preScale(-1, 1, midPoint.x, midPoint.y)
                sticker.setFlippedHorizontally(!sticker.isFlippedHorizontally())
            }
            if ((direction and StickerViewMigrateKotlin.FLIP_VERTICALLY) > 0) {
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
                handlingSticker?.let {
                    sticker.apply {
                        setMatrix(handlingSticker.getMatrix())
                        setFlippedVertically(handlingSticker.isFlippedVertically())
                        setFlippedHorizontally(handlingSticker.isFlippedHorizontally())
                    }
                }
            } else {

                handlingSticker?.let {
                    handlingSticker!!.matrix.reset()
                    // Reset scale, angle, and put it in center
                    val offsetX = (width - handlingSticker!!.width) / 2f
                    val offsetY = (height - handlingSticker!!.height) / 2f
                    sticker.matrix.postTranslate(offsetX, offsetY)

                    val scaleFactor = if (width < height) {
                        width / handlingSticker!!.drawable.intrinsicWidth
                    } else {
                        height / handlingSticker!!.drawable.intrinsicHeight
                    }
                    sticker.matrix.postScale(
                        scaleFactor / 2f, scaleFactor / 2f, width / 2f, height / 2f
                    )
                }
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
            sticker?.let { onStickerOperationListener?.onStickerDeleted(it) }
            if (handlingSticker == sticker) {
                handlingSticker = null
            }
            invalidate()
            return true
        } else {
            return false
        }
    }

    fun removeCurrentSticker(): Boolean = remove(handlingSticker)
    fun removeAllStickers() {
        stickers.clear()
        handlingSticker?.let {
            it.release()
            handlingSticker = null
        }
        invalidate()
    }

    fun addSticker(sticker: Sticker): StickerView = addSticker(sticker, Sticker.Position.CENTER)

    fun addSticker(sticker: Sticker, @Sticker.Position position: Int): StickerView {
        Log.d("LAM", "addSticker: ")
        post { addStickerImmediately(sticker, position) }
        return this
    }


    protected fun addStickerImmediately(sticker: Sticker, @Sticker.Position position: Int) {
        setStickerPosition(sticker, position)

        val widthScaleFactor = width.toFloat() / sticker.drawable.intrinsicWidth
        val heightScaleFactor = height.toFloat() / sticker.drawable.intrinsicHeight
        val scaleFactor = minOf(widthScaleFactor, heightScaleFactor)

        when (sticker) {
            DrawableSticker -> {
                sticker.matrix.postScale(
                    scaleFactor / 4, scaleFactor / 4, width / 2f, height / 2f
                )
            }

            BitmapSticker -> {
                val displayMetrics = DisplayMetrics()
                (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
                val screenWidth = displayMetrics.widthPixels
                val targetWidth = screenWidth / 3f
                val originalWidth = sticker.width
                val scale = targetWidth / originalWidth
                sticker.matrix.postScale(scale, scale, width / 2f, height / 2f)
            }

            else -> {
                sticker.matrix.postScale(
                    scaleFactor / 2, scaleFactor / 2, width / 2f, height / 2f
                )
            }
        }

        handlingSticker = sticker
        stickers.add(sticker)

        if (currentSticker < stickers.size) {
            stickers[currentSticker].isSelected = false
        } else {
            stickers[stickers.size - 1].isSelected = false
        }

        onStickerOperationListener?.onStickerAdded(sticker)
        updatePositionSticker()
        invalidate()
    }

    private fun updatePositionSticker() {
        try {
            if (handlingSticker is TextSticker && (handlingSticker as TextSticker).getCurve() != 0) {
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

    fun setLocked(locked: Boolean): StickerView {
        this.isLocked = locked
        invalidate()
        return this
    }

    fun setMinClickDelayTime(minClickDelayTime: Int): StickerView {
        this.minClickDelayTime = minClickDelayTime
        return this
    }

    fun setConstrained(constrained: Boolean): StickerView {
        this.isConstrained = constrained
        postInvalidate()
        return this
    }

    fun setOnStickerOperationListener(onStickerOperationListener: OnStickerOperationListener?): StickerView {
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

    fun calculateRangerLockSticker(sticker: Sticker) {
        val dx: Float
        if (sticker is TextSticker && (sticker as TextSticker).getCurve() != 0) {
            sticker.getMappedCenterPointCurve(
                currentCenterPoint, point, tmp, (sticker as TextSticker).getTextRectCurve()
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
}