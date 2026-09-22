package com.ultron.assistant.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.view.MotionEvent;

public class UltronReferenceDesignView extends View {

    public interface ActionListener {
        void onAction(String action);
    }

    private ActionListener actionListener;

    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float sx = 1f;
    private float sy = 1f;

    private final int BG = Color.rgb(3, 7, 11);
    private final int PANEL = Color.rgb(8, 12, 17);
    private final int ORANGE = Color.rgb(255, 125, 25);
    private final int GOLD = Color.rgb(255, 190, 110);
    private final int BRONZE = Color.rgb(120, 70, 30);
    private final int TEXT = Color.rgb(220, 210, 198);
    private final int BLUE = Color.rgb(35, 150, 210);

    public UltronReferenceDesignView(Context context) {
        super(context);
        p.setTypeface(android.graphics.Typeface.create(
                android.graphics.Typeface.SANS_SERIF,
                android.graphics.Typeface.NORMAL
        ));
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
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

    private void action(String name) {
        if (actionListener != null) actionListener.onAction(name);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP) return true;

        float x = event.getX() / sx;
        float y = event.getY() / sy;

        if (x >= 45 && x <= 285) {
            if (y >= 65 && y < 155) action("HOME");
            else if (y >= 155 && y < 245) action("CHAT");
            else if (y >= 245 && y < 335) action("CREATE");
            else if (y >= 335 && y < 425) action("ANALYSE");
            else if (y >= 425 && y < 515) action("SETTINGS");
            else if (y >= 515 && y < 600) action("HELP");
        }
        else if (x >= 680 && x <= 855 && y >= 120 && y <= 285) {
            action("VOICE");
        }
        else if (x >= 320 && x <= 445 && y >= 180 && y < 310) {
            action("VOICE");
        }
        else if (x >= 320 && x <= 445 && y >= 310 && y < 425) {
            action("GENERATE");
        }
        else if (x >= 320 && x <= 445 && y >= 425 && y < 535) {
            action("SUMMARIZE");
        }
        else if (x >= 430 && x <= 565 && y >= 500 && y < 625) {
            action("RESEARCH");
        }
        else if (x >= 430 && x <= 565 && y >= 625 && y < 745) {
            action("CHAT");
        }
        else if (x >= 1060 && x <= 1190 && y >= 180 && y < 310) {
            action("TRANSLATE");
        }
        else if (x >= 1060 && x <= 1190 && y >= 310 && y < 425) {
            action("CODE");
        }
        else if (x >= 1060 && x <= 1190 && y >= 425 && y < 555) {
            action("ANALYSE_FILE");
        }
        else if (x >= 980 && x <= 1110 && y >= 500 && y < 625) {
            action("ANALYSE");
        }
        else if (x >= 980 && x <= 1110 && y >= 625 && y < 745) {
            action("OPTIMIZE");
        }
        else if (x >= 690 && x <= 850 && y >= 700 && y < 820) {
            action("IDEATE");
        }
        else if (x >= 440 && x <= 1080 && y >= 825 && y <= 910) {
            action("CHAT");
        }
        else if (y >= 890 && y <= 980) {
            if (x >= 450 && x < 535) action("VOICE");
            else if (x >= 535 && x < 615) action("MUSIC");
            else if (x >= 615 && x < 700) action("CAMERA");
            else if (x >= 700 && x < 785) action("CODE");
            else if (x >= 785 && x < 870) action("FILE");
            else if (x >= 870 && x < 955) action("SETTINGS");
            else if (x >= 1260 && x < 1430) action("CLOSE");
            else if (x >= 1430 && x < 1535) action("POWER");
        }

        return true;
    }

    private float Y(float y) { return y * sy; }

    private void stroke(Canvas c, float width, int color) {
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(X(width));
        p.setColor(color);
        p.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
    }

    private void fill(Canvas c, int color) {
        p.setStyle(Paint.Style.FILL);
        p.setColor(color);
        p.clearShadowLayer();
    }

    private void glow(int color, float radius) {
        p.setShadowLayer(X(radius), 0, 0, color);
    }

    private void line(Canvas c, float x1, float y1, float x2, float y2) {
        c.drawLine(X(x1), Y(y1), X(x2), Y(y2), p);
    }

    private void text(Canvas c, String s, float x, float y,
                      float size, int color, Paint.Align align) {
        fill(c, color);
        p.setTextSize(X(size));
        p.setTextAlign(align);
        c.drawText(s, X(x), Y(y), p);
    }

