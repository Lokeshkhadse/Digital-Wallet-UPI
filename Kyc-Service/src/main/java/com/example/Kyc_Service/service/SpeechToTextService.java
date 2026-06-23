//package com.example.Kyc_Service.service;
//
//import com.example.Kyc_Service.exception.KycException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.vosk.Model;
//import org.vosk.Recognizer;
//
//import javax.sound.sampled.*;
//import java.io.*;
//
//@Service
//@Slf4j
//public class SpeechToTextService {
//
//    @Value("${vosk.model-path}")
//    private String modelPath;
//
//    public String convertSpeechToText(File audioFile) {
//
//        try (
//                Model model = new Model(modelPath);
//                InputStream ais =
//                        AudioSystem.getAudioInputStream(audioFile)
//        ) {
//
//            AudioFormat format =
//                    ((AudioInputStream) ais).getFormat();
//
//            Recognizer recognizer =
//                    new Recognizer(
//                            model,
//                            format.getSampleRate()
//                    );
//
//            byte[] buffer =
//                    new byte[4096];
//
//            int bytesRead;
//
//            while ((bytesRead =
//                    ais.read(buffer)) >= 0) {
//
//                recognizer.acceptWaveForm(
//                        buffer,
//                        bytesRead
//                );
//            }
//
//            String result =
//                    recognizer.getFinalResult();
//
//            log.info(
//                    "Speech To Text Result : {}",
//                    result
//            );
//
//            return result;
//
//        } catch (Exception ex) {
//
//            throw new KycException(
//                    "Speech To Text Failed : "
//                            + ex.getMessage()
//            );
//        }
//    }
//}