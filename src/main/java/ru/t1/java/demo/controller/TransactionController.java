package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.service.transaction.TransactionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("transaction/{id}")
    public TransactionDto getById(@PathVariable long id) {
        log.info("Call GET endpoint /transaction/{id} with id: {}", id);
        return transactionService.getById(id);
    }

    @GetMapping("/transaction")
    public List<TransactionDto> getAll() {
        log.info("Call GET endpoint /transaction");
        return transactionService.getAll();
    }

    @PostMapping("/transaction")
    public TransactionDto create(@RequestBody TransactionDto transactionDto) {
        log.info("Call POST endpoint /transaction with body: {}", transactionDto);
        return transactionService.create(transactionDto);
    }

}
