package ru.t1.java.demo.service.transaction.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.TransactionAcceptDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.dto.TransactionResultDto;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.exception.TransactionException;
import ru.t1.java.demo.kafka.KafkaTransactionAcceptProducer;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.AccountStatus;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.TransactionStatus;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.transaction.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    private final KafkaTransactionAcceptProducer kafkaTransactionAcceptProducer;

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

    @Override
    public void request(TransactionDto transactionDto) {
        log.info("Call method request with transactionDto: {}", transactionDto);

        Account account = accountRepository.findById(transactionDto.getAccountId()).get();
        if (account.getStatus() != AccountStatus.OPEN)
            return;

        Transaction transaction = TransactionMapper.toEntity(transactionDto);
        transaction.setAccount(account);
        transaction.setTime(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.REQUESTED);
        transaction.setTransactionId(UUID.randomUUID().toString());

        account.setBalance(account.getBalance() + transaction.getAmount());

        transaction = transactionRepository.save(transaction);
        accountRepository.save(account);
        log.info("Requested transaction: {}", transaction);

        double newBalance = account.getBalance() + transaction.getAmount();
        TransactionAcceptDto transactionAcceptDto = TransactionAcceptDto.builder()
                .clientId(account.getClient().getClientId())
                .accountId(account.getAccountId())
                .transactionId(transaction.getTransactionId())
                .time(transaction.getTime())
                .transactionAmount(transaction.getAmount())
                .accountBalance(newBalance)
                .build();
        kafkaTransactionAcceptProducer.send(transactionAcceptDto);
    }

    @Override
    public void accept(TransactionResultDto transactionResultDto) {
        log.info("Call method accept with transactionResultDto: {}", transactionResultDto);

        Optional<Transaction> transactionOptional = transactionRepository.findByTransactionId(transactionResultDto.getTransactionId());
        if (transactionOptional.isEmpty()) {
            log.warn("Transaction with transactionId {} not found", transactionResultDto.getTransactionId());
            return;
        }
        Transaction transaction = transactionOptional.get();

        Optional<Account> accountOptional = accountRepository.findByAccountId(transaction.getAccount().getAccountId());
        if (accountOptional.isEmpty()) {
            log.warn("Account with id {} not found", transaction.getAccount().getAccountId());
            return;
        }
        Account account = accountOptional.get();

        if (transactionResultDto.getStatus() == TransactionStatus.ACCEPTED) {
            transaction.setStatus(TransactionStatus.ACCEPTED);
            transactionRepository.save(transaction);
            return;
        }

        if (transactionResultDto.getStatus() == TransactionStatus.BLOCKED) {
            List<Transaction> otherTransactions = transactionRepository.findAllByTransactionIdIn(transactionResultDto.getOtherBlockedTransactionIds());
            double frozenAmount = 0d;

            transaction.setStatus(TransactionStatus.BLOCKED);
            for (Transaction otherTransaction : otherTransactions) {
                otherTransaction.setStatus(TransactionStatus.BLOCKED);
                frozenAmount += otherTransaction.getAmount();
            }
            frozenAmount += transaction.getAmount();

            System.out.println(frozenAmount);

            account.setBalance(account.getBalance() - frozenAmount);
            account.setFrozenAmount(account.getFrozenAmount() + frozenAmount);

            otherTransactions.add(transaction);
            transactionRepository.saveAll(otherTransactions);
            accountRepository.save(account);

            return;
        }

        if (transactionResultDto.getStatus() == TransactionStatus.REJECTED) {
            transaction.setStatus(TransactionStatus.REJECTED);
            account.setBalance(account.getBalance() - transaction.getAmount());

            transactionRepository.save(transaction);
            accountRepository.save(account);
        }

    }
}
