package com.ultron.assistant.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class UltronAccessibilityService extends AccessibilityService {

    private static volatile UltronAccessibilityService instance;
    private volatile String currentPackage = "";

    public static UltronAccessibilityService getInstance() {
        return instance;
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;

        CharSequence pkg = event.getPackageName();
        if (pkg != null) {
            currentPackage = pkg.toString();
        }
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public void onDestroy() {
        if (instance == this) {
            instance = null;
        }
        super.onDestroy();
    }

    public String getCurrentPackage() {
        return currentPackage;
    }

    public boolean clickText(String text) {
        if (text == null || text.trim().isEmpty()) return false;

        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return false;

        java.util.List<AccessibilityNodeInfo> nodes =
                root.findAccessibilityNodeInfosByText(text);

        if (nodes == null) return false;

        for (AccessibilityNodeInfo node : nodes) {
            if (clickNode(node)) return true;
        }

        return false;
    }

    private boolean clickNode(AccessibilityNodeInfo node) {
        AccessibilityNodeInfo current = node;

        while (current != null) {
            if (current.isClickable() && current.isEnabled()) {
                return current.performAction(
                        AccessibilityNodeInfo.ACTION_CLICK
                );
            }

            current = current.getParent();
        }

        return false;
    }

    public boolean setText(String targetText, String value) {
        if (targetText == null || targetText.trim().isEmpty()) return false;

        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return false;

        java.util.List<AccessibilityNodeInfo> nodes =
                root.findAccessibilityNodeInfosByText(targetText);

        if (nodes == null) return false;

        for (AccessibilityNodeInfo node : nodes) {
            if (!node.isEditable()) continue;

            Bundle args = new Bundle();
            args.putCharSequence(
                    AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    value
            );

            if (node.performAction(
                    AccessibilityNodeInfo.ACTION_SET_TEXT,
                    args
            )) {
                return true;
            }
        }

        return false;
    }

    public boolean scrollForward() {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return false;

        return root.performAction(
                AccessibilityNodeInfo.ACTION_SCROLL_FORWARD
        );
    }

    public String readVisibleText() {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return "";

        StringBuilder out = new StringBuilder();
        collectText(root, out);

        return out.toString().trim();
    }

    private void collectText(
            AccessibilityNodeInfo node,
            StringBuilder out
    ) {
        if (node == null) return;

        CharSequence text = node.getText();

        if (text != null && text.length() > 0) {
            if (out.length() > 0) out.append('\n');
            out.append(text);
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            collectText(node.getChild(i), out);
        }
    }
}