    private void panel(Canvas c, float l, float t, float r, float b) {
        fill(c, PANEL);
        c.drawRoundRect(new RectF(X(l),Y(t),X(r),Y(b)),
                X(8), X(8), p);

        stroke(c, 1.5f, BRONZE);
        c.drawRoundRect(new RectF(X(l),Y(t),X(r),Y(b)),
                X(8), X(8), p);

        stroke(c, 1f, Color.argb(80,255,130,30));
        c.drawLine(X(l+14),Y(t+2),X(r-25),Y(t+2),p);
    }

    private void hudButton(Canvas c, float l, float t, float r, float b,
                           String label) {
        panel(c,l,t,r,b);
        stroke(c,1.2f,ORANGE);
        c.drawRoundRect(new RectF(X(l+4),Y(t+4),X(r-4),Y(b-4)),
                X(5),X(5),p);
        text(c,label,(l+r)/2,(t+b)/2+5,14,TEXT,Paint.Align.CENTER);
    }

    private void core(Canvas c, float cx, float cy, float radius) {
        // outer glow
        fill(c, Color.TRANSPARENT);
        glow(ORANGE, 22);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(X(5));
        p.setColor(Color.argb(180,255,105,15));
        c.drawCircle(X(cx),Y(cy),X(radius),p);
        p.clearShadowLayer();

        // reactor rings
        for (int i=0;i<8;i++) {
            stroke(c, i % 2 == 0 ? 2f : 1f,
                    i % 2 == 0 ? BRONZE : Color.rgb(50,75,95));
            c.drawCircle(X(cx),Y(cy),X(radius-i*13),p);
        }

        // segmented outer ring
        stroke(c,4,ORANGE);
        for (int i=0;i<24;i++) {
            double a1 = Math.toRadians(i*15+2);
            double a2 = Math.toRadians(i*15+10);
            float r = radius-15;
            line(c,
                    cx+(float)Math.cos(a1)*r,
                    cy+(float)Math.sin(a1)*r,
                    cx+(float)Math.cos(a2)*r,
                    cy+(float)Math.sin(a2)*r);
        }

        // inner energy sphere
        glow(ORANGE,18);
        fill(c,Color.rgb(30,14,6));
        c.drawCircle(X(cx),Y(cy),X(radius*.42f),p);
        p.clearShadowLayer();

        stroke(c,1.5f,ORANGE);
        c.drawCircle(X(cx),Y(cy),X(radius*.42f),p);

        // energy nodes
        glow(ORANGE,10);
        fill(c,ORANGE);
        c.drawCircle(X(cx),Y(cy),X(9),p);

        for(int i=0;i<12;i++){
            double a=Math.toRadians(i*30);
            float rr=radius*.32f;
            c.drawCircle(
                    X(cx+(float)Math.cos(a)*rr),
                    Y(cy+(float)Math.sin(a)*rr),
                    X(3),p);
        }
        p.clearShadowLayer();

        text(c,"ULTRON",cx,cy-3,23,GOLD,Paint.Align.CENTER);
        text(c,"AI CORE",cx,cy+22,13,TEXT,Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);

        sx = getWidth()/1536f;
        sy = getHeight()/1024f;

        // Background
        c.drawColor(BG);

        // grid
        stroke(c,0.5f,Color.rgb(10,25,35));
        for(int x=20;x<1536;x+=45) line(c,x,0,x,1024);
        for(int y=20;y<1024;y+=45) line(c,0,y,1536,y);

        // Main futuristic border
        stroke(c,3,BRONZE);
        c.drawRoundRect(new RectF(X(12),Y(12),X(1524),Y(1012)),
                X(24),X(24),p);

        stroke(c,1.2f,ORANGE);
        c.drawRoundRect(new RectF(X(25),Y(25),X(1511),Y(999)),
                X(18),X(18),p);

        // Corner tech lines
        stroke(c,2,ORANGE);
        line(c,35,60,170,60);
        line(c,35,60,35,190);
        line(c,1365,60,1500,60);
        line(c,1500,60,1500,190);
        line(c,35,964,170,964);
        line(c,35,834,35,964);
        line(c,1365,964,1500,964);
        line(c,1500,834,1500,964);

        // Header
        text(c,"△",575,72,42,GOLD,Paint.Align.CENTER);
        text(c,"ULTRON",768,74,52,GOLD,Paint.Align.CENTER);
        text(c,"PERSONAL AI ASSISTANT",768,105,18,TEXT,Paint.Align.CENTER);

        stroke(c,2,ORANGE);
        line(c,475,112,1060,112);

        // Left navigation
        panel(c,48,150,300,565);
        text(c,"COMMAND",70,180,15,ORANGE,Paint.Align.LEFT);

        hudButton(c,65,195,280,255,"⌂    HOME");
        hudButton(c,65,265,280,325,"●    CHAT");
        hudButton(c,65,335,280,395,"✦    CREATE");
        hudButton(c,65,405,280,465,"▮    ANALYSE");
        hudButton(c,65,475,280,535,"⚙    SETTINGS");

        // right status panels
        panel(c,1195,145,1490,330);
        text(c,"ULTRON",1342,205,27,TEXT,Paint.Align.CENTER);
        text(c,"ONLINE",1342,238,22,Color.rgb(0,230,110),Paint.Align.CENTER);
        text(c,"“Ready, Imtiyaz.”",1342,275,16,BLUE,Paint.Align.CENTER);

        panel(c,1195,350,1490,570);
        text(c,"SYSTEM STATUS",1220,380,16,TEXT,Paint.Align.LEFT);
        text(c,"Battery",1280,425,15,TEXT,Paint.Align.LEFT);
        text(c,"Brightness",1280,465,15,TEXT,Paint.Align.LEFT);
        text(c,"Network",1280,505,15,TEXT,Paint.Align.LEFT);
        text(c,"Time",1280,545,15,TEXT,Paint.Align.LEFT);

        // Core area
        core(c,768,515,205);

        text(c,"NEW CONVERSATION",768,165,17,TEXT,Paint.Align.CENTER);

        // Core action circles
        String[] a={"VOICE","GENERATE","SUMMARIZE","RESEARCH",
                    "CHAT","IDEATE","ANALYSE","OPTIMIZE"};
        float[][] pos={
                {385,280},{385,390},{385,500},{500,560},
                {500,690},{770,755},{1035,560},{1035,390}
        };

        for(int i=0;i<a.length;i++){
            float cx=pos[i][0],cy=pos[i][1];
            glow(ORANGE,7);
            fill(c,Color.rgb(8,12,17));
            c.drawCircle(X(cx),Y(cy),X(42),p);
            p.clearShadowLayer();
            stroke(c,2,ORANGE);
            c.drawCircle(X(cx),Y(cy),X(42),p);
            text(c,"•",cx,cy+7,28,GOLD,Paint.Align.CENTER);
            text(c,a[i],cx,cy+65,14,TEXT,Paint.Align.CENTER);
        }

        // Bottom command area
        panel(c,445,830,1080,900);
        text(c,"◉",475,873,20,GOLD,Paint.Align.CENTER);
        text(c,"STANDARD",545,873,15,TEXT,Paint.Align.CENTER);
        stroke(c,1,BRONZE);
        line(c,610,845,610,885);
        text(c,"Ask anything...",650,874,16,Color.rgb(145,135,125),Paint.Align.LEFT);
        text(c,"➤",1040,874,22,GOLD,Paint.Align.CENTER);

        // Bottom controls
        String[] bottom={"MIC","MUSIC","IMAGE","CODE","FILE","SETTINGS"};
        for(int i=0;i<bottom.length;i++){
            float x=490+i*82;
            glow(ORANGE,5);
            fill(c,Color.rgb(7,10,14));
            c.drawCircle(X(x),Y(935),X(24),p);
            p.clearShadowLayer();
            stroke(c,1.5f,BRONZE);
            c.drawCircle(X(x),Y(935),X(24),p);
            text(c,"•",x,941,20,GOLD,Paint.Align.CENTER);
        }

        // Lower right power controls
        glow(ORANGE,7);
        fill(c,Color.rgb(8,10,13));
        c.drawCircle(X(1390),Y(920),X(32),p);
        c.drawCircle(X(1470),Y(920),X(32),p);
        p.clearShadowLayer();
        stroke(c,2,BRONZE);
        c.drawCircle(X(1390),Y(920),X(32),p);
        c.drawCircle(X(1470),Y(920),X(32),p);
        text(c,"×",1390,930,32,TEXT,Paint.Align.CENTER);
        text(c,"⏻",1470,930,27,TEXT,Paint.Align.CENTER);
        text(c,"CLOSE",1390,975,13,TEXT,Paint.Align.CENTER);
        text(c,"POWER",1470,975,13,TEXT,Paint.Align.CENTER);

        // subtle blue tech accents
        stroke(c,1,BLUE);
        line(c,315,155,520,155);
        line(c,1010,155,1180,155);
        line(c,315,810,520,810);
        line(c,1010,810,1180,810);

        postInvalidateDelayed(80);
    }
}
