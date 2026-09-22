package com.ultron.assistant.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

public class UltronFinalHudView extends View {

    public interface HudListener {
        void onHome();
        void onChat();
        void onCreate();
        void onAnalyse();
        void onSettings();
        void onHelp();
        void onVoice();
        void onGenerate();
        void onResearch();
        void onSummarize();
        void onTranslate();
        void onCode();
        void onAnalyseFile();
        void onOptimize();
        void onIdeate();
        void onNewConversation();
        void onClose();
        void onPower();
        void onFile();
    }

    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    private HudListener listener;

    private float pulse;
    private float rotation;

    private final int BG = Color.rgb(3, 8, 13);
    private final int PANEL = Color.rgb(7, 14, 21);
    private final int PANEL2 = Color.rgb(10, 17, 24);
    private final int ORANGE = Color.rgb(255, 112, 24);
    private final int GOLD = Color.rgb(255, 181, 104);
    private final int WHITE = Color.rgb(224, 226, 230);
    private final int MUTED = Color.rgb(151, 158, 168);
    private final int CYAN = Color.rgb(38, 190, 225);
    private final int GREEN = Color.rgb(52, 235, 133);

    public UltronFinalHudView(Context context) {
        super(context);
        p.setTypeface(android.graphics.Typeface.create(
                android.graphics.Typeface.SANS_SERIF,
                android.graphics.Typeface.NORMAL
        ));
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        postInvalidateOnAnimation();
    }

    public void setHudListener(HudListener listener) {
        this.listener = listener;
    }

    private float sx(float x) {
        return x * getWidth() / 1536f;
    }

    private float sy(float y) {
        return y * getHeight() / 1024f;
    }

    private float dp(float v) {
        return v * getResources().getDisplayMetrics().density;
    }

    private void text(Canvas c, String s, float x, float y,
                      float size, int color, Paint.Align align) {
        p.setStyle(Paint.Style.FILL);
        p.setColor(color);
        p.setTextSize(sy(size));
        p.setTextAlign(align);
        p.setStrokeWidth(1);
        p.clearShadowLayer();
        c.drawText(s, sx(x), sy(y), p);
    }

    private void glowText(Canvas c, String s, float x, float y,
                          float size, int color, Paint.Align align) {
        p.setStyle(Paint.Style.FILL);
        p.setColor(color);
        p.setTextSize(sy(size));
        p.setTextAlign(align);
        p.setShadowLayer(sy(10), 0, 0,
                Color.argb(210, 255, 91, 20));
        c.drawText(s, sx(x), sy(y), p);
        p.clearShadowLayer();
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);

        float w = getWidth();
        float h = getHeight();

        c.drawColor(BG);

        // Deep orange/blue futuristic background
        p.setStyle(Paint.Style.FILL);
        p.setShader(new android.graphics.RadialGradient(
                w * .50f, h * .50f, Math.max(w, h) * .48f,
                new int[]{
                        Color.rgb(35, 18, 8),
                        Color.rgb(8, 16, 23),
                        Color.rgb(2, 6, 10)
                },
                new float[]{0f, .42f, 1f},
                android.graphics.Shader.TileMode.CLAMP
        ));
        c.drawRect(0, 0, w, h, p);
        p.setShader(null);

        drawGrid(c);
        drawFrame(c);
        drawHeader(c);
        drawLeftRail(c);
        drawRightDeck(c);
        drawCoreArea(c);
        drawBottom(c);

