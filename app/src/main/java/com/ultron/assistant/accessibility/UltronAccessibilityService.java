package com.ultron.assistant.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public class UltronAccessibilityService
        extends AccessibilityService {

    private static UltronAccessibilityService instance;

    @Override
    public void onServiceConnected() {

        super.onServiceConnected();

        instance = this;
    }

    @Override
    public void onAccessibilityEvent(
            AccessibilityEvent event
    ) {
        // UI events can be handled here later.
    }

    @Override
    public void onInterrupt() {
        // Required method.
    }

    @Override
    public void onDestroy() {

        if (instance == this) {
            instance = null;
        }

        super.onDestroy();
    }

    public static boolean isRunning() {
        return instance != null;
    }

    public static boolean goHome() {

        if (instance == null) {
            return false;
        }

        return instance.performGlobalAction(
                GLOBAL_ACTION_HOME
        );
    }

    public static boolean goBack() {

        if (instance == null) {
            return false;
        }

        return instance.performGlobalAction(
                GLOBAL_ACTION_BACK
        );
    }
}
