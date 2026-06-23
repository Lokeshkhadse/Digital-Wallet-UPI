
package com.example.Kyc_Service.service;

import com.example.Kyc_Service.dto.ParsedVoiceDataDto;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class VoiceParserService {

    private static final Pattern NAME_PATTERN =
            Pattern.compile("my name is ([a-zA-Z ]+)", Pattern.CASE_INSENSITIVE);

    private static final Pattern DOB_NUMERIC =
            Pattern.compile("(\\d{1,2})[\\-/ ](\\d{1,2})[\\-/ ](\\d{4})");

    private static final Pattern DOB_TEXT =
            Pattern.compile(
                    "(january|february|march|april|may|june|july|august|september|october|november|december)\\s+(\\d{1,2})(st|nd|rd|th)?\\s+(\\d{4})",
                    Pattern.CASE_INSENSITIVE
            );

    private static final Pattern PAN_PATTERN =
            Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]");

    private static final Pattern AADHAAR_PATTERN =
            Pattern.compile("\\d{12}");

    public ParsedVoiceDataDto parseSpeechText(String text) {

        if (text == null || text.isBlank()) {
            return ParsedVoiceDataDto.builder().build();
        }

        return ParsedVoiceDataDto.builder()
                .name(extractName(text))
                .dob(extractDob(text))        // not comming
                .pan(extractPan(text))        // correct karo yah bhi
                .aadhaar(extractAadhaar(text))
                .build();
    }

    // ---------------- NAME ----------------
    private String extractName(String text) {
        Matcher m = NAME_PATTERN.matcher(text);
        return m.find() ? m.group(1).trim() : null;
    }

    // ---------------- DOB (UNCHANGED) ----------------
    private String extractDob(String text) {

        if (text == null) return null;

        String lower = text.toLowerCase();

        Matcher m1 = DOB_NUMERIC.matcher(lower);

        if (m1.find()) {
            return String.format("%02d-%02d-%s",
                    Integer.parseInt(m1.group(1)),
                    Integer.parseInt(m1.group(2)),
                    m1.group(3));
        }

        Matcher m2 = DOB_TEXT.matcher(lower);

        if (m2.find()) {

            int day = Integer.parseInt(m2.group(2));
            int month = monthToNumber(m2.group(1));
            String year = m2.group(4);

            return String.format("%02d-%02d-%s", day, month, year);
        }

        return null;
    }

    private int monthToNumber(String month) {
        return switch (month.toLowerCase()) {
            case "january" -> 1;
            case "february" -> 2;
            case "march" -> 3;
            case "april" -> 4;
            case "may" -> 5;
            case "june" -> 6;
            case "july" -> 7;
            case "august" -> 8;
            case "september" -> 9;
            case "october" -> 10;
            case "november" -> 11;
            case "december" -> 12;
            default -> 0;
        };
    }

    // ---------------- 🔥 ONLY PAN FIXED ----------------
    // ---------------- PAN ----------------
    private String extractPan(String text) {

        if (text == null) return null;

        // STEP 1: normalize ONLY PAN section safely
        String cleaned = text.toUpperCase()
                .replaceAll("[^A-Z0-9]", ""); // remove spaces & symbols

        // STEP 2: try strict PAN match
        Matcher m = Pattern
                .compile("[A-Z]{5}[0-9]{4}[A-Z]")
                .matcher(cleaned);

        if (m.find()) {
            return m.group();
        }

        return null;
    }

    // ---------------- AADHAAR (UNCHANGED) ----------------
    private String extractAadhaar(String text) {

        if (text == null) return null;

        Matcher m = AADHAAR_PATTERN.matcher(text);

        return m.find() ? m.group() : null;
    }
}