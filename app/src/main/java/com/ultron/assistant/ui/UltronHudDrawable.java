package com.ultron.assistant.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UltronHudDrawable extends Drawable {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();

    private float angle = 0f;
    private long lastTime = SystemClock.uptimeMillis();

    private final Runnable animator = new Runnable() {
        @Override
        public void run() {
            long now = SystemClock.uptimeMillis();
            float delta = (now - lastTime) / 1000f;
            lastTime = now;

            angle += delta * 22f;
            if (angle >= 360f) angle -= 360f;

            invalidateSelf();
            scheduleSelf(this, now + 40);
        }
    };

    public UltronHudDrawable() {
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setTypeface(Typeface.MONOSPACE);
        scheduleSelf(animator, SystemClock.uptimeMillis() + 40);
    }

    private void stroke(Canvas c, int color, float width) {
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(width);
    }

    private void fill(Canvas c, int color) {
        paint.setShader(null);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
    }

    private void text(Canvas c, String value, float x, float y,
                      float size, int color) {
        fill(c, color);
        paint.setTextSize(size);
        paint.setTypeface(Typeface.MONOSPACE);
        c.drawText(value, x, y, paint);
    }

    @Override
    public void draw(Canvas canvas) {
        float w = getBounds().width();
        float h = getBounds().height();

        if (w <= 0 || h <= 0) return;

        // =========================================================
        // ULTRON HUD V2 — dark holographic foundation
        // =========================================================
        canvas.drawColor(Color.rgb(1, 6, 11));

        // Soft central cyan atmosphere
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new RadialGradient(
                w * 0.67f,
                h * 0.43f,
                Math.max(w, h) * 0.72f,
                new int[]{
                        Color.argb(34, 0, 255, 255),
                        Color.argb(14, 0, 170, 220),
                        Color.TRANSPARENT
                },
                null,
                Shader.TileMode.CLAMP
        ));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);

        // =========================================================
        // TECH GRID
        // =========================================================
        stroke(canvas, Color.argb(22, 0, 235, 255), 1f);

        float grid = Math.max(34f, w * 0.075f);

        for (float x = 0; x <= w; x += grid) {
            canvas.drawLine(x, 0, x, h, paint);
        }

        for (float y = 0; y <= h; y += grid) {
            canvas.drawLine(0, y, w, y, paint);
        }

        // Larger cross-grid accents
        stroke(canvas, Color.argb(38, 0, 255, 255), 1.5f);

        for (float x = 0; x <= w; x += grid * 4f) {
            canvas.drawLine(x, 0, x, h, paint);
        }

        for (float y = 0; y <= h; y += grid * 4f) {
            canvas.drawLine(0, y, w, y, paint);
        }

        // =========================================================
        // TOP STATUS BAR
        // =========================================================
        float margin = Math.max(16f, w * 0.045f);

        stroke(canvas, Color.argb(120, 0, 240, 255), 1.5f);
        canvas.drawLine(margin, h * 0.075f, w - margin, h * 0.075f, paint);

        fill(canvas, Color.rgb(0, 245, 255));
        canvas.drawCircle(margin + 5f, h * 0.075f, 3.5f, paint);

        text(canvas, "ULTRON // CORE", margin + 18f, h * 0.064f,
                Math.max(12f, w * 0.024f),
                Color.argb(225, 0, 255, 255));

        text(canvas, "ONLINE", w * 0.42f, h * 0.064f,
                Math.max(10f, w * 0.019f),
                Color.argb(170, 0, 255, 220));

        String time = new SimpleDateFormat(
                "HH:mm:ss", Locale.getDefault()
        ).format(new Date());

        text(canvas, time, w * 0.78f, h * 0.064f,
                Math.max(11f, w * 0.021f),
                Color.argb(190, 150, 245, 255));

        // =========================================================
        // MAIN ARC REACTOR / AI CORE
        // =========================================================
        float cx = w * 0.70f;
        float cy = h * 0.38f;
        float base = Math.min(w, h) * 0.235f;

        // Outer glow
        stroke(canvas, Color.argb(38, 0, 255, 255), 9f);
        canvas.drawCircle(cx, cy, base * 1.12f, paint);

        // Outer segmented ring
        stroke(canvas, Color.argb(145, 0, 235, 255), 2f);
        canvas.drawCircle(cx, cy, base * 1.02f, paint);

        stroke(canvas, Color.argb(90, 0, 190, 230), 1f);
        canvas.drawCircle(cx, cy, base * 0.94f, paint);

        // Rotating segmented arcs
        stroke(canvas, Color.rgb(0, 245, 255), 5f);
        canvas.drawArc(
                cx - base,
                cy - base,
                cx + base,
                cy + base,
                angle,
                72f,
                false,
                paint
        );

        canvas.drawArc(
                cx - base,
                cy - base,
                cx + base,
                cy + base,
                angle + 155f,
                38f,
                false,
                paint
        );

        stroke(canvas, Color.argb(180, 0, 220, 255), 2.5f);
        canvas.drawArc(
                cx - base * 0.84f,
                cy - base * 0.84f,
                cx + base * 0.84f,
                cy + base * 0.84f,
                -angle * 1.45f,
                100f,
                false,
                paint
        );

        // Inner rings
        stroke(canvas, Color.argb(110, 0, 255, 255), 2f);
        canvas.drawCircle(cx, cy, base * 0.69f, paint);
        canvas.drawCircle(cx, cy, base * 0.47f, paint);

        // Core glow
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new RadialGradient(
                cx, cy, base * 0.48f,
                new int[]{
                        Color.argb(105, 0, 255, 255),
                        Color.argb(35, 0, 210, 240),
                        Color.TRANSPARENT
                },
                null,
                Shader.TileMode.CLAMP
        ));
        canvas.drawCircle(cx, cy, base * 0.48f, paint);
        paint.setShader(null);

        // Core
        fill(canvas, Color.argb(215, 0, 245, 255));
        canvas.drawCircle(cx, cy, base * 0.105f, paint);

        fill(canvas, Color.WHITE);
        canvas.drawCircle(cx, cy, base * 0.035f, paint);

        // Core crosshair
        stroke(canvas, Color.argb(190, 0, 255, 255), 1.5f);
        canvas.drawLine(cx - base * 0.58f, cy,
                cx - base * 0.23f, cy, paint);
        canvas.drawLine(cx + base * 0.23f, cy,
                cx + base * 0.58f, cy, paint);
        canvas.drawLine(cx, cy - base * 0.58f,
                cx, cy - base * 0.23f, paint);
        canvas.drawLine(cx, cy + base * 0.23f,
                cx, cy + base * 0.58f, paint);

        // Targeting ticks
        stroke(canvas, Color.argb(175, 0, 255, 255), 2f);

        for (int i = 0; i < 12; i++) {
            double a = Math.toRadians(i * 30.0 + angle * 0.18);
            float r1 = base * 1.10f;
            float r2 = (i % 3 == 0) ? base * 1.19f : base * 1.14f;

            float x1 = cx + (float) Math.cos(a) * r1;
            float y1 = cy + (float) Math.sin(a) * r1;
            float x2 = cx + (float) Math.cos(a) * r2;
            float y2 = cy + (float) Math.sin(a) * r2;

            canvas.drawLine(x1, y1, x2, y2, paint);
        }

        // =========================================================
        // LEFT AI DATA RAIL
        // =========================================================
        float railX = w * 0.065f;
        float railTop = h * 0.19f;
        float railBottom = h * 0.76f;

        stroke(canvas, Color.argb(155, 0, 240, 255), 2f);
        canvas.drawLine(railX, railTop, railX, railBottom, paint);

        stroke(canvas, Color.argb(55, 0, 220, 255), 1f);
        canvas.drawLine(railX + 10f, railTop,
                railX + 10f, railBottom - h * 0.07f, paint);

        text(canvas, "AI", railX - 7f, railTop - 12f,
                Math.max(11f, w * 0.022f),
                Color.argb(210, 0, 255, 255));

        // Data blocks
        for (int i = 0; i < 7; i++) {
            float yy = railTop + 42f + i * h * 0.068f;
            float width = (float) (16 + ((i * 31) % 42));

            stroke(canvas, Color.argb(95, 0, 225, 255), 2f);
            canvas.drawLine(railX + 18f, yy,
                    railX + 18f + width, yy, paint);

            fill(canvas, Color.argb(150, 0, 255, 255));
            canvas.drawCircle(railX + 4f, yy, 2.5f, paint);
        }

        // =========================================================
        // VISION / CAMERA PANEL
        // =========================================================
        float panelL = w * 0.075f;
        float panelT = h * 0.80f;
        float panelR = w * 0.53f;
        float panelB = h * 0.94f;

        stroke(canvas, Color.argb(110, 0, 240, 255), 1.5f);
        canvas.drawRect(panelL, panelT, panelR, panelB, paint);

        // Corner cuts
        stroke(canvas, Color.argb(185, 0, 255, 255), 2f);

        float cut = 12f;

        canvas.drawLine(panelL, panelT, panelL + cut, panelT, paint);
        canvas.drawLine(panelL, panelT, panelL, panelT + cut, paint);

        canvas.drawLine(panelR - cut, panelB, panelR, panelB, paint);
        canvas.drawLine(panelR, panelB - cut, panelR, panelB, paint);

        text(canvas, "VISION LINK", panelL + 12f, panelT + 23f,
                Math.max(11f, w * 0.021f),
                Color.argb(220, 0, 255, 255));

        text(canvas, "CAMERA / LIVE", panelL + 12f, panelT + 47f,
                Math.max(9f, w * 0.017f),
                Color.argb(155, 160, 240, 255));

        // Live indicator
        fill(canvas, Color.argb(230, 0, 255, 220));
        canvas.drawCircle(panelR - 18f, panelT + 19f, 4f, paint);

        // Mini signal bars
        stroke(canvas, Color.argb(150, 0, 255, 255), 2f);

        for (int i = 0; i < 5; i++) {
            float x = panelL + 13f + i * 13f;
            float top = panelB - 11f - i * 3f;
            canvas.drawLine(x, panelB - 11f, x, top, paint);
        }

        // =========================================================
        // BOTTOM-RIGHT SYSTEM RING
        // =========================================================
        float rx = w * 0.82f;
        float ry = h * 0.79f;
        float rr = Math.min(w, h) * 0.125f;

        stroke(canvas, Color.argb(95, 0, 240, 255), 2f);
        canvas.drawCircle(rx, ry, rr, paint);
        canvas.drawCircle(rx, ry, rr * 0.72f, paint);

        stroke(canvas, Color.rgb(0, 235, 255), 4f);
        canvas.drawArc(
                rx - rr,
                ry - rr,
                rx + rr,
                ry + rr,
                -angle,
                88f,
                false,
                paint
        );

        stroke(canvas, Color.argb(125, 0, 220, 255), 2f);
        canvas.drawArc(
                rx - rr * 0.72f,
                ry - rr * 0.72f,
                rx + rr * 0.72f,
                ry + rr * 0.72f,
                angle * 1.4f,
                135f,
                false,
                paint
        );

        text(canvas, "SYS", rx - 13f, ry + 4f,
                Math.max(10f, w * 0.019f),
                Color.argb(205, 0, 255, 255));

        // =========================================================
        // SCANNING BEAM
        // =========================================================
        float scanY = (SystemClock.uptimeMillis() % Math.max(1L, (long) (h * 5))) / 5f;

        if (scanY <= h) {
            paint.setShader(new LinearGradient(
                    0, scany- 18f,
                    0, scanY + 18f,
                    Color.TRANSPARENT,
                    Color.argb(48, 0, 255, 255),
                    Shader.TileMode.CLAMP
            ));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2f);
            canvas.drawLine(0, scanY, w, scanY, paint);
            paint.setShader(null);
        }

        // =========================================================
        // CORNER BRACKETS
        // =========================================================
        stroke(canvas, Color.argb(155, 0, 240, 255), 2f);

        float cm = Math.max(14f, w * 0.035f);
        float cl = Math.max(28f, w * 0.085f);

        canvas.drawLine(cm, cm, cm + cl, cm, paint);
        canvas.drawLine(cm, cm, cm, cm + cl, paint);

        canvas.drawLine(w - cm, cm, w - cm - cl, cm, paint);
        canvas.drawLine(w - cm, cm, w - cm, cm + cl, paint);

        canvas.drawLine(cm, h - cm, cm + cl, h - cm, paint);
        canvas.drawLine(cm, h - cm, cm, h - cm - cl, paint);

        canvas.drawLine(w - cm, h - cm, w - cm - cl, h - cm, paint);
        canvas.drawLine(w - cm, h - cm, w - cm, h - cm - cl, paint);

        // =========================================================
        // FOOTER STATUS
        // =========================================================
        text(canvas, "AI CORE", w * 0.075f, h * 0.972f,
                Math.max(9f, w * 0.018f),
                Color.argb(155, 0, 235, 255));

        text(canvas, "SYSTEM ONLINE", w * 0.25f, h * 0.972f,
                Math.max(9f, w * 0.018f),
                Color.argb(175, 0, 255, 220));

        text(canvas, "v2 // HUD", w * 0.78f, h * 0.972f,
                Math.max(9f, w * 0.018f),
                Color.argb(120, 130, 225, 255));

        // Top edge energy line
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
        paint.setShader(new LinearGradient(
                0, 0, w, 0,
                new int[]{
                        Color.TRANSPARENT,
                        Color.argb(80, 0, 255, 255),
                        Color.rgb(0, 255, 255),
                        Color.argb(80, 0, 255, 255),
                        Color.TRANSPARENT
                },
                null,
                Shader.TileMode.CLAMP
        ));
        canvas.drawLine(0, 2f, w, 2f, paint);
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
