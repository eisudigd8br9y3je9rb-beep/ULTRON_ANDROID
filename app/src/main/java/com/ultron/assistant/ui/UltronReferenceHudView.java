package com.ultron.assistant.ui;

import android.content.Context;
import android.graphics.*;
import android.view.View;

public class UltronReferenceHudView extends View {

    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float rotation = 0f;
    private long last = 0;

    private final int BG = Color.rgb(3, 10, 16);
    private final int ORANGE = Color.rgb(255, 125, 25);
    private final int GOLD = Color.rgb(245, 190, 125);
    private final int WHITE = Color.rgb(220, 215, 205);
    private final int CYAN = Color.rgb(35, 150, 180);
    private final int PANEL = Color.argb(150, 5, 12, 18);

    public UltronReferenceHudView(Context context) {
        super(context);
        p.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private float d(float v) {
        return v * getResources().getDisplayMetrics().density;
    }

    private void text(Canvas c, String s, float x, float y, float size, int color,
                      Paint.Align align) {
        p.setStyle(Paint.Style.FILL);
        p.setColor(color);
        p.setTextSize(d(size));
        p.setTextAlign(align);
        p.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
        c.drawText(s, x, y, p);
    }

    private void line(Canvas c, float x1, float y1, float x2, float y2,
                      float w, int color) {
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(d(w));
        p.setColor(color);
        p.setShadowLayer(d(5), 0, 0, Color.argb(130, color == ORANGE ? 255 : 20,
                color == ORANGE ? 100 : 180, 20));
        c.drawLine(x1, y1, x2, y2, p);
        p.clearShadowLayer();
    }

    private void panel(Canvas c, float l, float t, float r, float b) {
        p.setStyle(Paint.Style.FILL);
        p.setColor(PANEL);
        c.drawRect(l, t, r, b, p);

        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(d(1));
        p.setColor(Color.argb(210, 205, 105, 35));
        c.drawRect(l, t, r, b, p);

        line(c, l + d(10), t + d(2), r - d(35), t + d(2), 1, ORANGE);
    }

    private void circleButton(Canvas c, float x, float y, float r, String label) {
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.argb(150, 8, 13, 18));
        c.drawCircle(x, y, r, p);

        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(d(2));
        p.setColor(ORANGE);
        p.setShadowLayer(d(8), 0, 0, Color.argb(220, 255, 100, 15));
        c.drawCircle(x, y, r, p);
        p.clearShadowLayer();

        p.setStrokeWidth(d(1));
        p.setColor(GOLD);
        c.drawCircle(x, y, r * .78f, p);

        text(c, label, x, y + r + d(17), 7, WHITE, Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);

        float w = getWidth();
        float h = getHeight();

        p.setStyle(Paint.Style.FILL);
        p.setColor(BG);
        c.drawRect(0, 0, w, h, p);

        // futuristic outer frame
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(d(2));
        p.setColor(Color.rgb(75, 78, 76));
        c.drawRect(d(7), d(7), w - d(7), h - d(7), p);

        p.setStrokeWidth(d(1));
        p.setColor(ORANGE);
        c.drawRect(d(14), d(14), w - d(14), h - d(14), p);

        // header
        text(c, "△  ULTRON", w / 2, d(38), 25, GOLD, Paint.Align.CENTER);
        text(c, "PERSONAL AI ASSISTANT", w / 2, d(59), 8, WHITE, Paint.Align.CENTER);

        line(c, d(310), d(68), d(580), d(68), 1, ORANGE);
        line(c, w - d(580), d(68), w - d(310), d(68), 1, ORANGE);

        // top system indicators
        text(c, "▣  78%", w - d(280), d(39), 8, WHITE, Paint.Align.CENTER);
        text(c, "☼  70%", w - d(205), d(39), 8, WHITE, Paint.Align.CENTER);
        text(c, "▥  4G", w - d(130), d(39), 8, WHITE, Paint.Align.CENTER);
        text(c, "◷  10:24 PM", w - d(55), d(39), 7, WHITE, Paint.Align.CENTER);

        // left command panel
        panel(c, d(28), d(82), d(255), h - d(260));
        text(c, "COMMAND", d(48), d(105), 9, ORANGE, Paint.Align.LEFT);

        String[] nav = {"⌂   HOME", "●   CHAT", "✦   CREATE", "▥   ANALYSE",
                "⚙   SETTINGS", "?   HELP"};
        float ny = d(135);

        for (String s : nav) {
            panel(c, d(40), ny - d(25), d(245), ny + d(8));
            text(c, s, d(62), ny - d(3), 9, WHITE, Paint.Align.LEFT);
            ny += d(42);
        }

        // left briefing
        panel(c, d(28), h - d(245), d(420), h - d(92));
        text(c, "DAILY BRIEFING", d(50), h - d(220), 9, ORANGE, Paint.Align.LEFT);
        text(c, "◉", d(105), h - d(155), 38, CYAN, Paint.Align.CENTER);
        text(c, "24°C", d(190), h - d(166), 13, WHITE, Paint.Align.LEFT);
        text(c, "Partly Cloudy", d(190), h - d(149), 7, WHITE, Paint.Align.LEFT);
        text(c, "78%  HUMIDITY", d(190), h - d(125), 8, GOLD, Paint.Align.LEFT);
        line(c, d(48), h - d(112), d(395), h - d(112), 1, CYAN);

        // right status
        panel(c, w - d(285), d(115), w - d(30), d(245));
        text(c, "ULTRON", w - d(155), d(150), 15, WHITE, Paint.Align.CENTER);
        text(c, "ONLINE", w - d(155), d(174), 12, Color.rgb(0, 235, 100), Paint.Align.CENTER);
        text(c, "“Ready, Imtiyaz.”", w - d(155), d(210), 8, CYAN, Paint.Align.CENTER);

        panel(c, w - d(285), d(265), w - d(30), d(425));
        text(c, "SYSTEM STATUS", w - d(260), d(290), 9, ORANGE, Paint.Align.LEFT);
        text(c, "78%", w - d(210), d(345), 18, WHITE, Paint.Align.CENTER);
        text(c, "Battery       78%", w - d(255), d(320), 7, WHITE, Paint.Align.LEFT);
        text(c, "Brightness    70%", w - d(255), d(350), 7, WHITE, Paint.Align.LEFT);
        text(c, "Network       4G", w - d(255), d(380), 7, WHITE, Paint.Align.LEFT);
        text(c, "Time          10:24 PM", w - d(255), d(410), 7, WHITE, Paint.Align.LEFT);

        panel(c, w - d(395), h - d(245), w - d(30), h - d(92));
        text(c, "INSIGHTS", w - d(365), h - d(220), 9, ORANGE, Paint.Align.LEFT);
        text(c, "Generated 5 insights", w - d(365), h - d(185), 7, WHITE, Paint.Align.LEFT);
        text(c, "Summarized 3 key points", w - d(365), h - d(158), 7, WHITE, Paint.Align.LEFT);
        text(c, "Found 2 improvements", w - d(365), h - d(131), 7, WHITE, Paint.Align.LEFT);

        // central reactor
        float cx = w / 2;
        float cy = h * .49f;
        float r = Math.min(w, h) * .22f;

        long now = System.currentTimeMillis();
        if (last == 0) last = now;
        rotation += (now - last) * .035f;
        last = now;

        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.argb(120, 255, 90, 10));
        p.setShadowLayer(d(35), 0, 0, Color.argb(210, 255, 100, 15));
        c.drawCircle(cx, cy, r * .63f, p);
        p.clearShadowLayer();

