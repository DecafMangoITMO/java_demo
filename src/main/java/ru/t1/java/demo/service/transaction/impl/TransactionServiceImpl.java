package ru.t1.java.demo.service.transaction.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.exception.TransactionException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.transaction.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountRepository accountRepository;

    @Override
    @LogDataSourceError
    public TransactionDto getById(long id) {
        log.info("Call method getById with id: {}", id);

        Optional<Transaction> transactionOptional = transactionRepository.findById(id);
        if (transactionOptional.isEmpty()) {
            log.error("Transaction with id {} not found", id);
            throw new TransactionException(String.format("Transaction with id %s not found", id));
        }
        Transaction transaction = transactionOptional.get();

        log.info("Got transaction: {}", transaction);
        return TransactionMapper.toDto(transaction);
    }

    @Override
    public List<TransactionDto> getAll() {
        log.info("Call method getAll");

        List<Transaction> transactions = transactionRepository.findAll();

        log.info("Got {} transactions", transactions.size());
        return transactions.stream()
                .map(TransactionMapper::toDto)
                .toList();
    }

    @Override
    @LogDataSourceError
    public TransactionDto create(TransactionDto transactionDto) {
        log.info("Call method create with transactionDto: {}", transactionDto);

        Optional<Account> accountOptional = accountRepository.findById(transactionDto.getAccountId());
        if (accountOptional.isEmpty()) {
            log.error("Account with id {} not found", transactionDto.getAccountId());
            throw new AccountException(String.format("Account with id %s not found", transactionDto.getAccountId()));
        }
        Account account = accountOptional.get();

        Transaction transaction;
        try {
            transaction = TransactionMapper.toEntity(transactionDto);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            throw new TransactionException(e.getMessage(), e);
        }
        transaction.setAccount(account);

        try {
            transaction = transactionRepository.save(transaction);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            throw new TransactionException(e.getMessage(), e);
        }

        log.info("Created transaction: {}", transaction);
        return TransactionMapper.toDto(transaction);
    }
}
