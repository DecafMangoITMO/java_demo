package ru.t1.java.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account")
@ToString
public class Account extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "balance_type")
    @Enumerated(EnumType.STRING)
    private BalanceType balanceType;

    @Column(name = "balance")
    private double balance;

}
