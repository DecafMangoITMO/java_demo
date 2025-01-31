package ru.t1.java.demo.util;

import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;

public class TransactionMapper {

    public static Transaction toEntity(TransactionDto transactionDto) {
        if (transactionDto.getAccountId() == null)
            throw new NullPointerException("Account id is null");
        if (transactionDto.getAmount() == null)
            throw new NullPointerException("Amount is null");

        return Transaction.builder()
                .account(null)
                .amount(transactionDto.getAmount())
                .time(transactionDto.getTime())
                .build();
    }

    public static TransactionDto toDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .accountId(transaction.getAccount().getId())
                .amount(transaction.getAmount())
                .time(transaction.getTime())
                .build();
    }

}
