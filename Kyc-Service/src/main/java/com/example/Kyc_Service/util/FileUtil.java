package com.example.Kyc_Service.util;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

public class FileUtil {

    private FileUtil() {
    }

    public static String convertToBase64(
            MultipartFile file) throws IOException {

        return Base64.getEncoder()
                .encodeToString(file.getBytes());
    }

}