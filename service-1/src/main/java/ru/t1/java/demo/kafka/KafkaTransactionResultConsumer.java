package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionResultDto;
import ru.t1.java.demo.service.transaction.TransactionService;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaTransactionResultConsumer {

    private final TransactionService transactionService;

    @KafkaListener(
            topics = {"${t1.kafka.topic.transaction_result}"},
            containerFactory = "kafkaTransactionResultListenerContainerFactory"
    )
    public void listener(@Payload TransactionResultDto transactionResultdto) {
        log.info("TransactionResult consumer: Обработка новых сообщений");
        transactionService.accept(transactionResultdto);
        log.info("TransactionResult consumer: записи обработаны");
    }

}