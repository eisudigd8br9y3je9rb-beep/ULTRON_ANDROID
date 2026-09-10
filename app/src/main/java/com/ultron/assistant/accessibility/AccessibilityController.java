package com.ultron.assistant.accessibility;

public final class AccessibilityController {

    private AccessibilityController() {}

    public static boolean isAvailable() {
        return UltronAccessibilityService.getInstance() != null;
    }

    public static String currentPackage() {
        UltronAccessibilityService service =
                UltronAccessibilityService.getInstance();

        return service == null ? "" : service.getCurrentPackage();
    }

    public static boolean click(String text) {
        UltronAccessibilityService service =
                UltronAccessibilityService.getInstance();

        return service != null && service.clickText(text);
    }

    public static boolean type(String fieldText, String value) {
        UltronAccessibilityService service =
                UltronAccessibilityService.getInstance();

        return service != null && service.setText(fieldText, value);
    }

    public static boolean scroll() {
        UltronAccessibilityService service =
                UltronAccessibilityService.getInstance();

        return service != null && service.scrollForward();
    }

    public static String visibleText() {
        UltronAccessibilityService service =
                UltronAccessibilityService.getInstance();

        return service == null ? "" : service.readVisibleText();
    }
}
