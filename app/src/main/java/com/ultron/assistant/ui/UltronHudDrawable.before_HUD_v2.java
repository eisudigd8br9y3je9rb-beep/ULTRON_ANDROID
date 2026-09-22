package com.ultron.assistant.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UltronHudDrawable extends Drawable {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float angle = 0f;
    private long lastTime = SystemClock.uptimeMillis();

    private final Runnable animator = new Runnable() {
        @Override
        public void run() {
            long now = SystemClock.uptimeMillis();
            float delta = (now - lastTime) / 1000f;
            lastTime = now;

            angle += delta * 24f;
            if (angle > 360f) angle -= 360f;

            invalidateSelf();
            scheduleSelf(this, now + 40);
        }
    };

    public UltronHudDrawable() {
        paint.setStrokeCap(Paint.Cap.ROUND);
        scheduleSelf(animator, SystemClock.uptimeMillis() + 40);
    }

    @Override
    public void draw(Canvas canvas) {
        float w = getBounds().width();
        float h = getBounds().height();

        // Deep black/blue base
        canvas.drawColor(Color.rgb(2, 7, 12));

        // Soft cyan core glow
        paint.setShader(new RadialGradient(
                w * 0.78f,
                h * 0.34f,
                Math.max(w, h) * 0.42f,
                new int[]{
                        Color.argb(45, 0, 255, 255),
                        Color.argb(18, 0, 180, 220),
                        Color.TRANSPARENT
                },
                null,
                Shader.TileMode.CLAMP
        ));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);

        // Fine HUD grid
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1f);
        paint.setColor(Color.argb(28, 0, 255, 255));

        float grid = 42f;
        for (float x = 0; x < w; x += grid) {
            canvas.drawLine(x, 0, x, h, paint);
        }
        for (float y = 0; y < h; y += grid) {
            canvas.drawLine(0, y, w, y, paint);
        }

        // Main circular HUD
        float cx = w * 0.76f;
        float cy = h * 0.28f;
        float base = Math.min(w, h) * 0.20f;

        paint.setColor(Color.argb(75, 0, 255, 255));
        paint.setStrokeWidth(3f);
        canvas.drawCircle(cx, cy, base, paint);
        canvas.drawCircle(cx, cy, base * 0.78f, paint);
        canvas.drawCircle(cx, cy, base * 0.48f, paint);

        // Rotating arc
        paint.setColor(Color.rgb(0, 240, 255));
        paint.setStrokeWidth(7f);
        canvas.drawArc(
                cx - base,
                cy - base,
                cx + base,
                cy + base,
                angle,
                105f,
                false,
                paint
        );

        // Inner rotating arc
        paint.setStrokeWidth(3f);
        canvas.drawArc(
                cx - base * 0.78f,
                cy - base * 0.78f,
                cx + base * 0.78f,
                cy + base * 0.78f,
                -angle * 1.7f,
                65f,
                false,
                paint
        );

        // Bottom-right system ring
        float rx = w * 0.78f;
        float ry = h * 0.78f;
        float rr = Math.min(w, h) * 0.13f;

        paint.setColor(Color.argb(80, 0, 255, 255));
        paint.setStrokeWidth(5f);
        canvas.drawCircle(rx, ry, rr, paint);

        paint.setColor(Color.rgb(0, 235, 255));
        paint.setStrokeWidth(6f);
        canvas.drawArc(
                rx - rr,
                ry - rr,
                rx + rr,
                ry + rr,
                -angle,
                115f,
                false,
                paint
        );

        // Left vertical HUD rail
        float railX = w * 0.075f;

        paint.setColor(Color.argb(150, 0, 240, 255));
        paint.setStrokeWidth(3f);
        canvas.drawLine(railX, h * 0.20f, railX, h * 0.78f, paint);

        paint.setStrokeWidth(1f);
        canvas.drawLine(railX + 12, h * 0.20f, railX + 12, h * 0.70f, paint);

        // Horizontal scanning line
        float scanY = (float) ((SystemClock.uptimeMillis() / 8) % Math.max(1, (int) h));

        paint.setColor(Color.argb(24, 0, 255, 255));
        paint.setStrokeWidth(2f);
        canvas.drawLine(0, scanY, w, scanY, paint);

        // HUD corner brackets
        paint.setColor(Color.argb(130, 0, 240, 255));
        paint.setStrokeWidth(2f);

        float m = 18f;
        float len = 45f;

        canvas.drawLine(m, m, m + len, m, paint);
        canvas.drawLine(m, m, m, m + len, paint);

        canvas.drawLine(w - m, m, w - m - len, m, paint);
        canvas.drawLine(w - m, m, w - m, m + len, paint);

        canvas.drawLine(m, h - m, m + len, h - m, paint);
        canvas.drawLine(m, h - m, m, h - m - len, paint);

        canvas.drawLine(w - m, h - m, w - m - len, h - m, paint);
        canvas.drawLine(w - m, h - m, w - m, h - m - len, paint);

        // Futuristic labels
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(180, 0, 255, 255));
        paint.setTextSize(Math.max(12f, w * 0.025f));
        paint.setTypeface(android.graphics.Typeface.MONOSPACE);

        canvas.drawText("ULTRON // CORE", w * 0.08f, h * 0.10f, paint);
        canvas.drawText("VISION LINK", w * 0.08f, h * 0.87f, paint);
        canvas.drawText("AI CORE", w * 0.08f, h * 0.91f, paint);
        canvas.drawText("SYSTEM ONLINE", w * 0.08f, h * 0.95f, paint);

        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                .format(new Date());

        paint.setTextSize(Math.max(13f, w * 0.028f));
        canvas.drawText(time, w * 0.67f, h * 0.10f, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.argb(60, 0, 255, 255));
        paint.setStrokeWidth(1f);

        canvas.drawRect(
                w * 0.08f,
                h * 0.84f,
                w * 0.55f,
                h * 0.965f,
                paint
        );

        paint.setShader(new LinearGradient(
                0, 0, w, 0,
                Color.TRANSPARENT,
                Color.argb(80, 0, 255, 255),
                Shader.TileMode.CLAMP
        ));
        canvas.drawRect(0, 0, w, 3, paint);
        paint.setShader(null);

        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(android.graphics.ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return android.graphics.PixelFormat.TRANSLUCENT;
    }

    @Override
    protected void onBoundsChange(android.graphics.Rect bounds) {
        super.onBoundsChange(bounds);
    }
}
