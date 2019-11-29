package com.xw.repo.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;

public class BounceScrollView extends NestedScrollView {

    private static final float DEFAULT_DAMPING_COEFFICIENT = 4.0f;
    private static final int DEFAULT_SCROLL_THRESHOLD = 20;
    private static final long DEFAULT_BOUNCE_DELAY = 400;

    private boolean isHorizontal;
    private float mDamping;
    private boolean mIncrementalDamping;
    private long mBounceDelay;
    private int mTriggerOverScrollThreshold;
    private boolean mDisableBounce;

    private Interpolator mInterpolator;
    private View mChildView;
    private float mStart;
    private int mPreDelta;
    private int mOverScrolledDistance;
    private ObjectAnimator mAnimator;
    private OnScrollListener mScrollListener;
    private OnOverScrollListener mOverScrollListener;

    public BounceScrollView(@NonNull Context context) {
        this(context, null);
    }

    public BounceScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BounceScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        setFillViewport(true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BounceScrollView, 0, 0);
        mDamping = a.getFloat(R.styleable.BounceScrollView_damping, DEFAULT_DAMPING_COEFFICIENT);
        int orientation = a.getInt(R.styleable.BounceScrollView_scrollOrientation, 0);
        isHorizontal = orientation == 1;
        mIncrementalDamping = a.getBoolean(R.styleable.BounceScrollView_incrementalDamping, true);
        mBounceDelay = a.getInt(R.styleable.BounceScrollView_bounceDelay, (int) DEFAULT_BOUNCE_DELAY);
        mTriggerOverScrollThreshold = a.getInt(R.styleable.BounceScrollView_triggerOverScrollThreshold, DEFAULT_SCROLL_THRESHOLD);
        mDisableBounce = a.getBoolean(R.styleable.BounceScrollView_disableBounce, false);
        boolean enable = a.getBoolean(R.styleable.BounceScrollView_nestedScrollingEnabled, true);
        a.recycle();

        setNestedScrollingEnabled(enable);
        mInterpolator = new DefaultQuartOutInterpolator();
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return !isHorizontal;
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return isHorizontal;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mChildView == null && getChildCount() > 0 || mChildView != getChildAt(0)) {
            mChildView = getChildAt(0);
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mChildView == null || mDisableBounce)
            return super.onTouchEvent(ev);

        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mStart = isHorizontal ? ev.getX() : ev.getY();

                break;
            case MotionEvent.ACTION_MOVE:
                float now, delta;
                int dampingDelta;

                now = isHorizontal ? ev.getX() : ev.getY();
                delta = mStart - now;
                dampingDelta = (int) (delta / calculateDamping());
                mStart = now;

                boolean onePointerTouch = true;
                if (mPreDelta <= 0 && dampingDelta > 0) {
                    onePointerTouch = false;
                } else if (mPreDelta >= 0 && dampingDelta < 0) {
                    onePointerTouch = false;
                }
                mPreDelta = dampingDelta;

                if (onePointerTouch && canMove(dampingDelta)) {
                    mOverScrolledDistance += dampingDelta;
                    if (isHorizontal) {
                        mChildView.setTranslationX(-mOverScrolledDistance);
                    } else {
                        mChildView.setTranslationY(-mOverScrolledDistance);
                    }
                    if (mOverScrollListener != null) {
                        mOverScrollListener.onOverScrolling(mOverScrolledDistance <= 0, Math.abs(mOverScrolledDistance));
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mPreDelta = 0;
                mOverScrolledDistance = 0;

                cancelAnimator();
                if (isHorizontal) {
                    mAnimator = ObjectAnimator.ofFloat(mChildView, View.TRANSLATION_X, 0);
                } else {
                    mAnimator = ObjectAnimator.ofFloat(mChildView, View.TRANSLATION_Y, 0);
                }
                mAnimator.setDuration(mBounceDelay).setInterpolator(mInterpolator);
                if (mOverScrollListener != null) {
                    mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float value = (float) animation.getAnimatedValue();
                            mOverScrollListener.onOverScrolling(value <= 0, Math.abs((int) value));
                        }
                    });
                }
                mAnimator.start();

                break;
        }

        return super.onTouchEvent(ev);
    }

    private float calculateDamping() {
        float ratio;
        if (isHorizontal) {
            ratio = Math.abs(mChildView.getTranslationX()) * 1.0f / mChildView.getMeasuredWidth();
        } else {
            ratio = Math.abs(mChildView.getTranslationY()) * 1.0f / mChildView.getMeasuredHeight();
        }
        ratio += 0.2;

        if (mIncrementalDamping) {
            return mDamping / (1.0f - (float) Math.pow(ratio, 2));
        } else {
            return mDamping;
        }
    }

    private boolean canMove(int delta) {
        return delta < 0 ? canMoveFromStart() : canMoveFromEnd();
    }

    private boolean canMoveFromStart() {
        return isHorizontal ? getScrollX() == 0 : getScrollY() == 0;
    }

    private boolean canMoveFromEnd() {
        if (isHorizontal) {
            int offset = mChildView.getMeasuredWidth() - getWidth();
            offset = offset < 0 ? 0 : offset;
            return getScrollX() == offset;
        } else {
            int offset = mChildView.getMeasuredHeight() - getHeight();
            offset = offset < 0 ? 0 : offset;
            return getScrollY() == offset;
        }
    }

    private void cancelAnimator() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
    }

    @Override
    protected void onScrollChanged(int scrollX, int scrollY, int oldl, int oldt) {
        super.onScrollChanged(scrollX, scrollY, oldl, oldt);

        if (mScrollListener != null) {
            mScrollListener.onScrolling(scrollX, scrollY);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        cancelAnimator();
    }

    public void setScrollHorizontally(boolean horizontal) {
        this.isHorizontal = horizontal;
    }

    public boolean isScrollHorizontally() {
        return isHorizontal;
    }

    public float getDamping() {
        return mDamping;
    }

    public void setDamping(@FloatRange(from = 0, to = 100) float damping) {
        if (mDamping > 0) {
            mDamping = damping;
        }
    }

    public long getBounceDelay() {
        return mBounceDelay;
    }

    public void setBounceDelay(long bounceDelay) {
        if (bounceDelay >= 0) {
            mBounceDelay = bounceDelay;
        }
    }

    public void setIncrementalDamping(boolean incrementalDamping) {
        mIncrementalDamping = incrementalDamping;
    }

    public boolean isIncrementalDamping() {
        return mIncrementalDamping;
    }

    public boolean isDisableBounce() {
        return mDisableBounce;
    }

    public void setDisableBounce(boolean disable) {
        mDisableBounce = disable;
    }

    public void setBounceInterpolator(@NonNull Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    public int getTriggerOverScrollThreshold() {
        return mTriggerOverScrollThreshold;
    }

    public void setTriggerOverScrollThreshold(int threshold) {
        if (threshold >= 0) {
            mTriggerOverScrollThreshold = threshold;
        }
    }

    public void setOnScrollListener(OnScrollListener scrollListener) {
        mScrollListener = scrollListener;
    }

    public void setOnOverScrollListener(OnOverScrollListener overScrollListener) {
        mOverScrollListener = overScrollListener;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static class DefaultQuartOutInterpolator implements Interpolator {

        @Override
        public float getInterpolation(float input) {
            return (float) (1.0f - Math.pow(1 - input, 4));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public interface OnScrollListener {
        void onScrolling(int scrollX, int scrollY);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public interface OnOverScrollListener {
        /**
         * @param fromStart LTR, the left is start; RTL, the right is start.
         */
        void onOverScrolling(boolean fromStart, int overScrolledDistance);
    }
}