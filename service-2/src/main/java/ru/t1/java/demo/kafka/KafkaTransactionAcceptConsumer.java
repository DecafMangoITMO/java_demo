package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionAcceptDto;
import ru.t1.java.demo.service.transaction.impl.TransactionAcceptService;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaTransactionAcceptConsumer {

    private final TransactionAcceptService transactionAcceptService;

    @KafkaListener(
            topics = {"${t1.kafka.topic.transaction_accept}"},
            containerFactory = "kafkaTransactionAcceptListenerContainerFactory"
    )
    public void listener(@Payload TransactionAcceptDto transactionAcceptDto) {
        log.info("TransactionAccept consumer: Обработка новых сообщений");
        transactionAcceptService.accept(transactionAcceptDto);
        log.info("TransactionAccept consumer: записи обработаны");
    }

}
