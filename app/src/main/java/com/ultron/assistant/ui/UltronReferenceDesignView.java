package com.ultron.assistant.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

public class UltronReferenceDesignView extends View {

    public interface ActionListener {
        void onAction(String action);
    }

    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private ActionListener actionListener;

    private float sx = 1f;
    private float sy = 1f;
    private float pulse = 0f;

    private static final int BG = Color.rgb(3, 7, 11);
    private static final int PANEL = Color.rgb(7, 12, 17);
    private static final int PANEL2 = Color.rgb(10, 16, 22);
    private static final int ORANGE = Color.rgb(255, 125, 25);
    private static final int GOLD = Color.rgb(255, 190, 110);
    private static final int BRONZE = Color.rgb(125, 72, 30);
    private static final int TEXT = Color.rgb(225, 216, 205);
    private static final int MUTED = Color.rgb(145, 140, 132);
    private static final int BLUE = Color.rgb(35, 150, 210);
    private static final int GREEN = Color.rgb(0, 235, 145);

    public UltronReferenceDesignView(Context context) {
        super(context);

        p.setTypeface(android.graphics.Typeface.create(
                android.graphics.Typeface.SANS_SERIF,
                android.graphics.Typeface.NORMAL
        ));

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setClickable(true);
    }

    public void setActionListener(ActionListener listener) {
        this.actionListener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        sx = w / 1536f;
        sy = h / 1025f;
    }

    private float X(float v) {
        return v * sx;
    }

    private float Y(float v) {
        return v * sy;
    }

    private void fill(int color) {
        p.setStyle(Paint.Style.FILL);
        p.setColor(color);
        p.clearShadowLayer();
    }

    private void stroke(float width, int color) {
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(X(width));
        p.setColor(color);
        p.clearShadowLayer();
    }

    private void glow(int color, float radius) {
        p.setShadowLayer(X(radius), 0, 0, color);
    }

    private void txt(Canvas c, String s, float x, float y,
                     float size, int color, Paint.Align align) {
        fill(color);
        p.setTextSize(X(size));
        p.setTextAlign(align);
        p.setTypeface(android.graphics.Typeface.create(
                android.graphics.Typeface.SANS_SERIF,
                android.graphics.Typeface.NORMAL
        ));
        c.drawText(s, X(x), Y(y), p);
    }

    private void line(Canvas c, float x1, float y1, float x2, float y2) {
        c.drawLine(X(x1), Y(y1), X(x2), Y(y2), p);
    }

    private void panel(Canvas c, float l, float t, float r, float b) {
        fill(PANEL);
        c.drawRoundRect(
                new RectF(X(l), Y(t), X(r), Y(b)),
                X(10), Y(10), p
        );

        stroke(1.5f, BRONZE);
        c.drawRoundRect(
                new RectF(X(l), Y(t), X(r), Y(b)),
                X(10), Y(10), p
        );

        stroke(1f, Color.argb(90, 255, 130, 30));
        line(c, l + 18, t + 2, r - 25, t + 2);
    }

    private void circleButton(Canvas c, float cx, float cy,
                              float radius, String label) {
        glow(ORANGE, 8);
        fill(PANEL2);
        c.drawCircle(X(cx), Y(cy), X(radius), p);

        p.clearShadowLayer();
        stroke(1.8f, ORANGE);
        c.drawCircle(X(cx), Y(cy), X(radius), p);

        txt(c, "•", cx, cy + 8, 28, GOLD, Paint.Align.CENTER);
        txt(c, label, cx, cy + radius + 28, 14, TEXT, Paint.Align.CENTER);
    }

    private void navButton(Canvas c, float y, String label, String action) {
        boolean active = "HOME".equals(action);

        if (active) {
            glow(ORANGE, 7);
        }

        fill(active ? Color.rgb(18, 20, 22) : PANEL);
        c.drawRoundRect(
                new RectF(X(48), Y(y), X(280), Y(y + 76)),
                X(12), Y(12), p
        );

        p.clearShadowLayer();
        stroke(1.5f, active ? ORANGE : BRONZE);
        c.drawRoundRect(
                new RectF(X(48), Y(y), X(280), Y(y + 76)),
                X(12), Y(12), p
        );

        fill(Color.rgb(13, 18, 23));
        c.drawCircle(X(100), Y(y + 38), X(27), p);

        stroke(1.5f, ORANGE);
        c.drawCircle(X(100), Y(y + 38), X(27), p);

        txt(c, "•", 100, y + 46, 24, GOLD, Paint.Align.CENTER);
        txt(c, label, 145, y + 45, 18, TEXT, Paint.Align.LEFT);
    }

