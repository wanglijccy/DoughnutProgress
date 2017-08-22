package com.demo.mycircle;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;


/**
 * Created by passion on 2017/2/23.
 */
public class PercentDrawable extends Drawable {

    private int COLOR_NORMAL = Color.parseColor("#5677FC");
    private final static int COLOR_TEXT = Color.parseColor("#99000000");
    private final static int COLOR_FLOOR = Color.parseColor("#80c6c5c5");

    private float mBackgroundFactor = 1f;
    private float mPaddingTopFactor = 1f;
    private float mOuterCycleRingFactor = 1f;
    private float mStrokeWidthFactor = 0.12f * mBackgroundFactor;

    private final static float ALPHA_ARC_START_ANGLE = 270;
    private final static float ALPHA_ARC_SWEEP_ANGLE = 360;

    private Context mContext;
    //初始百分比为100%
    private int mCurrentPercent = 100;
    private int mPreviousPercent;

    private boolean isFirstDraw = true;

    private Paint mPercentTextPaint = new Paint();//文字
    private Paint mArcPaint = new Paint();
    private Paint mCirclePaint = new Paint();
    private float mMemoryArcStrokeWidth;

    private RectF mOuterRingOval;
    private RectF mStartCycleOval;
    private RectF mEndCycleOval;

    private AnimatorParam mAnimatorParam;

    private float mOuterRingStartAngle = ALPHA_ARC_START_ANGLE;
    private float mOuterRingSweepAngle = ALPHA_ARC_SWEEP_ANGLE;

    private ValueAnimator percentAnim;

    public PercentDrawable(Context context) {
        this.mContext = context;
        initPaint();
    }

    public PercentDrawable(Context context, int color) {
        this.mContext = context;
        COLOR_NORMAL = color;
        initPaint();
    }

    // 初始化画笔风格
    private void initPaint() {
        int numberTextSize = mContext.getResources().getDimensionPixelSize(R.dimen.percent_number_text_size);

        //文字画笔
        mPercentTextPaint.setStyle(Paint.Style.STROKE);//设置空心
        mPercentTextPaint.setAntiAlias(true);//设置画笔的锯齿效果
        mPercentTextPaint.setTextSize(numberTextSize);
        mPercentTextPaint.setColor(COLOR_TEXT);

        //初始 圆角 弧线的画笔风格；
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.FILL);

