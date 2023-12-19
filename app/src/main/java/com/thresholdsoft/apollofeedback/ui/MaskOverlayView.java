package com.thresholdsoft.apollofeedback.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

public class MaskOverlayView extends View {

    private Paint maskPaint;
    private Paint clearPaint;

    public MaskOverlayView(Context context) {
        super(context);
        init();
    }

    public MaskOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        maskPaint = new Paint();
        maskPaint.setColor(Color.BLACK);
        maskPaint.setAlpha(100); // Semi-transparent

        clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR)); // Clear mode
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the semi-transparent mask
        canvas.drawRect(0, 0, getWidth(), getHeight(), maskPaint);

        // Calculate center and radius for the circle
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int radius = Math.min(getWidth(), getHeight()) / 4; // Adjust the size as needed

        // Draw the clear circle
        canvas.drawCircle(cx, cy, radius, clearPaint);
    }
}
