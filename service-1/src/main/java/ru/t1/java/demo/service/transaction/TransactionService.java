package ru.t1.java.demo.service.transaction;

import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.dto.TransactionResultDto;

import java.util.List;

public interface TransactionService {

    TransactionDto getById(long id);

    List<TransactionDto> getAll();

    TransactionDto create(TransactionDto transactionDto);

    void request(TransactionDto transactionDto);

    void accept(TransactionResultDto transactionResultDto);
}