    private void core(Canvas c, float cx, float cy, float radius) {
        float a = pulse;

        // Outer glow
        glow(ORANGE, 25);
        fill(Color.argb(25, 255, 125, 25));
        c.drawCircle(X(cx), Y(cy), X(radius), p);
        p.clearShadowLayer();

        // Concentric reactor rings
        for (int i = 0; i < 7; i++) {
            float rr = radius - 8 - (i * 20);

            stroke(i == 0 ? 3f : 1.3f,
                    i % 2 == 0 ? ORANGE : BRONZE);

            c.drawCircle(X(cx), Y(cy), X(rr), p);
        }

        // Blue inner technology ring
        stroke(2f, BLUE);
        c.drawCircle(X(cx), Y(cy), X(radius - 93), p);
        c.drawCircle(X(cx), Y(cy), X(radius - 115), p);

        // Reactor ticks
        stroke(2f, ORANGE);
        for (int i = 0; i < 24; i++) {
            double ang = Math.toRadians(i * 15 + a * 0.3);
            float r1 = radius - 35;
            float r2 = radius - (i % 2 == 0 ? 18 : 26);

            float x1 = cx + (float)Math.cos(ang) * r1;
            float y1 = cy + (float)Math.sin(ang) * r1;
            float x2 = cx + (float)Math.cos(ang) * r2;
            float y2 = cy + (float)Math.sin(ang) * r2;

            line(c, x1, y1, x2, y2);
        }

        // Core
        glow(ORANGE, 18);
        fill(Color.rgb(25, 16, 10));
        c.drawCircle(X(cx), Y(cy), X(radius - 130), p);
        p.clearShadowLayer();

        stroke(2f, ORANGE);
        c.drawCircle(X(cx), Y(cy), X(radius - 130), p);

        // Energy points
        for (int i = 0; i < 12; i++) {
            double ang = Math.toRadians(i * 30 + a);
            float rr = radius - 160;

            float px = cx + (float)Math.cos(ang) * rr;
            float py = cy + (float)Math.sin(ang) * rr;

            fill(i % 3 == 0 ? BLUE : ORANGE);
            c.drawCircle(X(px), Y(py), X(4), p);
        }

        txt(c, "ULTRON", cx, cy - 5, 30, GOLD, Paint.Align.CENTER);
        txt(c, "AI CORE", cx, cy + 27, 19, TEXT, Paint.Align.CENTER);

        stroke(1f, Color.argb(100, 255, 125, 25));
        c.drawCircle(X(cx), Y(cy), X(radius - 150), p);
    }

    private void drawHeader(Canvas c) {
        txt(c, "△", 575, 73, 42, GOLD, Paint.Align.CENTER);
        txt(c, "ULTRON", 768, 70, 42, TEXT, Paint.Align.CENTER);
        txt(c, "PERSONAL AI ASSISTANT", 768, 101, 18, GOLD, Paint.Align.CENTER);

        stroke(2f, ORANGE);
        line(c, 475, 110, 660, 110);
        line(c, 875, 110, 1060, 110);

        stroke(1f, BLUE);
        line(c, 310, 155, 515, 155);
        line(c, 1020, 155, 1225, 155);
    }

