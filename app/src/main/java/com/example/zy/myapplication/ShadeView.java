package com.example.zy.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class ShadeView extends View {

    /**
     * 图标
     */
    private Bitmap iconBitmap;
    /**
     * 图标背景色
     */
    private int iconBackgroundColor;
    /**
     * 图标默认背景色
     */
    private final int DEFAULT_ICON_BACKGROUND_COLOR = 0x3CAF36;
    /**
     * 图标底部文本
     */
    private String text;
    /**
     * 图标底部文字默认大小（sp）
     */
    private final int DEFAULT_TEXT_SIZE = 12;
    /**
     * 图标底部文字默认颜色
     */
    private final int DEFAULT_TEXT_COLOR = 0x2B2B2B;
    /**
     * 图标绘制范围
     */
    private Rect iconRect;
    /**
     * 文字笔画
     */
    private Paint textPaint;
    /**
     * 文字范围
     */
    private Rect textBound;
    /**
     * 透明度（0.0-1.0）
     */
    private float mAlpha;

    private Bitmap mBitmap;

    public ShadeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //获取自定义属性值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShadeView);
        BitmapDrawable drawable = (BitmapDrawable) typedArray.getDrawable(R.styleable.ShadeView_icon);
        if (drawable != null) {
            iconBitmap = drawable.getBitmap();
        }
        iconBackgroundColor = typedArray.getColor(R.styleable.ShadeView_color, DEFAULT_ICON_BACKGROUND_COLOR);
        text = typedArray.getString(R.styleable.ShadeView_text);
        int textSize = (int) typedArray.getDimension(R.styleable.ShadeView_text_size,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE, getResources().getDisplayMetrics()));
        //资源回收
        typedArray.recycle();
        //初始化
        textBound = new Rect();
        textPaint = new Paint();
        textPaint.setTextSize(textSize);
        textPaint.setColor(DEFAULT_TEXT_COLOR);
        textPaint.setAntiAlias(true);
        textPaint.setDither(true);
        textPaint.getTextBounds(text, 0, text.length(), textBound);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //因为图标是正方形且需要居中显示的，所以View的大小去掉padding和文字所占空间后，
        //剩余的空间的宽和高的最小值才是图标的边长
        int bitmapSide = Math.min(getMeasuredWidth() - getPaddingLeft()
                - getPaddingRight(), getMeasuredHeight() - getPaddingTop()
                - getPaddingBottom() - textBound.height());
        int left = getMeasuredWidth() / 2 - bitmapSide / 2;
        int top = (getMeasuredHeight() - textBound.height()) / 2 - bitmapSide / 2;
        //获取图标的绘制范围
        iconRect = new Rect(left, top, left + bitmapSide, top + bitmapSide);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //进一取整
        int alpha = (int) Math.ceil((255 * mAlpha));
        //绘制原图标
        canvas.drawBitmap(iconBitmap, null, iconRect, null);
        setupTargetBitmap(alpha);
        drawSourceText(canvas, alpha);
        drawTargetText(canvas, alpha);
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    /**
     * 在mBitmap上绘制以iconBackgroundColor颜色为Dst，DST_IN模式下的图标
     *
     * @param alpha Src颜色的透明度
     */
    private void setupTargetBitmap(int alpha) {
        mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(mBitmap);
        Paint paint = new Paint();
        paint.setColor(iconBackgroundColor);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setAlpha(alpha);
        //在图标背后先绘制一层iconBackgroundColor颜色的背景
        canvas.drawRect(iconRect, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        paint.setAlpha(255);
        //在mBitmap上绘制以iconBackgroundColor颜色为Dst，DST_IN模式下的图标
        canvas.drawBitmap(iconBitmap, null, iconRect, paint);
    }

    /**
     * 绘制默认状态下的字体
     *
     * @param canvas Canvas
     * @param alpha  字体颜色透明度
     */
    private void drawSourceText(Canvas canvas, int alpha) {
        textPaint.setColor(DEFAULT_TEXT_COLOR);
        textPaint.setAlpha(255 - alpha);
        canvas.drawText(text, iconRect.left + iconRect.width() / 2 - textBound.width() / 2,
                iconRect.bottom + textBound.height(), textPaint);
    }

    /**
     * 绘制滑动到该标签时的字体
     *
     * @param canvas Canvas
     * @param alpha  字体颜色透明度
     */
    private void drawTargetText(Canvas canvas, int alpha) {
        textPaint.setColor(iconBackgroundColor);
        textPaint.setAlpha(alpha);
        canvas.drawText(text, iconRect.left + iconRect.width() / 2 - textBound.width() / 2,
                iconRect.bottom + textBound.height(), textPaint);
    }

    /**
     * 设置图标透明度并重绘
     *
     * @param alpha 透明度
     */
    public void setIconAlpha(float alpha) {
        if (mAlpha != alpha) {
            this.mAlpha = alpha;
            invalidateView();
        }
    }

    public void setIconBitmap(Context context, int resourceID) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(context, resourceID);
        if (!bitmapDrawable.getBitmap().equals(iconBitmap)) {
            iconBitmap = bitmapDrawable.getBitmap();
            invalidateView();
        }
    }

    /**
     * 判断当前是否为UI线程，是则直接重绘，否则调用postInvalidate()利用Handler来重绘
     */
    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    private static final String STATE_INSTANCE = "STATE_INSTANCE";

    private static final String STATE_ALPHA = "STATE_ALPHA";

    /**
     * 保存状态
     *
     * @return Parcelable
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_INSTANCE, super.onSaveInstanceState());
        bundle.putFloat(STATE_ALPHA, mAlpha);
        return bundle;
    }

    /**
     * 恢复状态
     *
     * @param parcelable Parcelable
     */
    @Override
    protected void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof Bundle) {
            Bundle bundle = (Bundle) parcelable;
            mAlpha = bundle.getFloat(STATE_ALPHA);
            super.onRestoreInstanceState(bundle.getParcelable(STATE_INSTANCE));
        } else {
            super.onRestoreInstanceState(parcelable);
        }
    }

}
