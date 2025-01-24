package ru.t1.java.demo.service.account.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.aop.Metric;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.account.AccountService;
import ru.t1.java.demo.util.AccountMapper;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final ClientRepository clientRepository;

    @Override
    @LogDataSourceError
    public AccountDto getById(long id) {
        log.info("Call method getById with id: {}", id);

        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isEmpty()) {
            log.error("Account with id {} not found", id);
            throw new AccountException(String.format("Account with id %s not found", id));
        }
        Account account = accountOptional.get();

        log.info("Got account: {}", account);
        return AccountMapper.toDto(account);
    }

    @Override
    @LogDataSourceError
    @Metric(intervalInMillis = 50)
    public List<AccountDto> getAll() {
        log.info("Call method getAll");

        List<Account> accounts = accountRepository.findAll();

        log.info("Got accounts: {}", accounts.size());
        return accounts.stream()
                .map(AccountMapper::toDto)
                .toList();
    }

    @Override
    @LogDataSourceError
    public AccountDto create(AccountDto accountDto) {
        log.info("Call method create with accountDto: {}", accountDto);

        Optional<Client> clientOptional = clientRepository.findById(accountDto.getClientId());
        if (clientOptional.isEmpty()) {
            log.error("Client with id {} not found", accountDto.getClientId());
            throw new ClientException(String.format("Client with id %s not found", accountDto.getClientId()));
        }
        Client client = clientOptional.get();

        Account account;
        try {
            account = AccountMapper.toEntity(accountDto);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            throw new AccountException(e.getMessage(), e);
        }
        account.setClient(client);

        try {
            account = accountRepository.save(account);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            throw new AccountException(e.getMessage(), e);
        }

        log.info("Account created: {}", account);
        return AccountMapper.toDto(account);
    }

    @Override
    @LogDataSourceError
    public AccountDto updateBalance(long id, double balance) {
        log.info("Call method updateBalance with id and balance: {} {}", id, balance);

        if (balance < 0) {
            log.error("Account balance is negative");
            throw new AccountException("Account balance is negative");
        }

        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isEmpty()) {
            log.error("Account with id {} not found", id);
            throw new AccountException(String.format("Account with id %s not found", id));
        }
        Account account = accountOptional.get();

        account.setBalance(balance);

        try {
            account = accountRepository.save(account);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            throw new AccountException(e.getMessage(), e);
        }

        log.info("Account balance updated: {}", account);
        return AccountMapper.toDto(account);
    }

    @Override
    @LogDataSourceError
    public void delete(long id) {
        log.info("Call method delete with id {}", id);

        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isEmpty()) {
            log.error("Account with id {} not found", id);
            throw new AccountException(String.format("Account with id %s not found", id));
        }

        try {
            accountRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            throw new AccountException(e.getMessage(), e);
        }
    }
}
