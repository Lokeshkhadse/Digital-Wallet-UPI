package com.example.Query_Service.service.ai;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DateNormalizerService {


    public String normalize(String question){


        if(question==null || question.isBlank())
            return question;


        LocalDate today =
                LocalDate.now();


        String result = question;



        result=result.replaceAll(
                "(?i)today",
                today.toString()
        );



        result=result.replaceAll(
                "(?i)yesterday",
                today.minusDays(1).toString()
        );



        result=result.replaceAll(
                "(?i)tomorrow",
                today.plusDays(1).toString()
        );




        Pattern fullDate =
                Pattern.compile(
                        "(\\d{1,2})\\s+(January|February|March|April|May|June|July|August|September|October|November|December)\\s+(\\d{4})",
                        Pattern.CASE_INSENSITIVE);



        Matcher matcher =
                fullDate.matcher(result);



        while(matcher.find()){


            int day =
                    Integer.parseInt(
                            matcher.group(1));


            Month month =
                    Month.valueOf(
                            matcher.group(2)
                                    .toUpperCase());


            int year =
                    Integer.parseInt(
                            matcher.group(3));



            result=result.replace(
                    matcher.group(0),
                    LocalDate.of(
                            year,
                            month,
                            day
                    ).toString()
            );

        }



        return result;

    }

}
