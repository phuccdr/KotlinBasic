package com.wd.kotlin_basic.migrateToKotlin;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.MotionEventCompat;

//import com.eco.textonphoto.analystic.AnalyticsManager;
//import com.eco.textonphoto.analystic.Event;
//import com.eco.textonphoto.R;
//import com.eco.textonphoto.model.StickerEvent;
//import com.eco.textonphoto.util.HawkHelper;
//
//import org.greenrobot.eventbus.EventBus;

import com.wd.kotlin_basic.R;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StickerView extends RelativeLayout {

    private final boolean showBorder;
    private final boolean bringToFrontCurrentSticker;
    private boolean drawSticker = true;
    private boolean drawLine = true;
    private boolean showBackground = true;
    private boolean showIcons = true;
    private float[] mappingPoint = {0};
    private boolean allowShowLineHorizon, allowShowLineVertical;

    @IntDef({ActionMode.NONE, ActionMode.DRAG, ActionMode.ZOOM_WITH_TWO_FINGER, ActionMode.ICON, ActionMode.CLICK})
    @Retention(RetentionPolicy.SOURCE)
    protected @interface ActionMode {
        int NONE = 0;
        int DRAG = 1;
        int ZOOM_WITH_TWO_FINGER = 2;
        int ICON = 3;
        int CLICK = 4;
    }

    @IntDef(flag = true, value = {FLIP_HORIZONTALLY, FLIP_VERTICALLY})
    @Retention(RetentionPolicy.SOURCE)
    protected @interface Flip {
    }
    private static final int DEFAULT_MIN_CLICK_DELAY_TIME = 250;

    public static final int FLIP_HORIZONTALLY = 1;
    public static final int FLIP_VERTICALLY = 1 << 1;

    private final List<Sticker> stickers = new ArrayList<>();
    private final List<BitmapStickerIcon> icons = new ArrayList<>(4);

    private final Paint borderPaint = new Paint();
    private final Paint borderPaint1 = new Paint();
    private final Paint lineUpPaint = new Paint();
    private final RectF stickerRect = new RectF();

    private final Matrix sizeMatrix = new Matrix();
    private final Matrix downMatrix = new Matrix();
    private final Matrix moveMatrix = new Matrix();

    // region storing variables
    private final float[] bitmapPoints = new float[8];
    private final float[] bitmapPointCurve = new float[8];
    private final float[] bounds = new float[8];
    private final float[] point = new float[2];
    private final PointF currentCenterPoint = new PointF();
    private final float[] tmp = new float[2];
    private PointF midPoint = new PointF();
    // endregion
    private final int touchSlop;
    private float x, y;
    private BitmapStickerIcon currentIcon;
    //the first point down position
    private float downX;
    private float downY;

    private float oldDistance = 0f;
    private float oldRotation = 0f;
    private int currentSticker = 0;
    private int oldSticker = 0;
    RectF rectCurve;
    private int marginSize = 40;

    @ActionMode
    private int currentMode = ActionMode.NONE;

    private Sticker handlingSticker;

    private boolean locked;
    private boolean disableClick = false;

    private boolean constrained;

    private OnStickerOperationListener onStickerOperationListener;

    private long lastClickTime = 0;
    private int minClickDelayTime = DEFAULT_MIN_CLICK_DELAY_TIME;
    private boolean isShowLine;

    private float originalLeft;
    private float originalTop;

    public int getCurrentMode() {
        return currentMode;
    }

    public StickerView(Context context) {
        this(context, null);
    }

    public StickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        TypedArray a = null;
        try {
            a = context.obtainStyledAttributes(attrs, R.styleable.StickerView);
            showIcons = a.getBoolean(R.styleable.StickerView_showIcons, false);
            showBorder = a.getBoolean(R.styleable.StickerView_showBorder, false);
            bringToFrontCurrentSticker = a.getBoolean(R.styleable.StickerView_bringToFrontCurrentSticker, false);
            borderPaint1.setStyle(Paint.Style.STROKE);
            borderPaint1.setStrokeWidth(6);
            borderPaint1.setPathEffect(new DashPathEffect(new float[]{15f, 20f}, 0f));
            borderPaint1.setAntiAlias(true);
            borderPaint1.setColor(Color.WHITE);

            lineUpPaint.setStrokeWidth(2);
            lineUpPaint.setStyle(Paint.Style.STROKE);
            lineUpPaint.setColor(Color.parseColor("#03A9F4"));

            borderPaint.setAntiAlias(true);
            borderPaint.setColor(a.getColor(R.styleable.StickerView_borderColor,
                    context.getResources().getColor(R.color.colorBGCustomview)));
            borderPaint.setAlpha(a.getInteger(R.styleable.StickerView_borderAlpha, 128));
            marginSize = getResources().getDimensionPixelSize(R.dimen._16sdp);
            configDefaultIcons();
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
    }

    public List<Sticker> getStickers() {
        return stickers;
    }

    public void setDrawSticker(boolean drawSticker) {
        this.drawSticker = drawSticker;
    }

    public boolean isDrawSticker() {
        return drawSticker;
    }

    public void setShowIcons(boolean showIcons) {
        this.showIcons = showIcons;
    }

    public boolean isShowIcons() {
        return showIcons;
    }

    public boolean isDrawLine() {
        return drawLine;
    }

    public void setDrawLine(boolean drawLine) {
        this.drawLine = drawLine;
    }

    public boolean isShowBackground() {
        return showBackground;
    }

    public void setShowBackground(boolean showBackground) {
        this.showBackground = showBackground;
    }

    public void configDefaultIcons() {
        try {
            BitmapStickerIcon deleteIcon = new BitmapStickerIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_close_1),
                    BitmapStickerIcon.LEFT_TOP);
            deleteIcon.setIconEvent(new DeleteIconEvent());
            BitmapStickerIcon zoomIcon = new BitmapStickerIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_transform),
                    BitmapStickerIcon.RIGHT_BOTOM);
            zoomIcon.setIconEvent(new ZoomIconEvent());
            icons.clear();
            icons.add(zoomIcon);
            icons.add(deleteIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void moveToTop(Sticker sticker) {
        if(sticker instanceof TextSticker) {
            originalLeft = getRectFBase(bitmapPoints).left;
            originalTop = getRectFBase(bitmapPoints).top;
            if (handlingSticker instanceof TextSticker && ((TextSticker) handlingSticker).getCurve() != 0) {
                RectF rectF = getRectFBase(bitmapPointCurve);
                float stickerWidth = rectF.width();
                float leftOffset = (getWidth() - stickerWidth) / 2;
                float targetX = leftOffset - rectF.left;
                float targetY = getHeight() / 3f - rectF.top;
                updateAnimation(sticker, rectF, targetX, targetY);

            } else {
                RectF rectF = getRectFBase(bitmapPoints);
                float stickerWidth = rectF.width();
                float leftOffset = (getWidth() - stickerWidth) / 2;
                float targetX = leftOffset - rectF.left;
                float targetY = getHeight() / 3f - rectF.top;
                updateAnimation(sticker, rectF, targetX, targetY);

            }
        }
    }

    private void updateAnimation(Sticker sticker, RectF rectF, float targetX, float targetY) {

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1); // Tạo một ValueAnimator từ 0 đến 1
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private float startX = 0;
            private float startY = 0;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedFraction = animation.getAnimatedFraction();
                float newX = startX + animatedFraction * (targetX - startX);
                float newY = startY + animatedFraction * (targetY - startY);
                sticker.getMatrix().postTranslate(newX - startX, newY - startY);
                startX = newX;
                startY = newY;
                invalidate();
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        animator.setDuration(500);
        animator.start();
    }


    public void moveToOriginalPosition(Sticker sticker) {
        updateAnimation(sticker, getRectFBase(bitmapPoints), originalLeft - getRectFBase(bitmapPoints).left, originalTop - getRectFBase(bitmapPoints).top);
    }

    /**
     * Swaps sticker at layer [[oldPos]] with the one at layer [[newPos]].
     * Does nothing if either of the specified layers doesn't exist.
     */
    public void swapLayers(int oldPos, int newPos) {
        if (stickers.size() >= oldPos && stickers.size() >= newPos) {
            Collections.swap(stickers, oldPos, newPos);
            invalidate();
        }
    }

    /**
     * Sends sticker from layer [[oldPos]] to layer [[newPos]].
     * Does nothing if either of the specified layers doesn't exist.
     */
    public void sendToLayer(int oldPos, int newPos) {
        if (stickers.size() >= oldPos && stickers.size() >= newPos) {
            Sticker s = stickers.get(oldPos);
            stickers.remove(oldPos);
            stickers.add(newPos, s);
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            stickerRect.left = left;
            stickerRect.top = top;
            stickerRect.right = right;
            stickerRect.bottom = bottom;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (drawSticker) {
            try {
                if (!showBackground) {
                    Paint backgroundPaint = new Paint();
                    backgroundPaint.setColor(getResources().getColor(R.color.color_0000000)); // Màu nền đen với độ trong suốt
                    backgroundPaint.setStyle(Paint.Style.FILL);
                    canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);
                }
                drawStickers(canvas);
            } catch (Throwable e) {

            }
        }
    }

    public void drawStickers(Canvas canvas) {
        boolean isCurve = false;

        if (handlingSticker != null && !locked && (showBorder || showIcons)) {
            getStickerPoints(handlingSticker, bitmapPoints);
        }


        for (int i = 0; i < stickers.size(); i++) {
            Sticker sticker = stickers.get(i);
            if (sticker != null) {
                sticker.draw(canvas);
            }
        }

        if (handlingSticker != null && !locked && (showBorder || showIcons)) {
            if (handlingSticker instanceof TextSticker && ((TextSticker) handlingSticker).getCurve() != 0) {
                isCurve = true;
            }
            float x1, y1, x2, y2, x3, y3, x4, y4;
            if (isCurve) {
                getStickerPoints2(handlingSticker, bitmapPointCurve);
                x1 = bitmapPointCurve[0];
                y1 = bitmapPointCurve[1];
                x2 = bitmapPointCurve[2];
                y2 = bitmapPointCurve[3];
                x3 = bitmapPointCurve[4];
                y3 = bitmapPointCurve[5];
                x4 = bitmapPointCurve[6];
                y4 = bitmapPointCurve[7];
            } else {
                x1 = bitmapPoints[0];
                y1 = bitmapPoints[1];
                x2 = bitmapPoints[2];
                y2 = bitmapPoints[3];
                x3 = bitmapPoints[4];
                y3 = bitmapPoints[5];
                x4 = bitmapPoints[6];
                y4 = bitmapPoints[7];
            }
            float rotation = calculateRotation(x4, y4, x3, y3);

            if (showBorder) {
                canvas.save();
                canvas.rotate(rotation, (x4 - x1) / 2 + x1, (y4 - y1) / 2 + y1);
                if (handlingSticker instanceof TextSticker && ((TextSticker) handlingSticker).getCurve() != 0) {
                    rectCurve = getRectFBase(bitmapPointCurve);
                    drawLineText(canvas, rectCurve);
                } else {
                    drawLineText(canvas, getRectFBase(bitmapPoints));
                    if(handlingSticker instanceof TextSticker) {

                    }
                }
                canvas.restore();
            }



            //draw icon
            if (showIcons) {
                for (int i = 0; i < icons.size(); i++) {
                    BitmapStickerIcon icon = icons.get(i);
                    float x = 0, y = 0;
                    switch (icon.getPosition()) {
                        case BitmapStickerIcon.LEFT_TOP:
                            x = x1;
                            y = y1;
                            break;

                        case BitmapStickerIcon.RIGHT_TOP:
                            x = x2;
                            y = y2 ;
                            break;

                        case BitmapStickerIcon.LEFT_BOTTOM:
                            x = x3;
                            y = y3;
                            break;

                        case BitmapStickerIcon.RIGHT_BOTOM:
                            x = x4;
                            y = y4;
                            break;
                    }
                    configIconMatrix(icon, x, y, rotation);
                    icon.draw(canvas, borderPaint);
                }
            }
        }


        if (this.handlingSticker != null && !locked && this.allowShowLineHorizon && (currentMode == ActionMode.DRAG) && isShowLine) {
            canvas.drawLine(((float) getWidth()) / 2.0f, 0, ((float) getWidth()) / 2.0f, (float) getHeight(), lineUpPaint);
        }
        if (this.handlingSticker != null && !locked && this.allowShowLineVertical && (currentMode == ActionMode.DRAG) && isShowLine) {
            canvas.drawLine(0, ((float) getHeight()) / 2.0f, (float) getWidth(), ((float) getHeight()) / 2.0f, lineUpPaint);
        }

    }

    private void drawCircle(Canvas canvas, RectF rectF) {
        Paint painCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        painCircle.setStyle(Paint.Style.FILL);
        painCircle.setColor(Color.WHITE);
        canvas.drawCircle((rectF.left + rectF.right) / 2, rectF.top, getResources().getDimension(R.dimen._2sdp), painCircle);
        canvas.drawCircle((rectF.left + rectF.right) / 2, rectF.bottom, getResources().getDimension(R.dimen._2sdp), painCircle);
        canvas.drawCircle(rectF.left,  (rectF.top + rectF.bottom) / 2, getResources().getDimension(R.dimen._2sdp), painCircle);
        canvas.drawCircle(rectF.right,  (rectF.top + rectF.bottom) / 2, getResources().getDimension(R.dimen._2sdp), painCircle);
    }

    // This method using to draw a tool rect (gray rect when add new sticker)
    private RectF getRectFBase(float[] coordinate) {
        float x1 = coordinate[0];
        float y1 = coordinate[1];
        float x2 = coordinate[2];
        float y2 = coordinate[3];
        float x3 = coordinate[4];
        float y3 = coordinate[5];
        float x4 = coordinate[6];
        float y4 = coordinate[7];

        float topBound = calculateDistance(x1, y1, x2, y2) / 2;
        float leftBound = calculateDistance(x1, y1, x3, y3) / 2;
        float middlePointX = (x4 - x1) / 2 + x1;
        float middlePointY = (y4 - y1) / 2 + y1;

        stickerRect.top = middlePointY - leftBound;
        stickerRect.left = middlePointX - topBound;
        stickerRect.right = middlePointX + topBound;
        stickerRect.bottom = middlePointY + leftBound;

        //if (leftBound <= 30) {
        // stickerRect.top -= 15;
        // stickerRect.bottom += 15;
        // }
        return stickerRect;
    }

    public RectF getStickerRect() {
        return stickerRect;
    }

    public void drawLineText(Canvas canvas, RectF rect) {
        if(drawLine) {
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2f);
            paint.setColor(Color.WHITE);
            canvas.drawRect(rect, paint);
            drawCircle(canvas, rect);
        }
    }

    protected void configIconMatrix(@NonNull BitmapStickerIcon icon, float x, float y, float rotation) {
        icon.setX(x);
        icon.setY(y);
        icon.getMatrix().reset();
        icon.getMatrix().postRotate(rotation, icon.getWidth() / 2, icon.getHeight() / 2);
        icon.getMatrix().postTranslate(x - icon.getWidth() / 2, y - icon.getHeight() / 2);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
        }

        return super.onInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        if(!disableClick) {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if (!onTouchDown(event)) {
                        return false;
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDistance = calculateDistance(event);
                    oldRotation = calculateRotation(event);
                    midPoint = calculateMidPoint(event);

                    if (handlingSticker != null && isInStickerArea(handlingSticker,
                            event.getX(1),
                            event.getY(1)) && findCurrentIconTouched() == null) {
                        currentMode = ActionMode.ZOOM_WITH_TWO_FINGER;
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    handleCurrentMode(event);
                    invalidate();
                    break;

                case MotionEvent.ACTION_UP:
                    this.allowShowLineVertical = false;
                    this.allowShowLineHorizon = false;
                    updatePositionSticker();
                    onTouchUp(event);
//                onStickerOperationListener.onStickerTouchUp(handlingSticker);
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    if (currentMode == ActionMode.ZOOM_WITH_TWO_FINGER && handlingSticker != null) {
                        if (onStickerOperationListener != null) {
                            onStickerOperationListener.onStickerZoomFinished(handlingSticker);
                        }
                    }
                    currentMode = ActionMode.NONE;
                    break;
            }
        }
        return true;
    }

    /**
     * @param event MotionEvent received from {@link #onTouchEvent)
     * @return true if has touch something
     */

    protected boolean onTouchDown(@NonNull MotionEvent event) {
        currentMode = ActionMode.DRAG;
        isShowLine = true;
        downX = event.getX();
        downY = event.getY();
        midPoint = calculateMidPoint();

        oldDistance = calculateDistance(midPoint.x, midPoint.y, downX, downY);
        oldRotation = calculateRotation(midPoint.x, midPoint.y, downX, downY);

        currentIcon = findCurrentIconTouched();
        if (currentIcon != null) {
            currentMode = ActionMode.ICON;
            currentIcon.onActionDown(this, event);
        } else {
            handlingSticker = findHandlingSticker();
        }
        if (handlingSticker != null) {
            onStickerOperationListener.onStickerTouchedDown(handlingSticker);
            downMatrix.set(handlingSticker.getMatrix());

            mappingPoint = new float[]{handlingSticker.getMappedBound().centerX()};
            // Ánh xạ tọa độ tâm x của sticker qua ma trận hiện tại. Ở đây là ma trận đã lưu trước đó
            downMatrix.mapPoints(mappingPoint);
            if (bringToFrontCurrentSticker) {
                stickers.remove(handlingSticker);
                stickers.add(handlingSticker);
            }

        } else {
            EventBus.getDefault().postSticky(new StickerEvent(false));
        }

        stickerCheck();

        if (currentIcon == null && handlingSticker == null) {
            invalidate();
            return false;
        }
        invalidate();
        return true;
    }

    public void clearSelectedSticker() {
        Log.d("LAM", "clearSelectedSticker: ");
        handlingSticker = null;
    }

    public Sticker findLastSticker() {
        if(getStickers().size() > 0) {
            handlingSticker = getStickers().get(getStickers().size() - 1);
            return handlingSticker;
        }
        return null;
    }

    protected void onTouchUp(@NonNull MotionEvent event) {
        isShowLine = false;
        long currentTime = SystemClock.uptimeMillis();

        if (currentMode == ActionMode.ICON && currentIcon != null && handlingSticker != null) {
            currentIcon.onActionUp(this, event);
        }

        if (currentMode == ActionMode.DRAG && Math.abs(event.getX() - downX) < touchSlop && Math.abs(event.getY() - downY) < touchSlop && handlingSticker != null) {
            currentMode = ActionMode.CLICK;
            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerClicked(handlingSticker);
            }
            if (currentTime - lastClickTime < minClickDelayTime) {
                if (onStickerOperationListener != null) {
                    onStickerOperationListener.onStickerDoubleTapped(handlingSticker);
                }
            }
            invalidate();
        }


        if (currentMode == ActionMode.DRAG && handlingSticker != null) {
            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerDragFinished(handlingSticker);
                invalidate();
            }
        }

        currentMode = ActionMode.NONE;
        lastClickTime = currentTime;

        // Đặt lại tọa độ x và khoảng cách đã di chuyển được của ngón tay
    }

    protected void handleCurrentMode(@NonNull MotionEvent event) {
        switch (currentMode) {
            case ActionMode.NONE:
            case ActionMode.CLICK:
                break;
            case ActionMode.DRAG:
                if (handlingSticker != null) {
                    moveMatrix.set(downMatrix);
                    moveMatrix.postTranslate(event.getX() - downX, event.getY() - downY);
                    handlingSticker.setMatrix(moveMatrix);
                    if (constrained) {
                        constrainSticker(handlingSticker);
                    }
                    caculateRangerLockSticker(handlingSticker);
                }
                break;
            case ActionMode.ZOOM_WITH_TWO_FINGER:
                if (handlingSticker != null) {
                    float newDistance = calculateDistance(event);
                    float newRotation = calculateRotation(event);
                    if (newDistance <= (float) getWidth() / 15f) {
                        newDistance = (float) getWidth() / 15f;
                        if (oldDistance <= getWidth() / 15f) {
                            oldDistance = getWidth() / 15f;
                        }
                    }
                    moveMatrix.set(downMatrix);
                    moveMatrix.postScale(newDistance / oldDistance, newDistance / oldDistance, midPoint.x, midPoint.y);
                    moveMatrix.postRotate(newRotation - oldRotation, midPoint.x, midPoint.y);
                    handlingSticker.setMatrix(moveMatrix);
                }
                break;

            case ActionMode.ICON:
                if (handlingSticker != null && currentIcon != null) {
                    currentIcon.onActionMove(this, event);
                }
                break;
        }
    }

    public void zoomAndRotateCurrentSticker(@NonNull MotionEvent event) {
        zoomAndRotateSticker(handlingSticker, event);
    }

    public void zoomAndRotateSticker(@Nullable Sticker sticker, @NonNull MotionEvent event) {
        if (sticker != null) {
            float newDistance = calculateDistance(midPoint.x, midPoint.y, event.getX(), event.getY());
            float newRotation = calculateRotation(midPoint.x, midPoint.y, event.getX(), event.getY());
            moveMatrix.set(downMatrix);

            if (newDistance <= getWidth() / 15f) {
                newDistance = getWidth() / 15f;
                if (oldDistance < getWidth() / 15f) {
                    oldDistance = getWidth() / 15f;
                }
            }
            moveMatrix.postScale(newDistance / oldDistance, newDistance / oldDistance, midPoint.x, midPoint.y);

            // caculateRangerCornerSticker(handlingSticker,newRotation);
            moveMatrix.postRotate(newRotation - oldRotation, midPoint.x, midPoint.y);
            handlingSticker.setMatrix(moveMatrix);
        }
    }

    public void setTextSizeUp(Sticker sticker) {
        if (sticker != null) {
            sticker.getMatrix().postScale(1f, 1.05f, x, y);
        }

    }

    public void setTextSizeDown(Sticker sticker) {
        if (sticker != null) {
            sticker.getMatrix().postScale(1f, (100f / 105f), x, y);
        }
    }


    // NKP : Sample for rotate sticker
    public void rotateSticker(@Nullable Sticker sticker, float rotate, float scale, PointF point) {
        if (sticker != null && sticker.rotate) {
            Matrix matrix = sticker.getMatrix();
            matrix.reset();
            matrix.postScale(scale, scale, sticker.getCenterPoint().x, sticker.getCenterPoint().y);
            matrix.postRotate(rotate, sticker.getCenterPoint().x, sticker.getCenterPoint().y);
            matrix.postTranslate(point.x - sticker.getWidth() / 2f, point.y - sticker.getHeight() / 2f);
            handlingSticker.setMatrix(matrix);
            invalidate();
        }
    }

    protected void constrainSticker(@NonNull Sticker sticker) {
        float moveX = 0;
        float moveY = 0;
        int width = getWidth();
        int height = getHeight();

        if (sticker instanceof TextSticker && ((TextSticker) sticker).getCurve() != 0) {
            sticker.getMappedCenterPointCurve(currentCenterPoint, point, tmp, ((TextSticker) sticker).getTextRectCurve());
        } else {
            sticker.getMappedCenterPoint(currentCenterPoint, point, tmp);
        }
        if (currentCenterPoint.x < 0) {
            moveX = -currentCenterPoint.x;
        }

        if (currentCenterPoint.x > width) {
            moveX = width - currentCenterPoint.x;
        }

        if (currentCenterPoint.y < 0) {
            moveY = -currentCenterPoint.y;
        }

        if (currentCenterPoint.y > height) {
            moveY = height - currentCenterPoint.y;
        }
        sticker.getMatrix().postTranslate(moveX, moveY);
    }

    @Nullable
    protected BitmapStickerIcon findCurrentIconTouched() {
        for (BitmapStickerIcon icon : icons) {
            float x = icon.getX() - downX;
            float y = icon.getY() - downY;
            float distance_pow_2 = x * x + y * y;

            // Thêm bán kính bổ sung để tăng vùng chọn
            float additionalRadius = getResources().getDimension(R.dimen._12sdp); // Giá trị có thể điều chỉnh
            float touchRadius = icon.getIconRadius() + additionalRadius;

            if (distance_pow_2 <= touchRadius * touchRadius) {
                return icon;
            }
        }
        return null;
    }

    /**
     * find the touched Sticker
     **/
    @Nullable
    protected Sticker findHandlingSticker() {
        for (int i = stickers.size() - 1; i >= 0; i--) {
            if (isInStickerArea(stickers.get(i), downX, downY)) {
                oldSticker = currentSticker;
                currentSticker = i;
                return stickers.get(i);
            }
        }
        return null;
    }

    protected boolean isInStickerArea(@NonNull Sticker sticker, float downX, float downY) {
        tmp[0] = downX;
        tmp[1] = downY;
        return sticker.contains(tmp);
    }

    @NonNull
    protected PointF calculateMidPoint(@Nullable MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) {
            midPoint.set(0, 0);
            return midPoint;
        }
        float x = (event.getX(0) + event.getX(1)) / 2;
        float y = (event.getY(0) + event.getY(1)) / 2;
        midPoint.set(x, y);
        return midPoint;
    }

    @NonNull
    protected PointF calculateMidPoint() {
        if (handlingSticker == null) {
            midPoint.set(0, 0);
            return midPoint;
        }
        if (handlingSticker instanceof TextSticker && ((TextSticker) handlingSticker).getCurve() != 0) {
            handlingSticker.getMappedCenterPointCurve(midPoint, point, tmp, ((TextSticker) handlingSticker).getTextRectCurve());
        } else {
            handlingSticker.getMappedCenterPoint(midPoint, point, tmp);
        }
        return midPoint;
    }

    /**
     * calculate rotation in line with two fingers and x-axis
     **/
    protected float calculateRotation(@Nullable MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) {
            return 0f;
        }
        return calculateRotation(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
    }

    protected float calculateRotation(float x1, float y1, float x2, float y2) {
        double x = x1 - x2;
        double y = y1 - y2;
        double radians = Math.atan2(y, x);
        return (float) Math.toDegrees(radians);
    }

    /**
     * calculate Distance in two fingers
     **/
    protected float calculateDistance(@Nullable MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) {
            return 0f;
        }
        return calculateDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
    }

    protected float calculateDistance(float x1, float y1, float x2, float y2) {
        double x = x1 - x2;
        double y = y1 - y2;

        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        if (onStickerOperationListener != null) {
            onStickerOperationListener.onSizeChange(w, h, oldW, oldH);
        }
    }

    public void setSize(int w, int h) {
        onSizeChanged(w, h, w, h);
    }

    public void gava(@Nullable Sticker sticker) {
        if (sticker == null) {
            return;
        }

        float width = getWidth();
        float height = getHeight();
        float stickerWidth = sticker.getWidth();
        float stickerHeight = sticker.getHeight();
        //step 1
        float offsetX = (width - stickerWidth) / 2;
        float offsetY = (height - stickerHeight) / 2;

        sizeMatrix.postTranslate(offsetX, offsetY);
        invalidate();
    }

    /**
     * Sticker's drawable will be too bigger or smaller
     * This method is to transform it to fit
     * step 1：let the center of the sticker image is coincident with the center of the View.
     * step 2：Calculate the zoom and zoom
     **/
    protected void transformSticker(@Nullable Sticker sticker) {
        if (sticker == null) {
            return;
        }

        sizeMatrix.reset();

        float width = getWidth();
        float height = getHeight();
        float stickerWidth = sticker.getWidth();
        float stickerHeight = sticker.getHeight();
        //step 1
        float offsetX = (width - stickerWidth) / 2;
        float offsetY = (height - stickerHeight) / 2;

        sizeMatrix.postTranslate(offsetX, offsetY);

        //step 2
        float scaleFactor;
        if (width < height) {
            scaleFactor = width / stickerWidth;
        } else {
            scaleFactor = height / stickerHeight;
        }

        sizeMatrix.postScale(scaleFactor / 2f, scaleFactor / 2f, width / 2f, height / 2f);

        sticker.getMatrix().reset();
        sticker.setMatrix(sizeMatrix);

        invalidate();
    }

    public void flipCurrentSticker(int direction) {
        flip(handlingSticker, direction);
    }

    public void flip(@Nullable Sticker sticker, @Flip int direction) {
        if (sticker != null) {
            sticker.getCenterPoint(midPoint);
            if ((direction & FLIP_HORIZONTALLY) > 0) {
                sticker.getMatrix().preScale(-1, 1, midPoint.x, midPoint.y);
                sticker.setFlippedHorizontally(!sticker.isFlippedHorizontally());
            }
            if ((direction & FLIP_VERTICALLY) > 0) {
                sticker.getMatrix().preScale(1, -1, midPoint.x, midPoint.y);
                sticker.setFlippedVertically(!sticker.isFlippedVertically());
            }

            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerFlipped(sticker);
            }

            invalidate();
        }
    }

    public boolean replace(@Nullable Sticker sticker) {
        return replace(sticker, false);
    }

    public boolean replace(@Nullable Sticker sticker, boolean needStayState) {
        if (handlingSticker != null && sticker != null) {
            float width = getWidth();
            float height = getHeight();
            if (needStayState) {
                sticker.setMatrix(handlingSticker.getMatrix());
                sticker.setFlippedVertically(handlingSticker.isFlippedVertically());
                sticker.setFlippedHorizontally(handlingSticker.isFlippedHorizontally());
            } else {
                handlingSticker.getMatrix().reset();
                // reset scale, angle, and put it in center
                float offsetX = (width - handlingSticker.getWidth()) / 2f;
                float offsetY = (height - handlingSticker.getHeight()) / 2f;
                sticker.getMatrix().postTranslate(offsetX, offsetY);

                float scaleFactor;
                if (width < height) {
                    scaleFactor = width / handlingSticker.getDrawable().getIntrinsicWidth();
                } else {
                    scaleFactor = height / handlingSticker.getDrawable().getIntrinsicHeight();
                }
                sticker.getMatrix().postScale(scaleFactor / 2f, scaleFactor / 2f, width / 2f, height / 2f);
            }
            int index = stickers.indexOf(handlingSticker);
            stickers.set(index, sticker);
            handlingSticker = sticker;

            invalidate();
            return true;
        } else {
            return false;
        }
    }

    public boolean remove(@Nullable Sticker sticker) {
        if (stickers.contains(sticker)) {
            stickers.remove(sticker);
            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerDeleted(sticker);
            }
            if (handlingSticker == sticker) {
                handlingSticker = null;
            }
            invalidate();

            return true;
        } else {
            return false;
        }
    }

    public boolean removeCurrentSticker() {
        return remove(handlingSticker);
    }

    public void removeAllStickers() {
        stickers.clear();
        if (handlingSticker != null) {
            handlingSticker.release();
            handlingSticker = null;
        }
        invalidate();
    }

    @NonNull
    public StickerView addSticker(@NonNull Sticker sticker) {
        return addSticker(sticker, Sticker.Position.CENTER);
    }

    public StickerView addSticker(@NonNull final Sticker sticker, final @Sticker.Position int position) {
        Log.d("LAM", "addSticker: ");
        post(() -> addStickerImmediately(sticker, position));
        return this;
    }

    protected void addStickerImmediately(@NonNull Sticker sticker, @Sticker.Position int position) {
        setStickerPosition(sticker, position);

        float scaleFactor, widthScaleFactor, heightScaleFactor;

        widthScaleFactor = (float) getWidth() / sticker.getDrawable().getIntrinsicWidth();
        heightScaleFactor = (float) getHeight() / sticker.getDrawable().getIntrinsicHeight();
        scaleFactor = widthScaleFactor > heightScaleFactor ? heightScaleFactor : widthScaleFactor;
        if(sticker instanceof  DrawableSticker) {
            sticker.getMatrix().postScale(scaleFactor / 4, scaleFactor / 4, getWidth() / 2, getHeight() / 2);
        } else if(sticker instanceof BitmapSticker) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            float targetWidth = width / 3f;
            float originalWidth = sticker.getWidth();
            float scale = targetWidth / originalWidth;
            sticker.getMatrix().postScale(scale, scale, getWidth() / 2, getHeight() / 2);
        }
        else {
            sticker.getMatrix().postScale(scaleFactor / 2, scaleFactor / 2, getWidth() / 2, getHeight() / 2);
        }

        handlingSticker = sticker;
        stickers.add(sticker);
        if (currentSticker < stickers.size()) {
            stickers.get(currentSticker).setSelected(false);
        } else {
            stickers.get(stickers.size() - 1).setSelected(false);
        }
        if (onStickerOperationListener != null) {
            onStickerOperationListener.onStickerAdded(sticker);
        }
        updatePositionSticker();
        invalidate();
    }


    private void updatePositionSticker() {
        try {
            if (handlingSticker instanceof TextSticker && ((TextSticker) handlingSticker).getCurve() != 0) {
                handlingSticker.getMappedCenterPointCurve(currentCenterPoint, point, tmp, ((TextSticker) handlingSticker).getTextRectCurve());
                x = handlingSticker.getMappedCenterPointCurve(((TextSticker) handlingSticker).getTextRectCurve()).x;
                y = handlingSticker.getMappedCenterPointCurve(((TextSticker) handlingSticker).getTextRectCurve()).y;
            } else {
                x = handlingSticker.getMappedCenterPoint().x;
                y = handlingSticker.getMappedCenterPoint().y;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStickerPosition(@NonNull Sticker sticker, @Sticker.Position int position) {
        float width = getWidth();
        float height = getHeight();
        float offsetX = width - sticker.getWidth();
        float offsetY = height - sticker.getHeight();
        if ((position & Sticker.Position.TOP) > 0) {
            offsetY /= 4f;
        } else if ((position & Sticker.Position.BOTTOM) > 0) {
            offsetY *= 3f / 4f;
        } else {
            offsetY /= 2f;
        }
        if ((position & Sticker.Position.LEFT) > 0) {
            offsetX /= 4f;
        } else if ((position & Sticker.Position.RIGHT) > 0) {
            offsetX *= 3f / 4f;
        } else {
            offsetX /= 2f;
        }

        sticker.getMatrix().postTranslate(offsetX, offsetY);

    }

    public void getStickerPoints(@Nullable Sticker sticker, @NonNull float[] dst) {
        if (sticker == null) {
            Arrays.fill(dst, 0);
            return;
        }
        sticker.getBoundPoints(bounds);
        sticker.getMappedPoints(dst, bounds);
    }

    public void getStickerPoints2(@Nullable Sticker sticker, @NonNull float[] dst) {
        if (sticker == null) {
            Arrays.fill(dst, 0);
            return;
        }
        ((TextSticker) sticker).getBoundPointCurve(bounds);
        sticker.getMappedPoints(dst, bounds);
    }

    public String save(Context context, @NonNull File file, boolean setWallpaper, StickerUtils.ProgressListener listener) {
        try {
            String path = StickerUtils.saveImageToGallery(getContext(), file, createBitmap(), listener);
            if(setWallpaper && HawkHelper.isWallPaperAuto()) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), bmOptions);
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(context.getApplicationContext());
                try {
                    wallpaperManager.setBitmap(bitmap);
                } catch (IOException | NullPointerException e) {
                }
            }
            return path;
        } catch (IllegalArgumentException | IllegalStateException ignored) {
            return "";
        }
    }

    public String saveImage(@NonNull Context context, String folderName) {
        return StickerUtils.saveImageToExternalFileDir(context, createBitmapForSave(), folderName);
    }

    public Bitmap createBitmap() {
        if (isDrawSticker()) {
            handlingSticker = null;
        }
        if (currentSticker < stickers.size()) {
            stickers.get(currentSticker).setSelected(false);
        }
        Bitmap bitmap;
        try {
            bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            this.draw(canvas);
        } catch (OutOfMemoryError error) {
            AnalyticsManager.getInstance().trackEvent(new Event("OUTOFMEMORY_CREATEBITMAP", new Bundle()));
            int height = 300 * getWidth() / getHeight();
            bitmap = Bitmap.createBitmap(300, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            this.draw(canvas);
        } catch (Exception error) {
            bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            canvas.drawRect(0F, 0F, 300, 300, paint);
        }
        return bitmap;
    }

    public Bitmap createBitmapForSave() {
        if (isDrawSticker()) {
            handlingSticker = null;
        }
        if (currentSticker < stickers.size()) {
            stickers.get(currentSticker).setSelected(false);
        }
        Bitmap bitmap;
        try {
            bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            this.draw(canvas);


        } catch (OutOfMemoryError error) {
            AnalyticsManager.getInstance().trackEvent(new Event("OUTOFMEMORY_CREATEBITMAP", new Bundle()));
            return null;
        } catch (Exception error) {
            return null;
        }
        return bitmap;
    }

    public int getStickerCount() {
        return stickers.size();
    }

    public boolean isNoneSticker() {
        return getStickerCount() == 0;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isDisableClick() {
        return disableClick;
    }

    public void setDisableClick(boolean disableClick) {
        this.disableClick = disableClick;
    }

    @NonNull
    public StickerView setLocked(boolean locked) {
        this.locked = locked;
        invalidate();
        return this;
    }

    @NonNull
    public StickerView setMinClickDelayTime(int minClickDelayTime) {
        this.minClickDelayTime = minClickDelayTime;
        return this;
    }

    public int getMinClickDelayTime() {
        return minClickDelayTime;
    }

    public boolean isConstrained() {
        return constrained;
    }

    @NonNull
    public StickerView setConstrained(boolean constrained) {
        this.constrained = constrained;
        postInvalidate();
        return this;
    }

    @NonNull
    public StickerView setOnStickerOperationListener(@Nullable OnStickerOperationListener onStickerOperationListener) {
        this.onStickerOperationListener = onStickerOperationListener;
        return this;
    }

    @Nullable
    public OnStickerOperationListener getOnStickerOperationListener() {
        return onStickerOperationListener;
    }

    @Nullable
    public Sticker getCurrentSticker() {
        return handlingSticker;
    }

    @NonNull
    public List<BitmapStickerIcon> getIcons() {
        return icons;
    }

    public void setIcons(@NonNull List<BitmapStickerIcon> icons) {
        this.icons.clear();
        Log.d("LAM", "setIcons: " + icons.size());
        this.icons.addAll(icons);
        invalidate();
    }

    private void stickerCheck() {
        if (handlingSticker != null) {
            if (currentSticker < stickers.size()) {
                stickers.get(currentSticker).setSelected(true);
            }
            if (oldSticker != currentSticker && oldSticker < stickers.size()) {
                stickers.get(oldSticker).setSelected(false);
            }
        } else {
            if (currentSticker < stickers.size()) {
                stickers.get(currentSticker).setSelected(false);
            }
        }
    }

    public interface OnStickerOperationListener {
        void onStickerAdded(@NonNull Sticker sticker);

        void onStickerClicked(@NonNull Sticker sticker);

        void onStickerDeleted(@NonNull Sticker sticker);

        void onStickerDragFinished(@NonNull Sticker sticker);

        void onStickerTouchedDown(@NonNull Sticker sticker);

        void onStickerZoomFinished(@NonNull Sticker sticker);

        void onStickerFlipped(@NonNull Sticker sticker);

        void onStickerDoubleTapped(@NonNull Sticker sticker);

        void onSizeChange(int w, int h, int oldW, int oldH);

    }

    public void caculateRangerLockSticker(Sticker sticker) {
        float dx;
        if (sticker instanceof TextSticker && ((TextSticker) sticker).getCurve() != 0) {
            sticker.getMappedCenterPointCurve(currentCenterPoint, point, tmp, ((TextSticker) sticker).getTextRectCurve());
        } else {
            sticker.getMappedCenterPoint(currentCenterPoint, point, tmp);
        }
        float dy = 0;
        if (this.currentCenterPoint.x <= (((float) getWidth()) / 2.0f) - 15.0f || this.currentCenterPoint.x >= (((float) getWidth()) / 2.0f) + 15.0f) {
            this.allowShowLineHorizon = false;
            dx = 0;
        } else {
            dx = (((float) getWidth()) / 2.0f) - this.currentCenterPoint.x;
            this.allowShowLineHorizon = true;
        }
        if (this.currentCenterPoint.y <= (((float) getHeight()) / 2.0f) - 15.0f || this.currentCenterPoint.y >= (((float) getHeight()) / 2.0f) + 15.0f) {
            this.allowShowLineVertical = false;
        } else {
            dy = (((float) getHeight()) / 2.0f) - this.currentCenterPoint.y;
            this.allowShowLineVertical = true;
        }
        sticker.getMatrix().postTranslate(dx, dy);
    }
}