        // 初始化外环动画时代表内存状态的画笔
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.STROKE);
    }

    // 计算起始点位置
    private void initParam() {
        setDrawableProperty();
    }

    @Override
    public void draw(Canvas canvas) {
        if (isFirstDraw) {
            initParam();
            isFirstDraw = false;
        }
        drawBaseView(canvas);
        drawNumberText(canvas);
        drawAnimator(canvas);
    }

    //设置百分比
    public void setCurrentPercent(int percent) {
        mCurrentPercent = percent;
        mOuterRingSweepAngle = (mCurrentPercent * 1f / 100) * ALPHA_ARC_SWEEP_ANGLE;
        invalidateSelf();
    }

    @Override
    public void setAlpha(int alpha) {
        mPercentTextPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override
    public int getOpacity() {
        return 0;
    }

    private void drawAnimator(Canvas canvas) {
        mCirclePaint.setColor(COLOR_NORMAL);
        computerAnimatorDrawableProperty();
        mCirclePaint.setStrokeWidth(mMemoryArcStrokeWidth);
        canvas.drawArc(mOuterRingOval, mOuterRingStartAngle, mOuterRingSweepAngle, false, mCirclePaint);
        mArcPaint.setColor(COLOR_NORMAL);
        canvas.drawArc(mStartCycleOval, 0, ALPHA_ARC_SWEEP_ANGLE, false, mArcPaint);
        canvas.drawArc(mEndCycleOval, 0, ALPHA_ARC_SWEEP_ANGLE, false, mArcPaint);
    }

    //画背景圆环
    private void drawBaseView(Canvas canvas) {

        mCirclePaint.setColor(COLOR_FLOOR);
        canvas.drawArc(mOuterRingOval, ALPHA_ARC_START_ANGLE, ALPHA_ARC_SWEEP_ANGLE, false, mCirclePaint);
    }

    // 画数字
    private void drawNumberText(Canvas canvas) {
        Paint.FontMetrics fontMetrics = mPercentTextPaint.getFontMetrics();
        String percent = mCurrentPercent + "%";
        float textWidth = mPercentTextPaint.measureText(percent);
        canvas.drawText(percent, mAnimatorParam.centerX - textWidth / 2,
                mAnimatorParam.centerY - fontMetrics.ascent / 2, mPercentTextPaint);
    }

    class AnimatorParam {
        int centerX;
        int centerY;
        int availableArea;
        float outerRingRadius;
        float paddingTopRadius;

        AnimatorParam(int centerX, int centerY, int availableArea, float outerRingRadius, float paddingTopRadius) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.availableArea = availableArea;
            this.outerRingRadius = outerRingRadius;
            this.paddingTopRadius = paddingTopRadius;
        }
    }

    private void setDrawableProperty() {
        int centerX = (getBounds().right - getBounds().left) / 2;
        int centerY = (getBounds().bottom - getBounds().top) / 2;
        int availableArea = Math.min(centerX * 2, centerY * 2);

        float mPaddingTopRadius = availableArea * 1.0f * mPaddingTopFactor / 2;
        float outerRingRadius = availableArea * 1.0f * mOuterCycleRingFactor / 2;

        mMemoryArcStrokeWidth = availableArea * mStrokeWidthFactor / 2;

        mOuterRingOval = new RectF(centerX - outerRingRadius + mMemoryArcStrokeWidth / 2, centerY - outerRingRadius + mMemoryArcStrokeWidth / 2,
                centerX + outerRingRadius - mMemoryArcStrokeWidth / 2, centerY + outerRingRadius - mMemoryArcStrokeWidth / 2);

        mAnimatorParam = new AnimatorParam(centerX, centerY, availableArea, outerRingRadius, mPaddingTopRadius);

    }

    private void computerAnimatorDrawableProperty() {
        float radius = mMemoryArcStrokeWidth / 2;
        float r = mAnimatorParam.outerRingRadius - mMemoryArcStrokeWidth / 2;
        float startCenterX = (((float) Math.cos((mOuterRingStartAngle / 180) * Math.PI)) * r) + mAnimatorParam.centerX;
        float startCenterY = (((float) Math.sin((mOuterRingStartAngle / 180) * Math.PI)) * r) + mAnimatorParam.centerY;
        float endCenterX = (((float) Math.cos(((mOuterRingStartAngle + mOuterRingSweepAngle) / 180) * Math.PI)) * r) + mAnimatorParam.centerX;
        float endCenterY = (((float) Math.sin(((mOuterRingStartAngle + mOuterRingSweepAngle) / 180) * Math.PI)) * r) + mAnimatorParam.centerY;
        mStartCycleOval = new RectF(startCenterX - radius, startCenterY - radius, startCenterX + radius, startCenterY + radius);
        mEndCycleOval = new RectF(endCenterX - radius, endCenterY - radius, endCenterX + radius, endCenterY + radius);

    }

    private void resetParam() {
        mCurrentPercent = 100;
        mOuterRingStartAngle = ALPHA_ARC_START_ANGLE;
        mOuterRingSweepAngle = ALPHA_ARC_SWEEP_ANGLE;
    }


    public void startAnimationWithPercent(final int percent) {
        if (percentAnim == null) {
            percentAnim = ValueAnimator.ofFloat(0, 1f);
        }
        if (percentAnim.isRunning()) {
            return;
        }
        percentAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float process = (float) animation.getAnimatedValue();
                setCurrentPercent((int) (mPreviousPercent - (mPreviousPercent - percent) * process));
            }
        });
        percentAnim.setDuration(1000);

        percentAnim.start();

        percentAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPreviousPercent = percent;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationStart(Animator animation) {

            }
        });
    }


}
