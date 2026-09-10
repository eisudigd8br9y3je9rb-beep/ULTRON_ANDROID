package com.ultron.assistant.message;

import com.ultron.assistant.notification.UltronNotificationListener;

public final class MessageReader {

    private MessageReader() {}

    public static String latestMessage() {
        String result =
                UltronNotificationListener.getLatest();

        if (result == null || result.trim().isEmpty()) {
            return "I cannot read the latest message right now.";
        }

        return result;
    }
}