    private void drawRightStatus(Canvas c) {
        panel(c, 1220, 185, 1490, 350);

        txt(c, "ULTRON", 1370, 235, 25, TEXT, Paint.Align.CENTER);
        txt(c, "ONLINE", 1370, 268, 20, GREEN, Paint.Align.CENTER);
        txt(c, "\"Ready, Imtiyaz.\"", 1370, 310, 17, BLUE, Paint.Align.CENTER);

        panel(c, 1220, 375, 1490, 585);
        txt(c, "⚡  SYSTEM STATUS", 1245, 408, 16, TEXT, Paint.Align.LEFT);

        txt(c, "◉", 1270, 472, 30, ORANGE, Paint.Align.CENTER);
        txt(c, "78%", 1270, 515, 22, TEXT, Paint.Align.CENTER);

        txt(c, "Battery", 1320, 458, 14, TEXT, Paint.Align.LEFT);
        txt(c, "78%", 1460, 458, 14, GOLD, Paint.Align.RIGHT);

        txt(c, "Brightness", 1320, 492, 14, TEXT, Paint.Align.LEFT);
        txt(c, "--%", 1460, 492, 14, GOLD, Paint.Align.RIGHT);

        txt(c, "Network", 1320, 526, 14, TEXT, Paint.Align.LEFT);
        txt(c, "4G", 1460, 526, 14, GOLD, Paint.Align.RIGHT);

        txt(c, "Time", 1320, 558, 14, TEXT, Paint.Align.LEFT);
        txt(c, "--:--", 1460, 558, 14, GOLD, Paint.Align.RIGHT);

        panel(c, 1135, 610, 1490, 805);
        txt(c, "⚡  INSIGHTS", 1160, 643, 16, TEXT, Paint.Align.LEFT);

        txt(c, "◆", 1170, 690, 18, BLUE, Paint.Align.CENTER);
        txt(c, "AI modules ready", 1200, 692, 14, TEXT, Paint.Align.LEFT);

        txt(c, "◆", 1170, 735, 18, BLUE, Paint.Align.CENTER);
        txt(c, "Voice system available", 1200, 737, 14, TEXT, Paint.Align.LEFT);

        txt(c, "◆", 1170, 780, 18, BLUE, Paint.Align.CENTER);
        txt(c, "Local vision standby", 1200, 782, 14, TEXT, Paint.Align.LEFT);
    }

    private void drawBottom(Canvas c) {
        panel(c, 450, 835, 1085, 900);

        txt(c, "◉", 478, 875, 20, GOLD, Paint.Align.CENTER);
        txt(c, "STANDARD", 550, 875, 15, TEXT, Paint.Align.CENTER);

        stroke(1f, BRONZE);
        line(c, 615, 850, 615, 885);

        txt(c, "Ask anything...", 650, 875, 16,
                Color.rgb(150, 145, 138), Paint.Align.LEFT);

        txt(c, "➤", 1045, 877, 23, GOLD, Paint.Align.CENTER);

        String[] controls = {
                "MIC", "MUSIC", "IMAGE", "CODE", "FILE", "SETTINGS"
        };

        for (int i = 0; i < controls.length; i++) {
            float x = 500 + (i * 82);

            glow(ORANGE, 5);
            fill(PANEL2);
            c.drawCircle(X(x), Y(935), X(24), p);

            p.clearShadowLayer();
            stroke(1.5f, BRONZE);
            c.drawCircle(X(x), Y(935), X(24), p);

            txt(c, "•", x, 941, 20, GOLD, Paint.Align.CENTER);
        }

        glow(ORANGE, 7);
        fill(PANEL2);
        c.drawCircle(X(1390), Y(920), X(32), p);
        c.drawCircle(X(1470), Y(920), X(32), p);
        p.clearShadowLayer();

        stroke(2f, BRONZE);
        c.drawCircle(X(1390), Y(920), X(32), p);
        c.drawCircle(X(1470), Y(920), X(32), p);

        txt(c, "×", 1390, 930, 32, TEXT, Paint.Align.CENTER);
        txt(c, "⏻", 1470, 930, 27, TEXT, Paint.Align.CENTER);

        txt(c, "CLOSE", 1390, 975, 13, TEXT, Paint.Align.CENTER);
        txt(c, "POWER", 1470, 975, 13, TEXT, Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);

        sx = getWidth() / 1536f;
        sy = getHeight() / 1025f;

        fill(BG);
        c.drawRect(0, 0, getWidth(), getHeight(), p);

        // Tech grid
        stroke(1f, Color.argb(28, 80, 130, 160));
        for (int x = 0; x < 1536; x += 45) {
            line(c, x, 0, x, 1025);
        }
        for (int y = 0; y < 1025; y += 45) {
            line(c, 0, y, 1536, y);
        }

        // Outer frame
        stroke(2f, BRONZE);
        c.drawRoundRect(
                new RectF(X(15), Y(15), X(1521), Y(1008)),
                X(22), Y(22), p
        );

        stroke(1.5f, ORANGE);
        c.drawRoundRect(
                new RectF(X(28), Y(28), X(1508), Y(995)),
                X(18), Y(18), p
        );

        drawHeader(c);

        // Left navigation
        navButton(c, 125, "HOME", "HOME");
        navButton(c, 215, "CHAT", "CHAT");
        navButton(c, 305, "CREATE", "CREATE");
        navButton(c, 395, "ANALYSE", "ANALYSE");
        navButton(c, 485, "SETTINGS", "SETTINGS");
        navButton(c, 575, "HELP", "HELP");

        panel(c, 48, 685, 430, 815);
        txt(c, "DAILY BRIEFING", 72, 716, 15, TEXT, Paint.Align.LEFT);
        txt(c, "Ready for your commands.", 72, 752, 14, MUTED, Paint.Align.LEFT);
        txt(c, "ULTRON systems operational", 72, 780, 14, BLUE, Paint.Align.LEFT);

        // Core
        core(c, 768, 500, 205);

        txt(c, "NEW CONVERSATION", 768, 170, 17, TEXT, Paint.Align.CENTER);

        // Action modules
        circleButton(c, 385, 270, 42, "GENERATE");
        circleButton(c, 385, 395, 42, "SUMMARIZE");
        circleButton(c, 385, 520, 42, "RESEARCH");

        circleButton(c, 540, 215, 42, "FUNNELS");
        circleButton(c, 768, 190, 42, "VOICE");
        circleButton(c, 995, 215, 42, "TRANSLATE");

        circleButton(c, 1135, 330, 42, "CODE");
        circleButton(c, 1140, 470, 42, "ANALYSE FILE");
        circleButton(c, 1035, 600, 42, "ANALYSE");
        circleButton(c, 920, 725, 42, "OPTIMIZE");
        circleButton(c, 768, 770, 42, "IDEATE");
        circleButton(c, 520, 690, 42, "CHAT");

        drawRightStatus(c);
        drawBottom(c);

        pulse += 1.5f;
        if (pulse > 360f) {
            pulse = 0f;
        }

        postInvalidateDelayed(70);
    }