        for (int i = 0; i < 5; i++) {
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(d(i == 2 ? 5 : 2));
            p.setColor(i % 2 == 0 ? ORANGE : Color.rgb(180, 180, 175));
            RectF q = new RectF(cx - r * (1f - i * .13f),
                    cy - r * (1f - i * .13f),
                    cx + r * (1f - i * .13f),
                    cy + r * (1f - i * .13f));
            c.save();
            c.rotate(rotation * (i % 2 == 0 ? 1 : -1), cx, cy);
            c.drawArc(q, 5, 55, false, p);
            c.drawArc(q, 95, 65, false, p);
            c.drawArc(q, 210, 75, false, p);
            c.drawArc(q, 315, 35, false, p);
            c.restore();
        }

        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.rgb(12, 20, 27));
        p.setShadowLayer(d(25), 0, 0, Color.argb(230, 255, 100, 15));
        c.drawCircle(cx, cy, r * .52f, p);
        p.clearShadowLayer();

        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(d(3));
        p.setColor(ORANGE);
        c.drawCircle(cx, cy, r * .52f, p);

        text(c, "ULTRON", cx, cy - d(2), 13, WHITE, Paint.Align.CENTER);
        text(c, "AI CORE", cx, cy + d(18), 8, GOLD, Paint.Align.CENTER);

        // surrounding actions
        circleButton(c, cx - r * 1.25f, cy - r * .92f, d(31), "VOICE");
        circleButton(c, cx - r * 1.25f, cy - r * .25f, d(31), "GENERATE");
        circleButton(c, cx - r * 1.25f, cy + r * .45f, d(31), "SUMMARIZE");
        circleButton(c, cx - r * .78f, cy + r * .98f, d(31), "CHAT");

        circleButton(c, cx + r * 1.25f, cy - r * .92f, d(31), "TRANSLATE");
        circleButton(c, cx + r * 1.25f, cy - r * .25f, d(31), "CODE");
        circleButton(c, cx + r * 1.25f, cy + r * .45f, d(31), "ANALYSE FILE");
        circleButton(c, cx + r * .78f, cy + r * .98f, d(31), "OPTIMIZE");

        circleButton(c, cx, cy - r * 1.62f, d(42), "MIC");
        text(c, "NEW CONVERSATION", cx, cy - r * 1.27f, 9, WHITE, Paint.Align.CENTER);

        // bottom command bar
        panel(c, d(420), h - d(87), w - d(420), h - d(45));
        text(c, "◎  STANDARD", d(445), h - d(61), 8, WHITE, Paint.Align.LEFT);
        text(c, "Ask anything...", d(585), h - d(61), 9,
                Color.rgb(135, 130, 125), Paint.Align.LEFT);

        text(c, "◉   ♪   ▣   </>   ▤   ⚙", d(485), h - d(17), 13, GOLD, Paint.Align.LEFT);

        postInvalidateOnAnimation();
    }
}
