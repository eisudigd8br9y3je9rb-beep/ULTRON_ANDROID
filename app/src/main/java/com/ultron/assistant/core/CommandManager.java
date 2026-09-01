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
        UNKNOWN
    }

    public CommandType parseCommand(String command) {

        if (command == null) {
            return CommandType.UNKNOWN;
        }

        String text = command.toLowerCase(Locale.getDefault()).trim();

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