        pulse += .045f;
        rotation += .35f;
        postInvalidateOnAnimation();
    }

    private void drawGrid(Canvas c) {
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(1);
        p.setColor(Color.argb(28, 255, 122, 30));

        float stepX = sx(45);
        float stepY = sy(45);

        for (float x = 25; x < getWidth(); x += stepX) {
            c.drawLine(x, 0, x, getHeight(), p);
        }

        for (float y = 20; y < getHeight(); y += stepY) {
            c.drawLine(0, y, getWidth(), y, p);
        }
    }

    private void drawFrame(Canvas c) {
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(dp(2));
        p.setColor(Color.rgb(78, 53, 30));
        c.drawRoundRect(
                sx(12), sy(12), sx(1524), sy(1012),
                dp(16), dp(16), p
        );

        p.setStrokeWidth(dp(1));
        p.setColor(Color.argb(180, 255, 108, 20));
        c.drawRoundRect(
                sx(21), sy(21), sx(1515), sy(1003),
                dp(12), dp(12), p
        );

        // Top angular orange lines
        p.setStrokeWidth(dp(2));
        p.setColor(ORANGE);

        path.reset();
        path.moveTo(sx(35), sy(32));
        path.lineTo(sx(335), sy(32));
        path.lineTo(sx(365), sy(55));
        path.lineTo(sx(470), sy(55));
        c.drawPath(path, p);

        path.reset();
        path.moveTo(sx(1500), sy(32));
        path.lineTo(sx(1200), sy(32));
        path.lineTo(sx(1170), sy(55));
        path.lineTo(sx(1065), sy(55));
        c.drawPath(path, p);

        // Bottom angular lines
        path.reset();
        path.moveTo(sx(35), sy(992));
        path.lineTo(sx(350), sy(992));
        path.lineTo(sx(375), sy(972));
        c.drawPath(path, p);

        path.reset();
        path.moveTo(sx(1500), sy(992));
        path.lineTo(sx(1185), sy(992));
        path.lineTo(sx(1160), sy(972));
        c.drawPath(path, p);
    }

    private void drawHeader(Canvas c) {
        glowText(c, "△", 548, 67, 40, GOLD, Paint.Align.CENTER);
        glowText(c, "ULTRON", 768, 69, 43, WHITE, Paint.Align.CENTER);

        text(c, "PERSONAL AI ASSISTANT",
                768, 101, 18, GOLD, Paint.Align.CENTER);

        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(dp(1));
        p.setColor(Color.argb(120, 255, 105, 22));

        c.drawLine(sx(320), sy(116), sx(650), sy(116), p);
        c.drawLine(sx(886), sy(116), sx(1216), sy(116), p);

        // top system indicators
        drawTopMetric(c, 1210, "▣", "78%", "BATTERY");
        drawTopMetric(c, 1290, "☼", "70%", "BRIGHTNESS");
        drawTopMetric(c, 1370, "▥", "4G", "NETWORK");
        drawTopMetric(c, 1450, "◷", "10:24 PM", "TIME");
    }

    private void drawTopMetric(Canvas c, float x, String icon,
                               String value, String label) {
        glowText(c, icon, x, 61, 24, GOLD, Paint.Align.CENTER);
        text(c, value, x, 82, 12, WHITE, Paint.Align.CENTER);
        text(c, label, x, 99, 8, MUTED, Paint.Align.CENTER);
    }

    private void drawLeftRail(Canvas c) {
        panel(c, 35, 140, 292, 570);

        text(c, "COMMAND", 58, 165, 12, GOLD, Paint.Align.LEFT);

        menu(c, 55, 190, "⌂", "HOME", true);
        menu(c, 55, 282, "●", "CHAT", false);
        menu(c, 55, 374, "✣", "CREATE", false);
        menu(c, 55, 466, "▥", "ANALYSE", false);
        menu(c, 55, 558, "⚙", "SETTINGS", false);
        menu(c, 55, 650, "?", "HELP", false);

        // Daily briefing
        panel(c, 35, 728, 430, 920);
        text(c, "DAILY BRIEFING", 58, 755, 12, GOLD, Paint.Align.LEFT);

        // Decorative globe
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(dp(2));
        p.setColor(Color.argb(150, 70, 170, 210));
        p.setShadowLayer(dp(12), 0, 0,
                Color.argb(100, 30, 180, 230));
        c.drawCircle(sx(165), sy(820), sx(62), p);
        c.drawOval(
                sx(112), sy(758), sx(218), sy(882), p
        );
        c.drawLine(sx(105), sy(820), sx(225), sy(820), p);
        p.clearShadowLayer();

        text(c, "☼  24°C", 270, 793, 18, WHITE, Paint.Align.LEFT);
        text(c, "Partly Cloudy", 270, 813, 10, MUTED, Paint.Align.LEFT);
        text(c, "◉  78%", 270, 850, 18, WHITE, Paint.Align.LEFT);
        text(c, "Humidity", 270, 870, 10, MUTED, Paint.Align.LEFT);

        text(c, "◉  Project Review          10:00 AM",
                58, 897, 10, MUTED, Paint.Align.LEFT);
        text(c, "◉  Design Feedback         11:30 AM",
                58, 916, 10, MUTED, Paint.Align.LEFT);
    }

    private void menu(Canvas c, float x, float y,
                      String icon, String name, boolean active) {
        p.setStyle(Paint.Style.FILL);
        p.setColor(active
                ? Color.rgb(31, 18, 10)
                : Color.rgb(7, 14, 21));

        path.reset();
        path.moveTo(sx(x), sy(y));
        path.lineTo(sx(x + 195), sy(y));
        path.lineTo(sx(x + 213), sy(y + 17));
        path.lineTo(sx(x + 213), sy(y + 72));
        path.lineTo(sx(x + 195), sy(y + 88));
        path.lineTo(sx(x), sy(y + 88));
        path.close();
        c.drawPath(path, p);

        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(dp(active ? 2 : 1));
        p.setColor(active ? ORANGE : Color.rgb(58, 70, 82));
        c.drawPath(path, p);

        p.setStyle(Paint.Style.FILL);
        p.setColor(active ? GOLD : WHITE);
        c.drawCircle(sx(x + 43), sy(y + 44), sx(27), p);

        p.setColor(Color.rgb(15, 20, 25));
        c.drawCircle(sx(x + 43), sy(y + 44), sx(23), p);

        glowText(c, icon, x + 43, y + 52,
                25, active ? GOLD : WHITE, Paint.Align.CENTER);

        text(c, name, x + 82, y + 51,
                15, WHITE, Paint.Align.LEFT);
    }

    private void drawRightDeck(Canvas c) {
        // ULTRON status
        panel(c, 1210, 170, 1495, 350);
        drawRobot(c, 1260, 255);
        glowText(c, "ULTRON", 1380, 245, 24, WHITE, Paint.Align.CENTER);
        text(c, "● ONLINE", 1380, 278, 19, GREEN, Paint.Align.CENTER);
        text(c, "“Ready, Imtiyaz.”", 1380, 322,
                14, CYAN, Paint.Align.CENTER);

        // System status
        panel(c, 1210, 370, 1495, 570);
        text(c, "⚡  SYSTEM STATUS", 1232, 398,
                13, GOLD, Paint.Align.LEFT);

        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(dp(7));
        p.setColor(Color.rgb(35, 48, 59));
        c.drawCircle(sx(1275), sy(485), sx(58), p);

        p.setColor(ORANGE);
        RectF arc = new RectF(
                sx(1217), sy(427), sx(1333), sy(543)
        );
        c.drawArc(arc, -90, 280, false, p);

        text(c, "78%", 1275, 493, 20, WHITE, Paint.Align.CENTER);

        text(c, "▣  Battery          78%", 1345, 443,
                11, WHITE, Paint.Align.LEFT);
        text(c, "☼  Brightness       70%", 1345, 477,
                11, WHITE, Paint.Align.LEFT);
        text(c, "▥  Network          4G", 1345, 511,
                11, CYAN, Paint.Align.LEFT);
        text(c, "◷  Time             10:24 PM", 1345, 545,
                11, WHITE, Paint.Align.LEFT);

        // Insights
        panel(c, 1090, 585, 1495, 825);
        text(c, "⚡  INSIGHTS", 1112, 614,
                13, GOLD, Paint.Align.LEFT);

        insight(c, 1115, 650, "✦",
                "Generated 5 insights",
                "for your recent topic.");

        insight(c, 1115, 710, "▤",
                "Summarized 3 key points",
                "from your notes.");

        insight(c, 1115, 770, "◉",
                "Found 2 potential",
                "improvements.");

        text(c, "CONTEXT TIMELINE   ›",
                1115, 805, 9, MUTED, Paint.Align.LEFT);
    }

    private void drawRobot(Canvas c, float x, float y) {
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.rgb(15, 30, 40));
        p.setShadowLayer(dp(14), 0, 0,
                Color.argb(160, 30, 170, 220));

        path.reset();
        path.moveTo(sx(x), sy(y - 45));
        path.lineTo(sx(x + 37), sy(y - 27));
        path.lineTo(sx(x + 45), sy(y + 20));
        path.lineTo(sx(x + 20), sy(y + 47));
        path.lineTo(sx(x - 20), sy(y + 47));
        path.lineTo(sx(x - 45), sy(y + 20));
        path.lineTo(sx(x - 37), sy(y - 27));
        path.close();
        c.drawPath(path, p);
        p.clearShadowLayer();

        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(dp(2));
        p.setColor(CYAN);
        c.drawPath(path, p);

        p.setStyle(Paint.Style.FILL);
        p.setColor(CYAN);
        c.drawCircle(sx(x - 15), sy(y), sx(4), p);
        c.drawCircle(sx(x + 15), sy(y), sx(4), p);
    }

    private void insight(Canvas c, float x, float y,
                         String icon, String line1, String line2) {
        glowText(c, icon, x + 15, y + 10,
                18, GOLD, Paint.Align.CENTER);
        text(c, line1, x + 43, y + 5,
                10, WHITE, Paint.Align.LEFT);
        text(c, line2, x + 43, y + 22,
                9, MUTED, Paint.Align.LEFT);

        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(1);
        p.setColor(Color.rgb(35, 48, 58));
        c.drawLine(sx(x), sy(y + 38),
                sx(1455), sy(y + 38), p);
    }

    private void drawCoreArea(Canvas c) {
        // New conversation microphone
        drawCircleButton(c, 768, 165, 55, "♩", "NEW CONVERSATION");

        // Left orbital actions
        drawCircleButton(c, 375, 225, 42, "♩", "VOICE");
        drawCircleButton(c, 375, 345, 42, "✦", "GENERATE");
        drawCircleButton(c, 375, 465, 42, "▤", "SUMMARIZE");
        drawCircleButton(c, 500, 560, 42, "●", "RESEARCH");
        drawCircleButton(c, 500, 680, 42, "▰", "CHAT");

        // Right orbital actions
        drawCircleButton(c, 1135, 225, 42, "文", "TRANSLATE");
        drawCircleButton(c, 1135, 345, 42, "</>", "CODE");
        drawCircleButton(c, 1135, 465, 42, "⇧", "ANALYSE FILE");
        drawCircleButton(c, 1015, 560, 42, "⌘", "ANALYSE");
        drawCircleButton(c, 1015, 680, 42, "⚙", "OPTIMIZE");
        drawCircleButton(c, 768, 770, 42, "♧", "IDEATE");

        drawReactor(c, 768, 505, 225);
    }

    private void drawCircleButton(Canvas c, float x, float y,
                                  float r, String icon, String label) {
        float rr = sx(r);

        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(dp(2));
        p.setColor(Color.rgb(103, 58, 27));
        p.setShadowLayer(dp(10), 0, 0,
                Color.argb(180, 255, 94, 15));

        c.drawCircle(sx(x), sy(y), rr, p);
        p.clearShadowLayer();

        p.setStrokeWidth(dp(1));
        p.setColor(GOLD);
        c.drawCircle(sx(x), sy(y), rr * .82f, p);

        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.rgb(19, 22, 25));
        c.drawCircle(sx(x), sy(y), rr * .67f, p);

        glowText(c, icon, x, y + 9,
                23, GOLD, Paint.Align.CENTER);

        text(c, label, x, y + r + 25,
                11, WHITE, Paint.Align.CENTER);
    }

    private void drawReactor(Canvas c, float x, float y, float r) {
        float rr = sx(r);

        // Outer glow
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(dp(3));
        p.setColor(ORANGE);
        p.setShadowLayer(dp(28), 0, 0,
                Color.argb(230, 255, 92, 16));
        c.drawCircle(sx(x), sy(y), rr, p);
        p.clearShadowLayer();

        // Mechanical rings
        for (int i = 0; i < 7; i++) {
            float radius = rr - sx(i * 16);

            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(dp(i % 2 == 0 ? 5 : 2));
            p.setColor(i % 2 == 0
                    ? Color.rgb(122, 91, 70)
                    : Color.rgb(48, 91, 112));

            RectF oval = new RectF(
                    sx(x) - radius, sy(y) - radius,
                    sx(x) + radius, sy(y) + radius
            );

            float start = rotation * (i % 2 == 0 ? 1 : -1)
                    + i * 33;

            c.drawArc(oval, start, 58, false, p);
            c.drawArc(oval, start + 100, 42, false, p);
            c.drawArc(oval, start + 205, 72, false, p);
        }

        // Bright orange segments
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(dp(7));
        p.setColor(ORANGE);
        p.setShadowLayer(dp(12), 0, 0,
                Color.argb(230, 255, 93, 14));

        RectF outer = new RectF(
                sx(x - r + 25), sy(y - r + 25),
                sx(x + r - 25), sy(y + r - 25)
        );

        c.drawArc(outer, rotation, 48, false, p);
        c.drawArc(outer, rotation + 120, 38, false, p);
        c.drawArc(outer, rotation + 240, 55, false, p);

        p.clearShadowLayer();

        // Inner energy field
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.rgb(19, 22, 27));
        p.setShadowLayer(dp(25), 0, 0,
                Color.argb(220, 255, 91, 14));
        c.drawCircle(sx(x), sy(y), sx(91), p);
        p.clearShadowLayer();

        // Energy network
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(dp(1));
        p.setColor(Color.argb(210, 255, 145, 48));

        for (int i = 0; i < 18; i++) {
            double a = Math.toRadians(rotation * 2 + i * 20);
            float px = x + (float)Math.cos(a) * 72;
            float py = y + (float)Math.sin(a) * 72;
            c.drawLine(sx(x), sy(y), sx(px), sy(py), p);
            p.setStyle(Paint.Style.FILL);
            c.drawCircle(sx(px), sy(py), dp(2), p);
            p.setStyle(Paint.Style.STROKE);
        }

        // Core orb
        float corePulse = (float)(Math.sin(pulse) * 5f);
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.rgb(255, 111, 23));
        p.setShadowLayer(dp(35), 0, 0,
                Color.argb(255, 255, 89, 10));
        c.drawCircle(sx(x), sy(y), sx(27 + corePulse), p);
        p.clearShadowLayer();

        p.setColor(Color.rgb(255, 216, 146));
        c.drawCircle(sx(x), sy(y), sx(11), p);

        glowText(c, "ULTRON", x, y + 57,
                19, WHITE, Paint.Align.CENTER);
        text(c, "AI CORE", x, y + 80,
                13, GOLD, Paint.Align.CENTER);
    }

    private void drawBottom(Canvas c) {
        // Chat / command bar
        panel(c, 450, 845, 1090, 905);

        text(c, "◎  STANDARD", 485, 882,
                12, WHITE, Paint.Align.LEFT);

        p.setColor(Color.rgb(65, 72, 80));
        p.setStrokeWidth(dp(1));
        c.drawLine(sx(605), sy(855), sx(605), sy(895), p);

        text(c, "Ask anything...", 630, 882,
                14, MUTED, Paint.Align.LEFT);

        glowText(c, "➤", 1050, 883,
                23, GOLD, Paint.Align.CENTER);

        // Bottom quick controls
        drawMini(c, 490, 938, "♩");
        drawMini(c, 555, 938, "♫");
        drawMini(c, 620, 938, "▧");
        drawMini(c, 685, 938, "</>");
        drawMini(c, 750, 938, "▤");
        drawMini(c, 815, 938, "⚙");

        panel(c, 875, 915, 1055, 965);
        text(c, "MODE", 895, 947, 10, MUTED, Paint.Align.LEFT);
        text(c, "ASSISTANT  ▼", 970, 947, 11, WHITE, Paint.Align.CENTER);

        // Close / power
        drawPowerButton(c, 1330, 925, "×", "CLOSE");
        drawPowerButton(c, 1420, 925, "⏻", "POWER");
    }

    private void drawMini(Canvas c, float x, float y, String icon) {
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(dp(2));
        p.setColor(Color.rgb(105, 65, 35));
        p.setShadowLayer(dp(7), 0, 0,
                Color.argb(170, 255, 91, 15));
        c.drawCircle(sx(x), sy(y), sx(24), p);
        p.clearShadowLayer();

        glowText(c, icon, x, y + 7,
                18, GOLD, Paint.Align.CENTER);
    }

    private void drawPowerButton(Canvas c, float x, float y,
                                 String icon, String label) {
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(dp(2));
        p.setColor(Color.rgb(100, 62, 33));
        p.setShadowLayer(dp(10), 0, 0,
                Color.argb(190, 255, 91, 15));
        c.drawCircle(sx(x), sy(y), sx(37), p);
        p.clearShadowLayer();

        glowText(c, icon, x, y + 11,
                34, WHITE, Paint.Align.CENTER);
        text(c, label, x, y + 61,
                12, WHITE, Paint.Align.CENTER);
    }

    private void panel(Canvas c, float l, float t, float r, float b) {
        p.setStyle(Paint.Style.FILL);
        p.setColor(PANEL);
        c.drawRoundRect(
                sx(l), sy(t), sx(r), sy(b),
                dp(10), dp(10), p
        );

        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(dp(1.5f));
        p.setColor(Color.rgb(91, 57, 29));
        c.drawRoundRect(
                sx(l), sy(t), sx(r), sy(b),
                dp(10), dp(10), p
        );

        p.setColor(Color.argb(100, 255, 104, 20));
        c.drawRoundRect(
                sx(l + 5), sy(t + 5),
                sx(r - 5), sy(b - 5),
                dp(7), dp(7), p
        );

        p.setStrokeWidth(dp(2));
        p.setColor(ORANGE);

        c.drawLine(sx(l + 12), sy(t),
                sx(l + 80), sy(t), p);
        c.drawLine(sx(r - 80), sy(b),
                sx(r - 12), sy(b), p);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() != MotionEvent.ACTION_UP) {
            return true;
        }

        float x = e.getX() * 1536f / getWidth();
        float y = e.getY() * 1024f / getHeight();

        if (listener == null) return true;

        // Left menu
        if (x >= 50 && x <= 285) {
            if (y >= 185 && y < 275) listener.onHome();
            else if (y >= 275 && y < 365) listener.onChat();
            else if (y >= 365 && y < 455) listener.onCreate();
            else if (y >= 455 && y < 550) listener.onAnalyse();
            else if (y >= 550 && y < 640) listener.onSettings();
            else if (y >= 640 && y < 725) listener.onHelp();
            return true;
        }

        // New conversation
        if (dist(x, y, 768, 165) < 65) {
            listener.onNewConversation();
            return true;
        }

        // Orbital actions
        if (dist(x, y, 375, 225) < 55) listener.onVoice();
        else if (dist(x, y, 375, 345) < 55) listener.onGenerate();
        else if (dist(x, y, 375, 465) < 55) listener.onSummarize();
        else if (dist(x, y, 500, 560) < 55) listener.onResearch();
        else if (dist(x, y, 500, 680) < 55) listener.onChat();
        else if (dist(x, y, 1135, 225) < 55) listener.onCreate();
        else if (dist(x, y, 1135, 345) < 55) listener.onCode();
        else if (dist(x, y, 1135, 465) < 55) listener.onAnalyseFile();
        else if (dist(x, y, 1015, 560) < 55) listener.onAnalyse();
        else if (dist(x, y, 1015, 680) < 55) listener.onOptimize();
        else if (dist(x, y, 768, 770) < 55) listener.onIdeate();

        // Bottom
        if (x > 1260 && y > 875) {
            if (x < 1375) listener.onClose();
            else listener.onPower();
        }

        // FILE action area
        if (x >= 700 && x <= 820 && y >= 910 && y <= 970) {
            listener.onFile();
        }

        return true;
    }

    private float dist(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }
}
