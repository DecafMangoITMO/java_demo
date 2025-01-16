package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.service.account.AccountService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService service;

    @GetMapping("/account/{id}")
    public AccountDto getById(@PathVariable(name = "id") long id) {
        log.info("Call GET endpoint /account/{id} with id: {}", id);
        return service.getById(id);
    }

    @GetMapping("/account")
    public List<AccountDto> getAll() {
        log.info("Call GET endpoint /account");
        return service.getAll();
    }

    @PostMapping("/account")
    public AccountDto create(@RequestBody AccountDto accountDto) {
        log.info("Call POST endpoint /account with body: {}", accountDto);
        return service.create(accountDto);
    }

    @PatchMapping("/account/{id}")
    public AccountDto updateBalance(@PathVariable(name = "id") long id, @RequestParam(name = "balance") double balance) {
        log.info("Call PATCH endpoint /account/{id} with id and balance: {} {}", id, balance);
        return service.updateBalance(id, balance);
    }

    @DeleteMapping("/account/{id}")
    public void delete(@PathVariable(name = "id") long id) {
        log.info("Call DELETE endpoint /account/{id} with id: {}", id);
        service.delete(id);
    }
}
