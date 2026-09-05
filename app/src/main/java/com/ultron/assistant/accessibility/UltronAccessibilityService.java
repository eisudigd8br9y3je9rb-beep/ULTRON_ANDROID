package com.ultron.assistant.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class UltronAccessibilityService
        extends AccessibilityService {private static UltronAccessibilityService instance;

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
    }

    @Override
    public void onAccessibilityEvent(
            android.view.accessibility.AccessibilityEvent event
    ) {
        // Service monitors active windows.
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public void onDestroy() {
        instance = null;
        super.onDestroy();
    }

    public static boolean isRunning() {
        return instance != null;
    }

    public static boolean clickText(String text) {

        if (instance == null || text == null || text.trim().isEmpty()) {
            return false;
        }

        AccessibilityNodeInfo root =
                instance.getRootInActiveWindow();

        if (root == null) {
            return false;
        }

        try {
            List<AccessibilityNodeInfo> nodes =
                    root.findAccessibilityNodeInfosByText(text);

            if (nodes == null || nodes.isEmpty()) {
                return false;
            }

            for (AccessibilityNodeInfo node : nodes) {

                AccessibilityNodeInfo current = node;

                while (current != null) {

                    if (current.isClickable()) {
                        return current.performAction(
                                AccessibilityNodeInfo.ACTION_CLICK
                        );
                    }

                    current = current.getParent();
                }
            }

        } finally {
            root.recycle();
        }

        return false;
    }

    public static boolean typeText(String text) {

        if (instance == null || text == null) {
            return false;
        }

        AccessibilityNodeInfo root =
                instance.getRootInActiveWindow();

        if (root == null) {
            return false;
        }

        try {
            AccessibilityNodeInfo focused =
                    root.findFocus(
                            AccessibilityNodeInfo.FOCUS_INPUT
                    );

            if (focused == null) {
                return false;
            }

            Bundle arguments = new Bundle();

            arguments.putCharSequence(
                    AccessibilityNodeInfo
                            .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    text
            );

            return focused.performAction(
                    AccessibilityNodeInfo.ACTION_SET_TEXT,
                    arguments
            );

        } finally {
            root.recycle();
        }
    }

    public static boolean clickSearchField() {

        if (instance == null) {
            return false;
        }

        AccessibilityNodeInfo root =
                instance.getRootInActiveWindow();

        if (root == null) {
            return false;
        }

        try {
            AccessibilityNodeInfo node =
                    findEditableNode(root);

            if (node != null) {

                boolean clicked =
                        node.performAction(
                                AccessibilityNodeInfo.ACTION_CLICK
                        );

                node.performAction(
                        AccessibilityNodeInfo.ACTION_FOCUS
                );

                return clicked;
            }

        } finally {
            root.recycle();
        }

        return false;
    }

    private static AccessibilityNodeInfo findEditableNode(
            AccessibilityNodeInfo node
    ) {

        if (node == null) {
            return null;
        }

        if (node.isEditable()) {
            return node;
        }

        for (int i = 0;
             i < node.getChildCount();
             i++) {

            AccessibilityNodeInfo child =
                    node.getChild(i);

            AccessibilityNodeInfo result =
                    findEditableNode(child);

            if (result != null) {
                return result;
            }
        }

        return null;
    }

    public static boolean goBack() {

        return instance != null
                && instance.performGlobalAction(
                        GLOBAL_ACTION_BACK
                );
    }

    public static boolean goHome() {

        return instance != null
                && instance.performGlobalAction(
                        GLOBAL_ACTION_HOME
                );
    }

    public static boolean scrollDown() {

        if (instance == null) {
            return false;
        }

        AccessibilityNodeInfo root =
                instance.getRootInActiveWindow();

        if (root == null) {
            return false;
        }

        try {
            return findAndScroll(
                    root,
                    AccessibilityNodeInfo.ACTION_SCROLL_FORWARD
            );

        } finally {
            root.recycle();
        }
    }

    public static boolean scrollUp() {

        if (instance == null) {
            return false;
        }

        AccessibilityNodeInfo root =
                instance.getRootInActiveWindow();

        if (root == null) {
            return false;
        }

        try {
            return findAndScroll(
                    root,
                    AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
            );

        } finally {
            root.recycle();
        }
    }

    private static boolean findAndScroll(
            AccessibilityNodeInfo node,
            int action
    ) {

        if (node == null) {
            return false;
        }

        if (node.isScrollable()) {
            return node.performAction(action);
        }

        for (int i = 0;
             i < node.getChildCount();
             i++) {

            AccessibilityNodeInfo child =
                    node.getChild(i);

            if (findAndScroll(child, action)) {
                return true;
            }
        }

        return false;
    }
}
