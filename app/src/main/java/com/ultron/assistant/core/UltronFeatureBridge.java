package com.ultron.assistant.core;

import android.content.Context;

import com.ultron.assistant.accessibility.AccessibilityController;
import com.ultron.assistant.message.MessageReader;
import com.ultron.assistant.whatsapp.WhatsAppHelper;

public class UltronFeatureBridge {

    private final WhatsAppHelper whatsapp;

    public UltronFeatureBridge(Context context) {
        whatsapp = new WhatsAppHelper(context);
    }

    public boolean openWhatsApp() {
        return whatsapp.openWhatsApp();
    }

    public boolean openWhatsAppBusiness() {
        return whatsapp.openWhatsAppBusiness();
    }

    public boolean openWhatsAppChat(
            String number,
            String message
    ) {
        return whatsapp.openChat(number, message);
    }

    public String readLatestMessage() {
        return MessageReader.latestMessage();
    }

    public boolean accessibilityAvailable() {
        return AccessibilityController.isAvailable();
    }

    public String currentApp() {
        return AccessibilityController.currentPackage();
    }

    public boolean click(String text) {
        return AccessibilityController.click(text);
    }

    public boolean type(String field, String value) {
        return AccessibilityController.type(field, value);
    }

    public boolean scroll() {
        return AccessibilityController.scroll();
    }

    public String visibleText() {
        return AccessibilityController.visibleText();
    }
}
