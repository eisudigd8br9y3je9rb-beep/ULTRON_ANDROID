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
                        );

        if (bestParagraph.length() > 700) {

            bestParagraph =
                    bestParagraph.substring(
                            0,
                            700
                    );
        }

        return bestParagraph;
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