    private boolean hit(float x, float y,
                        float l, float t, float r, float b) {
        return x >= X(l) && x <= X(r)
                && y >= Y(t) && y <= Y(b);
    }

    private void fire(String action) {
        if (actionListener != null) {
            actionListener.onAction(action);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP) {
            return true;
        }

        float x = event.getX();
        float y = event.getY();

        if (hit(x, y, 48, 125, 280, 201)) fire("HOME");
        else if (hit(x, y, 48, 215, 280, 291)) fire("CHAT");
        else if (hit(x, y, 48, 305, 280, 381)) fire("CREATE");
        else if (hit(x, y, 48, 395, 280, 471)) fire("ANALYSE");
        else if (hit(x, y, 48, 485, 280, 561)) fire("SETTINGS");
        else if (hit(x, y, 48, 575, 280, 651)) fire("HELP");

        else if (hit(x, y, 343, 228, 427, 312)) fire("GENERATE");
        else if (hit(x, y, 343, 353, 427, 437)) fire("SUMMARIZE");
        else if (hit(x, y, 343, 478, 427, 562)) fire("RESEARCH");

        else if (hit(x, y, 498, 173, 582, 257)) fire("FUNNELS");
        else if (hit(x, y, 726, 148, 810, 232)) fire("VOICE");
        else if (hit(x, y, 953, 173, 1037, 257)) fire("TRANSLATE");

        else if (hit(x, y, 1093, 288, 1177, 372)) fire("CODE");
        else if (hit(x, y, 1098, 428, 1182, 512)) fire("FILE");
        else if (hit(x, y, 993, 558, 1077, 642)) fire("ANALYSE");
        else if (hit(x, y, 878, 683, 962, 767)) fire("OPTIMIZE");
        else if (hit(x, y, 726, 728, 810, 812)) fire("IDEATE");
        else if (hit(x, y, 478, 648, 562, 732)) fire("CHAT");

        else if (hit(x, y, 476, 910, 524, 958)) fire("VOICE");
        else if (hit(x, y, 558, 910, 606, 958)) fire("MUSIC");
        else if (hit(x, y, 640, 910, 688, 958)) fire("FILE");
        else if (hit(x, y, 722, 910, 770, 958)) fire("CODE");
        else if (hit(x, y, 804, 910, 852, 958)) fire("FILE");
        else if (hit(x, y, 886, 910, 934, 958)) fire("SETTINGS");

        else if (hit(x, y, 1358, 888, 1422, 952)) fire("CLOSE");
        else if (hit(x, y, 1438, 888, 1502, 952)) fire("POWER");

        return true;
    }
}
