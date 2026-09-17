package com.ultron.assistant.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class UltronCoreView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF oval = new RectF();

    private float rotation = 0f;
    private float pulse = 0f;
    private boolean active = true;
    private int mode = 0; // 0 standby, 1 listening, 2 processing
    private long lastTime = 0L;

    public UltronCoreView(Context context) {
        super(context);
        paint.setStrokeCap(Paint.Cap.ROUND);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public void setMode(int newMode) {
        mode = newMode;
        active = true;
        invalidate();
    }

    public void sleep() {
        active = false;
        mode = 0;
        invalidate();
    }

    public void wake() {
        active = true;
        mode = 0;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float r = Math.min(getWidth(), getHeight()) * 0.31f;

        long now = System.currentTimeMillis();
        if (lastTime == 0L) lastTime = now;
        float dt = Math.min(50f, now - lastTime);
        lastTime = now;

        if (active) {
            float speed = mode == 1 ? 0.32f : mode == 2 ? 0.22f : 0.055f;
            rotation += dt * speed;
            pulse += dt * 0.004f;
        }

        float pulseWave = (float) ((Math.sin(pulse) + 1.0) * 0.5);
        float glow = active ? (mode == 1 ? 34f : mode == 2 ? 28f : 18f) : 7f;

        // Deep reactor background
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(android.graphics.Color.rgb(3, 12, 20));
        paint.setShadowLayer(dp(18), 0, 0,
                android.graphics.Color.argb(150, 0, 220, 235));
        canvas.drawCircle(cx, cy, r * 1.08f, paint);
        paint.clearShadowLayer();

        // Outer reactor rings
        drawRing(canvas, cx, cy, r * 1.55f, 1.5f, 150, rotation * 0.55f);
        drawRing(canvas, cx, cy, r * 1.42f, 3f, 190, -rotation * 0.75f);
        drawRing(canvas, cx, cy, r * 1.29f, 5f, 230, rotation);
        drawRing(canvas, cx, cy, r * 1.13f, 2f, 150, -rotation * 1.25f);

        // Segmented scanning arcs
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(7));
        paint.setColor(android.graphics.Color.rgb(0, 235, 255));
        paint.setShadowLayer(dp(12), 0, 0,
                android.graphics.Color.argb(210, 0, 230, 255));

        oval.set(cx - r * 1.48f, cy - r * 1.48f,
                cx + r * 1.48f, cy + r * 1.48f);
        canvas.save();
        canvas.rotate(rotation, cx, cy);
        canvas.drawArc(oval, 8, 58, false, paint);
        canvas.drawArc(oval, 178, 38, false, paint);
        canvas.drawArc(oval, 278, 26, false, paint);
        canvas.restore();
        paint.clearShadowLayer();

        // Inner energy rings
        drawRing(canvas, cx, cy, r * 0.92f, 2.5f, 220, -rotation * 1.4f);
        drawRing(canvas, cx, cy, r * 0.74f, 3.5f, 235, rotation * 1.7f);

        // Central reactor
        float coreR = r * (0.43f + pulseWave * 0.035f);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(android.graphics.Color.rgb(4, 28, 40));
        paint.setShadowLayer(dp(24), 0, 0,
                android.graphics.Color.argb((int) glow + 100, 0, 235, 255));
        canvas.drawCircle(cx, cy, coreR, paint);
        paint.clearShadowLayer();

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(3));
        paint.setColor(android.graphics.Color.rgb(0, 245, 255));
        paint.setShadowLayer(dp(10), 0, 0,
                android.graphics.Color.argb(230, 0, 230, 255));
        canvas.drawCircle(cx, cy, coreR, paint);
        paint.clearShadowLayer();

        // ULTRON energy symbol
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(android.graphics.Color.rgb(0, 245, 255));
        paint.setShadowLayer(dp(18), 0, 0,
                android.graphics.Color.argb(230, 0, 230, 255));

        canvas.drawCircle(cx, cy, coreR * 0.17f, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(3));

        canvas.save();
        canvas.rotate(rotation * 0.65f, cx, cy);
        canvas.drawCircle(cx, cy, coreR * 0.30f, paint);
        canvas.drawLine(cx - coreR * 0.52f, cy,
                cx - coreR * 0.24f, cy, paint);
        canvas.drawLine(cx + coreR * 0.24f, cy,
                cx + coreR * 0.52f, cy, paint);
        canvas.restore();

        paint.clearShadowLayer();

        // Four reactor nodes
        paint.setStyle(Paint.Style.FILL);
        float nodeR = dp(5);

        for (int i = 0; i < 4; i++) {
            double a = Math.toRadians(rotation + i * 90);
            float nx = cx + (float) Math.cos(a) * r * 0.62f;
            float ny = cy + (float) Math.sin(a) * r * 0.62f;

            paint.setColor(android.graphics.Color.rgb(0, 235, 255));
            paint.setShadowLayer(dp(10), 0, 0,
                    android.graphics.Color.argb(220, 0, 230, 255));
            canvas.drawCircle(nx, ny, nodeR, paint);
            paint.clearShadowLayer();
        }

        // Keep animation alive
        postInvalidateOnAnimation();
    }

    private void drawRing(Canvas canvas, float cx, float cy,
                          float radius, float width,
                          int alpha, float angle) {

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(width));
        paint.setColor(android.graphics.Color.argb(
                alpha, 0, 220, 240));
        paint.setShadowLayer(dp(7), 0, 0,
                android.graphics.Color.argb(
                        alpha / 2, 0, 220, 240));

        oval.set(cx - radius, cy - radius,
                cx + radius, cy + radius);

        canvas.save();
        canvas.rotate(angle, cx, cy);

        canvas.drawArc(oval, 0, 52, false, paint);
        canvas.drawArc(oval, 82, 48, false, paint);
        canvas.drawArc(oval, 170, 72, false, paint);
        canvas.drawArc(oval, 286, 48, false, paint);

        canvas.restore();
        paint.clearShadowLayer();
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }
}
