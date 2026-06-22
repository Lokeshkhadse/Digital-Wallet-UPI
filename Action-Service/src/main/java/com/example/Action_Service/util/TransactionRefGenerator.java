package com.example.Action_Service.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class TransactionRefGenerator {

    public static String generateRefNo() {

        String time = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        String random = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 6);

        return "TXN-" + time + "-" + random;
    }
}
