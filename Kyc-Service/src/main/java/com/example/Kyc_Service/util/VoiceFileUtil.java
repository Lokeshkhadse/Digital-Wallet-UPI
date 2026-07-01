package com.example.Kyc_Service.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;

public class VoiceFileUtil {

    private VoiceFileUtil() {
    }

    public static File convertBase64ToWavFile(
            String base64)
            throws Exception {

        byte[] decoded =
                Base64.getDecoder()
                        .decode(base64);

        File tempFile =
                File.createTempFile(
                        "voice_",
                        ".wav"
                );

        try (
                FileOutputStream fos =
                        new FileOutputStream(tempFile)
        ) {

            fos.write(decoded);
        }

        return tempFile;
    }
}