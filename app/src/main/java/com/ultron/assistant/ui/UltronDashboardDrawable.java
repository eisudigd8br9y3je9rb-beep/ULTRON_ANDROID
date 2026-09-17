
package com.ultron.assistant.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class UltronDashboardDrawable extends Drawable {

    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();

    private float d(float v) {
        return v * getBounds().width() / 1000f;
    }

    @Override
    public void draw(Canvas c) {
        float w = getBounds().width();
        float h = getBounds().height();

        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.rgb(7, 8, 9));
        c.drawRect(0, 0, w, h, p);

        // Gunmetal layered background
        p.setColor(Color.rgb(14, 14, 14));
        c.drawRect(d(1), d(1), w-d(1), h-d(1), p);

        // Fine technical grid
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(d(.45f));
        p.setColor(Color.argb(32, 210, 145, 70));

        for (float x = 20; x < 1000; x += 32) {
            c.drawLine(d(x), 0, d(x), h, p);
        }

        for (float y = 18; y < 1000; y += 32) {
            c.drawLine(0, d(y), w, d(y), p);
        }

        // Hex-like technical marks
        p.setStrokeWidth(d(.8f));
        p.setColor(Color.argb(48, 220, 150, 75));

        for (int row = 0; row < 12; row++) {
            for (int col = 0; col < 24; col++) {
                float x = d(10 + col * 43 + (row % 2) * 21);
                float y = d(20 + row * 43);

                path.reset();
                path.moveTo(x + d(7), y);
                path.lineTo(x + d(14), y + d(4));
                path.lineTo(x + d(14), y + d(11));
                path.lineTo(x + d(7), y + d(15));
                path.lineTo(x, y + d(11));
                path.lineTo(x, y + d(4));
                path.close();
                c.drawPath(path, p);
            }
        }

        // Outer mechanical frame
        p.setStrokeWidth(d(2));
        p.setColor(Color.rgb(76, 61, 47));
        c.drawRect(d(5), d(5), w-d(5), h-d(5), p);

        p.setStrokeWidth(d(1));
        p.setColor(Color.rgb(178, 102, 37));
        c.drawRect(d(10), d(10), w-d(10), h-d(10), p);

        // Corner brackets
        p.setStrokeWidth(d(2));
        p.setColor(Color.rgb(225, 128, 42));

        float[][] corners = {
                {12,12,65,12,12,65},
                {988,12,935,12,988,65},
                {12,988,65,988,12,935},
                {988,988,935,988,988,935}
        };

        for (float[] q : corners) {
            c.drawLine(d(q[0]), d(q[1]), d(q[2]), d(q[3]), p);
            c.drawLine(d(q[0]), d(q[1]), d(q[4]), d(q[5]), p);
        }

        // Header mechanical rails
        p.setStrokeWidth(d(1.4f));
        p.setColor(Color.argb(190, 220, 125, 40));

        c.drawLine(d(180), d(72), d(395), d(72), p);
        c.drawLine(d(605), d(72), d(820), d(72), p);

        c.drawLine(d(245), d(78), d(380), d(78), p);
        c.drawLine(d(620), d(78), d(755), d(78), p);

        // Circuit traces
        p.setStrokeWidth(d(1));
        p.setColor(Color.argb(90, 220, 120, 35));

        for (int i = 0; i < 7; i++) {
            float y = d(180 + i * 88);
            c.drawLine(d(300), y, d(355), y, p);
            c.drawLine(d(645), y, d(700), y, p);
            c.drawCircle(d(300), y, d(2), p);
            c.drawCircle(d(700), y, d(2), p);
        }

        // Bottom command rail
        p.setColor(Color.argb(180, 190, 105, 35));
        c.drawLine(d(275), h-d(74), d(725), h-d(74), p);
        c.drawLine(d(300), h-d(68), d(700), h-d(68), p);
    }

    @Override public void setAlpha(int alpha) {}
    @Override public void setColorFilter(android.graphics.ColorFilter f) {}
    @Override public int getOpacity() {
        return android.graphics.PixelFormat.OPAQUE;
    }
}
