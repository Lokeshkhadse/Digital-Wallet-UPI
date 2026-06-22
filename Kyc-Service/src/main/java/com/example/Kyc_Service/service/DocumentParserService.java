//
//package com.example.Kyc_Service.service;
//
//import com.example.Kyc_Service.dto.ParsedDocumentDto;
//import com.example.Kyc_Service.enums.DocumentType;
//import org.springframework.stereotype.Service;
//
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Service
//public class DocumentParserService {
//
//    public ParsedDocumentDto parseDocument(
//            String ocrText,
//            DocumentType documentType) {
//
//        String name;
//        String documentNumber;
//
//        if (documentType == DocumentType.PAN) {
//
//            name = extractPanName(ocrText);
//            documentNumber = extractPanNumber(ocrText);
//
//        } else {
//
//            name = extractAadhaarName(ocrText);
//            documentNumber = extractAadhaarNumber(ocrText);
//        }
//
//        String dob = extractDob(ocrText);
//
//        return ParsedDocumentDto.builder()
//                .name(name)
//                .dob(dob)
//                .documentNumber(documentNumber)
//                .build();
//    }
//
//    private String extractPanNumber(String text) {
//
//        Pattern pattern =
//                Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]");
//
//        Matcher matcher =
//                pattern.matcher(text.toUpperCase());
//
//        return matcher.find()
//                ? matcher.group()
//                : null;
//    }
//
//    private String extractAadhaarNumber(String text) {
//
//        Pattern pattern =
//                Pattern.compile("(\\d{4}\\s?\\d{4}\\s?\\d{4})");
//
//        Matcher matcher =
//                pattern.matcher(text);
//
//        if (matcher.find()) {
//
//            return matcher.group()
//                    .replaceAll("\\s", "");
//        }
//
//        return null;
//    }
//
//    private String extractDob(String text) {
//
//        Pattern pattern =
//                Pattern.compile(
//                        "\d{2}[-/]\d{2}[-/]\d{4}"
//                );
//
//        Matcher matcher =
//                pattern.matcher(text);
//
//        return matcher.find()
//                ? matcher.group()
//                : null;
//    }
//
//    private String extractPanName(String text) {
//
//        String[] lines = text.split("\\r?\\n");
//
//        for (int i = 0; i < lines.length; i++) {
//
//            String line = lines[i]
//                    .trim()
//                    .toUpperCase();
//
//            if (line.matches("[A-Z]{5}[0-9]{4}[A-Z]")) {
//
//                if (i + 1 < lines.length) {
//
//                    String name =
//                            lines[i + 1]
//                                    .trim()
//                                    .replaceAll("[^A-Za-z ]", "");
//
//                    if (!name.isBlank()) {
//                        return name;
//                    }
//                }
//            }
//        }
//
//        return extractFallbackName(text);
//    }
//
//    private String extractAadhaarName(String text) {
//
//        String[] lines = text.split("\\r?\\n");
//
//        for (int i = 0; i < lines.length; i++) {
//
//            String line =
//                    lines[i].trim();
//
//            if (line.equalsIgnoreCase("GOVERNMENT OF INDIA")) {
//
//                if (i + 1 < lines.length) {
//
//                    String name =
//                            lines[i + 1]
//                                    .trim()
//                                    .replaceAll("[^A-Za-z ]", "");
//
//                    if (!name.isBlank()) {
//                        return name;
//                    }
//                }
//            }
//        }
//
//        return extractFallbackName(text);
//    }
//
//    private String extractFallbackName(String text) {
//
//        String[] lines =
//                text.split("\\r?\\n");
//
//        for (String line : lines) {
//
//            line = line.trim();
//
//            if (line.matches("^[A-Z ]{5,}$")
//                    && !line.contains("INCOME TAX")
//                    && !line.contains("DEPARTMENT")
//                    && !line.contains("GOVERNMENT")
//                    && !line.contains("INDIA")
//                    && !line.contains("MALE")
//                    && !line.contains("FEMALE")
//                    && !line.contains("PERMANENT")
//                    && !line.contains("ACCOUNT")
//                    && !line.contains("CARD")) {
//
//                return line;
//            }
//        }
//
//        return null;
//    }
//}

package com.example.Kyc_Service.service;

