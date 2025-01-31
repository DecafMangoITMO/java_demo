package ru.t1.java.demo.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionId(String transactionId);

    List<Transaction> findAllByTransactionIdIn(List<String> transactionIds);

    @Query("select t from Transaction as t " +
            "where t.account.accountId = :accountId and t.time between :startTime and :endTime")
    List<Transaction> findTransactionsWithinInterval(String accountId, LocalDateTime startTime, LocalDateTime endTime);

}
