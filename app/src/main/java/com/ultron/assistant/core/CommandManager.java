package com.ultron.assistant.core;

import java.util.Locale;

public class CommandManager {

    public enum CommandType {
        OPEN_YOUTUBE,
        OPEN_INSTAGRAM,
        OPEN_PUBG,
        OPEN_SETTINGS,
        GO_HOME,
        SEARCH_GOOGLE,
        CALL,
        SMS,
        OPEN_CAMERA,
        GREETING,
        HOW_ARE_YOU,
        WHO_ARE_YOU,
        WHAT_IS_YOUR_NAME,
        UNKNOWN
    }

    public CommandType parseCommand(String command) {

        if (command == null) {
            return CommandType.UNKNOWN;
        }

        String text = command.toLowerCase(Locale.getDefault()).trim();

        if (text.contains("hello ultron")
                || text.contains("hi ultron")
                || text.equals("hello")
                || text.equals("hi")
                || text.contains("हेलो अल्ट्रॉन")
                || text.contains("हाय अल्ट्रॉन")) {
            return CommandType.GREETING;
        }

        if (text.contains("how are you")
                || text.contains("कैसे हो")) {
            return CommandType.HOW_ARE_YOU;
        }

        if (text.contains("who are you")
                || text.contains("तुम कौन हो")) {
            return CommandType.WHO_ARE_YOU;
        }

        if (text.contains("what is your name")
                || text.contains("तुम्हारा नाम क्या है")
                || text.contains("आपका नाम क्या है")) {
            return CommandType.WHAT_IS_YOUR_NAME;
        }

        if (text.contains("youtube") || text.contains("यूट्यूब")) {
            return CommandType.OPEN_YOUTUBE;
        }

        if (text.contains("instagram") || text.contains("इंस्टाग्राम")) {
            return CommandType.OPEN_INSTAGRAM;
        }

        if (text.contains("pubg")) {
            return CommandType.OPEN_PUBG;
        }

        if (text.contains("settings") || text.contains("सेटिंग")) {
            return CommandType.OPEN_SETTINGS;
        }

        if (text.contains("home") || text.contains("होम")) {
            return CommandType.GO_HOME;
        }

        if (text.contains("google") || text.contains("गूगल")
                || text.contains("search") || text.contains("सर्च")) {
            return CommandType.SEARCH_GOOGLE;
        }

        if (text.contains("call") || text.contains("कॉल")
                || text.contains("फोन लगाओ")) {
            return CommandType.CALL;
        }

        if (text.contains("sms") || text.contains("message")
                || text.contains("मैसेज")) {
            return CommandType.SMS;
        }

        if (text.contains("camera") || text.contains("कैमरा")) {
            return CommandType.OPEN_CAMERA;
        }

        return CommandType.UNKNOWN;
    }
}
