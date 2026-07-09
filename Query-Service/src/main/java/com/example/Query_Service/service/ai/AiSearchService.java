//package com.example.Query_Service.service.ai;
//
//import com.example.Query_Service.dto.UserBankDetailsResponse;
//import com.example.Query_Service.dto.ai.AiSearchRequest;
//import com.example.Query_Service.dto.ai.AiSearchResponse;
//import com.example.Query_Service.enums.TransactionStatus;
//import com.example.Query_Service.enums.TransactionType;
//import com.example.Query_Service.service.*;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//
//@Service
//@RequiredArgsConstructor
//public class AiSearchService {
//
//    private final ChatClient.Builder chatClientBuilder;
//    private final AiPromptService promptService;
//    private final ObjectMapper objectMapper;
//    private final DateNormalizerService dateNormalizerService;
//
//    private final TransactionService transactionService;
//    private final DepositService depositService;
//    private final WithdrawService withdrawService;
//    private final DashboardService dashboardService;
//    private final UserBankBalanceService balanceService;
//    private final UserBankDetailsService bankDetailsService;
//
//    public Object understandQuestion(
//            AiSearchRequest request) {
//
//        ChatClient chatClient =
//                chatClientBuilder.build();  //Ye Gemini AI ke saath communicate karne wala object hai.
//
//        //date format changing (if question contain date)
//        String normalizedQuestion =
//                dateNormalizerService.normalize(
//                        request.getQuestion());
//
//
//        String prompt =
//                promptService.buildPrompt(
//                        normalizedQuestion);
//
//        String aiResponse;
//        try {
//            aiResponse =
//                    chatClient.prompt()  //Create Prompt Request
//                            .user(prompt) //Yaha tum actual message bhejte ho.
//                            .call()  //Ye actual gemini server ki API call karta hai.
//                            .content(); //Ye Gemini ke response me se sirf text nikalta hai.
//        }
//        catch(Exception e){
//
//            throw new RuntimeException(
//                    "AI service unavailable. Try again later."
//            );
//
//        }
//
//        System.out.println("========== PROMPT ==========");
//        System.out.println(prompt);
//
//        System.out.println("========== AI RESPONSE ==========");
//        System.out.println(aiResponse);
//
//        try {
//
//            AiSearchResponse response =
//                    objectMapper.readValue(
//                            aiResponse,
//                            AiSearchResponse.class);
//
//            return executeIntent(
//                    request.getUserId(),
//                    response);
//
//        } catch (Exception e) {
//
//            throw new RuntimeException(
//                    "Unable to parse AI response",
//                    e);
//        }
//
//    }
//
//    private Object executeIntent(
//            Long userId,
//            AiSearchResponse ai) {
//
//        if (ai.getIntent() == null) {
//            return "Unable to understand request";
//        }
//
//
//        switch (ai.getIntent()) {
//
//            // =====================================================
//            // TRANSACTIONS
//            // =====================================================
//
//            case ALL_TRANSACTIONS:
//
//                return transactionService.getUserTransactions(
//                        userId,
//                        0,
//                        ai.getLimit() == null ? 10 : ai.getLimit());
//
//            case LAST_TRANSACTION:
//
//                return transactionService
//                        .miniStatement(userId)
//                        .stream()
//                        .limit(1)
//                        .toList();
//
//            case LAST_N_TRANSACTIONS:
//
//                return transactionService
//                        .miniStatement(userId)
//                        .stream()
//                        .limit(ai.getLimit() == null ? 5 : ai.getLimit())
//                        .toList();
//
//            case TRANSACTIONS_BY_STATUS:
//
//                if (ai.getStatus() == null) {
//                    return "Transaction status is missing";
//                }
//
//                return transactionService.getByStatus(
//                        TransactionStatus.valueOf(
//                                ai.getStatus().toUpperCase()),
//                        0,
//                        10);
//
//            case TRANSACTION_BY_REFERENCE:
//
//                return transactionService.getByRefNo(
//                        ai.getReferenceNumber());
//
//            case TRANSACTIONS_BY_DATE_RANGE:
//
//                return transactionService.getUserTransactionsByDateRange(
//                        userId,
//                        java.time.LocalDate.parse(ai.getFromDate()).atStartOfDay(),
//                        java.time.LocalDate.parse(ai.getToDate()).atTime(23,59,59),
//                        0,
//                        10);
//
//            case TRANSACTIONS_BY_AMOUNT_RANGE:
//
//                return transactionService.getUserTransactionsByAmountRange(
//                        userId,
//                        new java.math.BigDecimal(ai.getMinAmount()),
//                        new java.math.BigDecimal(ai.getMaxAmount()),
//                        0,
//                        10);
//
//
//            case TRANSACTIONS_BY_TYPE:
//
//               return " write logic";
//
//
//            // =====================================================
//            // DEPOSIT
//            // =====================================================
//
//            case ALL_DEPOSITS:
//
//                return depositService.getUserDeposits(
//                        userId,
//                        0,
//                        ai.getLimit() == null ? 10 : ai.getLimit());
//
//            case DEPOSITS_BY_STATUS:
//
//                if(ai.getStatus()==null){
//                    return "Deposit status missing";
//                }
//
//                return depositService.getDepositsByStatus(
//                        TransactionStatus.valueOf(ai.getStatus().toUpperCase()),
//                        0,
//                        10);
//
//            case DEPOSITS_BY_DATE_RANGE:
//
//                return depositService.getDepositsByDateRange(
//
//                        LocalDate.parse(ai.getFromDate())
//                                .atStartOfDay(),
//
//                        LocalDate.parse(ai.getToDate())
//                                .atTime(23,59,59),
//
//                        0,
//                        10
//                );
//
//            case DEPOSITS_BY_AMOUNT_RANGE:
//
//                return depositService.getDepositsByAmountRange(
//
//                        new BigDecimal(ai.getMinAmount()),
//
//                        new BigDecimal(ai.getMaxAmount()),
//
//                        0,
//                        10
//                );
//
//
//            // =====================================================
//            // WITHDRAW
//            // =====================================================
//
//            case ALL_WITHDRAWALS:
//
//                return withdrawService.getUserWithdrawals(
//                        userId,
//                        0,
//                        ai.getLimit() == null ? 10 : ai.getLimit());
//
//            case WITHDRAWALS_BY_STATUS:
//
//                return withdrawService.getWithdrawalsByStatus(
//
//                        TransactionStatus.valueOf(
//                                ai.getStatus().toUpperCase()),
//
//                        0,
//                        10
//                );
//
//            case WITHDRAWALS_BY_DATE_RANGE:
//
//                return withdrawService.getWithdrawalsByDate(
//
//                        LocalDate.parse(ai.getFromDate())
//                                .atStartOfDay(),
//
//                        LocalDate.parse(ai.getToDate())
//                                .atTime(23,59,59),
//
//                        0,
//                        10
//                );
//
//            case WITHDRAWALS_BY_AMOUNT_RANGE:
//
//                return withdrawService.getWithdrawalsByAmount(
//
//                        new BigDecimal(ai.getMinAmount()),
//
//                        new BigDecimal(ai.getMaxAmount()),
//
//                        0,
//                        10
//                );
//
//
//            // =====================================================
//            // DASHBOARD
//            // =====================================================
//
//            case DASHBOARD:
//
//                return dashboardService.getUserDashboard(
//                        userId);
//
//            case ACCOUNT_DASHBOARD:
//
//                return "Account Dashboard";
//
//
//            // =====================================================
//            // ACCOUNT
//            // =====================================================
//
//            case BALANCE:
//
//                return balanceService.getBalanceByUserId(
//                        userId);
//
//            case ACCOUNT_DETAILS:
//
//                return bankDetailsService.getBanksByUserId(
//                        userId);
//
//
//            // =====================================================
//            // DEFAULT
//            // =====================================================
//
//            default:
//
//                return "Sorry, I could not understand your request.";
//        }
//    }
//
//}

