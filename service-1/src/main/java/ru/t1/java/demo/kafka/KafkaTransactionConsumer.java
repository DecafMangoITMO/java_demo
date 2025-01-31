package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.service.transaction.TransactionService;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaTransactionConsumer {

    private final TransactionService transactionService;

    @KafkaListener(
            topics = {"${t1.kafka.topic.transaction}"},
            containerFactory = "kafkaTransactionListenerContainerFactory"
    )
    public void listener(@Payload TransactionDto transactionDto) {
        log.debug("Transaction consumer: Обработка новых сообщений");
        transactionService.request(transactionDto);
        log.debug("Transaction consumer: записи обработаны");
    }

}
