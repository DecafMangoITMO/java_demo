package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.t1.java.demo.model.TransactionStatus;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class TransactionResultDto {
    @JsonProperty("transaction_id")
    private String transactionId;
    private TransactionStatus status;
    // Just used when transactions are blocked
    private List<String> otherBlockedTransactionIds;
}