package com.example.Query_Service.service.ai;

import com.example.Query_Service.dto.ai.AiSearchRequest;
import com.example.Query_Service.dto.ai.AiSearchResponse;
import com.example.Query_Service.enums.TransactionStatus;
import com.example.Query_Service.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AiSearchService {


    private final ChatClient.Builder chatClientBuilder;

    private final AiPromptService promptService;

    private final ObjectMapper objectMapper;

    private final DateNormalizerService dateNormalizerService;


    private final TransactionService transactionService;
    private final DepositService depositService;
    private final WithdrawService withdrawService;
    private final DashboardService dashboardService;
    private final UserBankBalanceService balanceService;
    private final UserBankDetailsService bankDetailsService;



    public Object understandQuestion(
            AiSearchRequest request) {


        ChatClient chatClient =
                chatClientBuilder.build();


        String normalizedQuestion =
                dateNormalizerService.normalize(
                        request.getQuestion()
                );


        String prompt =
                promptService.buildPrompt(
                        normalizedQuestion
                );


        String aiResponse;


        try {

            aiResponse =
                    chatClient.prompt()
                            .user(prompt)
                            .call()
                            .content();


        }catch(Exception e){

            return "AI service unavailable. Please try again later.";

        }



        try {


            AiSearchResponse response =
                    objectMapper.readValue(
                            aiResponse,
                            AiSearchResponse.class
                    );


            return executeIntent(
                    request.getUserId(),
                    response
            );


        }catch(Exception e){

            return "Unable to understand request.";

        }

    }





    private Object executeIntent(
            Long userId,
            AiSearchResponse ai){



        if(ai == null || ai.getIntent()==null){

            return "Invalid request";

        }



        try {


            switch(ai.getIntent()){



                // ===============================
                // TRANSACTION
                // ===============================


                case ALL_TRANSACTIONS:


                    return transactionService.getUserTransactions(
                            userId,
                            0,
                            safeLimit(ai)
                    );



                case LAST_TRANSACTION:


                    return transactionService
                            .miniStatement(userId)
                            .stream()
                            .limit(1)
                            .toList();



                case LAST_N_TRANSACTIONS:


                    return transactionService
                            .miniStatement(userId)
                            .stream()
                            .limit(
                                    ai.getLimit()==null?
                                            5:
                                            ai.getLimit()
                            )
                            .toList();





                case TRANSACTIONS_BY_STATUS:


                    TransactionStatus status =
                            safeStatus(
                                    ai.getStatus()
                            );


                    if(status==null){

                        return "Status missing";

                    }


                    return transactionService.getByStatus(
                            status,
                            0,
                            safeLimit(ai)
                    );





                case TRANSACTION_BY_REFERENCE:


                    if(ai.getReferenceNumber()==null){

                        return "Reference number missing";

                    }


                    return transactionService.getByRefNo(
                            ai.getReferenceNumber()
                    );





                case TRANSACTIONS_BY_DATE_RANGE:


                    if(ai.getFromDate()==null ||
                            ai.getToDate()==null){

                        return "Date range missing";

                    }



                    return transactionService
                            .getUserTransactionsByDateRange(
                                    userId,
                                    parseStart(ai.getFromDate()),
                                    parseEnd(ai.getToDate()),
                                    0,
                                    safeLimit(ai)
                            );





                case TRANSACTIONS_BY_AMOUNT_RANGE:


                    BigDecimal min =
                            parseAmount(
                                    ai.getMinAmount()
                            );


                    BigDecimal max =
                            parseAmount(
                                    ai.getMaxAmount()
                            );



                    return transactionService
                            .getUserTransactionsByAmountRange(
                                    userId,
                                    min,
                                    max,
                                    0,
                                    safeLimit(ai)
                            );






                // ===============================
                // DEPOSIT
                // ===============================


                case ALL_DEPOSITS:


                    return depositService.getUserDeposits(
                            userId,
                            0,
                            safeLimit(ai)
                    );





                case DEPOSITS_BY_STATUS:


                    TransactionStatus depositStatus =
                            safeStatus(ai.getStatus());


                    if(depositStatus==null){

                        return "Deposit status missing";

                    }


                    return depositService.getDepositsByStatus(
                            depositStatus,
                            0,
                            safeLimit(ai)
                    );






                case DEPOSITS_BY_DATE_RANGE:


                    if(ai.getFromDate()==null ||
                            ai.getToDate()==null){

                        return "Date missing";

                    }


                    return depositService
                            .getDepositsByDateRange(
                                    parseStart(ai.getFromDate()),
                                    parseEnd(ai.getToDate()),
                                    0,
                                    safeLimit(ai)
                            );






                case DEPOSITS_BY_AMOUNT_RANGE:


                    return depositService
                            .getDepositsByAmountRange(
                                    parseAmount(ai.getMinAmount()),
                                    parseAmount(ai.getMaxAmount()),
                                    0,
                                    safeLimit(ai)
                            );






                // ===============================
                // WITHDRAW
                // ===============================


                case ALL_WITHDRAWALS:


                    return withdrawService.getUserWithdrawals(
                            userId,
                            0,
                            safeLimit(ai)
                    );






                case WITHDRAWALS_BY_STATUS:


                    TransactionStatus withdrawStatus =
                            safeStatus(ai.getStatus());


                    if(withdrawStatus==null){

                        return "Withdraw status missing";

                    }



                    return withdrawService
                            .getWithdrawalsByStatus(
                                    withdrawStatus,
                                    0,
                                    safeLimit(ai)
                            );





                case WITHDRAWALS_BY_DATE_RANGE:


                    return withdrawService
                            .getWithdrawalsByDate(
                                    parseStart(ai.getFromDate()),
                                    parseEnd(ai.getToDate()),
                                    0,
                                    safeLimit(ai)
                            );






                case WITHDRAWALS_BY_AMOUNT_RANGE:


                    return withdrawService
                            .getWithdrawalsByAmount(
                                    parseAmount(ai.getMinAmount()),
                                    parseAmount(ai.getMaxAmount()),
                                    0,
                                    safeLimit(ai)
                            );







                // ===============================
                // DASHBOARD
                // ===============================



                case DASHBOARD:


                    return dashboardService
                            .getUserDashboard(userId);





                // ===============================
                // ACCOUNT
                // ===============================



                case BALANCE:


                    return balanceService
                            .getBalanceByUserId(userId);





                case ACCOUNT_DETAILS:


                    return bankDetailsService
                            .getBanksByUserId(userId);




                default:


                    return "Sorry, I could not understand your request.";

            }


        }catch(Exception e){


            return "Unable to process your request.";

        }


    }







    private int safeLimit(AiSearchResponse ai){


        return ai.getLimit()==null?
                10:
                ai.getLimit();

    }





    private TransactionStatus safeStatus(
            String status){


        if(status==null)
            return null;


        try{

            return TransactionStatus
                    .valueOf(
                            status.toUpperCase()
                    );

        }catch(Exception e){

            return null;

        }

    }






    private BigDecimal parseAmount(
            String value){


        if(value==null || value.isBlank()){

            return BigDecimal.ZERO;

        }


        try{

            return new BigDecimal(value);

        }catch(Exception e){

            return BigDecimal.ZERO;

        }

    }







    private LocalDateTime parseStart(
            String date){


        return LocalDate
                .parse(date)
                .atStartOfDay();

    }






    private LocalDateTime parseEnd(
            String date){


        return LocalDate
                .parse(date)
                .atTime(23,59,59);

    }


}