import com.example.Kyc_Service.dto.ParsedDocumentDto;
import com.example.Kyc_Service.enums.DocumentType;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DocumentParserService {

    public ParsedDocumentDto parseDocument(
            String ocrText,
            DocumentType documentType) {

        String name;
        String documentNumber;

        if (documentType == DocumentType.PAN) {

            name = extractPanName(ocrText);
            documentNumber = extractPanNumber(ocrText);

        } else {

            name = extractAadhaarName(ocrText);
            documentNumber = extractAadhaarNumber(ocrText);
        }

        String dob = extractDob(ocrText);

        return ParsedDocumentDto.builder()
                .name(name)
                .dob(dob)
                .documentNumber(documentNumber)
                .build();
    }

    private String extractPanNumber(String text) {

        Matcher matcher =
                Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]")
                        .matcher(text.toUpperCase());

        return matcher.find()
                ? matcher.group()
                : null;
    }

    private String extractAadhaarNumber(String text) {

        Matcher matcher =
                Pattern.compile("(\\d{4}\\s?\\d{4}\\s?\\d{4})")
                        .matcher(text);

        if (matcher.find()) {

            return matcher.group()
                    .replaceAll("\\s", "");
        }

        return null;
    }

    private String extractDob(String text) {

        Matcher matcher =
                Pattern.compile("\\d{2}[-/]\\d{2}[-/]\\d{4}")
                        .matcher(text);

        return matcher.find()
                ? matcher.group()
                : null;
    }

    private String extractPanName(String text) {

        String[] lines = text.split("\\r?\\n");

        String panNumber = extractPanNumber(text);

        if (panNumber == null) {
            return null;
        }

        for (int i = 0; i < lines.length; i++) {

            String line = cleanText(lines[i]);

            if (!line.contains(panNumber)) {
                continue;
            }

            // PAN line mil gayi
            // next non-empty line = applicant name

            for (int j = i + 1; j < lines.length; j++) {

                String candidate =
                        cleanText(lines[j]);

                if (candidate.isBlank()) {
                    continue;
                }

                candidate = candidate
                        .replaceAll("^(FP|EP|PP|RP)\\s+", "")
                        .trim();

                // reject common headings
                if (candidate.contains("FATHER")
                        || candidate.contains("DATE")
                        || candidate.contains("BIRTH")
                        || candidate.contains("SIGNATURE")
                        || candidate.contains("GOVERNMENT")
                        || candidate.contains("INDIA")) {
                    continue;
                }

                // no numbers allowed in name
                if (candidate.matches(".*\\d.*")) {
                    continue;
                }

                return normalizeName(candidate);
            }
        }

        return null;
    }


    private String extractAadhaarName(String text) {

        String[] lines =
                text.split("\\r?\\n");

        boolean govtFound = false;

        for (String line : lines) {

            String cleaned =
                    cleanText(line);

            if (cleaned.contains("GOVERNMENT OF INDIA")) {

                govtFound = true;
                continue;
            }

            if (govtFound) {

                if (cleaned.contains("MALE")
                        || cleaned.contains("FEMALE")
                        || cleaned.matches(".*\\d{2}[-/]\\d{2}[-/]\\d{4}.*")) {

                    break;
                }

                String candidate =
                        extractRealName(cleaned);

                if (candidate != null) {
                    return candidate;
                }
            }
        }

        return extractFallbackName(text);
    }

    private String extractFallbackName(String text) {

        String[] lines =
                text.split("\\r?\\n");

        for (String line : lines) {

            String cleaned =
                    cleanText(line);

            String candidate =
                    extractRealName(cleaned);

            if (candidate != null) {
                return candidate;
            }
        }

        return null;
    }

    private String extractRealName(String text) {

        if (text == null || text.isBlank()) {
            return null;
        }

        text = text.toUpperCase();

        /*
         * OCR garbage removal
         * FP SAMARTH SHARMA
         * EP SAMARTH SHARMA
         * PP SAMARTH SHARMA
         */
        text = text.replaceAll(
                "^(FP|EP|PP|RP|NP|MP|SP|LP)[\\s\\-\\.]+",
                ""
        );

        text = text.replaceAll(
                "[^A-Z ]",
                " "
        );

        text = text.replaceAll(
                "\\s+",
                " "
        ).trim();

        if (!isValidName(text)) {
            return null;
        }

        String[] words =
                text.split("\\s+");

        if (words.length < 2) {
            return null;
        }

        return normalizeName(text);
    }

    private boolean isValidName(String text) {

        if (text == null || text.isBlank()) {
            return false;
        }

        if (text.length() < 5) {
            return false;
        }

        if (text.matches(".*\\d{2,}.*")) {
            return false;
        }

        return !text.contains("GOVERNMENT")
                && !text.contains("INDIA")
                && !text.contains("AADHAAR")
                && !text.contains("INCOME")
                && !text.contains("TAX")
                && !text.contains("DEPARTMENT")
                && !text.contains("CARD")
                && !text.contains("PERMANENT")
                && !text.contains("ACCOUNT")
                && !text.contains("NUMBER")
                && !text.contains("FATHER")
                && !text.contains("DATE")
                && !text.contains("BIRTH")
                && !text.contains("MALE")
                && !text.contains("FEMALE")
                && !text.contains("SIGNATURE")
                && !text.contains("GOVT");
    }

    private String cleanText(String text) {

        return text
                .replaceAll("[^A-Za-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim()
                .toUpperCase();
    }

    private String normalizeName(String name) {

        name = name.toUpperCase();

        // OCR garbage words remove
        name = name.replaceAll("\\bAE\\b", "");
        name = name.replaceAll("\\bAL\\b", "");
        name = name.replaceAll("\\bAI\\b", "");

        name = name.replaceAll("[^A-Z ]", " ");
        name = name.replaceAll("\\s+", " ").trim();

        StringBuilder sb = new StringBuilder();

        for (String word : name.split("\\s+")) {

            if (word.length() < 2) {
                continue;
            }

            sb.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase())
                    .append(" ");
        }

        return sb.toString().trim();
    }
}