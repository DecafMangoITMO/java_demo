package ru.t1.java.demo.service.transaction.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.TransactionAcceptDto;
import ru.t1.java.demo.dto.TransactionResultDto;
import ru.t1.java.demo.kafka.KafkaTransactionResultProducer;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.TransactionStatus;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionAcceptService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Value("${t1.transactions_period_sec}")
    private long TRANSACTIONS_PERIOD_SEC;
    @Value("${t1.allowed_transactions_per_period}")
    private long ALLOWED_TRANSACTIONS_PER_PERIOD;

    private final KafkaTransactionResultProducer kafkaTransactionResultProducer;

    public void accept(TransactionAcceptDto transactionAcceptDto) {
        log.info("Call method accept with transactionAcceptDto: {}", transactionAcceptDto);

        Optional<Account> accountOptional = accountRepository.findByAccountId(transactionAcceptDto.getAccountId());
        if (accountOptional.isEmpty()) {
            log.warn("Account with accountId {} not found", transactionAcceptDto.getAccountId());
            TransactionResultDto transactionResultDto = TransactionResultDto.builder()
                    .transactionId(transactionAcceptDto.getTransactionId())
                    .status(TransactionStatus.CANCELLED)
                    .build();
            kafkaTransactionResultProducer.send(transactionResultDto);
            return;
        }
        Account account = accountOptional.get();

        if (transactionAcceptDto.getTransactionAmount() < 0 && account.getBalance() < Math.abs(transactionAcceptDto.getTransactionAmount())) {
            log.warn("Account with accountId {} not enough", transactionAcceptDto.getAccountId());
            TransactionResultDto transactionResultDto = TransactionResultDto.builder()
                    .transactionId(transactionAcceptDto.getTransactionId())
                    .status(TransactionStatus.REJECTED)
                    .build();
            kafkaTransactionResultProducer.send(transactionResultDto);
            return;
        }

        List<Transaction> transactionsBefore = transactionRepository.findTransactionsWithinInterval(
                account.getAccountId(),
                transactionAcceptDto.getTime().minusSeconds(TRANSACTIONS_PERIOD_SEC),
                transactionAcceptDto.getTime()
        );
        if (transactionsBefore.size() >= ALLOWED_TRANSACTIONS_PER_PERIOD) {
            log.warn("Transactions per period exceeded");
            TransactionResultDto transactionResultDto = TransactionResultDto.builder()
                    .transactionId(transactionAcceptDto.getTransactionId())
                    .status(TransactionStatus.BLOCKED)
                    .otherBlockedTransactionIds(
                            transactionsBefore.stream()
                                    .map(Transaction::getTransactionId)
                                    .filter(transactionId -> !transactionId.equals(transactionAcceptDto.getTransactionId()))
                                    .toList()
                    )
                    .build();
            kafkaTransactionResultProducer.send(transactionResultDto);
            return;
        }

        TransactionResultDto transactionResultDto = TransactionResultDto.builder()
                .transactionId(transactionAcceptDto.getTransactionId())
                .status(TransactionStatus.ACCEPTED)
                .build();
        kafkaTransactionResultProducer.send(transactionResultDto);
        log.info("Transaction accepted: {}", transactionAcceptDto);
    }

}
