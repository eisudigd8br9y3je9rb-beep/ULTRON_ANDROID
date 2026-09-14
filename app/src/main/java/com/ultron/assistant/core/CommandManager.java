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
        WHO_AM_I,
        WHO_IS_YOUR_OWNER,
        SET_OWNER_NAME,

        ULTRON_ON,
        ULTRON_OFF,
        ULTRON_WAIT,
        ULTRON_WAKE,

        OPEN_ANY_APP,
        FLASHLIGHT_ON,
        FLASHLIGHT_OFF,
        VOLUME_UP,
        VOLUME_DOWN,
        OPEN_WIFI_SETTINGS,
        OPEN_BLUETOOTH_SETTINGS,
        GET_TIME,
        GET_DATE,
        GET_BATTERY,

        OPEN_SECURITY_SETTINGS,
        BRIGHTNESS_UP,
        BRIGHTNESS_DOWN,
        SILENT_MODE,
        RINGER_MODE,
        VIBRATE_MODE,
        OPEN_BATTERY_SAVER,
        OPEN_MOBILE_NETWORK_SETTINGS,
        OPEN_AIRPLANE_MODE_SETTINGS,
        OPEN_LOCATION_SETTINGS,
        OPEN_NOTIFICATION_SETTINGS,
        OPEN_DIALER,
        OPEN_SMS_APP,
        OPEN_WHATSAPP,
        OPEN_WHATSAPP_CHAT,
        WHATSAPP_MESSAGE,
        OPEN_PLAY_STORE,
        OPEN_MUSIC_APP,
        CHECK_WIFI_STATUS,
        GET_CHARGING_STATUS,
        OPEN_APP_SETTINGS,
        FEATURE_INFO,
        MEMORY_LAST,
        MEMORY_LAST_RESPONSE,
        MEMORY_CONTEXT,
        FOLLOW_UP,
        MEMORY_CLEAR,

        UNKNOWN
    }

    public CommandType parseCommand(String command) {

        if (command == null) {
            return CommandType.UNKNOWN;
        }

        String text =
                command.toLowerCase(Locale.getDefault()).trim();

        // ACTIVATION — check before normal greeting
        if (text.contains("hello ultron activate")
                || text.contains("hi ultron activate")
                || text.contains("ultron activate")
                || text.contains("activate ultron")
                || text.equals("activate")
                || text.contains("अल्ट्रॉन एक्टिवेट")
                || text.contains("अल्ट्रॉन सक्रिय")
                || text.contains("हेलो अल्ट्रॉन एक्टिवेट")) {
            return CommandType.ULTRON_ON;
        }

        // GREETING
        if (text.contains("hello ultron")
                || text.contains("hi ultron")
                || text.equals("hello")
                || text.equals("hi")
                || text.contains("हेलो अल्ट्रॉन")
                || text.contains("हाय अल्ट्रॉन")
                || text.contains("नमस्ते अल्ट्रॉन")) {

            return CommandType.GREETING;
        }

        // HOW ARE YOU
        if (text.contains("how are you")
                || text.contains("कैसे हो")
                || text.contains("कैसे हैं")) {

            return CommandType.HOW_ARE_YOU;
        }

        // WHO ARE YOU
        if (text.contains("who are you")
                || text.contains("तुम कौन हो")
                || text.contains("आप कौन हो")) {

            return CommandType.WHO_ARE_YOU;
        }

        // YOUR NAME
        if (text.contains("what is your name")
                || text.contains("तुम्हारा नाम क्या है")
                || text.contains("आपका नाम क्या है")
                || text.contains("तुम्हारा नाम")) {

            return CommandType.WHAT_IS_YOUR_NAME;
        }

        // WHO AM I
        if (text.contains("who am i")
                || text.contains("मैं कौन हूँ")
                || text.contains("मैं कौन हूं")
                || text.contains("मेरा नाम क्या है?")
                || text.contains("mera naam kya hai")
                || text.contains("mera naam kya h")
                || text.contains("what is my name")
                || text.contains("मेरा नाम क्या है")) {

            return CommandType.WHO_AM_I;
        }

        // OWNER
        if (text.contains("who is your owner")
                || text.contains("तुम्हारा मालिक कौन है")
                || text.contains("तुम्हारा ओनर कौन है")) {

            return CommandType.WHO_IS_YOUR_OWNER;
        }

        // SET OWNER NAME (a statement, not a question — the WHO_AM_I check
        // above already consumes every "...kya hai / kya h / क्या है"
        // question form, including the short "mera naam kya h")
        if (text.contains("my name is")
                || (text.contains("mera naam")
                        && text.contains("hai")
                        && !text.contains("kya"))
                || (text.contains("मेरा नाम")
                        && text.contains("है")
                        && !text.contains("क्या"))) {

            return CommandType.SET_OWNER_NAME;
        }

        // ULTRON ON
        if (text.contains("ultron on")
                || text.contains("ultron start")
                || text.contains("ultron activate")
                || text.contains("ultron चालू")
                || text.contains("ultron चालू हो जाओ")
                || text.contains("अल्ट्रॉन ऑन")
                || text.contains("अल्ट्रॉन चालू")
                || text.contains("अल्ट्रॉन शुरू हो जाओ")) {

            return CommandType.ULTRON_ON;
        }

        // ULTRON OFF
        if (text.contains("ultron off")
                || text.contains("ultron stop")
                || text.contains("ultron deactivate")
                || text.contains("ultron बंद")
                || text.contains("अल्ट्रॉन ऑफ")
                || text.contains("अल्ट्रॉन बंद")
                || text.contains("अल्ट्रॉन बंद हो जाओ")) {

            return CommandType.ULTRON_OFF;
        }

        // ULTRON WAIT / SILENT MODE
        if (text.contains("ultron wait")
                || text.contains("wait ultron")
                || text.contains("be quiet")
                || text.contains("stay quiet")
                || text.contains("चुप रहो")
                || text.contains("रुको")
                || text.contains("अल्ट्रॉन वेट करो")
                || text.contains("अल्ट्रॉन चुप रहो")
                || text.contains("अल्ट्रॉन रुको")) {

            return CommandType.ULTRON_WAIT;
        }

        // ULTRON WAKE / ACTIVE AGAIN
        if (text.contains("ultron wake")
                || text.contains("wake up ultron")
                || text.contains("ultron listen")
                || text.contains("start listening")
                || text.contains("अल्ट्रॉन उठो")
                || text.contains("अल्ट्रॉन सुनो")
                || text.contains("फिर से सुनो")
                || text.contains("वापस चालू हो जाओ")) {

            return CommandType.ULTRON_WAKE;
        }

        // FLASHLIGHT ON
        if (text.contains("torch on")
                || text.contains("flashlight on")
                || text.contains("torch चालू")
                || text.contains("टॉर्च चालू")
                || text.contains("टॉर्च ऑन")) {

            return CommandType.FLASHLIGHT_ON;
        }

        // FLASHLIGHT OFF
        if (text.contains("torch off")
                || text.contains("flashlight off")
                || text.contains("torch बंद")
                || text.contains("टॉर्च बंद")
                || text.contains("टॉर्च ऑफ")) {

            return CommandType.FLASHLIGHT_OFF;
        }

        // VOLUME UP
        if (text.contains("volume up")
                || text.contains("increase volume")
                || text.contains("आवाज़ बढ़ाओ")
                || text.contains("वॉल्यूम बढ़ाओ")) {

            return CommandType.VOLUME_UP;
        }

        // VOLUME DOWN
        if (text.contains("volume down")
                || text.contains("decrease volume")
                || text.contains("आवाज़ कम करो")
                || text.contains("वॉल्यूम कम करो")) {

            return CommandType.VOLUME_DOWN;
        }

        // WIFI
        // Keep WiFi status questions out of the settings command.
        if ((text.contains("wifi")
                || text.contains("wi-fi")
                || text.contains("वाईफाई")
                || text.contains("वाई फाई"))
                && !text.contains("wifi status")
                && !text.contains("wi-fi status")
                && !text.contains("wifi connected")
                && !text.contains("वाईफाई स्टेटस")
                && !text.contains("वाईफाई कनेक्ट")) {
            return CommandType.OPEN_WIFI_SETTINGS;
        }

        // BLUETOOTH
        if (text.contains("bluetooth")
                || text.contains("ब्लूटूथ")) {

            return CommandType.OPEN_BLUETOOTH_SETTINGS;
        }

        // TIME
        if (text.contains("what time")
                || text.contains("what is the time")
                || text.contains("current time")
                || text.contains("time now")
                || text.contains("time kya hai")
                || text.contains("समय क्या है")
                || text.contains("समय बताओ")
                || text.contains("टाइम क्या है")
                || text.contains("टाइम बताओ")
                || text.contains("कितने बजे")
                || text.contains("अभी कितने बजे")) {

            return CommandType.GET_TIME;
        }

        // DATE
        if (text.contains("what is the date")
                || text.contains("what date is it")
                || text.contains("what's the date")
                || text.contains("date today")
                || text.contains("today date")
                || text.contains("today's date")
                || text.contains("today date please")
                || text.contains("aaj ki date")
                || text.contains("aaj ki tareekh")
                || text.contains("आज की तारीख")
                || text.contains("आज की तारीख क्या है")
                || text.contains("आज कौन सी तारीख है")
                || text.contains("आज की डेट")) {

            return CommandType.GET_DATE;
        }

        // BATTERY
        // Keep battery-saver phrases out of the generic battery command.
        if ((text.contains("battery")
                || text.contains("बैटरी"))
                && !text.contains("battery saver")
                && !text.contains("battery saving")
                && !text.contains("बैटरी सेवर")
                && !text.contains("बैटरी सेविंग")) {

            return CommandType.GET_BATTERY;
        }

        // YOUTUBE
        if (text.contains("youtube")
                || text.contains("यूट्यूब")) {

            return CommandType.OPEN_YOUTUBE;
        }

        // INSTAGRAM
        if (text.contains("instagram")
                || text.contains("इंस्टाग्राम")) {

            return CommandType.OPEN_INSTAGRAM;
        }

        // PUBG
        if (text.contains("pubg")) {

            return CommandType.OPEN_PUBG;
        }

        // SETTINGS
        // Keep the more specific settings screens (security, app,
        // notification, mobile network, location) out of this generic one —
        // same style of fix already applied above to WIFI/BATTERY/SMS.
        if ((text.contains("settings")
                || text.contains("सेटिंग"))
                && !text.contains("security setting")
                && !text.contains("सिक्योरिटी सेटिंग")
                && !text.contains("app settings")
                && !text.contains("application settings")
                && !text.contains("ऐप सेटिंग")
                && !text.contains("एप्लिकेशन सेटिंग")
                && !text.contains("notification settings")
                && !text.contains("नोटिफिकेशन सेटिंग")
                && !text.contains("mobile data settings")
                && !text.contains("network settings")
                && !text.contains("location settings")
                && !text.contains("लोकेशन सेटिंग")) {

            return CommandType.OPEN_SETTINGS;
        }

        // HOME
        if (text.contains("go home")
                || text.equals("home")
                || text.contains("होम स्क्रीन")
                || text.equals("होम")) {

            return CommandType.GO_HOME;
        }

        // GOOGLE SEARCH
        if (text.contains("google")
                || text.contains("गूगल")
                || text.contains("search")
                || text.contains("सर्च")) {

            return CommandType.SEARCH_GOOGLE;
        }

        // CALL
        if (text.contains("call")
                || text.contains("कॉल")
                || text.contains("फोन लगाओ")) {

            return CommandType.CALL;
        }

        // SMS
        // Keep app-opening and WhatsApp message commands out of generic SMS.
        if ((text.contains("sms")
                || text.contains("message")
                || text.contains("मैसेज"))
                && !text.contains("open sms")
                && !text.contains("sms app")
                && !text.contains("message app")
                && !text.contains("मैसेज ऐप")
                && !text.contains("एसएमएस ऐप")
                && !text.contains("whatsapp")
                && !text.contains("व्हाट्सऐप")
                && !text.contains("व्हाट्सएप")) {
            return CommandType.SMS;
        }

        // CAMERA
        if (text.contains("camera")
                || text.contains("कैमरा")) {

            return CommandType.OPEN_CAMERA;
        }


        // SECURITY SETTINGS
        if (text.contains("security settings")
                || text.contains("security setting")
                || text.contains("security खोलो")
                || text.contains("सिक्योरिटी सेटिंग")) {
            return CommandType.OPEN_SECURITY_SETTINGS;
        }

        // BRIGHTNESS UP
        if (text.contains("brightness up")
                || text.contains("increase brightness")
                || text.contains("brightness बढ़ाओ")
                || text.contains("ब्राइटनेस बढ़ाओ")) {
            return CommandType.BRIGHTNESS_UP;
        }

        // BRIGHTNESS DOWN
        if (text.contains("brightness down")
                || text.contains("decrease brightness")
                || text.contains("brightness कम करो")
                || text.contains("ब्राइटनेस कम करो")) {
            return CommandType.BRIGHTNESS_DOWN;
        }

        // SILENT MODE
        if (text.contains("silent mode")
                || text.contains("silent कर दो")
                || text.contains("phone silent")
                || text.contains("साइलेंट मोड")
                || text.contains("फोन साइलेंट")) {
            return CommandType.SILENT_MODE;
        }

        // RINGER MODE
        if (text.contains("ringer mode")
                || text.contains("normal mode")
                || text.contains("sound on")
                || text.contains("रिंगर मोड")
                || text.contains("नॉर्मल मोड")) {
            return CommandType.RINGER_MODE;
        }

        // VIBRATE MODE
        if (text.contains("vibrate mode")
                || text.contains("vibration mode")
                || text.contains("vibrate कर दो")
                || text.contains("वाइब्रेट मोड")) {
            return CommandType.VIBRATE_MODE;
        }

        // BATTERY SAVER
        if (text.contains("battery saver")
                || text.contains("battery saving")
                || text.contains("बैटरी सेवर")
                || text.contains("बैटरी सेविंग")) {
            return CommandType.OPEN_BATTERY_SAVER;
        }

        // MOBILE NETWORK SETTINGS
        if (text.contains("mobile network")
                || text.contains("mobile data settings")
                || text.contains("network settings")
                || text.contains("मोबाइल नेटवर्क")
                || text.contains("मोबाइल डेटा")) {
            return CommandType.OPEN_MOBILE_NETWORK_SETTINGS;
        }

        // AIRPLANE MODE SETTINGS
        if (text.contains("airplane mode")
                || text.contains("flight mode")
                || text.contains("एयरप्लेन मोड")
                || text.contains("फ्लाइट मोड")) {
            return CommandType.OPEN_AIRPLANE_MODE_SETTINGS;
        }

        // LOCATION SETTINGS
        if (text.contains("location settings")
                || text.contains("location on")
                || text.contains("लोकेशन सेटिंग")
                || text.contains("लोकेशन ऑन")) {
            return CommandType.OPEN_LOCATION_SETTINGS;
        }

        // NOTIFICATION SETTINGS
        if (text.contains("notification settings")
                || text.contains("notifications")
                || text.contains("नोटिफिकेशन सेटिंग")
                || text.contains("नोटिफिकेशन")) {
            return CommandType.OPEN_NOTIFICATION_SETTINGS;
        }

        // OPEN DIALER
        if (text.contains("open dialer")
                || text.contains("dialer खोलो")
                || text.contains("फोन डायलर")
                || text.contains("डायलर खोलो")) {
            return CommandType.OPEN_DIALER;
        }

        // OPEN SMS APP
        if (text.contains("open sms")
                || text.contains("sms app")
                || text.contains("message app")
                || text.contains("मैसेज ऐप")
                || text.contains("एसएमएस ऐप")) {
            return CommandType.OPEN_SMS_APP;
        }

        // OPEN MUSIC APP
        if (text.contains("open music")
                || text.contains("music app")
                || text.contains("म्यूजिक ऐप")
                || text.contains("गाना ऐप")) {
            return CommandType.OPEN_MUSIC_APP;
        }

        // CHECK WIFI STATUS
        if (text.contains("wifi status")
                || text.contains("wi-fi status")
                || text.contains("wifi connected")
                || text.contains("वाईफाई स्टेटस")
                || text.contains("वाईफाई कनेक्ट")) {
            return CommandType.CHECK_WIFI_STATUS;
        }

        // CHARGING STATUS
        if (text.contains("charging status")
                || text.contains("am i charging")
                || text.contains("charging")
                || text.contains("चार्जिंग स्टेटस")
                || text.contains("चार्ज हो रहा है")) {
            return CommandType.GET_CHARGING_STATUS;
        }

        // OPEN APP SETTINGS
        if (text.contains("app settings")
                || text.contains("application settings")
                || text.contains("ऐप सेटिंग")
                || text.contains("एप्लिकेशन सेटिंग")) {
            return CommandType.OPEN_APP_SETTINGS;
        }



        // MEMORY - LAST RESPONSE
        if (text.contains("repeat that")
                || text.contains("repeat your answer")
                || text.contains("repeat last answer")
                || text.contains("say that again")
                || text.contains("what did you say")
                || text.contains("आपने क्या कहा")
                || text.contains("फिर से बताओ")
                || text.contains("फिर से बोलो")
                || text.contains("दोबारा बताओ")
                || text.contains("अपना जवाब दोबारा बताओ")) {
            return CommandType.MEMORY_LAST_RESPONSE;
        }

        // MEMORY - CONVERSATION CONTEXT
        if (text.contains("what were we talking about")
                || text.contains("what are we talking about")
                || text.contains("what did we talk about")
                || text.contains("recent conversation")
                || text.contains("conversation context")
                || text.contains("हम किस बारे में बात कर रहे थे")
                || text.contains("हम क्या बात कर रहे थे")
                || text.contains("हमारी पिछली बातचीत")
                || text.contains("पिछली बातचीत")) {
            return CommandType.MEMORY_CONTEXT;
        }

        // MEMORY - LAST CONVERSATION
        if (text.contains("remember what i asked")
                || text.contains("do you remember what i asked")
                || text.contains("what was my last question")
                || text.contains("what did i just ask")

                || text.contains("what did i ask")
                || text.contains("last question")
                || text.contains("previous question")
                || text.contains("पिछली बात")
                || text.contains("पिछला सवाल")
                || text.contains("मैंने अभी क्या पूछा")
                || text.contains("मैंने क्या पूछा")
                || text.contains("मेरी पिछली बात क्या थी")
                || text.contains("मेरा पिछला सवाल क्या था")
                || text.contains("क्या तुम्हें याद है मैंने क्या पूछा")) {
            return CommandType.MEMORY_LAST;
        }

        // FOLLOW-UP / CONTEXT QUESTION
        if (text.equals("solution")
                || text.equals("solution?")
                || text.equals("fix")
                || text.equals("fix?")
                || text.equals("reason")
                || text.equals("reason?")
                || text.equals("why")
                || text.equals("why?")
                || text.equals("how to fix")
                || text.equals("how to fix it")
                || text.equals("what is the solution")
                || text.contains("iska solution")
                || text.contains("iska reason")
                || text.contains("iska fix")
                || text.contains("iska kya solution")
                || text.contains("इसका solution")
                || text.contains("इसका सॉल्यूशन")
                || text.contains("इसका समाधान")
                || text.contains("इसका कारण")
                || text.contains("इसका reason")
                || text.contains("इसे कैसे ठीक करें")
                || text.contains("इसे कैसे ठीक करे")
                || text.contains("इसे कैसे ठीक करें")
                || text.contains("फिर क्या")
                || text.contains("और इसका solution")
                || text.contains("और इसका समाधान")
                || text.contains("और क्या करना है")) {
            return CommandType.FOLLOW_UP;
        }

        // MEMORY - CLEAR
        if (text.contains("clear memory")
                || text.contains("forget memory")
                || text.contains("delete memory")
                || text.contains("मेमोरी साफ")
                || text.contains("मेमोरी क्लियर")
                || text.contains("याददाश्त साफ")
                || text.contains("सब भूल जाओ")
                || text.contains("मेरी मेमोरी मिटा दो")
                || text.contains("मेरी याददाश्त मिटा दो")
                || text.contains("सारी मेमोरी साफ करो")) {
            return CommandType.MEMORY_CLEAR;
        }

        // FEATURE INFORMATION
        if (text.contains("what can you do")
                || text.contains("what are your features")
                || text.contains("tell me your features")
                || text.contains("your features")
                || text.contains("तुम क्या कर सकते हो")
                || text.contains("अपने फीचर बताओ")
                || text.contains("अपने फीचर्स बताओ")
                || text.contains("तुम्हारे फीचर क्या हैं")
                || text.contains("तुम क्या क्या कर सकते हो")) {

            return CommandType.FEATURE_INFO;
        }

        // OPEN WHATSAPP
        if (text.contains("open whatsapp")
                || text.contains("launch whatsapp")
                || text.contains("start whatsapp")
                || text.contains("whatsapp खोलो")
                || text.contains("व्हाट्सऐप खोलो")
                || text.contains("व्हाट्सएप खोलो")) {
            return CommandType.OPEN_WHATSAPP;
        }

        // WHATSAPP MESSAGE BY NUMBER
        if ((text.contains("whatsapp") || text.contains("व्हाट्सएप") || text.contains("व्हाट्सऐप"))
                && text.matches(".*[0-9]{7,}.*")
                && (text.contains("message")
                    || text.contains("msg")
                    || text.contains("send")
                    || text.contains("मैसेज")
                    || text.contains("संदेश")
                    || text.contains("भेजो")
                    || text.contains("भेज"))) {
            return CommandType.WHATSAPP_MESSAGE;
        }

        // OPEN WHATSAPP CHAT BY NUMBER
        if ((text.contains("whatsapp") || text.contains("व्हाट्सएप") || text.contains("व्हाट्सऐप"))
                && text.matches(".*[0-9]{7,}.*")
                && (text.contains("chat")
                    || text.contains("open")
                    || text.contains("चैट")
                    || text.contains("खोलो")
                    || text.contains("ओपन"))) {
            return CommandType.OPEN_WHATSAPP_CHAT;
        }

        // OPEN PLAY STORE
        if (text.contains("open play store")
                || text.contains("play store खोलो")
                || text.contains("play store open")
                || text.contains("प्ले स्टोर खोलो")
                || text.contains("प्ले स्टोर ओपन करो")) {
            return CommandType.OPEN_PLAY_STORE;
        }

        // OPEN ANY APP
        if (text.startsWith("open ")
                || text.startsWith("launch ")
                || text.startsWith("start ")
                || text.contains(" खोलो")
                || text.contains(" खोल दो")
                || text.contains(" ओपन करो")
                || text.contains(" चालू करो")) {

            return CommandType.OPEN_ANY_APP;
        }

        return CommandType.UNKNOWN;
    }
}
