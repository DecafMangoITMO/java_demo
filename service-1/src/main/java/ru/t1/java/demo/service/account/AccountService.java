package ru.t1.java.demo.service.account;

import ru.t1.java.demo.dto.AccountDto;

import java.util.List;

public interface AccountService {

    AccountDto getById(long id);

    List<AccountDto> getAll();

    AccountDto create(AccountDto accountDto);

    AccountDto updateBalance(long id, double balance);

    void delete(long id);

}
