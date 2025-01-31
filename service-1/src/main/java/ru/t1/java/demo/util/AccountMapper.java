package ru.t1.java.demo.util;

import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;

public class AccountMapper {

    public static Account toEntity(AccountDto accountDto) {
        if (accountDto.getClientId() == null)
            throw new NullPointerException("Client id is null");
        if (accountDto.getBalanceType() == null)
            throw new NullPointerException("Balance type is null");
        if (accountDto.getBalance() == null)
            throw new NullPointerException("Balance is null");

        return Account.builder()
                .client(null)
                .balanceType(accountDto.getBalanceType())
                .balance(accountDto.getBalance())
                .build();
    }


    public static AccountDto toDto(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .clientId(account.getClient().getId())
                .balanceType(account.getBalanceType())
                .balance(account.getBalance())
                .build();
    }

}
