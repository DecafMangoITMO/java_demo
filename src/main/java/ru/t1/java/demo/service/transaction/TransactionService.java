package ru.t1.java.demo.service.transaction;

import ru.t1.java.demo.dto.TransactionDto;

import java.util.List;

public interface TransactionService {

    TransactionDto getById(long id);

    List<TransactionDto> getAll();

    TransactionDto create(TransactionDto transactionDto);

}
