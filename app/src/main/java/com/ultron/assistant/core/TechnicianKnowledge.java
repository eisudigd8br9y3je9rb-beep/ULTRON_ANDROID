package com.ultron.assistant.core;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

public class TechnicianKnowledge {

    private final Context context;
    private String knowledgeText = "";

    public TechnicianKnowledge(Context context) {
        this.context = context.getApplicationContext();
        loadKnowledge();
    }

    private void loadKnowledge() {
        try {
            InputStream inputStream =
                    context.getAssets().open(
                            "ac_technician_full.txt"
                    );

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    inputStream
                            )
                    );

            StringBuilder builder =
                    new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line)
                        .append("\n");
            }

            reader.close();

            knowledgeText =
                    builder.toString();

        } catch (Exception e) {
            knowledgeText = "";
        }
    }

    public String search(String question) {

        if (!isTechnicianQuestion(question)) {
            return null;
        }

        if (knowledgeText == null
                || knowledgeText.trim().isEmpty()) {

            return null;
        }

        if (question == null
                || question.trim().isEmpty()) {

            return null;
        }

        String lowerQuestion =
                question.toLowerCase(
                        Locale.getDefault()
                );

        String[] words =
                lowerQuestion.split("\\s+");

        ArrayList<String> keywords =
                new ArrayList<>();

        for (String word : words) {

            word = word.trim();

            if (word.length() >= 3
                    && !isCommonWord(word)) {

                keywords.add(word);
            }
        }

        if (keywords.isEmpty()) {
            return null;
        }

        String[] paragraphs =
                knowledgeText.split(
                        "\\n\\s*\\n"
                );

        String bestParagraph = null;
        int bestScore = 0;

        for (String paragraph : paragraphs) {

            String lowerParagraph =
                    paragraph.toLowerCase(
                            Locale.getDefault()
                    );

            int score = 0;

            for (String keyword : keywords) {

                if (lowerParagraph.contains(
                        keyword
                )) {
                    score++;
                }
            }

            if (score > bestScore) {

                bestScore = score;
                bestParagraph = paragraph;
            }
        }

        if (bestParagraph == null
                || bestScore == 0) {

            return null;
        }

        bestParagraph =
                bestParagraph.trim()
                        .replaceAll(
                                "\\s+",
                                " "
                        )
                        .replaceAll(
                                "\\.{3,}",
                                " "
                        )
                        .replaceAll(
                                "…+",
                                " "
                        )
                        .replaceAll(
                                "\\s{2,}",
                                " "
                        )
                        .trim();

        if (bestParagraph.length() > 700) {

            bestParagraph =
                    bestParagraph.substring(
                            0,
                            700
                    );
        }

        return bestParagraph;
    }

    private boolean isTechnicianQuestion(String question) {

        if (question == null || question.trim().isEmpty()) {
            return false;
        }

        String text =
                question.toLowerCase(Locale.getDefault());

        String[] applianceWords = {
                "ac",
                "air conditioner",
                "air conditioning",
                "fridge",
                "refrigerator",
                "freezer",
                "washing machine",
                "geyser",
                "water purifier",
                "purifier",
                "ro",
                "एसी",
                "एयर कंडीशनर",
                "फ्रिज",
                "रेफ्रिजरेटर",
                "फ्रीजर",
                "वॉशिंग मशीन",
                "गीजर",
                "आरओ",
                "प्यूरिफायर"
        };

        String[] technicalWords = {
                "compressor",
                "condenser",
                "evaporator",
                "refrigerant",
                "capacitor",
                "thermostat",
                "contactor",
                "relay",
                "pcb",
                "inverter",
                "voltage",
                "current",
                "ampere",
                "pressure",
                "leak",
                "leakage",
                "wiring",
                "coil",
                "blower",
                "fan motor",
                "thermistor",
                "defrost",
                "cooling",
                "gas charging",
                "repair",
                "fault",
                "error code",
                "multimeter",
                "compressor",
                "कंप्रेसर",
                "कंडेंसर",
                "इवैपोरेटर",
                "रेफ्रिजरेंट",
                "कैपेसिटर",
                "थर्मोस्टेट",
                "कॉन्टैक्टर",
                "रिले",
                "पीसीबी",
                "इन्वर्टर",
                "वोल्टेज",
                "करंट",
                "एम्पियर",
                "प्रेशर",
                "लीक",
                "लीकेज",
                "वायरिंग",
                "कॉइल",
                "ब्लोअर",
                "फैन मोटर",
                "थर्मिस्टर",
                "डिफ्रॉस्ट",
                "कूलिंग",
                "गैस चार्जिंग",
                "रिपेयर",
                "फॉल्ट",
                "एरर कोड",
                "मल्टीमीटर"
        };

        for (String word : applianceWords) {
            if (text.contains(word)) {
                return true;
            }
        }

        for (String word : technicalWords) {
            if (text.contains(word)) {
                return true;
            }
        }

        return false;
    }

    private boolean isCommonWord(
            String word
    ) {

        String[] commonWords = {
                "क्या",
                "है",
                "हैं",
                "का",
                "की",
                "के",
                "और",
                "में",
                "को",
                "से",
                "पर",
                "लिए",
                "करो",
                "बताओ",
                "tell",
                "about",
                "what",
                "is",
                "the",
                "and",
                "how",
                "why"
        };

        for (String common : commonWords) {

            if (word.equals(common)) {
                return true;
            }
        }

        return false;
    }
}
